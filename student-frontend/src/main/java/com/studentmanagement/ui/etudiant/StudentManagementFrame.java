package com.studentmanagement.ui.etudiant;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import com.studentmanagement.model.Etudiant;
import com.studentmanagement.service.ApiException;
import com.studentmanagement.service.StudentService;
import com.studentmanagement.ui.common.MainWindow;
import com.studentmanagement.ui.common.SidebarUtil;

public class StudentManagementFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField idField, firstNameField, lastNameField, emailField;
    private DefaultTableModel tableModel;
    private JTable studentTable;
    private JPanel cardsPanel;
    private MainWindow mainWindow;
    private StudentService studentService;
    private boolean isTableView = true;
    private JDialog addStudentDialog;
    private String responsableId;

    private Color primaryColor = new Color(41, 128, 185);
    private Color sidebarColor = new Color(52, 73, 94);
    private Color backgroundColor = new Color(236, 240, 241);
    private Color cardColor = Color.WHITE;

    public StudentManagementFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.studentService = new StudentService();
        this.responsableId = mainWindow.getCurrentResponsable() != null ? mainWindow.getCurrentResponsable().getId()
                : null;
        setLayout(new BorderLayout());
        setBackground(backgroundColor);

        // Use SidebarUtil to create the sidebar
        JPanel sidebar = SidebarUtil.createSidebar(mainWindow, "Students");
        add(sidebar, BorderLayout.WEST);

        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);

        loadStudents();
    }

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(backgroundColor);
        mainContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel header = createHeader();
        mainContent.add(header, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(backgroundColor);

        JPanel tablePanel = createTableView();
        contentPanel.add(tablePanel, "table");

        cardsPanel = createCardsView();
        JScrollPane cardsScrollPane = new JScrollPane(cardsPanel);
        cardsScrollPane.setBorder(null);
        cardsScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        contentPanel.add(cardsScrollPane, "cards");

        mainContent.add(contentPanel, BorderLayout.CENTER);

        return mainContent;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(backgroundColor);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Gestion des Étudiants");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(sidebarColor);
        header.add(titleLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(backgroundColor);

        JButton addButton = createStyledButton("➕ Ajouter Étudiant", primaryColor);
        addButton.addActionListener(_ -> showAddStudentDialog());
        buttonPanel.add(addButton);

        JButton viewToggleButton = createStyledButton(isTableView ? "🔲 Vue Cartes" : "📋 Vue Tableau",
                new Color(46, 204, 113));
        viewToggleButton.addActionListener(_ -> toggleView(viewToggleButton));
        buttonPanel.add(viewToggleButton);

        header.add(buttonPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createTableView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);

        String[] columns = { "ID", "Prénom", "Nom", "Email", "Actions" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Arial", Font.PLAIN, 14));
        studentTable.setRowHeight(40);
        studentTable.setBackground(cardColor);
        studentTable.setSelectionBackground(new Color(230, 240, 250));
        studentTable.setGridColor(new Color(220, 220, 220));

        studentTable.getColumn("Actions").setCellRenderer(new ButtonRenderer());
        studentTable.getColumn("Actions").setCellEditor(new ButtonEditor());

        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        scrollPane.getViewport().setBackground(cardColor);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCardsView() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        return panel;
    }

    private void toggleView(JButton toggleButton) {
        isTableView = !isTableView;
        toggleButton.setText(isTableView ? "🔲 Vue Cartes" : "📋 Vue Tableau");

        CardLayout cl = (CardLayout) ((JPanel) ((JPanel) getComponent(1)).getComponent(1)).getLayout();
        cl.show((JPanel) ((JPanel) getComponent(1)).getComponent(1), isTableView ? "table" : "cards");

        if (!isTableView) {
            updateCardsView();
        }
    }

    private void updateCardsView() {
        cardsPanel.removeAll();

        for (Etudiant etudiant : studentService.getAllEtudiants(responsableId)) {
            JPanel card = createStatCard(etudiant);
            cardsPanel.add(card);
            cardsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel createStatCard(Etudiant etudiant) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                new SoftBevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(15, 10, 15, 10)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(cardColor);

        JLabel nameLabel = new JLabel(etudiant.getPrenom() + " " + etudiant.getNom());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        nameLabel.setForeground(sidebarColor);

        JLabel emailLabel = new JLabel(etudiant.getEmail());
        emailLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        emailLabel.setForeground(new Color(100, 100, 100));

        JLabel idLabel = new JLabel("ID: " + etudiant.getId());
        idLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        idLabel.setForeground(new Color(150, 150, 150));

        infoPanel.add(nameLabel);
        infoPanel.add(emailLabel);
        infoPanel.add(idLabel);

        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setBackground(cardColor);

        JButton editButton = createSmallButton("Modifier", new Color(230, 126, 34));
        JButton deleteButton = createSmallButton("Supprimer", new Color(231, 76, 60));

        editButton.addActionListener(_ -> editStudent(etudiant));
        deleteButton.addActionListener(_ -> deleteStudent(etudiant.getId()));

        actionPanel.add(editButton);
        actionPanel.add(deleteButton);

        card.add(infoPanel, BorderLayout.CENTER);
        card.add(actionPanel, BorderLayout.EAST);

        return card;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        return button;
    }

    private JButton createSmallButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(30, 30));

        return button;
    }

    private void showAddStudentDialog() {
        addStudentDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter un Étudiant", true);
        addStudentDialog.setLayout(new BorderLayout());
        addStudentDialog.setSize(650, 500);
        addStudentDialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(cardColor);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel titleLabel = new JLabel("Nouveau Étudiant");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(primaryColor);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("ID"), gbc);
        gbc.gridx = 1;
        idField = new JTextField(20);
        idField.setFont(new Font("Arial", Font.PLAIN, 14));
        idField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        formPanel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Prénom"), gbc);
        gbc.gridx = 1;
        firstNameField = new JTextField(20);
        firstNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        firstNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        formPanel.add(firstNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Nom"), gbc);
        gbc.gridx = 1;
        lastNameField = new JTextField(20);
        lastNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        lastNameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        formPanel.add(lastNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Email"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        formPanel.add(emailField, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(cardColor);

        JButton saveButton = createStyledButton("Enregistrer", primaryColor);
        JButton cancelButton = createStyledButton("Annuler", new Color(149, 165, 166));

        saveButton.addActionListener(_ -> saveStudent());
        cancelButton.addActionListener(_ -> addStudentDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        addStudentDialog.add(formPanel, BorderLayout.CENTER);
        addStudentDialog.add(buttonPanel, BorderLayout.SOUTH);
        addStudentDialog.setVisible(true);
    }

    private void saveStudent() {
        String id = idField.getText();
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();

        if (id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(addStudentDialog, "Veuillez remplir tous les champs nécessaires.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        Etudiant etudiant = new Etudiant();
        etudiant.setId(id);
        etudiant.setPrenom(firstName);
        etudiant.setNom(lastName);
        etudiant.setEmail(email);

        try {
            studentService.addEtudiant(etudiant, responsableId);
            if (isTableView) {
                tableModel.addRow(new Object[] { id, firstName, lastName, email, "Actions" });
            } else {
                updateCardsView();
            }
            clearFields();
            addStudentDialog.dispose();
            JOptionPane.showMessageDialog(this, "Étudiant ajouté avec succès !");
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editStudent(Etudiant etudiant) {
        JOptionPane.showMessageDialog(this, "Fonction d'édition à implémenter");
    }

    private void deleteStudent(String id) {
        int result = JOptionPane.showConfirmDialog(this, "Êtes-vous sûr de vouloir supprimer cet étudiant?",
                "Confirmation", JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            try {
                studentService.deleteEtudiant(id);
                loadStudents();
                JOptionPane.showMessageDialog(this, "Étudiant supprimé avec succès !");
            } catch (ApiException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void loadStudents() {
        if (tableModel != null) {
            tableModel.setRowCount(0);
            try {
                for (Etudiant etudiant : studentService.getAllEtudiants(responsableId)) {
                    tableModel.addRow(new Object[] {
                            etudiant.getId(),
                            etudiant.getPrenom(),
                            etudiant.getNom(),
                            etudiant.getEmail(),
                            "Actions"
                    });
                }
            } catch (ApiException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (!isTableView) {
            updateCardsView();
        }
    }

    private void clearFields() {
        if (idField != null)
            idField.setText("");
        if (firstNameField != null)
            firstNameField.setText("");
        if (lastNameField != null)
            lastNameField.setText("");
        if (emailField != null)
            emailField.setText("");
    }

    class ButtonRenderer extends JLabel implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setHorizontalAlignment(CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("✏️ 🗑️");
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;

        public ButtonEditor() {
            super(new JTextField());
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(_ -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                int column) {
            label = (value == null) ? "" : value.toString();
            button.setText("✏️ 🗑️");
            isPushed = true;
            currentRow = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                String id = tableModel.getValueAt(currentRow, 0).toString();
                int choice = JOptionPane.showOptionDialog(button,
                        "Que voulez-vous faire?",
                        "Actions",
                        JOptionPane.DEFAULT_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        new String[] { "Modifier", "Supprimer", "Annuler" },
                        "Modifier");

                if (choice == 0) {
                    Etudiant etudiant = new Etudiant();
                    etudiant.setId(tableModel.getValueAt(currentRow, 0).toString());
                    etudiant.setPrenom(tableModel.getValueAt(currentRow, 1).toString());
                    etudiant.setNom(tableModel.getValueAt(currentRow, 2).toString());
                    etudiant.setEmail(tableModel.getValueAt(currentRow, 3).toString());
                    editStudent(etudiant);
                } else if (choice == 1) {
                    deleteStudent(id);
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}