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
public class GenericCallBin extends AbstractEngineAwareServlet {

    private final static Logger logger = LoggerFactory.getLogger(GenericCall.class);

    public GenericCallBin() {
        this.addBuilder(GET, new GenericCallBinContextBuilder());
        this.addBuilder(POST, new GenericCallBinContextBuilder());
    }

    private class GenericCallBinContextBuilder extends GenericContextBuilder {

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
            byte[] result = (byte[]) context.get("data");
            if (result != null) {
                response.setContentLength(result.length);
                response.getOutputStream().write(result);
            } else {
                response.setContentLength(0);
            }
        }
    }
}
