package controllers;

import helper.Salesforce;
import helper.SessionHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Product2;
import model.Product2.Family;
import model.Question.Community;
import model.Reply;
import model.User;
import model.Vote;

import org.apache.commons.lang.StringUtils;

import play.Logger;
import play.cache.Cache;
import play.mvc.Before;
import play.mvc.Catch;
import play.mvc.Controller;
import exception.SessionTimeoutException;
import ext.CategoryExtensions;
import ext.DateExtensions;
import ext.QuestionExtensions;

public class Question extends Controller {

    @Catch(SessionTimeoutException.class)
    public static void sessionTimeout(Throwable t) {
        Logger.info("session timeout.", t);
        SessionHelper.invalidate();
        SessionHelper.setSessionTimeout();
        Application.index();
    }
    
    @Before
    public static void before() {
        if(SessionHelper.getAdminSessionId() == null) {
            SessionHelper.setAdminSessionId(Salesforce.adminLogin());
        }
        String nickname = SessionHelper.getNickname();
        if(nickname == null) {
            nickname = "ゲスト";
        }
        List<model.Question> recentQuestions = model.Question.findRecent(3);
        renderArgs.put("recentQuestions", recentQuestions);
        renderArgs.put("loginNickname", nickname);
    }
    
    public static void index(String category, String product) {
        render(category, product);
    }
    
    public static void post(String qtitle, String qbody, String category, String product) {
        model.Question.post(qtitle, qbody, category);
        Application.index();
    }
    
    public static void answer(String questionId, String addPointUser) {
        model.Question question = model.Question.findById(questionId);
        List<Reply> replies = Reply.findByQuestionId(questionId);
        // TODO 一回で取るようにしたい
        String[] replyIds = new String[replies.size()]; 
        for(int i = 0; i < replies.size(); i++) {
            replyIds[i] = replies.get(i).id;
            // 自分で返信したものには投票できない
            replies.get(i).canVote =
                !replies.get(i).createdBy.username.equals(SessionHelper.getUsername());
        }
        List<Vote> votes = new ArrayList<Vote>();
        for(Reply reply : replies) {
            if(reply.canVote) {
                // INで取れないという制約が・・・
                votes.addAll(Vote.findByReplyId(reply.id));
            }
        }
        List<String> votedReplyIds = new ArrayList<String>();
        for(Vote vote : votes) {
            // 一度投票したものには投票できない
            if(vote.createdBy.username.equals(SessionHelper.getUsername())) {
                votedReplyIds.add(vote.parentId);
            }
        }
        for(Reply reply : replies) {
            if(votedReplyIds.contains(reply.id)) {
                reply.canVote = false;
            }
        }
        render(question, replies, addPointUser);
    }
    
    public static void postAnswer(String rbody, String questionId) {
        Reply.post(rbody, questionId);
        answer(questionId, null);
    }
    
    public static void search(String q, String productCode, String categoryName) {
        Product2 selectedProduct = null;
        Cache.set(SessionHelper.getSessionId() + "questionsPage", null);
        if(productCode != null) {
            selectedProduct = Product2.findByProductCode(productCode);
        }
        List<model.Question> questions = new ArrayList<model.Question>();
        if(StringUtils.isEmpty(q)) {
            if(StringUtils.isEmpty(categoryName) && selectedProduct != null) {
                categoryName = selectedProduct.familyEn__c;
            } else if(categoryName != null){
                selectedProduct = Product2.findProductsByFamilyEn(categoryName);
            }
            // FIXME 204SOLSRV-27
            List<model.Question> allQuestions = model.Question.findAllOrderByPostdate();
            for(model.Question question : allQuestions) {
                if(selectedProduct != null 
                        && Community.getNameById(question.communityId).equals(selectedProduct.familyEn__c)) {
                    questions.add(question);
                }
            }
        } else {
            String[] qArray = q.split(" ");
            questions = model.Question.findLikeQuestionTitle(Arrays.asList(qArray));    
        }
        Family[] families = Family.values();
        List<Product2> products = Product2.findAllOrdered();
        boolean hasNext = false;
        int size = questions.size();
        if(size > 5) {
            Cache.set(SessionHelper.getSessionId() + "questions", questions);
            hasNext = true;
            questions = questions.subList(0, 5);
        }
        render(questions, q, families, products, selectedProduct, hasNext, size);
    }
    
    public static void searchNext() {
        List<model.Question> cached = (List<model.Question>)Cache.get(SessionHelper.getSessionId() + "questions");
        List<model.Question> questions = new ArrayList<model.Question>();
        Integer next = (Integer)Cache.get(SessionHelper.getSessionId() + "questionsPage");
        if(next == null) {
            next = 1;
        }
        try {
            for(int i = next * 5; i < next * 5 + 5; i++) {
                if(cached != null && i >= cached.size()) {
                    break;
                }
                cached.get(i).createdDate = DateExtensions.formatDate(DateExtensions.localeJa(cached.get(i).createdDate));
                cached.get(i).category = CategoryExtensions.getNameById(cached.get(i).communityId);
                cached.get(i).title = QuestionExtensions.shorten(cached.get(i).title, 35);
                cached.get(i).body = QuestionExtensions.shorten(cached.get(i).body, 60);
                questions.add(cached.get(i));
            }
            
        } catch(Exception e){
            e.printStackTrace();
        }
        QuestionList list = new QuestionList();
        list.hasNext = cached != null && cached.size() > (next + 1) * 5;
        list.questions = questions;
        next++;
        Cache.set(SessionHelper.getSessionId() + "questionsPage", next);
        renderJSON(list);
    }
    
    public static void point(String questionId, String replyId, String userId) {
        Vote.up(replyId);
        User user = User.findById(userId);
        if(user != null) {
            User.addPoint(user, 10);
        }
        answer(questionId, user.communityNickname);
    }
    
    public static class QuestionList {
        public List<model.Question> questions;
        public boolean hasNext;
    }
}
