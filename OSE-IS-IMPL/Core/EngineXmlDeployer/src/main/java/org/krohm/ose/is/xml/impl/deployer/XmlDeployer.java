/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.xml.impl.deployer;

import org.krohm.ose.is.xml.impl.configurator.XmlConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.*;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import javax.xml.parsers.*;
import org.krohm.ose.is.api.configurator.Configurator;
import org.krohm.ose.is.api.engine.Engine;
import org.krohm.ose.is.api.engine.EngineAware;

/**
 *
 * @author arnaud
 */
public class XmlDeployer implements EngineAware, Runnable {

    private static final String OSE_ROOT_KEY = "org.krohm.ose.is";
    private final Logger logger = LoggerFactory.getLogger(XmlDeployer.class);
    private final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    private final DocumentBuilder builder = factory.newDocumentBuilder();
    // properties
    private final String home = System.getProperty(OSE_ROOT_KEY + ".home", ".");
    private final String configPath = System.getProperty(OSE_ROOT_KEY + ".config", home + "/config/config.xml");
    private final String targetFolder = System.getProperty(OSE_ROOT_KEY + ".save", home + "/config");
    private final String strReload = System.getProperty(OSE_ROOT_KEY + ".reload", "enable");
    private final boolean isConfReload = "enable".equals(strReload);
    private Engine oseEngine = null;
    // file path
    private String path = configPath;
    // for file reload
    private long referenceModificationTime = 0;
    private String rootDeploymentId = null;
    // private Action rootAction = null;
    private final SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMdd_HHmmss");
    private Configurator xmlConfigurator = new XmlConfigurator();

    XmlDeployer() throws Exception {
        factory.setNamespaceAware(true);
    }

    public void init() {
        oseEngine.getConfiguratorService().register(xmlConfigurator);
        new Thread(this).start();
    }

    @Override
    public void setEngine(Engine oseEngine) {
        this.oseEngine = oseEngine;
    }

    @Override
    public void run() {
        try {
            do {
                File f = new File(path);
                logger.trace("Reading Configuration file : <" + path + ">");
                if (isModified(f)) {
                    logger.info("Configuration file modified : <" + path + ">");
                    // todo : add checksum in order not to reload indentical file if just touched
                    try {
                        Element rootElement = null;
                        Document newconf = builder.parse(f);
                        rootElement = newconf.getDocumentElement();
                        // unloads the configuration file
                        if (this.rootDeploymentId != null) {
                            oseEngine.getPackageService().unregister(rootDeploymentId);
                        }
                        rootDeploymentId = oseEngine.getPackageService().register(rootElement);
                        setReference(f);
                    } catch (Exception e) {
                        logger.error("Bad Configuration file, keeping previous configuration", e);
                    }
                }
                Thread.sleep(5000);
            } while (isConfReload);

            logger.info("ConfReload Disabled, thread stopped");

        } catch (final Exception e) {
            logger.error("Cannot initialize main workflow", e);
        }
    }

    private void saveConfigurationFile(Document configurationDocument) throws Exception {
        String confTargetFile = targetFolder + "/config." + dateFormater.format(new Date()) + ".save.xml";
        logger.info("Saving configuration file : <" + confTargetFile + ">");

        File file = new File(confTargetFile);
        FileWriter writer = new FileWriter(file);

        DOMSource domSource = new DOMSource(configurationDocument);
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        writer.close();
    }

    public boolean isModified(File nextFile) {
        return referenceModificationTime < nextFile.lastModified();
    }

    private void setReference(File refFile) {
        referenceModificationTime = refFile.lastModified();
    }
}
