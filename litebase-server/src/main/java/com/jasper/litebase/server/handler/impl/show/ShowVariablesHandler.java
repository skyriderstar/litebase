package com.jasper.litebase.server.handler.impl.show;

import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlShowVariantsStatement;
import com.jasper.litebase.engine.api.StoreEngine;
import com.jasper.litebase.engine.domain.ExecutionContext;
import com.jasper.litebase.engine.domain.ResultSet;
import com.jasper.litebase.server.connection.BackendConnection;
import com.jasper.litebase.server.engine.EngineManager;
import com.jasper.litebase.server.handler.ComQueryHandler;

public class ShowVariablesHandler extends ComQueryHandler<MySqlShowVariantsStatement> {

    @Override
    protected ResultSet doQuery(BackendConnection c, Long queryId, String sql, MySqlShowVariantsStatement statement) {
        String where = null;
        if (statement.getWhere() != null) {
            where = statement.getWhere().toString();
            // predicate = element -> EvalWhere.match(statement.getWhere().toString().replaceAll("Variable_name", "?"),
            // Collections.singletonList(element.get(0)));
        } else if (statement.getLike() != null) {
            where = "variable_name like " + statement.getLike().toString();
            // predicate = element -> EvalWhere.match("? like " + statement.getLike().toString(),
            // Collections.singletonList(element.get(0)));
            // predicate = element -> {
            // String input = (String) element.get(0);
            // String expr = statement.getLike().toString();
            // expr = expr.toLowerCase(); // ignoring locale for now
            // expr = expr.replace(".", "\\."); // "\\" is escaped to "\" (thanks, Alan M)
            // // ... escape any other potentially problematic characters here
            // expr = expr.replace("?", ".");
            // expr = expr.replace("%", ".*");
            // input = input.toLowerCase();
            // return input.matches(expr);
            // };
        }
        return EngineManager.getInstance(c.getGlobalConfig().getEngineType()).query(
                new ExecutionContext(queryId, c.getSessionConfig()), "performance_schema", "session_variables", where);
    }
}
