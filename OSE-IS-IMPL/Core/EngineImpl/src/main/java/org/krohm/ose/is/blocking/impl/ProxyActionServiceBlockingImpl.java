/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.blocking.impl;

import org.krohm.ose.is.api.action.Action;
import org.krohm.ose.is.api.engine.ProxyActionService;
import org.krohm.ose.is.api.proxyaction.ProxyAction;
import org.krohm.ose.is.blocking.impl.registerer.EngineAwareBlockingRegisterer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class ProxyActionServiceBlockingImpl extends EngineAwareBlockingRegisterer<Class<? extends ProxyAction>> implements ProxyActionService {

    private final static Logger logger = LoggerFactory.getLogger(ProxyActionServiceBlockingImpl.class);

    @Override
    public void register(ProxyAction proxyActionPrototype, String identifier) {
        Class<? extends ProxyAction> currentClass = proxyActionPrototype.getClass();
        super.register(currentClass, identifier);
    }

    @Override
    public String register(ProxyAction proxyActionPrototype) {
        Class<? extends ProxyAction> currentClass = proxyActionPrototype.getClass();
        return super.register(currentClass);
    }

    public Action getProxifiedAction(Action unproxifiedAction) throws Exception {
        Action currentAction = unproxifiedAction;
        for (String currentIdentifier : this.getKeys()) {
            logger.info("Applying ProxyAction :<" + currentIdentifier + "> to Action :<" + unproxifiedAction + ">");
            Class<? extends ProxyAction> currentProxyActionClass = super.getRegisteredUnsafe(currentIdentifier);
            if (currentProxyActionClass == null) {
                logger.warn("Attempting to use a non existant proxyAction for identifier :<" + currentIdentifier + ">");
            } else {
                ProxyAction newProxyInstance = currentProxyActionClass.newInstance();
                newProxyInstance.setAction(currentAction);
                currentAction = newProxyInstance;
            }
        }
        return currentAction;
    }
}
