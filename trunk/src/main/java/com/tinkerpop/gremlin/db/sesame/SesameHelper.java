package com.tinkerpop.gremlin.db.sesame;

import com.tinkerpop.gremlin.statements.EvaluationException;
import org.openrdf.model.Statement;
import org.openrdf.model.Resource;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.ContextStatementImpl;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.sail.SailConnection;
import org.openrdf.sail.SailException;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 * @version 0.1
 */
public class SesameHelper {
    protected static void removeStatement(Statement statement, SailConnection sailConnection) {
        try {
            if (null != statement.getContext()) {
                sailConnection.removeStatements(statement.getSubject(), statement.getPredicate(), statement.getObject(), statement.getContext());
            } else {
                sailConnection.removeStatements(statement.getSubject(), statement.getPredicate(), statement.getObject());
            }
        }
        catch (SailException e) {
            throw new EvaluationException(e.getMessage());
        }
    }

    protected static void addStatement(Statement statement, SailConnection sailConnection) {
        try {
            if (null != statement.getContext()) {
                sailConnection.addStatement(statement.getSubject(), statement.getPredicate(), statement.getObject(), statement.getContext());
            } else {
                sailConnection.addStatement(statement.getSubject(), statement.getPredicate(), statement.getObject());
            }
        }
        catch (SailException e) {
            throw new EvaluationException(e.getMessage());
        }
    }

    protected static void addStatement(Resource subject, URI predicate, Value object, Resource context, SailConnection sailConnection) {
        Statement statement;
        if(null != context) {
           statement = new ContextStatementImpl(subject, predicate, object, context);
        } else {
            statement = new StatementImpl(subject, predicate, object);
        }
        SesameHelper.addStatement(statement, sailConnection);
    }
}
