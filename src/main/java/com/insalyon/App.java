package com.insalyon;



import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            //Word2VecHelper.extract(INPUT_RESOURCE);
            //DBpediaHelper.annotate();
            //WordVectors vec = Word2VecHelper.loadModel() ;
            //Word2VecHelper.writeEntitiesFile(vec);

//            log.info("vocab size : " + vec.vocab().words().size());
//            log.info("nearest words to city : " + vec.wordsNearest("city",10));
//            log.info("nearest words to France : " + vec.wordsNearest("France",20));
//            log.info("nearest words to Paris : " + vec.wordsNearest("Paris",20));

            // king - queen = man - woman
//            log.info("woman + king - man = " + vec.wordsNearest(Arrays.asList("woman", "king"), Arrays.asList("man"), 10));

            // france - paris = china - beijing
//            log.info("china + paris - france = " + vec.wordsNearest(Arrays.asList("china", "paris"), Arrays.asList("france"), 10));

            //JenaSparqlClient.getRelations("Berlin", "Germany");


            //DBpediaHelper.getRDFRelations(vec.vocab().words());
            //DBpediaHelper.getRDFRelations(Arrays.asList("France", "Paris", "Berlin", "Germany", "Europe"));

           // DBpediaHelper.getRDFRelations(vec.wordsNearest("France",10));

           // DBpediaHelper.getNearestRDFRelations(vec,10);

            //DBpediaHelper.getTypesIndex(vec);

            Map<String, List<String>> typesIndex = DBpediaHelper.getTypesIndexFromFile();
            DBpediaHelper.writeTypesIndexToTextFile(typesIndex, false);




        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
