package com.tinkerpop.gremlin.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @version 0.1
 */
public class EdgeTestSuite extends ModelTestSuite {

    public EdgeTestSuite() {
    }

    public EdgeTestSuite(SuiteConfiguration config) {
        super(config);
    }

    public void testAddEdges(Graph graph) {
        Vertex v1 = graph.addVertex(convertId("1"));
        Vertex v2 = graph.addVertex(convertId("2"));
        Vertex v3 = graph.addVertex(convertId("3"));
        graph.addEdge(null, v1, v2, convertId("knows"));
        graph.addEdge(null, v2, v3, convertId("pets"));
        graph.addEdge(null, v2, v3, convertId("cares_for"));
        assertEquals(v1.getOutEdges().size(), 1);
        assertEquals(v2.getOutEdges().size(), 2);
        assertEquals(v3.getOutEdges().size(), 0);
        assertEquals(v1.getInEdges().size(), 0);
        assertEquals(v2.getInEdges().size(), 1);
        assertEquals(v3.getInEdges().size(), 2);
    }

    public void testAddManyEdges(Graph graph) {
        long counter = 0l;
        for (int i = 0; i < 1000; i++) {
            Vertex out = graph.addVertex(convertId("" + counter++));
            Vertex in = graph.addVertex(convertId("" + counter++));
            graph.addEdge(null, out, in, convertId(UUID.randomUUID().toString()));
        }
        if (config.supportsEdgeIteration)
            assertEquals(countIterator(graph.getEdges()), 1000);
        if (config.supportsVertexIteration) {
            assertEquals(countIterator(graph.getVertices()), 2000);
            for (Vertex vertex : graph.getVertices()) {
                if (vertex.getOutEdges().size() > 0) {
                    assertEquals(vertex.getOutEdges().size(), 1);
                    assertFalse(vertex.getInEdges().size() > 0);

                } else {
                    assertEquals(vertex.getInEdges().size(), 1);
                    assertFalse(vertex.getOutEdges().size() > 0);
                }
            }
        }
    }

    public void testRemoveManyEdges(Graph graph) {
        long counter = 200000l;
        Set<Edge> edges = new HashSet<Edge>();
        for (int i = 0; i < 1000; i++) {
            Vertex out = graph.addVertex(convertId("" + counter++));
            Vertex in = graph.addVertex(convertId("" + counter++));
            edges.add(graph.addEdge(null, out, in, convertId(UUID.randomUUID().toString())));
        }
        assertEquals(edges.size(), 1000);

        if (config.supportsVertexIteration)
            assertEquals(countIterator(graph.getVertices()), 2000);

        if (config.supportsEdgeIteration) {
            assertEquals(countIterator(graph.getEdges()), 1000);

            int i = 1000;
            for (Edge edge : edges) {
                graph.removeEdge(edge);
                i--;
                assertEquals(countIterator(graph.getEdges()), i);
                if (config.supportsVertexIteration) {
                    int x = 0;
                    for (Vertex vertex : graph.getVertices()) {

                        if (vertex.getOutEdges().size() > 0) {
                            assertEquals(vertex.getOutEdges().size(), 1);
                            assertFalse(vertex.getInEdges().size() > 0);

                        } else if (vertex.getInEdges().size() > 0) {
                            assertEquals(vertex.getInEdges().size(), 1);
                            assertFalse(vertex.getOutEdges().size() > 0);
                        } else {
                            x++;
                        }
                    }
                    assertEquals(x, (1000 - i) * 2);
                }
            }
        }
    }

    public void testAddingDuplicateEdges(Graph graph) {

        Vertex v1 = graph.addVertex(convertId("1"));
        Vertex v2 = graph.addVertex(convertId("2"));
        Vertex v3 = graph.addVertex(convertId("3"));
        graph.addEdge(null, v1, v2, convertId("knows"));
        graph.addEdge(null, v2, v3, convertId("pets"));
        graph.addEdge(null, v2, v3, convertId("pets"));
        graph.addEdge(null, v2, v3, convertId("pets"));
        graph.addEdge(null, v2, v3, convertId("pets"));

        /*Iterator<Edge> itty = graph.getEdges();
        while(itty.hasNext()) {
            System.out.println("edge!" + itty.next());
        }*/

        if (config.allowsDuplicateEdges) {
            if (config.supportsVertexIteration)
                assertEquals(countIterator(graph.getVertices()), 3);
            if (config.supportsEdgeIteration)
                assertEquals(countIterator(graph.getEdges()), 5);

            assertEquals(v1.getInEdges().size(), 0);
            assertEquals(v1.getOutEdges().size(), 1);
            assertEquals(v2.getInEdges().size(), 1);
            assertEquals(v2.getOutEdges().size(), 4);
            assertEquals(v3.getInEdges().size(), 4);
            assertEquals(v3.getOutEdges().size(), 0);
        } else {
            if (config.supportsVertexIteration)
                assertEquals(countIterator(graph.getVertices()), 3);
            if (config.supportsEdgeIteration)
                assertEquals(countIterator(graph.getEdges()), 2);

            assertEquals(v1.getInEdges().size(), 0);
            assertEquals(v1.getOutEdges().size(), 1);
            assertEquals(v2.getInEdges().size(), 1);
            assertEquals(v2.getOutEdges().size(), 1);
            assertEquals(v3.getInEdges().size(), 1);
            assertEquals(v3.getOutEdges().size(), 0);
        }
    }

