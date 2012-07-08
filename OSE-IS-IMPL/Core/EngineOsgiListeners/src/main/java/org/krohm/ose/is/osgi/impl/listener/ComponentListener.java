package org.krohm.ose.is.osgi.impl.listener;

import org.krohm.ose.is.api.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision$
 */
public class ComponentListener extends AbstractEngineAwareListener<Action> {

    private static final String filter = "(&(main_app="+OSE_OSGI_KEY+")(type=ComponentPrototype))";
    private static final Logger logger = LoggerFactory.getLogger(ComponentListener.class);

    public ComponentListener() {
        super(Action.class);
    }

    @Override
    protected String getFilter() {
        return filter;
    }

    @Override
    protected void onRegister(Action serviceObject, String serviceName) {
        logger.info("Component :<" + serviceName + "> has been registered");
        oseEngine.getComponentService().register(serviceObject, serviceName);
    }

    @Override
    protected void onUnRegister(String serviceName) {
        logger.info("Component :<" + serviceName + "> has been unregistered");
        oseEngine.getComponentService().unregister(serviceName);
    }

    @Override
    protected void onModify(Action serviceObject, String serviceName) {
        logger.info("Component :<" + serviceName + "> has been modified");
        onRegister(serviceObject, serviceName);
        onUnRegister(serviceName);
    }
}
