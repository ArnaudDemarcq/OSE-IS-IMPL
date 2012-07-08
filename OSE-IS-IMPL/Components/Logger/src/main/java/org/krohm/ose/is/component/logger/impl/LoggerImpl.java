/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.component.logger.impl;

import java.util.Map;
import org.krohm.ose.is.api.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author arnaud
 */
public class LoggerImpl implements Action {

    private static final Logger logger = LoggerFactory.getLogger(LoggerImpl.class);
    //
    // Bean Members
    //
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    //
    // Real Methods
    //
    public void init() throws Exception {

    }

    public void run(Map context) throws Exception {
        logger.error(message);
        logger.error("" + context.get("data"));
        context.put("data", "this is an answer from within H");
    }

    public void shutdown() throws Exception {
    }
}
