package org.krohm.ose.is.impl.xml.configurator;

import org.krohm.ose.is.xml.impl.configurator.XmlConfigurator;
import org.krohm.ose.is.impl.xml.configurator.testAction.TestAction1;
import org.krohm.ose.is.impl.xml.configurator.testAction.TestAction2;
import org.junit.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.w3c.dom.*;



import javax.xml.parsers.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class MainTest {

    private final Logger logger = LoggerFactory.getLogger(MainTest.class);
    private Element[] elements = null;
    private int iNodes = 0;
    private XmlConfigurator configurator = new XmlConfigurator();
    private XmlConfigurator specificConfigurator = new SpecificConfigurator();

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    @Before
    public void onSetUp() throws Exception {
        //Load XML configuration file
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(this.getClass().getResourceAsStream("/test.xml"));
        NodeList actionList = document.getDocumentElement().getElementsByTagName("action");

        iNodes = actionList.getLength();
        elements = new Element[iNodes];

        for (int i = 0; i < iNodes; i++) {
            elements[i] = (Element) actionList.item(i);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    /**
     * DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    // test on TestAction1;
    @Test
    public void testIsEligible() throws Exception {
        Element testElement = elements[0];
        assert (configurator.isEligibleConfig(null) == XmlConfigurator.NOT_ELIGIBLE);
        assert (configurator.isEligibleConfig(testElement) == XmlConfigurator.GENERIC);
        assert (configurator.isEligibleConfig(configurator) == XmlConfigurator.NOT_ELIGIBLE);
        logger.error("testIsEligible Done");
    }

    @Test
    public void testAction1() throws Exception {

        TestAction1 testAction1 = new TestAction1();
        Element testElement = elements[0];
        configurator.configureAction(testAction1, testElement);
        assert (testAction1.getRawXmlConfig() != null);
        assert ("stringValue".equals(testAction1.getStringMember()));
        assert (42 == testAction1.getIntMember());
        logger.error("testAction1 Done");

    }

    @Test
    public void testAction2() throws Exception {

        TestAction2 testAction2 = new TestAction2();
        Element testElement = elements[1];
        specificConfigurator.configureAction(testAction2, testElement);
        assert ("stringValue".equals(testAction2.getStringMember()));
        assert (42 == testAction2.getIntMember());
        assert (testAction2.getElementList().size() == 3);
        logger.debug(""+ testAction2.toString());
        logger.error("testAction2 Done");
    }
    /**/
}
