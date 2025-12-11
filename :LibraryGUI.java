import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class LibraryGUI extends JFrame {

    private Library library; 
    private JTable booksTable, usersTable, issuesTable;
    private DefaultTableModel booksModel, usersModel, issuesModel;
    private DateTimeFormatter dtf = DateTimeFormatter.ISO_LOCAL_DATE;

    public LibraryGUI() {
        super("Digital Library Management System");
        library = new Library();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        initUI();

        // save on close
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                library.saveAll();
                System.exit(0);
            }
        });
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", createBooksPanel());
        tabs.addTab("Members", createMembersPanel());
        tabs.addTab("Issue / Return", createIssuesPanel());
        add(tabs, BorderLayout.CENTER);

        // load initial data
        refreshBooks();
        refreshUsers();
        refreshIssues();
    }

    // ---------------- Books Panel ----------------
    private JPanel createBooksPanel() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField search = new JTextField(60);
        JButton searchBtn = new JButton("Search");
        JButton add = new JButton("Add Book");
        JButton edit = new JButton("Edit Book");
        JButton del = new JButton("Delete Book");

        top.add(new JLabel("Search:"));
        top.add(search);
        top.add(searchBtn);
        top.add(add);
        top.add(edit);
        top.add(del);
        p.add(top, BorderLayout.NORTH);

        booksModel = new DefaultTableModel(
            new Object[]{"ID","Title","Author","ISBN","Total","Available","Category"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        booksTable = new JTable(booksModel);
        p.add(new JScrollPane(booksTable), BorderLayout.CENTER);

        // search action
        searchBtn.addActionListener(e -> {
            String q = search.getText().trim().toLowerCase();
            booksModel.setRowCount(0);
            for (Book b : library.getBooks()) {
                if (q.isEmpty() || b.getTitle().toLowerCase().contains(q)
                        || b.getAuthor().toLowerCase().contains(q)
                        || b.getIsbn().toLowerCase().contains(q)) {
                    booksModel.addRow(new Object[]{
                        b.getId(), b.getTitle(), b.getAuthor(),
                        b.getIsbn(), b.getTotalCopies(),
                        b.getAvailableCopies(), b.getCategory()
                    });
                }
            }
        });

        // add book
        add.addActionListener(e -> {
            String title = JOptionPane.showInputDialog(this, "Enter Title:");
            if (title == null || title.trim().isEmpty()) return;
            String author = JOptionPane.showInputDialog(this, "Enter Author:");
            String isbn = JOptionPane.showInputDialog(this, "Enter ISBN:");
            String totStr = JOptionPane.showInputDialog(this, "Total Copies (number):", "1");
            int tot = 1;
            try { tot = Integer.parseInt(totStr); } catch (Exception ex) { tot = 1; }
            String cat = JOptionPane.showInputDialog(this, "Category:");
            library.addBook(
                title.trim(),
                author == null ? "" : author.trim(),
                isbn == null ? "" : isbn.trim(),
                tot,
                cat == null ? "" : cat.trim()
            );
            refreshBooks();
        });

        // edit book
        edit.addActionListener(e -> {
            int r = booksTable.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this, "Select a book to edit");
                return;
            }
            int id = (int) booksModel.getValueAt(r,0);
            Book b = library.findBookById(id);
            if (b == null) return;

            String title = JOptionPane.showInputDialog(this, "Title:", b.getTitle());
            if (title == null) return;
            String author = JOptionPane.showInputDialog(this, "Author:", b.getAuthor());
            String isbn = JOptionPane.showInputDialog(this, "ISBN:", b.getIsbn());
            String totStr = JOptionPane.showInputDialog(this, "Total copies:", String.valueOf(b.getTotalCopies()));
            int tot = b.getTotalCopies();
            try { tot = Integer.parseInt(totStr); } catch (Exception ex) {}
            String cat = JOptionPane.showInputDialog(this, "Category:", b.getCategory());

            b.setTitle(title);
            b.setAuthor(author);
            b.setIsbn(isbn);
            b.setTotalCopies(tot);
            b.setCategory(cat == null ? "" : cat);
            library.updateBook(b);
            refreshBooks();
        });

        // delete book
        del.addActionListener(e -> {
            int r = booksTable.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this, "Select a book to delete");
                return;
            }
            int id = (int) booksModel.getValueAt(r,0);
            int c = JOptionPane.showConfirmDialog(this, "Delete book ID " + id + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                library.deleteBook(id);
                refreshBooks();
            }
        });

        return p;
    }

    private void refreshBooks() {
        booksModel.setRowCount(0);
        for (Book b : library.getBooks()) {
            booksModel.addRow(new Object[]{
                b.getId(), b.getTitle(), b.getAuthor(),
                b.getIsbn(), b.getTotalCopies(),
                b.getAvailableCopies(), b.getCategory()
            });
        }
    }

    // ---------------- Members Panel ----------------
    private JPanel createMembersPanel() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton add = new JButton("Add Member");
        JButton edit = new JButton("Edit Member");
        JButton del = new JButton("Delete Member");
        top.add(add);
        top.add(edit);
        top.add(del);
        p.add(top, BorderLayout.NORTH);

        usersModel = new DefaultTableModel(
            new Object[]{"ID","Name","Type","Email","Phone","Extra"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        usersTable = new JTable(usersModel);
        p.add(new JScrollPane(usersTable), BorderLayout.CENTER);

        add.addActionListener(e -> {
            MemberFormDialog d = new MemberFormDialog(this, null);
            d.setVisible(true);
            if (d.isSaved()) {
                String type = d.getSelectedType();
                String name = d.getNameField();
                String email = d.getEmailField();
                String phone = d.getPhoneField();
                String roll = d.getRollField();
                if (type.equals("Student"))
                    library.addStudent(name, email, phone, roll);
                else
                    library.addLibrarian(name, email, phone);
                refreshUsers();
            }
        });

        edit.addActionListener(e -> {
            int r = usersTable.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this, "Select a member to edit");
                return;
            }
            int id = (int) usersModel.getValueAt(r,0);
            User u = library.findUserById(id);
            if (u == null) return;
            MemberFormDialog d = new MemberFormDialog(this, u);
            d.setVisible(true);
            if (d.isSaved()) {
                library.updateUser(u);
                refreshUsers();
            }
        });

        del.addActionListener(e -> {
            int r = usersTable.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this, "Select a member to delete");
                return;
            }
            int id = (int) usersModel.getValueAt(r,0);
            int c = JOptionPane.showConfirmDialog(this, "Delete member ID " + id + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) {
                library.deleteUser(id);
                refreshUsers();
            }
        });

        return p;
    }

    private void refreshUsers() {
        usersModel.setRowCount(0);
        for (User u : library.getUsers()) {
            String type = (u instanceof Student) ? "Student" : "Librarian";
            String extra = (u instanceof Student) ? ((Student) u).getRollNumber() : "-";
            usersModel.addRow(new Object[]{
                u.getId(), u.getName(), type,
                u.getEmail(), u.getPhone(), extra
            });
        }
    }

    // ---------------- Issues Panel ----------------
    private JPanel createIssuesPanel() {
        JPanel p = new JPanel(new BorderLayout(8,8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton issueBtn = new JButton("Issue Book");
        JButton returnBtn = new JButton("Return Selected");
        JButton refreshBtn = new JButton("Refresh");
        top.add(issueBtn);
        top.add(returnBtn);
        top.add(refreshBtn);
        p.add(top, BorderLayout.NORTH);

        issuesModel = new DefaultTableModel(
            new Object[]{"IssueID","BookID","Book Title","UserID","User Name","IssueDate","DueDate","Returned"}, 0) {
            public boolean isCellEditable(int r, int c){ return false; }
        };
        issuesTable = new JTable(issuesModel);
        p.add(new JScrollPane(issuesTable), BorderLayout.CENTER);

        issueBtn.addActionListener(e -> {
            List<Book> books = library.getBooks();
            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No books available");
                return;
            }
            String[] bookOptions = books.stream().map(Book::toString).toArray(String[]::new);
            String chosenBook = (String) JOptionPane.showInputDialog(
                this, "Select book", "Issue", JOptionPane.PLAIN_MESSAGE, null, bookOptions, bookOptions[0]);
            if (chosenBook == null) return;
            int bId = parseIdFromBrackets(chosenBook);

            List<User> users = library.getUsers();
            if (users.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No members available");
                return;
            }
            String[] userOptions = users.stream().map(User::toString).toArray(String[]::new);
            String chosenUser = (String) JOptionPane.showInputDialog(
                this, "Select user", "Issue", JOptionPane.PLAIN_MESSAGE, null, userOptions, userOptions[0]);
            if (chosenUser == null) return;
            int uId = parseIdFromBrackets(chosenUser);

            String daysStr = JOptionPane.showInputDialog(this, "Issue for how many days? (default 14)", "14");
            int days = 14;
            try { days = Integer.parseInt(daysStr); } catch (Exception ex) { days = 14; }

            try {
                IssueRecord rec = library.issueBook(bId, uId, LocalDate.now(), LocalDate.now().plusDays(days));
                JOptionPane.showMessageDialog(this, "Issued! Issue ID: " + rec.getId());
                refreshBooks();
                refreshIssues();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Issue failed: " + ex.getMessage());
            }
        });

        returnBtn.addActionListener(e -> {
            int r = issuesTable.getSelectedRow();
            if (r < 0) {
                JOptionPane.showMessageDialog(this, "Select an active issue row to return");
                return;
            }
            int issueId = (int) issuesModel.getValueAt(r, 0);
            String returned = (String) issuesModel.getValueAt(r, 7);
            if (!"-".equals(returned)) {
                JOptionPane.showMessageDialog(this, "Already returned");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Return issue ID " + issueId + " ?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;
            try {
                library.returnBook(issueId, LocalDate.now());
                JOptionPane.showMessageDialog(this, "Returned.");
                refreshBooks();
                refreshIssues();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Return failed: " + ex.getMessage());
            }
        });

        refreshBtn.addActionListener(e -> refreshIssues());
        return p;
    }

    private int parseIdFromBrackets(String s) {
        if (s == null) return -1;
        int start = s.indexOf('[');
        int end = s.indexOf(']');
        if (start >= 0 && end > start) {
            try { return Integer.parseInt(s.substring(start + 1, end)); } catch (Exception e) {}
        }
        return -1;
    }

    private void refreshIssues() {
        issuesModel.setRowCount(0);
        for (IssueRecord rec : library.getIssues()) {
            Book b = library.findBookById(rec.getBookId());
            User u = library.findUserById(rec.getUserId());
            String bTitle = (b == null) ? "N/A" : b.getTitle();
            String uName = (u == null) ? "N/A" : u.getName();
            issuesModel.addRow(new Object[]{
                rec.getId(), rec.getBookId(), bTitle,
                rec.getUserId(), uName,
                rec.getIssueDate().format(dtf),
                rec.getDueDate().format(dtf),
                (rec.getReturnDate() == null ? "-" : rec.getReturnDate().format(dtf))
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryGUI gui = new LibraryGUI();
            gui.setVisible(true);
        });
    }
}
