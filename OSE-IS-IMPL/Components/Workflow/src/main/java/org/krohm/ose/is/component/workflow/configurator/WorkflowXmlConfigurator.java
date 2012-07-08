/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.component.workflow.configurator;

import org.krohm.ose.is.component.workflow.impl.WorkflowImpl;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.NodeCreateRule;
import org.krohm.ose.is.api.action.Action;
import org.krohm.ose.is.xml.impl.configurator.XmlConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

/**
 *
 * @author arnaud
 */
public class WorkflowXmlConfigurator extends XmlConfigurator {

    private static final String actionKey = "action";
    private static final String actionKey2 = "workflow";
    private static final String addActionMethod = "addActionConfig";
    private static final Logger logger = LoggerFactory.getLogger(WorkflowXmlConfigurator.class);

    @Override
    protected Digester getDigester(String rootNodeName, Action action) {
        Digester digester = super.getDigester(rootNodeName, action);
        String actionPath = rootNodeName + "/" + actionKey;
        String actionPath2 = rootNodeName + "/" + actionKey2;
        try {
            digester.addRule(actionPath, new NodeCreateRule());
            digester.addRule(actionPath2, new NodeCreateRule());
        } catch (ParserConfigurationException ex) {
            logger.error("Error Occured during Workflow Digester Initialization");
            logger.debug("Error Occured during Workflow Digester Initialization", ex);
        }
        digester.addSetNext(actionPath, addActionMethod);
        digester.addSetNext(actionPath2, addActionMethod);
        return digester;
    }

    @Override
    public int isEligibleAction(Action action) {
        if (action instanceof WorkflowImpl) {
            return WorkflowXmlConfigurator.SPECIFIC;
        } else {
            return WorkflowXmlConfigurator.NOT_ELIGIBLE;
        }
    }

    @Override
    public int isEligibleConfig(Object configObject) {
        if (!(configObject instanceof Element)) {
            return WorkflowXmlConfigurator.NOT_ELIGIBLE;
        }
        Element configElement = (Element) configObject;
        String configRootName = configElement.getNodeName();
        String serviceName = configElement.getAttribute("service");
        if ("workflow".equals(configRootName) || "config".equals(configRootName)) {
            return WorkflowXmlConfigurator.SPECIFIC;
        }
        if ("action".equals(configRootName) && "workflow".equals(serviceName)) {
            return WorkflowXmlConfigurator.SPECIFIC;
        }
        return WorkflowXmlConfigurator.NOT_ELIGIBLE;
    }

    @Override
    public String getServiceName(Element configObject) {
        String superString = super.getServiceName(configObject);
        if (superString == null || "".equals(superString)) {
            return "workflow";
        }
        return superString;
    }
}
