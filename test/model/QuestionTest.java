package model;

import helper.Conf;
import helper.Salesforce;
import helper.SessionHelper;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.cache.Cache;
import play.test.UnitTest;

public class QuestionTest extends UnitTest {
    
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
    public void findForTopPage() {
        List<Question> questions = Question.findRecent(2);
        assertTrue(questions.size() == 2);
    }
    
    @Test
    public void findById() {
        Question question = Question.findById("906U00000008US2IAM");
        assertNotNull(question);
        assertNotNull(question.createdBy);
        assertTrue(!question.createdBy.id.isEmpty());
    }
    
    @Test
    public void findByUserId() {
        List<Question> questions = Question.findByUserId("005U0000000ErcTIAS");
        assertTrue(questions.size() > 0);
        assertTrue(!questions.get(0).createdBy.id.isEmpty());
    }
    
    @Test
    public void findLikeQuestionTitle() {
        List<String> words = new ArrayList<String>();
        words.add("テスト02");
        List<Question> questions = Question.findLikeQuestionTitle(words);
        assertTrue(questions.size() > 0);
        words.clear();
        words.add("テスト");
        words.add("質問");
        List<Question> questions2 = Question.findLikeQuestionTitle(words);
        assertTrue(questions2.size() > 0);
    }
    
    public void post() {
        Question question = new Question();
        question.title = "カスタマーポータルユーザからの質問になります";
        question.body = "質問できるとうれしいなー";
        question.communityId = "09aU00000005ve4IAA";
        Question.post(question);
        assertNotNull(question.id);
        assertTrue(question.id.length() > 0);
        try {
            Question.delete(question);
        } catch(Exception e) {
            fail();
        }
    }
    @Test
    public void allDelete() {
        List<Question> questions = Question.findAll(Question.class);
        for(Question q : questions) {
            Salesforce.delete(q);
        }
    }
}
