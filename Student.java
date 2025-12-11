public class Student extends User {
    private static final long serialVersionUID = 1L;
    private String rollNumber;

    public Student(int id, String name, String email, String phone, String rollNumber) {
        super(id, name, email, phone);
        this.rollNumber = rollNumber;
    }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    @Override
    public String toString() {
        return String.format("[%d] %s (Student) R:%s", id, name, rollNumber == null ? "-" : rollNumber);
    }
}
