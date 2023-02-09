class CourseTest {
    public static void main(String[] args) {
        Course course = new Course();
        course.title = "Intermediate Programming";
        course.catNumber = 11;
        course.schedNumber = 73035;
    }
}
class Course {
    String title;
    int catNumber;
    int schedNumber;
}