package helper;

import internal.SalesforceFind;
import internal.SalesforceSave;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Id;

import model.SalesforceEntity;
import model.User;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.cache.Cache;
import play.libs.WS;
import play.mvc.Scope.Session;
import util.ReflectionUtil;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sforce.soap.enterprise.Email;
import com.sforce.soap.enterprise.LoginResult;
import com.sforce.soap.enterprise.LoginScopeHeader;
import com.sforce.soap.enterprise.ResetPasswordResult;
import com.sforce.soap.enterprise.SaveResult;
import com.sforce.soap.enterprise.SessionHeader;
import com.sforce.soap.enterprise.SforceServiceLocator;
import com.sforce.soap.enterprise.SingleEmailMessage;
import com.sforce.soap.enterprise.SoapBindingStub;
import com.sforce.soap.enterprise.sobject.QuestionDataCategorySelection;
import com.sforce.soap.enterprise.sobject.SObject;

import exception.PlayRuntimeException;

/**
 * Salesforceにアクセスするためのファサードです。
 * 
 * @author n-shinya
 */
public class Salesforce {
    
    // TODO ユーティリティ的に使いたいのでstaticメソッドにしている。状態がもてないため毎度ログインしたりする。
    // TODO PlayのSessionに依存しているためポータビリティに問題あるので直す。
    
    public static String adminLogin() {
        Logger.info("Admin login...");
        LoginResult result;
        try {
            SoapBindingStub binding = (SoapBindingStub)new SforceServiceLocator().getSoap();
            result = binding.login(Conf.Salesforce.ADMIN_USERNAME,
                    Conf.Salesforce.ADMIN_PASSWORD);
            Cache.set(result.getSessionId(), result.getServerUrl());
        } catch(Exception e) {
            throw new PlayRuntimeException(e);
        }        
        return result.getSessionId();
    }
    
    public static String loginIfNeed(String id, String password) {
        String sessionId = SessionHelper.getSessionId();
        if(sessionId == null) {
            return login(id, password);
        }
        return sessionId;
    }
    
    public static String adminLoginIfNeed() {
        String sessionId = SessionHelper.getAdminSessionId();
        if(sessionId == null) {
            return adminLogin();
        }
        return sessionId;
    }
    
    public static String login(String id, String password) {
        Logger.info("Portal user login...");
        LoginResult result;
        try {
            SoapBindingStub  binding = (SoapBindingStub)new SforceServiceLocator().getSoap();
            LoginScopeHeader lsh = new LoginScopeHeader();
            lsh.setPortalId("060U0000000D7WV");
            lsh.setOrganizationId("00DU0000000K89Z");
            binding.setHeader( new SforceServiceLocator().getServiceName().getNamespaceURI(), "LoginScopeHeader", lsh);
            binding.setPassword(password);
            result = binding.login(id ,password);
            Cache.set(result.getSessionId(), result.getServerUrl());
            return result.getSessionId();
        } catch(Exception e) {
            Logger.error("Login Fault.", e);
            return null;
        }
    }
    
    @Deprecated
    public static void setQuestionCategory(String sessionId, String questionId, String categoryName, String categoryGroupName) {
        try {
            String url = (String)Cache.get(sessionId);
            SoapBindingStub binding = (SoapBindingStub)new SforceServiceLocator().getSoap(new URL(url));
            SessionHeader sh = new SessionHeader();
            sh.setSessionId(sessionId);
            binding.setHeader( new SforceServiceLocator().getServiceName().getNamespaceURI(), "SessionHeader", sh);
            QuestionDataCategorySelection categorySelection = new QuestionDataCategorySelection();
            categorySelection.setParentId(questionId);
            categorySelection.setDataCategoryGroupName(categoryGroupName);
            categorySelection.setDataCategoryName(categoryName);
            SaveResult[] result = binding.create(new SObject[]{categorySelection});
        } catch (Exception e) {
            throw new PlayRuntimeException(e);
        }
    }
    
