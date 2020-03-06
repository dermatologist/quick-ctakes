package com.canehealth.qtakes;

import javax.annotation.PostConstruct;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ctakes.core.pipeline.PipelineBuilder;
import org.apache.ctakes.core.pipeline.PiperFileReader;
import org.apache.uima.UIMAFramework;
import org.apache.uima.jcas.JCas;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.util.JCasPool;

import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/analyze")
public class CtakesResource {
    private static final String DEFAULT_PIPER_FILE = "/META-INF/resources/Default.piper";
    private static final String DEFAULT_DICT_FILE = "/META-INF/resources/customDictionary.xml";
    private static final String DEFAULT_PIPELINE = "Default";
    private static final Map<String, PipelineRunner> _pipelineRunners = new HashMap<>();

    @PostConstruct
    public void init() {
        writeResources();
        _pipelineRunners.put(DEFAULT_PIPELINE, new PipelineRunner("/tmp/Default.piper"));
    }

    /**
     * The pipeline (Default.piper) file and the init method above
     * require the full/relative path. Accessing the path inside the container
     * is tricky. Hence the resources are written to the tmp folder and read
     * from there.
     */
    private void writeResources() {
        InputStream in = getClass().getResourceAsStream(DEFAULT_PIPER_FILE); 
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            //piper = File.createTempFile("Default", ".piper");
            File piper = new File("/tmp/Default.piper");
            piper.delete();
            piper.createNewFile();
            BufferedWriter bw = new BufferedWriter(new FileWriter(piper));
            String line;
            while ((line = reader.readLine()) != null) {
                bw.write(line);
                // must do this: .readLine() will have stripped line endings
                bw.newLine();
            }
            bw.close();
            reader.close();
            in = getClass().getResourceAsStream(DEFAULT_DICT_FILE); 
            reader = new BufferedReader(new InputStreamReader(in));
            //File dictFile = File.createTempFile("customDictionary", ".xml");
            File dictFile = new File("/tmp/customDictionary.xml");
            dictFile.delete();
            dictFile.createNewFile();
            bw = new BufferedWriter(new FileWriter(dictFile));
            while ((line = reader.readLine()) != null) {
                bw.write(line);
                // must do this: .readLine() will have stripped line endings
                bw.newLine();
            }
            bw.close();
            reader.close(); 
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
 
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String analyze(String analysisText) {
        Map<String, List<CuiResponse>> pipelineResponse = new HashMap<>();
        try {
            String pipeline = DEFAULT_PIPELINE;
            final PipelineRunner runner = _pipelineRunners.get(pipeline);
            pipelineResponse = runner.process(analysisText);
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(pipelineResponse);
            return json;
        } catch (Exception ignored) {
            return analysisText;
        }
    }

    static private Map<String, List<CuiResponse>> formatResults(JCas jcas) throws Exception {
        JCasParser parser = new JCasParser();
        return parser.parse(jcas);
    }


    static private final class PipelineRunner {
        private AnalysisEngine _engine;
        private JCasPool _pool;

        private PipelineRunner(final String piperPath) {
            try {
                PiperFileReader reader = new PiperFileReader(piperPath);
                PipelineBuilder builder = reader.getBuilder();
                AnalysisEngineDescription pipeline = builder.getAnalysisEngineDesc();
                _engine = UIMAFramework.produceAnalysisEngine(pipeline);
                _pool = new JCasPool(10, _engine);
            } catch (Exception e) {

            }
        }

        public Map<String, List<CuiResponse>> process(final String text) {
            JCas jcas = null;
            Map<String, List<CuiResponse>> resultMap = null;
            if (text != null) {
                try {
                    jcas = _pool.getJCas(-1);
                    jcas.setDocumentText(text);
                    _engine.process(jcas);
                    resultMap = formatResults(jcas);
                    _pool.releaseJCas(jcas);
                } catch (Exception e) {

                }
            }
            return resultMap;
        }
    }
}
