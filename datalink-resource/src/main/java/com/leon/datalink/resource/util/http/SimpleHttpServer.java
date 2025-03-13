package com.leon.datalink.resource.util.http;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.GlobalThreadPool;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.server.HttpServerRequest;
import cn.hutool.http.server.HttpServerResponse;
import cn.hutool.http.server.action.Action;
import cn.hutool.http.server.action.RootAction;
import cn.hutool.http.server.filter.HttpFilter;
import cn.hutool.http.server.filter.SimpleFilter;
import cn.hutool.http.server.handler.ActionHandler;
import com.sun.net.httpserver.*;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class SimpleHttpServer {
    private final HttpServer server;
    private final List<Filter> filters;

    public SimpleHttpServer(int port) {
        this(new InetSocketAddress(port));
    }

    public SimpleHttpServer(String hostname, int port) {
        this(new InetSocketAddress(hostname, port));
    }

    public SimpleHttpServer(InetSocketAddress address) {
        this(address, (HttpsConfigurator)null);
    }

    public SimpleHttpServer(InetSocketAddress address, HttpsConfigurator configurator) {
        try {
            if (null != configurator) {
                HttpsServer server = HttpsServer.create(address, 0);
                server.setHttpsConfigurator(configurator);
                this.server = server;
            } else {
                this.server = HttpServer.create(address, 0);
            }
        } catch (IOException var4) {
            throw new IORuntimeException(var4);
        }

        this.setExecutor(GlobalThreadPool.getExecutor());
        this.filters = new ArrayList();
    }

    public SimpleHttpServer addFilter(Filter filter) {
        this.filters.add(filter);
        return this;
    }

    public SimpleHttpServer addFilter(final HttpFilter filter) {
        return this.addFilter((Filter)(new SimpleFilter() {
            public void doFilter(HttpExchange httpExchange, Chain chain) throws IOException {
                filter.doFilter(new HttpServerRequest(httpExchange), new HttpServerResponse(httpExchange), chain);
            }
        }));
    }

    public SimpleHttpServer addHandler(String path, HttpHandler handler) {
        this.createContext(path, handler);
        return this;
    }

    public HttpContext createContext(String path, HttpHandler handler) {
        path = StrUtil.addPrefixIfNot(path, "/");
        HttpContext context = this.server.createContext(path, handler);
        context.getFilters().addAll(this.filters);
        return context;
    }

    public SimpleHttpServer setRoot(String root) {
        return this.setRoot(new File(root));
    }

    public SimpleHttpServer setRoot(File root) {
        return this.addAction("/", new RootAction(root));
    }

    public SimpleHttpServer addAction(String path, Action action) {
        return this.addHandler(path, new ActionHandler(action));
    }

    public SimpleHttpServer setExecutor(Executor executor) {
        this.server.setExecutor(executor);
        return this;
    }

    public HttpServer getRawServer() {
        return this.server;
    }

    public InetSocketAddress getAddress() {
        return this.server.getAddress();
    }

    public void start() {
        this.server.start();
    }

    public void stop(int delay) {
        this.server.stop(delay);
    }
}