    public static void logout(String sessionId) {
        try {
            String url = (String)Cache.get(sessionId);
            SoapBindingStub binding = (SoapBindingStub)new SforceServiceLocator().getSoap(new URL(url));
            SessionHeader sh = new SessionHeader();
            sh.setSessionId(sessionId);
            binding.setHeader( new SforceServiceLocator().getServiceName().getNamespaceURI(), "SessionHeader", sh);
            binding.logout();
        } catch (Exception e) {
            Logger.error(e, "ログアウトに失敗しました.");
            Session session = Session.current();
            if(session != null) {
                session.clear();
            }
        }
    }

    
    public static String setPassword(User user, String password) {
        LoginResult result;
        try {
            SoapBindingStub binding = (SoapBindingStub)new SforceServiceLocator().getSoap();
            result = binding.login(Conf.Salesforce.ADMIN_USERNAME,
                    Conf.Salesforce.ADMIN_PASSWORD);
            binding._setProperty(SoapBindingStub.ENDPOINT_ADDRESS_PROPERTY, result.getServerUrl());
            SessionHeader sessionHeader = new SessionHeader();
            sessionHeader.setSessionId(result.getSessionId());
            binding.setHeader( new SforceServiceLocator().getServiceName().getNamespaceURI(), "SessionHeader", sessionHeader);
            binding.setPassword(user.id, password);
            SingleEmailMessage message = new SingleEmailMessage();
            message.setSubject("[FLECT ELECTRONICS]パスワードが変更されました。");
            message.setPlainTextBody("ID: " + user.username + " / パスワード: " + password);
            message.setToAddresses(new String[] { user.email });
            binding.sendEmail(new Email[] {message});
            return password;
        } catch(Exception e) {
            throw new PlayRuntimeException(e);
        }        
    }
    
    public static String resetPassword(User user) {
        LoginResult result;
        try {
            SoapBindingStub binding = (SoapBindingStub)new SforceServiceLocator().getSoap();
            result = binding.login(Conf.Salesforce.ADMIN_USERNAME,
                    Conf.Salesforce.ADMIN_PASSWORD);
            binding._setProperty(SoapBindingStub.ENDPOINT_ADDRESS_PROPERTY, result.getServerUrl());
            SessionHeader sessionHeader = new SessionHeader();
            sessionHeader.setSessionId(result.getSessionId());
            binding.setHeader( new SforceServiceLocator().getServiceName().getNamespaceURI(), "SessionHeader", sessionHeader);
            ResetPasswordResult passwordResult = binding.resetPassword(user.id);
            String password = passwordResult.getPassword();
            SingleEmailMessage message = new SingleEmailMessage();
            message.setSubject("[FLECT ELECTRONICS]ようこそ！");
            message.setPlainTextBody("ID: " + user.username + " / パスワード: " + password);
            message.setToAddresses(new String[] { user.email });
            binding.sendEmail(new Email[] {message});
            return password;
        } catch(Exception e) {
            throw new PlayRuntimeException(e);
        }        
    }
    
    public static String metadata(Class<?> clazz) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "OAuth " + SessionHelper.getAdminSessionId());
        headers.put("X-PrettyPrint","1");
        return WS.url(Conf.Salesforce.getModifyUrl() + getObjectName(clazz) + "/describe")
            .headers(headers)
            .get()
            .getString();
    }
    
    public static JsonObject findByQuery(String query, String sessionId) {
        Logger.debug("[QUERY]" + query);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("q", query);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "OAuth " + sessionId);
        headers.put("X-PrettyPrint","1");
        
        JsonElement json = WS.url(Conf.Salesforce.getQueryUrl())
                .params(params)
                .headers(headers)
                .get()
                .getJson();
        if(json.toString().contains("Session expired or invalid")) {
            String newSessionId = adminLogin();
            SessionHelper.setAdminSessionId(newSessionId);
            return findByQuery(query, newSessionId);
        }
        if(!json.isJsonObject()) {
            throw new PlayRuntimeException("問い合わせに失敗しました。" + json.toString());
        }
        return json.getAsJsonObject();
    }
    
    public static <T> SalesforceFind<T> find(Class<T> clazz) {
        return new SalesforceFind(clazz);
    }
    
    public static SalesforceSave save(Object entity) {
        return new SalesforceSave(entity);
    }
    
    public static void delete(Object entity) {
        String id = "";
        for(Field f : entity.getClass().getDeclaredFields()) {
            if(f.getAnnotation(Id.class) != null) {
                Object value = ReflectionUtil.getFieldValue(entity, f);
                if(value == null) {
                    throw new PlayRuntimeException("idフィールドの値が空です。");
                }
                id = value.toString();
                break;
            }
        }
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "OAuth " + SessionHelper.getAdminSessionId());
        WS.url(Conf.Salesforce.getModifyUrl() + getObjectName(entity.getClass()) + "/" + id)
                .headers(headers)
                .delete();
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
