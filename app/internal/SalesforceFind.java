package internal;

import helper.Salesforce;
import helper.SessionHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import model.SalesforceEntity;

import org.apache.commons.lang.StringUtils;

import util.ReflectionUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import exception.PlayRuntimeException;

public class SalesforceFind <T> {
    
    private From from;
    private Where where;
    private Limit limit;
    private OrderBy orderBy;
    private boolean usePortalUser = false;

    public enum Order {
        ASC,DESC
    }
    
    private static class OrderBy {
        private Map<String, Order> orderBy;
        public void add(String objectName, Order order) {
            if(orderBy == null) {
                orderBy = new LinkedHashMap();
            }
            orderBy.put(objectName, order);
        }
        
        public String buildClause() {
            StringBuilder sb = new StringBuilder();
            if(orderBy != null) {
                sb.append(" ORDER BY");
                for(Map.Entry<String, Order> e : orderBy.entrySet()) {
                    String objectName = e.getKey();
                    Order order = e.getValue();
                    sb.append(' ' + objectName + ' ' + order.toString() + ' ');
                    sb.append(',');
                }
                removeComma(sb);
            }
            return sb.toString();
        }
        
    }
    
    private static class From {
        private Class<?> clazz;
        private Map<Class<?>, String> joins;
        public From(Class<?> clazz) {
            this.clazz = clazz;
            this.joins = new LinkedHashMap<Class<?>, String>();                
        }
        
        public Class<?> getClazz() {
            return this.clazz;
        }
        
        public String getName() {
            return this.clazz.getSimpleName();
        }
        
        public void join(Class<?> clazz, String objectName) {
            joins.put(clazz, objectName);
        } 
        
        public Map<Class<?>, String> getJoins() {
            return joins;
        }
        
        public String buildClause() {
            StringBuilder sb = new StringBuilder();
            sb.append(" from ").append(getObjectName(clazz) + ' ' + clazz.getSimpleName());
            if(joins != null) {
                for(Map.Entry<Class<?>, String> e : joins.entrySet()) {
                    Class<?> joinClass = e.getKey();
                    String objectName = e.getValue();
                    sb.append(',' + getObjectName(clazz) + '.');
                    sb.append(objectName.equals("") ?  joinClass.getSimpleName() : objectName);
                    sb.append(' ' + joinClass.getSimpleName());
                }
            }
            return sb.toString();
        }
    }
    
    private static class Where {
        private Map<String, List<String>> wheres;
        private static final Pattern inPattern;
        private static final Pattern booleanPattern;
        static {
            inPattern = Pattern.compile(".+IN\\s*\\(\\s*\\?\\s*\\).*");
            booleanPattern = Pattern.compile("true|false");
        }
        public void add(String criteria, List<String> params) {
            if(wheres == null) {
                wheres = new LinkedHashMap<String, List<String>>();
            }
            wheres.put(criteria, params);
        }
        
        public String buildClause() {
            StringBuilder sb = new StringBuilder();
            if(wheres != null) {
                sb.append(" WHERE ");
                for(Map.Entry<String, List<String>> e : wheres.entrySet()) {
                    String criteria = e.getKey();
                    List<String> params = e.getValue();
                    if(inPattern.matcher(criteria).find()) {
                        StringBuilder paramsb = new StringBuilder();
                        for(String param : params) {
                            paramsb.append("'" + param + "'");
                            paramsb.append(',');
                        }
                        removeComma(paramsb    );
                        sb.append(criteria.replaceFirst("\\?", paramsb.toString()));
                    } else {
                        for(String param : params) {
                            // TODO ひどい回避策なのでサービスパック開発で根本的に見直す
                            if(booleanPattern.matcher(param).find()) {
                                criteria = criteria.replaceFirst("\\?", param);
                            } else {
                                criteria = criteria.replaceFirst("\\?", "'" + param + "'");
                            }
                        }
                        sb.append(criteria);
                    }
                }    
            }
            return sb.toString();
        }
    }
    
    private static class Limit {
        private int limit = 0;
        public Limit(int limit) {
            this.limit = limit;
        }
        public String buildClause() {
            return " limit  " + limit;
        }
    }

    public SalesforceFind(Class<T> clazz) {
        this.from = new From(clazz);
    }
    
    public SalesforceFind<T> join(Class<?> clazz) {
        from.join(clazz, "");
        return this;
    }
    
    public SalesforceFind<T> join(Class<?> clazz, String objectName) {
        from.join(clazz, objectName);
        return this;
    }
    
