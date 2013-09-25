package model;

import helper.Salesforce;
import internal.SalesforceFind.Order;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.apache.commons.collections.CollectionUtils;

import exception.SessionTimeoutException;

@SalesforceEntity
public class Question extends SalesforceModel {

    @Id
    @Column
    public String id;

    @Column
    public String title;
    
    @Column
    public String body;
    
    @Column
    public String communityId;
    
    @Column
    public String createdDate;
    
    @ManyToOne
    public User createdBy;
    
    public String category;
    
    public static Question findById(String id) {
        return Salesforce.find(Question.class)
                .join(User.class, "CreatedBy")
                .where("Question.Id = ?", id)
                .getSingle();
    }
    
    public static List<Question> findByIds(String[] ids) {
        return Salesforce.find(Question.class)
                .join(User.class, "CreatedBy")
                .where("Question.Id IN (?)", ids)
                .getList();
    }

    
    public static List<Question> findByUserId(String id) {
        return Salesforce
            .find(Question.class)
            .join(User.class, "CreatedBy")
            .where("Question.CreatedBy.Id = ?", id).getList();
    }
    
    public static List<Question> findLikeQuestionTitle(List<String> words) {
        if(CollectionUtils.isEmpty(words)) {
            return new ArrayList<Question>();
        }
        StringBuilder where = new StringBuilder();
        int size = words.size();
        String[] values = new String[size];
        for(int i = 0; i < size; i++) {
            where.append("Question.Title Like ? ");
            if(i != size - 1) {
                where.append(" AND ");
            }
            values[i] = '%' + words.get(i) + '%';
        }
        return Salesforce
                .find(Question.class)
                .where(where.toString(), values)
                .getList();
    }
    
    public static List<Question> findRecent(int limit) {
        return Salesforce
                .find(Question.class)
                .orderBy("Question.createdDate", Order.DESC)
                .limit(limit)
                .getList();
    }
    public static List<Question> findAllOrderByPostdate() {
        return Salesforce
                    .find(Question.class)
                    .orderBy("Question.createdDate", Order.DESC)
                    .getList();
    }
    
    public static String post(String title, String body, String communityName) {
        Question question = new Question();
        question.title = title;
        question.body = body;
        // FIXME カテゴリ
        //question.communityId = "09aU00000005ve4";
        question.communityId = Community.getIdByName(communityName);
        Salesforce.save(question).portalUser().execute();
        return question.id;
    }
    
    public static void post(Object entity) {
        Salesforce
            .save(entity).portalUser().execute();
    }
        
    public enum Community {
        TELEVISION("09aU0000000Gmw6IAC", "tv")
        ,RECORDER("09aU0000000GmwBIAS", "recorder")
        ,DEGITAL_CAMERA("09aU0000000GmwGIAS", "headphone")
        ,HEADPHONE("09aU0000000GmwLIAS", "camera")
        ,PERIPHERAL("09aU0000000Gmw7IAC", "etc");
        
        private String id;
        private String name;
        Community(String id, String name) {
            this.id = id;
            this.name = name;
        }
        
        public String getId() {
            return this.id;
        }
        
        public String getName() {
            return this.name;
        }
        
        public static String getIdByName(String name) {
        for(Community c : Community.values()) {
            if(c.name.equals(name)) {
                return c.getId();
            }
        }
            return "";
        }
    
        public static String getNameById(String id) {
            for(Community c : Community.values()) {
                if(c.getId().equals(id)) {
                    return c.getName();
                }
            }
            return "";
        }
    
        @Override
        public String toString() {
            return this.name;
        }
    }
}
