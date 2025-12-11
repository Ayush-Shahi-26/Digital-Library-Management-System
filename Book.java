import java.io.Serializable;

public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String author;
    private String isbn;
    private int totalCopies;
    private int availableCopies;
    private String category;

    public Book(int id, String title, String author, String isbn, int totalCopies, String category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.totalCopies = Math.max(1, totalCopies);
        this.availableCopies = this.totalCopies;
        this.category = category;
    }

    // getters / setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public int getTotalCopies() { return totalCopies; }
    public void setTotalCopies(int totalCopies) {
        int diff = totalCopies - this.totalCopies;
        this.totalCopies = Math.max(1, totalCopies);
        this.availableCopies = Math.max(0, this.availableCopies + diff);
    }
    public int getAvailableCopies() { return availableCopies; }
    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isAvailable() { return availableCopies > 0; }

    public boolean issueOne() {
        if (availableCopies > 0) { availableCopies--; return true; }
        return false;
    }

    public void returnOne() { if (availableCopies < totalCopies) availableCopies++; }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s (%d/%d)", id, title, author, availableCopies, totalCopies);
    }
}
