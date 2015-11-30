package com.insalyon;

/**
 * Created by mgarchery on 30/11/2015.
 */
public class RDFTriple {

    private String subject;
    private String relation;
    private String object;

    public RDFTriple(String subject, String relation, String object){
        this.subject = subject;
        this.relation = relation;
        this.object = object;
    }

    public String getSubject() {
        return subject;
    }

    public String getRelation() {
        return relation;
    }

    public String getObject() {
        return object;
    }
}
