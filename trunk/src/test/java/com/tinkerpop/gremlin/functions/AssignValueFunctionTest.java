package com.tinkerpop.gremlin.functions;

import com.tinkerpop.gremlin.BaseTest;
import com.tinkerpop.gremlin.XPathEvaluator;
import com.tinkerpop.gremlin.statements.EvaluationException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @version 0.1
 */
public class AssignValueFunctionTest extends TestCase {

    public void testAssignValueFunctionMap() {
        XPathEvaluator xe = new XPathEvaluator();
        Map map = new HashMap();
        map.put("marko", 1.0);
        map.put("jen", 2.0);
        xe.setVariable("$i", map);
        assertEquals(xe.evaluate("$i/@marko").get(0), 1.0);
        assertEquals(xe.evaluate("$i/@jen").get(0), 2.0);
        assertEquals(xe.evaluate("'marko'[g:assign-value($i,(1+2))]").get(0), "marko");
        assertEquals(xe.evaluate("$i/@marko").get(0), 3.0);
        assertEquals(xe.evaluate("$i/@jen").get(0), 2.0);
        assertEquals(xe.evaluate("'peter'[g:assign-value($i, 1000)]").get(0), "peter");
        assertEquals(xe.evaluate("$i/@peter").get(0), 1000.0);

        xe.evaluate("g:append('marko','jen','peter')[g:assign-value($i,2000)]");
        assertEquals(xe.evaluate("$i/@marko").get(0), 2000.0);
        assertEquals(xe.evaluate("$i/@jen").get(0), 2000.0);
        assertEquals(xe.evaluate("$i/@peter").get(0), 2000.0);
        try {
            xe.evaluate("g:map()[g:assign-value($i,3000)]");
            assertFalse(true);
        } catch (EvaluationException e) {
            assertTrue(true);
        }
    }

    public void testAssignValueFunctionList() {
        XPathEvaluator xe = new XPathEvaluator();
        List list = new ArrayList();
        list.add(1.0);
        list.add(2.0);
        xe.setVariable("$i", list);
        assertEquals(xe.evaluate("$i[1]").get(0), 1.0);
        assertEquals(xe.evaluate("$i[2]").get(0), 2.0);
        assertEquals(xe.evaluate("1[g:assign-value($i,(1+2))]").get(0), 1.0);
        assertEquals(xe.evaluate("$i[1]").get(0), 3.0);
        assertEquals(xe.evaluate("$i[2]").get(0), 2.0);
        xe.evaluate("g:append(1,2)[g:assign-value($i,1000)]");
        assertEquals(xe.evaluate("$i[1]").get(0), 1000.0);
        assertEquals(xe.evaluate("$i[2]").get(0), 1000.0);
        try {
            xe.evaluate("g:map()[g:assign-value($i,1+2)]");
            assertFalse(true);
        } catch (EvaluationException e) {
            assertTrue(true);
        }
    }
}
