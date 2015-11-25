package com.insalyon;

import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;

/**
 * Created by mgarchery on 25/11/2015.
 */
public class DBpediaPreprocessor implements TokenPreProcess {
    public DBpediaPreprocessor() {
    }

    //return token as is (corpus with DBpedia annotations is already clean)
    public String preProcess(String token) {
        return token;
    }
}
