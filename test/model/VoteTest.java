package model;

import helper.Conf;
import helper.Salesforce;
import helper.SessionHelper;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class VoteTest extends UnitTest {

    @Before
    public void setup() {
        if(SessionHelper.getAdminSessionId() == null) {
            SessionHelper.setAdminSessionId(Salesforce.adminLoginIfNeed());
        }
        if(SessionHelper.getSessionId() == null) {
            SessionHelper.setSessionId(
                    Salesforce.loginIfNeed(Conf.Salesforce.TEST_PORTAL_USERNAME
                            ,Conf.Salesforce.TEST_PORTAL_PASSWORD));
        }
    }
    
    @Test
    public void findByReplyId() {
//        Vote vote = Vote.findByReplyId("907U00000008WbmIAE");
//        assertNotNull(vote);
    }
    
    //@Test
    public void addPoint() {
        Vote vote = Vote.up("907U00000008TznIAE");
        Vote.delete(vote);
    }
}
