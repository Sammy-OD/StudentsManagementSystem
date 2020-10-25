package zkysms.property;

import org.w3c.dom.Document;
import zkysms.constants.Path;
import zkysms.constants.SessionConstants;
import zkysms.utils.XMLUtil;
import java.util.HashMap;
import java.util.Map;

public class SessionProperty {

    private static Map<String, String> map;

    public static void setSessionProperties(){
        map = new HashMap<>();
        Document doc = XMLUtil.loadXML(Path.APP_XML.getPath());
        if(doc != null){
            map.put(SessionConstants.CURRENT_SESSION.getKey(), doc.getElementsByTagName(SessionConstants.CURRENT_SESSION.getTagName()).item(SessionConstants.CURRENT_SESSION.getIndex()).getTextContent());
            map.put(SessionConstants.NO_OF_TEACHERS.getKey(), doc.getElementsByTagName(SessionConstants.NO_OF_TEACHERS.getTagName()).item(SessionConstants.CURRENT_SESSION.getIndex()).getTextContent());
            map.put(SessionConstants.NO_OF_P1STUDENTS.getKey(), doc.getElementsByTagName(SessionConstants.NO_OF_P1STUDENTS.getTagName()).item(SessionConstants.CURRENT_SESSION.getIndex()).getTextContent());
            map.put(SessionConstants.NO_OF_P2STUDENTS.getKey(), doc.getElementsByTagName(SessionConstants.NO_OF_P2STUDENTS.getTagName()).item(SessionConstants.CURRENT_SESSION.getIndex()).getTextContent());
            map.put(SessionConstants.NO_OF_P3STUDENTS.getKey(), doc.getElementsByTagName(SessionConstants.NO_OF_P3STUDENTS.getTagName()).item(SessionConstants.CURRENT_SESSION.getIndex()).getTextContent());
            map.put(SessionConstants.NO_OF_P4STUDENTS.getKey(), doc.getElementsByTagName(SessionConstants.NO_OF_P4STUDENTS.getTagName()).item(SessionConstants.CURRENT_SESSION.getIndex()).getTextContent());
            map.put(SessionConstants.NO_OF_P5STUDENTS.getKey(), doc.getElementsByTagName(SessionConstants.NO_OF_P5STUDENTS.getTagName()).item(SessionConstants.CURRENT_SESSION.getIndex()).getTextContent());
            map.put(SessionConstants.NO_OF_P6STUDENTS.getKey(), doc.getElementsByTagName(SessionConstants.NO_OF_P6STUDENTS.getTagName()).item(SessionConstants.CURRENT_SESSION.getIndex()).getTextContent());
            map.put(SessionConstants.NO_OF_J1STUDENTS.getKey(), doc.getElementsByTagName(SessionConstants.NO_OF_J1STUDENTS.getTagName()).item(SessionConstants.CURRENT_SESSION.getIndex()).getTextContent());
            map.put(SessionConstants.NO_OF_J2STUDENTS.getKey(), doc.getElementsByTagName(SessionConstants.NO_OF_J2STUDENTS.getTagName()).item(SessionConstants.CURRENT_SESSION.getIndex()).getTextContent());
            map.put(SessionConstants.NO_OF_J3STUDENTS.getKey(), doc.getElementsByTagName(SessionConstants.NO_OF_J3STUDENTS.getTagName()).item(SessionConstants.CURRENT_SESSION.getIndex()).getTextContent());
            map.put(SessionConstants.NO_OF_S1STUDENTS.getKey(), doc.getElementsByTagName(SessionConstants.NO_OF_S1STUDENTS.getTagName()).item(SessionConstants.CURRENT_SESSION.getIndex()).getTextContent());
            map.put(SessionConstants.NO_OF_S2STUDENTS.getKey(), doc.getElementsByTagName(SessionConstants.NO_OF_S2STUDENTS.getTagName()).item(SessionConstants.CURRENT_SESSION.getIndex()).getTextContent());
            map.put(SessionConstants.NO_OF_S3STUDENTS.getKey(), doc.getElementsByTagName(SessionConstants.NO_OF_S3STUDENTS.getTagName()).item(SessionConstants.CURRENT_SESSION.getIndex()).getTextContent());
        }
    }

    public static String getProperty(String key) {
        return map.get(key);
    }

    public static void updateProperty(String key, String value) {
        map.replace(key, value);
        Document doc = XMLUtil.loadXML(Path.APP_XML.getPath());
        if(doc != null){
            doc.getElementsByTagName(SessionConstants.constant(key).getTagName()).item(SessionConstants.constant(key).getIndex()).setTextContent(value);
            XMLUtil.updateXML(Path.APP_XML.getPath(), doc);
        }
    }

}