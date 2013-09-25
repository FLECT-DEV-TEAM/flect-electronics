package helper;

import model.User;
import play.cache.Cache;
import play.mvc.Scope.Session;

public class SessionHelper {
    
    public static String getAdminSessionId() {
        Session session = Session.current();
        if (session == null) {
            return (String)Cache.get(Conf.Salesforce.ADMIN_SESSIONID_CACHEKEY);
        } else {
            return session.get("adminSessionId");
        }
    }

    public static String getSessionId() {
        Session session = Session.current();
        if (session == null) {
            return (String)Cache.get(Conf.Salesforce.PORTAL_SESSIONID_CACHEKEY);
        } else {
            return session.get("sessionId");
        }
    }
    
    public static void setAdminSessionId(String sessionId) {
        Session session = Session.current();
        if (session == null) {
            Cache.set(Conf.Salesforce.ADMIN_SESSIONID_CACHEKEY, sessionId);
        } else {
            session.put("adminSessionId", sessionId);
        }
    }
    
    public static void setSessionId(String sessionId) {
        Session session = Session.current();
        if (session == null) {
            Cache.set(Conf.Salesforce.PORTAL_SESSIONID_CACHEKEY
                    , sessionId);
        } else {
            session.put("sessionId", sessionId);
        }
    }
    
    public static void invalidate() {
        Session session = Session.current();
        if (session != null) {
            session.clear();
        }
        
    }
    
    public static void setUsername(User user) {
        Session.current().put("username", user.username);
    }

    public static String getUsername() {
        return Session.current().get("username");
    }    

    public static void setNickname(User user) {
        Session.current().put("nickname", user.lastName);
    }

    public static String getNickname() {
        return Session.current().get("nickname");
    }
    
    public static void setSessionTimeout() {
        Session.current().put("sessionTimeout", "true");
    }
    
    public static String getSessionTImeout() {
        return Session.current().get("sessionTimeout");
    }
    
    public static void clearSessionTImeout() {
        Session.current().put("sessionTimeout", null);
    }
    
}
