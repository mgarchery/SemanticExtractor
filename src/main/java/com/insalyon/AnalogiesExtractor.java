package com.insalyon;

import javafx.util.Pair;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;

import java.util.*;

/**
 * Created by mgarchery on 30/11/2015.
 */
public class AnalogiesExtractor {

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
                String object = triple.getObject();
                object = object.replaceAll("http://dbpedia.org/resource/", "");
                if(word2vec.hasWord(object)){
                    triple.setVector(MathUtil.vectorMinus(word2vec.getWordVector(object), word2vec.getWordVector(triple.getSubject())));
                    relations.add(triple);
                }
            }

        }

        return relations;
    }


    public List<RDFTriple> inferAnalogies(String baseEntity, List<RDFTriple> baseEntityRelations, List<Map.Entry<String, Integer>> similarEntities){
        List<RDFTriple> analogies = new ArrayList<RDFTriple>();

        for(Map.Entry<String,Integer> similarEntity : similarEntities){
            for(RDFTriple triple : baseEntityRelations){
                List<String> positive = Arrays.asList(triple.getObject(true), similarEntity.getKey());
                List<String> negative = Arrays.asList(baseEntity);

                Collection<String> targets = word2vec.wordsNearest(positive, negative, 1);
                for (String target : targets){
                    analogies.add(new RDFTriple(similarEntity.getKey(), triple.getRelation(), target));
                }

            }
        }

        return analogies;
    }

}
