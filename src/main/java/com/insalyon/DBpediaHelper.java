package com.insalyon;

import javafx.util.Pair;
import org.dbpedia.spotlight.evaluation.external.DBpediaSpotlightClient;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.Text;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by mgarchery on 20/11/2015.
 */
public class DBpediaHelper {

    private static Logger log = LoggerFactory.getLogger(DBpediaHelper.class);

    public static void annotate() throws Exception{

        DBpediaSpotlightCustomClient client = new DBpediaSpotlightCustomClient();

        List<Pair<String,DBpediaResource>> annotations = new ArrayList<Pair<String,DBpediaResource>>();

        File input = new File("input/" + App.INPUT_RESOURCE);
        BufferedReader br = new BufferedReader(new FileReader(input));

        File output = new File("input/" + App.INPUT_RESOURCE + ".annotated");
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));

        String line;
        String annotatedLine;

        while (( line = br.readLine()) != null) {

            annotatedLine = line;
            try{
                //annotate line
                Text t = new Text(line);
                List<Pair<String,DBpediaResource>> lineAnnotations = client.extractWithSurfaceForm(t);
                annotations.addAll(lineAnnotations);
                for(Pair<String,DBpediaResource> annotation: lineAnnotations){
                    annotatedLine = annotatedLine.replaceAll(annotation.getKey(), annotation.getValue().uri());
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            bw.write(annotatedLine);
            bw.newLine();
            bw.flush();
        }
        br.close();
        bw.close();


        //log.info("Found " + annotations.size() + " annotations");
//        for (Pair<String,DBpediaResource> p : annotations){
//            log.info(p.getKey() + " : " + p.getValue().getFullUri());
//        }

    }

    public static void getRDFRelations(Collection<String> dbpediaResources) throws Exception{

        File output = new File("output/" + App.INPUT_RESOURCE + ".annotated.relations");
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));

        for(String entity1 : dbpediaResources){
            for(String entity2 : dbpediaResources){

                if(entity1 != entity2){
                    List<String []> relations = JenaSparqlClient.getRelations(entity1, entity2);

                    for (String[] relation : relations) {
                        bw.write(relation[0] + "\t" + relation[1] + "\t" + relation[2]);
                        bw.newLine();
                        bw.flush();
                    }
                }
            }
        }
        bw.close();
    }

    public static void getNearestRDFRelations(WordVectors word2vec, int nearestNeighbors) throws Exception{

        File output = new File("output/" + App.INPUT_RESOURCE + ".annotated.relations");
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));

        for(String entity1 : word2vec.vocab().words()){
            for(String entity2 : word2vec.wordsNearest(entity1,nearestNeighbors)){

                if(entity1 != entity2) {
                    List<String[]> relations = JenaSparqlClient.getRelations(entity1, entity2);

                    for (String[] relation : relations) {
                        bw.write(relation[0] + "\t" + relation[1] + "\t" + relation[2]);
                        bw.newLine();
                        bw.flush();
                    }
                }
            }
        }
        bw.close();
    }

    /**
     * builds an index of entities in given word2vec model vocabulary by type
     * serializes index to binary file
     * @param word2vec word2vec model to consider
     * @return map<type, list<entities of this type>>
     * @throws Exception
     */
    public static Map<String,List<String>> getTypesIndex(WordVectors word2vec) throws Exception{

        // map of <dbpedia type, <list of entities of this type>>
        Map<String,List<String>> typesIndex = new HashMap<String, List<String>>();

        log.info("Constructing type index");
        for(String entity : word2vec.vocab().words()){

            try{
                List<String> types = JenaSparqlClient.getTypes(entity);
                for (String type : types){
                    if(typesIndex.containsKey(type)){
                        typesIndex.get(type).add(entity);
                    }else{
                        log.info("found new type: " + type);
                        typesIndex.put(type, new ArrayList<String>(Arrays.asList(entity)));
                    }
                }
            }catch(Exception e){
                log.info("error retrieving type for :" + entity + " : " + e.getMessage());
            }

        }

        log.info("Serializing type index");
        File output = new File("output/" + App.INPUT_RESOURCE + ".annotated.typesIndex");
        ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(output));
        os.writeObject(typesIndex);
        os.close();

        return typesIndex;
    }

    /**
     * deserializes type index from binary file
     * @return type index
     * @throws Exception if any file error occurs
     */
    public static Map<String,List<String>> getTypesIndexFromFile() throws Exception{
        log.info("Deserializing type index");
        File input = new File("output/" + App.INPUT_RESOURCE + ".annotated.typesIndex");
        ObjectInputStream is = new ObjectInputStream(new FileInputStream(input));
        Map<String,List<String>> typesIndex = (Map<String,List<String>>)is.readObject();
        is.close();
        return typesIndex;
    }

    /**
     * writes type index to text file
     * @param index index object
     * @param detail set to true to write all entities of each type, set to false for only the number of entites per type
     * @throws Exception if any file error occurs
     */
    public static void writeTypesIndexToTextFile(Map<String,List<String>> index, boolean detail) throws Exception{
        File output = new File("output/" + App.INPUT_RESOURCE + ".annotated.typesIndexText");
        BufferedWriter bw = new BufferedWriter(new FileWriter(output));

        List<Map.Entry<String,List<String>>> sortedIndex = new ArrayList<Map.Entry<String,List<String>>>(index.entrySet());

        Collections.sort(sortedIndex, new Comparator<Map.Entry<String,List<String>>>() {

            public int compare(Map.Entry<String,List<String>> o1, Map.Entry<String,List<String>> o2) {
                return o2.getValue().size() - o1.getValue().size();
            }
        });

        for(Map.Entry<String,List<String>> type : sortedIndex){
            String line = (detail ? type : type.getKey()) + " (" + type.getValue().size() + ")";
            bw.write(line);
            bw.newLine();
            bw.flush();
        }
        bw.close();
    }


}


