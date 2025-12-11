public class Librarian extends User {
    private static final long serialVersionUID = 1L;

    public Librarian(int id, String name, String email, String phone) {
        super(id, name, email, phone);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (Librarian)", id, name);
    }
}
