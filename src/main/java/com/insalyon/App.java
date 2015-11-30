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

            //getSimilarTypesTest();

            getRelationsInCorpusTest();


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private static void getSimilarTypesTest() throws Exception{

        final int MIN_COMMON_TYPES = 10;

        WordVectors vec = Word2VecHelper.loadModel() ;
        Map<String, List<String>> typesIndex = DBpediaHelper.getTypesIndexFromFile();

        AnalogiesExtractor anex = new AnalogiesExtractor(vec,typesIndex);
        List<Map.Entry<String, Integer>> similar= anex.getEntitiesWithSimilarType("France");
        for (Map.Entry<String, Integer> s : similar){
            if(s.getValue() > MIN_COMMON_TYPES){
                log.info(s.getKey() + " " + s.getValue());
            }
        }


    }

    private static void getRelationsInCorpusTest() throws Exception{

        final int MIN_COMMON_TYPES = 10;

        WordVectors vec = Word2VecHelper.loadModel() ;
        Map<String, List<String>> typesIndex = DBpediaHelper.getTypesIndexFromFile();

        AnalogiesExtractor anex = new AnalogiesExtractor(vec,typesIndex);
        List<RDFTriple> relations = anex.getRelationsWithinCorpus(Arrays.asList("France"));

        for (RDFTriple triple : relations){
            log.info(triple.getSubject() + " " + triple.getRelation() + " " + triple.getObject());
        }

    }
}
