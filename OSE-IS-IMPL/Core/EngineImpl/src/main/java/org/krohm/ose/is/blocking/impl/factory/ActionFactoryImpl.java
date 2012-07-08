/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.blocking.impl.factory;

import org.krohm.ose.is.blocking.impl.BlockingEngineAware;
import org.krohm.ose.is.blocking.impl.EngineBlockingImpl;
import java.util.concurrent.ConcurrentHashMap;
import org.krohm.ose.is.api.action.Action;
import org.krohm.ose.is.api.configurator.Configurator;
import org.krohm.ose.is.api.engine.EngineAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;

/**
 *
 * @author arnaud
 */
public class ActionFactoryImpl implements ActionFactory, BlockingEngineAware, ApplicationContextAware {

    private EngineBlockingImpl oseEngine;
    private GenericApplicationContext applicationContext;
    private static final Logger logger = LoggerFactory.getLogger(ActionFactoryImpl.class);
    private Class<? extends Action> actionClass;
    private final ConcurrentHashMap<Object, Action> actions = new ConcurrentHashMap<Object, Action>();
    private final ConcurrentHashMap<Object, Action> actionsRegistered = new ConcurrentHashMap<Object, Action>();

    public ActionFactoryImpl(Class<? extends Action> actionClass) {
        this.actionClass = actionClass;
    }

    public void setEngine(EngineBlockingImpl oseEngine) {
        this.oseEngine = oseEngine;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logger.info("Context received");
        this.applicationContext = new GenericApplicationContext(applicationContext);
        RootBeanDefinition rbd = new RootBeanDefinition(actionClass);
        rbd.setScope("prototype");
        this.applicationContext.registerBeanDefinition(actionClass.getName(), rbd);
        this.applicationContext.refresh();
    }

    public Action getUnsafeAction(Object configObject) {
        return actions.get(configObject);
    }

    // The only complexity of the whole engine as some synchro is needed to ensure
    // init is done only once
    public Action getAction(Object configObject) throws Exception {
        logger.debug("####A0");
        Action newAction = actions.get(configObject);
        if (newAction == null) {
            newAction = createBeanAction();
            synchronized (configObject) {
                Action last = actionsRegistered.putIfAbsent(configObject, newAction);
                if (last == null) {
                    logger.debug("new action: " + configObject + "[" + configObject.hashCode() + "]");
                    if (newAction instanceof EngineAware) {
                        logger.debug("Action is EngineAware, injecting Engine");
                        if (oseEngine == null) {
                            logger.warn("Injecting null Engine !");
                        }
                        EngineAware engineAwareAction = (EngineAware) newAction;
                        engineAwareAction.setEngine(oseEngine);
                    }
                    logger.debug("####0");
                    Configurator bestConfigurator = oseEngine.getConfiguratorService().findBestConfiguratorConfig(configObject);
                    logger.debug("####0.1" + bestConfigurator);
                    bestConfigurator.configureAction(newAction, configObject);
                    logger.debug("####1");
                    // proxification !
                    Action proxifiedAction = oseEngine.getProxyActionService().getProxifiedAction(newAction);
                    // init
                    logger.debug("####2");
                    proxifiedAction.init();
                    actions.put(configObject, proxifiedAction);
                    logger.debug("####3");
                    newAction = proxifiedAction;
                    logger.debug("####4");
                } else {
                    // todo : unregister
                    logger.debug("reuse action: " + actionClass.getName() + "[" + configObject.hashCode() + "]");
                    newAction = last;
                }
            }
        }
        newAction = actions.get(configObject);
        return newAction;
    }

    public void clear() {
        // TODO : also make shutdowns
        for (Object currentKey : actions.keySet()) {
            try {
                destroyAction(currentKey);
            } catch (Exception E) {
                logger.error("Cannot shutdow action : <" + currentKey + ">");
            }
        }
        // WAS :
        // actionsRegistered.clear();
        // actions.clear();
    }

    public void destroyAction(Object configObject) throws Exception {
        Action toDestroyAction = actions.get(configObject);
        if (toDestroyAction == null) {
            logger.warn("Attempting to destroy unregisterd Action");
            return;
        }
        logger.info("Unregistering Action");
        actionsRegistered.remove(configObject);
        actions.remove(configObject);
        // a bit violent, but not very costy as actions are cached by their Action Factories
        oseEngine.getActionService().clearCache();
        logger.info("Shutting Down Action");
        // Warning : we do not know if the instance is running : this might cause issues
        // TODO : find a way to make this clearer
        toDestroyAction.shutdown();
        logger.info("Action destroy done");
    }
    /*
     * private methods
     */

    private Action createBeanAction() {
        Action newAction = null;
        String className = actionClass.getName();
        /**/
        newAction = (Action) applicationContext.getBean(className);
        logger.info("Registering MBean for class : <" + className + ">");
        return newAction;
    }

    public Class<? extends Action> getActionClass() {
        return actionClass;
    }
}
