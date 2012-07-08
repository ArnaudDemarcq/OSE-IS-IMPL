/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.osgi.impl.listener;

import org.krohm.ose.is.api.proxyaction.ProxyAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class ProxyActionListener extends AbstractEngineAwareListener<ProxyAction> {
    

    private String filter = "(&(main_app="+OSE_OSGI_KEY+")(type=ProxyActionPrototype))";
    private static final Logger logger = LoggerFactory.getLogger(ConfiguratorListener.class);

    public ProxyActionListener() {
        super(ProxyAction.class);
    }

    @Override
    protected String getFilter() {
        return filter;
    }

    @Override
    protected void onRegister(ProxyAction serviceObject, String serviceName) {
        logger.info("ProxyAction :<" + serviceName + "> has been registered");
        oseEngine.getProxyActionService().register(serviceObject, serviceName);
    }

    @Override
    protected void onUnRegister(String serviceName) {
        oseEngine.getProxyActionService().unregister(serviceName);
        logger.info("ProxyAction :<" + serviceName + "> has been unregistered");

    }

    @Override
    protected void onModify(ProxyAction serviceObject, String serviceName) {
        logger.info("ProxyAction :<" + serviceName + "> has been modified");
        onRegister(serviceObject, serviceName);
        onUnRegister(serviceName);
    }
}
