import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Library {
    private List<Book> books;
    private List<User> users;
    private List<IssueRecord> issues;

    private final File booksFile = new File("books.dat");
    private final File usersFile = new File("users.dat");
    private final File issuesFile = new File("issues.dat");

    public Library() {
        loadAll();
    }

    // ---------- persistence ----------
    @SuppressWarnings("unchecked")
    private <T> List<T> readList(File f) {
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object o = ois.readObject();
            if (o instanceof List) return (List<T>) o;
        } catch (Exception e) {
            System.err.println("Failed to read " + f.getName() + ": " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private void writeList(File f, List<?> list) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
            oos.writeObject(list);
        } catch (Exception e) {
            System.err.println("Failed to write " + f.getName() + ": " + e.getMessage());
        }
    }

    public void loadAll() {
        books = readList(booksFile);
        users = readList(usersFile);
        issues = readList(issuesFile);
    }

    public void saveAll() {
        writeList(booksFile, books);
        writeList(usersFile, users);
        writeList(issuesFile, issues);
    }

    // ---------- id helpers ----------
    private int nextBookId() {
        int max = 0;
        for (Book b : books) if (b.getId() > max) max = b.getId();
        return max + 1;
    }
    private int nextUserId() {
        int max = 0;
        for (User u : users) if (u.getId() > max) max = u.getId();
        return max + 1;
    }
    private int nextIssueId() {
        int max = 0;
        for (IssueRecord r : issues) if (r.getId() > max) max = r.getId();
        return max + 1;
    }

    // ---------- book operations ----------
    public Book addBook(String title, String author, String isbn, int totalCopies, String category) {
        Book b = new Book(nextBookId(), title, author, isbn, totalCopies, category);
        books.add(b);
        saveAll();
        return b;
    }

    public List<Book> getBooks() { return books; }

    public void updateBook(Book book) {
        Optional<Book> opt = books.stream().filter(b -> b.getId() == book.getId()).findFirst();
        if (opt.isPresent()) {
            Book existing = opt.get();
            existing.setTitle(book.getTitle());
            existing.setAuthor(book.getAuthor());
            existing.setIsbn(book.getIsbn());
            existing.setTotalCopies(book.getTotalCopies());
            existing.setAvailableCopies(book.getAvailableCopies());
            existing.setCategory(book.getCategory());
            saveAll();
        }
    }

    public void deleteBook(int bookId) {
        books.removeIf(b -> b.getId() == bookId);
        // also remove any non-returned issues referencing it — simple approach: remove those issues
        issues.removeIf(r -> r.getBookId() == bookId);
        saveAll();
    }

    public Book findBookById(int id) {
        return books.stream().filter(b -> b.getId() == id).findFirst().orElse(null);
    }

    // ---------- user operations ----------
    public User addStudent(String name, String email, String phone, String roll) {
        Student s = new Student(nextUserId(), name, email, phone, roll);
        users.add(s);
        saveAll();
        return s;
    }
    public User addLibrarian(String name, String email, String phone) {
        Librarian l = new Librarian(nextUserId(), name, email, phone);
        users.add(l);
        saveAll();
        return l;
    }

    public List<User> getUsers() { return users; }

    public void updateUser(User u) {
        Optional<User> opt = users.stream().filter(x -> x.getId() == u.getId()).findFirst();
        if (opt.isPresent()) {
            User existing = opt.get();
            existing.setName(u.getName());
            existing.setEmail(u.getEmail());
            existing.setPhone(u.getPhone());
            if (existing instanceof Student && u instanceof Student) {
                ((Student) existing).setRollNumber(((Student) u).getRollNumber());
            }
            saveAll();
        }
    }

    public void deleteUser(int userId) {
        users.removeIf(u -> u.getId() == userId);
        // remove issues for that user
        issues.removeIf(r -> r.getUserId() == userId);
        saveAll();
    }

    public User findUserById(int id) {
        return users.stream().filter(u -> u.getId() == id).findFirst().orElse(null);
    }

    // ---------- issue / return ----------
    public IssueRecord issueBook(int bookId, int userId, LocalDate issueDate, LocalDate dueDate) throws Exception {
        Book b = findBookById(bookId);
        if (b == null) throw new Exception("Book not found");
        if (!b.isAvailable()) throw new Exception("No copies available");
        b.issueOne();
        IssueRecord rec = new IssueRecord(nextIssueId(), bookId, userId, issueDate, dueDate);
        issues.add(rec);
        saveAll();
        return rec;
    }

    public void returnBook(int issueId, LocalDate returnDate) throws Exception {
        IssueRecord rec = issues.stream().filter(r -> r.getId() == issueId).findFirst().orElse(null);
        if (rec == null) throw new Exception("Issue record not found");
        if (rec.isReturned()) throw new Exception("Already returned");
        rec.setReturnDate(returnDate);
        Book b = findBookById(rec.getBookId());
        if (b != null) b.returnOne();
        saveAll();
    }

    public List<IssueRecord> getIssues() { return issues; }
    public List<IssueRecord> getActiveIssues() {
        List<IssueRecord> out = new ArrayList<>();
        for (IssueRecord r : issues) if (!r.isReturned()) out.add(r);
        return out;
    }
}
