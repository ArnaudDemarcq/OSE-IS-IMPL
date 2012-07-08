import Dependency;

import java.util.Map;
import org.w3c.dom.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.krohm.ose.is.api.action.Action


public class PackagedGroovy implements Action {
    private final Logger logger = LoggerFactory.getLogger(PackagedGroovy.class);

	public void init(){
		logger.debug("Groovy Example init");
	}
	
	public void run(Map context) throws Exception{
		logger.warn("Groovy Example run and wait 10 second");
                logger.warn(Dependency.getDependencyString());
		//Thread.sleep(10000);
	}
	
	public void shutdown(){}
}
