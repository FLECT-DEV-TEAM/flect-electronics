package helper;

import java.util.List;

import model.Contact;
import model.Product2;

import org.junit.Before;
import org.junit.Test;

import play.cache.Cache;
import play.test.UnitTest;


public class SalesforceTest extends UnitTest {

    @Before
    public void setup() {
        if(Cache.get("SFSessionId") == null) {
            Cache.set("SFSessionId", Salesforce.adminLogin());
        }
    }

    @Test
    public void login() {
        String sessionId = Salesforce.adminLogin();
        assertNotNull(sessionId);
    }
}
