package helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.java.sen.SenFactory;
import net.java.sen.StringTagger;
import net.java.sen.dictionary.Token;
import exception.PlayRuntimeException;

public class GoSen {
    
    public List<String> analyze(String word) {
        List<String> nouns = new ArrayList<String>();
        try {
            StringTagger tagger = SenFactory.getStringTagger();
            List<Token> tokens = tagger.analyze(word);
            for(Token token : tokens) {
                if(token.getMorpheme().getPartOfSpeech().startsWith("名詞")) {
                    nouns.add(token.toString());
                }
            }
        } catch (IOException e) {
            throw new PlayRuntimeException(e);
        }
        return nouns;
    }
}
