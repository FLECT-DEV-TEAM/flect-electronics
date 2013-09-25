package model;

import helper.Conf;
import helper.Salesforce;
import helper.SessionHelper;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.cache.Cache;
import play.test.UnitTest;

public class ReplyTest extends UnitTest {

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
    public void findByQuestionId() {
        List<Reply> replies = Reply.findByQuestionId("906U00000008U1eIAE");
        assertTrue(replies.size() > 0);
        assertTrue(!replies.get(0).question.id.isEmpty());
        assertTrue(!replies.get(0).createdBy.id.isEmpty());
    }
    
    @Test
    public void findByUserId() {
        List<Reply> replies = Reply.findByUserId("005U0000000ErcTIAS");
        assertTrue(replies.size() > 0);
        assertTrue(!replies.get(0).createdBy.id.isEmpty());
    }

    
    @Test
    public void post() {
        Reply reply = new Reply();
        reply.body = "これは成功する！";
        reply.questionId = "906U00000008UnUIAU";
        Reply.post(reply);
        System.out.println(reply.id);
        assertNotNull(reply.id);
        assertTrue(reply.id.length() > 0);
        try {
            Salesforce.delete(reply);
        } catch(Exception e) {
            fail();
        }
    }
}
