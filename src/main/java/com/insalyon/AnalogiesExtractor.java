package com.insalyon;

import javafx.util.Pair;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by mgarchery on 30/11/2015.
 */
public class AnalogiesExtractor {

    private static Logger log = LoggerFactory.getLogger(AnalogiesExtractor.class);

    private WordVectors word2vec;
    private Map<String, List<String>> typesIndex;

    public AnalogiesExtractor(WordVectors word2vec, Map<String, List<String>> typesIndex){
        this.word2vec = word2vec;
        this.typesIndex = typesIndex;
    }

    /**
     * gets entities with similar types to input entity
     * @param entity
     * @return
     */
    public List<Map.Entry<String, Integer>> getEntitiesWithSimilarType(String entity, int nResults){

        Map<String, Integer> unsortedResults = new HashMap<String, Integer>();
        List<Map.Entry<String, Integer>> sortedResults = null;

        if(word2vec.vocab().containsWord(entity)){
            List<String> types = JenaSparqlClient.getTypes(entity);

            for(String type : types){
                List<String> entitiesWithSameType = typesIndex.get(type);
                entitiesWithSameType.remove(entity);

                for (String entityWithSameType : entitiesWithSameType){
                    if(unsortedResults.containsKey(entityWithSameType)){
                        unsortedResults.put(entityWithSameType, unsortedResults.get(entityWithSameType) + 1) ;
                    }else{
                        unsortedResults.put(entityWithSameType, 1) ;
                    }
                }

            }

           sortedResults = new ArrayList<Map.Entry<String, Integer>>(unsortedResults.entrySet());
            Collections.sort(sortedResults, new Comparator<Map.Entry<String,Integer>>() {

                public int compare(Map.Entry<String,Integer> o1, Map.Entry<String,Integer> o2) {
                    return o2.getValue() - o1.getValue();
                }
            });



        }
        if(sortedResults.size() > nResults){
            return sortedResults.subList(0, nResults);
        }else{
            return sortedResults;
        }

    }

    /**
     * gets all relations with given entities as subject within corpus
     * @param entities
     * @return
     */
    public List<RDFTriple> getRelationsWithinCorpus(List<String> entities){
        List<RDFTriple> relations = new ArrayList<RDFTriple>();
        for(String entity : entities){
            List<RDFTriple> rel = JenaSparqlClient.getRelations(entity);
            for (RDFTriple triple : rel){
                String object = triple.getObject(true);
                //object = object.replaceAll("http://dbpedia.org/resource/", "");
                if(word2vec.hasWord(object)){
                    triple.setVector(MathUtil.vectorMinus(word2vec.getWordVector(object), word2vec.getWordVector(triple.getSubject())));
                    relations.add(triple);
                }
            }

        }

        return relations;
    }

    /**
     * finds new analogies using a base entity, its known relations and similar entities
     * returns only analogies whose cosim is greater to minCosim
     * @param baseEntity base entity
     * @param baseEntityRelations known relations of the base entity in the corpus
     * @param similarEntities entities similar to the base entity
     * @param minCosim minimum cosine similarity
     * @return new relations as list of RDF triples
     */
    public List<Analogy> inferAnalogies(String baseEntity, List<RDFTriple> baseEntityRelations, List<Map.Entry<String, Integer>> similarEntities, double minCosim, int word2vecNeighbors){
        List<Analogy> analogies = new ArrayList<Analogy>();

        List<String> negative = Arrays.asList(baseEntity);
        for(Map.Entry<String,Integer> similarEntity : similarEntities){
             for(RDFTriple triple : baseEntityRelations){
                List<String> positive = Arrays.asList(similarEntity.getKey(), triple.getObject(true));

                Collection<String> targets = word2vec.wordsNearest(positive, negative, word2vecNeighbors);
                for (String target : targets){
                    RDFTriple analogyTriple = new RDFTriple(similarEntity.getKey(), triple.getRelation(), target);
                    analogyTriple.setVector(MathUtil.vectorMinus(word2vec.getWordVector(target), word2vec.getWordVector(similarEntity.getKey())));
                    Analogy a = new Analogy(triple, analogyTriple);

                    if(a.getCosim() >= minCosim){
                        analogies.add(a);
                    }


                }

            }
        }

        return analogies;
    }


    private static List<Map.Entry<String, Integer>> getSimilarEntities(String baseEntity, int similarEntities, AnalogiesExtractor anex) throws Exception{

        List<Map.Entry<String, Integer>> similar = anex.getEntitiesWithSimilarType(baseEntity, similarEntities);
        for (Map.Entry<String, Integer> s : similar){

            log.info(s.getKey() + " " + s.getValue());

        }
        return similar;
    }

    private static List<RDFTriple> getRelationsInCorpus(String baseEntity, AnalogiesExtractor anex) throws Exception{

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

    public static List<Analogy> inferAnalogies(String baseEntity, int numberSimilarEntities, double minCosim, int word2vecNeighbors) throws Exception{


        //load model and get index and analogies extractor
        WordVectors vec = Word2VecHelper.loadModel() ;
        Map<String, List<String>> typesIndex = DBpediaHelper.getTypesIndexFromFile();
        AnalogiesExtractor anex = new AnalogiesExtractor(vec,typesIndex);

        //get entities similar to base entity
        List<Map.Entry<String, Integer>> similarEntities = getSimilarEntities(baseEntity, numberSimilarEntities, anex);

        if(similarEntities.isEmpty()){
            log.info("Found no entity similar to '" + baseEntity +"'");
        }

        //get base entity relations
        List<RDFTriple> baseEntityRelations = getRelationsInCorpus(baseEntity, anex);

        if(baseEntityRelations.isEmpty()){
            log.info("Found no relations in corpus for '" + baseEntity +"'");
        }

        //infer analogies
        List<Analogy> newRelations = anex.inferAnalogies(baseEntity, baseEntityRelations, similarEntities, minCosim, word2vecNeighbors);

        if(newRelations.isEmpty()){
            log.info("Found no analogies for '" + baseEntity +"'");
        }

        for (Analogy a : newRelations){
            log.info(a.toString());
//            String vector = "";
//            for(int i = 0; i < triple.getVector().length; i++){
//                vector += triple.getVector()[i] + ";";
//            }
//            log.info(vector);
        }


        return newRelations;

    }

}
