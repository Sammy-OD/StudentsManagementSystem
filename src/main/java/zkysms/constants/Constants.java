package zkysms.constants;

public enum Constants {

    SCHOOL_NAME_TAG(new Tag("name", 0)), SESSION_TAG(new Tag("session", 0)), TERM_TAG(new Tag("term", 0)), SCHOOL_LOGO_TAG(new Tag("logo" , 0)),
    TEACHERS_IN_SESSION_TAG(new Tag("teachers", 0)), P1STUDENTS_IN_SESSION_TAG(new Tag("p1", 0)), P2STUDENTS_IN_SESSION_TAG(new Tag("p2", 0)),
    P3STUDENTS_IN_SESSION_TAG(new Tag("p3", 0)), P4STUDENTS_IN_SESSION_TAG(new Tag("p4", 0)), P5STUDENTS_IN_SESSION_TAG(new Tag("p5", 0)),
    P6STUDENTS_IN_SESSION_TAG(new Tag("p6", 0)), J1STUDENTS_IN_SESSION_TAG(new Tag("j1", 0)), J2STUDENTS_IN_SESSION_TAG(new Tag("j2", 0)),
    J3STUDENTS_IN_SESSION_TAG(new Tag("j3", 0)), S1STUDENTS_IN_SESSION_TAG(new Tag("s1", 0)), S2STUDENTS_IN_SESSION_TAG(new Tag("s2", 0)),
    S3STUDENTS_IN_SESSION_TAG(new Tag("s3", 0)), PRIMARY_SUBJECTS_TAG(new Tag("primary", 0)), JUNIOR_SUBJECTS_TAG(new Tag("junior", 0)),
    SCIENCE_SUBJECTS_TAG(new Tag("science", 0)), SOCIAL_SUBJECTS_TAG(new Tag("social", 0)), ARTS_SUBJECTS_TAG(new Tag("arts", 0)),
    ADMIN_EMAIL_TAG(new Tag("email", 0)), NO_REPLY_EMAIL("odunlamizacchaeus544@gmail.com"), NO_REPLY_PASSWORD("me.zkyellow@java"),
    KEYSTORE_EMAIL_SUBJECT("Keystore Password"), SECRET_KEY_ALIAS("secret-key"), DRIVE_ROOT_FOLDER("Students Management System"),
    DRIVE_DATABASE_FOLDER("database"), DRIVE_IMAGE_FOLDER("image"), DRIVE_DOC_FOLDER("doc"), LOCAL_DATABASE_FOLDER("database"),
    LOCAL_DATABASE_RETAINED_FOLDER("database/retained"), LOCAL_IMAGE_FOLDER("image"), LOCAL_DOC_FOLDER("doc");

    private Tag tag; // XML
    private String value;

    Constants(Tag tag){
        this.tag = tag;
    }

    Constants(String value) {
        this.value = value;
    }

    public Tag getTag(){
        return tag;
    }

    public String getValue() {
        return value;
    }

    public static class Tag{

        private String tagName;
        private int i;

        public Tag(String tagName, int i){
            this.i = i;
            this.tagName = tagName;
        }

        public String getTagName(){
            return tagName;
        }

        public int getIndex(){
            return  i;
        }

    }

}