package com.jasper.litebase.server.handler;

import com.jasper.litebase.server.connection.BackendConnection;
import com.jasper.litebase.server.handler.impl.SelectHandler;
import com.jasper.litebase.sql.parser.enumeration.QueryType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ComQueryHandler {
    private static Map<QueryType, ComQueryHandler> HANDLERS = new ConcurrentHashMap<>();

    static {
        HANDLERS.put(QueryType.SELECT, new SelectHandler());
    }

    public static void query(BackendConnection c, String sql) {
        // 解析
        if(sql.startsWith("SELECT") || sql.startsWith("select")) {
            HANDLERS.get(QueryType.SELECT).handle(c, sql);
        }
    }

    public void handle(BackendConnection c) {
        throw new UnsupportedOperationException();
    }

    public void handle(BackendConnection c, String sql) {
        throw new UnsupportedOperationException();
    }

    protected abstract QueryType operation();
}