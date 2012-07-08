/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.genericcall.internal;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author arnaud
 */
public interface ContextBuilder {

    public Map getContext(HttpServletRequest request) throws Exception;

    public String getWorkflowName(HttpServletRequest request) throws Exception;

    public void populateResponse(Map Context, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
