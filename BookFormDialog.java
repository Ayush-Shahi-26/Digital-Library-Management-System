import java.awt.*;
import javax.swing.*;

public class BookFormDialog extends JDialog {
    private JTextField titleF = new JTextField(30);
    private JTextField authorF = new JTextField(25);
    private JTextField isbnF = new JTextField(20);
    private JTextField totalF = new JTextField(5);
    private JTextField categoryF = new JTextField(20);
    private boolean saved = false;
    private Book editing;

    public BookFormDialog(Frame owner, Book bookToEdit) {
        super(owner, true);
        setTitle(bookToEdit == null ? "Add Book" : "Edit Book");
        this.editing = bookToEdit;
        init();
        if (editing != null) populate();
        pack();
        setLocationRelativeTo(owner);
    }

    private void init() {
        setLayout(new BorderLayout(8,8));
        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        form.add(new JLabel("Title:")); form.add(titleF);
        form.add(new JLabel("Author:")); form.add(authorF);
        form.add(new JLabel("ISBN:")); form.add(isbnF);
        form.add(new JLabel("Total copies:")); form.add(totalF);
        form.add(new JLabel("Category:")); form.add(categoryF);
        add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel();
        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        btns.add(save); btns.add(cancel);
        add(btns, BorderLayout.SOUTH);

        save.addActionListener(e -> onSave());
        cancel.addActionListener(e -> dispose());
    }

    private void populate() {
        titleF.setText(editing.getTitle());
        authorF.setText(editing.getAuthor());
        isbnF.setText(editing.getIsbn());
        totalF.setText(String.valueOf(editing.getTotalCopies()));
        categoryF.setText(editing.getCategory());
    }

    private void onSave() {
        try {
            String title = titleF.getText().trim();
            if (title.isEmpty()) { JOptionPane.showMessageDialog(this, "Title required"); return; }
            String author = authorF.getText().trim();
            String isbn = isbnF.getText().trim();
            int total = Integer.parseInt(totalF.getText().trim());
            String cat = categoryF.getText().trim();

            if (editing == null) {
                // handled by caller
            } else {
                editing.setTitle(title);
                editing.setAuthor(author);
                editing.setIsbn(isbn);
                editing.setTotalCopies(total);
                editing.setCategory(cat);
            }
            saved = true;
            dispose();
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "Total copies must be a number");
        }
    }

    public boolean isSaved() { return saved; }
}
