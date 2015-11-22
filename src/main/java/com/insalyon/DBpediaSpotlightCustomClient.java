package com.insalyon;

import javafx.util.Pair;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;
import org.dbpedia.spotlight.evaluation.external.AnnotationClient;
import org.dbpedia.spotlight.evaluation.external.DBpediaSpotlightClient;
import org.dbpedia.spotlight.exceptions.AnnotationException;
import org.dbpedia.spotlight.model.DBpediaResource;
import org.dbpedia.spotlight.model.Text;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by mgarchery on 20/11/2015.
 */
public class DBpediaSpotlightCustomClient extends DBpediaSpotlightClient {

    //private final static String API_URL = "http://spotlight.dbpedia.org/";
    private final static String API_URL = "http://spotlight.sztaki.hu:2222/";
    private static final double CONFIDENCE = 0.3;
    private static final int SUPPORT = 10;

    public List<Pair<String,DBpediaResource>> extractWithSurfaceForm(Text text) throws AnnotationException {

        LOG.info("Querying API.");
        String spotlightResponse;
        try {
            GetMethod getMethod = new GetMethod(API_URL + "rest/annotate/?" +
                    "confidence=" + CONFIDENCE
                    + "&support=" + SUPPORT
                    + "&text=" + URLEncoder.encode(text.text(), "utf-8"));
            getMethod.addRequestHeader(new Header("Accept", "application/json"));

            spotlightResponse = request(getMethod);
        } catch (UnsupportedEncodingException e) {
            throw new AnnotationException("Could not encode text.", e);
        }

        assert spotlightResponse != null;

        JSONObject resultJSON = null;
        JSONArray entities = null;

        try {
            resultJSON = new JSONObject(spotlightResponse);
            entities = resultJSON.getJSONArray("Resources");
        } catch (JSONException e) {
            throw new AnnotationException("Received invalid response from DBpedia Spotlight API.");
        }

        LinkedList<Pair<String,DBpediaResource>> resources = new LinkedList<Pair<String,DBpediaResource>>();
        for(int i = 0; i < entities.length(); i++) {
            try {
                JSONObject entity = entities.getJSONObject(i);

                Pair<String, DBpediaResource> p = new Pair(entity.get("@surfaceForm"),new DBpediaResource(entity.getString("@URI")));

                resources.add(p);

            } catch (JSONException e) {
                LOG.error("JSON exception "+e);
            }

        }


        return resources;
    }

}
