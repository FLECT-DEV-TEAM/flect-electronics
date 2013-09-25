package helper;

import java.util.List;

import net.java.sen.dictionary.Token;

import org.junit.Test;

import play.test.UnitTest;

public class GoSenTest extends UnitTest {

    @Test
    public void simpleAnalyze() {
        GoSen gosen = new GoSen();
        List<String> words = gosen.analyze("低音がよく出る機種を教えてください。");
        assertEquals(words.size(), 2);
        assertEquals(words.get(0), "低音");
        assertEquals(words.get(1), "機種");
    }
}
