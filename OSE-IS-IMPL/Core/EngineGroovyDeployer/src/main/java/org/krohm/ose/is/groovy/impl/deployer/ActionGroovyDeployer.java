package org.krohm.ose.is.groovy.impl.deployer;

import groovy.util.GroovyScriptEngine;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.krohm.ose.is.api.action.Action;
import org.krohm.ose.is.api.engine.Engine;
import org.krohm.ose.is.api.engine.EngineAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActionGroovyDeployer extends TimerTask implements Action, EngineAware, Runnable {

    private final static Logger logger = LoggerFactory.getLogger(ActionGroovyDeployer.class);
    private Engine oseEngine = null;
    private String rootPath = "custom";
    private String servicePrefix = "";
    private GroovyScriptEngine gse;
    private Timer timer = new Timer();
    private static long PERIOD = 5000;
    private Map<String, DeploymentDescriptor> deployments = new HashMap<String, DeploymentDescriptor>();

    //
    // Bean Members
    //
    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public String getServicePrefix() {
        return servicePrefix;
    }

    public void setServicePrefix(String servicePrefix) {
        this.servicePrefix = servicePrefix;
    }

    //
    // Real Methods
    //
    @Override
    public void setEngine(Engine oseEngine) {
        this.oseEngine = oseEngine;
    }

    public void init() throws Exception {
        CompilerConfiguration testConfig = new CompilerConfiguration();
        gse = new GroovyScriptEngine(rootPath, Thread.currentThread().getContextClassLoader());
        timer.schedule(this, 0, PERIOD);
    }

    public void run(final Map context) throws Exception {
        logger.error("Groovy plugin shouldn't be run");
        throw new Exception("Groovy plugin shouldn't be run");
    }

    /**
     * DOCUMENT ME!
     */
    public void shutdown() {
        timer.cancel();
    }

    public void run() {
        File deployDir = new File(rootPath);
        gse.getGroovyClassLoader().clearCache();
        try {
            // marks all the "old" script to be deleted
            for (String currentKey : deployments.keySet()) {
                DeploymentDescriptor currentDeploymentDescriptor = deployments.get(currentKey);
                currentDeploymentDescriptor.setStillExist(false);
            }
            // gets all the content of the root Folder
            List<String> newList = getFileNames(deployDir, ".");
            for (String currentString : newList) {
                // Then performs the tasks for each of these files
                Long currentModif = (new File(rootPath + "/" + currentString)).lastModified();
                DeploymentDescriptor currentDD = deployments.get(currentString);
                if (currentDD == null) {
                    currentDD = new DeploymentDescriptor();
                    deployments.put(currentString, currentDD);
                }
                currentDD.setStillExist(true);
                Long previousModif = currentDD.getLastModificationDate();
                if (previousModif == null || previousModif.longValue() != currentModif.longValue()) {
                    // load the groovy script
                    Class groovyClass = gse.loadScriptByName(currentString);
                    logGseContent();
                    currentDD.setLastModificationDate(currentModif.longValue());
                    currentDD.setClassObject(groovyClass);
                    currentDD.setFilePath(currentString);
                    if (isActionClass(groovyClass)) {

                        String serviceName = groovyClass.getCanonicalName();
                        logger.error("Deploying Service :<" + serviceName + ">");
                        if (previousModif != null) {
                            oseEngine.getComponentService().unregister(serviceName);
                        }
                        oseEngine.getComponentService().register(groovyClass, serviceName);
                    }
                }
            }

        } catch (Exception ex) {
            logger.warn("", ex);
        }
    }

    //
    // private Methods
    //
    private List<String> getFileNames(File deployDir, String currentFolderPrefix) {
        List<String> currentList = new ArrayList<String>();
        for (File currentFile : deployDir.listFiles()) {
            if (currentFile.isDirectory()) {
                List<String> subList = getFileNames(currentFile, currentFolderPrefix + "/" + currentFile.getName());
                currentList.addAll(subList);
            } else {
                currentList.add(currentFolderPrefix + "/" + currentFile.getName());
            }
        }
        return currentList;
    }

    private void logGseContent() {
        logger.debug("List of loaded classes :");
        for (Class currentClass : gse.getGroovyClassLoader().getLoadedClasses()) {
            logger.debug("Loaded class :<" + currentClass + ">");
        }
    }

    //
    // private bean class
    //
    private class DeploymentDescriptor {

        private String filePath;
        private Class classObject;
        private Long lastModificationDate = null;
        private boolean stillExist = true;

        public Class getClassObject() {
            return classObject;
        }

        public void setClassObject(Class classObject) {
            this.classObject = classObject;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public Long getLastModificationDate() {
            return lastModificationDate;
        }

        public void setLastModificationDate(Long lastModificationDate) {
            this.lastModificationDate = lastModificationDate;
        }

        public boolean isStillExist() {
            return stillExist;
        }

        public void setStillExist(boolean stillExist) {
            this.stillExist = stillExist;
        }
    }

    private static final boolean isActionClass(Class groovyClass) {
        Class[] currentInterfaces = groovyClass.getInterfaces();
        for (Class currentInterface : currentInterfaces) {
            if (Action.class.equals(currentInterface)) {
                return true;
            }
        }
        return false;
    }
}
