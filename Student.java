public class Student {

    private String id;
    private String name;
    private int age;
    private String grade;

    public Student(String name, int age, String grade, String id) {
        this.name = name;
        this.age = age;
        this.grade = grade;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", ID: " + id + ", Age: " + age + ", Grade: " + grade;
    }

}