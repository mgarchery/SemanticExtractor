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

            inferAnalogiesTest();



        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private static List<Map.Entry<String, Integer>> getSimilarTypesTest(String baseEntity, int similarEntities, AnalogiesExtractor anex) throws Exception{

        List<Map.Entry<String, Integer>> similar= anex.getEntitiesWithSimilarType(baseEntity, similarEntities);
        for (Map.Entry<String, Integer> s : similar){

                log.info(s.getKey() + " " + s.getValue());

        }
        return similar;
    }

    private static List<RDFTriple> getRelationsInCorpusTest(String baseEntity, AnalogiesExtractor anex) throws Exception{

        List<RDFTriple> relations = anex.getRelationsWithinCorpus(Arrays.asList(baseEntity));

        for (RDFTriple triple : relations){
            log.info(triple.getSubject() + " " + triple.getRelation() + " " + triple.getObject());
//            String vector = "";
//            for(int i = 0; i < triple.getVector().length; i++){
//                vector += triple.getVector()[i] + ";";
//            }
//            log.info(vector);
        }

        return relations;

    }

    private static List<RDFTriple> inferAnalogiesTest() throws Exception{
        String baseEntity = "Paris";
        int numberSimilarEntities = 15;

        //load model and get index and analogies extractor
        WordVectors vec = Word2VecHelper.loadModel() ;
        Map<String, List<String>> typesIndex = DBpediaHelper.getTypesIndexFromFile();
        AnalogiesExtractor anex = new AnalogiesExtractor(vec,typesIndex);

        //get entities similar to base entity
        List<Map.Entry<String, Integer>> similarEntities = getSimilarTypesTest(baseEntity, numberSimilarEntities, anex);

        //get base entity relations
        List<RDFTriple> baseEntityRelations = getRelationsInCorpusTest(baseEntity, anex);

        //infer analogies
        List<RDFTriple> newRelations = anex.inferAnalogies(baseEntity, baseEntityRelations, similarEntities);

        for (RDFTriple triple : newRelations){
            log.info(triple.getSubject() + " " + triple.getRelation() + " " + triple.getObject());
//            String vector = "";
//            for(int i = 0; i < triple.getVector().length; i++){
//                vector += triple.getVector()[i] + ";";
//            }
//            log.info(vector);
        }

        return newRelations;

    }
}
