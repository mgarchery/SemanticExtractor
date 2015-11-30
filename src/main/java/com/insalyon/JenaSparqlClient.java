package com.insalyon;


import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;

import java.util.ArrayList;
import java.util.List;


public class JenaSparqlClient {

    private static String sparqlEndpoint = "http://dbpedia.org/sparql";

    /**
     * gets all DBpedia relations between given subject and given object entities
     * @param subjectResource subject DBpedia resource (without http://dbpedia.org/resource/ prefix)
     * @param objectResource object DBpedia resource (without http://dbpedia.org/resource/ prefix)
     * @return list of RDF triples as string array : [subject][relation][object]
     */
    public static List<String[]> getRelations (String subjectResource, String objectResource){

        ParameterizedSparqlString queryString = new ParameterizedSparqlString(
                        "SELECT DISTINCT ?relation WHERE\n" +
                        "{\n" +
                        "?subject ?relation ?object \n"+
                        "}\n"
        ) {{
            setNsPrefix( "dbres", "http://dbpedia.org/resource/" );
            setNsPrefix( "rdfs", "http://www.w3.org/2000/01/rdf-schema#" );
        }};

        queryString.setIri("?subject", queryString.getNsPrefixURI( "dbres" ) + subjectResource );
        queryString.setIri("?object", queryString.getNsPrefixURI( "dbres" ) + objectResource );

        Query query = QueryFactory.create(queryString.toString(), Syntax.syntaxARQ) ;
        QuerySolutionMap querySolutionMap = new QuerySolutionMap();
        ParameterizedSparqlString parameterizedSparqlString = new ParameterizedSparqlString(query.toString(), querySolutionMap);

        List<String[]> resultsList = new ArrayList<String[]>();
        QueryEngineHTTP httpQuery = new QueryEngineHTTP(sparqlEndpoint,parameterizedSparqlString.asQuery());
        // execute a Select query
        ResultSet results = httpQuery.execSelect();
        while (results.hasNext()) {
            QuerySolution solution = results.next();
            String[] result  = new String[3];
            result[0] = subjectResource;
            result[1] = solution.get("relation").toString();
            result[2] = objectResource;

            resultsList.add(result);

        }
        return resultsList;
    }

    /**
     * gets the DBpedia types from given entity
     * @param subjectResource entity to retrieve the types from
     * @return list<DBpedia types> for input entity
     */
    public static List<String> getTypes (String subjectResource){

        ParameterizedSparqlString queryString = new ParameterizedSparqlString(
                "SELECT DISTINCT ?type WHERE\n" +
                        "{\n" +
                        "?subject a ?type \n"+
                        "}\n"
        ) {{
            setNsPrefix( "dbres", "http://dbpedia.org/resource/" );
            setNsPrefix( "rdfs", "http://www.w3.org/2000/01/rdf-schema#" );
        }};

        queryString.setIri("?subject", queryString.getNsPrefixURI( "dbres" ) + subjectResource );

        Query query = QueryFactory.create(queryString.toString(), Syntax.syntaxARQ) ;
        QuerySolutionMap querySolutionMap = new QuerySolutionMap();
        ParameterizedSparqlString parameterizedSparqlString = new ParameterizedSparqlString(query.toString(), querySolutionMap);

        List<String> resultsList = new ArrayList<String>();
        QueryEngineHTTP httpQuery = new QueryEngineHTTP(sparqlEndpoint,parameterizedSparqlString.asQuery());
        // execute a Select query
        ResultSet results = httpQuery.execSelect();
        while (results.hasNext()) {
            QuerySolution solution = results.next();

            resultsList.add(solution.get("type").toString());
        }
        return resultsList;
    }



}
