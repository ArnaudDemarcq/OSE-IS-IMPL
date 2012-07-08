/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.osgi.impl.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class PackageListener extends AbstractEngineAwareListener<Object> {
    

    private String filter = "(&(main_app="+OSE_OSGI_KEY+")(type=Package))";
    private static final Logger logger = LoggerFactory.getLogger(ConfiguratorListener.class);

    public PackageListener() {
        super(Object.class);
    }

    @Override
    protected String getFilter() {
        return filter;
    }

    @Override
    protected void onRegister(Object serviceObject, String serviceName) {
        try {
            oseEngine.getPackageService().register(serviceObject, serviceName);
        } catch (Exception ex) {
            logger.error("Error getting Root Action for deployment :<" + serviceName + ">");
            logger.debug("Error details", ex);
        }
    }

    @Override
    protected void onUnRegister(String serviceName) {
        oseEngine.getPackageService().unregister(serviceName);
    }

    @Override
    protected void onModify(Object serviceObject, String serviceName) {
        onRegister(serviceObject, serviceName);
        onUnRegister(serviceName);
    }
}
