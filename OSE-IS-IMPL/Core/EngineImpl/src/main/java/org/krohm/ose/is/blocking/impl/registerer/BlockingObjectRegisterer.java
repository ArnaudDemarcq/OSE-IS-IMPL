/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.krohm.ose.is.blocking.impl.registerer;

import org.krohm.ose.is.blocking.impl.util.UniqueId;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import org.krohm.ose.is.api.engine.internal.ObjectRegisterer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author arnaud
 */
public class BlockingObjectRegisterer<T> implements ObjectRegisterer<T> {

    private final static Logger localObjectRegistererLogger = LoggerFactory.getLogger(BlockingObjectRegisterer.class);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    //private final BlockingConcurrentHashMap<String, T> registered = new BlockingConcurrentHashMap<String, T>();
    private final ConcurrentHashMap<String, BlockingQueue<T>> registered = new ConcurrentHashMap<String, BlockingQueue<T>>();

    @Override
    public String register(T registrableObject) {
        String identifier = UniqueId.getUniqueId(registrableObject);
        register(registrableObject, identifier);
        return identifier;
    }

    @Override
    public void register(T registrableObject, String identifier) {
        registered.putIfAbsent(identifier, getNewBlockingQueue());
        BlockingQueue<T> workingQueue = registered.get(identifier);
        boolean offerResult = workingQueue.offer(registrableObject);
        if (!(offerResult)) {
            localObjectRegistererLogger.warn("Attempted to register a object with identifier :"
                    + identifier + "> which was alreay taken. Skipping");
        }
    }

    @Override
    public void unregister(String identifier) {
        BlockingQueue<T> currentQueue = registered.get(identifier);
        if (currentQueue == null) {
            localObjectRegistererLogger.warn("Attempted to unregister a non registered object with identifier :"
                    + identifier + ">. Skipping");
        }
        currentQueue.clear();
    }

    @Override
    public T getRegistered(String identifier) throws InterruptedException {
        registered.putIfAbsent(identifier, getNewBlockingQueue());
        BlockingQueue<T> workingQueue = registered.get(identifier);
        T returnObject = workingQueue.take();
        boolean offerResult = workingQueue.offer(returnObject);
        if (!(offerResult)) {
            logger.error("This shouln't have happend ! An object have been inserted at the wrong time !");
        }
        return returnObject;
    }

    @Override
    public T getRegistered(String identifier, long timeout) throws InterruptedException {
        registered.putIfAbsent(identifier, getNewBlockingQueue());
        BlockingQueue<T> workingQueue = registered.get(identifier);
        T returnObject = workingQueue.poll(timeout, TimeUnit.MILLISECONDS);
        boolean offerResult = workingQueue.offer(returnObject);
        if (!(offerResult)) {
            logger.error("This shouln't have happend ! An object have been inserted at the wrong time !");
        }
        return returnObject;
    }

    @Override
    public T getRegisteredUnsafe(String identifier) {
        BlockingQueue<T> currentQueue = registered.get(identifier);
        if (currentQueue == null) {
            return null;
        }
        T currentObject = currentQueue.peek();
        return currentObject;
    }

    protected Set<String> getKeys() {
        return this.registered.keySet();
    }

    private final BlockingQueue<T> getNewBlockingQueue() {
        return new LinkedBlockingQueue<T>(1);
    }
}
