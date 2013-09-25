package controllers;

import java.util.List;

import helper.Salesforce;
import helper.SessionHelper;
import model.User;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(AccessLog.class)
public class Signup extends Controller {
    
    @Before
    public static void before() {
        if(SessionHelper.getAdminSessionId() == null) {
            SessionHelper.setAdminSessionId(Salesforce.adminLogin());
        }
        List<model.Question> recentQuestions = model.Question.findRecent(3);
        renderArgs.put("recentQuestions", recentQuestions);
    }

    public static void index() {
        render();
    }
    
    public static void confirm(String nickname, String username, String password , String passwordConfirm , String avator) {
        render(nickname, username, password, avator);
    }
    
    public static void complete(String nickname, String username, String password, String avator) {
        User.createUser(nickname, username, password, avator);
        render();
    }
}
