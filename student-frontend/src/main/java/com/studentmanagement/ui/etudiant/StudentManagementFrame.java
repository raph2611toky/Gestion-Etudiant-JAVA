package com.studentmanagement.ui.etudiant;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import com.studentmanagement.model.Etudiant;
import com.studentmanagement.model.Niveau;
import com.studentmanagement.model.Parcours;
import com.studentmanagement.service.ApiException;
import com.studentmanagement.service.StudentService;
import com.studentmanagement.ui.common.MainWindow;
import com.studentmanagement.ui.common.SidebarUtil;
import com.studentmanagement.ui.common.ModernComponents;

public class StudentManagementFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField matriculeField, firstNameField, lastNameField, emailField, adresseField;
    private JComboBox<Niveau> niveauComboBox;
    private JComboBox<Parcours> parcoursComboBox;
    private JComboBox<String> niveauFilterComboBox;
    private JComboBox<String> parcoursFilterComboBox;
    private DefaultTableModel tableModel;
    private JTable studentTable;
    private JPanel cardsPanel;
    private MainWindow mainWindow;
    private StudentService studentService;
    private boolean isTableView = true;
    private JDialog studentDialog;
    private String responsableId;
    private File selectedPhoto;
    private JLabel photoPreviewLabel;
    private JTextField searchField;
    private List<Etudiant> allStudents = new ArrayList<>();
    private List<Niveau> allNiveaux = new ArrayList<>();
    private List<Parcours> allParcours = new ArrayList<>();

    // Modern Color Palette - Enhanced
    private static final Color PRIMARY_COLOR = new Color(99, 102, 241);
    private static final Color SECONDARY_COLOR = new Color(139, 92, 246);
    private static final Color SUCCESS_COLOR = new Color(34, 197, 94);
    private static final Color WARNING_COLOR = new Color(251, 146, 60);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color HOVER_COLOR = new Color(241, 245, 249);

    public StudentManagementFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.studentService = new StudentService();
        this.responsableId = mainWindow.getCurrentResponsable() != null ? mainWindow.getCurrentResponsable().getId()
                : null;
        if (this.responsableId != null) {
            this.studentService.setJwtToken(mainWindow.getCurrentResponsable().getToken());
        } else {
            showErrorNotification("Erreur: Responsable non identifié.");
        }

        initializeUI();
        loadNiveauxAndParcours();
        loadStudents();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel sidebar = SidebarUtil.createSidebar(mainWindow, "Students");
        add(sidebar, BorderLayout.WEST);

        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);

        loadNiveauxAndParcours();
    }

    private void loadNiveauxAndParcours() {
        try {
            List<Niveau> niveaux = studentService.getAllNiveaux();
            allNiveaux = niveaux != null ? new ArrayList<>(niveaux) : new ArrayList<>();
            if (allNiveaux.isEmpty()) {
                showWarningNotification("Aucun niveau trouvé. Veuillez ajouter des niveaux dans Paramètres.");
            }
        } catch (ApiException ex) {
            showErrorNotification("Erreur lors du chargement des niveaux: " + ex.getMessage());
            allNiveaux = new ArrayList<>();
        }

        try {
            List<Parcours> parcours = studentService.getAllParcours();
            allParcours = parcours != null ? new ArrayList<>(parcours) : new ArrayList<>();
            if (allParcours.isEmpty()) {
                showWarningNotification("Aucun parcours trouvé. Veuillez ajouter des parcours dans Paramètres.");
            }
        } catch (ApiException ex) {
            showErrorNotification("Erreur lors du chargement des parcours: " + ex.getMessage());
            allParcours = new ArrayList<>();
        }
    }

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel headerSection = createEnhancedHeader();
        mainContent.add(headerSection, BorderLayout.NORTH);

        JPanel contentPanel = createContentPanel();
        mainContent.add(contentPanel, BorderLayout.CENTER);
        loadNiveauxAndParcours();

        return mainContent;
    }

    private JPanel createEnhancedHeader() {
        JPanel headerSection = new JPanel(new BorderLayout());
        headerSection.setBackground(BACKGROUND_COLOR);
        headerSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("🎓 Gestion des Étudiants");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel breadcrumbLabel = new JLabel("Dashboard > Gestion des Étudiants");
        breadcrumbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        breadcrumbLabel.setForeground(TEXT_SECONDARY);

        titleSection.add(titleLabel);
        titleSection.add(Box.createVerticalStrut(5));
        titleSection.add(breadcrumbLabel);

        JPanel controlsSection = createControlsSection();
        headerSection.add(titleSection, BorderLayout.WEST);
        headerSection.add(controlsSection, BorderLayout.EAST);

        return headerSection;
    }

    private JPanel createControlsSection() {
        JPanel controlsSection = new JPanel();
        controlsSection.setLayout(new BoxLayout(controlsSection, BoxLayout.Y_AXIS));
        controlsSection.setBackground(BACKGROUND_COLOR);

        JPanel filtersPanel = createFiltersPanel();
        controlsSection.add(filtersPanel);
        controlsSection.add(Box.createVerticalStrut(15));

        JPanel buttonsPanel = createActionButtonsPanel();
        controlsSection.add(buttonsPanel);

        return controlsSection;
    }

    private JPanel createFiltersPanel() {
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        filtersPanel.setBackground(BACKGROUND_COLOR);

        searchField = createEnhancedSearchField();
        filtersPanel.add(searchField);

        niveauFilterComboBox = createEnhancedComboBox("TOUS LES NIVEAUX");
        if (allNiveaux != null) {
            for (Niveau niveau : new ArrayList<>(allNiveaux)) {
                if (niveau != null && niveau.getNom() != null) {
                    niveauFilterComboBox.addItem(niveau.getNom());
                }
            }
        }
        niveauFilterComboBox.addActionListener(e -> filterStudents());
        filtersPanel.add(niveauFilterComboBox);

        parcoursFilterComboBox = createEnhancedComboBox("TOUS LES PARCOURS");
        if (allParcours != null) {
            for (Parcours parcours : new ArrayList<>(allParcours)) {
                if (parcours != null && parcours.getNom() != null) {
                    parcoursFilterComboBox.addItem(parcours.getNom());
                }
            }
        }
        parcoursFilterComboBox.addActionListener(e -> filterStudents());
        filtersPanel.add(parcoursFilterComboBox);

        return filtersPanel;
    }

    private JTextField createEnhancedSearchField() {
        JTextField searchField = new JTextField("🔍 Rechercher des étudiants...") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(280, 42));
        searchField.setBackground(CARD_COLOR);
        searchField.setForeground(TEXT_SECONDARY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new ModernBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("🔍 Rechercher des étudiants...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_PRIMARY);
                }
                searchField.setBorder(BorderFactory.createCompoundBorder(
                        new ModernBorder(PRIMARY_COLOR),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("🔍 Rechercher des étudiants...");
                    searchField.setForeground(TEXT_SECONDARY);
                }
                searchField.setBorder(BorderFactory.createCompoundBorder(
                        new ModernBorder(BORDER_COLOR),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            }
        });

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterStudents();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterStudents();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterStudents();
            }
        });

        return searchField;
    }

    private JComboBox<String> createEnhancedComboBox(String defaultItem) {
        JComboBox<String> comboBox = new JComboBox<String>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        comboBox.addItem(defaultItem);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setPreferredSize(new Dimension(180, 42));
        comboBox.setBackground(CARD_COLOR);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new ModernBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        return comboBox;
    }

    private JPanel createActionButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonsPanel.setBackground(BACKGROUND_COLOR);

        JButton viewToggleButton = createEnhancedButton(
                isTableView ? "📋 Vue Cartes" : "📊 Vue Tableau",
                SECONDARY_COLOR,
                false);
        viewToggleButton.addActionListener(e -> toggleView(viewToggleButton));

        JButton addButton = createEnhancedButton("➕ Ajouter Étudiant", PRIMARY_COLOR, true);
        addButton.addActionListener(e -> showStudentFormDialog(null));

        buttonsPanel.add(viewToggleButton);
        buttonsPanel.add(addButton);

        return buttonsPanel;
    }

    private JButton createEnhancedButton(String text, Color bgColor, boolean isPrimary) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentColor;
                if (getModel().isPressed()) {
                    currentColor = bgColor.darker();
                } else if (getModel().isRollover()) {
                    currentColor = isPrimary ? bgColor.brighter() : HOVER_COLOR;
                } else {
                    currentColor = isPrimary ? bgColor : CARD_COLOR;
                }

                g2d.setColor(currentColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);

                if (!isPrimary) {
                    g2d.setColor(BORDER_COLOR);
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                }

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(isPrimary ? Color.WHITE : TEXT_PRIMARY);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        return button;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);

        JPanel tablePanel = createEnhancedTableView();
        contentPanel.add(tablePanel, "table");

        cardsPanel = createEnhancedCardsView();
        JScrollPane cardsScrollPane = createEnhancedScrollPane(cardsPanel);
        contentPanel.add(cardsScrollPane, "cards");

        return contentPanel;
    }

    private JPanel createEnhancedTableView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        String[] columns = { "Matricule", "Prénom", "Nom", "Email", "Adresse", "Niveau", "Parcours", "Photo",
                "Actions" };
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8;
            }
        };

        studentTable = createEnhancedTable();

        studentTable.getColumn("Photo").setCellRenderer(new EnhancedPhotoCellRenderer());
        studentTable.getColumn("Photo").setPreferredWidth(70);
        studentTable.getColumn("Photo").setMaxWidth(70);
        studentTable.getColumn("Photo").setMinWidth(70);

        studentTable.getColumn("Actions").setCellRenderer(new EnhancedButtonRenderer());
        studentTable.getColumn("Actions").setCellEditor(new EnhancedButtonEditor());
        studentTable.getColumn("Actions").setPreferredWidth(160);

        JScrollPane scrollPane = createEnhancedScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JTable createEnhancedTable() {
        JTable table = new JTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);

                if (!isRowSelected(row)) {
                    comp.setBackground(row % 2 == 0 ? CARD_COLOR : new Color(249, 250, 251));
                } else {
                    comp.setBackground(new Color(239, 246, 255));
                }

                if (comp instanceof JLabel) {
                    ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
                }

                return comp;
            }
        };

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(60);
        table.setBackground(CARD_COLOR);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(241, 245, 249));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 50));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        return table;
    }

    private JScrollPane createEnhancedScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 16, 16);

                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 16, 16);

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane;
    }

    private JPanel createEnhancedCardsView() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 3, 25, 25));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        return panel;
    }

    private void toggleView(JButton toggleButton) {
        isTableView = !isTableView;
        toggleButton.setText(isTableView ? "📋 Vue Cartes" : "📊 Vue Tableau");

        CardLayout cl = (CardLayout) ((JPanel) ((JPanel) getComponent(1)).getComponent(1)).getLayout();
        cl.show((JPanel) ((JPanel) getComponent(1)).getComponent(1), isTableView ? "table" : "cards");

        if (!isTableView) {
            updateCardsView();
        }
    }

    private void updateCardsView() {
        if (cardsPanel != null) {
            cardsPanel.removeAll();
            List<Etudiant> students = filterStudentsList();
            for (Etudiant etudiant : students) {
                if (etudiant != null) {
                    JPanel card = createEnhancedStudentCard(etudiant);
                    cardsPanel.add(card);
                }
            }
            cardsPanel.revalidate();
            cardsPanel.repaint();
        }
    }

    private JPanel createEnhancedStudentCard(Etudiant etudiant) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 20, 20);

                GradientPaint gradient = new GradientPaint(0, 0, CARD_COLOR, 0, getHeight(), new Color(253, 254, 255));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);

                g2d.setColor(BORDER_COLOR);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);

                g2d.dispose();
            }
        };

        card.setLayout(new BorderLayout(20, 20));
        card.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        card.setPreferredSize(new Dimension(350, 320));

        JPanel photoSection = createEnhancedCardPhotoSection(etudiant);
        card.add(photoSection, BorderLayout.NORTH);

        JPanel infoSection = createEnhancedCardInfoSection(etudiant);
        card.add(infoSection, BorderLayout.CENTER);

        JPanel actionsSection = createEnhancedCardActionsSection(etudiant);
        card.add(actionsSection, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createEnhancedCardPhotoSection(Etudiant etudiant) {
        JPanel photoSection = new JPanel(new FlowLayout(FlowLayout.CENTER));
        photoSection.setBackground(Color.WHITE);

        EnhancedRoundImageLabel photoLabel = new EnhancedRoundImageLabel(70);
        if (etudiant != null && etudiant.getphoto_url() != null && !etudiant.getphoto_url().isEmpty()) {
            try {
                photoLabel.setImage(new URL(etudiant.getphoto_url()));
            } catch (Exception e) {
                photoLabel.setText("👤");
            }
        } else {
            photoLabel.setText("👤");
        }

        photoSection.add(photoLabel);
        return photoSection;
    }

    private JPanel createEnhancedCardInfoSection(Etudiant etudiant) {
        JPanel infoSection = new JPanel();
        infoSection.setLayout(new BoxLayout(infoSection, BoxLayout.Y_AXIS));
        infoSection.setBackground(Color.WHITE);

        String name = etudiant != null ? (etudiant.getPrenom() != null ? etudiant.getPrenom() : "") + " " +
                (etudiant.getNom() != null ? etudiant.getNom() : "") : "N/A";
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String matricule = etudiant != null && etudiant.getMatricule() != null ? etudiant.getMatricule() : "N/A";
        JLabel matriculeLabel = new JLabel("📋 " + matricule);
        matriculeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        matriculeLabel.setForeground(PRIMARY_COLOR);
        matriculeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String email = etudiant != null && etudiant.getEmail() != null ? etudiant.getEmail() : "N/A";
        JLabel emailLabel = new JLabel("📧 " + email);
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        emailLabel.setForeground(TEXT_SECONDARY);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String niveau = etudiant != null ? getNiveauLabel(etudiant.getNiveauId()) : "N/A";
        JLabel niveauLabel = new JLabel("📚 " + niveau);
        niveauLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        niveauLabel.setForeground(TEXT_SECONDARY);
        niveauLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String parcours = etudiant != null ? getParcoursLabel(etudiant.getParcoursId()) : "N/A";
        JLabel parcoursLabel = new JLabel("🎯 " + parcours);
        parcoursLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        parcoursLabel.setForeground(TEXT_SECONDARY);
        parcoursLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoSection.add(nameLabel);
        infoSection.add(Box.createVerticalStrut(10));
        infoSection.add(matriculeLabel);
        infoSection.add(Box.createVerticalStrut(8));
        infoSection.add(emailLabel);
        infoSection.add(Box.createVerticalStrut(6));
        infoSection.add(niveauLabel);
        infoSection.add(Box.createVerticalStrut(6));
        infoSection.add(parcoursLabel);

        return infoSection;
    }

    private JPanel createEnhancedCardActionsSection(Etudiant etudiant) {
        JPanel actionsSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        actionsSection.setBackground(Color.WHITE);

        JButton editButton = createEnhancedCardActionButton("✏️", WARNING_COLOR, "Modifier");
        editButton.addActionListener(e -> showStudentFormDialog(etudiant));

        JButton deleteButton = createEnhancedCardActionButton("🗑️", ERROR_COLOR, "Supprimer");
        deleteButton.addActionListener(e -> {
            if (etudiant != null && etudiant.getId() != null) {
                deleteStudent(etudiant.getId());
            }
        });

        JButton updateButton = createEnhancedCardActionButton("🔄", SECONDARY_COLOR, "Niveau/Parcours");
        updateButton.addActionListener(e -> showUpdateNiveauParcoursDialog(etudiant));

        actionsSection.add(editButton);
        actionsSection.add(deleteButton);
        actionsSection.add(updateButton);

        return actionsSection;
    }

    private JButton createEnhancedCardActionButton(String icon, Color color, String tooltip) {
        JButton button = new JButton(icon) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentColor;
                if (getModel().isPressed()) {
                    currentColor = color.darker();
                } else if (getModel().isRollover()) {
                    currentColor = color.brighter();
                } else {
                    currentColor = color;
                }

                g2d.setColor(currentColor);
                g2d.fillOval(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawOval(1, 1, getWidth() - 3, getHeight() - 3);

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(40, 40));
        button.setToolTipText(tooltip);

        return button;
    }

    private void showStudentFormDialog(Etudiant etudiant) {
        boolean isEdit = etudiant != null;
        studentDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Modifier l'Étudiant" : "Ajouter un Étudiant", true);
        studentDialog.setLayout(new BorderLayout());
        studentDialog.setSize(650, 800);
        studentDialog.setLocationRelativeTo(this);

        loadNiveauxAndParcours();

        JPanel mainPanel = createEnhancedDialogPanel();

        JPanel headerPanel = createEnhancedDialogHeader(
                isEdit ? "✏️ Modifier l'Étudiant" : "➕ Ajouter un Étudiant",
                "Veuillez remplir tous les champs requis");
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = createEnhancedStudentFormPanel(etudiant, isEdit);
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(null);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(formScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createEnhancedStudentFormButtons(etudiant, isEdit);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        studentDialog.add(mainPanel);
        studentDialog.setVisible(true);
    }

    private JPanel createEnhancedStudentFormPanel(Etudiant etudiant, boolean isEdit) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createEnhancedFormLabel("Matricule *"), gbc);
        gbc.gridy = 1;
        matriculeField = createEnhancedTextField(isEdit && etudiant != null ? etudiant.getMatricule() : "");
        matriculeField.setPreferredSize(new Dimension(550, 50));
        formPanel.add(matriculeField, gbc);

        gbc.gridy = 2;
        formPanel.add(createEnhancedFormLabel("Prénom *"), gbc);
        gbc.gridy = 3;
        firstNameField = createEnhancedTextField(isEdit && etudiant != null ? etudiant.getPrenom() : "");
        firstNameField.setPreferredSize(new Dimension(550, 50));
        formPanel.add(firstNameField, gbc);

        gbc.gridy = 4;
        formPanel.add(createEnhancedFormLabel("Nom *"), gbc);
        gbc.gridy = 5;
        lastNameField = createEnhancedTextField(isEdit && etudiant != null ? etudiant.getNom() : "");
        lastNameField.setPreferredSize(new Dimension(550, 50));
        formPanel.add(lastNameField, gbc);

        gbc.gridy = 6;
        formPanel.add(createEnhancedFormLabel("Email *"), gbc);
        gbc.gridy = 7;
        emailField = createEnhancedTextField(isEdit && etudiant != null ? etudiant.getEmail() : "");
        emailField.setPreferredSize(new Dimension(550, 50));
        emailField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateEmailField(emailField);
            }
        });
        formPanel.add(emailField, gbc);

        gbc.gridy = 8;
        formPanel.add(createEnhancedFormLabel("Adresse"), gbc);
        gbc.gridy = 9;
        adresseField = createEnhancedTextField(isEdit && etudiant != null ? etudiant.getAdresse() : "");
        adresseField.setPreferredSize(new Dimension(550, 50));
        formPanel.add(adresseField, gbc);

        gbc.gridy = 10;
        formPanel.add(createEnhancedFormLabel("Niveau *"), gbc);
        gbc.gridy = 11;
        niveauComboBox = createEnhancedFormComboBox();
        niveauComboBox.setPreferredSize(new Dimension(550, 50));
        if (allNiveaux != null) {
            for (Niveau niveau : new ArrayList<>(allNiveaux)) {
                if (niveau != null) {
                    niveauComboBox.addItem(niveau);
                }
            }
        }
        if (isEdit && etudiant != null && etudiant.getNiveauId() != null && allNiveaux != null) {
            for (Niveau niveau : new ArrayList<>(allNiveaux)) {
                if (niveau != null && niveau.getId() != null && niveau.getId().equals(etudiant.getNiveauId())) {
                    niveauComboBox.setSelectedItem(niveau);
                    break;
                }
            }
        }
        formPanel.add(niveauComboBox, gbc);

        gbc.gridy = 12;
        formPanel.add(createEnhancedFormLabel("Parcours *"), gbc);
        gbc.gridy = 13;
        parcoursComboBox = createEnhancedFormComboBox();
        parcoursComboBox.setPreferredSize(new Dimension(550, 50));
        if (allParcours != null) {
            for (Parcours parcours : new ArrayList<>(allParcours)) {
                if (parcours != null) {
                    parcoursComboBox.addItem(parcours);
                }
            }
        }
        if (isEdit && etudiant != null && etudiant.getParcoursId() != null && allParcours != null) {
            for (Parcours parcours : new ArrayList<>(allParcours)) {
                if (parcours != null && parcours.getId() != null && parcours.getId().equals(etudiant.getParcoursId())) {
                    parcoursComboBox.setSelectedItem(parcours);
                    break;
                }
            }
        }
        formPanel.add(parcoursComboBox, gbc);

        gbc.gridy = 14;
        formPanel.add(createEnhancedFormLabel("Photo"), gbc);
        gbc.gridy = 15;
        JPanel photoPanel = createEnhancedPhotoSelectionPanel();
        formPanel.add(photoPanel, gbc);

        return formPanel;
    }

    private JPanel createEnhancedPhotoSelectionPanel() {
        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        photoPanel.setBackground(CARD_COLOR);

        JButton choosePhotoButton = createEnhancedButton("📷 Choisir une Photo", SECONDARY_COLOR, false);
        choosePhotoButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "Images (*.jpg, *.png, *.jpeg)", "jpg", "png", "jpeg"));

            if (fileChooser.showOpenDialog(studentDialog) == JFileChooser.APPROVE_OPTION) {
                selectedPhoto = fileChooser.getSelectedFile();
                if (selectedPhoto != null) {
                    photoPreviewLabel.setText("📷 " + selectedPhoto.getName());
                    photoPreviewLabel.setForeground(SUCCESS_COLOR);
                }
            }
        });

        photoPreviewLabel = new JLabel("Aucune photo sélectionnée");
        photoPreviewLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        photoPreviewLabel.setForeground(TEXT_SECONDARY);

        photoPanel.add(choosePhotoButton);
        photoPanel.add(photoPreviewLabel);

        return photoPanel;
    }

    private JPanel createEnhancedStudentFormButtons(Etudiant etudiant, boolean isEdit) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        loadNiveauxAndParcours();

        JButton saveButton = createEnhancedButton("💾 Enregistrer", PRIMARY_COLOR, true);
        JButton cancelButton = createEnhancedButton("❌ Annuler", new Color(156, 163, 175), false);

        saveButton.addActionListener(e -> saveStudent(etudiant, isEdit));
        cancelButton.addActionListener(e -> studentDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    private void saveStudent(Etudiant etudiant, boolean isEdit) {
        String matricule = matriculeField.getText() != null ? matriculeField.getText().trim() : "";
        String firstName = firstNameField.getText() != null ? firstNameField.getText().trim() : "";
        String lastName = lastNameField.getText() != null ? lastNameField.getText().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String adresse = adresseField.getText() != null ? adresseField.getText().trim() : "";
        Niveau selectedNiveau = (Niveau) niveauComboBox.getSelectedItem();
        Parcours selectedParcours = (Parcours) parcoursComboBox.getSelectedItem();

        if (matricule.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            showEnhancedErrorDialog("Tous les champs marqués d'un * sont obligatoires.");
            return;
        }

        if (!isValidEmail(email)) {
            showEnhancedErrorDialog("Veuillez saisir une adresse email valide.");
            emailField.requestFocus();
            return;
        }

        if (selectedNiveau == null) {
            showEnhancedErrorDialog("Veuillez sélectionner un niveau.");
            return;
        }

        if (selectedParcours == null) {
            showEnhancedErrorDialog("Veuillez sélectionner un parcours.");
            return;
        }

        Etudiant student = new Etudiant();
        student.setMatricule(matricule);
        student.setPrenom(firstName);
        student.setNom(lastName);
        student.setEmail(email);
        student.setAdresse(adresse);
        student.setNiveauId(selectedNiveau.getId());
        student.setParcoursId(selectedParcours.getId());

        try {
            if (isEdit && etudiant != null) {
                student.setId(etudiant.getId());
                studentService.updateEtudiant(etudiant.getId(), student, selectedPhoto);
                showSuccessNotification("Étudiant modifié avec succès!");
            } else {
                studentService.addEtudiant(student, responsableId, selectedPhoto);
                showSuccessNotification("Étudiant ajouté avec succès!");
            }
            loadStudents();
            clearFields();
            studentDialog.dispose();
        } catch (ApiException ex) {
            showEnhancedErrorDialog("Erreur lors de l'enregistrement: " + ex.getMessage());
        }
    }

    private void showUpdateNiveauParcoursDialog(Etudiant etudiant) {
        if (etudiant == null) {
            showErrorNotification("Erreur: Étudiant non sélectionné.");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Modifier Niveau & Parcours", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(550, 450);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = createEnhancedDialogPanel();

        JPanel headerPanel = createEnhancedDialogHeader(
                "🔄 Modifier Niveau & Parcours",
                "Sélectionnez le nouveau niveau et parcours pour " +
                        (etudiant.getPrenom() != null ? etudiant.getPrenom() : "") + " " +
                        (etudiant.getNom() != null ? etudiant.getNom() : ""));
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 0, 20, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel currentInfoLabel = new JLabel("📋 " +
                (etudiant.getMatricule() != null ? etudiant.getMatricule() : "N/A") + " - " +
                (etudiant.getPrenom() != null ? etudiant.getPrenom() : "") + " " +
                (etudiant.getNom() != null ? etudiant.getNom() : ""));
        currentInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        currentInfoLabel.setForeground(TEXT_PRIMARY);
        formPanel.add(currentInfoLabel, gbc);

        gbc.gridy = 1;
        formPanel.add(createEnhancedFormLabel("Nouveau Niveau *"), gbc);
        gbc.gridy = 2;
        JComboBox<Niveau> updateNiveauComboBox = createEnhancedFormComboBox();
        updateNiveauComboBox.setPreferredSize(new Dimension(450, 50));
        if (allNiveaux != null) {
            for (Niveau niveau : new ArrayList<>(allNiveaux)) {
                if (niveau != null) {
                    updateNiveauComboBox.addItem(niveau);
                }
            }
        }
        if (etudiant.getNiveauId() != null && allNiveaux != null) {
            for (Niveau niveau : new ArrayList<>(allNiveaux)) {
                if (niveau != null && niveau.getId() != null && niveau.getId().equals(etudiant.getNiveauId())) {
                    updateNiveauComboBox.setSelectedItem(niveau);
                    break;
                }
            }
        }
        formPanel.add(updateNiveauComboBox, gbc);

        gbc.gridy = 3;
        formPanel.add(createEnhancedFormLabel("Nouveau Parcours *"), gbc);
        gbc.gridy = 4;
        JComboBox<Parcours> updateParcoursComboBox = createEnhancedFormComboBox();
        updateParcoursComboBox.setPreferredSize(new Dimension(450, 50));
        if (allParcours != null) {
            for (Parcours parcours : new ArrayList<>(allParcours)) {
                if (parcours != null) {
                    updateParcoursComboBox.addItem(parcours);
                }
            }
        }
        if (etudiant.getParcoursId() != null && allParcours != null) {
            for (Parcours parcours : new ArrayList<>(allParcours)) {
                if (parcours != null && parcours.getId() != null && parcours.getId().equals(etudiant.getParcoursId())) {
                    updateParcoursComboBox.setSelectedItem(parcours);
                    break;
                }
            }
        }
        formPanel.add(updateParcoursComboBox, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        JButton saveButton = createEnhancedButton("💾 Enregistrer", PRIMARY_COLOR, true);
        JButton cancelButton = createEnhancedButton("❌ Annuler", new Color(156, 163, 175), false);

        saveButton.addActionListener(e -> {
            Niveau selectedNiveau = (Niveau) updateNiveauComboBox.getSelectedItem();
            Parcours selectedParcours = (Parcours) updateParcoursComboBox.getSelectedItem();

            if (selectedNiveau == null || selectedParcours == null) {
                showEnhancedErrorDialog("Veuillez sélectionner un niveau et un parcours.");
                return;
            }

            try {
                studentService.updateNiveauParcours(etudiant.getId(), selectedNiveau.getId(), selectedParcours.getId());
                showSuccessNotification("Niveau et parcours mis à jour avec succès!");
                loadStudents();
                dialog.dispose();
            } catch (ApiException ex) {
                showEnhancedErrorDialog("Erreur lors de la mise à jour: " + ex.getMessage());
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void deleteStudent(String id) {
        if (id == null) {
            showErrorNotification("Erreur: ID de l'étudiant non spécifié.");
            return;
        }
        showEnhancedConfirmDialog(
                "Confirmer la suppression",
                "Êtes-vous sûr de vouloir supprimer cet étudiant ?",
                "Cette action est irréversible et supprimera toutes les données associées.",
                () -> performDeleteStudent(id));
    }

    private void performDeleteStudent(String id) {
        try {
            studentService.deleteEtudiant(id);
            loadStudents();
            showSuccessNotification("Étudiant supprimé avec succès!");
        } catch (ApiException ex) {
            showErrorNotification("Erreur lors de la suppression: " + ex.getMessage());
        }
    }

    private void loadStudents() {
        SwingWorker<List<Etudiant>, Void> worker = new SwingWorker<List<Etudiant>, Void>() {
            @Override
            protected List<Etudiant> doInBackground() {
                return studentService.getAllEtudiants(responsableId);
            }

            @Override
            protected void done() {
                try {
                    List<Etudiant> students = get();
                    allStudents = students != null ? new ArrayList<>(students) : new ArrayList<>();
                    filterStudents();
                } catch (Exception ex) {
                    showErrorNotification("Erreur lors du chargement des étudiants: " + ex.getMessage());
                    allStudents = new ArrayList<>();
                }
            }
        };
        worker.execute();
    }

    private void filterStudents() {
        if (tableModel != null) {
            tableModel.setRowCount(0);
            List<Etudiant> students = filterStudentsList();
            for (Etudiant e : students) {
                if (e != null) {
                    tableModel.addRow(new Object[] {
                            e.getMatricule() != null ? e.getMatricule() : "",
                            e.getPrenom() != null ? e.getPrenom() : "",
                            e.getNom() != null ? e.getNom() : "",
                            e.getEmail() != null ? e.getEmail() : "",
                            e.getAdresse() != null ? e.getAdresse() : "",
                            getNiveauLabel(e.getNiveauId()),
                            getParcoursLabel(e.getParcoursId()),
                            e.getphoto_url(),
                            "Actions"
                    });
                }
            }
            if (!isTableView) {
                updateCardsView();
            }
        }
    }

    private List<Etudiant> filterStudentsList() {
        List<Etudiant> filtered = new ArrayList<>();
        if (allStudents == null || allStudents.isEmpty()) {
            return filtered;
        }

        String query = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        if (query.equals("🔍 rechercher des étudiants..."))
            query = "";

        String selectedNiveau = niveauFilterComboBox.getSelectedItem() != null
                ? (String) niveauFilterComboBox.getSelectedItem()
                : "TOUS LES NIVEAUX";
        String selectedParcours = parcoursFilterComboBox.getSelectedItem() != null
                ? (String) parcoursFilterComboBox.getSelectedItem()
                : "TOUS LES PARCOURS";

        for (Etudiant e : allStudents) {
            if (e == null)
                continue;
            boolean matchesSearch = query.isEmpty() ||
                    (e.getPrenom() != null && e.getPrenom().toLowerCase().contains(query)) ||
                    (e.getNom() != null && e.getNom().toLowerCase().contains(query)) ||
                    (e.getEmail() != null && e.getEmail().toLowerCase().contains(query)) ||
                    (e.getMatricule() != null && e.getMatricule().toLowerCase().contains(query));

            boolean matchesNiveau = selectedNiveau.equals("TOUS LES NIVEAUX") ||
                    getNiveauLabel(e.getNiveauId()).equals(selectedNiveau);

            boolean matchesParcours = selectedParcours.equals("TOUS LES PARCOURS") ||
                    getParcoursLabel(e.getParcoursId()).equals(selectedParcours);

            if (matchesSearch && matchesNiveau && matchesParcours) {
                filtered.add(e);
            }
        }
        return filtered;
    }

    private String getNiveauLabel(String niveauId) {
        if (niveauId == null || allNiveaux == null)
            return "N/A";
        for (Niveau niveau : new ArrayList<>(allNiveaux)) {
            if (niveau != null && niveau.getId() != null && niveau.getId().equals(niveauId)) {
                return niveau.getNom() != null ? niveau.getNom() : "N/A";
            }
        }
        return "Inconnu";
    }

    private String getParcoursLabel(String parcoursId) {
        if (parcoursId == null || allParcours == null)
            return "N/A";
        for (Parcours parcours : new ArrayList<>(allParcours)) {
            if (parcours != null && parcours.getId() != null && parcours.getId().equals(parcoursId)) {
                return parcours.getNom() != null ? parcours.getNom() : "N/A";
            }
        }
        return "Inconnu";
    }

    private boolean isValidEmail(String email) {
        if (email == null)
            return false;
        return Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", email);
    }

    private void validateEmailField(JTextField field) {
        String email = field.getText() != null ? field.getText().trim() : "";
        if (!email.isEmpty()) {
            if (isValidEmail(email)) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new ModernBorder(SUCCESS_COLOR),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)));
                field.setToolTipText("✅ Email valide");
            } else {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new ModernBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)));
                field.setToolTipText("❌ Format d'email invalide");
            }
        } else {
            field.setBorder(BorderFactory.createCompoundBorder(
                    new ModernBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            field.setToolTipText("");
        }
    }

    private void clearFields() {
        matriculeField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        emailField.setText("");
        adresseField.setText("");
        niveauComboBox.setSelectedIndex(-1);
        parcoursComboBox.setSelectedIndex(-1);
        selectedPhoto = null;
        if (photoPreviewLabel != null) {
            photoPreviewLabel.setText("Aucune photo sélectionnée");
            photoPreviewLabel.setForeground(TEXT_SECONDARY);
        }
    }

    private JPanel createEnhancedDialogPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, CARD_COLOR, 0, getHeight(), new Color(253, 254, 255));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);

                g2d.dispose();
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));
        return panel;
    }

    private JPanel createEnhancedDialogHeader(String title, String subtitle) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));

        JLabel titleLabel = new JLabel(title != null ? title : "");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel(subtitle != null ? subtitle : "");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        return headerPanel;
    }

    private JLabel createEnhancedFormLabel(String text) {
        JLabel label = new JLabel(text != null ? text : "");
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JTextField createEnhancedTextField(String text) {
        JTextField field = new JTextField(text != null ? text : "") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBackground(new Color(249, 250, 251));
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new ModernBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBackground(CARD_COLOR);
                field.setBorder(BorderFactory.createCompoundBorder(
                        new ModernBorder(PRIMARY_COLOR, 2),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBackground(new Color(249, 250, 251));
                field.setBorder(BorderFactory.createCompoundBorder(
                        new ModernBorder(BORDER_COLOR),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            }
        });

        return field;
    }

    private <T> JComboBox<T> createEnhancedFormComboBox() {
        JComboBox<T> comboBox = new JComboBox<T>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        comboBox.setBackground(new Color(249, 250, 251));
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                new ModernBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        return comboBox;
    }

    private void showEnhancedConfirmDialog(String title, String message, String subtitle, Runnable onConfirm) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                title != null ? title : "Confirmation", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(550, 400);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = createEnhancedDialogPanel();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel iconLabel = new JLabel("⚠️");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 64));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel(message != null ? message : "");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        messageLabel.setForeground(TEXT_PRIMARY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle != null ? subtitle : "");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalStrut(12));
        contentPanel.add(subtitleLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        JButton confirmButton = createEnhancedButton("🗑️ Supprimer", ERROR_COLOR, true);
        JButton cancelButton = createEnhancedButton("❌ Annuler", new Color(156, 163, 175), false);

        confirmButton.addActionListener(e -> {
            dialog.dispose();
            if (onConfirm != null) {
                onConfirm.run();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showEnhancedErrorDialog(String message) {
        JOptionPane.showMessageDialog(this,
                message != null ? message : "Une erreur est survenue.",
                "❌ Erreur de Validation",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessNotification(String message) {
        createEnhancedToastNotification(message != null ? message : "Opération réussie.", SUCCESS_COLOR, "✅");
    }

    private void showErrorNotification(String message) {
        createEnhancedToastNotification(message != null ? message : "Une erreur est survenue.", ERROR_COLOR, "❌");
    }

    private void showWarningNotification(String message) {
        createEnhancedToastNotification(message != null ? message : "Attention.", WARNING_COLOR, "⚠️");
    }

    private void createEnhancedToastNotification(String message, Color bgColor, String icon) {
        JWindow toast = new JWindow();
        toast.setAlwaysOnTop(true);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 120));
                g2d.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 16, 16);

                GradientPaint gradient = new GradientPaint(0, 0, bgColor, 0, getHeight(), bgColor.darker());
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 16, 16);

                g2d.dispose();
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JLabel messageLabel = new JLabel((icon != null ? icon : "") + " " + (message != null ? message : ""));
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        messageLabel.setForeground(Color.WHITE);

        panel.add(messageLabel, BorderLayout.CENTER);
        toast.add(panel);
        toast.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int finalX = screenSize.width - toast.getWidth() - 30;
        int finalY = 30;

        toast.setLocation(screenSize.width, finalY);
        toast.setVisible(true);

        Timer slideInTimer = new Timer(10, null);
        slideInTimer.addActionListener(new ActionListener() {
            int currentX = screenSize.width;

            @Override
            public void actionPerformed(ActionEvent e) {
                currentX -= 15;
                if (currentX <= finalX) {
                    currentX = finalX;
                    slideInTimer.stop();
                }
                toast.setLocation(currentX, finalY);
            }
        });
        slideInTimer.start();

        Timer hideTimer = new Timer(4000, e -> {
            Timer slideOutTimer = new Timer(10, null);
            slideOutTimer.addActionListener(new ActionListener() {
                int currentX = finalX;

                @Override
                public void actionPerformed(ActionEvent e) {
                    currentX += 15;
                    if (currentX >= screenSize.width) {
                        slideOutTimer.stop();
                        toast.setVisible(false);
                        toast.dispose();
                    } else {
                        toast.setLocation(currentX, finalY);
                    }
                }
            });
            slideOutTimer.start();
        });
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    private static class ModernBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;

        public ModernBorder(Color color) {
            this(color, 1);
        }

        public ModernBorder(Color color, int thickness) {
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(x, y, width - 1, height - 1, 12, 12);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }

    private class EnhancedPhotoCellRenderer extends JPanel implements TableCellRenderer {
        private EnhancedRoundImageLabel imageLabel;

        public EnhancedPhotoCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
            setOpaque(false);
            imageLabel = new EnhancedRoundImageLabel(45);
            add(imageLabel);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

            if (allStudents != null && row >= 0 && row < allStudents.size()) {
                Etudiant etudiant = allStudents.get(row);
                if (etudiant != null && etudiant.getphoto_url() != null && !etudiant.getphoto_url().isEmpty()) {
                    try {
                        imageLabel.setImage(new URL(etudiant.getphoto_url()));
                    } catch (Exception e) {
                        imageLabel.setText("👤");
                    }
                } else {
                    imageLabel.setText("👤");
                }
            } else {
                imageLabel.setText("👤");
            }

            return this;
        }
    }

    private class EnhancedButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton;
        private JButton deleteButton;
        private JButton updateButton;

        public EnhancedButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
            setOpaque(false);

            editButton = createEnhancedTableActionButton("✏️", WARNING_COLOR);
            deleteButton = createEnhancedTableActionButton("🗑️", ERROR_COLOR);
            updateButton = createEnhancedTableActionButton("🔄", SECONDARY_COLOR);

            add(editButton);
            add(deleteButton);
            add(updateButton);
        }

        private JButton createEnhancedTableActionButton(String text, Color color) {
            JButton button = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(color);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };

            button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            button.setForeground(Color.WHITE);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(35, 30));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            return button;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }

    private class EnhancedButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;
        private JButton updateButton;
        private Etudiant currentStudent;

        public EnhancedButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
            panel.setOpaque(false);

            editButton = createEnhancedTableActionButton("✏️", WARNING_COLOR);
            deleteButton = createEnhancedTableActionButton("🗑️", ERROR_COLOR);
            updateButton = createEnhancedTableActionButton("🔄", SECONDARY_COLOR);

            editButton.addActionListener(e -> {
                fireEditingStopped();
                if (currentStudent != null) {
                    showStudentFormDialog(currentStudent);
                }
            });

            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                if (currentStudent != null && currentStudent.getId() != null) {
                    deleteStudent(currentStudent.getId());
                }
            });

            updateButton.addActionListener(e -> {
                fireEditingStopped();
                if (currentStudent != null) {
                    showUpdateNiveauParcoursDialog(currentStudent);
                }
            });

            panel.add(editButton);
            panel.add(deleteButton);
            panel.add(updateButton);
        }

        private JButton createEnhancedTableActionButton(String text, Color color) {
            JButton button = new JButton(text) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    if (getModel().isPressed()) {
                        g2d.setColor(color.darker());
                    } else if (getModel().isRollover()) {
                        g2d.setColor(color.brighter());
                    } else {
                        g2d.setColor(color);
                    }

                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };

            button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            button.setForeground(Color.WHITE);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(35, 30));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            return button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {

            currentStudent = (allStudents != null && row >= 0 && row < allStudents.size()) ? allStudents.get(row)
                    : null;

            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }

    private static class EnhancedRoundImageLabel extends JLabel {
        private ImageIcon imageIcon;
        private final int size;
        private final Color borderColor = new Color(226, 232, 240);

        public EnhancedRoundImageLabel(int size) {
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
            setFont(new Font("Segoe UI", Font.PLAIN, size / 3));
        }

        public void setImage(URL url) {
            try {
                if (url != null) {
                    ImageIcon icon = new ImageIcon(url);
                    Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    imageIcon = new ImageIcon(scaled);
                    setText("");
                    repaint();
                } else {
                    imageIcon = null;
                    setText("👤");
                    repaint();
                }
            } catch (Exception e) {
                imageIcon = null;
                setText("👤");
                repaint();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, size, size);
            g2.setClip(circle);

            if (imageIcon != null) {
                g2.drawImage(imageIcon.getImage(), 0, 0, size, size, this);
            } else {
                GradientPaint gradient = new GradientPaint(0, 0, new Color(248, 250, 252),
                        0, size, new Color(241, 245, 249));
                g2.setPaint(gradient);
                g2.fill(circle);
                g2.setColor(TEXT_SECONDARY);
                super.paintComponent(g2);
            }

            g2.setClip(null);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(2));
            g2.draw(circle);

            g2.dispose();
        }
    }
}