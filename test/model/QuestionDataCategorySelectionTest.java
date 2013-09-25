package model;

import helper.Salesforce;
import helper.SessionHelper;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import play.test.UnitTest;

public class QuestionDataCategorySelectionTest extends UnitTest {
    
    @Before
    public void setup() {
        if(SessionHelper.getAdminSessionId() == null) {
            SessionHelper.setAdminSessionId(Salesforce.adminLoginIfNeed());
        }
    }

    @Test
    public void findAll() {
        List<QuestionDataCategorySelection> category 
            = QuestionDataCategorySelection.findAll(QuestionDataCategorySelection.class);
        assertTrue(category.size() > 0);
    }

}
