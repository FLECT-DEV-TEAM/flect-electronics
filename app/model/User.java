package model;

import helper.Salesforce;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.commons.lang.StringUtils;

import exception.PlayRuntimeException;

@SalesforceEntity
public class User extends SalesforceModel {

    @Id
    @Column
    public String id;
    
    @Column
    public String name;
    
    @Column
    public String username;
    
    @Column
    public String email;
    
    @Column
    public String profileId;
    
    @Column
    public String isPortalEnabled;
    
    @Column
    public String contactId;
    
    @Column
    public String userType;
    
    @Column
    public String lastName;
    
    @Column
    public String alias;
    
    @Column
    public String timeZoneSidKey;
    
    @Column
    public String localeSidKey; 
    
    @Column
    public String emailEncodingKey;
    
    @Column
    public String languageLocaleKey;
    
    @Column
    public String userRoleId;
    
    @Column
    public String communityNickname;
    
    @Column
    public String point__c;
    
    @Column
    public String avator__c;
    
    @OneToOne
    public Contact contact;
    
    public static User findByUserName(String username) {
        return Salesforce.find(User.class)
                    .where("User.Username = ?", username)
                    .getSingle();
    }
    
    public static User findById(String userId) {
        return Salesforce.find(User.class)
                .where("User.Id = ?", userId)
                .getSingle();
    }
    
    public static User findContactByUserId(String userId) {
        return Salesforce.find(User.class)
                    .join(Contact.class)
                    .where("User.Id = ?", userId)
                    .getSingle();
    }
    
    public static void addPoint(User user, int point) {
        if(StringUtils.isEmpty(user.id)) {
            throw new PlayRuntimeException("ユーザIDが空です。");
        }
        int current = (int)Double.parseDouble(user.point__c);
        user.point__c = String.valueOf(current + point);
        user.name = null;
        user.userType = null;
        user.contactId = null;
        Salesforce.save(user).execute();
    }
    
    public static void createUser(String nickname, String username, String password, String avator) {
        
        Contact contact = new Contact();
        contact.lastName = nickname;
        contact.point__c = "0";
        contact.nickname__c = nickname;
        contact.avatar__c = "type01";
        contact.accountId = "001U0000005z67YIAQ";
        Contact.save(contact);
        
        User user = new User();
        user.contactId = contact.id;
        user.username = username;
        user.email = username;
        user.alias = substrAlias(nickname);
        user.emailEncodingKey = "ISO-2022-JP";
        user.languageLocaleKey = "ja";
        user.localeSidKey = "ja_JP";
        user.timeZoneSidKey = "Asia/Tokyo";
        user.lastName = nickname;
        user.profileId = "00eU0000000EQc3IAG";
        user.communityNickname = nickname;
        user.point__c = "0";
        user.avator__c = avator;
        User.save(user);
        
        Salesforce.setPassword(user, password);    
    }
    private static String substrAlias(String nickname) {
        if(nickname.length() > 8) {
            nickname.substring(0, 8);
        }
        return nickname;
    }
}
