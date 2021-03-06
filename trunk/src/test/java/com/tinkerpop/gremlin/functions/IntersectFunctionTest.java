package com.tinkerpop.gremlin.functions;

import com.tinkerpop.gremlin.BaseTest;
import com.tinkerpop.gremlin.XPathEvaluator;
import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @version 0.1
 */
public class IntersectFunctionTest extends TestCase {

    public void testIntersectFunction() {
        XPathEvaluator xe = new XPathEvaluator();
        assertEquals(xe.evaluate("g:intersect(g:append(1,2,3,4),3)").size(), 1);
        assertTrue(xe.evaluate("g:intersect(g:append(1,2,3,4),3)").contains(3.0));
        assertEquals(xe.evaluate("g:intersect(g:append(1,2,3,4),g:append(1,2))").size(), 2);
        assertTrue(xe.evaluate("g:intersect(g:append(1,2,3,4),g:append(1,2))").contains(1.0));
        assertTrue(xe.evaluate("g:intersect(g:append(1,2,3,4),g:append(1,2))").contains(2.0));
        assertEquals(xe.evaluate("g:intersect(g:append(1,2,3,4),g:append(1,2,3),g:append(1,2))").size(), 2);
        assertTrue(xe.evaluate("g:intersect(g:append(1,2,3,4),g:append(1,2,3),g:append(1,2))").contains(1.0));
        assertTrue(xe.evaluate("g:intersect(g:append(1,2,3,4),g:append(1,2,3),g:append(1,2))").contains(2.0));

        assertEquals(xe.evaluate("g:intersect(1,1)").size(), 1);
        assertEquals(xe.evaluate("g:intersect(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1)").size(), 1);
        assertEquals(xe.evaluate("g:intersect(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0)").size(), 0);
        assertEquals(xe.evaluate("g:intersect(1,2)").size(), 0);
        assertEquals(xe.evaluate("g:intersect(1,2,3,4,5,6,7,8,9)").size(), 0);

        assertEquals(xe.evaluate("g:intersect('marko','marko','marko')").size(), 1);
        assertEquals(xe.evaluate("g:intersect('marko','marko','josh')").size(), 0);
        assertEquals(xe.evaluate("g:intersect('marko','marko',42)").size(), 0);
    }
}
