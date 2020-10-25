package zkysms.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {

    public static Logger getLOGGER(String name) {
        return LoggerFactory.getLogger(name);
    }

}
