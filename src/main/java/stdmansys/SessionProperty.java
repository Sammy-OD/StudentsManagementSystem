package stdmansys;

import org.w3c.dom.Document;
import stdmansys.utils.XMLUtil;

public class SessionProperty {

    private static String currentSession;
    private static int currentNumberOfTeachersRegisteredThisSession, currentNumberOfStudentsRegisteredThisSession;

    public static void setSessionProperties(){
        Document doc = XMLUtil.loadXML("app");
        if(doc != null){
            currentSession = doc.getElementsByTagName("session").item(0).getTextContent();
            currentNumberOfTeachersRegisteredThisSession = Integer.parseInt(doc.getElementsByTagName("teachers").item(0).getTextContent());
            currentNumberOfStudentsRegisteredThisSession = Integer.parseInt(doc.getElementsByTagName("students").item(0).getTextContent());
        }
    }

    public static String getCurrentSession(){
        return currentSession;
    }

    public static int getCurrentNumberOfTeachersRegisteredThisSession(){
        return currentNumberOfTeachersRegisteredThisSession;
    }

    public static int getCurrentNumberOfStudentsRegisteredThisSession(){
        return currentNumberOfStudentsRegisteredThisSession;
    }

    public static void setCurrentSession(String currentSession){
        SessionProperty.currentSession = currentSession;
    }

    public static void setCurrentNumberOfTeachersRegisteredThisSession(int currentNumberOfTeachersRegisteredThisSession){
        SessionProperty.currentNumberOfTeachersRegisteredThisSession = currentNumberOfTeachersRegisteredThisSession;
    }

    public static void setCurrentNumberOfStudentsRegisteredThisSession(int currentNumberOfStudentsRegisteredThisSession){
        SessionProperty.currentNumberOfStudentsRegisteredThisSession = currentNumberOfStudentsRegisteredThisSession;
    }

}