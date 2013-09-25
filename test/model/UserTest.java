package model;

import helper.Salesforce;
import helper.SessionHelper;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class UserTest extends UnitTest {
    
    
    @Before
    public void setup() {
        if(SessionHelper.getAdminSessionId() == null) {
            SessionHelper.setAdminSessionId(Salesforce.adminLoginIfNeed());
        }
    }

    @Test
    public void findContactByUserId() {
        User user = User.findContactByUserId("005U0000000ErlXIAS");
        assertNotNull(user);
    }
    
    @Test
    public void addPoint() {
        User user1 = User.findContactByUserId("005U0000000ErlXIAS");
        double before = Double.parseDouble(user1.point__c);
        user1.addPoint(user1, 10);
        User user2 = User.findContactByUserId("005U0000000ErlXIAS");
        double after = Double.parseDouble(user2.point__c);
        assertTrue(before < after);
    }
    

    
    //@Test
    public void createUser() {
        Contact contact = new Contact();
        contact.lastName = "テストユーザ01";
        contact.point__c = "0";
        contact.nickname__c = "テストユーザ01";
        contact.avatar__c = "type01";
        contact.accountId = "001U0000005z67YIAQ";
        Contact.save(contact);
        User user = new User();
        user.contactId = contact.id;
        user.username = "test75@solsrv.flect.co.jp";
        user.email = "nishinaka.shinya@gmail.com";
        user.alias = contact.nickname__c;
        user.emailEncodingKey = "ISO-2022-JP";
        user.languageLocaleKey = "ja";
        user.localeSidKey = "ja_JP";
        user.timeZoneSidKey = "Asia/Tokyo";
        user.lastName = contact.nickname__c;
        user.profileId = "00eU0000000EQc3IAG";
        
        try {
            User.save(user);
            Salesforce.setPassword(user, "password1234!");
            
        } catch(Exception e) {
            fail();
        }
    }
}
