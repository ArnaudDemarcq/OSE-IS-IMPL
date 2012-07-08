/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.osgi.impl.listener;

import org.krohm.ose.is.api.configurator.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class ConfiguratorListener extends AbstractEngineAwareListener<Configurator> {

    private String filter = "(&(main_app="+OSE_OSGI_KEY+")(type=ComponentConfigurator))";
    private static final Logger logger = LoggerFactory.getLogger(ConfiguratorListener.class);

    public ConfiguratorListener() {
        super(Configurator.class);
    }

    @Override
    protected String getFilter() {
        return filter;
    }

    @Override
    protected void onRegister(Configurator serviceObject, String serviceName) {
        logger.info("Configurator :<" + serviceName + "> has been registered");
        oseEngine.getConfiguratorService().register(serviceObject, serviceName);
    }

    @Override
    protected void onUnRegister(String serviceName) {
        logger.info("Configurator :<" + serviceName + "> has been unregistered");
        oseEngine.getConfiguratorService().unregister(serviceName);
    }

    @Override
    protected void onModify(Configurator serviceObject, String serviceName) {
        logger.info("Configurator :<" + serviceName + "> has been modified");
        onRegister(serviceObject, serviceName);
        onUnRegister(serviceName);
    }
}
