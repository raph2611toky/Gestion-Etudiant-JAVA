package com.studentmanagement.ui.parameters;

import com.studentmanagement.model.Mention;
import com.studentmanagement.model.Niveau;
import com.studentmanagement.model.Parcours;
import com.studentmanagement.model.Matiere;
import com.studentmanagement.service.ParameterService;
import com.studentmanagement.service.ApiException;
import com.studentmanagement.ui.common.MainWindow;
import com.studentmanagement.ui.common.SidebarUtil;
import com.studentmanagement.ui.common.ModernComponents;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ParametersFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private ParameterService parameterService;
    private MainWindow mainWindow;
    private JTabbedPane tabbedPane;
    private DefaultTableModel mentionTableModel, niveauTableModel, parcoursTableModel, matiereTableModel;
    private List<Mention> allMentions;
    private List<Niveau> allNiveaux;
    private List<Parcours> allParcours;
    private List<Matiere> allMatieres;
    private JComboBox<Mention> mentionComboBox;
    private JComboBox<Niveau> niveauComboBox;
    private JButton addParcoursButton;
    private JButton addMatiereButton;

    // Palette de couleurs modernes
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

    public ParametersFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.parameterService = new ParameterService();
        this.parameterService.setJwtToken(mainWindow.getCurrentResponsable().getToken());
        initializeUI();
        loadAllData();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel sidebar = SidebarUtil.createSidebar(mainWindow, "Settings");
        add(sidebar, BorderLayout.WEST);

        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);
    }

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(BACKGROUND_COLOR);
        mainContent.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel headerPanel = createHeaderPanel();
        mainContent.add(headerPanel, BorderLayout.NORTH);

        tabbedPane = createModernTabbedPane();
        mainContent.add(tabbedPane, BorderLayout.CENTER);

        return mainContent;
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BACKGROUND_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel("Gestion des Paramètres");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel breadcrumbLabel = new JLabel("Dashboard > Paramètres > Configuration");
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

        tabbedPane.addTab("🏆 Mentions", createTablePanel("Mention", 
            mentionTableModel = createTableModel(new String[]{"Nom", "Description", "Actions"}), 
            this::addMention, this::editMention));
        
        tabbedPane.addTab("📚 Niveaux", createTablePanel("Niveau", 
            niveauTableModel = createTableModel(new String[]{"Nom", "Description", "Actions"}), 
            this::addNiveau, this::editNiveau));
        
        tabbedPane.addTab("🎯 Parcours", createParcoursTablePanel());
        tabbedPane.addTab("📖 Matières", createMatiereTablePanel());

        return tabbedPane;
    }

    private DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == columns.length - 1; // Seule la colonne "Actions" est éditable
            }
        };
    }

    private JPanel createTablePanel(String entityName, DefaultTableModel tableModel, Runnable addAction, Runnable editAction) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel header = createEntityHeader(entityName, addAction);
        panel.add(header, BorderLayout.NORTH);

        JTable table = ModernComponents.createModernTable(tableModel);
        table.getColumn("Actions").setCellRenderer(new ModernActionButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ModernActionButtonEditor(editAction, () -> deleteEntity(entityName)));

        JScrollPane scrollPane = ModernComponents.createModernScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEntityHeader(String entityName, Runnable addAction) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Gestion des " + entityName + "s");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);

        JButton addButton = ModernComponents.createPrimaryButton("➕ Ajouter " + entityName);
        addButton.addActionListener(e -> addAction.run());

        header.add(titleLabel, BorderLayout.WEST);
        header.add(addButton, BorderLayout.EAST);

        return header;
    }

    private JPanel createParcoursTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel header = createParcoursHeader();
        panel.add(header, BorderLayout.NORTH);

        parcoursTableModel = createTableModel(new String[]{"Nom", "Mention", "Description", "Actions"});
        JTable table = ModernComponents.createModernTable(parcoursTableModel);
        table.getColumn("Actions").setCellRenderer(new ModernActionButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ModernActionButtonEditor(this::editParcours, () -> deleteEntity("Parcours")));

        JScrollPane scrollPane = ModernComponents.createModernScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createParcoursHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(CARD_COLOR);

        JLabel titleLabel = new JLabel("Gestion des Parcours");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(CARD_COLOR);

        JLabel mentionLabel = new JLabel("Filtrer par Mention:");
        mentionLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        mentionLabel.setForeground(TEXT_PRIMARY);

        mentionComboBox = ModernComponents.createModernComboBox();
        mentionComboBox.setPreferredSize(new Dimension(200, 40));
        mentionComboBox.addActionListener(e -> loadParcours());

        filterPanel.add(mentionLabel);
        filterPanel.add(mentionComboBox);

        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(filterPanel, BorderLayout.SOUTH);

        addParcoursButton = ModernComponents.createPrimaryButton("➕ Ajouter Parcours");
        addParcoursButton.addActionListener(e -> addParcours());

        header.add(leftPanel, BorderLayout.WEST);
        header.add(addParcoursButton, BorderLayout.EAST);

        return header;
    }

    private JPanel createMatiereTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel header = createMatiereHeader();
        panel.add(header, BorderLayout.NORTH);

        matiereTableModel = createTableModel(new String[]{"Nom", "Catégorie", "Coefficient", "Niveau", "Actions"});
        JTable table = ModernComponents.createModernTable(matiereTableModel);
        table.getColumn("Actions").setCellRenderer(new ModernActionButtonRenderer());
        table.getColumn("Actions").setCellEditor(new ModernActionButtonEditor(this::editMatiere, () -> deleteEntity("Matière")));

        JScrollPane scrollPane = ModernComponents.createModernScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMatiereHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CARD_COLOR);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(CARD_COLOR);

        JLabel titleLabel = new JLabel("Gestion des Matières");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setBackground(CARD_COLOR);

        JLabel niveauLabel = new JLabel("Filtrer par Niveau:");
        niveauLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        niveauLabel.setForeground(TEXT_PRIMARY);

        niveauComboBox = ModernComponents.createModernComboBox();
        niveauComboBox.setPreferredSize(new Dimension(200, 40));
        niveauComboBox.addActionListener(e -> loadMatieres());

        filterPanel.add(niveauLabel);
        filterPanel.add(niveauComboBox);

        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(filterPanel, BorderLayout.SOUTH);

        addMatiereButton = ModernComponents.createPrimaryButton("➕ Ajouter Matière");
        addMatiereButton.addActionListener(e -> addMatiere());

        header.add(leftPanel, BorderLayout.WEST);
        header.add(addMatiereButton, BorderLayout.EAST);

        return header;
    }

    // Méthodes de chargement des données
    private void loadAllData() {
        loadMentions();
        loadNiveaux();
        loadMatieres();
    }

    private void loadMentions() {
        try {
            allMentions = parameterService.getAllMentions();
            mentionTableModel.setRowCount(0);
            for (Mention mention : allMentions) {
                mentionTableModel.addRow(new Object[]{mention.getNom(), mention.getDescription(), "Actions"});
            }

            mentionComboBox.removeAllItems();
            if (allMentions.isEmpty()) {
                mentionComboBox.addItem(new Mention("0", "Aucune mention disponible", ""));
                addParcoursButton.setEnabled(false);
            } else {
                for (Mention mention : allMentions) {
                    mentionComboBox.addItem(mention);
                }
                mentionComboBox.setSelectedIndex(0);
                addParcoursButton.setEnabled(true);
                loadParcours();
            }
        } catch (ApiException ex) {
            showErrorNotification("Erreur lors du chargement des mentions: " + ex.getMessage());
        }
    }

    private void loadNiveaux() {
        try {
            allNiveaux = parameterService.getAllNiveaux();
            niveauTableModel.setRowCount(0);
            for (Niveau niveau : allNiveaux) {
                niveauTableModel.addRow(new Object[]{niveau.getNom(), niveau.getDescription(), "Actions"});
            }

            niveauComboBox.removeAllItems();
            niveauComboBox.addItem(new Niveau("0", "TOUS", ""));
            if (allNiveaux.isEmpty()) {
                addMatiereButton.setEnabled(false);
            } else {
                for (Niveau niveau : allNiveaux) {
                    niveauComboBox.addItem(niveau);
                }
                niveauComboBox.setSelectedIndex(0);
                addMatiereButton.setEnabled(true);
            }
        } catch (ApiException ex) {
            showErrorNotification("Erreur lors du chargement des niveaux: " + ex.getMessage());
        }
    }

    private void loadParcours() {
        try {
            Mention selectedMention = (Mention) mentionComboBox.getSelectedItem();
            if (selectedMention == null || selectedMention.getId().equals("0")) {
                parcoursTableModel.setRowCount(0);
                return;
            }

            allParcours = parameterService.getAllParcoursByMention(selectedMention.getId());
            parcoursTableModel.setRowCount(0);

            Map<String, String> mentionMap = allMentions.stream()
                    .collect(Collectors.toMap(Mention::getId, Mention::getNom));

            for (Parcours parcours : allParcours) {
                String mentionNom = mentionMap.getOrDefault(parcours.getMentionId(), "Inconnu");
                parcoursTableModel.addRow(new Object[]{parcours.getNom(), mentionNom, parcours.getDescription(), "Actions"});
            }
        } catch (ApiException ex) {
            showErrorNotification("Erreur lors du chargement des parcours: " + ex.getMessage());
        }
    }

    private void loadMatieres() {
        try {
            Niveau selectedNiveau = (Niveau) niveauComboBox.getSelectedItem();
            if (selectedNiveau == null) {
                matiereTableModel.setRowCount(0);
                return;
            }

            if (selectedNiveau.getId().equals("0")) {
                allMatieres = parameterService.getAllMatieres();
            } else {
                allMatieres = parameterService.getAllMatieresByNiveau(selectedNiveau.getId());
            }

            matiereTableModel.setRowCount(0);
            Map<String, String> niveauMap = allNiveaux.stream()
                    .collect(Collectors.toMap(Niveau::getId, Niveau::getNom));

            for (Matiere matiere : allMatieres) {
                String niveauNom = niveauMap.getOrDefault(matiere.getNiveauId(), "Inconnu");
                matiereTableModel.addRow(new Object[]{
                    matiere.getNom(),
                    matiere.getCategorie(),
                    matiere.getCoefficient(),
                    niveauNom,
                    "Actions"
                });
            }
        } catch (ApiException ex) {
            showErrorNotification("Erreur lors du chargement des matières: " + ex.getMessage());
        }
    }

    // Opérations CRUD
    private void addMention() {
        showFormDialog("Ajouter une Mention", null, null, (nom, description, refComboBox) -> {
            if (validateBasicFields(nom, description)) {
                Mention mention = new Mention();
                mention.setNom(nom);
                mention.setDescription(description);
                parameterService.addMention(mention);
                loadMentions();
                showSuccessNotification("Mention ajoutée avec succès!");
            }
        }, null, null);
    }

    private void editMention() {
        int selectedRow = getSelectedRow(mentionTableModel);
        if (selectedRow >= 0 && selectedRow < allMentions.size()) {
            Mention mention = allMentions.get(selectedRow);
            showFormDialog("Modifier la Mention", mention.getNom(), mention.getDescription(), (nom, description, refComboBox) -> {
                if (validateBasicFields(nom, description)) {
                    mention.setNom(nom);
                    mention.setDescription(description);
                    parameterService.updateMention(mention.getId(), mention);
                    loadMentions();
                    showSuccessNotification("Mention modifiée avec succès!");
                }
            }, null, null);
        }
    }

    private void addNiveau() {
        showFormDialog("Ajouter un Niveau", null, null, (nom, description, refComboBox) -> {
            if (validateBasicFields(nom, description)) {
                Niveau niveau = new Niveau();
                niveau.setNom(nom);
                niveau.setDescription(description);
                parameterService.addNiveau(niveau);
                loadNiveaux();
                showSuccessNotification("Niveau ajouté avec succès!");
            }
        }, null, null);
    }

    private void editNiveau() {
        int selectedRow = getSelectedRow(niveauTableModel);
        if (selectedRow >= 0 && selectedRow < allNiveaux.size()) {
            Niveau niveau = allNiveaux.get(selectedRow);
            showFormDialog("Modifier le Niveau", niveau.getNom(), niveau.getDescription(), (nom, description, refComboBox) -> {
                if (validateBasicFields(nom, description)) {
                    niveau.setNom(nom);
                    niveau.setDescription(description);
                    parameterService.updateNiveau(niveau.getId(), niveau);
                    loadNiveaux();
                    showSuccessNotification("Niveau modifié avec succès!");
                }
            }, null, null);
        }
    }

    private void addParcours() {
        if (allMentions.isEmpty()) {
            showErrorNotification("Aucune mention disponible. Veuillez d'abord ajouter une mention.");
            return;
        }

        Mention selectedMention = (Mention) mentionComboBox.getSelectedItem();
        showFormDialog("Ajouter un Parcours", null, null, (nom, description, refComboBox) -> {
            if (validateBasicFields(nom, description) && validateReferenceSelection(refComboBox, "Mention")) {
                Parcours parcours = new Parcours();
                parcours.setNom(nom);
                parcours.setDescription(description);
                parcours.setMentionId(getSelectedReferenceId(allMentions, refComboBox));
                parameterService.addParcours(parcours);
                loadParcours();
                showSuccessNotification("Parcours ajouté avec succès!");
            }
        }, allMentions, selectedMention);
    }

    private void editParcours() {
        int selectedRow = getSelectedRow(parcoursTableModel);
        if (selectedRow >= 0 && selectedRow < allParcours.size()) {
            Parcours parcours = allParcours.get(selectedRow);
            Mention currentMention = allMentions.stream()
                    .filter(m -> m.getId().equals(parcours.getMentionId()))
                    .findFirst().orElse(null);

            showFormDialog("Modifier le Parcours", parcours.getNom(), parcours.getDescription(), (nom, description, refComboBox) -> {
                if (validateBasicFields(nom, description) && validateReferenceSelection(refComboBox, "Mention")) {
                    parcours.setNom(nom);
                    parcours.setDescription(description);
                    parcours.setMentionId(getSelectedReferenceId(allMentions, refComboBox));
                    parameterService.updateParcours(parcours.getId(), parcours);
                    loadParcours();
                    showSuccessNotification("Parcours modifié avec succès!");
                }
            }, allMentions, currentMention);
        }
    }

    private void addMatiere() {
        if (allNiveaux.isEmpty()) {
            showErrorNotification("Aucun niveau disponible. Veuillez d'abord ajouter un niveau.");
            return;
        }
        showMatiereFormDialog("Ajouter une Matière", null, false);
    }

    private void editMatiere() {
        int selectedRow = getSelectedRow(matiereTableModel);
        if (selectedRow >= 0 && selectedRow < allMatieres.size()) {
            Matiere matiere = allMatieres.get(selectedRow);
            showMatiereFormDialog("Modifier la Matière", matiere, true);
        }
    }

    private void showMatiereFormDialog(String title, Matiere matiere, boolean isEdit) {
        JDialog dialog = new JDialog(mainWindow, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(550, 650);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = createModernDialogPanel();

        JPanel headerPanel = createDialogHeader(title, "Veuillez remplir tous les champs requis");
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nomLabel = createFormLabel("Nom de la Matière *");
        formPanel.add(nomLabel, gbc);

        gbc.gridy = 1;
        JTextField nomField = createModernTextField(matiere != null ? matiere.getNom() : "");
        nomField.setPreferredSize(new Dimension(450, 45));
        formPanel.add(nomField, gbc);

        gbc.gridy = 2;
        JLabel categorieLabel = createFormLabel("Catégorie *");
        formPanel.add(categorieLabel, gbc);

        gbc.gridy = 3;
        JTextField categorieField = createModernTextField(matiere != null ? matiere.getCategorie() : "");
        categorieField.setPreferredSize(new Dimension(450, 45));
        formPanel.add(categorieField, gbc);

        gbc.gridy = 4;
        JLabel coefficientLabel = createFormLabel("Coefficient *");
        formPanel.add(coefficientLabel, gbc);

        gbc.gridy = 5;
        JTextField coefficientField = createModernTextField(matiere != null ? String.valueOf(matiere.getCoefficient()) : "");
        coefficientField.setPreferredSize(new Dimension(450, 45));
        coefficientField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateCoefficientField(coefficientField);
            }
        });
        formPanel.add(coefficientField, gbc);

        gbc.gridy = 6;
        JLabel niveauLabel = createFormLabel("Niveau *");
        formPanel.add(niveauLabel, gbc);

        gbc.gridy = 7;
        JComboBox<Niveau> niveauCombo = ModernComponents.createModernComboBox();
        niveauCombo.setPreferredSize(new Dimension(450, 45));
        for (Niveau niveau : allNiveaux) {
            niveauCombo.addItem(niveau);
        }
        if (isEdit && matiere != null && matiere.getNiveauId() != null) {
            for (Niveau niveau : allNiveaux) {
                if (niveau.getId().equals(matiere.getNiveauId())) {
                    niveauCombo.setSelectedItem(niveau);
                    break;
                }
            }
        }
        formPanel.add(niveauCombo, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        JButton saveButton = ModernComponents.createPrimaryButton("💾 Enregistrer");
        JButton cancelButton = ModernComponents.createSecondaryButton("❌ Annuler");

        saveButton.addActionListener(e -> {
            if (validateAndSaveMatiere(dialog, nomField, categorieField, coefficientField, niveauCombo, matiere, isEdit)) {
                dialog.dispose();
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

    private boolean validateAndSaveMatiere(JDialog dialog, JTextField nomField, JTextField categorieField,
            JTextField coefficientField, JComboBox<Niveau> niveauCombo, Matiere matiere, boolean isEdit) {
        try {
            String nom = nomField.getText().trim();
            String categorie = categorieField.getText().trim();
            String coefficientText = coefficientField.getText().trim();
            Niveau selectedNiveau = (Niveau) niveauCombo.getSelectedItem();

            if (nom.isEmpty() || categorie.isEmpty() || coefficientText.isEmpty() || selectedNiveau == null) {
                showErrorDialog(dialog, "Tous les champs marqués d'un * sont obligatoires.");
                return false;
            }

            int coefficient;
            try {
                coefficient = Integer.parseInt(coefficientText);
                if (coefficient <= 0) {
                    showErrorDialog(dialog, "Le coefficient doit être un nombre entier positif.");
                    coefficientField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException ex) {
                showErrorDialog(dialog, "Veuillez saisir un coefficient valide (nombre entier).");
                coefficientField.requestFocus();
                return false;
            }

            Matiere newMatiere = new Matiere();
            newMatiere.setNom(nom);
            newMatiere.setCategorie(categorie);
            newMatiere.setCoefficient(coefficient);
            newMatiere.setNiveauId(selectedNiveau.getId());

            if (isEdit && matiere != null) {
                newMatiere.setId(matiere.getId());
                parameterService.updateMatiere(matiere.getId(), newMatiere);
                showSuccessNotification("Matière modifiée avec succès!");
            } else {
                parameterService.addMatiere(newMatiere);
                showSuccessNotification("Matière ajoutée avec succès!");
            }

            loadMatieres();
            return true;

        } catch (ApiException ex) {
            showErrorDialog(dialog, "Erreur lors de l'enregistrement: " + ex.getMessage());
            return false;
        }
    }

    private void validateCoefficientField(JTextField field) {
        String text = field.getText().trim();
        if (!text.isEmpty()) {
            try {
                int value = Integer.parseInt(text);
                if (value <= 0) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        new ModernBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)
                    ));
                    field.setToolTipText("Le coefficient doit être positif");
                } else {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        new ModernBorder(SUCCESS_COLOR),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)
                    ));
                    field.setToolTipText("Coefficient valide");
                }
            } catch (NumberFormatException e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new ModernBorder(ERROR_COLOR),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
                field.setToolTipText("Veuillez saisir un nombre entier");
            }
        } else {
            field.setBorder(BorderFactory.createCompoundBorder(
                new ModernBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
            ));
            field.setToolTipText("");
        }
    }

    private void deleteEntity(String entityName) {
        int selectedRow = -1;
        String itemName = "";

        switch (tabbedPane.getSelectedIndex()) {
            case 0: // Mentions
                selectedRow = getSelectedRow(mentionTableModel);
                if (selectedRow >= 0 && selectedRow < allMentions.size()) {
                    final String mentionId = allMentions.get(selectedRow).getId();
                    itemName = allMentions.get(selectedRow).getNom();
                    showModernConfirmDialog(
                        "Confirmer la suppression",
                        "Êtes-vous sûr de vouloir supprimer \"" + itemName + "\" ?",
                        "Cette action est irréversible.",
                        () -> performDelete(entityName, mentionId)
                    );
                }
                break;
            case 1: // Niveaux
                selectedRow = getSelectedRow(niveauTableModel);
                if (selectedRow >= 0 && selectedRow < allNiveaux.size()) {
                    final String niveauId = allNiveaux.get(selectedRow).getId();
                    itemName = allNiveaux.get(selectedRow).getNom();
                    showModernConfirmDialog(
                        "Confirmer la suppression",
                        "Êtes-vous sûr de vouloir supprimer \"" + itemName + "\" ?",
                        "Cette action est irréversible.",
                        () -> performDelete(entityName, niveauId)
                    );
                }
                break;
            case 2: // Parcours
                selectedRow = getSelectedRow(parcoursTableModel);
                if (selectedRow >= 0 && selectedRow < allParcours.size()) {
                    final String parcoursId = allParcours.get(selectedRow).getId();
                    itemName = allParcours.get(selectedRow).getNom();
                    showModernConfirmDialog(
                        "Confirmer la suppression",
                        "Êtes-vous sûr de vouloir supprimer \"" + itemName + "\" ?",
                        "Cette action est irréversible.",
                        () -> performDelete(entityName, parcoursId)
                    );
                }
                break;
            case 3: // Matières
                selectedRow = getSelectedRow(matiereTableModel);
                if (selectedRow >= 0 && selectedRow < allMatieres.size()) {
                    final String matiereId = allMatieres.get(selectedRow).getId();
                    itemName = allMatieres.get(selectedRow).getNom();
                    showModernConfirmDialog(
                        "Confirmer la suppression",
                        "Êtes-vous sûr de vouloir supprimer \"" + itemName + "\" ?",
                        "Cette action est irréversible.",
                        () -> performDelete(entityName, matiereId)
                    );
                }
                break;
        }
    }

    private void performDelete(String entityName, String id) {
        try {
            switch (entityName.toLowerCase()) {
                case "mention":
                    parameterService.deleteMention(id);
                    loadMentions();
                    break;
                case "niveau":
                    parameterService.deleteNiveau(id);
                    loadNiveaux();
                    break;
                case "parcours":
                    parameterService.deleteParcours(id);
                    loadParcours();
                    break;
                case "matière":
                    parameterService.deleteMatiere(id);
                    loadMatieres();
                    break;
            }
            showSuccessNotification(entityName + " supprimé(e) avec succès!");
        } catch (ApiException ex) {
            showErrorNotification("Erreur lors de la suppression: " + ex.getMessage());
        }
    }

    // Méthodes utilitaires
    private int getSelectedRow(DefaultTableModel tableModel) {
        Component selectedComponent = tabbedPane.getSelectedComponent();
        if (selectedComponent instanceof JPanel) {
            return findTableInPanel((JPanel) selectedComponent).map(JTable::getSelectedRow).orElse(-1);
        }
        return -1;
    }

    private java.util.Optional<JTable> findTableInPanel(JPanel panel) {
        for (Component comp : panel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                if (scrollPane.getViewport().getView() instanceof JTable) {
                    return java.util.Optional.of((JTable) scrollPane.getViewport().getView());
                }
            }
        }
        return java.util.Optional.empty();
    }

    private String getSelectedReferenceId(List<?> referenceList, JComboBox<?> refComboBox) {
        if (refComboBox != null && refComboBox.getSelectedItem() != null) {
            Object selectedItem = refComboBox.getSelectedItem();
            if (selectedItem instanceof Mention) {
                return ((Mention) selectedItem).getId();
            } else if (selectedItem instanceof Niveau) {
                return ((Niveau) selectedItem).getId();
            }
        }
        return "";
    }

    private void showFormDialog(String title, String currentNom, String currentDescription,
            FormSaveAction saveAction, List<Mention> referenceList, Mention defaultMention) {

        JDialog dialog = new JDialog(mainWindow, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 550);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = createModernDialogPanel();

        JPanel headerPanel = createDialogHeader(title, "Veuillez remplir tous les champs requis");
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nomLabel = createFormLabel("Nom *");
        formPanel.add(nomLabel, gbc);

        gbc.gridy = 1;
        JTextField nomField = createModernTextField(currentNom != null ? currentNom : "");
        nomField.setPreferredSize(new Dimension(400, 45));
        formPanel.add(nomField, gbc);

        gbc.gridy = 2;
        JLabel descriptionLabel = createFormLabel("Description");
        formPanel.add(descriptionLabel, gbc);

        gbc.gridy = 3;
        JTextField descriptionField = createModernTextField(currentDescription != null ? currentDescription : "");
        descriptionField.setPreferredSize(new Dimension(400, 45));
        formPanel.add(descriptionField, gbc);

        JComboBox<Mention> refComboBox = null;
        if (referenceList != null && !referenceList.isEmpty()) {
            gbc.gridy = 4;
            JLabel refLabel = createFormLabel("Mention *");
            formPanel.add(refLabel, gbc);

            gbc.gridy = 5;
            refComboBox = ModernComponents.createModernComboBox();
            refComboBox.setPreferredSize(new Dimension(400, 45));
            for (Mention mention : referenceList) {
                refComboBox.addItem(mention);
            }
            if (defaultMention != null) {
                refComboBox.setSelectedItem(defaultMention);
            }
            formPanel.add(refComboBox, gbc);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        JButton saveButton = ModernComponents.createPrimaryButton("💾 Enregistrer");
        JButton cancelButton = ModernComponents.createSecondaryButton("❌ Annuler");

        JComboBox<Mention> finalRefComboBox = refComboBox;
        saveButton.addActionListener(e -> {
            String nomText = nomField.getText().trim();
            String descriptionText = descriptionField.getText().trim();

            if (validateBasicFields(nomText, descriptionText) &&
                (finalRefComboBox == null || validateReferenceSelection(finalRefComboBox, "Mention"))) {
                try {
                    saveAction.save(nomText, descriptionText, finalRefComboBox);
                    dialog.dispose();
                } catch (ApiException ex) {
                    showErrorDialog(dialog, "Erreur lors de l'enregistrement: " + ex.getMessage());
                }
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

    // Méthodes de validation
    private boolean validateBasicFields(String nom, String description) {
        if (nom.isEmpty()) {
            showErrorNotification("Le nom est obligatoire.");
            return false;
        }
        if (nom.length() < 2) {
            showErrorNotification("Le nom doit contenir au moins 2 caractères.");
            return false;
        }
        return true;
    }

    private boolean validateReferenceSelection(JComboBox<?> refComboBox, String fieldName) {
        if (refComboBox != null && refComboBox.getSelectedItem() == null) {
            showErrorNotification("Veuillez sélectionner une " + fieldName.toLowerCase() + ".");
            return false;
        }
        return true;
    }

    // Méthodes d'aide pour l'interface utilisateur
    private JPanel createModernDialogPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_COLOR);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.dispose();
            }
        };
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        return panel;
    }

    private JPanel createDialogHeader(String title, String subtitle) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(CARD_COLOR);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        return headerPanel;
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
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new ModernBorder(PRIMARY_COLOR),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new ModernBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)
                ));
            }
        });

        return field;
    }

    private void showModernConfirmDialog(String title, String message, String subtitle, Runnable onConfirm) {
        JDialog dialog = new JDialog(mainWindow, title, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = createModernDialogPanel();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(CARD_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel iconLabel = new JLabel("⚠️");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        messageLabel.setForeground(TEXT_PRIMARY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        contentPanel.add(iconLabel);
        contentPanel.add(Box.createVerticalStrut(15));
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(subtitleLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton confirmButton = ModernComponents.createErrorButton("🗑️ Supprimer");
        JButton cancelButton = ModernComponents.createSecondaryButton("❌ Annuler");

        confirmButton.addActionListener(e -> {
            dialog.dispose();
            onConfirm.run();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void showErrorDialog(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "❌ Erreur de Validation", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessNotification(String message) {
        createToastNotification(message, SUCCESS_COLOR, "✅");
    }

    private void showErrorNotification(String message) {
        createToastNotification(message, ERROR_COLOR, "❌");
    }

    private void createToastNotification(String message, Color bgColor, String icon) {
        JWindow toast = new JWindow();
        toast.setAlwaysOnTop(true);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);
                g2d.dispose();
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel messageLabel = new JLabel(icon + " " + message);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageLabel.setForeground(Color.WHITE);

        panel.add(messageLabel, BorderLayout.CENTER);
        toast.add(panel);
        toast.pack();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        toast.setLocation(screenSize.width - toast.getWidth() - 30, 30);

        toast.setVisible(true);

        Timer timer = new Timer(3000, e -> {
            toast.setVisible(false);
            toast.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Classe de bordure personnalisée
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

    // Rendu et éditeur des boutons d'action modernes
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
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                panel.setBackground(table.getBackground());
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return null; // Pas de valeur à retourner car c'est pour des actions
        }
    }

    @FunctionalInterface
    private interface FormSaveAction {
        void save(String nom, String description, JComboBox<?> refComboBox) throws ApiException;
    }
}