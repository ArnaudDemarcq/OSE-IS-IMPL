package org.krohm.ose.is.blocking.impl.factory;

import org.krohm.ose.is.api.action.Action;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public interface ActionFactory {

    Action getUnsafeAction(final Object configObject);

    Action getAction(final Object configObject) throws Exception;

    void destroyAction(final Object configObject) throws Exception;

    Class<? extends Action> getActionClass();

    void clear();
}
