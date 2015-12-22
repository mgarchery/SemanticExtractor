package com.insalyon;

/**
 * Created by mgarchery on 06/12/2015.
 */
public class Analogy {

    private RDFTriple baseRelation;
    private RDFTriple analogyRelation;

    public Analogy(RDFTriple baseRelation, RDFTriple analogyRelation) {
        this.baseRelation = baseRelation;
        this.analogyRelation = analogyRelation;
    }


    public double getCosim(){
        return MathUtil.cosineSimilarity(baseRelation.getVector(), analogyRelation.getVector());
    }

    @Override
    public String toString(){
        return baseRelation.getSubject() + " : " + baseRelation.getObject(true)
                + " :: " + analogyRelation.getSubject() + " : " + analogyRelation.getObject()
                + " (" + baseRelation.getRelation() + " " + String.format("%.3f", getCosim()) +")";
    }

}
