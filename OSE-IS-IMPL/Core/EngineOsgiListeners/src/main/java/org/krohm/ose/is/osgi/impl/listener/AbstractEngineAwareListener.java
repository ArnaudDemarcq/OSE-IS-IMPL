/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.osgi.impl.listener;

import org.krohm.ose.is.api.engine.Engine;
import org.krohm.ose.is.api.engine.EngineAware;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.osgi.context.BundleContextAware;

/**
 *
 * @author arnaud
 */
public abstract class AbstractEngineAwareListener<T> implements BundleContextAware, ApplicationContextAware,
        ServiceListener, InitializingBean, EngineAware {

    protected static final String OSE_OSGI_KEY="oseframework";

    protected ApplicationContext applicationContext;
    protected Engine oseEngine;
    protected BundleContext bundleContext;
    private static final Logger logger = LoggerFactory.getLogger(AbstractEngineAwareListener.class);
    private Class<T> clazz;

    AbstractEngineAwareListener(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void afterPropertiesSet() {
        try {
            bundleContext.addServiceListener(this, getFilter());
            // Retrieve already registered Configurators
            ServiceReference[] existingObjects = bundleContext.getServiceReferences(clazz.getName(), getFilter());
            logger.info(clazz.getName());
            logger.info(getFilter());


            if (existingObjects != null) {
                logger.info("Action to register: " + existingObjects.length);

                for (final ServiceReference sRef : existingObjects) {
                    String serviceName = (String) sRef.getProperty("name");
                    logger.info("Registering action: " + serviceName);
                    T tmpObject = (T) bundleContext.getService(sRef);
                    onRegister(tmpObject, serviceName);
                }
            }

            logger.info("Listener registered");
        } catch (final InvalidSyntaxException e) {
            logger.error("Details", e);
        }
    }

    public void serviceChanged(final ServiceEvent event) {
        if (logger.isDebugEnabled()) {
            logger.debug("New Action service event");
        }

        ServiceReference sRef = event.getServiceReference();
        String serviceName = (String) sRef.getProperty("name");

        if (event.getType() == ServiceEvent.REGISTERED) {
            logger.info("Action Service registered: " + serviceName);
            T tmpObject = (T) bundleContext.getService(sRef);
            onRegister(tmpObject, serviceName);
        } else if (event.getType() == ServiceEvent.UNREGISTERING) {
            logger.info("Action Service unregistered: " + serviceName);
            onUnRegister(serviceName);
        } else if (event.getType() == ServiceEvent.MODIFIED) {
            logger.info("Action Service modified: " + serviceName);
            onUnRegister(serviceName);
            T tmpObject = (T) bundleContext.getService(sRef);
            onRegister(tmpObject, serviceName);
        }
    }

    public void setEngine(Engine oseEngine) {
        this.oseEngine = oseEngine;
    }

    public void setBundleContext(final BundleContext context) {
        logger.debug("Bundle context received");
        this.bundleContext = context;
    }

    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        logger.info("Context received");
        GenericApplicationContext ctx = new GenericApplicationContext(applicationContext);
        this.applicationContext = ctx;
    }

    protected Class<T> getFilteredClass() {
        return clazz;
    }

    protected abstract String getFilter();

    protected abstract void onRegister(T serviceObject, String serviceName);

    protected abstract void onUnRegister(String serviceName);

    protected abstract void onModify(T serviceObject, String serviceName);
}
