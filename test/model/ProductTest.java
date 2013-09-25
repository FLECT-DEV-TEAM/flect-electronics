package model;

import helper.Conf;
import helper.Salesforce;
import helper.SessionHelper;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.cache.Cache;
import play.test.UnitTest;

public class ProductTest extends UnitTest {

    @Before
    public void setup() {
        if(SessionHelper.getAdminSessionId() == null) {
            SessionHelper.setAdminSessionId(Salesforce.adminLoginIfNeed());
        }
    }

    @Test
    public void findAll() {
        assertTrue(Product2.findAll(Product2.class).size() > 0);
    }
}
