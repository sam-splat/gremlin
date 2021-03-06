package com.tinkerpop.gremlin.functions;

import com.tinkerpop.gremlin.XPathEvaluator;
import junit.framework.TestCase;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @version 0.1
 */
public class TypeFunctionTest extends TestCase {

    public void testTypeFunction() {

        XPathEvaluator xe = new XPathEvaluator();
        assertEquals(xe.evaluate("g:type('marko')").size(), 1);
        assertEquals(xe.evaluate("g:type('marko')").get(0), "string");

        assertEquals(xe.evaluate("g:type(1+2)").size(), 1);
        assertEquals(xe.evaluate("g:type(1+2)").get(0), "number");

        assertEquals(xe.evaluate("g:type(g:append(1,2))").size(), 1);
        assertEquals(xe.evaluate("g:type(g:append(1,2))").get(0), "list");

        assertEquals(xe.evaluate("g:type(g:map())").size(), 1);
        assertEquals(xe.evaluate("g:type(g:map())").get(0), "map");

    }
}
