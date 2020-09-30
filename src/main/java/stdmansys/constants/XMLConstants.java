package stdmansys.constants;

public enum XMLConstants {

    SCHOOL_NAME(new Tag("name", 0)), SESSION(new Tag("session", 0)), TERM(new Tag("term", 0)), SCHOOL_LOGO(new Tag("logo" , 0)),
    TEACHERS_IN_SESSION(new Tag("teachers", 0)), P1STUDENTS_IN_SESSION(new Tag("p1", 0)), P2STUDENTS_IN_SESSION(new Tag("p2", 0)),
    P3STUDENTS_IN_SESSION(new Tag("p3", 0)), P4STUDENTS_IN_SESSION(new Tag("p4", 0)), P5STUDENTS_IN_SESSION(new Tag("p5", 0)),
    P6STUDENTS_IN_SESSION(new Tag("p6", 0)), J1STUDENTS_IN_SESSION(new Tag("j1", 0)), J2STUDENTS_IN_SESSION(new Tag("j2", 0)),
    J3STUDENTS_IN_SESSION(new Tag("j3", 0)), S1STUDENTS_IN_SESSION(new Tag("s1", 0)), S2STUDENTS_IN_SESSION(new Tag("s2", 0)),
    S3STUDENTS_IN_SESSION(new Tag("s3", 0)), PRIMARY_SUBJECTS(new Tag("primary", 0)), JUNIOR_SUBJECTS(new Tag("junior", 0)),
    SCIENCE_SUBJECTS(new Tag("science", 0)), SOCIAL_SUBJECTS(new Tag("social", 0)), ARTS_SUBJECTS(new Tag("arts", 0));

    private Tag tag;

    XMLConstants(Tag tag){
        this.tag = tag;
    }

    public Tag getTag(){
        return tag;
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