/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.xml.impl.deployer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.krohm.ose.is.api.engine.Engine;
import org.krohm.ose.is.api.engine.EngineAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author arnaud
 */
public class ResourceXmlDeployer implements EngineAware {

    private final Logger logger = LoggerFactory.getLogger(ResourceXmlDeployer.class);
    private Engine oseEngine;
    private String configFile;
    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private final DocumentBuilder builder = factory.newDocumentBuilder();

    public ResourceXmlDeployer() throws Exception {
    }

    public void setEngine(Engine oseEngine) {
        this.oseEngine = oseEngine;
    }

    public String getConfigFile() {
        return configFile;
    }

    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

    public void init() throws Exception {
        logger.error("########################## We are in the init thing");
        logger.error("########################## Engine is :" + oseEngine);

        Document newconf = builder.parse(Thread.currentThread().
                getContextClassLoader().
                getResourceAsStream(configFile));

        Element rootElement = newconf.getDocumentElement();
        logger.error("########################## Element : " + rootElement);

        String rootDeploymentId = oseEngine.getPackageService().register(rootElement);
        logger.error("########################## Deployed with ID : <" + rootDeploymentId + ">");

    }

    public void destroy() throws Exception {
        logger.error("DESTROY CALLED !!!!");
        // TODO : unregister registered actions
    }
}
