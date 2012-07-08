/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.blocking.impl;

import org.krohm.ose.is.api.action.Action;
import org.krohm.ose.is.api.configurator.Configurator;
import org.krohm.ose.is.api.engine.ConfiguratorService;
import org.krohm.ose.is.blocking.impl.registerer.EngineAwareConcurrentRegisterer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class ConfiguratorServiceBlockingImpl extends EngineAwareConcurrentRegisterer<Configurator> implements ConfiguratorService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //
    // Overiden methods
    //
    @Override
    public void register(Configurator registrableObject, String identifier) {
        super.register(registrableObject, identifier);
        this.getEngine().getActionService().unregisterAll();
    }

    @Override
    public void unregister(String identifier) {
        super.unregister(identifier);
        this.getEngine().getActionService().unregisterAll();
    }

    //
    // Specific Methods
    //
    public String getServiceName(Object configObject) {
        // TODO : caching
        Configurator bestConfigurator = findBestConfiguratorConfig(configObject);
        if (bestConfigurator == null) {
            logger.warn("No Confiugurator able to manage object :<" + configObject + ">");
            return null;
        }
        return bestConfigurator.getServiceName(configObject);
    }

    public Configurator findBestConfiguratorAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Configurator findBestConfiguratorConfig(Object configObject) {
        Configurator bestConfigurator = null;
        int bestConfiguratorEval = Configurator.NOT_ELIGIBLE;
        //logger.error("" + super.getRegisterationMap());
        for (String currentConfiguratorKey : super.getRegisterationMap().keySet()) {
            //logger.error("=====>" + currentConfiguratorKey);
            //logger.error("=====>" + super.getRegisterationMap().get(currentConfiguratorKey));
            try {
                Configurator currentConfigurator = super.getRegisterationMap().get(currentConfiguratorKey);
                int currentConfiguratorEval = currentConfigurator.isEligibleConfig(configObject);
                if (currentConfiguratorEval > bestConfiguratorEval) {
                    bestConfiguratorEval = currentConfiguratorEval;
                    bestConfigurator = currentConfigurator;
                }
            } catch (Exception e) {
                logger.debug("Exception occured during evaluation of Confiugrators");
            }
        }
        logger.debug("Best Configurator is :<" + bestConfigurator
                + "> with Evaluation : <" + bestConfiguratorEval + ">");
        return bestConfigurator;
    }

    public Action configureAction(Action action, Object configObject) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int isEligibleAction(Action action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int isEligibleConfig(Object configObject) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
