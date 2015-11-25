package com.insalyon;

/**
 * Created by mgarchery on 19/11/2015.
 */
import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.models.embeddings.WeightLookupTable;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
import org.deeplearning4j.text.sentenceiterator.LineSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Word2VecHelper {

    private static Logger log = LoggerFactory.getLogger(Word2VecHelper.class);


    private static final String OUTPUT_VECTORS = "output/" + App.INPUT_RESOURCE + "_vectors" ;

    private static final float LEARNING_RATE = 0.025f;
    private static final int VECTOR_LENGTH = 100;
    private static final int BATCH_SIZE = 1000;
    private static final double SUBSAMPLING = 1e-5;
    private static final int MIN_WORD_FREQUENCY = 5;
    private static final int NET_ITERATIONS = 1;
    private static final int LAYER_SIZE = 100;
    private static final int WINDOW_SIZE = 5;

    public static void extract(String inputResource) throws Exception {

        log.info("Load & Vectorize Sentences....");
        // Strip white space before and after for each line
        SentenceIterator iter = new LineSentenceIterator(new File("input/" + inputResource));

        // Split on white spaces in the line to get words
        TokenizerFactory t = new DefaultTokenizerFactory();
        t.setTokenPreProcessor(new DBpediaPreprocessor());

        InMemoryLookupCache cache = new InMemoryLookupCache();
        WeightLookupTable table = new InMemoryLookupTable.Builder()
                .vectorLength(VECTOR_LENGTH)
                .useAdaGrad(false)
                .cache(cache)
                .lr(LEARNING_RATE).build();

        log.info("Building model....");
        Word2Vec vec = new Word2Vec.Builder()
                .batchSize(BATCH_SIZE)
                .sampling(SUBSAMPLING) //negative sampling
                .minWordFrequency(MIN_WORD_FREQUENCY).iterations(NET_ITERATIONS)
                .layerSize(LAYER_SIZE).lookupTable(table)
                .stopWords(new ArrayList<String>())
                .vocabCache(cache).seed(42)
                .windowSize(WINDOW_SIZE).iterate(iter).tokenizerFactory(t).build();

        log.info("Fitting Word2Vec model....");
        vec.fit();

        log.info("Writing word vectors to text file....");
        // Write word
        WordVectorSerializer.writeWordVectors(vec,OUTPUT_VECTORS);
        writeParametersFile(inputResource);
    }

    public static WordVectors loadModel() throws Exception{
        File f = new File(OUTPUT_VECTORS);
        Pair<InMemoryLookupTable, VocabCache> p = WordVectorSerializer.loadTxt(f);
        return WordVectorSerializer.fromPair(p);
    }

    private static void writeParametersFile(String inputResource) throws Exception{
        File f = new File(OUTPUT_VECTORS + ".parameters");
        FileOutputStream fos = new FileOutputStream(f);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        List<String> lines = new ArrayList<String>();
        lines.add("INPUT_RESOURCE : " + inputResource);
        lines.add("LEARNING_RATE : " + LEARNING_RATE);
        lines.add("VECTOR_LENGTH : " + VECTOR_LENGTH);
        lines.add("BATCH_SIZE : " + BATCH_SIZE);
        lines.add("SUBSAMPLING : " + SUBSAMPLING);
        lines.add("MIN_WORD_FREQUENCY : " + MIN_WORD_FREQUENCY);
        lines.add("NET_ITERATIONS : " + NET_ITERATIONS);
        lines.add("LAYER_SIZE : " + LAYER_SIZE);
        lines.add("WINDOW_SIZE : " + WINDOW_SIZE);

        for (String line : lines){
            bw.write(line);
            bw.newLine();
        }
        bw.close();


    }
}
