/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.blocking.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.krohm.ose.is.api.engine.Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 *
 * @author arnaud
 */
public class EngineBlockingImpl implements Engine, ApplicationContextAware {

    // Logger
    private static final Logger logger = LoggerFactory.getLogger(EngineBlockingImpl.class);
    // Services
    private final WorkflowServiceBlockingImpl localWorkflowServiceBlockingImpl = new WorkflowServiceBlockingImpl();
    private final ComponentServiceBlockingImpl localComponentServiceBlockingImpl = new ComponentServiceBlockingImpl();
    private final ConfiguratorServiceBlockingImpl localConfiguratorServiceBlockingImpl = new ConfiguratorServiceBlockingImpl();
    private final ActionServiceBlockingImpl localActionServiceBlockingImpl = new ActionServiceBlockingImpl();
    private final ProxyActionServiceBlockingImpl localProxyActionServiceBlockingImpl = new ProxyActionServiceBlockingImpl();
    private final PackageServiceBlockingImpl localPackageServiceBlockingImpl = new PackageServiceBlockingImpl();
    // EngineAwareChildren
    private final List<BlockingEngineAware> engineAwareChildren =
            new ArrayList<BlockingEngineAware>(Arrays.asList(
            localWorkflowServiceBlockingImpl,
            localComponentServiceBlockingImpl,
            localConfiguratorServiceBlockingImpl,
            localActionServiceBlockingImpl,
            localProxyActionServiceBlockingImpl,
            localPackageServiceBlockingImpl));
    // Spring Aware Children (for Action Factory to be able to produce JMX compatible actions)
    // TODO : find a way to do it without beeing Spring dependant
    // in order to remove any spring dependency from the Engine
    private final List<ApplicationContextAware> applicationContextAwareChildren =
            new ArrayList<ApplicationContextAware>(Arrays.asList(
            localComponentServiceBlockingImpl));

    public EngineBlockingImpl() {
        logger.info("Initilizing Engine (Blocking based implementation");
        for (BlockingEngineAware child : engineAwareChildren) {
            child.setEngine(this);
        }
        logger.info("Initilization done for Engine (Blocking based implementation");
    }

    public WorkflowServiceBlockingImpl getWorkflowServive() {
        return localWorkflowServiceBlockingImpl;
    }

    public ComponentServiceBlockingImpl getComponentService() {
        return localComponentServiceBlockingImpl;
    }

    public ConfiguratorServiceBlockingImpl getConfiguratorService() {
        return localConfiguratorServiceBlockingImpl;
    }

    public ActionServiceBlockingImpl getActionService() {
        return localActionServiceBlockingImpl;
    }

    public ProxyActionServiceBlockingImpl getProxyActionService() {
        return localProxyActionServiceBlockingImpl;
    }

    public PackageServiceBlockingImpl getPackageService() {
        return localPackageServiceBlockingImpl;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logger.info("SET SPRING CONTEXT");
        for (ApplicationContextAware child : applicationContextAwareChildren) {
            child.setApplicationContext(applicationContext);
        }
    }
}
