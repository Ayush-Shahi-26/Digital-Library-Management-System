import java.awt.*;
import javax.swing.*;

public class MemberFormDialog extends JDialog {
    private JTextField nameF = new JTextField(25);
    private JTextField emailF = new JTextField(25);
    private JTextField phoneF = new JTextField(15);
    private JTextField rollF = new JTextField(15); // for student
    private JComboBox<String> typeBox = new JComboBox<>(new String[]{"Student", "Librarian"});
    private boolean saved = false;
    private User editing;

    public MemberFormDialog(Frame owner, User toEdit) {
        super(owner, true);
        this.editing = toEdit;
        setTitle(toEdit == null ? "Add Member" : "Edit Member");
        init();
        if (editing != null) populate();
        pack();
        setLocationRelativeTo(owner);
    }

    private void init() {
        setLayout(new BorderLayout(8,8));
        JPanel form = new JPanel(new GridLayout(0,2,6,6));
        form.add(new JLabel("Type:")); form.add(typeBox);
        form.add(new JLabel("Name:")); form.add(nameF);
        form.add(new JLabel("Email:")); form.add(emailF);
        form.add(new JLabel("Phone:")); form.add(phoneF);
        form.add(new JLabel("Roll No (students):")); form.add(rollF);
        add(form, BorderLayout.CENTER);

        JPanel btns = new JPanel();
        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");
        btns.add(save); btns.add(cancel);
        add(btns, BorderLayout.SOUTH);

        save.addActionListener(e -> onSave());
        cancel.addActionListener(e -> dispose());
        typeBox.addActionListener(e -> {
            if (typeBox.getSelectedItem().equals("Librarian")) rollF.setEnabled(false);
            else rollF.setEnabled(true);
        });
    }

    private void populate() {
        if (editing instanceof Student) {
            typeBox.setSelectedItem("Student");
            rollF.setText(((Student) editing).getRollNumber());
        } else typeBox.setSelectedItem("Librarian");
        nameF.setText(editing.getName());
        emailF.setText(editing.getEmail());
        phoneF.setText(editing.getPhone());
    }

    private void onSave() {
        String type = (String) typeBox.getSelectedItem();
        String name = nameF.getText().trim();
        if (name.isEmpty()) { JOptionPane.showMessageDialog(this, "Name required"); return; }
        String email = emailF.getText().trim();
        String phone = phoneF.getText().trim();
        String roll = rollF.getText().trim();

        // For edit, caller will handle update; here we just mark saved and let caller read fields
        // We'll store the input temporarily in the editing object if present.
        if (editing != null) {
            editing.setName(name); editing.setEmail(email); editing.setPhone(phone);
            if (editing instanceof Student && type.equals("Student")) {
                ((Student) editing).setRollNumber(roll);
            }
            // if changing type on existing object is complex; keep original type.
        } else {
            // nothing to do here; caller will create new user using the getter methods after dialog closes.
        }
        saved = true;
        dispose();
    }

    public boolean isSaved() { return saved; }

    // getters for new member data when adding (editing==null)
    public String getSelectedType() { return (String) typeBox.getSelectedItem(); }
    public String getNameField() { return nameF.getText().trim(); }
    public String getEmailField() { return emailF.getText().trim(); }
    public String getPhoneField() { return phoneF.getText().trim(); }
    public String getRollField() { return rollF.getText().trim(); }
}
