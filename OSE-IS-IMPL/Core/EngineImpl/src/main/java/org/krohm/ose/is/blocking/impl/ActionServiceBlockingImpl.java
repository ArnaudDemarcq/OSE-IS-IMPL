/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.blocking.impl;

import org.krohm.ose.is.blocking.impl.factory.ActionFactory;
import java.util.concurrent.ConcurrentHashMap;
import org.krohm.ose.is.api.action.Action;
import org.krohm.ose.is.api.engine.ActionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionServiceBlockingImpl implements ActionService, BlockingEngineAware {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // This cache is just here for performances. It drives nothing (and is reseted by all other caches)
    private ConcurrentHashMap<Object, ActionDescriptor> actionCache = new ConcurrentHashMap<Object, ActionDescriptor>();
    private static final long refreshRate = 2000;
    private EngineBlockingImpl oseEngine;

    @Override
    public void setEngine(EngineBlockingImpl oseEngine) {
        this.oseEngine = oseEngine;
    }

    public Action getAction(final Object keyObject) throws Exception {
        ActionDescriptor descriptor = actionCache.get(keyObject);
        if (descriptor == null || descriptor.getActionInstance() == null) {
            // Getting the serviceName
            String serviceName = oseEngine.getConfiguratorService().getServiceName(keyObject);
            if (serviceName == null) {
                logger.warn("Waiting for a configurator able to manage config Object :<" + keyObject + ">.");
                while (serviceName == null) {
                    serviceName = oseEngine.getConfiguratorService().getServiceName(keyObject);
                    if (serviceName == null) {
                        Thread.sleep(refreshRate);
                    } else {
                        logger.warn("Configurator found for config Object :<" + keyObject + ">.");
                    }
                }
            }
            logger.info("The Targeted service is :<" + serviceName + ">");
            logger.debug("####ASB0");
            ActionFactory serviceActionFactory = oseEngine.getComponentService().getRegisteredFactory(serviceName);
            logger.debug("####ASB1");
            Action tmpAction = serviceActionFactory.getAction(keyObject);
            logger.debug("####ASB2");
            ActionDescriptor newActionDescriptor = new ActionDescriptor(keyObject, serviceName, tmpAction);
            logger.debug("####ASB3");
            actionCache.put(keyObject, newActionDescriptor);
            logger.debug("####ASB4");
        }
        return actionCache.get(keyObject).getActionInstance();
    }

    public Action getAction(final Object keyObject, long timeout) throws Exception {
        logger.warn("Timeout Not supported yet");
        return getAction(keyObject);
    }

    public Object getKeyObject(Action actionInstance) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void unregisterConfig(Object keyObject) {
        ActionDescriptor descriptor = actionCache.get(keyObject);
        if (descriptor == null) {
            return;
        }
        actionCache.remove(keyObject);
    }

    public void clearCache() {
        actionCache.clear();
    }

    public void unregisterAll() {
        for (Object currentKey : actionCache.keySet()) {
            unregisterConfig(currentKey);
        }
    }

    private class ActionDescriptor {

        private Object objectKey;
        private String serviceName;
        private Action actionInstance;

        public ActionDescriptor(Object objectKey, String serviceName, Action actionInstance) {
            this.objectKey = objectKey;
            this.serviceName = serviceName;
            this.actionInstance = actionInstance;
        }

        public Action getActionInstance() {
            return actionInstance;
        }

        public void setActionInstance(Action actionInstance) {
            this.actionInstance = actionInstance;
        }

        public Object getObjectKey() {
            return objectKey;
        }

        public void setObjectKey(Object objectKey) {
            this.objectKey = objectKey;
        }

        public String getServiceName() {
            return serviceName;
        }

        public void setServiceName(String serviceName) {
            this.serviceName = serviceName;
        }
    }
}
