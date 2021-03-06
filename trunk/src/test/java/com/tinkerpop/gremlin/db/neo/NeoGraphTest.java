package com.tinkerpop.gremlin.db.neo;

import com.tinkerpop.gremlin.model.*;
import junit.framework.TestCase;

import java.io.File;
import java.lang.reflect.Method;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @version 0.1
 */
public class NeoGraphTest extends TestCase {

    private static final String NEO_TEST_DIRECTORY = "/tmp/neo-tests";
    private static final SuiteConfiguration config = new SuiteConfiguration();

    static {
        config.allowsDuplicateEdges = true;
        config.allowsSelfLoops = false;
        config.requiresRDFIds = false;
        config.isRDFModel = false;
        config.supportsEdgeIteration = true;
        config.supportsVertexIteration = true;
        config.supportsVertexIndex = true;
    }

    public void testVertexSuite() throws Exception {
        doSuiteTest(new VertexTestSuite(config));
    }

    public void testEdgeSuite() throws Exception {
        doSuiteTest(new EdgeTestSuite(config));
    }

    public void testIndexSuite() throws Exception {
        doSuiteTest(new IndexTestSuite(config));
    }

    /*public void testGraphMLReaderSuite() throws Exception {
        doSuiteTest(new GraphMLReaderTestSuite());
    }*/


    private static void doSuiteTest(ModelTestSuite suite) throws Exception {
        for (Method method : suite.getClass().getDeclaredMethods()) {
            deleteGraphDirectory(new File(NEO_TEST_DIRECTORY));
            if (method.getName().startsWith("test")) {
                Graph graph = new NeoGraph(NEO_TEST_DIRECTORY);
                graph.removeVertex(graph.getVertex(0));
                System.out.println("Testing " + method.getName() + "...");
                method.invoke(suite, graph);
                graph.shutdown();
            }
        }
    }

    public void testLongIdConversions() {
        String id1 = "100";  // good  100
        String id2 = "100.0"; // good 100
        String id3 = "100.1"; // good 100
        String id4 = "one"; // bad

        try {
            Double.valueOf(id1).longValue();
            assertTrue(true);
        } catch (NumberFormatException e) {
            assertFalse(true);
        }
        try {
            Double.valueOf(id2).longValue();
            assertTrue(true);
        } catch (NumberFormatException e) {
            assertFalse(true);
        }
        try {
            Double.valueOf(id3).longValue();
            assertTrue(true);
        } catch (NumberFormatException e) {
            assertFalse(true);
        }
        try {
            Double.valueOf(id4).longValue();
            assertTrue(false);
        } catch (NumberFormatException e) {
            assertFalse(false);
        }

    }

    private static void deleteGraphDirectory(File directory) {
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    deleteGraphDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
    }
}
