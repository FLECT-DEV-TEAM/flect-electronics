package model;

import helper.Salesforce;
import internal.SalesforceFind.Order;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@SalesforceEntity
public class Reply extends SalesforceModel {
    
    @Id
    @Column
    public String id;
    
    @Column
    public String body;
    
    @Column
    public String questionId;
    
    @Column
    public String upVotes;
    
    @Column
    public String voteTotal;
    
    @Column
    public String createdDate;
    
    @ManyToOne
    public Question question;

    @OneToOne
    public User createdBy;
    
    public boolean canVote;

    public static List<Reply> findByQuestionId(String questionId) {
        return Salesforce.find(Reply.class)
                .join(Question.class)
                .join(User.class, "CreatedBy")
                .where("Reply.Question.Id = ?", questionId)
                .orderBy("Reply.createdDate", Order.ASC)
                .getList();
    }
    
    public static List<Reply> findByUserId(String id) {
        return Salesforce
                .find(Reply.class)
                .join(User.class, "CreatedBy")
                .where("Reply.CreatedBy.Id = ?", id)
                .getList();
    }
    
    public static void post(Reply entity) {
        Salesforce
            .save(entity)
            .portalUser()
            .execute();
    }

    public static void post(String body, String questionId) {
        Reply reply = new Reply();
        reply.body = body;
        reply.questionId = questionId;
        Salesforce
            .save(reply)
            .portalUser()
            .execute();
    }
}
