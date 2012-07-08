/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.blocking.impl.registerer;

import org.krohm.ose.is.blocking.impl.BlockingEngineAware;
import org.krohm.ose.is.blocking.impl.EngineBlockingImpl;

/**
 *
 * @author arnaud
 */
public class EngineAwareConcurrentRegisterer<T> extends ConcurrentObjectRegisterer<T> implements BlockingEngineAware {

    private EngineBlockingImpl oseEngine;

    @Override
    public void setEngine(EngineBlockingImpl oseEngine) {
        this.oseEngine = oseEngine;
    }

    public EngineBlockingImpl getEngine() {
        return oseEngine;
    }
}
