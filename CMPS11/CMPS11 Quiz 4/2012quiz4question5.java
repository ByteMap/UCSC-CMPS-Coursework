class PersonTest {
    public static void main(String[] args) {
        Person p1 = new Person("Able");
        Person p2 = new Person("Bonnie");
        Person p3 = new Person("Charlie");
        System.out.println(p1);// Able:1
        System.out.println(p2);// Bonnie:2
        System.out.println(p3);// Charlie:3
    }
}
class Person {
    String name;
    int employeeNumber;
    static int numEmployees;
    public String toString() {
        return name + ":" + employeeNumber;
    }
    Person(String name) {
        this.name = name;
        numEmployees++;
        employeeNumber = numEmployees;
    }
}
