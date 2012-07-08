/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.blocking.impl;

import org.krohm.ose.is.api.action.Action;
import org.krohm.ose.is.api.engine.ComponentService;
import org.krohm.ose.is.blocking.impl.factory.ActionFactory;
import org.krohm.ose.is.blocking.impl.factory.ActionFactoryImpl;
import org.krohm.ose.is.blocking.impl.registerer.EngineAwareBlockingRegisterer;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author arnaud
 */
public class ComponentServiceBlockingImpl implements ComponentService, BlockingEngineAware, ApplicationContextAware {

    private EngineBlockingImpl oseEngine;
    private ApplicationContext applicationContext;
    private final EngineAwareBlockingRegisterer<ActionFactoryImpl> factories = new EngineAwareBlockingRegisterer<ActionFactoryImpl>();

    public ComponentServiceBlockingImpl() {
    }

    public void register(Action actionInstance, String identifier) {
        // todo : manage null action ?
        register(actionInstance.getClass(), identifier);
    }

    public String register(Action actionInstance) {
        // todo : manage null action ?
        return register(actionInstance.getClass());
    }

    public String register(Class<? extends Action> registrableObject) {
        ActionFactoryImpl actionFactory = new ActionFactoryImpl(registrableObject);
        actionFactory.setEngine(oseEngine);
        actionFactory.setApplicationContext(applicationContext);
        return factories.register(actionFactory);
    }

    public void register(Class<? extends Action> registrableObject, String identifier) {
        ActionFactoryImpl actionFactory = new ActionFactoryImpl(registrableObject);
        actionFactory.setEngine(oseEngine);
        actionFactory.setApplicationContext(applicationContext);
        factories.register(actionFactory, identifier);
    }

    public void unregister(String identifier) {
        factories.unregister(identifier);
    }

    public Class<? extends Action> getRegistered(String identifier) throws InterruptedException {
        return factories.getRegistered(identifier).getActionClass();
    }

    public Class<? extends Action> getRegistered(String identifier, long timeout) throws InterruptedException {
        return factories.getRegistered(identifier, timeout).getActionClass();
    }

    public Class<? extends Action> getRegisteredUnsafe(String identifier) {
        ActionFactoryImpl tmpActionFactoryImpl = factories.getRegisteredUnsafe(identifier);
        if (tmpActionFactoryImpl == null) {
            return null;
        }
        return tmpActionFactoryImpl.getActionClass();
    }

    public void setApplicationContext(ApplicationContext ac) throws BeansException {
        this.applicationContext = ac;
    }

    public void setEngine(EngineBlockingImpl oseEngine) {
        this.oseEngine = oseEngine;
        factories.setEngine(oseEngine);
    }

    public ActionFactory getRegisteredFactory(String identifier) throws InterruptedException {
        return factories.getRegistered(identifier);
    }

    public ActionFactory getRegisteredFactory(String identifier, long timeout) throws InterruptedException {
        return factories.getRegistered(identifier, timeout);
    }

    public ActionFactory getRegisteredFactoryUnsafe(String identifier) {
        return factories.getRegisteredUnsafe(identifier);
    }
}
