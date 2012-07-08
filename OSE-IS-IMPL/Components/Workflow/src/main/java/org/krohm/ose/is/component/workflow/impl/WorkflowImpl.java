/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.component.workflow.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.krohm.ose.is.api.action.Action;
import org.krohm.ose.is.api.engine.Engine;
import org.krohm.ose.is.api.engine.EngineAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class WorkflowImpl implements EngineAware, Runnable, Action {

    private Engine oseEngine;
    private static final Logger logger = LoggerFactory.getLogger(WorkflowImpl.class);
    //
    // Bean Members
    //
    private List<Object> containedActionsConfig = new ArrayList<Object>();
    private String service = null;
    private int threads = 0;
    private long maxLoop = -1;
    private long waitLoop = 0;
    private boolean lasyInit = true;
    private String name = null;

    public void addActionConfig(Object nextActionConfig) {
        containedActionsConfig.add(nextActionConfig);
    }

    public long getMaxLoop() {
        return maxLoop;
    }

    public void setMaxLoop(long maxLoop) {
        this.maxLoop = maxLoop;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getThreads() {
        return threads;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public boolean isLasyInit() {
        return lasyInit;
    }

    public void setLasyInit(boolean lasyInit) {
        this.lasyInit = lasyInit;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public long getWaitLoop() {
        return waitLoop;
    }

    public void setWaitLoop(long waitLoop) {
        this.waitLoop = waitLoop;
    }
    //
    // Non Bean Members
    //
    boolean isShutdown = false;
    //
    // Real Methods
    //

    public void init() throws Exception {
        logger.debug("Service Name : " + service);
        if (null == service || "".equals(service)) {
            lasyInit = false;
        }
        if (!(lasyInit)) {
            for (Object currentConfig : containedActionsConfig) {
                Action action = oseEngine.getActionService().getAction(currentConfig);
            }
        }
        if (name != null) {
            logger.info("Registering Workflow :<" + name + ">");
            oseEngine.getWorkflowServive().register(this, name);
        }
        for (int i = 0; i < threads; i++) {
            Thread thread = new Thread(this);
            thread.start();
        }
    }

    // Very simple run. Might need to be modified
    public void run(Map context) throws Exception {

        logger.debug("Entering Workflow :<" + name + ">.");
        int i = 0;
        for (Object currentConfigObject : this.containedActionsConfig) {
            i++;
            logger.debug("Executing Workflow :<" + name + "> at Step : <" + i + ">.");

            Action currentAction = this.oseEngine.getActionService().getAction(currentConfigObject);
            currentAction.run(context);
        }
    }

    // TODO : manage shutdown properly
    public void shutdown() throws Exception {
        oseEngine.getWorkflowServive().unregister(name);
    }

    public void setEngine(Engine oseEngine) {
        this.oseEngine = oseEngine;
    }

    public void run() {
        long currentLoop = 0;
        try {
            while ((!isShutdown) && ((-1 == maxLoop) || (maxLoop > currentLoop))) {
                currentLoop++;
                logger.debug("Now in loop :<" + currentLoop + "> for Workflow <" + name + ">");
                Action selfWorkflow = oseEngine.getWorkflowServive().getRegistered(name);
                selfWorkflow.run(new HashMap());
                //logger.debug("Waiting :<" + waitLoop + "> ms as per configuration");
                if (waitLoop > 0) {
                    logger.debug("Waiting :<" + waitLoop + "> ms as per configuration");
                    Thread.sleep(waitLoop);
                }
            }
        } catch (final Exception e) {
            logger.error("Exception in workflow thread: ", e);
        }
    }
}
