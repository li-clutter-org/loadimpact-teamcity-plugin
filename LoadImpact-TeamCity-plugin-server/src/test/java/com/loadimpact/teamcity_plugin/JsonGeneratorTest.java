package com.loadimpact.teamcity_plugin;

import org.junit.Before;
import org.junit.Test;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonStructure;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * DESCRIPTION
 *
 * @author jens
 */
public class JsonGeneratorTest {
    private JsonGenerator target;

    @Before
    public void setUp() throws Exception {
        target = new JsonGenerator();
    }

    private JsonStructure getExpectedJson(String file) {
        InputStream jsonInput = this.getClass().getResourceAsStream(file);
        assertThat(jsonInput, notNullValue());
        return Json.createReader(jsonInput).read();
    }

    private Map<String,String> getExpectedProperties(String file) throws IOException {
        InputStream stream = this.getClass().getResourceAsStream(file);
        assertThat(stream, notNullValue());
        Properties p = new Properties();
        p.load(stream);

        Map<String, String> m = new TreeMap<String, String>();
        for (Map.Entry<Object, Object> e : p.entrySet()) {
            m.put(e.getKey().toString(), e.getValue().toString());
        }
        
        return m;
    }

    @Test
    public void testGetOperatorsTxt() throws Exception {
        String expected = "[{\"name\":\"greaterThan\",\"label\":\"&gt;\"},{\"name\":\"lessThan\",\"label\":\"&lt;\"}]";
        assertThat(target.getOperators(), is(expected));
    }

    @Test
    public void testGetOperators() throws Exception {
        JsonStructure actualJson = Json.createReader(new StringReader(target.getOperators())).read();
        assertThat(actualJson, is(getExpectedJson("/operators.json")));
    }

    @Test
    public void testGetActions() throws Exception {
        JsonStructure actualJson = Json.createReader(new StringReader(target.getActions())).read();
        assertThat(actualJson, is(getExpectedJson("/actions.json")));
    }
    
    @Test
    public void testGetMetrics() throws Exception {
        JsonStructure actualJson = Json.createReader(new StringReader(target.getMetrics())).read();
        assertThat(actualJson, is(getExpectedJson("/metrics.json")));
    }

    @Test
    public void testGetThresholds() throws Exception {
        Map<String,String> settings = getExpectedProperties("/loadTestSettings.properties");
        JsonStructure actualJson = Json.createReader(new StringReader(target.getThresholds(settings))).read();
        assertThat(actualJson, is(getExpectedJson("/thresholds.json")));
    }
}
