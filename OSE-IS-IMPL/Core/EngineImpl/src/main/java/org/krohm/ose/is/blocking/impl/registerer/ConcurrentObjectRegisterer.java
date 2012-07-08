/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.blocking.impl.registerer;

import org.krohm.ose.is.blocking.impl.util.UniqueId;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.krohm.ose.is.api.engine.internal.ObjectRegisterer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class ConcurrentObjectRegisterer<T> implements ObjectRegisterer<T> {

    private final static Logger localObjectRegistererLogger = LoggerFactory.getLogger(ConcurrentObjectRegisterer.class);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConcurrentHashMap<String, T> registered = new ConcurrentHashMap<String, T>();

    public String register(T registrableObject) {
        String identifier = UniqueId.getUniqueId(registrableObject);
        register(registrableObject, identifier);
        return identifier;
    }

    public void register(T registrableObject, String identifier) {
        T last = registered.putIfAbsent(identifier, registrableObject);
        if (last != null) {
            localObjectRegistererLogger.warn("Attempted to register a object with identifier :"
                    + identifier + "> which was alreay taken. Skipping");
        }
    }

    public void unregister(String identifier) {
        registered.remove(identifier);
    }

    public T getRegistered(String identifier) {
        return registered.get(identifier);
    }

    public T getRegistered(String identifier, long timeout) {
        logger.warn("Timeout is not supported for this implementation of ObjectRegisterer");
        return registered.get(identifier);
    }

    public T getRegisteredUnsafe(String identifier) {
        logger.warn("Timeout is not supported for this implementation of ObjectRegisterer");
        return registered.get(identifier);
    }

    protected Map<String,T> getRegisterationMap(){
        return registered;
    }
}
