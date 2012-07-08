/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.genericcall.internal;

import org.krohm.ose.is.genericcall.internal.AbstractEngineAwareServlet;
import org.krohm.ose.is.genericcall.internal.ContextBuilder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class GenericContextBuilder implements ContextBuilder {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected static final String sessionKey = "__session";
    protected static final String headerKey = "__headers";
    protected static final String dataKey = "data";

    public Map getContext(HttpServletRequest request) throws Exception {
        Map context = new HashMap();
        // session
        HttpSession session = request.getSession();
        context.put(sessionKey, session);
        // headers
        Map<String, String> headers = new HashMap<String, String>();
        for (Enumeration<String> e = request.getHeaderNames();
                e.hasMoreElements();) {
            String key = e.nextElement();
            headers.put(key, request.getHeader(key));
        }
        context.put(headerKey, headers);
        return context;
    }

    public String getWorkflowName(HttpServletRequest request) throws Exception {
        String workflowName = request.getPathInfo().substring(1);
        logger.info("Workflow to call: " + workflowName);
        return workflowName;
    }

    public void populateResponse(Map context, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Map<String, String> headers = (Map<String, String>) context.get(headerKey);

        // Copy headers back
        if (headers != null) {
            for (final String key : headers.keySet()) {
                response.setHeader(key, headers.get(key));
            }
        }
    }
}
