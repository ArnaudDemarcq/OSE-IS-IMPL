/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.krohm.ose.is.impll.xml.configurator.testAction;

import java.util.List;
import java.util.Map;
import org.junit.Ignore;
import org.krohm.ose.is.api.action.Action;
import org.w3c.dom.Element;

/**
 *
 * @author arnaud
 */
@Ignore
public class TestAction1 implements  Action{

    private String stringMember;
    private int intMember;
    private List<String> testList;

    public List<String> getTestList() {
        return testList;
    }

    public void setTestList(List<String> testList) {
        this.testList = testList;
    }

    public int getIntMember() {
        return intMember;
    }

    public void setIntMember(int intMember) {
        this.intMember = intMember;
    }

    public String getStringMember() {
        return stringMember;
    }

    public void setStringMember(String stringMember) {
        this.stringMember = stringMember;
    }

    private Element rawXmlConfig;

    public Element getRawXmlConfig() {
        return rawXmlConfig;
    }

    public void setRawXmlConfig(Element rawXmlConfig) {
        this.rawXmlConfig = rawXmlConfig;
    }
    

    /* THE CODE */

    public void init() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void run(Map context) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void shutdown() throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String toString(){
        return "stringMember :" + stringMember +". intMember :" + intMember;
    }
}
