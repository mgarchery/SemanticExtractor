package com.insalyon;


import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class App
{
    private static Logger log = LoggerFactory.getLogger(Word2VecRawTextExample.class);

    public static void main( String[] args )
    {

        try{
            //Word2VecHelper.extract();
            WordVectors vec = Word2VecHelper.loadModel() ;


            log.info("vocab size : " + vec.vocab().words().size());
            log.info("nearest words to city : " + vec.wordsNearest("city",10));
            log.info("nearest words to france : " + vec.wordsNearest("france",10));

            // king - queen = man - woman
            log.info("woman + king - man = " + vec.wordsNearest(Arrays.asList("woman", "king"), Arrays.asList("man"), 10));

            // france - paris = china - beijing
            log.info("china + paris - france = " + vec.wordsNearest(Arrays.asList("china", "paris"), Arrays.asList("france"), 10));


        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
