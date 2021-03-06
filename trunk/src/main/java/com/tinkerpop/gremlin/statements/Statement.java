package com.tinkerpop.gremlin.statements;

import com.tinkerpop.gremlin.XPathEvaluator;

import java.util.List;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @version 0.1
 */
public abstract class Statement {


    protected XPathEvaluator xPathEvaluator;
    protected boolean complete = false;
    private String rawStatement = new String();

    public Statement(XPathEvaluator xPathEvaluator) {
        this.xPathEvaluator = xPathEvaluator;
    }

    public void compileTokens(String line) {
        line = line.trim();
        this.rawStatement = this.rawStatement + Tokens.SINGLESPACE + line;
    }

    public abstract List evaluate();

    public boolean isComplete() {
        return complete;
    }

    public String toString() {
        return this.rawStatement;
    }

}
