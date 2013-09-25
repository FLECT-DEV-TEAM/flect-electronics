package model;

import helper.Salesforce;
import helper.SessionHelper;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;


public class AssetTest extends UnitTest {

    @Before
    public void setup() {
        if(SessionHelper.getAdminSessionId() == null) {
            SessionHelper.setAdminSessionId(Salesforce.adminLoginIfNeed());
        }
    }

    @Test
    public void findByContactId() {
        List<Asset> assetList = 
            Asset.findByContactId("003U0000006Ci9eIAC");
        assertTrue(assetList.size() > 0);
        assertTrue(!assetList.get(0).contact.id.isEmpty());
        assertTrue(!assetList.get(0).product2.id.isEmpty());
    }
}
