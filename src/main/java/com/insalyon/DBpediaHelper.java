package com.insalyon;

import javafx.util.Pair;
import org.dbpedia.spotlight.evaluation.external.DBpediaSpotlightClient;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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


        log.info("Found " + annotations.size() + " annotations");
//        for (Pair<String,DBpediaResource> p : annotations){
//            log.info(p.getKey() + " : " + p.getValue().getFullUri());
//        }

        }


    }


