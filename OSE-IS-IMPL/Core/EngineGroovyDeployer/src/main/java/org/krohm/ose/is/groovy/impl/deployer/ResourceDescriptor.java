/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.krohm.ose.is.groovy.impl.deployer;

/**
 *
 * @author arnaud
 */
public class ResourceDescriptor  {

    private String scriptPath;
    private String targetServiceName;

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public String getTargetServiceName() {
        return targetServiceName;
    }

    public void setTargetServiceName(String targetServiceName) {
        this.targetServiceName = targetServiceName;
    }
    

}
