package controllers;

import helper.SessionHelper;
import internal.SimpleTimer;
import model.User;
import play.Logger;
import play.mvc.After;
import play.mvc.Before;
import play.mvc.Controller;

public class AccessLog extends Controller {

    private static SimpleTimer timer = new SimpleTimer();    
    @Before
    public static void before() {
        timer.begin();
        Logger.info("[Start]"+ "["+ getUsername() + "]"+request.controller + "/" + request.actionMethod);
    }
    
    @After
    public static void after() {
        timer.end();
        Logger.info("[End]"+ "["+ timer.getResult() + "ms" + "]" + "[" + getUsername() + "]"+request.controller + "/" + request.actionMethod);
    }
    
    private static String getUsername() {
        String username = SessionHelper.getUsername();
        if(username == null) {
            return "UNKNOWN USER";
        }
        return username;
    }
}
