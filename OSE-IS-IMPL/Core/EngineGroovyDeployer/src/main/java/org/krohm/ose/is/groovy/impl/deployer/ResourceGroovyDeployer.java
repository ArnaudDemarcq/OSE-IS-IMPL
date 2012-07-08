/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.groovy.impl.deployer;

import groovy.util.GroovyScriptEngine;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.krohm.ose.is.api.action.Action;
import org.krohm.ose.is.api.engine.Engine;
import org.krohm.ose.is.api.engine.EngineAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class ResourceGroovyDeployer implements EngineAware {

    private final Logger logger = LoggerFactory.getLogger(ResourceGroovyDeployer.class);
    private Engine oseEngine = null;
    private String rootPath = ".";
    private GroovyScriptEngine gse;
    private List<ResourceDescriptor> resourceDescriptors;
    private List<String> deployedComponents = new ArrayList<String>();

    public List<ResourceDescriptor> getResourceDescriptors() {
        return resourceDescriptors;
    }

    public void setResourceDescriptors(List<ResourceDescriptor> resourceDescriptors) {
        this.resourceDescriptors = resourceDescriptors;
    }

    @Override
    public void setEngine(Engine oseEngine) {
        this.oseEngine = oseEngine;
    }

    public void init() throws Exception {
        ClassLoader localClassLoader = ResourceGroovyDeployer.class.getClassLoader();
        gse = new GroovyScriptEngine(rootPath, localClassLoader);
        for (ResourceDescriptor resourceDescriptor : resourceDescriptors) {
            logger.info("Loading ressource script :" + resourceDescriptor.getScriptPath());
            loadScript(resourceDescriptor);
        }
    }

    public void destroy() throws Exception {
        logger.info("Destroy Called");
        for (String currentComponentString : deployedComponents) {
            oseEngine.getComponentService().unregister(currentComponentString);
        }
    }

    private void loadScript(ResourceDescriptor currentResourceDescriptor) {
        try {
            ClassLoader localClassLoader = ResourceGroovyDeployer.class.getClassLoader();
            InputStream fileInputStream = localClassLoader.getResourceAsStream(currentResourceDescriptor.getScriptPath());
            if (fileInputStream == null) {
                String message = "Ressource at path :<" + currentResourceDescriptor.getScriptPath() + "> does not exist";
                logger.warn(message);
                throw new NullPointerException(message);
            }
            String scriptString = this.convertStreamToString(fileInputStream);
            Class targetClass = gse.getGroovyClassLoader().parseClass(scriptString);
            logger.info("Loading Groovy Class :<" + targetClass + ">");
            Class[] currentInterfaces = targetClass.getInterfaces();
            for (Class currentInterface : currentInterfaces) {
                if (Action.class.equals(currentInterface) && currentResourceDescriptor.getTargetServiceName() != null) {
                    logger.info("Registering Groovy Class as an Action :<" + targetClass + ">");
                    oseEngine.getComponentService().register(targetClass, currentResourceDescriptor.getTargetServiceName());
                    deployedComponents.add(currentResourceDescriptor.getTargetServiceName());
                }
            }
        } catch (IOException ex) {
            logger.error("Error loading file :" + currentResourceDescriptor.getScriptPath());
            logger.debug("Error loading file :" + currentResourceDescriptor.getScriptPath(), ex);
        }catch (Exception ex) {
            logger.error("Error loading file :" + currentResourceDescriptor.getScriptPath());
            logger.debug("Error loading file :" + currentResourceDescriptor.getScriptPath(), ex);
        }
    }

    private String convertStreamToString(InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the
         * Reader.read(char[] buffer) method. We iterate until the
         * Reader return -1 which means there's no more data to
         * read. We use the StringWriter class to produce the string.
         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }
}