    public void testRemoveEdgesByRemovingVertex(Graph graph) {
        Vertex v1 = graph.addVertex(convertId("1"));
        Vertex v2 = graph.addVertex(convertId("2"));
        Vertex v3 = graph.addVertex(convertId("3"));
        graph.addEdge(null, v1, v2, convertId("knows"));
        graph.addEdge(null, v2, v3, convertId("pets"));
        graph.addEdge(null, v2, v3, convertId("pets"));

        if (config.supportsVertexIteration)
            assertEquals(countIterator(graph.getVertices()), 3);
        graph.removeVertex(v1);
        if (config.supportsVertexIteration)
            assertEquals(countIterator(graph.getVertices()), 2);
        if (config.allowsDuplicateEdges)
            assertEquals(v2.getOutEdges().size(), 2);
        else
            assertEquals(v2.getOutEdges().size(), 1);

        assertEquals(v3.getOutEdges().size(), 0);

        assertEquals(v2.getInEdges().size(), 0);

        if (config.allowsDuplicateEdges)
            assertEquals(v3.getInEdges().size(), 2);
        else
            assertEquals(v3.getInEdges().size(), 1);

    }

    public void testRemoveEdges(Graph graph) {
        Vertex v1 = graph.addVertex(convertId("1"));
        Vertex v2 = graph.addVertex(convertId("2"));
        Vertex v3 = graph.addVertex(convertId("3"));
        Edge e1 = graph.addEdge(null, v1, v2, convertId("knows"));
        Edge e2 = graph.addEdge(null, v2, v3, convertId("pets"));
        Edge e3 = graph.addEdge(null, v2, v3, convertId("cares_for"));

        if (config.supportsVertexIteration)
            assertEquals(countIterator(graph.getVertices()), 3);

        graph.removeEdge(e1);
        assertEquals(v1.getOutEdges().size(), 0);
        assertEquals(v2.getOutEdges().size(), 2);
        assertEquals(v3.getOutEdges().size(), 0);
        assertEquals(v1.getInEdges().size(), 0);
        assertEquals(v2.getInEdges().size(), 0);
        assertEquals(v3.getInEdges().size(), 2);
        graph.removeEdge(e2);
        assertEquals(v1.getOutEdges().size(), 0);
        assertEquals(v2.getOutEdges().size(), 1);
        assertEquals(v3.getOutEdges().size(), 0);
        assertEquals(v1.getInEdges().size(), 0);
        assertEquals(v2.getInEdges().size(), 0);
        assertEquals(v3.getInEdges().size(), 1);
        graph.removeEdge(e3);
        assertEquals(v1.getOutEdges().size(), 0);
        assertEquals(v2.getOutEdges().size(), 0);
        assertEquals(v3.getOutEdges().size(), 0);
        assertEquals(v1.getInEdges().size(), 0);
        assertEquals(v2.getInEdges().size(), 0);
        assertEquals(v3.getInEdges().size(), 0);

    }

    public void testAddingSelfLoops(Graph graph) {
        if (config.allowsSelfLoops) {
            Vertex v1 = graph.addVertex(convertId("1"));
            Vertex v2 = graph.addVertex(convertId("2"));
            Vertex v3 = graph.addVertex(convertId("3"));
            graph.addEdge(null, v1, v1, convertId("is_self"));
            graph.addEdge(null, v2, v2, convertId("is_self"));
            graph.addEdge(null, v3, v3, convertId("is_self"));

            if (config.supportsVertexIteration)
                assertEquals(countIterator(graph.getVertices()), 3);
            if (config.supportsEdgeIteration) {
                assertEquals(countIterator(graph.getEdges()), 3);
                int counter = 0;
                for (Edge edge : graph.getEdges()) {
                    counter++;
                    assertEquals(edge.getInVertex(), edge.getOutVertex());
                    assertEquals(edge.getInVertex().getId(), edge.getOutVertex().getId());
                }
                assertEquals(counter, 3);
            }

        }
    }

    public void testEdgeIterator(Graph graph) {
        if (config.supportsEdgeIteration) {
            Vertex v1 = graph.addVertex(convertId("1"));
            Vertex v2 = graph.addVertex(convertId("2"));
            Vertex v3 = graph.addVertex(convertId("3"));
            Edge e1 = graph.addEdge(null, v1, v2, convertId("test"));
            Edge e2 = graph.addEdge(null, v2, v3, convertId("test"));
            Edge e3 = graph.addEdge(null, v3, v1, convertId("test"));

            if (config.supportsVertexIteration)
                assertEquals(countIterator(graph.getVertices()), 3);
            if (config.supportsEdgeIteration)
                assertEquals(countIterator(graph.getEdges()), 3);

            Set<String> edgeIds = new HashSet<String>();
            int count = 0;
            for (Edge e : graph.getEdges()) {
                count++;
                edgeIds.add(e.getId().toString());
                assertEquals(e.getLabel(), convertId("test"));
                if (e.getId().toString().equals(e1.getId().toString())) {
                    assertEquals(e.getOutVertex(), v1);
                    assertEquals(e.getInVertex(), v2);
                } else if (e.getId().toString().equals(e2.getId().toString())) {
                    assertEquals(e.getOutVertex(), v2);
                    assertEquals(e.getInVertex(), v3);
                } else if (e.getId().toString().equals(e3.getId().toString())) {
                    assertEquals(e.getOutVertex(), v3);
                    assertEquals(e.getInVertex(), v1);
                } else {
                    assertTrue(false);
                }
                //System.out.println(e);
            }
            assertEquals(count, 3);
            assertEquals(edgeIds.size(), 3);
            assertTrue(edgeIds.contains(e1.getId().toString()));
            assertTrue(edgeIds.contains(e2.getId().toString()));
            assertTrue(edgeIds.contains(e3.getId().toString()));
        }
    }
}
