/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.webapp.genericcall;

import org.krohm.ose.is.genericcall.internal.AbstractEngineAwareServlet;
import org.krohm.ose.is.genericcall.internal.GenericContextBuilder;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class GenericCall extends AbstractEngineAwareServlet {

    private final static Logger logger = LoggerFactory.getLogger(GenericCall.class);

    public GenericCall() {
        this.addBuilder(GET, new GenericCallContextBuilder());
        this.addBuilder(POST, new GenericCallContextBuilder());
    }

    private class GenericCallContextBuilder extends GenericContextBuilder {

        @Override
        public Map getContext(HttpServletRequest request) throws Exception {
            Map context = super.getContext(request);
            // parameters
            for (final Object key : request.getParameterMap().keySet()) {
                context.put(key, request.getParameter((String) key));
            }
            return context;
        }

        @Override
        public void populateResponse(Map context, HttpServletRequest request, HttpServletResponse response) throws Exception {
            super.populateResponse(context, request, response);
            // copy answer into result
            String result = (String) context.get(dataKey);
            if (result != null) {
                response.setContentLength(result.length());
                response.getWriter().write(result);
            } else {
                response.setContentLength(0);
            }
        }
    }
}
