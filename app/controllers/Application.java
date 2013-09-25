package controllers;

import helper.Salesforce;
import helper.SessionHelper;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import model.Product2;
import model.Product2.Family;
import model.Product2.SubFamily;
import model.Question;
import model.QuestionDataCategorySelection;
import model.User;
import play.Logger;
import play.cache.Cache;
import play.data.validation.Validation;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;

@With(AccessLog.class)
public class Application extends Controller {

    @Before
    public static void before() {
        if(SessionHelper.getAdminSessionId() == null) {
            SessionHelper.setAdminSessionId(Salesforce.adminLogin());
        }
        String nickname = SessionHelper.getNickname();
        if(nickname == null) {
            nickname = "ゲスト";
        }
        renderArgs.put("loginNickname", nickname);
        if(Cache.get("products") == null) {
            Logger.info("Load Products....");
            Map<Family, Map<SubFamily, List<Product2>>> products = new LinkedHashMap<Product2.Family, Map<SubFamily,List<Product2>>>();
            for(Family family : Family.values()) {
                Map<SubFamily, List<Product2>> subMap = new LinkedHashMap<Product2.SubFamily, List<Product2>>();
                EnumSet<SubFamily> subFamilies = family.getSubFamilies();
                for(SubFamily subFamily : subFamilies) {
                    List<Product2> list = 
                            Product2.findProductsByFamilyAndSubFamily(family.getName(), subFamily.getName());
                    subMap.put(subFamily, list);
                }
                products.put(family, subMap);
            }
            Cache.set("products", products);
            List<model.Question> recentQuestions = model.Question.findRecent(3);
            renderArgs.put("recentQuestions", recentQuestions);
        }
    }

    public static void index() {
        List<Question> questions = Question.findRecent(5);
        boolean sessionTimeout = false;
        if(SessionHelper.getSessionTImeout() != null) {
            sessionTimeout = true;
            SessionHelper.clearSessionTImeout();
        }
        Map<Family, Map<SubFamily, List<Product2>>> products = (LinkedHashMap)Cache.get("products");
        render(questions, products,sessionTimeout);
    }

    public static void login(String username, String password) {
        String sessionId = Salesforce.login(username, password);
        // TODO Login fault message.
        if(sessionId == null) {
            Validation.clear();
            Validation.addError("dummy", "ログインに失敗しました");
        } else {
            User user = User.findByUserName(username);
            SessionHelper.setUsername(user);
            SessionHelper.setNickname(user);
            SessionHelper.setSessionId(sessionId);
        }
        index();
    }
    
   public static void logout() {
       Salesforce.logout(SessionHelper.getSessionId());
       SessionHelper.invalidate();
       index();
   }
    
    public static void invalidate() {
        session.clear();
        index();
    }
}