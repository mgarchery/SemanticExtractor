package com.insalyon;



import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class App
{
    private static Logger log = LoggerFactory.getLogger(App.class);

    public static final String INPUT_RESOURCE = "enwik8_clean.annotated";
    //public static final String INPUT_RESOURCE = "raw_sentences.txt";

    public static void main( String[] args )
    {

        try{

            //Word2VecRawTextExample.test();

            //Word2VecHelper.extract(INPUT_RESOURCE);
            //DBpediaHelper.annotate();
            //WordVectors vec = Word2VecHelper.loadModel() ;
            //Word2VecHelper.writeEntitiesFile(vec);

            int numberSimilarEntities = 15;
            double minCosim = 0.6;
            int word2vecNeighbors = 1 ;

            String baseEntity = "Berlin";

            AnalogiesExtractor.inferAnalogies(baseEntity, numberSimilarEntities, minCosim, word2vecNeighbors);



        }catch(Exception e){
            e.printStackTrace();
        }

    }

}
