package com.studentmanagement.ui.grades;

import com.studentmanagement.model.*;
import com.studentmanagement.service.StudentService;
import com.studentmanagement.service.ApiException;
import com.studentmanagement.ui.common.MainWindow;
import com.studentmanagement.ui.common.SidebarUtil;
import com.studentmanagement.ui.common.ModernComponents;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.regex.Pattern;

public class GradesManagementFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private StudentService studentService;
    private MainWindow mainWindow;
    private DefaultTableModel gradesTableModel;
    private DefaultTableModel averagesTableModel;
    private DefaultTableModel classStatsTableModel;
    private JTable gradesTable;
    private JTable averagesTable;
    private JTable classStatsTable;
    private JComboBox<Etudiant> studentComboBox;
    private JComboBox<String> semestreComboBox;
    private JComboBox<String> anneeComboBox;
    private List<Etudiant> allStudents;
    private List<Matiere> allMatieres;
    private List<Niveau> allNiveaux;

    // Modern Color Palette
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

    public GradesManagementFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.studentService = new StudentService();
        this.studentService.setJwtToken(mainWindow.getCurrentResponsable().getToken());
        initializeUI();
        loadData();
        loadGrades();
        loadStatistics();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel sidebar = SidebarUtil.createSidebar(mainWindow, "Grades");
        add(sidebar, BorderLayout.WEST);

        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header with title and breadcrumb
        JPanel headerPanel = createHeaderPanel();
        mainContent.add(headerPanel, BorderLayout.NORTH);

        // Modern tabbed pane
        JTabbedPane tabbedPane = createModernTabbedPane();
        mainContent.add(tabbedPane, BorderLayout.CENTER);

        return mainContent;
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel("Gestion des Notes");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel breadcrumbLabel = new JLabel("Dashboard > Gestion des Notes");
        breadcrumbLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        breadcrumbLabel.setForeground(TEXT_SECONDARY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(BACKGROUND_COLOR);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(breadcrumbLabel);

        header.add(titlePanel, BorderLayout.WEST);
        return header;
    }

    private JTabbedPane createModernTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tabbedPane.setBackground(CARD_COLOR);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setBorder(new ModernBorder());

        JPanel gradesPanel = createGradesPanel();
        JPanel statsPanel = createStatisticsPanel();

        tabbedPane.addTab("📝 Gestion des Notes", gradesPanel);
        tabbedPane.addTab("📊 Statistiques", statsPanel);

        // Add ChangeListener to reload data when "Gestion des Notes" tab is selected
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPane.getSelectedIndex() == 0) { // "Gestion des Notes" tab
                    loadData();
                }
            }
        });

        return tabbedPane;
    }

    private JPanel createGradesPanel() {
        JPanel gradesPanel = new JPanel(new BorderLayout());
        gradesPanel.setBackground(CARD_COLOR);
        gradesPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel header = createGradesHeader();
        gradesPanel.add(header, BorderLayout.NORTH);

        JPanel tablePanel = createGradesTable();
        gradesPanel.add(tablePanel, BorderLayout.CENTER);

        return gradesPanel;
    }

    private JPanel createGradesHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterPanel.setBackground(CARD_COLOR);

        JLabel studentLabel = new JLabel("Étudiant:");
        studentLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        studentLabel.setForeground(TEXT_PRIMARY);

        studentComboBox = ModernComponents.createModernComboBox();
        studentComboBox.setPreferredSize(new Dimension(250, 40));
        studentComboBox.addActionListener(e -> loadGrades());

        filterPanel.add(studentLabel);
        filterPanel.add(studentComboBox);

        // Action panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionPanel.setBackground(CARD_COLOR);

        JButton addButton = ModernComponents.createPrimaryButton("➕ Ajouter une Note");
        addButton.addActionListener(e -> showGradeFormDialog(null));

        actionPanel.add(addButton);

        header.add(filterPanel, BorderLayout.WEST);
        header.add(actionPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createGradesTable() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);

        String[] columns = { "Matière", "Coefficient", "Note /20", "Semestre", "Année", "Actions" };
        gradesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        gradesTable = ModernComponents.createModernTable(gradesTableModel);
        gradesTable.getColumn("Actions").setCellRenderer(new ModernActionButtonRenderer());
        gradesTable.getColumn("Actions")
                .setCellEditor(new ModernActionButtonEditor(this::editGrade, this::deleteGrade));

        JScrollPane scrollPane = ModernComponents.createModernScrollPane(gradesTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createStatisticsPanel() {
        JPanel statsPanel = new JPanel(new BorderLayout());
        statsPanel.setBackground(CARD_COLOR);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel filterPanel = createStatsFilterPanel();
        statsPanel.add(filterPanel, BorderLayout.NORTH);

        JTabbedPane statsTabbedPane = createStatsTabbedPane();
        statsPanel.add(statsTabbedPane, BorderLayout.CENTER);

        return statsPanel;
    }

    private JPanel createStatsFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterPanel.setBackground(CARD_COLOR);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel semestreLabel = new JLabel("Semestre:");
        semestreLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        semestreLabel.setForeground(TEXT_PRIMARY);

        semestreComboBox = ModernComponents.createModernComboBox();
        semestreComboBox.addItem("");
        semestreComboBox.addItem("S1");
        semestreComboBox.addItem("S2");

        JLabel anneeLabel = new JLabel("Année:");
        anneeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        anneeLabel.setForeground(TEXT_PRIMARY);

        anneeComboBox = ModernComponents.createModernComboBox();
        anneeComboBox.addItem("");
        anneeComboBox.addItem("2023-2024");
        anneeComboBox.addItem("2024-2025");
        anneeComboBox.addItem("2025-2026");

        JButton loadStatsButton = ModernComponents.createSecondaryButton("🔄 Charger les Statistiques");
        loadStatsButton.addActionListener(e -> loadStatistics());

        filterPanel.add(semestreLabel);
        filterPanel.add(semestreComboBox);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(anneeLabel);
        filterPanel.add(anneeComboBox);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(loadStatsButton);

        return filterPanel;
    }

    private JTabbedPane createStatsTabbedPane() {
        JTabbedPane statsTabbedPane = new JTabbedPane();
        statsTabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statsTabbedPane.setBackground(CARD_COLOR);

        JPanel averagesPanel = createAveragesPanel();
        JPanel classStatsPanel = createClassStatsPanel();

        statsTabbedPane.addTab("📈 Moyennes Étudiants", averagesPanel);
        statsTabbedPane.addTab("📊 Statistiques Classes", classStatsPanel);

        return statsTabbedPane;
    }

    private JPanel createAveragesPanel() {
        JPanel averagesPanel = new JPanel(new BorderLayout());
        averagesPanel.setBackground(CARD_COLOR);

        String[] columns = { "Matricule", "Prénom", "Nom", "Moyenne", "Statut", "Mention" };
        averagesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        averagesTable = ModernComponents.createModernTable(averagesTableModel);
        JScrollPane scrollPane = ModernComponents.createModernScrollPane(averagesTable);
        averagesPanel.add(scrollPane, BorderLayout.CENTER);

        return averagesPanel;
    }

    private JPanel createClassStatsPanel() {
        JPanel classStatsPanel = new JPanel(new BorderLayout());
        classStatsPanel.setBackground(CARD_COLOR);

        String[] columns = { "Niveau", "Moyenne Générale", "Moyenne Max", "Moyenne Min", "Détails" };
        classStatsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        classStatsTable = ModernComponents.createModernTable(classStatsTableModel);
        classStatsTable.getColumn("Détails").setCellRenderer(new ModernDetailsButtonRenderer());
        classStatsTable.getColumn("Détails").setCellEditor(new ModernDetailsButtonEditor(this::showLevelDetailsDialog));

        JScrollPane scrollPane = ModernComponents.createModernScrollPane(classStatsTable);
        classStatsPanel.add(scrollPane, BorderLayout.CENTER);

        return classStatsPanel;
    }

    private void showGradeFormDialog(Note note) {
        boolean isEdit = note != null;
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                isEdit ? "Modifier la Note" : "Ajouter une Note", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 600);
        dialog.setLocationRelativeTo(this);

        loadData();
        loadGrades();
        loadStatistics();

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel(isEdit ? "✏️ Modifier la Note" : "➕ Ajouter une Note");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Veuillez remplir tous les champs requis");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(CARD_COLOR);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Matière
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel matiereLabel = createFormLabel("Matière *");
        formPanel.add(matiereLabel, gbc);

        gbc.gridy = 1;
        JComboBox<Matiere> matiereComboBox = ModernComponents.createModernComboBox();
        matiereComboBox.setPreferredSize(new Dimension(400, 45));
        if (allMatieres != null) {
            for (Matiere matiere : allMatieres) {
                matiereComboBox.addItem(matiere);
            }
        }
        if (isEdit && note != null) {
            for (Matiere matiere : allMatieres) {
                if (matiere.getId().equals(note.getMatiereId())) {
                    matiereComboBox.setSelectedItem(matiere);
                    break;
                }
            }
        }
        formPanel.add(matiereComboBox, gbc);

        // Note
        gbc.gridy = 2;
        JLabel valeurLabel = createFormLabel("Note sur 20 *");
        formPanel.add(valeurLabel, gbc);

        gbc.gridy = 3;
        JTextField valeurField = createModernTextField(isEdit && note != null ? String.valueOf(note.getValeur()) : "");
        valeurField.setPreferredSize(new Dimension(400, 45));

        // Validation en temps réel pour la note
        valeurField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateGradeField(valeurField);
            }
        });

        formPanel.add(valeurField, gbc);

        // Semestre
        gbc.gridy = 4;
        JLabel semestreLabel = createFormLabel("Semestre *");
        formPanel.add(semestreLabel, gbc);

        gbc.gridy = 5;
        JComboBox<String> formSemestreComboBox = ModernComponents.createModernComboBox();
        formSemestreComboBox.addItem("S1");
        formSemestreComboBox.addItem("S2");
        formSemestreComboBox.setPreferredSize(new Dimension(400, 45));
        if (isEdit && note != null) {
            formSemestreComboBox.setSelectedItem(note.getSemestre());
        }
        formPanel.add(formSemestreComboBox, gbc);

        // Année académique
        gbc.gridy = 6;
        JLabel anneeLabel = createFormLabel("Année Académique *");
        formPanel.add(anneeLabel, gbc);

        gbc.gridy = 7;
        JPanel anneePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        anneePanel.setBackground(CARD_COLOR);

        JTextField startYearField = createModernTextField("");
        startYearField.setPreferredSize(new Dimension(120, 45));
        startYearField.setToolTipText("Année de début (ex: 2024)");

        JLabel separatorLabel = new JLabel("-");
        separatorLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        separatorLabel.setForeground(TEXT_PRIMARY);

        JTextField endYearField = createModernTextField("");
        endYearField.setPreferredSize(new Dimension(120, 45));
        endYearField.setToolTipText("Année de fin (ex: 2025)");

        // Validation en temps réel pour les années
        startYearField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateYearField(startYearField);
                if (startYearField.getText().length() == 4) {
                    try {
                        int startYear = Integer.parseInt(startYearField.getText());
                        endYearField.setText(String.valueOf(startYear + 1));
                    } catch (NumberFormatException ex) {
                        // Ignore
                    }
                }
            }
        });

        endYearField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateYearField(endYearField);
            }
        });

        if (isEdit && note != null && note.getAnnee() != null && note.getAnnee().matches("\\d{4}-\\d{4}")) {
            String[] years = note.getAnnee().split("-");
            startYearField.setText(years[0]);
            endYearField.setText(years[1]);
        }

        anneePanel.add(startYearField);
        anneePanel.add(separatorLabel);
        anneePanel.add(endYearField);
        formPanel.add(anneePanel, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        JButton saveButton = ModernComponents.createPrimaryButton("💾 Enregistrer");
        JButton cancelButton = ModernComponents.createSecondaryButton("❌ Annuler");

        saveButton.addActionListener(e -> {
            if (validateAndSaveGrade(dialog, matiereComboBox, valeurField, formSemestreComboBox,
                    startYearField, endYearField, note, isEdit)) {
                dialog.dispose();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private boolean validateAndSaveGrade(JDialog dialog, JComboBox<Matiere> matiereComboBox,
            JTextField valeurField, JComboBox<String> semestreComboBox,
            JTextField startYearField, JTextField endYearField, Note note, boolean isEdit) {

        try {
            // Validation des champs requis
            String valeurText = valeurField.getText().trim();
            String startYearText = startYearField.getText().trim();
            String endYearText = endYearField.getText().trim();

            if (valeurText.isEmpty() || startYearText.isEmpty() || endYearText.isEmpty()) {
                showErrorDialog(dialog, "Tous les champs marqués d'un * sont obligatoires.");
                return false;
            }

            // Validation de la note (0-20)
            float valeur;
            try {
                valeur = Float.parseFloat(valeurText);
                if (valeur < 0 || valeur > 20) {
                    showErrorDialog(dialog, "La note doit être comprise entre 0 et 20.");
                    valeurField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException ex) {
                showErrorDialog(dialog, "Veuillez saisir une note valide (nombre décimal).");
                valeurField.requestFocus();
                return false;
            }

            // Validation des années (4 chiffres)
            if (!Pattern.matches("\\d{4}", startYearText)) {
                showErrorDialog(dialog, "L'année de début doit contenir exactement 4 chiffres.");
                startYearField.requestFocus();
                return false;
            }

            if (!Pattern.matches("\\d{4}", endYearText)) {
                showErrorDialog(dialog, "L'année de fin doit contenir exactement 4 chiffres.");
                endYearField.requestFocus();
                return false;
            }

            int startYear = Integer.parseInt(startYearText);
            int endYear = Integer.parseInt(endYearText);

            if (endYear != startYear + 1) {
                showErrorDialog(dialog, "L'année de fin doit être l'année suivant l'année de début (ex: 2024-2025).");
                endYearField.requestFocus();
                return false;
            }

            // Validation de la sélection
            Etudiant selectedStudent = (Etudiant) studentComboBox.getSelectedItem();
            Matiere selectedMatiere = (Matiere) matiereComboBox.getSelectedItem();

            if (selectedStudent == null || selectedMatiere == null) {
                showErrorDialog(dialog, "Veuillez sélectionner un étudiant et une matière.");
                return false;
            }

            // Création/modification de la note
            Note newNote = new Note();
            newNote.setEtudiantId(selectedStudent.getId());
            newNote.setMatiereId(selectedMatiere.getId());
            newNote.setValeur(valeur);
            newNote.setSemestre((String) semestreComboBox.getSelectedItem());
            newNote.setAnnee(startYearText + "-" + endYearText);

            if (isEdit && note != null) {
                newNote.setId(note.getId());
                studentService.updateNote(note.getId(), newNote);
                showSuccessNotification("Note modifiée avec succès!");
            } else {
                studentService.addNote(newNote);
                showSuccessNotification("Note ajoutée avec succès!");
            }

            loadGrades();
            return true;

        } catch (ApiException ex) {
            showErrorDialog(dialog, "Erreur lors de l'enregistrement: " + ex.getMessage());
            return false;
        }
    }

    private void validateGradeField(JTextField field) {
        String text = field.getText().trim();
        if (!text.isEmpty()) {
            try {
                float value = Float.parseFloat(text);
                if (value < 0 || value > 20) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                            new ModernBorder(ERROR_COLOR),
                            BorderFactory.createEmptyBorder(12, 16, 12, 16)));
                    field.setToolTipText("La note doit être entre 0 et 20");
                } else {
                    field.setBorder(BorderFactory.createCompoundBorder(
                            new ModernBorder(SUCCESS_COLOR),
                            BorderFactory.createEmptyBorder(12, 16, 12, 16)));
                    field.setToolTipText("Note valide");
                }
            } catch (NumberFormatException e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new ModernBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)));
                field.setToolTipText("Veuillez saisir un nombre valide");
            }
        } else {
            field.setBorder(BorderFactory.createCompoundBorder(
                    new ModernBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            field.setToolTipText("");
        }
    }

    private void validateYearField(JTextField field) {
        String text = field.getText().trim();
        if (!text.isEmpty()) {
            if (Pattern.matches("\\d{4}", text)) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new ModernBorder(SUCCESS_COLOR),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)));
                field.setToolTipText("Année valide");
            } else {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new ModernBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)));
                field.setToolTipText("L'année doit contenir exactement 4 chiffres");
            }
        } else {
            field.setBorder(BorderFactory.createCompoundBorder(
                    new ModernBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            field.setToolTipText("");
        }
    }

    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        return label;
    }

    private JTextField createModernTextField(String text) {
        JTextField field = new JTextField(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(new Color(248, 250, 252));
        field.setForeground(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new ModernBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new ModernBorder(PRIMARY_COLOR),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new ModernBorder(BORDER_COLOR),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            }
        });

        return field;
    }

    private void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "❌ Erreur de Validation", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessNotification(String message) {
        // Create a modern toast notification
        JWindow toast = new JWindow();
        toast.setAlwaysOnTop(true);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);
                g2d.setColor(SUCCESS_COLOR);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);
                g2d.dispose();
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel messageLabel = new JLabel("✅ " + message);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageLabel.setForeground(Color.WHITE);

        panel.add(messageLabel, BorderLayout.CENTER);
        toast.add(panel);
        toast.pack();

        // Position at top-right of screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        toast.setLocation(screenSize.width - toast.getWidth() - 30, 30);

        toast.setVisible(true);

        // Auto-hide after 3 seconds
        Timer timer = new Timer(3000, e -> {
            toast.setVisible(false);
            toast.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Data loading methods
    private void loadData() {
        try {
            allStudents = studentService.getAllEtudiants(mainWindow.getCurrentResponsable().getId());
            allMatieres = studentService.getAllMatieres();
            allNiveaux = studentService.getAllNiveaux();

            studentComboBox.removeAllItems();
            for (Etudiant student : allStudents) {
                studentComboBox.addItem(student);
            }

            if (!allStudents.isEmpty()) {
                studentComboBox.setSelectedIndex(0);
                loadGrades();
            }
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadGrades() {
        Etudiant selectedStudent = (Etudiant) studentComboBox.getSelectedItem();
        if (selectedStudent == null)
            return;

        try {
            List<Note> notes = studentService.getNotesByEtudiant(selectedStudent.getId());
            gradesTableModel.setRowCount(0);

            for (Note note : notes) {
                Matiere matiere = getMatiereById(note.getMatiereId());
                gradesTableModel.addRow(new Object[] {
                        matiere != null ? matiere.getNom() : note.getMatiereId(),
                        matiere != null ? matiere.getCoefficient() : "N/A",
                        String.format("%.1f", note.getValeur()),
                        note.getSemestre(),
                        note.getAnnee(),
                        "Actions"
                });
            }
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStatistics() {
        String semestre = (String) semestreComboBox.getSelectedItem();
        String annee = (String) anneeComboBox.getSelectedItem();
        semestre = (semestre == null || semestre.equals("")) ? null : semestre;
        annee = (annee == null || annee.equals("")) ? null : annee;

        try {
            // Load student averages
            List<StudentAverageDTO> averages = studentService.getStudentAverages(semestre, annee);
            averagesTableModel.setRowCount(0);
            for (StudentAverageDTO avg : averages) {
                averagesTableModel.addRow(new Object[] {
                        avg.getMatricule(),
                        avg.getPrenom(),
                        avg.getNom(),
                        String.format("%.2f", avg.getMoyenne()),
                        avg.getAdmissionStatus(),
                        avg.getMention()
                });
            }

            // Load class statistics
            List<ClassStatisticsDTO> statsList = studentService.getAllClassStatistics(semestre, annee);
            classStatsTableModel.setRowCount(0);
            for (ClassStatisticsDTO stats : statsList) {
                classStatsTableModel.addRow(new Object[] {
                        stats.getNiveauNom(),
                        String.format("%.2f", stats.getMoyenneGenerale()),
                        String.format("%.2f", stats.getMaxMoyenne()),
                        String.format("%.2f", stats.getMinMoyenne()),
                        stats.getNiveauId()
                });
            }
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Matiere getMatiereById(String id) {
        if (allMatieres == null)
            return null;
        for (Matiere m : allMatieres) {
            if (m.getId().equals(id)) {
                return m;
            }
        }
        return null;
    }

    private void editGrade() {
        int selectedRow = gradesTable.getSelectedRow();
        if (selectedRow >= 0) {
            Etudiant selectedStudent = (Etudiant) studentComboBox.getSelectedItem();
            if (selectedStudent != null) {
                try {
                    List<Note> notes = studentService.getNotesByEtudiant(selectedStudent.getId());
                    if (selectedRow < notes.size()) {
                        showGradeFormDialog(notes.get(selectedRow));
                    }
                } catch (ApiException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void deleteGrade() {
        int selectedRow = gradesTable.getSelectedRow();
        if (selectedRow >= 0) {
            Etudiant selectedStudent = (Etudiant) studentComboBox.getSelectedItem();
            if (selectedStudent != null) {
                try {
                    List<Note> notes = studentService.getNotesByEtudiant(selectedStudent.getId());
                    if (selectedRow < notes.size()) {
                        int confirm = JOptionPane.showConfirmDialog(this,
                                "Êtes-vous sûr de vouloir supprimer cette note ?",
                                "Confirmer la suppression",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE);

                        if (confirm == JOptionPane.YES_OPTION) {
                            studentService.deleteNote(notes.get(selectedRow).getId());
                            loadGrades();
                            showSuccessNotification("Note supprimée avec succès!");
                        }
                    }
                } catch (ApiException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void showLevelDetailsDialog() {
        int selectedRow = classStatsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un niveau.", "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        loadData();
        loadGrades();
        loadStatistics();

        String niveauId = (String) classStatsTableModel.getValueAt(selectedRow, 4);
        String semestre = (String) semestreComboBox.getSelectedItem();
        String annee = (String) anneeComboBox.getSelectedItem();
        semestre = (semestre == null || semestre.equals("")) ? null : semestre;
        annee = (annee == null || annee.equals("")) ? null : annee;

        try {
            ClassStatisticsDTO stats = studentService.getClassStatistics(niveauId, semestre, annee, 5);
            showModernDetailsDialog(stats);
        } catch (ApiException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showModernDetailsDialog(ClassStatisticsDTO stats) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Détails du Niveau: " + stats.getNiveauNom(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(900, 700);
        dialog.setLocationRelativeTo(this);

        loadData();
        loadGrades();
        loadStatistics();

        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Header with statistics summary
        JPanel summaryPanel = createStatsSummaryPanel(stats);
        mainPanel.add(summaryPanel, BorderLayout.NORTH);

        // Student details tabs
        JTabbedPane studentTabs = createStudentDetailsTabs(stats);
        mainPanel.add(studentTabs, BorderLayout.CENTER);

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton closeButton = ModernComponents.createSecondaryButton("Fermer");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createStatsSummaryPanel(ClassStatisticsDTO stats) {
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 20, 15));
        summaryPanel.setBackground(CARD_COLOR);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        summaryPanel.add(createStatCard("📚 Niveau", stats.getNiveauNom(), PRIMARY_COLOR));
        summaryPanel.add(createStatCard("📊 Moyenne Générale", String.format("%.2f", stats.getMoyenneGenerale()),
                SUCCESS_COLOR));
        summaryPanel.add(createStatCard("📈 Moyenne Max", String.format("%.2f", stats.getMaxMoyenne()), WARNING_COLOR));
        summaryPanel.add(createStatCard("📉 Moyenne Min", String.format("%.2f", stats.getMinMoyenne()), ERROR_COLOR));

        return summaryPanel;
    }

    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);

                // Background
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);

                // Accent border
                g2d.setColor(accentColor);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);

                g2d.dispose();
            }
        };

        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_SECONDARY);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(TEXT_PRIMARY);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JTabbedPane createStudentDetailsTabs(ClassStatisticsDTO stats) {
        JTabbedPane studentTabs = new JTabbedPane();
        studentTabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        studentTabs.setBackground(CARD_COLOR);

        // Top students tab
        JPanel topStudentsPanel = createStudentListPanel(stats.getTopStudents(), "🏆 Top 5 Étudiants");
        studentTabs.addTab("🏆 Top 5", topStudentsPanel);

        // All students tab
        JPanel allStudentsPanel = createStudentListPanel(stats.getAllStudents(), "👥 Tous les Étudiants");
        studentTabs.addTab("👥 Tous", allStudentsPanel);

        return studentTabs;
    }

    private JPanel createStudentListPanel(List<StudentAverageDTO> students, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);

        String[] columns = { "Matricule", "Prénom", "Nom", "Moyenne", "Statut", "Mention" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = ModernComponents.createModernTable(model);

        for (StudentAverageDTO student : students) {
            model.addRow(new Object[] {
                    student.getMatricule(),
                    student.getPrenom(),
                    student.getNom(),
                    String.format("%.2f", student.getMoyenne()),
                    student.getAdmissionStatus(),
                    student.getMention()
            });
        }

        JScrollPane scrollPane = ModernComponents.createModernScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Custom border class
    private static class ModernBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;

        public ModernBorder() {
            this(BORDER_COLOR, 1);
        }

        public ModernBorder(Color color) {
            this(color, 2);
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
            g2d.drawRoundRect(x, y, width - 1, height - 1, 8, 8);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }
    }

    // Modern button renderers and editors
    private class ModernActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton;
        private JButton deleteButton;

        public ModernActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 5));
            setOpaque(false);

            editButton = createActionButton("✏️", WARNING_COLOR);
            deleteButton = createActionButton("🗑️", ERROR_COLOR);

            add(editButton);
            add(deleteButton);
        }

        private JButton createActionButton(String text, Color color) {
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

                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };

            button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            button.setForeground(Color.WHITE);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(32, 28));
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

    private class ModernActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;
        private final Runnable editAction;
        private final Runnable deleteAction;

        public ModernActionButtonEditor(Runnable editAction, Runnable deleteAction) {
            super(new JTextField());
            this.editAction = editAction;
            this.deleteAction = deleteAction;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
            panel.setOpaque(false);

            editButton = createActionButton("✏️", WARNING_COLOR);
            deleteButton = createActionButton("🗑️", ERROR_COLOR);

            editButton.addActionListener(e -> {
                fireEditingStopped();
                editAction.run();
            });

            deleteButton.addActionListener(e -> {
                fireEditingStopped();
                deleteAction.run();
            });

            panel.add(editButton);
            panel.add(deleteButton);
        }

        private JButton createActionButton(String text, Color color) {
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

                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };

            button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            button.setForeground(Color.WHITE);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(32, 28));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            return button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }

    private class ModernDetailsButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton button;

        public ModernDetailsButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
            setOpaque(false);

            button = createDetailsButton();
            add(button);
        }

        private JButton createDetailsButton() {
            JButton button = new JButton("📋 Détails") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    if (getModel().isPressed()) {
                        g2d.setColor(SECONDARY_COLOR.darker());
                    } else if (getModel().isRollover()) {
                        g2d.setColor(SECONDARY_COLOR.brighter());
                    } else {
                        g2d.setColor(SECONDARY_COLOR);
                    }

                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };

            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setForeground(Color.WHITE);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(80, 30));
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

    private class ModernDetailsButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton button;
        private final Runnable action;

        public ModernDetailsButtonEditor(Runnable action) {
            super(new JTextField());
            this.action = action;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 5));
            panel.setOpaque(false);

            button = createDetailsButton();
            button.addActionListener(e -> {
                fireEditingStopped();
                action.run();
            });

            panel.add(button);
        }

        private JButton createDetailsButton() {
            JButton button = new JButton("📋 Détails") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    if (getModel().isPressed()) {
                        g2d.setColor(SECONDARY_COLOR.darker());
                    } else if (getModel().isRollover()) {
                        g2d.setColor(SECONDARY_COLOR.brighter());
                    } else {
                        g2d.setColor(SECONDARY_COLOR);
                    }

                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };

            button.setFont(new Font("Segoe UI", Font.BOLD, 12));
            button.setForeground(Color.WHITE);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(80, 30));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            return button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Détails";
        }
    }
}