package com.tinkerpop.gremlin.db.sesame;

import com.tinkerpop.gremlin.model.Edge;
import com.tinkerpop.gremlin.model.Vertex;
import com.tinkerpop.gremlin.statements.EvaluationException;
import org.openrdf.model.Literal;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @version 0.1
 */
public class SesameEdge implements Edge {

    protected Statement statement;
    protected SailConnection sailConnection;

    private static final String NAMED_GRAPH_PROPERTY = "RDF graph edges can only have named graph (ng) properties";

    protected final static String NAMED_GRAPH = "ng";
    protected static Set<String> keys = new HashSet<String>();

    static {
        keys.add(NAMED_GRAPH);
    }

    public SesameEdge(Statement statement, SailConnection sailConnection) {
        this.statement = statement;
        this.sailConnection = sailConnection;
    }

    public String getLabel() {
        return this.statement.getPredicate().stringValue();
    }

    public Set<String> getPropertyKeys() {
        return keys;
    }

    public Object getProperty(String key) {
        if (key.equals(NAMED_GRAPH))
            return this.statement.getContext().stringValue();
        else
            return null;
    }

    public void setProperty(String key, Object value) {
        if (key.equals(NAMED_GRAPH)) {
            try {
                URI namedGraph = new URIImpl(SesameGraph.prefixToNamespace(value.toString(), this.sailConnection));
                SesameHelper.removeStatement(this.statement, this.sailConnection);
                this.statement = new ContextStatementImpl(this.statement.getSubject(), this.statement.getPredicate(), this.statement.getObject(), namedGraph);
                SesameHelper.addStatement(this.statement, this.sailConnection);
                this.sailConnection.commit();
            } catch (SailException e) {
                throw new EvaluationException(e.getMessage());
            }
        } else {
            throw new EvaluationException(NAMED_GRAPH_PROPERTY);
        }
    }

    public Object removeProperty(String key) {
        if (key.equals(NAMED_GRAPH)) {
            try {
                Resource ng = this.statement.getContext();
                SesameHelper.removeStatement(this.statement, this.sailConnection);
                this.statement = new StatementImpl(this.statement.getSubject(), this.statement.getPredicate(), this.statement.getObject());
                SesameHelper.addStatement(this.statement, this.sailConnection);
                this.sailConnection.commit();
                return ng;
            } catch (SailException e) {
                throw new EvaluationException(e.getMessage());
            }
        } else {
            throw new EvaluationException(NAMED_GRAPH_PROPERTY);
        }
    }

    public Vertex getInVertex() {
        return new SesameVertex(this.statement.getObject(), this.sailConnection);
    }

    public Vertex getOutVertex() {
        return new SesameVertex(this.statement.getSubject(), this.sailConnection);
    }

    public List<Vertex> getBothVertices() {
        List<Vertex> bothVertices = new ArrayList<Vertex>();
        bothVertices.add(this.getOutVertex());
        bothVertices.add(this.getInVertex());
        return bothVertices;
    }

    public Statement getRawStatement() {
        return this.statement;
    }

    public String toString() {
        String outVertex = SesameGraph.namespaceToPrefix(this.statement.getSubject().stringValue(), this.sailConnection);
        String edgeLabel = SesameGraph.namespaceToPrefix(this.statement.getPredicate().stringValue(), this.sailConnection);
        String inVertex;
        if (this.statement.getObject() instanceof Resource)
            inVertex = SesameGraph.namespaceToPrefix(this.statement.getObject().stringValue(), this.sailConnection);
        else
            inVertex = literalString((Literal) this.statement.getObject());

        String namedGraph = null;
        if (null != this.statement.getContext()) {
            namedGraph = SesameGraph.namespaceToPrefix(this.statement.getContext().stringValue(), this.sailConnection);
        }

        String edgeString = "e[" + outVertex + "-" + edgeLabel + "->" + inVertex + "]";
        if (null != namedGraph) {
            edgeString = edgeString + "<" + namedGraph + ">";
        }

        return edgeString;
    }

    private String literalString(Literal literal) {
        String language = literal.getLanguage();
        URI datatype = literal.getDatatype();
        if (null != datatype) {
            return "\"" + literal.getLabel() + "\"^^<" + SesameGraph.namespaceToPrefix(datatype.stringValue(), this.sailConnection) + ">";
        } else if (null != language) {
            return "\"" + literal.getLabel() + "\"@" + language;
        } else {
            return "\"" + literal.getLabel() + "\"";
        }
    }

    public int hashCode() {
        return this.statement.hashCode();
    }

    public boolean equals(Object object) {
        return object instanceof SesameEdge && object.hashCode() == this.hashCode();
    }

    public Object getId() {
        //return this.statement.hashCode();
        if(null != this.statement.getContext())
            return "(" + this.statement.getSubject() + ", " + this.statement.getPredicate() + ", " + this.statement.getObject() +") [" + this.statement.getContext() + "]";
        else
            return "(" + this.statement.getSubject() + ", " + this.statement.getPredicate() + ", " + this.statement.getObject() +")";
    }
}