    public SalesforceFind<T> where(String criteria, String... value) {
        if(this.where == null) {
            this.where = new Where();
        }
        this.where.add(criteria, Arrays.asList(value));
        return this;
    }
    
    public SalesforceFind<T> portalUser() {
        this.usePortalUser = true;
        return this;
    }
    
    public SalesforceFind<T> orderBy(String objectName, Order order) {
        if(orderBy == null) {
            orderBy = new OrderBy();
        }
        orderBy.add(objectName, order);
        return this;
    }
    
    public SalesforceFind<T> limit(int limit) {
        this.limit = new Limit(limit);
        return this;
    }
        
    public T getSingle() {
        JsonObject jsonObj = request();
        JsonArray jsonArray = jsonObj.getAsJsonArray("records");
        int size = jsonArray.size();
        if(size > 1) {
            throw new PlayRuntimeException("2件以上のデータが返却されています");
        }
        if(size == 0) {
            return null;
        }
        return ummarshall(jsonArray.get(0).getAsJsonObject());
    }
    
    public List<T> getList() {
        JsonObject jsonObj = request();
        JsonArray jsonArray = jsonObj.getAsJsonArray("records");
        int size = jsonArray.size();
        List<T> models = new ArrayList<T>(size);
        for(int i = 0; i < size; i++) {
            T model = ummarshall(jsonArray.get(i).getAsJsonObject());
            models.add(model);
        }
        return (List<T>)models;
    }

    private JsonObject request() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT ");
        for(Field f : from.getClazz().getDeclaredFields()) {
            Column column = f.getAnnotation(Column.class);
            if(column != null) {
                sb.append(from.getName() + '.' + camelize(f.getName()))
                .append(',');
            }
        }        
        for(Map.Entry<Class<?>, String> e : from.getJoins().entrySet()) {
            Class<?> clazz = e.getKey();
            String objectName = e.getValue();
            for(Field f : clazz.getDeclaredFields()) {
                Column column = f.getAnnotation(Column.class);
                if(column != null) {
                    sb.append(objectName.equals("") ? clazz.getSimpleName() : objectName )
                    .append('.' + camelize(f.getName()))
                    .append(',');
                }
            }
        }
        
        removeComma(sb);
        
        sb.append(from.buildClause());        
        if(where != null) {
            sb.append(where.buildClause());
        }
        
        if(orderBy != null) {
            sb.append(orderBy.buildClause());
        }
        
        if(limit != null) {
            sb.append(limit.buildClause());
        }
        
        String sessionId =
            usePortalUser ? SessionHelper.getSessionId() : SessionHelper.getAdminSessionId();
        return Salesforce.findByQuery(sb.toString(), sessionId);
    }

    private T ummarshall(JsonObject jsonModel) {
        T model = (T)ReflectionUtil.newInstance(from.getClazz());
        Field[] fields = model.getClass().getFields();
        for(Field f : fields) {
            if(f.getAnnotation(Column.class) != null) {
                JsonElement je = jsonModel.get(f.getName());
                if (je == null) {
                    je = jsonModel.get(camelize(f.getName()));
                }
                String value = je.isJsonNull() 
                    ? "" : je.getAsString();
                ReflectionUtil.setField(f, model, value);
            }
            if(hasRelationalAnnotation(f) && from.getJoins().containsKey(f.getType())) {
                Object relModel = ReflectionUtil.newInstance(f.getType());
                JsonObject relationJsonModel = jsonModel.get(camelize(f.getName())).getAsJsonObject();
                for(Field relModelField : relModel.getClass().getDeclaredFields()) {
                    String columnName = camelize(relModelField.getName());
                    if(relationJsonModel.get(columnName) != null) {
                        String value = relationJsonModel.get(columnName).isJsonNull()
                                ? "" : relationJsonModel.get(columnName).getAsString();
                        ReflectionUtil.setField(relModelField, relModel, value);
                    }
                }
                ReflectionUtil.setField(f, model, relModel);
            }
        }
        return (T)model;
    }

    private static boolean hasRelationalAnnotation(Field f) {
        return f.getAnnotation(ManyToOne.class) != null || f.getAnnotation(OneToOne.class) != null;
    }
    
    private static String getObjectName(Class<?> clazz) {
        SalesforceEntity entity = clazz.getAnnotation(SalesforceEntity.class);
        if(entity != null && StringUtils.isNotEmpty(entity.name())) {
            return entity.name();
        } else {
            return clazz.getSimpleName();
        }
    }
    
    private static String camelize(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
    
    private static void removeComma(StringBuilder sb) {
        if(sb.lastIndexOf(",") != -1) {
            sb.deleteCharAt(sb.length() - 1);
        }
    }    
}
