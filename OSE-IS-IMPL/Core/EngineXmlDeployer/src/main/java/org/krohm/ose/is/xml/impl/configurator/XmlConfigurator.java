/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.xml.impl.configurator;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.digester.Digester;
import org.apache.commons.digester.ObjectCreationFactory;
import org.krohm.ose.is.api.action.Action;
import org.krohm.ose.is.api.configurator.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;

/**
 *
 * @author arnaud
 */
public class XmlConfigurator implements Configurator<Element> {

    private static final Logger logger = LoggerFactory.getLogger(XmlConfigurator.class);
    private static final String rawXmlMethodName = "RawXmlConfig";
    private final MemberSetter<Element, Element> xmlSetter = new XmlSetter();

    @Override
    public Action configureAction(Action action, Element configObject) {

        // Sets the RawXmlElement if this member exists
        xmlSetter.setMemberValue(action, configObject, rawXmlMethodName);
        String rootNodeName = configObject.getNodeName();
        // Now do the real work
        Digester digester = getDigester(rootNodeName, action);
        try {
            Reader configReader = new StringReader(elementToString(configObject));
            Action testAction = (Action) digester.parse(configReader);
        } catch (Exception ex) {
            logger.error("Error digesting the XML config");
            logger.debug("Error digesting the XML config", ex);
        }
        return action;

    }

    @Override
    public int isEligibleAction(Action action) {
        if (action == null) {
            return Configurator.NOT_ELIGIBLE;
        }
        return Configurator.GENERIC;
    }

    @Override
    public int isEligibleConfig(Object configObject) {
        if (!(configObject instanceof Element)) {
            return Configurator.NOT_ELIGIBLE;
        }
        Element configElement = (Element) configObject;
        String nodeName = configElement.getTagName();
        String ServiceName = configElement.getAttribute("service");
        if ((!("action".equals(nodeName))) || (ServiceName == null)) {
            return Configurator.NOT_ELIGIBLE;
        }
        return Configurator.GENERIC;
    }

    protected Digester getDigester(String rootNodeName, Action action) {
        Digester digester = new Digester();
        digester.setValidating(false);
        ObjectCreationFactory pseudoFactory = new CurrentActionPseudoFactory(action);
        digester.addFactoryCreate(rootNodeName, pseudoFactory);
        digester.addSetProperties(rootNodeName);

        return digester;
    }

    private final String elementToString(Element configNode) throws Exception {
        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        StringWriter buffer = new StringWriter();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(configNode),
                new StreamResult(buffer));
        String str = buffer.toString();
        return str;
    }

    public String getServiceName(Element configObject) {
        return configObject.getAttribute("service");
    }

    private final class CurrentActionPseudoFactory implements ObjectCreationFactory {

        private Action actionObject = null;
        private Digester digester = null;

        CurrentActionPseudoFactory(Action actionObject) {
            this.actionObject = actionObject;
        }

        @Override
        public Object createObject(Attributes atrbts) throws Exception {
            return actionObject;
        }

        @Override
        public Digester getDigester() {
            return digester;
        }

        @Override
        public void setDigester(Digester digester) {
            this.digester = digester;
        }
    }

    private abstract class MemberSetter<S, T> {

        protected abstract T convert(S initialValue);

        protected abstract Class<T> getTargetClass();

        private void setMemberValueInternal(Action action, T memberValue, String memberName) {
            Class actionClass = action.getClass();
            Class tClass = getTargetClass();
            String targetMethodName = "set" + memberName.substring(0, 1).toUpperCase() + memberName.substring(1);
            try {
                Method currentMethod = actionClass.getMethod(targetMethodName, tClass);
                currentMethod.invoke(action, memberValue);
            } catch (NoSuchMethodException ex) {
                logger.trace("Object has no Member <" + memberName + "> with type :<" + tClass + ">");
            } catch (Exception ex) {
                logger.warn("Error calling populate Method", ex);
            }
            logger.error(targetMethodName);
        }

        public void setMemberValue(Action action, S originalValue, String memberName) {
            if (originalValue == null) {
                return;
            }
            setMemberValueInternal(action, convert(originalValue), memberName);
        }
    }

    private class XmlSetter extends MemberSetter<Element, Element> {

        @Override
        protected Element convert(Element initialValue) {
            return initialValue;
        }

        @Override
        protected Class<Element> getTargetClass() {
            return Element.class;
        }
    }
}
