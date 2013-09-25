package model;

import helper.Conf;
import helper.Salesforce;
import helper.SessionHelper;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.cache.Cache;
import play.test.Fixtures;
import play.test.UnitTest;

public class ContactTest extends UnitTest {
    
    @Before
    public void setup() {
        if(SessionHelper.getAdminSessionId() == null) {
            SessionHelper.setAdminSessionId(Salesforce.adminLoginIfNeed());
        }
    }

    @Test
    public void findByAccountId() {
        assertTrue(Contact.findByAccountId("001U0000005z67YIAQ").size() > 0);
    }
    
    @Test
    public void saveAndDelete () {
        Contact contact = new Contact();
        contact.lastName = "登録テスト";
        contact.nickname__c = "登録さん";
        contact.point__c = "100";
        contact.avatar__c = "type01";
        Contact.save(contact);
        assertNotNull(contact.id);
        assertTrue(contact.id.length() > 0);
        
        contact.lastName = "更新テスト";
        contact.nickname__c = "更新さん";
        contact.point__c = "200";
        contact.avatar__c = "type02";
        try {
            Contact.save(contact);
        } catch(Exception e) {
            fail();
        }
        try {
            Contact.delete(contact);
        } catch(Exception e) {
            fail();
        }
    }
}
