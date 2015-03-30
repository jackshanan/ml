package kenny.ml.nlp;


import kenny.ml.nn.rbm.math.Matrix;
import org.apache.log4j.Logger;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by kenny on 6/4/14.
 */
public class TestWordDictionary {

    private static final Logger LOGGER = Logger.getLogger(TestWordDictionary.class);

    @Test
    public void lookUpTest() {
        final WordDictionary dictionary = new WordDictionary();

        final String[] words = new String[] {
                "i", "am", "kenny", "and", "i'm", "training", "to", "be", "a", "metroid", "hunter"
        };

        for(String word : words) {
            dictionary.add(word);
        }

        for(String word : words) {
            final Matrix m = dictionary.getVector(word);
            final String prediction = dictionary.getClosestWord(m);
            LOGGER.info(word + " -> " + prediction);
            assertEquals(word, prediction);
        }
    }

}
