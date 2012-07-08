/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.krohm.ose.is.impl.xml.configurator.testAction;

import java.util.ArrayList;
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
public class TestAction2 implements  Action{

    private String stringMember;
    private int intMember;

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

    List<Element> elementList = new ArrayList<Element>();

    public  List<Element> getElementList() {
        return elementList;
    }

    public void setElementList( List<Element> elementList) {
        this.elementList = elementList;
    }

    public void addElement(Element e){
        elementList.add(e);
    }

    @Override
    public String toString(){
        String testString = "";

        for (Element currentElement : elementList)
        {
            testString= testString + currentElement.getTagName();
        }

        return testString;
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

}
