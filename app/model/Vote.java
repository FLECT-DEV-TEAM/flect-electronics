package model;

import helper.Salesforce;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class Vote extends SalesforceModel {

    @Id
    @Column
    public String id;
    
    @Column
    public String parentId;
    
    @Column
    public String type;
    
    @OneToOne
    public User createdBy;
    
    public static Vote up(String parentId) {
        Vote vote = new Vote();
        vote.parentId = parentId;
        vote.type = "up";
        Salesforce.save(vote).portalUser().execute();
        return vote;
    }

    public static void up(Object entity) {
        Salesforce.save(entity).portalUser().execute();
    }
    
    public static List<Vote> findByReplyId(String id) {
        return Salesforce
            .find(Vote.class)
            .join(User.class, "createdBy")
            .where("ParentId = ?", id)
            .getList();
    }
    public static List<Vote> findByReplyIds(String... ids) {
        return Salesforce
                .find(Vote.class)
                .join(User.class, "createdBy")
                .where("Vote.ParentId IN (?)", ids)
                .getList();
    }
}
