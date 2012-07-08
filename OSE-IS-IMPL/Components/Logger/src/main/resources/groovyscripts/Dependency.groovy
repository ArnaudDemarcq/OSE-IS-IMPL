import java.util.Map;
import org.w3c.dom.Element;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class Dependency  {
    private final Logger logger = LoggerFactory.getLogger(Dependency.class);

    public static final String getDependencyString(){
        return ("This string come from the dependency class");
    }
}
