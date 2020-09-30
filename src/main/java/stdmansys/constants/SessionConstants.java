package stdmansys.constants;

public enum SessionConstants {

    CURRENT_SESSION("currentSession", XMLConstants.SESSION.getTag().getTagName(), XMLConstants.SESSION.getTag().getIndex()),
    NO_OF_TEACHERS("currentNumberOfTeachersRegisteredThisSession", XMLConstants.TEACHERS_IN_SESSION.getTag().getTagName(), XMLConstants.TEACHERS_IN_SESSION.getTag().getIndex()),
    NO_OF_P1STUDENTS("currentNumberOfPrimaryOneStudentsRegisteredThisSession", XMLConstants.P1STUDENTS_IN_SESSION.getTag().getTagName(), XMLConstants.P1STUDENTS_IN_SESSION.getTag().getIndex()),
    NO_OF_P2STUDENTS("currentNumberOfPrimaryTwoStudentsRegisteredThisSession", XMLConstants.P2STUDENTS_IN_SESSION.getTag().getTagName(), XMLConstants.P2STUDENTS_IN_SESSION.getTag().getIndex()),
    NO_OF_P3STUDENTS("currentNumberOfPrimaryThreeStudentsRegisteredThisSession", XMLConstants.P3STUDENTS_IN_SESSION.getTag().getTagName(), XMLConstants.P3STUDENTS_IN_SESSION.getTag().getIndex()),
    NO_OF_P4STUDENTS("currentNumberOfPrimaryFourStudentsRegisteredThisSession", XMLConstants.P4STUDENTS_IN_SESSION.getTag().getTagName(), XMLConstants.P4STUDENTS_IN_SESSION.getTag().getIndex()),
    NO_OF_P5STUDENTS("currentNumberOfPrimaryFiveStudentsRegisteredThisSession", XMLConstants.P5STUDENTS_IN_SESSION.getTag().getTagName(), XMLConstants.P5STUDENTS_IN_SESSION.getTag().getIndex()),
    NO_OF_P6STUDENTS("currentNumberOfPrimarySixStudentsRegisteredThisSession", XMLConstants.P6STUDENTS_IN_SESSION.getTag().getTagName(), XMLConstants.P6STUDENTS_IN_SESSION.getTag().getIndex()),
    NO_OF_J1STUDENTS("currentNumberOfJSOneStudentsRegisteredThisSession", XMLConstants.J1STUDENTS_IN_SESSION.getTag().getTagName(), XMLConstants.J1STUDENTS_IN_SESSION.getTag().getIndex()),
    NO_OF_J2STUDENTS("currentNumberOfJSTwoStudentsRegisteredThisSession", XMLConstants.J2STUDENTS_IN_SESSION.getTag().getTagName(), XMLConstants.J2STUDENTS_IN_SESSION.getTag().getIndex()),
    NO_OF_J3STUDENTS("currentNumberOfJSThreeStudentsRegisteredThisSession", XMLConstants.J3STUDENTS_IN_SESSION.getTag().getTagName(), XMLConstants.J3STUDENTS_IN_SESSION.getTag().getIndex()),
    NO_OF_S1STUDENTS("currentNumberOfSSOneStudentsRegisteredThisSession", XMLConstants.S1STUDENTS_IN_SESSION.getTag().getTagName(), XMLConstants.S1STUDENTS_IN_SESSION.getTag().getIndex()),
    NO_OF_S2STUDENTS("currentNumberOfSSTwoStudentsRegisteredThisSession", XMLConstants.S2STUDENTS_IN_SESSION.getTag().getTagName(), XMLConstants.S2STUDENTS_IN_SESSION.getTag().getIndex()),
    NO_OF_S3STUDENTS("currentNumberOfSSThreeStudentsRegisteredThisSession", XMLConstants.S3STUDENTS_IN_SESSION.getTag().getTagName(), XMLConstants.S3STUDENTS_IN_SESSION.getTag().getIndex());

    private String key, tagName;
    private int i;

    SessionConstants(String key, String tagName, int i){
        this.key = key;
        this.tagName = tagName;
        this.i = i;
    }

    public String getKey(){
        return key;
    }

    public String getTagName(){
        return tagName;
    }

    public int getIndex(){
        return i;
    }

    public static SessionConstants constant(String key){
        switch(key){
            case "currentSession":
                return SessionConstants.CURRENT_SESSION;
            case "currentNumberOfTeachersRegisteredThisSession":
                return SessionConstants.NO_OF_TEACHERS;
            case "currentNumberOfPrimaryOneStudentsRegisteredThisSession":
                return SessionConstants.NO_OF_P1STUDENTS;
            case "currentNumberOfPrimaryTwoStudentsRegisteredThisSession":
                return SessionConstants.NO_OF_P2STUDENTS;
            case "currentNumberOfPrimaryThreeStudentsRegisteredThisSession":
                return SessionConstants.NO_OF_P3STUDENTS;
            case "currentNumberOfPrimaryFourStudentsRegisteredThisSession":
                return SessionConstants.NO_OF_P4STUDENTS;
            case "currentNumberOfPrimaryFiveStudentsRegisteredThisSession":
                return SessionConstants.NO_OF_P5STUDENTS;
            case "currentNumberOfPrimarySixStudentsRegisteredThisSession":
                return SessionConstants.NO_OF_P6STUDENTS;
            case "currentNumberOfJSOneStudentsRegisteredThisSession":
                return SessionConstants.NO_OF_J1STUDENTS;
            case "currentNumberOfJSTwoStudentsRegisteredThisSession":
                return SessionConstants.NO_OF_J2STUDENTS;
            case "currentNumberOfJSThreeStudentsRegisteredThisSession":
                return SessionConstants.NO_OF_J3STUDENTS;
            case "currentNumberOfSSOneStudentsRegisteredThisSession":
                return SessionConstants.NO_OF_S1STUDENTS;
            case "currentNumberOfSSTwoStudentsRegisteredThisSession":
                return SessionConstants.NO_OF_S2STUDENTS;
            case "currentNumberOfSSThreeStudentsRegisteredThisSession":
                return SessionConstants.NO_OF_S3STUDENTS;
            default:
                return null;
        }
    }

}