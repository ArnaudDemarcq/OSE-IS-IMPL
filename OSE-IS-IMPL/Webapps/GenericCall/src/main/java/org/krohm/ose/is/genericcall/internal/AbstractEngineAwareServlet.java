/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.genericcall.internal;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.krohm.ose.is.api.action.Action;
import org.krohm.ose.is.api.engine.Engine;
import org.krohm.ose.is.api.engine.EngineAware;
import org.krohm.ose.is.util.engineutil.EngineGetter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class AbstractEngineAwareServlet extends HttpServlet implements EngineAware {

    protected Engine oseEngine;
    public final static String DELETE = "DELETE";
    public final static String GET = "GET";
    public final static String HEAD = "HEAD";
    public final static String OPTIONS = "OPTIONS";
    public final static String POST = "POST";
    public final static String PUT = "PUT";
    public final static String TRACE = "TRACE";
    protected ConcurrentHashMap<String, ContextBuilder> builders = new ConcurrentHashMap<String, ContextBuilder>();
    private final static Logger logger = LoggerFactory.getLogger(AbstractEngineAwareServlet.class);

    public void setEngine(Engine oseEngine) {
        this.oseEngine = oseEngine;
    }

    public void addBuilder(String method, ContextBuilder builder) {
        builders.putIfAbsent(method, builder);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ContextBuilder currentBuilder = builders.get(GET);
        if (currentBuilder == null) {
            super.doGet(req, resp);
            return;
        }
        try {
            executeCall(GET, req, resp);
        } catch (Exception ex) {
            logger.error("Unexpected error", ex);
            resp.sendError(resp.SC_BAD_REQUEST, "Unexpected error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ContextBuilder currentBuilder = builders.get(POST);
        if (currentBuilder == null) {
            super.doGet(req, resp);
            return;
        }
        try {
            executeCall(POST, req, resp);
        } catch (Exception ex) {
            logger.error("Unexpected error", ex);
            resp.sendError(resp.SC_BAD_REQUEST, "Unexpected error");
        }
    }

    private final void executeCall(String method, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        oseEngine = EngineGetter.getEngineStatic();
        ContextBuilder currentBuilder = builders.get(method);
        String workflowName = currentBuilder.getWorkflowName(req);
        Map currentContext = currentBuilder.getContext(req);
        Action action = oseEngine.getWorkflowServive().getRegistered(workflowName);
        if (action == null) {
            logger.warn("workflow " + workflowName + " not found");
            resp.sendError(resp.SC_BAD_REQUEST, "workflow " + workflowName + " not found.");
        }
        action.run(currentContext);
        currentBuilder.populateResponse(currentContext, req, resp);
    }
}
