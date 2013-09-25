package internal;

import helper.Conf;
import helper.Salesforce;
import helper.SessionHelper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Id;

import model.SalesforceEntity;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import util.ReflectionUtil;

import com.google.gson.JsonObject;

import exception.PlayRuntimeException;
import exception.SessionTimeoutException;

public class SalesforceSave {
    
    private Object entity;
    private boolean usePortalUser = false;

    public SalesforceSave(Object entity) {
        this.entity = entity;
    }
    
    public SalesforceSave portalUser() {
        this.usePortalUser = true;
        return this;
    }

    
    public void execute() {
        String id = "";
        JsonObject json = new JsonObject();
        for(Field f : entity.getClass().getDeclaredFields()) {
            Column column = f.getAnnotation(Column.class);
            if(column != null) {
                Object value = ReflectionUtil.getFieldValue(entity, f);
                if(value != null) {
                    if(f.getAnnotation(Id.class) == null) {
                        json.addProperty(f.getName(), value.toString());    
                    } else {
                        id = value.toString(); 
                    }
                }
            }
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        String sessionId = usePortalUser ? SessionHelper.getSessionId() : SessionHelper.getAdminSessionId();
        headers.put("Authorization", "OAuth " + sessionId);
    
        StringBuilder url = new StringBuilder();
        url.append(Conf.Salesforce.getModifyUrl())
            .append(getObjectName(entity.getClass()))
            .append('/')
            .append(id)
            .append(id.isEmpty() ? "" : "?_HttpMethod=PATCH");
                
        HttpResponse response = 
                WS.url(url.toString())
                .headers(headers)
                .body(json)
                .post();
        Logger.info(response.getString());

        if(response.getString().contains("Session expired or invalid")) {
            if(usePortalUser) {
                throw new SessionTimeoutException("セッションがタイムアウトしているか不正な状態です。");
            } else {
                SessionHelper.setAdminSessionId(Salesforce.adminLogin());
                execute();
            }
        }
        
        if(id.isEmpty() && !response.getJson().isJsonObject()) {
            Logger.debug(response.getString());
            throw new PlayRuntimeException("[ERROR]" + response.getString());
        }
        
        if(id.isEmpty()) {
            String newId = response.getJson().getAsJsonObject().get("id").getAsString();
            ReflectionUtil.setField(
                    ReflectionUtil.getField(entity.getClass(), "id")
                    ,entity
                    ,newId);
        }
    }
    
    private static String getObjectName(Class<?> clazz) {
        SalesforceEntity entity = clazz.getAnnotation(SalesforceEntity.class);
        if(entity != null && StringUtils.isNotEmpty(entity.name())) {
            return entity.name();
        } else {
            return clazz.getSimpleName();
        }
    }
}
