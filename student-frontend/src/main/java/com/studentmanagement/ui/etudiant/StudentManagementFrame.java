package com.studentmanagement.ui.etudiant;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import com.studentmanagement.model.Etudiant;
import com.studentmanagement.model.Niveau;
import com.studentmanagement.model.Parcours;
import com.studentmanagement.model.Matiere;
import com.studentmanagement.model.Note;
import com.studentmanagement.service.ApiException;
import com.studentmanagement.service.StudentService;
import com.studentmanagement.ui.common.MainWindow;
import com.studentmanagement.ui.common.SidebarUtil;

public class StudentManagementFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Champs du formulaire
    private JTextField matriculeField, firstNameField, lastNameField, emailField, adresseField, niveauClasseField;
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
    private JDialog currentDialog;

    // Palette de couleurs ultra-moderne
    private static final Color PRIMARY_COLOR = new Color(99, 102, 241);
    private static final Color PRIMARY_HOVER = new Color(79, 70, 229);
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
    private static final Color NOTES_COLOR = new Color(59, 130, 246);
    private static final Color ACCENT_BLUE = new Color(14, 165, 233);

    public StudentManagementFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.studentService = new StudentService();
        this.responsableId = mainWindow.getCurrentResponsable() != null ? 
            mainWindow.getCurrentResponsable().getId() : null;
        
        if (this.responsableId != null) {
            this.studentService.setJwtToken(mainWindow.getCurrentResponsable().getToken());
        } else {
            showErrorNotification("Erreur: Responsable non identifié.");
        }

        initializeUI();
        loadNiveauxAndParcours();
        
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentShown(ComponentEvent e) {
                loadStudents();
            }
        });
    }

    private Font getModernFont(int style, int size) {
        return new Font("Segoe UI", style, size);
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(BACKGROUND_COLOR);

        JPanel sidebar = SidebarUtil.createSidebar(mainWindow, "Students");
        add(sidebar, BorderLayout.WEST);

        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);
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

        return mainContent;
    }

    private JPanel createEnhancedHeader() {
        JPanel headerSection = new JPanel(new BorderLayout());
        headerSection.setBackground(BACKGROUND_COLOR);
        headerSection.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Gestion des Étudiants");
        titleLabel.setFont(getModernFont(Font.BOLD, 32));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setIcon(createVectorIcon(IconType.GRADUATION, 32, PRIMARY_COLOR));

        JLabel breadcrumbLabel = new JLabel("Dashboard > Gestion des Étudiants");
        breadcrumbLabel.setFont(getModernFont(Font.PLAIN, 14));
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

        niveauFilterComboBox = createUltraModernFilterComboBox("TOUS LES NIVEAUX");
        for (Niveau niveau : allNiveaux) {
            if (niveau != null && niveau.getNom() != null) {
                niveauFilterComboBox.addItem(niveau.getNom());
            }
        }
        niveauFilterComboBox.addActionListener(_ -> filterStudents());
        filtersPanel.add(niveauFilterComboBox);

        parcoursFilterComboBox = createUltraModernFilterComboBox("TOUS LES PARCOURS");
        for (Parcours parcours : allParcours) {
            if (parcours != null && parcours.getNom() != null) {
                parcoursFilterComboBox.addItem(parcours.getNom());
            }
        }
        parcoursFilterComboBox.addActionListener(_ -> filterStudents());
        filtersPanel.add(parcoursFilterComboBox);

        return filtersPanel;
    }

    // Amélioration 1: Champ de recherche avec largeur fixe
    private JTextField createEnhancedSearchField() {
        JTextField searchField = new JTextField("Rechercher des étudiants...") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre douce
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 16, 16);
                
                // Fond avec gradient subtil
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 16, 16);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        searchField.setFont(getModernFont(Font.PLAIN, 14));
        // CORRECTION: Largeur fixe au lieu de préférée
        searchField.setPreferredSize(new Dimension(320, 48));
        searchField.setMinimumSize(new Dimension(320, 48));
        searchField.setMaximumSize(new Dimension(320, 48));
        searchField.setBackground(CARD_COLOR);
        searchField.setForeground(TEXT_SECONDARY);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            new UltraModernBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(14, 50, 14, 18)));

        // Ajouter l'icône de recherche
        JLabel searchIcon = new JLabel(createVectorIcon(IconType.SEARCH, 18, TEXT_SECONDARY));
        searchIcon.setBounds(16, 15, 18, 18);
        searchField.add(searchIcon);
        searchField.setComponentZOrder(searchIcon, 0);

        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Rechercher des étudiants...")) {
                    searchField.setText("");
                    searchField.setForeground(TEXT_PRIMARY);
                }
                searchField.setBorder(BorderFactory.createCompoundBorder(
                    new UltraModernBorder(PRIMARY_COLOR),
                    BorderFactory.createEmptyBorder(14, 50, 14, 18)));
                searchIcon.setIcon(createVectorIcon(IconType.SEARCH, 18, PRIMARY_COLOR));
                filterStudents();
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Rechercher des étudiants...");
                    searchField.setForeground(TEXT_SECONDARY);
                }
                searchField.setBorder(BorderFactory.createCompoundBorder(
                    new UltraModernBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(14, 50, 14, 18)));
                searchIcon.setIcon(createVectorIcon(IconType.SEARCH, 18, TEXT_SECONDARY));
                filterStudents();
            }
        });

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterStudents(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterStudents(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterStudents(); }
        });

        return searchField;
    }

    // Amélioration 2: ComboBox ultra-moderne pour les filtres
    private JComboBox<String> createUltraModernFilterComboBox(String defaultItem) {
        JComboBox<String> comboBox = new JComboBox<String>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre douce
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 16, 16);
                
                // Fond avec gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 16, 16);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        comboBox.addItem(defaultItem);
        comboBox.setFont(getModernFont(Font.BOLD, 14));
        comboBox.setPreferredSize(new Dimension(200, 48));
        comboBox.setBackground(CARD_COLOR);
        comboBox.setForeground(TEXT_PRIMARY);
        // CORRECTION: Enlever les bordures intérieures et moderniser
        comboBox.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // UI personnalisée pour moderniser l'apparence
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected ComboPopup createPopup() {
                return new BasicComboPopup(comboBox) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Ombre pour la popup
                        g2d.setColor(new Color(0, 0, 0, 30));
                        g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);
                        
                        // Fond avec gradient
                        GradientPaint gradient = new GradientPaint(
                            0, 0, CARD_COLOR,
                            0, getHeight(), new Color(248, 250, 252)
                        );
                        g2d.setPaint(gradient);
                        g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);
                        
                        // Bordure
                        g2d.setColor(PRIMARY_COLOR);
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 12, 12);
                        g2d.dispose();
                        super.paintComponent(g);
                    }
                };
            }

            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Dessiner la flèche
                        g2d.setColor(TEXT_SECONDARY);
                        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        
                        int centerX = getWidth() / 2;
                        int centerY = getHeight() / 2;
                        
                        g2d.drawLine(centerX - 4, centerY - 2, centerX, centerY + 2);
                        g2d.drawLine(centerX, centerY + 2, centerX + 4, centerY - 2);
                        g2d.dispose();
                    }
                };
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                return button;
            }
        });

        // Renderer pour moderniser les éléments de la liste
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                setFont(getModernFont(Font.PLAIN, 14));
                setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
                
                if (isSelected) {
                    setBackground(PRIMARY_COLOR);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(CARD_COLOR);
                    setForeground(TEXT_PRIMARY);
                }
                
                // Effet hover
                if (index >= 0 && !isSelected) {
                    setBackground(new Color(248, 250, 252));
                }
                
                return this;
            }
        });

        return comboBox;
    }

    private JPanel createActionButtonsPanel() {
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonsPanel.setBackground(BACKGROUND_COLOR);

        JButton viewToggleButton = createModernButton(
            isTableView ? "Vue Cartes" : "Vue Tableau",
            isTableView ? IconType.GRID : IconType.TABLE,
            SECONDARY_COLOR,
            false);
        viewToggleButton.addActionListener(_ -> toggleView(viewToggleButton));

        JButton addButton = createModernButton(
            "Ajouter Étudiant", 
            IconType.PLUS, 
            PRIMARY_COLOR, 
            true);
        addButton.addActionListener(_ -> showStudentFormDialog(null));

        buttonsPanel.add(viewToggleButton);
        buttonsPanel.add(addButton);

        return buttonsPanel;
    }

    private JButton createModernButton(String text, IconType iconType, Color bgColor, boolean isPrimary) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentColor;
                if (isPressed) {
                    currentColor = bgColor.darker();
                } else if (isHovered) {
                    currentColor = isPrimary ? PRIMARY_HOVER : HOVER_COLOR;
                } else {
                    currentColor = isPrimary ? bgColor : CARD_COLOR;
                }

                // Ombre plus prononcée
                if (!isPressed) {
                    g2d.setColor(new Color(0, 0, 0, isPrimary ? 20 : 12));
                    g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 16, 16);
                }

                // Gradient pour les boutons primaires
                if (isPrimary) {
                    GradientPaint gradient = new GradientPaint(
                        0, 0, currentColor,
                        0, getHeight(), currentColor.darker()
                    );
                    g2d.setPaint(gradient);
                } else {
                    g2d.setColor(currentColor);
                }
                
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 16, 16);

                // Bordure pour les boutons secondaires
                if (!isPrimary) {
                    g2d.setColor(BORDER_COLOR);
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 16, 16);
                }

                g2d.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void processMouseEvent(MouseEvent e) {
                switch (e.getID()) {
                    case MouseEvent.MOUSE_ENTERED:
                        isHovered = true;
                        repaint();
                        break;
                    case MouseEvent.MOUSE_EXITED:
                        isHovered = false;
                        repaint();
                        break;
                    case MouseEvent.MOUSE_PRESSED:
                        isPressed = true;
                        repaint();
                        break;
                    case MouseEvent.MOUSE_RELEASED:
                        isPressed = false;
                        repaint();
                        break;
                }
                super.processMouseEvent(e);
            }
        };

        button.setFont(getModernFont(Font.BOLD, 14));
        button.setForeground(isPrimary ? Color.WHITE : TEXT_PRIMARY);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
        
        // Ajouter l'icône
        Icon icon = createVectorIcon(iconType, 18, isPrimary ? Color.WHITE : TEXT_PRIMARY);
        button.setIcon(icon);
        button.setIconTextGap(10);

        return button;
    }

    // Énumération pour les types d'icônes
    private enum IconType {
        PLUS, EDIT, DELETE, REFRESH, NOTES, SEARCH, GRID, TABLE, GRADUATION, USER, EMAIL, LOCATION, BOOK, TARGET, CAMERA, SAVE, CANCEL, STAR, CHART
    }

    private Icon createVectorIcon(IconType type, int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                int centerX = x + size / 2;
                int centerY = y + size / 2;
                
                switch (type) {
                    case PLUS:
                        g2d.drawLine(centerX, y + 4, centerX, y + size - 4);
                        g2d.drawLine(x + 4, centerY, x + size - 4, centerY);
                        break;
                    case EDIT:
                        g2d.drawLine(x + 4, y + size - 4, x + size - 7, y + 7);
                        g2d.drawLine(x + size - 7, y + 7, x + size - 4, y + 4);
                        g2d.drawLine(x + size - 4, y + 4, x + size - 7, y + 7);
                        g2d.drawLine(x + 4, y + size - 7, x + 7, y + size - 4);
                        break;
                    case DELETE:
                        g2d.drawRect(x + 5, y + 7, size - 10, size - 10);
                        g2d.drawLine(x + 7, y + 5, x + size - 7, y + 5);
                        g2d.drawLine(x + 8, y + 9, x + 8, y + size - 5);
                        g2d.drawLine(centerX, y + 9, centerX, y + size - 5);
                        g2d.drawLine(x + size - 8, y + 9, x + size - 8, y + size - 5);
                        break;
                    case REFRESH:
                        g2d.drawArc(x + 3, y + 3, size - 6, size - 6, 45, 270);
                        g2d.drawLine(x + size - 5, y + 5, x + size - 3, y + 3);
                        g2d.drawLine(x + size - 5, y + 5, x + size - 7, y + 7);
                        break;
                    case NOTES:
                        g2d.drawRect(x + 4, y + 3, size - 8, size - 6);
                        g2d.drawLine(x + 7, y + 6, x + size - 7, y + 6);
                        g2d.drawLine(x + 7, y + 9, x + size - 7, y + 9);
                        g2d.drawLine(x + 7, y + 12, x + size - 9, y + 12);
                        break;
                    case SEARCH:
                        g2d.drawOval(x + 3, y + 3, size - 10, size - 10);
                        g2d.drawLine(x + size - 7, y + size - 7, x + size - 3, y + size - 3);
                        break;
                    case GRID:
                        int gridSize = (size - 6) / 3;
                        for (int i = 0; i < 3; i++) {
                            for (int j = 0; j < 3; j++) {
                                g2d.drawRect(x + 2 + i * (gridSize + 2), y + 2 + j * (gridSize + 2), gridSize, gridSize);
                            }
                        }
                        break;
                    case TABLE:
                        g2d.drawRect(x + 3, y + 4, size - 6, size - 8);
                        g2d.drawLine(x + 3, y + 7, x + size - 3, y + 7);
                        g2d.drawLine(x + 3, y + 10, x + size - 3, y + 10);
                        g2d.drawLine(x + 7, y + 4, x + 7, y + size - 4);
                        g2d.drawLine(x + 11, y + 4, x + 11, y + size - 4);
                        break;
                    case GRADUATION:
                        // Chapeau de graduation moderne
                        g2d.drawLine(x + 3, centerY, x + size - 3, centerY);
                        g2d.drawLine(x + 5, centerY - 3, x + size - 5, centerY - 3);
                        g2d.drawLine(centerX, y + 4, centerX, centerY + 4);
                        g2d.fillOval(centerX - 2, centerY + 4, 4, 4);
                        break;
                    case USER:
                        g2d.drawOval(centerX - 4, y + 4, 8, 8);
                        g2d.drawArc(x + 3, y + 10, size - 6, size - 8, 0, 180);
                        break;
                    case EMAIL:
                        g2d.drawRect(x + 3, y + 5, size - 6, size - 10);
                        g2d.drawLine(x + 3, y + 5, centerX, centerY);
                        g2d.drawLine(centerX, centerY, x + size - 3, y + 5);
                        break;
                    case LOCATION:
                        g2d.drawOval(centerX - 5, y + 3, 10, 10);
                        g2d.drawLine(centerX, y + 13, centerX - 3, y + size - 3);
                        g2d.drawLine(centerX, y + 13, centerX + 3, y + size - 3);
                        g2d.fillOval(centerX - 3, y + 6, 6, 6);
                        break;
                    case BOOK:
                        g2d.drawRect(x + 4, y + 3, size - 8, size - 6);
                        g2d.drawLine(x + 4, y + 6, x + size - 4, y + 6);
                        g2d.drawLine(centerX, y + 3, centerX, y + size - 3);
                        break;
                    case TARGET:
                        g2d.drawOval(x + 3, y + 3, size - 6, size - 6);
                        g2d.drawOval(centerX - 3, centerY - 3, 6, 6);
                        g2d.fillOval(centerX - 2, centerY - 2, 4, 4);
                        break;
                    case CAMERA:
                        g2d.drawRect(x + 3, y + 5, size - 6, size - 8);
                        g2d.drawRect(x + 6, y + 3, size - 12, 3);
                        g2d.drawOval(centerX - 4, centerY, 8, 8);
                        g2d.fillOval(centerX - 2, centerY + 2, 4, 4);
                        break;
                    case SAVE:
                        g2d.drawRect(x + 3, y + 3, size - 6, size - 6);
                        g2d.drawRect(x + 5, y + 3, size - 10, 5);
                        g2d.drawLine(x + 7, y + 10, x + 7, y + size - 5);
                        g2d.drawLine(x + size - 7, y + 10, x + size - 7, y + size - 5);
                        break;
                    case CANCEL:
                        g2d.drawLine(x + 4, y + 4, x + size - 4, y + size - 4);
                        g2d.drawLine(x + 4, y + size - 4, x + size - 4, y + 4);
                        break;
                    case STAR:
                        // Étoile moderne
                        int[] xPoints = {centerX, centerX + 3, centerX + 6, centerX + 2, centerX + 4, centerX, centerX - 4, centerX - 2, centerX - 6, centerX - 3};
                        int[] yPoints = {y + 3, y + 7, y + 7, y + 11, y + size - 3, y + 13, y + size - 3, y + 11, y + 7, y + 7};
                        g2d.drawPolygon(xPoints, yPoints, 10);
                        break;
                    case CHART:
                        g2d.drawRect(x + 3, y + 3, size - 6, size - 6);
                        g2d.drawLine(x + 6, y + size - 6, x + 6, y + 10);
                        g2d.drawLine(x + 9, y + size - 6, x + 9, y + 8);
                        g2d.drawLine(x + 12, y + size - 6, x + 12, y + 6);
                        break;
                }
                g2d.dispose();
            }

            @Override
            public int getIconWidth() { return size; }

            @Override
            public int getIconHeight() { return size; }
        };
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(BACKGROUND_COLOR);

        JPanel tablePanel = createEnhancedTableView();
        contentPanel.add(tablePanel, "table");

        cardsPanel = createEnhancedCardsView();
        JScrollPane cardsScrollPane = createUltraModernScrollPane(cardsPanel);
        contentPanel.add(cardsScrollPane, "cards");

        return contentPanel;
    }

    private JPanel createEnhancedTableView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);

        String[] columns = {"Matricule", "Prénom", "Nom", "Email", "Adresse", "Niveau", "Parcours", "Photo", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Seule la colonne "Actions" est éditable
            }
        };

        studentTable = createEnhancedTable();
        studentTable.getColumn("Photo").setCellRenderer(new EnhancedPhotoCellRenderer());
        studentTable.getColumn("Photo").setPreferredWidth(80);
        studentTable.getColumn("Photo").setMaxWidth(80);
        studentTable.getColumn("Photo").setMinWidth(80);
        studentTable.getColumn("Actions").setCellRenderer(new EnhancedButtonRenderer());
        studentTable.getColumn("Actions").setCellEditor(new EnhancedButtonEditor());
        studentTable.getColumn("Actions").setPreferredWidth(220);

        JScrollPane scrollPane = createUltraModernScrollPane(studentTable);
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

        table.setFont(getModernFont(Font.PLAIN, 14));
        table.setRowHeight(70);
        table.setBackground(CARD_COLOR);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(241, 245, 249));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(getModernFont(Font.BOLD, 15));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, BORDER_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 55));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        return table;
    }

    // Amélioration 3: ScrollPane ultra-moderne avec scrollbars améliorées
    private JScrollPane createUltraModernScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre plus douce
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 20, 20);
                
                // Fond avec gradient subtil
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.setBackground(BACKGROUND_COLOR);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);

        // AMÉLIORATION: Scrollbar verticale ultra-moderne
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(156, 163, 175); // Gris plus visible
                this.trackColor = new Color(243, 244, 246); // Fond plus clair
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(0, 0));
                button.setMinimumSize(new Dimension(0, 0));
                button.setMaximumSize(new Dimension(0, 0));
                return button;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre pour le thumb
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 1,
                        thumbBounds.width - 2, thumbBounds.height - 2, 8, 8);
                
                // Gradient pour le thumb
                GradientPaint gradient = new GradientPaint(
                    thumbBounds.x, thumbBounds.y, new Color(156, 163, 175),
                    thumbBounds.x + thumbBounds.width, thumbBounds.y, new Color(107, 114, 128)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(thumbBounds.x, thumbBounds.y,
                        thumbBounds.width - 1, thumbBounds.height - 1, 8, 8);
                
                // Highlight
                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 1,
                        thumbBounds.width - 3, thumbBounds.height / 3, 6, 6);
                
                g2d.dispose();
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Track avec gradient
                GradientPaint gradient = new GradientPaint(
                    trackBounds.x, trackBounds.y, new Color(248, 250, 252),
                    trackBounds.x + trackBounds.width, trackBounds.y, new Color(241, 245, 249)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(trackBounds.x, trackBounds.y,
                        trackBounds.width, trackBounds.height, 8, 8);
                
                g2d.dispose();
            }
        });

        return scrollPane;
    }

    private JPanel createEnhancedCardsView() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(0, 3, 30, 30));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        return panel;
    }

    private void toggleView(JButton toggleButton) {
        isTableView = !isTableView;
        toggleButton.setText(isTableView ? "Vue Cartes" : "Vue Tableau");
        toggleButton.setIcon(createVectorIcon(
            isTableView ? IconType.GRID : IconType.TABLE, 
            18, 
            TEXT_PRIMARY));

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
                
                // Ombre plus prononcée et moderne
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, 24, 24);
                
                // Gradient de fond plus sophistiqué
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR, 
                    0, getHeight(), new Color(252, 253, 255));
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 24, 24);
                
                // Bordure subtile avec gradient
                g2d.setPaint(new GradientPaint(
                    0, 0, BORDER_COLOR,
                    0, getHeight(), new Color(241, 245, 249)
                ));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRoundRect(0, 0, getWidth() - 7, getHeight() - 7, 24, 24);
                
                // Accent coloré en haut
                g2d.setColor(PRIMARY_COLOR);
                g2d.fillRoundRect(0, 0, getWidth() - 6, 6, 24, 24);
                
                g2d.dispose();
            }
        };

        card.setLayout(new BorderLayout(25, 25));
        card.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));
        card.setPreferredSize(new Dimension(380, 350));

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

        EnhancedRoundImageLabel photoLabel = new EnhancedRoundImageLabel(80);
        if (etudiant != null && etudiant.getphoto_url() != null && !etudiant.getphoto_url().isEmpty()) {
            try {
                photoLabel.setImage(URI.create(etudiant.getphoto_url()).toURL());
            } catch (Exception e) {
                photoLabel.setIcon(createVectorIcon(IconType.USER, 40, TEXT_SECONDARY));
            }
        } else {
            photoLabel.setIcon(createVectorIcon(IconType.USER, 40, TEXT_SECONDARY));
        }

        photoSection.add(photoLabel);
        return photoSection;
    }

    private JPanel createEnhancedCardInfoSection(Etudiant etudiant) {
        JPanel infoSection = new JPanel();
        infoSection.setLayout(new BoxLayout(infoSection, BoxLayout.Y_AXIS));
        infoSection.setBackground(Color.WHITE);

        String name = etudiant != null ? 
            (etudiant.getPrenom() != null ? etudiant.getPrenom() : "") + " " +
            (etudiant.getNom() != null ? etudiant.getNom() : "") : "N/A";
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(getModernFont(Font.BOLD, 22));
        nameLabel.setForeground(TEXT_PRIMARY);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        String matricule = etudiant != null && etudiant.getMatricule() != null ? etudiant.getMatricule() : "N/A";
        JLabel matriculeLabel = new JLabel(matricule);
        matriculeLabel.setFont(getModernFont(Font.BOLD, 15));
        matriculeLabel.setForeground(PRIMARY_COLOR);
        matriculeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        matriculeLabel.setIcon(createVectorIcon(IconType.USER, 16, PRIMARY_COLOR));

        String email = etudiant != null && etudiant.getEmail() != null ? etudiant.getEmail() : "N/A";
        JLabel emailLabel = new JLabel(email);
        emailLabel.setFont(getModernFont(Font.PLAIN, 14));
        emailLabel.setForeground(TEXT_SECONDARY);
        emailLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        emailLabel.setIcon(createVectorIcon(IconType.EMAIL, 16, TEXT_SECONDARY));

        String niveau = etudiant != null ? getNiveauLabel(etudiant.getNiveauId()) : "N/A";
        JLabel niveauLabel = new JLabel(niveau);
        niveauLabel.setFont(getModernFont(Font.PLAIN, 14));
        niveauLabel.setForeground(TEXT_SECONDARY);
        niveauLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        niveauLabel.setIcon(createVectorIcon(IconType.BOOK, 16, TEXT_SECONDARY));

        String parcours = etudiant != null ? getParcoursLabel(etudiant.getParcoursId()) : "N/A";
        JLabel parcoursLabel = new JLabel(parcours);
        parcoursLabel.setFont(getModernFont(Font.PLAIN, 14));
        parcoursLabel.setForeground(TEXT_SECONDARY);
        parcoursLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        parcoursLabel.setIcon(createVectorIcon(IconType.TARGET, 16, TEXT_SECONDARY));

        infoSection.add(nameLabel);
        infoSection.add(Box.createVerticalStrut(12));
        infoSection.add(matriculeLabel);
        infoSection.add(Box.createVerticalStrut(10));
        infoSection.add(emailLabel);
        infoSection.add(Box.createVerticalStrut(8));
        infoSection.add(niveauLabel);
        infoSection.add(Box.createVerticalStrut(8));
        infoSection.add(parcoursLabel);

        return infoSection;
    }

    private JPanel createEnhancedCardActionsSection(Etudiant etudiant) {
        JPanel actionsSection = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        actionsSection.setBackground(Color.WHITE);

        JButton editButton = createEnhancedCardActionButton(IconType.EDIT, WARNING_COLOR, "Modifier");
        editButton.addActionListener(_ -> showStudentFormDialog(etudiant));

        JButton deleteButton = createEnhancedCardActionButton(IconType.DELETE, ERROR_COLOR, "Supprimer");
        deleteButton.addActionListener(_ -> {
            if (etudiant != null && etudiant.getId() != null) {
                showUltraModernDeleteConfirmDialog(() -> deleteStudent(etudiant.getId()));
            }
        });

        JButton updateButton = createEnhancedCardActionButton(IconType.REFRESH, SECONDARY_COLOR, "Niveau/Parcours");
        updateButton.addActionListener(_ -> showUpdateNiveauParcoursDialog(etudiant));

        JButton notesButton = createEnhancedCardActionButton(IconType.NOTES, NOTES_COLOR, "Gérer les Notes");
        notesButton.addActionListener(_ -> showUltraModernNotesDialog(etudiant));

        actionsSection.add(editButton);
        actionsSection.add(deleteButton);
        actionsSection.add(updateButton);
        actionsSection.add(notesButton);

        return actionsSection;
    }

    private JButton createEnhancedCardActionButton(IconType iconType, Color color, String tooltip) {
        JButton button = new JButton() {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                Color currentColor = isHovered ? color.brighter() : color;
                
                // Ombre plus prononcée
                g2d.setColor(new Color(0, 0, 0, 25));
                g2d.fillOval(3, 3, getWidth() - 6, getHeight() - 6);
                
                // Gradient pour le fond du bouton
                GradientPaint gradient = new GradientPaint(
                    0, 0, currentColor,
                    0, getHeight(), currentColor.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillOval(0, 0, getWidth() - 3, getHeight() - 3);
                
                g2d.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void processMouseEvent(MouseEvent e) {
                if (e.getID() == MouseEvent.MOUSE_ENTERED) {
                    isHovered = true;
                    repaint();
                } else if (e.getID() == MouseEvent.MOUSE_EXITED) {
                    isHovered = false;
                    repaint();
                }
                super.processMouseEvent(e);
            }
        };

        button.setIcon(createVectorIcon(iconType, 18, Color.WHITE));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(45, 45));
        button.setToolTipText(tooltip);

        return button;
    }

    // Amélioration 4: Dialog de suppression ultra-moderne
    private void showUltraModernDeleteConfirmDialog(Runnable onConfirm) {
        JDialog confirmDialog = new UltraModernDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Confirmation de suppression"
        );
        confirmDialog.setSize(480, 280);
        confirmDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CARD_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // En-tête avec icône d'avertissement
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel warningIcon = new JLabel(createVectorIcon(IconType.DELETE, 48, ERROR_COLOR));
        JLabel titleLabel = new JLabel("Êtes-vous sûr ?");
        titleLabel.setFont(getModernFont(Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        headerPanel.add(warningIcon);
        headerPanel.add(titleLabel);

        // Message
        JLabel messageLabel = new JLabel("<html><center>Cette action est irréversible.<br/>L'étudiant sera définitivement supprimé.</center></html>");
        messageLabel.setFont(getModernFont(Font.PLAIN, 16));
        messageLabel.setForeground(TEXT_SECONDARY);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Boutons personnalisés
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonPanel.setBackground(CARD_COLOR);

        JButton yesButton = createUltraModernConfirmButton("Oui, j'en suis sûr", ERROR_COLOR);
        JButton noButton = createUltraModernConfirmButton("Non", new Color(107, 114, 128));

        yesButton.addActionListener(_ -> {
            confirmDialog.dispose();
            onConfirm.run();
        });

        noButton.addActionListener(_ -> confirmDialog.dispose());

        buttonPanel.add(noButton);
        buttonPanel.add(yesButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        confirmDialog.add(mainPanel);
        confirmDialog.setVisible(true);
    }

    private JButton createUltraModernConfirmButton(String text, Color bgColor) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentColor;
                if (isPressed) {
                    currentColor = bgColor.darker();
                } else if (isHovered) {
                    currentColor = bgColor.brighter();
                } else {
                    currentColor = bgColor;
                }

                // Ombre dynamique
                if (!isPressed) {
                    g2d.setColor(new Color(0, 0, 0, isHovered ? 30 : 20));
                    g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                }

                // Gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, currentColor,
                    0, getHeight(), currentColor.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);

                g2d.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void processMouseEvent(MouseEvent e) {
                switch (e.getID()) {
                    case MouseEvent.MOUSE_ENTERED:
                        isHovered = true;
                        repaint();
                        break;
                    case MouseEvent.MOUSE_EXITED:
                        isHovered = false;
                        repaint();
                        break;
                    case MouseEvent.MOUSE_PRESSED:
                        isPressed = true;
                        repaint();
                        break;
                    case MouseEvent.MOUSE_RELEASED:
                        isPressed = false;
                        repaint();
                        break;
                }
                super.processMouseEvent(e);
            }
        };

        button.setFont(getModernFont(Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(12, 25, 12, 25));
        button.setPreferredSize(new Dimension(140, 45));

        return button;
    }

    // Amélioration 5: Dialogue ultra-moderne pour les notes
    private void showUltraModernNotesDialog(Etudiant etudiant) {
        if (etudiant == null) {
            showModernErrorDialog("Erreur: Étudiant non sélectionné.");
            return;
        }

        currentDialog = new UltraModernDialog((Frame) SwingUtilities.getWindowAncestor(this), "Gestion des Notes");
        currentDialog.setSize(900, 700);
        currentDialog.setLocationRelativeTo(this);

        JPanel mainPanel = createUltraModernNotesContent(etudiant, currentDialog);
        currentDialog.add(mainPanel);
        currentDialog.setVisible(true);
    }

    // Classe pour dialogue ultra-moderne
    private class UltraModernDialog extends JDialog {
        public UltraModernDialog(Frame parent, String title) {
            super(parent, title, true);
            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0));
            
            // Bordure ultra-moderne avec ombre
            getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                new UltraModernBorder()
            ));
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Ombre ultra-moderne avec blur
            for (int i = 0; i < 10; i++) {
                g2d.setColor(new Color(0, 0, 0, 5 - i/2));
                g2d.fillRoundRect(8 + i, 8 + i, getWidth() - 16 - 2*i, getHeight() - 16 - 2*i, 28, 28);
            }
            
            // Fond avec gradient sophistiqué
            GradientPaint gradient = new GradientPaint(
                0, 0, CARD_COLOR,
                getWidth(), getHeight(), new Color(250, 251, 255)
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 28, 28);
            
            g2d.dispose();
            super.paint(g);
        }
    }

    // CORRECTION: Ajout de la classe UltraModernBorder manquante
    private class UltraModernBorder extends AbstractBorder {
        private final Color borderColor;
        private final int thickness;

        public UltraModernBorder() {
            this(BORDER_COLOR, 1);
        }

        public UltraModernBorder(Color borderColor) {
            this(borderColor, 2);
        }

        public UltraModernBorder(Color borderColor, int thickness) {
            this.borderColor = borderColor;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Bordure avec gradient
            GradientPaint borderGradient = new GradientPaint(
                0, 0, PRIMARY_COLOR,
                width, height, SECONDARY_COLOR
            );
            g2d.setPaint(borderGradient);
            g2d.setStroke(new BasicStroke(thickness));
            g2d.drawRoundRect(x, y, width - 1, height - 1, 12, 12);
            
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
        }
    }

    private JPanel createUltraModernNotesContent(Etudiant etudiant, JDialog dialog) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CARD_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // En-tête ultra-moderne
        JPanel headerPanel = createUltraModernNotesHeader(etudiant, dialog);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Contenu principal avec onglets
        JTabbedPane tabbedPane = createUltraModernTabbedPane(etudiant);
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel createUltraModernNotesHeader(Etudiant etudiant, JDialog dialog) {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient de fond pour l'en-tête
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(99, 102, 241, 20),
                    getWidth(), getHeight(), new Color(139, 92, 246, 20)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel titleSection = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        titleSection.setBackground(Color.WHITE);

        // Photo de l'étudiant
        EnhancedRoundImageLabel photoLabel = new EnhancedRoundImageLabel(60);
        if (etudiant.getphoto_url() != null && !etudiant.getphoto_url().isEmpty()) {
            try {
                photoLabel.setImage(URI.create(etudiant.getphoto_url()).toURL());
            } catch (Exception e) {
                photoLabel.setIcon(createVectorIcon(IconType.USER, 30, TEXT_SECONDARY));
            }
        } else {
            photoLabel.setIcon(createVectorIcon(IconType.USER, 30, TEXT_SECONDARY));
        }

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Gestion des Notes");
        titleLabel.setFont(getModernFont(Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setIcon(createVectorIcon(IconType.STAR, 28, PRIMARY_COLOR));

        JLabel studentLabel = new JLabel(etudiant.getPrenom() + " " + etudiant.getNom());
        studentLabel.setFont(getModernFont(Font.BOLD, 20));
        studentLabel.setForeground(PRIMARY_COLOR);

        JLabel matriculeLabel = new JLabel("Matricule: " + etudiant.getMatricule());
        matriculeLabel.setFont(getModernFont(Font.PLAIN, 14));
        matriculeLabel.setForeground(TEXT_SECONDARY);

        infoPanel.add(titleLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(studentLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(matriculeLabel);

        titleSection.add(photoLabel);
        titleSection.add(infoPanel);

        // Bouton de fermeture ultra-moderne
        JButton closeButton = createUltraModernCloseButton(dialog);

        headerPanel.add(titleSection, BorderLayout.WEST);
        headerPanel.add(closeButton, BorderLayout.EAST);

        return headerPanel;
    }

    private JButton createUltraModernCloseButton(JDialog dialog) {
        JButton closeButton = new JButton() {
            private boolean isHovered = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (isHovered) {
                    g2d.setColor(new Color(239, 68, 68, 100));
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                }
                
                g2d.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void processMouseEvent(MouseEvent e) {
                if (e.getID() == MouseEvent.MOUSE_ENTERED) {
                    isHovered = true;
                    repaint();
                } else if (e.getID() == MouseEvent.MOUSE_EXITED) {
                    isHovered = false;
                    repaint();
                }
                super.processMouseEvent(e);
            }
        };

        closeButton.setIcon(createVectorIcon(IconType.CANCEL, 20, TEXT_SECONDARY));
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setPreferredSize(new Dimension(40, 40));
        closeButton.addActionListener(_ -> dialog.dispose());

        return closeButton;
    }

    private JTabbedPane createUltraModernTabbedPane(Etudiant etudiant) {
        JTabbedPane tabbedPane = new JTabbedPane() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec gradient subtil
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(252, 253, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        tabbedPane.setFont(getModernFont(Font.BOLD, 14));
        tabbedPane.setBackground(CARD_COLOR);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Onglet Saisie des Notes
        JPanel notesInputPanel = createUltraModernNotesInputPanel(etudiant);
        tabbedPane.addTab("📝 Saisie des Notes", notesInputPanel);

        // Onglet Visualisation avec contenu
        JPanel notesViewPanel = createUltraModernNotesViewPanel(etudiant);
        tabbedPane.addTab("📊 Visualisation", notesViewPanel);

        // Onglet Statistiques avec contenu
        JPanel statsPanel = createUltraModernStatsPanel(etudiant);
        tabbedPane.addTab("📈 Statistiques", statsPanel);

        return tabbedPane;
    }

    private JPanel createUltraModernNotesInputPanel(Etudiant etudiant) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Formulaire de saisie ultra-moderne
        JPanel formPanel = createUltraModernNotesForm(etudiant);
        panel.add(formPanel, BorderLayout.NORTH);

        // Table des notes existantes
        JPanel tablePanel = createUltraModernNotesTable(etudiant);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUltraModernNotesForm(Etudiant etudiant) {
        JPanel formPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre douce
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 20, 20);
                
                // Fond avec gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(250, 251, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 20, 20);
                
                // Bordure colorée
                g2d.setColor(PRIMARY_COLOR);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        formPanel.setLayout(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Titre du formulaire
        JLabel formTitle = new JLabel("Ajouter une nouvelle note");
        formTitle.setFont(getModernFont(Font.BOLD, 18));
        formTitle.setForeground(PRIMARY_COLOR);
        formTitle.setIcon(createVectorIcon(IconType.PLUS, 20, PRIMARY_COLOR));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 4;
        formPanel.add(formTitle, gbc);

        gbc.gridwidth = 1;

        // Matière
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(createUltraModernFormLabel("Matière", IconType.BOOK), gbc);
        gbc.gridx = 1;
        JComboBox<Matiere> matiereCombo = createUltraModernMatiereComboBox();
        try {
            List<Matiere> matieres = studentService.getAllMatieres();
            for (Matiere matiere : matieres) {
                matiereCombo.addItem(matiere);
            }
        } catch (Exception e) {
            showErrorNotification("Erreur lors du chargement des matières: " + e.getMessage());
        }
        formPanel.add(matiereCombo, gbc);

        // Note
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(createUltraModernFormLabel("Note (/20)", IconType.STAR), gbc);
        gbc.gridx = 3;
        JTextField noteField = createUltraModernTextField("");
        noteField.setPreferredSize(new Dimension(100, 45));
        formPanel.add(noteField, gbc);

        // Semestre
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(createUltraModernFormLabel("Semestre", IconType.CHART), gbc);
        gbc.gridx = 1;
        JComboBox<String> semestreCombo = createUltraModernStringComboBox();
        semestreCombo.addItem("S1");
        semestreCombo.addItem("S2");
        formPanel.add(semestreCombo, gbc);

        // Année
        gbc.gridx = 2; gbc.gridy = 2;
        formPanel.add(createUltraModernFormLabel("Année", IconType.CHART), gbc);
        gbc.gridx = 3;
        JComboBox<String> anneeCombo = createUltraModernStringComboBox();
        anneeCombo.addItem("2023-2024");
        anneeCombo.addItem("2024-2025");
        anneeCombo.addItem("2025-2026");
        formPanel.add(anneeCombo, gbc);

        // Bouton d'ajout
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        JButton addButton = createUltraModernButton("Ajouter la Note", IconType.PLUS, SUCCESS_COLOR);
        addButton.addActionListener(_ -> {
            try {
                Matiere selectedMatiere = (Matiere) matiereCombo.getSelectedItem();
                String noteText = noteField.getText().trim();
                String semestre = (String) semestreCombo.getSelectedItem();
                String annee = (String) anneeCombo.getSelectedItem();

                if (selectedMatiere == null || noteText.isEmpty()) {
                    showModernErrorDialog("Veuillez remplir tous les champs.");
                    return;
                }

                float noteValue = Float.parseFloat(noteText);
                if (noteValue < 0 || noteValue > 20) {
                    showModernErrorDialog("La note doit être comprise entre 0 et 20.");
                    return;
                }

                Note note = new Note();
                note.setEtudiantId(etudiant.getId());
                note.setMatiereId(selectedMatiere.getId());
                note.setValeur(noteValue);
                note.setSemestre(semestre);
                note.setAnnee(annee);

                studentService.addNote(note);
                showModernSuccessDialog("Note ajoutée avec succès!");
                
                // Réinitialiser le formulaire
                noteField.setText("");
                matiereCombo.setSelectedIndex(0);

            } catch (NumberFormatException ex) {
                showModernErrorDialog("Veuillez entrer une note valide.");
            } catch (ApiException ex) {
                showModernErrorDialog("Erreur lors de l'ajout de la note: " + ex.getMessage());
            }
        });
        formPanel.add(addButton, gbc);

        return formPanel;
    }

    private JLabel createUltraModernFormLabel(String text, IconType iconType) {
        JLabel label = new JLabel(text);
        label.setFont(getModernFont(Font.BOLD, 14));
        label.setForeground(TEXT_PRIMARY);
        label.setIcon(createVectorIcon(iconType, 16, PRIMARY_COLOR));
        label.setIconTextGap(8);
        return label;
    }

    private JTextField createUltraModernTextField(String text) {
        JTextField textField = new JTextField(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre subtile
                g2d.setColor(new Color(0, 0, 0, 5));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                
                // Fond avec gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, getBackground(),
                    0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        textField.setFont(getModernFont(Font.PLAIN, 14));
        textField.setBackground(CARD_COLOR);
        textField.setForeground(TEXT_PRIMARY);
        textField.setBorder(BorderFactory.createCompoundBorder(
            new UltraModernBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        textField.setPreferredSize(new Dimension(150, 45));

        // Effet de focus amélioré
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    new UltraModernBorder(PRIMARY_COLOR),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    new UltraModernBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(12, 16, 12, 16)));
            }
        });

        return textField;
    }

    private JComboBox<Matiere> createUltraModernMatiereComboBox() {
        JComboBox<Matiere> comboBox = new JComboBox<Matiere>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre subtile
                g2d.setColor(new Color(0, 0, 0, 5));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                
                // Fond avec gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, getBackground(),
                    0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        comboBox.setFont(getModernFont(Font.PLAIN, 14));
        comboBox.setBackground(CARD_COLOR);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        comboBox.setPreferredSize(new Dimension(180, 45));

        return comboBox;
    }

    private JComboBox<String> createUltraModernStringComboBox() {
        JComboBox<String> comboBox = new JComboBox<String>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre subtile
                g2d.setColor(new Color(0, 0, 0, 5));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                
                // Fond avec gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, getBackground(),
                    0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 12, 12);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        comboBox.setFont(getModernFont(Font.PLAIN, 14));
        comboBox.setBackground(CARD_COLOR);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        comboBox.setPreferredSize(new Dimension(120, 45));

        return comboBox;
    }

    private JButton createUltraModernButton(String text, IconType iconType, Color bgColor) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentColor = isPressed ? bgColor.darker() : 
                                   isHovered ? bgColor.brighter() : bgColor;

                // Ombre plus prononcée
                if (!isPressed) {
                    g2d.setColor(new Color(0, 0, 0, 20));
                    g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 16, 16);
                }

                // Gradient sophistiqué
                GradientPaint gradient = new GradientPaint(
                    0, 0, currentColor,
                    0, getHeight(), currentColor.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 16, 16);

                g2d.dispose();
                super.paintComponent(g);
            }

            @Override
            protected void processMouseEvent(MouseEvent e) {
                switch (e.getID()) {
                    case MouseEvent.MOUSE_ENTERED:
                        isHovered = true;
                        repaint();
                        break;
                    case MouseEvent.MOUSE_EXITED:
                        isHovered = false;
                        repaint();
                        break;
                    case MouseEvent.MOUSE_PRESSED:
                        isPressed = true;
                        repaint();
                        break;
                    case MouseEvent.MOUSE_RELEASED:
                        isPressed = false;
                        repaint();
                        break;
                }
                super.processMouseEvent(e);
            }
        };

        button.setFont(getModernFont(Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(14, 28, 14, 28));
        button.setIcon(createVectorIcon(iconType, 18, Color.WHITE));
        button.setIconTextGap(10);

        return button;
    }

    private JPanel createUltraModernNotesTable(Etudiant etudiant) {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_COLOR);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel tableTitle = new JLabel("Notes existantes");
        tableTitle.setFont(getModernFont(Font.BOLD, 18));
        tableTitle.setForeground(TEXT_PRIMARY);
        tableTitle.setIcon(createVectorIcon(IconType.NOTES, 20, PRIMARY_COLOR));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        tablePanel.add(tableTitle, BorderLayout.NORTH);

        DefaultTableModel notesTableModel = new DefaultTableModel(
            new String[]{"Matière", "Note", "Semestre", "Année", "Actions"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        JTable notesTable = createUltraModernTable(notesTableModel);
        notesTable.getColumn("Actions").setCellRenderer(new UltraModernNotesButtonRenderer());
        notesTable.getColumn("Actions").setCellEditor(new UltraModernNotesButtonEditor(etudiant, notesTable, notesTableModel));

        try {
            List<Note> notes = studentService.getNotesByEtudiant(etudiant.getId());
            List<Matiere> matieres = studentService.getAllMatieres();

            for (Matiere matiere : matieres) {
                String noteValue = "";
                String semestre = "";
                String annee = "";

                for (Note note : notes) {
                    if (note.getMatiereId().equals(matiere.getId())) {
                        noteValue = String.valueOf(note.getValeur());
                        semestre = note.getSemestre() != null ? note.getSemestre() : "";
                        annee = note.getAnnee() != null ? note.getAnnee() : "";
                        break;
                    }
                }

                notesTableModel.addRow(new Object[]{matiere.getNom(), noteValue, semestre, annee, "Actions"});
            }
        } catch (ApiException ex) {
            showModernErrorDialog("Erreur lors du chargement des notes: " + ex.getMessage());
        }

        JScrollPane scrollPane = createUltraModernScrollPane(notesTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JTable createUltraModernTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
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

        table.setFont(getModernFont(Font.PLAIN, 14));
        table.setRowHeight(55);
        table.setBackground(CARD_COLOR);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(241, 245, 249));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        JTableHeader header = table.getTableHeader();
        header.setFont(getModernFont(Font.BOLD, 15));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PRIMARY_COLOR));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 50));
        ((DefaultTableCellRenderer) header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        return table;
    }

    // Amélioration 6: Contenu réel pour la visualisation des notes
    private JPanel createUltraModernNotesViewPanel(Etudiant etudiant) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Visualisation des Notes");
        titleLabel.setFont(getModernFont(Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setIcon(createVectorIcon(IconType.CHART, 24, PRIMARY_COLOR));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Créer un panel avec les moyennes et graphiques
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        contentPanel.setBackground(CARD_COLOR);

        try {
            List<Note> notes = studentService.getNotesByEtudiant(etudiant.getId());

            // Calcul des moyennes par semestre
            double moyenneS1 = calculateAverageForSemester(notes, "S1");
            double moyenneS2 = calculateAverageForSemester(notes, "S2");
            double moyenneGenerale = (moyenneS1 + moyenneS2) / 2;

            contentPanel.add(createStatCard("Moyenne S1", String.format("%.2f/20", moyenneS1), SUCCESS_COLOR));
            contentPanel.add(createStatCard("Moyenne S2", String.format("%.2f/20", moyenneS2), ACCENT_BLUE));
            contentPanel.add(createStatCard("Moyenne Générale", String.format("%.2f/20", moyenneGenerale), PRIMARY_COLOR));
            contentPanel.add(createStatCard("Nombre de Notes", String.valueOf(notes.size()), WARNING_COLOR));

            panel.add(contentPanel, BorderLayout.CENTER);

        } catch (ApiException ex) {
            JLabel errorLabel = new JLabel("Erreur lors du chargement des notes: " + ex.getMessage());
            errorLabel.setFont(getModernFont(Font.ITALIC, 16));
            errorLabel.setForeground(ERROR_COLOR);
            errorLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(errorLabel, BorderLayout.CENTER);
        }

        return panel;
    }

    // Amélioration 7: Contenu réel pour les statistiques
    private JPanel createUltraModernStatsPanel(Etudiant etudiant) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("Statistiques Détaillées");
        titleLabel.setFont(getModernFont(Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setIcon(createVectorIcon(IconType.CHART, 24, PRIMARY_COLOR));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        try {
            List<Note> notes = studentService.getNotesByEtudiant(etudiant.getId());
            List<Matiere> matieres = studentService.getAllMatieres();

            // Créer table des détails par matière
            String[] columns = {"Matière", "Coefficient", "Note S1", "Note S2", "Moyenne Matière"};
            DefaultTableModel statsModel = new DefaultTableModel(columns, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

            for (Matiere matiere : matieres) {
                Note noteS1 = findNoteForMatiereAndSemester(notes, matiere.getId(), "S1");
                Note noteS2 = findNoteForMatiereAndSemester(notes, matiere.getId(), "S2");
                
                String valeurS1 = noteS1 != null ? String.format("%.1f", noteS1.getValeur()) : "-";
                String valeurS2 = noteS2 != null ? String.format("%.1f", noteS2.getValeur()) : "-";
                
                double moyenne = 0;
                int count = 0;
                if (noteS1 != null) { moyenne += noteS1.getValeur(); count++; }
                if (noteS2 != null) { moyenne += noteS2.getValeur(); count++; }
                
                String moyenneStr = count > 0 ? String.format("%.2f", moyenne / count) : "-";

                statsModel.addRow(new Object[]{
                    matiere.getNom(),
                    String.valueOf(matiere.getCoefficient()),
                    valeurS1,
                    valeurS2,
                    moyenneStr
                });
            }

            JTable statsTable = createUltraModernTable(statsModel);
            JScrollPane scrollPane = createUltraModernScrollPane(statsTable);
            panel.add(scrollPane, BorderLayout.CENTER);

        } catch (ApiException ex) {
            JLabel errorLabel = new JLabel("Erreur lors du chargement des statistiques: " + ex.getMessage());
            errorLabel.setFont(getModernFont(Font.ITALIC, 16));
            errorLabel.setForeground(ERROR_COLOR);
            errorLabel.setHorizontalAlignment(JLabel.CENTER);
            panel.add(errorLabel, BorderLayout.CENTER);
        }

        return panel;
    }

    private double calculateAverageForSemester(List<Note> notes, String semestre) {
        List<Note> semestreNotes = notes.stream()
            .filter(note -> semestre.equals(note.getSemestre()))
            .toList();
        
        if (semestreNotes.isEmpty()) return 0;
        
        return semestreNotes.stream()
            .mapToDouble(Note::getValeur)
            .average()
            .orElse(0);
    }

    private Note findNoteForMatiereAndSemester(List<Note> notes, String matiereId, String semestre) {
        return notes.stream()
            .filter(note -> matiereId.equals(note.getMatiereId()) && semestre.equals(note.getSemestre()))
            .findFirst()
            .orElse(null);
    }

    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                
                // Fond avec gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);
                
                // Bordure accent
                g2d.setColor(accentColor);
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 15, 15);
                
                g2d.dispose();
            }
        };

        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(getModernFont(Font.BOLD, 14));
        titleLabel.setForeground(TEXT_SECONDARY);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(getModernFont(Font.BOLD, 24));
        valueLabel.setForeground(accentColor);
        valueLabel.setHorizontalAlignment(JLabel.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // Continuer avec les autres méthodes existantes mais modernisées...
    
    private void showStudentFormDialog(Etudiant etudiant) {
        boolean isEdit = etudiant != null;
        
        // Créer une boîte de dialogue ultra-moderne
        studentDialog = new UltraModernDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Modifier l'Étudiant" : "Ajouter un Étudiant"
        );
        
        studentDialog.setSize(750, 950);
        studentDialog.setLocationRelativeTo(this);

        loadNiveauxAndParcours();

        JPanel mainPanel = createModernDialogContent(etudiant, isEdit);
        studentDialog.add(mainPanel);
        studentDialog.setVisible(true);
    }

    private JPanel createModernDialogContent(Etudiant etudiant, boolean isEdit) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CARD_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        // En-tête ultra-moderne
        JPanel headerPanel = createUltraModernDialogHeader(
            isEdit ? "Modifier l'Étudiant" : "Ajouter un Étudiant",
            "Veuillez remplir tous les champs requis",
            isEdit ? IconType.EDIT : IconType.PLUS
        );
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = createModernStudentForm(etudiant, isEdit);
        JScrollPane formScrollPane = createUltraModernScrollPane(formPanel);
        formScrollPane.setBorder(null);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(formScrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = createUltraModernDialogButtons(etudiant, isEdit);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createUltraModernDialogHeader(String titleText, String subtitleText, IconType iconType) {
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient de fond pour l'en-tête
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(99, 102, 241, 15),
                    getWidth(), getHeight(), new Color(139, 92, 246, 15)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 35, 0));

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel(createVectorIcon(iconType, 36, PRIMARY_COLOR));
        titleRow.add(iconLabel);
        titleRow.add(Box.createHorizontalStrut(18));

        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(getModernFont(Font.BOLD, 30));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleRow.add(titleLabel);

        JLabel subtitleLabel = new JLabel(subtitleText);
        subtitleLabel.setFont(getModernFont(Font.PLAIN, 15));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(12, 54, 0, 0));

        // Bouton de fermeture ultra-moderne
        JButton closeButton = createUltraModernCloseButton(studentDialog);

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setBackground(Color.WHITE);
        topRow.add(titleRow, BorderLayout.WEST);
        topRow.add(closeButton, BorderLayout.EAST);

        headerPanel.add(topRow);
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createModernStudentForm(Etudiant etudiant, boolean isEdit) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 0, 18, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Matricule
        formPanel.add(createUltraModernFormLabel("Matricule *", IconType.USER), gbc);
        gbc.gridy = 1;
        matriculeField = createUltraModernTextField(isEdit && etudiant != null ? etudiant.getMatricule() : "");
        matriculeField.setPreferredSize(new Dimension(650, 55));
        formPanel.add(matriculeField, gbc);

        // Prénom
        gbc.gridy = 2;
        formPanel.add(createUltraModernFormLabel("Prénom *", IconType.USER), gbc);
        gbc.gridy = 3;
        firstNameField = createUltraModernTextField(isEdit && etudiant != null ? etudiant.getPrenom() : "");
        firstNameField.setPreferredSize(new Dimension(650, 55));
        formPanel.add(firstNameField, gbc);

        // Nom
        gbc.gridy = 4;
        formPanel.add(createUltraModernFormLabel("Nom *", IconType.USER), gbc);
        gbc.gridy = 5;
        lastNameField = createUltraModernTextField(isEdit && etudiant != null ? etudiant.getNom() : "");
        lastNameField.setPreferredSize(new Dimension(650, 55));
        formPanel.add(lastNameField, gbc);

        // Email
        gbc.gridy = 6;
        formPanel.add(createUltraModernFormLabel("Email *", IconType.EMAIL), gbc);
        gbc.gridy = 7;
        emailField = createUltraModernTextField(isEdit && etudiant != null ? etudiant.getEmail() : "");
        emailField.setPreferredSize(new Dimension(650, 55));
        emailField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateEmailField(emailField);
            }
        });
        formPanel.add(emailField, gbc);

        // Adresse
        gbc.gridy = 8;
        formPanel.add(createUltraModernFormLabel("Adresse", IconType.LOCATION), gbc);
        gbc.gridy = 9;
        adresseField = createUltraModernTextField(isEdit && etudiant != null ? etudiant.getAdresse() : "");
        adresseField.setPreferredSize(new Dimension(650, 55));
        formPanel.add(adresseField, gbc);

        // Niveau
        gbc.gridy = 10;
        formPanel.add(createUltraModernFormLabel("Niveau *", IconType.BOOK), gbc);
        gbc.gridy = 11;
        niveauComboBox = createUltraModernNiveauComboBox();
        niveauComboBox.setPreferredSize(new Dimension(650, 55));
        for (Niveau niveau : allNiveaux) {
            if (niveau != null) {
                niveauComboBox.addItem(niveau);
            }
        }
        if (isEdit && etudiant != null && etudiant.getNiveauId() != null) {
            for (Niveau niveau : allNiveaux) {
                if (niveau != null && niveau.getId() != null && niveau.getId().equals(etudiant.getNiveauId())) {
                    niveauComboBox.setSelectedItem(niveau);
                    break;
                }
            }
        }
        formPanel.add(niveauComboBox, gbc);

        // Parcours
        gbc.gridy = 12;
        formPanel.add(createUltraModernFormLabel("Parcours *", IconType.TARGET), gbc);
        gbc.gridy = 13;
        parcoursComboBox = createUltraModernParcoursComboBox();
        parcoursComboBox.setPreferredSize(new Dimension(650, 55));
        for (Parcours parcours : allParcours) {
            if (parcours != null) {
                parcoursComboBox.addItem(parcours);
            }
        }
        if (isEdit && etudiant != null && etudiant.getParcoursId() != null) {
            for (Parcours parcours : allParcours) {
                if (parcours != null && parcours.getId() != null && parcours.getId().equals(etudiant.getParcoursId())) {
                    parcoursComboBox.setSelectedItem(parcours);
                    break;
                }
            }
        }
        formPanel.add(parcoursComboBox, gbc);

        // Photo
        gbc.gridy = 14;
        formPanel.add(createUltraModernFormLabel("Photo", IconType.CAMERA), gbc);
        gbc.gridy = 15;
        JPanel photoPanel = createUltraModernPhotoSelectionPanel();
        formPanel.add(photoPanel, gbc);

        return formPanel;
    }

    private JComboBox<Niveau> createUltraModernNiveauComboBox() {
        JComboBox<Niveau> comboBox = new JComboBox<Niveau>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre subtile
                g2d.setColor(new Color(0, 0, 0, 5));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 16, 16);
                
                // Fond avec gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, getBackground(),
                    0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 16, 16);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        comboBox.setFont(getModernFont(Font.PLAIN, 15));
        comboBox.setBackground(CARD_COLOR);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // UI personnalisée
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected ComboPopup createPopup() {
                return new BasicComboPopup(comboBox) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Ombre pour la popup
                        g2d.setColor(new Color(0, 0, 0, 30));
                        g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);
                        
                        // Fond avec gradient
                        GradientPaint gradient = new GradientPaint(
                            0, 0, CARD_COLOR,
                            0, getHeight(), new Color(248, 250, 252)
                        );
                        g2d.setPaint(gradient);
                        g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);
                        
                        // Bordure
                        g2d.setColor(PRIMARY_COLOR);
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 12, 12);
                        g2d.dispose();
                        super.paintComponent(g);
                    }
                };
            }

            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        g2d.setColor(TEXT_SECONDARY);
                        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        
                        int centerX = getWidth() / 2;
                        int centerY = getHeight() / 2;
                        
                        g2d.drawLine(centerX - 4, centerY - 2, centerX, centerY + 2);
                        g2d.drawLine(centerX, centerY + 2, centerX + 4, centerY - 2);
                        g2d.dispose();
                    }
                };
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                return button;
            }
        });

        return comboBox;
    }

    private JComboBox<Parcours> createUltraModernParcoursComboBox() {
        JComboBox<Parcours> comboBox = new JComboBox<Parcours>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre subtile
                g2d.setColor(new Color(0, 0, 0, 5));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 16, 16);
                
                // Fond avec gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, getBackground(),
                    0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 16, 16);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        comboBox.setFont(getModernFont(Font.PLAIN, 15));
        comboBox.setBackground(CARD_COLOR);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        // UI personnalisée
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected ComboPopup createPopup() {
                return new BasicComboPopup(comboBox) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        // Ombre pour la popup
                        g2d.setColor(new Color(0, 0, 0, 30));
                        g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);
                        
                        // Fond avec gradient
                        GradientPaint gradient = new GradientPaint(
                            0, 0, CARD_COLOR,
                            0, getHeight(), new Color(248, 250, 252)
                        );
                        g2d.setPaint(gradient);
                        g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);
                        
                        // Bordure
                        g2d.setColor(PRIMARY_COLOR);
                        g2d.setStroke(new BasicStroke(2));
                        g2d.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 12, 12);
                        g2d.dispose();
                        super.paintComponent(g);
                    }
                };
            }

            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton() {
                    @Override
                    public void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        g2d.setColor(TEXT_SECONDARY);
                        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        
                        int centerX = getWidth() / 2;
                        int centerY = getHeight() / 2;
                        
                        g2d.drawLine(centerX - 4, centerY - 2, centerX, centerY + 2);
                        g2d.drawLine(centerX, centerY + 2, centerX + 4, centerY - 2);
                        g2d.dispose();
                    }
                };
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setContentAreaFilled(false);
                button.setFocusPainted(false);
                return button;
            }
        });

        return comboBox;
    }

    private JPanel createUltraModernPhotoSelectionPanel() {
        JPanel photoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 0));
        photoPanel.setBackground(CARD_COLOR);

        JButton choosePhotoButton = createUltraModernButton("Choisir une Photo", IconType.CAMERA, SECONDARY_COLOR);
        choosePhotoButton.addActionListener(_ -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Images (*.jpg, *.png, *.jpeg)", "jpg", "png", "jpeg"));
            if (fileChooser.showOpenDialog(studentDialog) == JFileChooser.APPROVE_OPTION) {
                selectedPhoto = fileChooser.getSelectedFile();
                if (selectedPhoto != null) {
                    photoPreviewLabel.setText(selectedPhoto.getName());
                    photoPreviewLabel.setForeground(SUCCESS_COLOR);
                    photoPreviewLabel.setIcon(createVectorIcon(IconType.CAMERA, 18, SUCCESS_COLOR));
                }
            }
        });

        photoPreviewLabel = new JLabel("Aucune photo sélectionnée");
        photoPreviewLabel.setFont(getModernFont(Font.ITALIC, 15));
        photoPreviewLabel.setForeground(TEXT_SECONDARY);
        photoPreviewLabel.setIcon(createVectorIcon(IconType.CAMERA, 18, TEXT_SECONDARY));

        photoPanel.add(choosePhotoButton);
        photoPanel.add(photoPreviewLabel);

        return photoPanel;
    }

    private JPanel createUltraModernDialogButtons(Etudiant etudiant, boolean isEdit) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(35, 0, 0, 0));

        JButton saveButton = createUltraModernButton("Enregistrer", IconType.SAVE, PRIMARY_COLOR);
        JButton cancelButton = createUltraModernButton("Annuler", IconType.CANCEL, new Color(156, 163, 175));

        saveButton.addActionListener(_ -> saveStudent(etudiant, isEdit));
        cancelButton.addActionListener(_ -> studentDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    // Amélioration 8: Dialog de modification de niveau/parcours sans bug
    private void showUpdateNiveauParcoursDialog(Etudiant etudiant) {
        if (etudiant == null) {
            showModernErrorDialog("Erreur: Étudiant non sélectionné.");
            return;
        }

        JDialog dialog = new UltraModernDialog((Frame) SwingUtilities.getWindowAncestor(this), 
            "Mettre à jour Niveau/Parcours");
        dialog.setSize(550, 350);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CARD_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        JPanel headerPanel = createUltraModernDialogHeader("Mettre à jour Niveau/Parcours",
            "Sélectionnez le nouveau niveau et parcours pour " + etudiant.getPrenom() + " " + etudiant.getNom(),
            IconType.REFRESH);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel updatePanel = new JPanel(new GridBagLayout());
        updatePanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        updatePanel.add(createUltraModernFormLabel("Niveau", IconType.BOOK), gbc);
        gbc.gridx = 1;
        
        JComboBox<Niveau> niveauUpdateCombo = createUltraModernNiveauComboBox();
        niveauUpdateCombo.setPreferredSize(new Dimension(320, 50));
        for (Niveau niveau : allNiveaux) {
            if (niveau != null) {
                niveauUpdateCombo.addItem(niveau);
            }
        }
        // CORRECTION: Sélection correcte du niveau actuel
        for (Niveau niveau : allNiveaux) {
            if (niveau != null && niveau.getId() != null && niveau.getId().equals(etudiant.getNiveauId())) {
                niveauUpdateCombo.setSelectedItem(niveau);
                break;
            }
        }
        updatePanel.add(niveauUpdateCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        updatePanel.add(createUltraModernFormLabel("Parcours", IconType.TARGET), gbc);
        gbc.gridx = 1;
        JComboBox<Parcours> parcoursUpdateCombo = createUltraModernParcoursComboBox();
        parcoursUpdateCombo.setPreferredSize(new Dimension(320, 50));
        for (Parcours parcours : allParcours) {
            if (parcours != null) {
                parcoursUpdateCombo.addItem(parcours);
            }
        }
        // CORRECTION: Sélection correcte du parcours actuel
        for (Parcours parcours : allParcours) {
            if (parcours != null && parcours.getId() != null && parcours.getId().equals(etudiant.getParcoursId())) {
                parcoursUpdateCombo.setSelectedItem(parcours);
                break;
            }
        }
        updatePanel.add(parcoursUpdateCombo, gbc);

        mainPanel.add(updatePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        JButton saveButton = createUltraModernButton("Enregistrer", IconType.SAVE, PRIMARY_COLOR);
        saveButton.addActionListener(_ -> {
            Niveau selectedNiveau = (Niveau) niveauUpdateCombo.getSelectedItem();
            Parcours selectedParcours = (Parcours) parcoursUpdateCombo.getSelectedItem();

            if (selectedNiveau != null && selectedParcours != null) {
                try {
                    studentService.updateNiveauParcours(etudiant.getId(), selectedNiveau.getId(),
                        selectedParcours.getId());
                    showModernSuccessDialog("Niveau et parcours mis à jour avec succès!");
                    loadStudents();
                    dialog.dispose();
                } catch (ApiException ex) {
                    showModernErrorDialog("Erreur lors de la mise à jour: " + ex.getMessage());
                }
            }
        });

        JButton cancelButton = createUltraModernButton("Annuler", IconType.CANCEL, ERROR_COLOR);
        cancelButton.addActionListener(_ -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    // Continuer avec les autres méthodes existantes...
    private void saveStudent(Etudiant etudiant, boolean isEdit) {
        String matricule = matriculeField.getText() != null ? matriculeField.getText().trim() : "";
        String firstName = firstNameField.getText() != null ? firstNameField.getText().trim() : "";
        String lastName = lastNameField.getText() != null ? lastNameField.getText().trim() : "";
        String email = emailField.getText() != null ? emailField.getText().trim() : "";
        String adresse = adresseField.getText() != null ? adresseField.getText().trim() : "";
        String niveauClasse = niveauClasseField != null && niveauClasseField.getText() != null ? 
            niveauClasseField.getText().trim() : "";

        Niveau selectedNiveau = (Niveau) niveauComboBox.getSelectedItem();
        Parcours selectedParcours = (Parcours) parcoursComboBox.getSelectedItem();

        if (matricule.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            showModernErrorDialog("Tous les champs marqués d'un * sont obligatoires.");
            return;
        }

        if (!isValidEmail(email)) {
            showModernErrorDialog("Veuillez saisir une adresse email valide.");
            emailField.requestFocus();
            return;
        }

        if (selectedNiveau == null) {
            showModernErrorDialog("Veuillez sélectionner un niveau.");
            return;
        }

        if (selectedParcours == null) {
            showModernErrorDialog("Veuillez sélectionner un parcours.");
            return;
        }

        Etudiant student = new Etudiant();
        student.setMatricule(matricule);
        student.setPrenom(firstName);
        student.setNom(lastName);
        student.setEmail(email);
        student.setAdresse(adresse);
        student.setNiveauClasse(niveauClasse);
        student.setNiveauId(selectedNiveau.getId());
        student.setParcoursId(selectedParcours.getId());

        try {
            if (isEdit && etudiant != null) {
                student.setId(etudiant.getId());
                studentService.updateEtudiant(etudiant.getId(), student, selectedPhoto);
                showModernSuccessDialog("Étudiant modifié avec succès!");
            } else {
                studentService.addEtudiant(student, responsableId, selectedPhoto);
                showModernSuccessDialog("Étudiant ajouté avec succès!");
            }

            loadStudents();
            clearFields();
            studentDialog.dispose();

        } catch (ApiException ex) {
            showModernErrorDialog("Erreur lors de l'enregistrement: " + ex.getMessage());
        }
    }

    private void showModernErrorDialog(String message) {
        showModernNotificationDialog(message, "Erreur", IconType.CANCEL, ERROR_COLOR);
    }

    private void showModernSuccessDialog(String message) {
        showModernNotificationDialog(message, "Succès", IconType.SAVE, SUCCESS_COLOR);
    }

    private void showModernNotificationDialog(String message, String title, IconType iconType, Color color) {
        JDialog dialog = new UltraModernDialog((Frame) SwingUtilities.getWindowAncestor(this), title);
        dialog.setSize(500, 220);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        messagePanel.setBackground(CARD_COLOR);

        JLabel iconLabel = new JLabel(createVectorIcon(iconType, 36, color));
        messagePanel.add(iconLabel);

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(getModernFont(Font.PLAIN, 17));
        messageLabel.setForeground(TEXT_PRIMARY);
        messagePanel.add(messageLabel);

        panel.add(messagePanel, BorderLayout.CENTER);

        JButton okButton = createUltraModernButton("OK", IconType.SAVE, PRIMARY_COLOR);
        okButton.addActionListener(_ -> dialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.add(okButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Continuer avec les autres méthodes existantes...
    private void loadStudents() {
        SwingWorker<List<Etudiant>, Void> worker = new SwingWorker<List<Etudiant>, Void>() {
            @Override
            protected List<Etudiant> doInBackground() throws Exception {
                try {
                    List<Etudiant> students = studentService.getAllEtudiants(responsableId);
                    System.out.println("Fetched " + (students != null ? students.size() : 0) + " students");
                    return students;
                } catch (Exception e) {
                    System.err.println("Error fetching students: " + e.getMessage());
                    throw e;
                }
            }

            @Override
            protected void done() {
                try {
                    List<Etudiant> students = get();
                    allStudents = students != null ? new ArrayList<>(students) : new ArrayList<>();
                    System.out.println("Students loaded: " + allStudents.size());
                    if (allStudents.isEmpty()) {
                        showWarningNotification("Aucun étudiant trouvé.");
                    }
                    filterStudents();
                } catch (InterruptedException | ExecutionException ex) {
                    System.err.println("Error loading students: " + ex.getMessage());
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
            System.out.println("Filtering " + students.size() + " students");

            for (Etudiant e : students) {
                if (e != null) {
                    tableModel.addRow(new Object[]{
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

            tableModel.fireTableDataChanged();
            if (!isTableView) {
                updateCardsView();
            }
        }
    }

    private List<Etudiant> filterStudentsList() {
        List<Etudiant> filteredList = new ArrayList<>(allStudents);
        String searchText = searchField.getText().trim().toLowerCase();
        System.out.println("Search text: '" + searchText + "'");

        String effectiveSearchText = searchText.equals("rechercher des étudiants...") || searchText.isEmpty() ? 
            "" : searchText;

        if (!effectiveSearchText.isEmpty()) {
            filteredList.removeIf(e -> {
                if (e == null) return true;
                String fullName = (e.getPrenom() != null ? e.getPrenom() : "") + " " +
                    (e.getNom() != null ? e.getNom() : "");
                String matricule = e.getMatricule() != null ? e.getMatricule() : "";
                String email = e.getEmail() != null ? e.getEmail() : "";

                return !fullName.toLowerCase().contains(effectiveSearchText) &&
                       !matricule.toLowerCase().contains(effectiveSearchText) &&
                       !email.toLowerCase().contains(effectiveSearchText);
            });
        }

        String niveauFilter = (String) niveauFilterComboBox.getSelectedItem();
        if (!"TOUS LES NIVEAUX".equals(niveauFilter)) {
            filteredList.removeIf(e -> e == null || !getNiveauLabel(e.getNiveauId()).equals(niveauFilter));
        }

        String parcoursFilter = (String) parcoursFilterComboBox.getSelectedItem();
        if (!"TOUS LES PARCOURS".equals(parcoursFilter)) {
            filteredList.removeIf(e -> e == null || !getParcoursLabel(e.getParcoursId()).equals(parcoursFilter));
        }

        return filteredList;
    }

    private String getNiveauLabel(String niveauId) {
        if (niveauId == null || allNiveaux == null) return "N/A";
        return allNiveaux.stream()
            .filter(n -> n != null && n.getId() != null && n.getId().equals(niveauId))
            .map(Niveau::getNom)
            .findFirst()
            .orElse("N/A");
    }

    private String getParcoursLabel(String parcoursId) {
        if (parcoursId == null || allParcours == null) return "N/A";
        return allParcours.stream()
            .filter(p -> p != null && p.getId() != null && p.getId().equals(parcoursId))
            .map(Parcours::getNom)
            .findFirst()
            .orElse("N/A");
    }

    private void deleteStudent(String studentId) {
        try {
            studentService.deleteEtudiant(studentId);
            showModernSuccessDialog("Étudiant supprimé avec succès!");
            loadStudents();
        } catch (ApiException ex) {
            showModernErrorDialog("Erreur lors de la suppression: " + ex.getMessage());
        }
    }

    private void showErrorNotification(String message) {
        showModernErrorDialog(message);
    }

    private void showWarningNotification(String message) {
        showModernNotificationDialog(message, "Avertissement", IconType.REFRESH, WARNING_COLOR);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private void validateEmailField(JTextField emailField) {
        String email = emailField.getText().trim();
        if (!isValidEmail(email) && !email.isEmpty()) {
            emailField.setBorder(BorderFactory.createCompoundBorder(
                new UltraModernBorder(ERROR_COLOR),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        } else {
            emailField.setBorder(BorderFactory.createCompoundBorder(
                new UltraModernBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));
        }
    }

    private void clearFields() {
        if (matriculeField != null) matriculeField.setText("");
        if (firstNameField != null) firstNameField.setText("");
        if (lastNameField != null) lastNameField.setText("");
        if (emailField != null) emailField.setText("");
        if (adresseField != null) adresseField.setText("");
        if (niveauClasseField != null) niveauClasseField.setText("");
        if (niveauComboBox != null) niveauComboBox.setSelectedIndex(-1);
        if (parcoursComboBox != null) parcoursComboBox.setSelectedIndex(-1);
        selectedPhoto = null;
        if (photoPreviewLabel != null) {
            photoPreviewLabel.setText("Aucune photo sélectionnée");
            photoPreviewLabel.setForeground(TEXT_SECONDARY);
            photoPreviewLabel.setIcon(createVectorIcon(IconType.CAMERA, 18, TEXT_SECONDARY));
        }
    }

    // Classes internes pour les renderers et editors ultra-modernes...
    private class EnhancedPhotoCellRenderer extends JPanel implements TableCellRenderer {
        private EnhancedRoundImageLabel imageLabel;

        public EnhancedPhotoCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            imageLabel = new EnhancedRoundImageLabel(50);
            add(imageLabel);
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());

            int modelRow = table.convertRowIndexToModel(row);
            List<Etudiant> filteredStudents = filterStudentsList();

            if (modelRow >= 0 && modelRow < filteredStudents.size()) {
                Etudiant etudiant = filteredStudents.get(modelRow);
                if (etudiant != null && etudiant.getphoto_url() != null && !etudiant.getphoto_url().isEmpty()) {
                    try {
                        imageLabel.setImage(URI.create(etudiant.getphoto_url()).toURL());
                    } catch (Exception e) {
                        imageLabel.setIcon(createVectorIcon(IconType.USER, 25, TEXT_SECONDARY));
                    }
                } else {
                    imageLabel.setIcon(createVectorIcon(IconType.USER, 25, TEXT_SECONDARY));
                }
            } else {
                imageLabel.setIcon(createVectorIcon(IconType.USER, 25, TEXT_SECONDARY));
            }

            return this;
        }
    }

    private class EnhancedButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton, deleteButton, updateButton, notesButton;

        public EnhancedButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
            setOpaque(false);

            editButton = createUltraModernTableActionButton(IconType.EDIT, WARNING_COLOR);
            deleteButton = createUltraModernTableActionButton(IconType.DELETE, ERROR_COLOR);
            updateButton = createUltraModernTableActionButton(IconType.REFRESH, SECONDARY_COLOR);
            notesButton = createUltraModernTableActionButton(IconType.NOTES, NOTES_COLOR);

            add(editButton);
            add(deleteButton);
            add(updateButton);
            add(notesButton);
        }

        private JButton createUltraModernTableActionButton(IconType iconType, Color color) {
            JButton button = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Gradient sophistiqué
                    GradientPaint gradient = new GradientPaint(
                        0, 0, color,
                        0, getHeight(), color.darker()
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };

            button.setIcon(createVectorIcon(iconType, 15, Color.WHITE));
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(38, 35));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            return button;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }

    private class EnhancedButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton editButton, deleteButton, updateButton, notesButton;
        private Etudiant currentStudent;

        public EnhancedButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
            panel.setOpaque(false);

            editButton = createUltraModernTableActionButton(IconType.EDIT, WARNING_COLOR);
            deleteButton = createUltraModernTableActionButton(IconType.DELETE, ERROR_COLOR);
            updateButton = createUltraModernTableActionButton(IconType.REFRESH, SECONDARY_COLOR);
            notesButton = createUltraModernTableActionButton(IconType.NOTES, NOTES_COLOR);

            editButton.addActionListener(_ -> {
                fireEditingStopped();
                if (currentStudent != null) {
                    showStudentFormDialog(currentStudent);
                }
            });

            deleteButton.addActionListener(_ -> {
                fireEditingStopped();
                if (currentStudent != null && currentStudent.getId() != null) {
                    showUltraModernDeleteConfirmDialog(() -> deleteStudent(currentStudent.getId()));
                }
            });

            updateButton.addActionListener(_ -> {
                fireEditingStopped();
                if (currentStudent != null) {
                    showUpdateNiveauParcoursDialog(currentStudent);
                }
            });

            notesButton.addActionListener(_ -> {
                fireEditingStopped();
                if (currentStudent != null) {
                    showUltraModernNotesDialog(currentStudent);
                }
            });

            panel.add(editButton);
            panel.add(deleteButton);
            panel.add(updateButton);
            panel.add(notesButton);
        }

        private JButton createUltraModernTableActionButton(IconType iconType, Color color) {
            JButton button = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Gradient sophistiqué
                    GradientPaint gradient = new GradientPaint(
                        0, 0, color,
                        0, getHeight(), color.darker()
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };

            button.setIcon(createVectorIcon(iconType, 15, Color.WHITE));
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(38, 35));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            return button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, 
                int row, int column) {
            int modelRow = table.convertRowIndexToModel(row);
            List<Etudiant> filteredStudents = filterStudentsList();

            if (modelRow >= 0 && modelRow < filteredStudents.size()) {
                currentStudent = filteredStudents.get(modelRow);
            } else {
                currentStudent = null;
            }

            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }

    private class UltraModernNotesButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton;

        public UltraModernNotesButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER));
            setOpaque(false);
            editButton = createUltraModernTableActionButton(IconType.EDIT, WARNING_COLOR);
            add(editButton);
        }

        private JButton createUltraModernTableActionButton(IconType iconType, Color color) {
            JButton button = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Gradient sophistiqué
                    GradientPaint gradient = new GradientPaint(
                        0, 0, color,
                        0, getHeight(), color.darker()
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };

            button.setIcon(createVectorIcon(iconType, 15, Color.WHITE));
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(38, 35));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            return button;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, 
                boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return this;
        }
    }

    private class UltraModernNotesButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton editButton;
        private Etudiant etudiant;
        private JTable notesTable;
        private DefaultTableModel tableModel;

        public UltraModernNotesButtonEditor(Etudiant etudiant, JTable notesTable, DefaultTableModel tableModel) {
            this.etudiant = etudiant;
            this.notesTable = notesTable;
            this.tableModel = tableModel;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            panel.setOpaque(false);

            editButton = createUltraModernTableActionButton(IconType.EDIT, WARNING_COLOR);
            editButton.addActionListener(_ -> {
                fireEditingStopped();
                editNote();
            });

            panel.add(editButton);
        }

        private JButton createUltraModernTableActionButton(IconType iconType, Color color) {
            JButton button = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Gradient sophistiqué
                    GradientPaint gradient = new GradientPaint(
                        0, 0, color,
                        0, getHeight(), color.darker()
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };

            button.setIcon(createVectorIcon(iconType, 15, Color.WHITE));
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(38, 35));
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            return button;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, 
                int row, int column) {
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }

        private void editNote() {
            int selectedRow = notesTable.getSelectedRow();
            if (selectedRow >= 0) {
                String matiereName = (String) tableModel.getValueAt(selectedRow, 0);
                String currentNote = (String) tableModel.getValueAt(selectedRow, 1);
                String currentSemestre = (String) tableModel.getValueAt(selectedRow, 2);
                String currentAnnee = (String) tableModel.getValueAt(selectedRow, 3);

                // Créer un dialogue ultra-moderne pour l'édition de note
                showUltraModernNoteEditDialog(matiereName, currentNote, currentSemestre, currentAnnee, selectedRow);
            }
        }

        private void showUltraModernNoteEditDialog(String matiereName, String currentNote, 
                String currentSemestre, String currentAnnee, int selectedRow) {
            
            JDialog editDialog = new UltraModernDialog(
                (Frame) SwingUtilities.getWindowAncestor(StudentManagementFrame.this),
                "Modifier la Note"
            );
            editDialog.setSize(500, 400);
            editDialog.setLocationRelativeTo(StudentManagementFrame.this);

            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(CARD_COLOR);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

            // En-tête
            JPanel headerPanel = new JPanel();
            headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
            headerPanel.setBackground(CARD_COLOR);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

            JLabel titleLabel = new JLabel("Modifier la Note");
            titleLabel.setFont(getModernFont(Font.BOLD, 24));
            titleLabel.setForeground(TEXT_PRIMARY);
            titleLabel.setIcon(createVectorIcon(IconType.EDIT, 24, PRIMARY_COLOR));

            JLabel matiereLabel = new JLabel("Matière: " + matiereName);
            matiereLabel.setFont(getModernFont(Font.PLAIN, 16));
            matiereLabel.setForeground(TEXT_SECONDARY);
            matiereLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

            headerPanel.add(titleLabel);
            headerPanel.add(matiereLabel);

            // Formulaire
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(CARD_COLOR);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(15, 15, 15, 15);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0;
            formPanel.add(createUltraModernFormLabel("Note (/20)", IconType.STAR), gbc);
            gbc.gridx = 1;
            JTextField noteField = createUltraModernTextField(currentNote);
            noteField.setPreferredSize(new Dimension(200, 45));
            formPanel.add(noteField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            formPanel.add(createUltraModernFormLabel("Semestre", IconType.CHART), gbc);
            gbc.gridx = 1;
            JTextField semestreField = createUltraModernTextField(currentSemestre);
            semestreField.setPreferredSize(new Dimension(200, 45));
            formPanel.add(semestreField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            formPanel.add(createUltraModernFormLabel("Année", IconType.CHART), gbc);
            gbc.gridx = 1;
            JTextField anneeField = createUltraModernTextField(currentAnnee);
            anneeField.setPreferredSize(new Dimension(200, 45));
            formPanel.add(anneeField, gbc);

            // Boutons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            buttonPanel.setBackground(CARD_COLOR);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

            JButton saveButton = createUltraModernButton("Enregistrer", IconType.SAVE, SUCCESS_COLOR);
            JButton cancelButton = createUltraModernButton("Annuler", IconType.CANCEL, ERROR_COLOR);

            saveButton.addActionListener(_ -> {
                try {
                    double noteValue = Double.parseDouble(noteField.getText().trim());
                    if (noteValue < 0 || noteValue > 20) {
                        showModernErrorDialog("La note doit être comprise entre 0 et 20.");
                        return;
                    }

                    String semestre = semestreField.getText().trim();
                    String annee = anneeField.getText().trim();

                    if (semestre.isEmpty() || annee.isEmpty()) {
                        showModernErrorDialog("Les champs Semestre et Année sont obligatoires.");
                        return;
                    }

                    Matiere matiere = studentService.getAllMatieres().stream()
                        .filter(m -> m.getNom().equals(matiereName))
                        .findFirst()
                        .orElse(null);

                    if (matiere != null) {
                        Note note = new Note();
                        note.setEtudiantId(etudiant.getId());
                        note.setMatiereId(matiere.getId());
                        note.setValeur((float) noteValue);
                        note.setSemestre(semestre);
                        note.setAnnee(annee);

                        List<Note> existingNotes = studentService.getNotesByEtudiant(etudiant.getId());
                        Note existingNote = existingNotes.stream()
                            .filter(n -> n.getMatiereId().equals(matiere.getId()) &&
                                       n.getSemestre().equals(semestre) &&
                                       n.getAnnee().equals(annee))
                            .findFirst()
                            .orElse(null);

                        if (existingNote != null) {
                            note.setId(existingNote.getId());
                            studentService.updateNote(existingNote.getId(), note);
                        } else {
                            studentService.addNote(note);
                        }

                        tableModel.setValueAt(String.valueOf(noteValue), selectedRow, 1);
                        tableModel.setValueAt(semestre, selectedRow, 2);
                        tableModel.setValueAt(annee, selectedRow, 3);

                        showModernSuccessDialog("Note mise à jour avec succès!");
                        editDialog.dispose();
                    }
                } catch (NumberFormatException ex) {
                    showModernErrorDialog("Veuillez entrer une note valide.");
                } catch (ApiException ex) {
                    showModernErrorDialog("Erreur lors de la mise à jour de la note: " + ex.getMessage());
                }
            });

            cancelButton.addActionListener(_ -> editDialog.dispose());

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(formPanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            editDialog.add(mainPanel);
            editDialog.setVisible(true);
        }
    }

    private class EnhancedRoundImageLabel extends JLabel {
        private Image image;
        private int size;

        public EnhancedRoundImageLabel(int size) {
            this.size = size;
            setPreferredSize(new Dimension(size, size));
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
            setFont(getModernFont(Font.PLAIN, size / 2));
        }

        public void setImage(URL url) {
            try {
                image = new ImageIcon(url).getImage();
                setText(null);
                setIcon(null);
                repaint();
            } catch (Exception e) {
                image = null;
                setIcon(createVectorIcon(IconType.USER, size / 2, TEXT_SECONDARY));
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int diameter = Math.min(getWidth(), getHeight());
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;

            Shape clip = new Ellipse2D.Double(x, y, diameter, diameter);
            g2d.setClip(clip);

            if (image != null) {
                g2d.drawImage(image, x, y, diameter, diameter, this);
            } else {
                g2d.setClip(null);
                g2d.setColor(new Color(248, 250, 252));
                g2d.fillOval(x, y, diameter, diameter);
                super.paintComponent(g2d);
            }

            g2d.setClip(null);
            
            // Bordure avec gradient
            GradientPaint borderGradient = new GradientPaint(
                x, y, PRIMARY_COLOR,
                x + diameter, y + diameter, SECONDARY_COLOR
            );
            g2d.setPaint(borderGradient);
            g2d.setStroke(new BasicStroke(3f));
            g2d.drawOval(x, y, diameter - 1, diameter - 1);

            g2d.dispose();
        }
    }
}