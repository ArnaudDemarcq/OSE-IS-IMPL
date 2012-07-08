/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.blocking.impl;

import org.krohm.ose.is.api.action.Action;
import org.krohm.ose.is.api.engine.PackageService;
import org.krohm.ose.is.blocking.impl.registerer.EngineAwareConcurrentRegisterer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class PackageServiceBlockingImpl extends EngineAwareConcurrentRegisterer<Object> implements PackageService {

    private final static Logger logger = LoggerFactory.getLogger(PackageServiceBlockingImpl.class);

    @Override
    public void register(Object registrableObject, String identifier) {
        super.register(registrableObject, identifier);
        try {
            Action packageRootAction = this.getEngine().getActionService().getAction(registrableObject);
        } catch (Exception ex) {
            logger.error("Error while resistering PackageObject");
            logger.debug("Error while resistering PackageObject", ex);
        }
    }

    @Override
    public void unregister(String identifier) {
        try {
            this.getEngine().getActionService().unregisterConfig(super.getRegistered(identifier));
        } catch (Exception ex) {
            logger.error("Error while unresistering PackageObject");
            logger.debug("Error while unresistering PackageObject", ex);
        }
        super.unregister(identifier);
    }
}
