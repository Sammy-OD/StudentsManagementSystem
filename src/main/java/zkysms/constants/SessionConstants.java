package zkysms.constants;

public enum SessionConstants {

    CURRENT_SESSION("currentSession", Constants.SESSION_TAG.getTag().getTagName(), Constants.SESSION_TAG.getTag().getIndex()),
    NO_OF_TEACHERS("currentNumberOfTeachersRegisteredThisSession", Constants.TEACHERS_IN_SESSION_TAG.getTag().getTagName(), Constants.TEACHERS_IN_SESSION_TAG.getTag().getIndex()),
    NO_OF_P1STUDENTS("currentNumberOfPrimaryOneStudentsRegisteredThisSession", Constants.P1STUDENTS_IN_SESSION_TAG.getTag().getTagName(), Constants.P1STUDENTS_IN_SESSION_TAG.getTag().getIndex()),
    NO_OF_P2STUDENTS("currentNumberOfPrimaryTwoStudentsRegisteredThisSession", Constants.P2STUDENTS_IN_SESSION_TAG.getTag().getTagName(), Constants.P2STUDENTS_IN_SESSION_TAG.getTag().getIndex()),
    NO_OF_P3STUDENTS("currentNumberOfPrimaryThreeStudentsRegisteredThisSession", Constants.P3STUDENTS_IN_SESSION_TAG.getTag().getTagName(), Constants.P3STUDENTS_IN_SESSION_TAG.getTag().getIndex()),
    NO_OF_P4STUDENTS("currentNumberOfPrimaryFourStudentsRegisteredThisSession", Constants.P4STUDENTS_IN_SESSION_TAG.getTag().getTagName(), Constants.P4STUDENTS_IN_SESSION_TAG.getTag().getIndex()),
    NO_OF_P5STUDENTS("currentNumberOfPrimaryFiveStudentsRegisteredThisSession", Constants.P5STUDENTS_IN_SESSION_TAG.getTag().getTagName(), Constants.P5STUDENTS_IN_SESSION_TAG.getTag().getIndex()),
    NO_OF_P6STUDENTS("currentNumberOfPrimarySixStudentsRegisteredThisSession", Constants.P6STUDENTS_IN_SESSION_TAG.getTag().getTagName(), Constants.P6STUDENTS_IN_SESSION_TAG.getTag().getIndex()),
    NO_OF_J1STUDENTS("currentNumberOfJSOneStudentsRegisteredThisSession", Constants.J1STUDENTS_IN_SESSION_TAG.getTag().getTagName(), Constants.J1STUDENTS_IN_SESSION_TAG.getTag().getIndex()),
    NO_OF_J2STUDENTS("currentNumberOfJSTwoStudentsRegisteredThisSession", Constants.J2STUDENTS_IN_SESSION_TAG.getTag().getTagName(), Constants.J2STUDENTS_IN_SESSION_TAG.getTag().getIndex()),
    NO_OF_J3STUDENTS("currentNumberOfJSThreeStudentsRegisteredThisSession", Constants.J3STUDENTS_IN_SESSION_TAG.getTag().getTagName(), Constants.J3STUDENTS_IN_SESSION_TAG.getTag().getIndex()),
    NO_OF_S1STUDENTS("currentNumberOfSSOneStudentsRegisteredThisSession", Constants.S1STUDENTS_IN_SESSION_TAG.getTag().getTagName(), Constants.S1STUDENTS_IN_SESSION_TAG.getTag().getIndex()),
    NO_OF_S2STUDENTS("currentNumberOfSSTwoStudentsRegisteredThisSession", Constants.S2STUDENTS_IN_SESSION_TAG.getTag().getTagName(), Constants.S2STUDENTS_IN_SESSION_TAG.getTag().getIndex()),
    NO_OF_S3STUDENTS("currentNumberOfSSThreeStudentsRegisteredThisSession", Constants.S3STUDENTS_IN_SESSION_TAG.getTag().getTagName(), Constants.S3STUDENTS_IN_SESSION_TAG.getTag().getIndex());

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