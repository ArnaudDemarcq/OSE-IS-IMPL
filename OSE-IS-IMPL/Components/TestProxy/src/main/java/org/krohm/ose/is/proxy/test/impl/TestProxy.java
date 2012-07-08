/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.proxy.test.impl;

import java.util.Map;
import org.krohm.ose.is.api.proxyaction.ProxyAction;
import org.krohm.ose.is.util.proxyaction.ProxyActionUtil;
import org.krohm.ose.is.util.proxyaction.impl.ProxyActionImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class TestProxy extends ProxyActionImpl implements ProxyAction {

    protected final Logger logger = LoggerFactory.getLogger(TestProxy.class);

    @Override
    protected void beforeRun(Map context) {
        logger.error("About to run Action :<" + ProxyActionUtil.getFinalTargetAction(this) + ">");
    }

    @Override
    protected void afterRun(Map context) {
        logger.error("Action run finished :<" + ProxyActionUtil.getFinalTargetAction(this) + ">");
    }
}
