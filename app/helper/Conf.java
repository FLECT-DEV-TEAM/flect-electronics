package helper;

import play.Play;

/**
 * application.confの内容を簡易に取得するためのクラスです。
 * 
 * @author n-shinya
 *
 */
public class Conf {
    
    private Conf(){}

    public static class Salesforce {
    
        public final static String ADMIN_SESSIONID_CACHEKEY;
        public final static String PORTAL_SESSIONID_CACHEKEY;
        public final static String ADMIN_USERNAME;
        public final static String ADMIN_PASSWORD;
        public final static String TEST_PORTAL_USERNAME;
        public final static String TEST_PORTAL_PASSWORD;
        public final static String API_BASE_URL;
        
        static {
            ADMIN_SESSIONID_CACHEKEY = Play.configuration.getProperty("salesforce.admin.sessionid.cachekey");
            PORTAL_SESSIONID_CACHEKEY = Play.configuration.getProperty("salesforce.portal.sessionid.cachekey");
            ADMIN_USERNAME = Play.configuration.getProperty("salesforce.username");
            ADMIN_PASSWORD = Play.configuration.getProperty("salesforce.password");
            API_BASE_URL = Play.configuration.getProperty("salesforce.apibase.url");
            TEST_PORTAL_USERNAME = Play.configuration.getProperty("salesforce.portal.username");
            TEST_PORTAL_PASSWORD = Play.configuration.getProperty("salesforce.portal.password");
        }
        
        public static String getQueryUrl() {
            return API_BASE_URL + "query/";
        }
        
        public static String getModifyUrl() {
            return API_BASE_URL + "sobjects/";
        }
    }
}
