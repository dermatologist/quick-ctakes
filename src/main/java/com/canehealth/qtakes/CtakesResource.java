package com.canehealth.qtakes;

import javax.annotation.PostConstruct;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
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

@Path("/analyze")
public class CtakesResource {
    private static final String DEFAULT_PIPER_FILE_PATH = "pipers/Default.piper";
    private static final String FULL_PIPER_FILE_PATH = "pipers/Full.piper";
    private static final String DEFAULT_PIPELINE = "Default";
    private static final String FULL_PIPELINE = "Full";
    private static final Map<String, PipelineRunner> _pipelineRunners = new HashMap<>();

    @PostConstruct
    public void init() {
        _pipelineRunners.put(DEFAULT_PIPELINE, new PipelineRunner(DEFAULT_PIPER_FILE_PATH));
//        _pipelineRunners.put(FULL_PIPELINE, new PipelineRunner(FULL_PIPER_FILE_PATH));
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public String analyze() {
        return "{}";
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
