package com.insalyon;

/**
 * Created by mgarchery on 01/12/2015.
 */
public class MathUtil {

    /**
     * computes the difference between two vectors of same length
     * @param u
     * @param v
     * @return
     */
    public static double[] vectorMinus(double[] u, double[] v){
        double[] res = new double[u.length];
        for(int i = 0 ; i < u.length; i++){
            res[i] = u[i] - v[i];
        }
        return res;
    }

    /**
     * computes the cosine similarity between two input vectors (assuming same length)
     * @param vectorA
     * @param vectorB
     * @return cosine similarity as double
     */
    private static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
