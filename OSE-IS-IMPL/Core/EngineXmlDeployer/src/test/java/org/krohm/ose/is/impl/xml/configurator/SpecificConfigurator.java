/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.impl.xml.configurator;

import org.krohm.ose.is.xml.impl.configurator.XmlConfigurator;
import org.krohm.ose.is.impl.xml.configurator.testAction.TestAction2;

import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.NodeCreateRule;
import org.krohm.ose.is.api.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class SpecificConfigurator extends XmlConfigurator {

    private static final Logger logger = LoggerFactory.getLogger(XmlConfigurator.class);

    @Override
    protected Digester getDigester(String rootNodeName, Action action) {
        Digester digester = super.getDigester(rootNodeName, action);
        try {
            digester.addRule(rootNodeName + "/testList", new NodeCreateRule());
        } catch (ParserConfigurationException ex) {
            logger.error("#################################################### !");
        }
        digester.addSetNext(rootNodeName + "/testList", "addElement");
        return digester;
    }

    @Override
    public int isEligibleAction(Action action) {
        if (super.isEligibleAction(action) == SpecificConfigurator.NOT_ELIGIBLE) {
            return SpecificConfigurator.NOT_ELIGIBLE;
        }
        if (action instanceof TestAction2) {
            return SpecificConfigurator.NOT_ELIGIBLE;
        }
        return SpecificConfigurator.SPECIFIC;
    }

    @Override
    public int isEligibleConfig(Object configObject) {
        if (super.isEligibleConfig(configObject) == SpecificConfigurator.NOT_ELIGIBLE) {
            return SpecificConfigurator.NOT_ELIGIBLE;
        }
        return SpecificConfigurator.SPECIFIC;
    }
}
