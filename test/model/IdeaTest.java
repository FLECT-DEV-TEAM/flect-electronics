package model;

import helper.Conf;
import helper.Salesforce;
import helper.SessionHelper;

import java.util.List;

import net.sf.oval.constraint.AssertTrue;

import org.junit.Before;
import org.junit.Test;

import play.cache.Cache;
import play.test.UnitTest;

public class IdeaTest extends UnitTest {
    @Before
    public void setup() {
        if(SessionHelper.getAdminSessionId() == null) {
            SessionHelper.setAdminSessionId(Salesforce.adminLoginIfNeed());
        }
    }

    @Test
    public void findAll() {
        List<Idea> ideas = Idea.findAll(Idea.class);
        assertTrue(ideas.size() > 0);
    }
}
