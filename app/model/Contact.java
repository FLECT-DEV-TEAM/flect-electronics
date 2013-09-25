package model;

import helper.Salesforce;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import play.cache.Cache;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

@SalesforceEntity
public class Contact extends SalesforceModel {

    @Id
    @Column
    public String id;

    @Column
    public String lastName;
    
    @Column
    public String accountId;
    
    @Column
    public String nickname__c;
    
    @Column
    // TODO Type    
    public String point__c;
    
    @Column
    public String avatar__c;
    
    public static List<Contact> findByAccountId(String accountId) {
        return Salesforce.find(Contact.class)
            .where("Contact.AccountId = ?", accountId)
            .getList();
    }
}
