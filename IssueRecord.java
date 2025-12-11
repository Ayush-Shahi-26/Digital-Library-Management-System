import java.io.Serializable;
import java.time.LocalDate;

public class IssueRecord implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int bookId;
    private int userId;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate; // null if not returned

    public IssueRecord(int id, int bookId, int userId, LocalDate issueDate, LocalDate dueDate) {
        this.id = id;
        this.bookId = bookId;
        this.userId = userId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = null;
    }

    // getters/setters
    public int getId() { return id; }
    public int getBookId() { return bookId; }
    public int getUserId() { return userId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }

    public boolean isReturned() { return returnDate != null; }

    @Override
    public String toString() {
        return String.format("Issue[%d] Book:%d User:%d Issued:%s Due:%s Returned:%s",
                id, bookId, userId, issueDate, dueDate, returnDate == null ? "-" : returnDate.toString());
    }
}
