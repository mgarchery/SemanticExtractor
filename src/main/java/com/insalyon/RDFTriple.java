package com.insalyon;

/**
 * Created by mgarchery on 30/11/2015.
 */
public class RDFTriple {

    private String subject;
    private String relation;
    private String object;
    private double[] vector;

    private static final String PREFIX = "http://dbpedia.org/resource/";


    public RDFTriple(String subject, String relation, String object){
        this.subject = subject;
        this.relation = relation;
        this.object = object;

    }

    public String getSubject() {
        return subject;
    }

    public String getObject() {
        return object;
    }

    public String getSubject(boolean deletePrefix) {
        if(deletePrefix){
           return deletePrefix(subject);
        }else{
            return subject;
        }
    }

    public String getRelation() {
        return relation;
    }

    public String getObject(boolean deletePrefix) {
        if(deletePrefix){
            return deletePrefix(object);
        }else{
            return object;
        }
    }

    public double[] getVector() {
        return vector;
    }

    public void setVector(double[] vector) {
        this.vector = vector;
    }

    private String deletePrefix(String resource){
        return resource.replaceAll(PREFIX,"");
    }
}
