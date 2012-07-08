/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.webapp.genericcall;

import org.krohm.ose.is.genericcall.internal.AbstractEngineAwareServlet;
import org.krohm.ose.is.genericcall.internal.GenericContextBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class GenericData extends AbstractEngineAwareServlet {

    private final static Logger logger = LoggerFactory.getLogger(GenericData.class);

    public GenericData() {
        this.addBuilder(POST, new GenericDataContextBuilder());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        logger.debug("GET received");
        resp.sendError(resp.SC_INTERNAL_SERVER_ERROR, "Error GET request is not supported.");
    }

    private String bufferedReaderToString(final BufferedReader br)
            throws IOException {
        final int BUFFER_SIZE = 8192;
        char[] cbuf = new char[BUFFER_SIZE];
        StringBuffer sb = new StringBuffer();
        int len = br.read(cbuf, 0, BUFFER_SIZE);
        while (len != -1) {
            sb.append(new String(cbuf, 0, len));
            len = br.read(cbuf, 0, BUFFER_SIZE);
        }
        return sb.toString();
    }

    private class GenericDataContextBuilder extends GenericContextBuilder {

        @Override
        public Map getContext(HttpServletRequest request) throws Exception {
            Map context = super.getContext(request);
            // data
            String data = bufferedReaderToString(request.getReader());
            context.put(dataKey, data);
            return context;
        }

        @Override
        public void populateResponse(Map context, HttpServletRequest request, HttpServletResponse response) throws Exception {
            super.populateResponse(context, request, response);
            String result = (String) context.get(dataKey);

            if (result != null) {
                response.setContentLength(result.length());
                response.getWriter().write(result);
            } else {
                response.setContentLength(0);
            }
        }
    }
    /*
    private class GenericDataContextBuilder implements ContextBuilder {

    public Map getContext(HttpServletRequest request) throws Exception {
    Map context = new HashMap();
    // session
    HttpSession session = request.getSession();
    context.put(sessionKey, session);
    // data
    String data = bufferedReaderToString(request.getReader());
    context.put("data", data);
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
    // copy answer into result
    String result = (String) context.get(dataKey);
    if (result != null) {
    response.setContentLength(result.length());
    response.getWriter().write(result);
    } else {
    response.setContentLength(0);
    }
    }
    }/**/
}
