package com.studentmanagement.ui.parameters;

import com.studentmanagement.model.Mention;
import com.studentmanagement.model.Niveau;
import com.studentmanagement.model.Parcours;
import com.studentmanagement.model.Matiere;
import com.studentmanagement.service.ParameterService;
import com.studentmanagement.service.ApiException;
import com.studentmanagement.ui.common.MainWindow;
import com.studentmanagement.ui.common.SidebarUtil;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private JComboBox<AcademicLevel> niveauComboBox;
    private JButton addParcoursButton;
    private JButton addMatiereButton;
    private JDialog currentDialog;

    // Palette de couleurs ultra-modernes
    private static final Color PRIMARY_COLOR = new Color(79, 70, 229);
    private static final Color PRIMARY_HOVER = new Color(67, 56, 202);
    private static final Color SECONDARY_COLOR = new Color(139, 92, 246);
    private static final Color SUCCESS_COLOR = new Color(16, 185, 129);
    private static final Color WARNING_COLOR = new Color(245, 158, 11);
    private static final Color ERROR_COLOR = new Color(239, 68, 68);
    private static final Color BACKGROUND_COLOR = new Color(248, 250, 252);
    private static final Color CARD_COLOR = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(15, 23, 42);
    private static final Color TEXT_SECONDARY = new Color(100, 116, 139);
    private static final Color BORDER_COLOR = new Color(226, 232, 240);
    private static final Color HOVER_COLOR = new Color(241, 245, 249);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 10);
    private static final Color ACCENT_BLUE = new Color(59, 130, 246);
    private static final Color ACCENT_PURPLE = new Color(147, 51, 234);
    private static final Color ACCENT_PINK = new Color(236, 72, 153);
    private static final Color ACCENT_GREEN = new Color(34, 197, 94);

    // Énumération pour les types d'icônes
    private enum IconType {
        PLUS, EDIT, DELETE, REFRESH, SETTINGS, SEARCH, GRID, TABLE, GRADUATION, USER, EMAIL, 
        LOCATION, BOOK, TARGET, CAMERA, SAVE, CANCEL, CHART, STATS, FILTER, ACADEMIC, STAR, 
        TROPHY, MENTION, LEVEL, COURSE, SUBJECT, GEAR, TOOLS, DIAMOND, CROWN, SHIELD
    }

    // Classe pour représenter les niveaux académiques avec semestres
    private static class AcademicLevel {
        private final String id;
        private final String displayName;
        private final String description;
        private final String[] semesters;

        public AcademicLevel(String id, String displayName, String description, String[] semesters) {
            this.id = id;
            this.displayName = displayName;
            this.description = description;
            this.semesters = semesters;
        }

        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public String[] getSemesters() { return semesters; }

        @Override
        public String toString() { return displayName; }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            AcademicLevel that = (AcademicLevel) obj;
            return id.equals(that.id);
        }

        @Override
        public int hashCode() { return id.hashCode(); }
    }

    public ParametersFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.parameterService = new ParameterService();
        this.parameterService.setJwtToken(mainWindow.getCurrentResponsable().getToken());
        initializeUI();
        loadAllData();
    }

    private Font getModernFont(int style, int size) {
        return new Font("Segoe UI", style, size);
    }

    private Icon createVectorIcon(IconType type, int size, Color color) {
        return new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                
                int centerX = x + size / 2;
                int centerY = y + size / 2;
                
                switch (type) {
                    case PLUS:
                        g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawLine(centerX, y + 3, centerX, y + size - 3);
                        g2d.drawLine(x + 3, centerY, x + size - 3, centerY);
                        break;
                    case EDIT:
                        g2d.drawLine(x + 3, y + size - 3, x + size - 6, y + 6);
                        g2d.drawLine(x + size - 6, y + 6, x + size - 3, y + 3);
                        g2d.drawLine(x + size - 3, y + 3, x + size - 6, y + 6);
                        g2d.drawLine(x + 3, y + size - 6, x + 6, y + size - 3);
                        g2d.fillOval(x + size - 5, y + 1, 3, 3);
                        break;
                    case DELETE:
                        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawRect(x + 4, y + 6, size - 8, size - 8);
                        g2d.drawLine(x + 6, y + 4, x + size - 6, y + 4);
                        g2d.drawLine(x + 7, y + 8, x + 7, y + size - 4);
                        g2d.drawLine(centerX, y + 8, centerX, y + size - 4);
                        g2d.drawLine(x + size - 7, y + 8, x + size - 7, y + size - 4);
                        break;
                    case SETTINGS:
                        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawOval(centerX - 3, centerY - 3, 6, 6);
                        for (int i = 0; i < 8; i++) {
                            double angle = i * Math.PI / 4;
                            int x1 = (int) (centerX + 6 * Math.cos(angle));
                            int y1 = (int) (centerY + 6 * Math.sin(angle));
                            int x2 = (int) (centerX + 8 * Math.cos(angle));
                            int y2 = (int) (centerY + 8 * Math.sin(angle));
                            g2d.drawLine(x1, y1, x2, y2);
                        }
                        break;
                    case MENTION:
                        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawOval(centerX - 6, centerY - 6, 12, 12);
                        g2d.fillOval(centerX - 2, centerY - 2, 4, 4);
                        g2d.drawLine(centerX - 4, centerY + 4, centerX + 4, centerY + 4);
                        break;
                    case LEVEL:
                        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawLine(x + 2, y + size - 2, x + size - 2, y + size - 2);
                        g2d.drawLine(x + 4, y + size - 4, x + size - 4, y + size - 4);
                        g2d.drawLine(x + 6, y + size - 6, x + size - 6, y + size - 6);
                        g2d.drawLine(centerX, y + 2, centerX, y + size - 8);
                        break;
                    case COURSE:
                        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawLine(x + 2, centerY, x + size - 2, centerY);
                        g2d.drawLine(x + 4, centerY - 2, x + size - 4, centerY - 2);
                        g2d.drawLine(x + 4, centerY + 2, x + size - 4, centerY + 2);
                        g2d.drawLine(centerX, y + 3, centerX, centerY + 3);
                        g2d.fillOval(centerX - 1, centerY + 3, 2, 2);
                        break;
                    case SUBJECT:
                        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawRoundRect(x + 3, y + 2, size - 6, size - 4, 3, 3);
                        g2d.drawLine(x + 6, y + 5, x + size - 6, y + 5);
                        g2d.drawLine(x + 6, y + 8, x + size - 6, y + 8);
                        g2d.drawLine(x + 6, y + 11, x + size - 8, y + 11);
                        break;
                    case SAVE:
                        g2d.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawRoundRect(x + 2, y + 2, size - 4, size - 4, 3, 3);
                        g2d.fillRect(x + 4, y + 2, size - 8, 4);
                        g2d.drawLine(x + 6, y + 8, x + 6, y + size - 4);
                        g2d.drawLine(x + size - 6, y + 8, x + size - 6, y + size - 4);
                        break;
                    case CANCEL:
                        g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawLine(x + 4, y + 4, x + size - 4, y + size - 4);
                        g2d.drawLine(x + 4, y + size - 4, x + size - 4, y + 4);
                        break;
                    case REFRESH:
                        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawArc(x + 2, y + 2, size - 4, size - 4, 45, 270);
                        g2d.drawLine(x + size - 4, y + 4, x + size - 2, y + 2);
                        g2d.drawLine(x + size - 4, y + 4, x + size - 6, y + 6);
                        break;
                    case CROWN:
                        g2d.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        int[] xPoints = {x + 2, x + 4, centerX, x + size - 4, x + size - 2, x + size - 3, x + 3};
                        int[] yPoints = {y + size - 3, y + 4, y + 2, y + 4, y + size - 3, y + size - 2, y + size - 2};
                        g2d.drawPolygon(xPoints, yPoints, 7);
                        g2d.fillOval(centerX - 1, y + 6, 2, 2);
                        break;
                    case DIAMOND:
                        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        int[] diamondX = {centerX, x + size - 3, centerX, x + 3};
                        int[] diamondY = {y + 2, centerY, y + size - 2, centerY};
                        g2d.drawPolygon(diamondX, diamondY, 4);
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

        JPanel headerPanel = createUltraModernHeaderPanel();
        mainContent.add(headerPanel, BorderLayout.NORTH);

        tabbedPane = createUltraModernTabbedPane();
        mainContent.add(tabbedPane, BorderLayout.CENTER);

        return mainContent;
    }

    private JPanel createUltraModernHeaderPanel() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 200),
                    0, getHeight(), new Color(248, 250, 252, 100)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                // Subtle border
                g2d.setColor(new Color(226, 232, 240, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2d.dispose();
            }
        };
        
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Section titre avec icône et effets
        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);

        // Icône avec effet de brillance
        JLabel iconLabel = new JLabel(createVectorIcon(IconType.SETTINGS, 40, PRIMARY_COLOR));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        titleRow.add(iconLabel);
        titleRow.add(Box.createHorizontalStrut(20));

        JLabel titleLabel = new JLabel("Gestion des Paramètres");
        titleLabel.setFont(getModernFont(Font.BOLD, 36));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleRow.add(titleLabel);

        // Badge moderne
        JLabel badgeLabel = new JLabel("ADMIN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, ACCENT_PURPLE,
                    getWidth(), getHeight(), ACCENT_PINK
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        badgeLabel.setFont(getModernFont(Font.BOLD, 10));
        badgeLabel.setForeground(Color.WHITE);
        badgeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        badgeLabel.setPreferredSize(new Dimension(50, 20));
        badgeLabel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        
        titleRow.add(Box.createHorizontalStrut(15));
        titleRow.add(badgeLabel);

        JLabel breadcrumbLabel = new JLabel("Dashboard > Paramètres > Configuration Système");
        breadcrumbLabel.setFont(getModernFont(Font.PLAIN, 14));
        breadcrumbLabel.setForeground(TEXT_SECONDARY);
        breadcrumbLabel.setBorder(BorderFactory.createEmptyBorder(15, 60, 0, 0));
        breadcrumbLabel.setIcon(createVectorIcon(IconType.SETTINGS, 14, TEXT_SECONDARY));

        titleSection.add(titleRow);
        titleSection.add(breadcrumbLabel);

        header.add(titleSection, BorderLayout.WEST);

        return header;
    }

    private JTabbedPane createUltraModernTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre plus prononcée
                g2d.setColor(new Color(0, 0, 0, 25));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
                
                // Bordure brillante
                g2d.setColor(new Color(226, 232, 240, 150));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 6, getHeight() - 6, 20, 20);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        tabbedPane.setFont(getModernFont(Font.BOLD, 16));
        tabbedPane.setBackground(CARD_COLOR);
        tabbedPane.setForeground(TEXT_PRIMARY);
        tabbedPane.setBorder(new UltraModernBorder());

        // Onglets avec icônes améliorées
        tabbedPane.addTab("   Mentions   ", createVectorIcon(IconType.CROWN, 18, WARNING_COLOR), 
            createTablePanel("Mention", mentionTableModel = createTableModel(new String[]{"Nom", "Description", "Actions"}), 
            this::addMention, this::editMention));
        
        tabbedPane.addTab("   Niveaux   ", createVectorIcon(IconType.LEVEL, 18, ACCENT_BLUE), 
            createTablePanel("Niveau", niveauTableModel = createTableModel(new String[]{"Nom", "Description", "Actions"}), 
            this::addNiveau, this::editNiveau));
        
        tabbedPane.addTab("   Parcours   ", createVectorIcon(IconType.COURSE, 18, ACCENT_PURPLE), createParcoursTablePanel());
        tabbedPane.addTab("   Matières   ", createVectorIcon(IconType.SUBJECT, 18, SUCCESS_COLOR), createMatiereTablePanel());

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Actualiser les données quand on change d'onglet
                loadAllData();
            }
        });

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
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient subtil
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(249, 250, 251)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel header = createUltraModernEntityHeader(entityName, addAction);
        panel.add(header, BorderLayout.NORTH);

        JTable table = createUltraModernTable(tableModel);
        table.getColumn("Actions").setCellRenderer(new UltraModernActionButtonRenderer());
        table.getColumn("Actions").setCellEditor(new UltraModernActionButtonEditor(editAction, () -> deleteEntity(entityName)));

        JScrollPane scrollPane = createUltraModernScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUltraModernEntityHeader(String entityName, Runnable addAction) {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 200),
                    0, getHeight(), new Color(248, 250, 252, 100)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Bordure subtile
                g2d.setColor(new Color(226, 232, 240, 80));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2d.dispose();
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel titleLabel = new JLabel("Gestion des " + entityName + "s");
        titleLabel.setFont(getModernFont(Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        
        IconType iconType = switch (entityName.toLowerCase()) {
            case "mention" -> IconType.CROWN;
            case "niveau" -> IconType.LEVEL;
            default -> IconType.SETTINGS;
        };
        titleLabel.setIcon(createVectorIcon(iconType, 24, PRIMARY_COLOR));
        titleLabel.setIconTextGap(12);

        JButton addButton = createUltraModernButton("Ajouter " + entityName, IconType.PLUS, PRIMARY_COLOR, true);
        addButton.addActionListener(_ -> addAction.run());

        header.add(titleLabel, BorderLayout.WEST);
        header.add(addButton, BorderLayout.EAST);

        return header;
    }

    private JButton createUltraModernButton(String text, IconType iconType, Color bgColor, boolean isPrimary) {
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

                // Ombre dynamique
                if (!isPressed) {
                    g2d.setColor(new Color(0, 0, 0, isHovered ? 25 : 15));
                    g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
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
                
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);

                // Bordure pour les boutons secondaires
                if (!isPrimary) {
                    g2d.setColor(isHovered ? PRIMARY_COLOR : BORDER_COLOR);
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.drawRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 15, 15);
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
        button.setBorder(BorderFactory.createEmptyBorder(12, 28, 12, 28));

        // Icône avec espacement amélioré
        Icon icon = createVectorIcon(iconType, 16, isPrimary ? Color.WHITE : TEXT_PRIMARY);
        button.setIcon(icon);
        button.setIconTextGap(10);

        return button;
    }

    private JTable createUltraModernTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component comp = super.prepareRenderer(renderer, row, column);
                if (!isRowSelected(row)) {
                    if (row % 2 == 0) {
                        comp.setBackground(CARD_COLOR);
                    } else {
                        comp.setBackground(new Color(249, 250, 251));
                    }
                } else {
                    comp.setBackground(new Color(239, 246, 255));
                }
                
                // Centrage horizontal et vertical pour toutes les cellules
                if (comp instanceof JLabel) {
                    ((JLabel) comp).setHorizontalAlignment(JLabel.CENTER);
                }
                
                return comp;
            }
        };

        table.setFont(getModernFont(Font.PLAIN, 14));
        table.setRowHeight(65);
        table.setBackground(CARD_COLOR);
        table.setSelectionBackground(new Color(239, 246, 255));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setGridColor(new Color(241, 245, 249));
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));

        // En-tête ultra-moderne
        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(getModernFont(Font.BOLD, 15));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(79, 70, 229, 100)));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 55));

        // Renderer pour centrer les en-têtes
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setVerticalAlignment(JLabel.CENTER);
                setFont(getModernFont(Font.BOLD, 15));
                setForeground(TEXT_PRIMARY);
                setBackground(new Color(248, 250, 252));
                setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
                return this;
            }
        };

        // Appliquer le renderer à toutes les colonnes d'en-tête
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

        // Renderer pour centrer le contenu des cellules
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setVerticalAlignment(JLabel.CENTER);
                setFont(getModernFont(Font.PLAIN, 14));
                setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                return this;
            }
        };

        // Appliquer le renderer à toutes les colonnes sauf Actions
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        return table;
    }

    private JScrollPane createUltraModernScrollPane(Component component) {
        JScrollPane scrollPane = new JScrollPane(component) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond transparent
                g2d.setColor(new Color(0, 0, 0, 0));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_COLOR);
        scrollPane.setBackground(new Color(0, 0, 0, 0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);

        // Scrollbar verticale améliorée
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(156, 163, 175);
                this.trackColor = new Color(243, 244, 246);
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

    private JPanel createParcoursTablePanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient subtil
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(249, 250, 251)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel header = createParcoursHeader();
        panel.add(header, BorderLayout.NORTH);

        parcoursTableModel = createTableModel(new String[]{"Nom", "Mention", "Description", "Actions"});
        JTable table = createUltraModernTable(parcoursTableModel);
        table.getColumn("Actions").setCellRenderer(new UltraModernActionButtonRenderer());
        table.getColumn("Actions").setCellEditor(new UltraModernActionButtonEditor(this::editParcours, () -> deleteEntity("Parcours")));

        JScrollPane scrollPane = createUltraModernScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createParcoursHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 200),
                    0, getHeight(), new Color(248, 250, 252, 100)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Bordure subtile
                g2d.setColor(new Color(226, 232, 240, 80));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2d.dispose();
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Gestion des Parcours");
        titleLabel.setFont(getModernFont(Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setIcon(createVectorIcon(IconType.COURSE, 24, PRIMARY_COLOR));
        titleLabel.setIconTextGap(12);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setOpaque(false);

        JLabel mentionLabel = new JLabel("Filtrer par Mention:");
        mentionLabel.setFont(getModernFont(Font.BOLD, 14));
        mentionLabel.setForeground(TEXT_PRIMARY);
        mentionLabel.setIcon(createVectorIcon(IconType.CROWN, 16, ACCENT_PURPLE));
        mentionLabel.setIconTextGap(8);

        mentionComboBox = createUltraModernMentionComboBox();
        mentionComboBox.setPreferredSize(new Dimension(220, 40));
        mentionComboBox.addActionListener(_ -> loadParcours());

        filterPanel.add(mentionLabel);
        filterPanel.add(mentionComboBox);

        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(filterPanel, BorderLayout.SOUTH);

        addParcoursButton = createUltraModernButton("Ajouter Parcours", IconType.PLUS, PRIMARY_COLOR, true);
        addParcoursButton.addActionListener(_ -> addParcours());

        header.add(leftPanel, BorderLayout.WEST);
        header.add(addParcoursButton, BorderLayout.EAST);

        return header;
    }

    private JComboBox<Mention> createUltraModernMentionComboBox() {
        JComboBox<Mention> comboBox = new JComboBox<Mention>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre plus prononcée
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(248, 250, 252)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        comboBox.setFont(getModernFont(Font.PLAIN, 14));
        comboBox.setBackground(CARD_COLOR);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

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

        // Améliorer le renderer
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
                
                return this;
            }
        });

        return comboBox;
    }

    private JPanel createMatiereTablePanel() {
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient subtil
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(249, 250, 251)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel header = createMatiereHeader();
        panel.add(header, BorderLayout.NORTH);

        matiereTableModel = createTableModel(new String[]{"Nom", "Catégorie", "Coefficient", "Niveau", "Actions"});
        JTable table = createUltraModernTable(matiereTableModel);
        table.getColumn("Actions").setCellRenderer(new UltraModernActionButtonRenderer());
        table.getColumn("Actions").setCellEditor(new UltraModernActionButtonEditor(this::editMatiere, () -> deleteEntity("Matière")));

        JScrollPane scrollPane = createUltraModernScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMatiereHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fond avec gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 200),
                    0, getHeight(), new Color(248, 250, 252, 100)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Bordure subtile
                g2d.setColor(new Color(226, 232, 240, 80));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2d.dispose();
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Gestion des Matières");
        titleLabel.setFont(getModernFont(Font.BOLD, 22));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setIcon(createVectorIcon(IconType.SUBJECT, 24, PRIMARY_COLOR));
        titleLabel.setIconTextGap(12);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        filterPanel.setOpaque(false);

        JLabel niveauLabel = new JLabel("Filtrer par Niveau:");
        niveauLabel.setFont(getModernFont(Font.BOLD, 14));
        niveauLabel.setForeground(TEXT_PRIMARY);
        niveauLabel.setIcon(createVectorIcon(IconType.LEVEL, 16, ACCENT_BLUE));
        niveauLabel.setIconTextGap(8);

        niveauComboBox = createUltraModernAcademicLevelComboBox();
        niveauComboBox.setPreferredSize(new Dimension(220, 40));
        niveauComboBox.addActionListener(_ -> loadMatieres());

        filterPanel.add(niveauLabel);
        filterPanel.add(niveauComboBox);

        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(filterPanel, BorderLayout.SOUTH);

        addMatiereButton = createUltraModernButton("Ajouter Matière", IconType.PLUS, PRIMARY_COLOR, true);
        addMatiereButton.addActionListener(_ -> addMatiere());

        header.add(leftPanel, BorderLayout.WEST);
        header.add(addMatiereButton, BorderLayout.EAST);

        return header;
    }

    private JComboBox<AcademicLevel> createUltraModernAcademicLevelComboBox() {
        JComboBox<AcademicLevel> comboBox = new JComboBox<AcademicLevel>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre plus prononcée
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(248, 250, 252)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        comboBox.setFont(getModernFont(Font.PLAIN, 14));
        comboBox.setBackground(CARD_COLOR);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // UI personnalisée similaire à la ComboBox des mentions
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
                        g2d.setColor(ACCENT_BLUE);
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

        // Renderer personnalisé pour afficher les niveaux académiques
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                setFont(getModernFont(Font.PLAIN, 14));
                setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
                
                if (isSelected) {
                    setBackground(ACCENT_BLUE);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(CARD_COLOR);
                    setForeground(TEXT_PRIMARY);
                }
                
                return this;
            }
        });

        return comboBox;
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

            // Charger les niveaux académiques avec système de semestres
            niveauComboBox.removeAllItems();
            niveauComboBox.addItem(new AcademicLevel("0", "TOUS LES NIVEAUX", "", new String[]{}));
            
            if (allNiveaux.isEmpty()) {
                addMatiereButton.setEnabled(false);
            } else {
                // Convertir les niveaux en niveaux académiques avec semestres
                for (Niveau niveau : allNiveaux) {
                    AcademicLevel academicLevel = convertToAcademicLevel(niveau);
                    niveauComboBox.addItem(academicLevel);
                }
                niveauComboBox.setSelectedIndex(0);
                addMatiereButton.setEnabled(true);
            }
        } catch (ApiException ex) {
            showErrorNotification("Erreur lors du chargement des niveaux: " + ex.getMessage());
        }
    }

    private AcademicLevel convertToAcademicLevel(Niveau niveau) {
        String nom = niveau.getNom().toUpperCase();
        String[] semesters;
        String displayName;
        
        // Système de conversion des niveaux en format académique
        switch (nom) {
            case "L1":
                semesters = new String[]{"S1", "S2"};
                displayName = "L1 (S1-S2)";
                break;
            case "L2":
                semesters = new String[]{"S3", "S4"};
                displayName = "L2 (S3-S4)";
                break;
            case "L3":
                semesters = new String[]{"S5", "S6"};
                displayName = "L3 (S5-S6)";
                break;
            case "M1":
                semesters = new String[]{"S7", "S8"};
                displayName = "M1 (S7-S8)";
                break;
            case "M2":
                semesters = new String[]{"S9", "S10"};
                displayName = "M2 (S9-S10)";
                break;
            default:
                // Pour les niveaux personnalisés, essayer de détecter le pattern
                if (nom.matches(".*[1-5].*")) {
                    int levelNum = extractLevelNumber(nom);
                    if (levelNum > 0 && levelNum <= 5) {
                        int s1 = (levelNum - 1) * 2 + 1;
                        int s2 = s1 + 1;
                        semesters = new String[]{"S" + s1, "S" + s2};
                        displayName = nom + " (S" + s1 + "-S" + s2 + ")";
                    } else {
                        semesters = new String[]{"S1", "S2"};
                        displayName = nom + " (S1-S2)";
                    }
                } else {
                    semesters = new String[]{"S1", "S2"};
                    displayName = nom + " (S1-S2)";
                }
                break;
        }
        
        return new AcademicLevel(niveau.getId(), displayName, niveau.getDescription(), semesters);
    }

    private int extractLevelNumber(String nom) {
        try {
            // Extraire le numéro du niveau (L1, L2, M1, etc.)
            if (nom.startsWith("L") && nom.length() >= 2) {
                return Integer.parseInt(nom.substring(1, 2));
            } else if (nom.startsWith("M") && nom.length() >= 2) {
                return Integer.parseInt(nom.substring(1, 2)) + 3; // M1 = niveau 4, M2 = niveau 5
            }
            return 1; // Par défaut
        } catch (NumberFormatException e) {
            return 1; // Par défaut
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
            AcademicLevel selectedLevel = (AcademicLevel) niveauComboBox.getSelectedItem();
            if (selectedLevel == null) {
                matiereTableModel.setRowCount(0);
                return;
            }

            if (selectedLevel.getId().equals("0")) {
                allMatieres = parameterService.getAllMatieres();
            } else {
                allMatieres = parameterService.getAllMatieresByNiveau(selectedLevel.getId());
            }

            matiereTableModel.setRowCount(0);
            Map<String, String> niveauMap = allNiveaux.stream()
                    .collect(Collectors.toMap(Niveau::getId, niveau -> convertToAcademicLevel(niveau).getDisplayName()));

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

    // Opérations CRUD avec dialogs ultra-modernes
    private void addMention() {
        showUltraModernFormDialog("Ajouter une Mention", null, null, (nom, description, _) -> {
            if (validateBasicFields(nom, description)) {
                Mention mention = new Mention();
                mention.setNom(nom);
                mention.setDescription(description);
                parameterService.addMention(mention);
                loadMentions();
                showSuccessNotification("Mention ajoutée avec succès!");
            }
        }, null, null, IconType.CROWN);
    }

    private void editMention() {
        int selectedRow = getSelectedRow(mentionTableModel);
        if (selectedRow >= 0 && selectedRow < allMentions.size()) {
            Mention mention = allMentions.get(selectedRow);
            showUltraModernFormDialog("Modifier la Mention", mention.getNom(), mention.getDescription(), (nom, description, _) -> {
                if (validateBasicFields(nom, description)) {
                    mention.setNom(nom);
                    mention.setDescription(description);
                    parameterService.updateMention(mention.getId(), mention);
                    loadMentions();
                    showSuccessNotification("Mention modifiée avec succès!");
                }
            }, null, null, IconType.EDIT);
        }
    }

    private void addNiveau() {
        showUltraModernFormDialog("Ajouter un Niveau", null, null, (nom, description, _) -> {
            if (validateBasicFields(nom, description)) {
                Niveau niveau = new Niveau();
                niveau.setNom(nom);
                niveau.setDescription(description);
                parameterService.addNiveau(niveau);
                loadNiveaux();
                showSuccessNotification("Niveau ajouté avec succès!");
            }
        }, null, null, IconType.LEVEL);
    }

    private void editNiveau() {
        int selectedRow = getSelectedRow(niveauTableModel);
        if (selectedRow >= 0 && selectedRow < allNiveaux.size()) {
            Niveau niveau = allNiveaux.get(selectedRow);
            showUltraModernFormDialog("Modifier le Niveau", niveau.getNom(), niveau.getDescription(), (nom, description, _) -> {
                if (validateBasicFields(nom, description)) {
                    niveau.setNom(nom);
                    niveau.setDescription(description);
                    parameterService.updateNiveau(niveau.getId(), niveau);
                    loadNiveaux();
                    showSuccessNotification("Niveau modifié avec succès!");
                }
            }, null, null, IconType.EDIT);
        }
    }

    private void addParcours() {
        if (allMentions.isEmpty()) {
            showErrorNotification("Aucune mention disponible. Veuillez d'abord ajouter une mention.");
            return;
        }

        Mention selectedMention = (Mention) mentionComboBox.getSelectedItem();
        showUltraModernFormDialog("Ajouter un Parcours", null, null, (nom, description, refComboBox) -> {
            if (validateBasicFields(nom, description) && validateReferenceSelection(refComboBox, "Mention")) {
                Parcours parcours = new Parcours();
                parcours.setNom(nom);
                parcours.setDescription(description);
                parcours.setMentionId(getSelectedReferenceId(allMentions, refComboBox));
                parameterService.addParcours(parcours);
                loadParcours();
                showSuccessNotification("Parcours ajouté avec succès!");
            }
        }, allMentions, selectedMention, IconType.COURSE);
    }

    private void editParcours() {
        int selectedRow = getSelectedRow(parcoursTableModel);
        if (selectedRow >= 0 && selectedRow < allParcours.size()) {
            Parcours parcours = allParcours.get(selectedRow);
            Mention currentMention = allMentions.stream()
                    .filter(m -> m.getId().equals(parcours.getMentionId()))
                    .findFirst().orElse(null);

            showUltraModernFormDialog("Modifier le Parcours", parcours.getNom(), parcours.getDescription(), (nom, description, refComboBox) -> {
                if (validateBasicFields(nom, description) && validateReferenceSelection(refComboBox, "Mention")) {
                    parcours.setNom(nom);
                    parcours.setDescription(description);
                    parcours.setMentionId(getSelectedReferenceId(allMentions, refComboBox));
                    parameterService.updateParcours(parcours.getId(), parcours);
                    loadParcours();
                    showSuccessNotification("Parcours modifié avec succès!");
                }
            }, allMentions, currentMention, IconType.EDIT);
        }
    }

    private void addMatiere() {
        if (allNiveaux.isEmpty()) {
            showErrorNotification("Aucun niveau disponible. Veuillez d'abord ajouter un niveau.");
            return;
        }
        showUltraModernMatiereFormDialog("Ajouter une Matière", null, false);
    }

    private void editMatiere() {
        int selectedRow = getSelectedRow(matiereTableModel);
        if (selectedRow >= 0 && selectedRow < allMatieres.size()) {
            Matiere matiere = allMatieres.get(selectedRow);
            showUltraModernMatiereFormDialog("Modifier la Matière", matiere, true);
        }
    }

    // Classe pour une boîte de dialogue ultra-moderne
    private class UltraModernDialog extends JDialog {
        public UltraModernDialog(Frame parent, String title) {
            super(parent, title, true);
            setUndecorated(true);
            setBackground(new Color(0, 0, 0, 0));
            
            getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                new UltraModernBorder(new Color(79, 70, 229, 100))
            ));
        }

        @Override
        public void paint(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Ombre plus prononcée
            g2d.setColor(new Color(0, 0, 0, 40));
            g2d.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 25, 25);
            
            // Gradient background
            GradientPaint gradient = new GradientPaint(
                0, 0, CARD_COLOR,
                0, getHeight(), new Color(253, 254, 255)
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth() - 15, getHeight() - 15, 25, 25);
            
            // Bordure brillante
            g2d.setColor(new Color(79, 70, 229, 150));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(0, 0, getWidth() - 16, getHeight() - 16, 25, 25);
            
            g2d.dispose();
            super.paint(g);
        }
    }

    private void showUltraModernMatiereFormDialog(String title, Matiere matiere, boolean isEdit) {
        currentDialog = new UltraModernDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            title
        );
        currentDialog.setSize(650, 750);
        currentDialog.setLocationRelativeTo(this);

        JPanel mainPanel = createUltraModernDialogContent(title, matiere, isEdit);
        currentDialog.add(mainPanel);
        currentDialog.setVisible(true);
    }

    private JPanel createUltraModernDialogContent(String title, Matiere matiere, boolean isEdit) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CARD_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        // En-tête ultra-moderne avec icône
        JPanel headerPanel = createUltraModernDialogHeader(
            title,
            "Veuillez remplir tous les champs requis avec précision",
            isEdit ? IconType.EDIT : IconType.PLUS
        );
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Formulaire
        JPanel formPanel = createUltraModernMatiereForm(matiere, isEdit);
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(null);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        formScrollPane.setOpaque(false);
        formScrollPane.getViewport().setOpaque(false);
        mainPanel.add(formScrollPane, BorderLayout.CENTER);

        // Boutons
        JPanel buttonPanel = createUltraModernDialogButtons(matiere, isEdit);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel createUltraModernDialogHeader(String titleText, String subtitleText, IconType iconType) {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setBackground(CARD_COLOR);

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(CARD_COLOR);

        JLabel iconLabel = new JLabel(createVectorIcon(iconType, 36, PRIMARY_COLOR));
        leftPanel.add(iconLabel);
        leftPanel.add(Box.createHorizontalStrut(18));

        JLabel titleLabel = new JLabel(titleText);
        titleLabel.setFont(getModernFont(Font.BOLD, 30));
        titleLabel.setForeground(TEXT_PRIMARY);
        leftPanel.add(titleLabel);

        // Bouton de fermeture amélioré
        JButton closeButton = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(239, 68, 68, 100));
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                }
                
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        closeButton.setIcon(createVectorIcon(IconType.CANCEL, 18, TEXT_SECONDARY));
        closeButton.setContentAreaFilled(false);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setPreferredSize(new Dimension(36, 36));
        closeButton.addActionListener(_ -> currentDialog.dispose());

        titleRow.add(leftPanel, BorderLayout.WEST);
        titleRow.add(closeButton, BorderLayout.EAST);

        JLabel subtitleLabel = new JLabel(subtitleText);
        subtitleLabel.setFont(getModernFont(Font.PLAIN, 15));
        subtitleLabel.setForeground(TEXT_SECONDARY);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(15, 54, 0, 0));

        headerPanel.add(titleRow);
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createUltraModernMatiereForm(Matiere matiere, boolean isEdit) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 0, 18, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Nom de la matière
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nomLabel = createUltraModernFormLabel("Nom de la Matière *", IconType.SUBJECT);
        formPanel.add(nomLabel, gbc);

        gbc.gridy = 1;
        JTextField nomField = createUltraModernTextField(matiere != null ? matiere.getNom() : "");
        nomField.setPreferredSize(new Dimension(500, 50));
        formPanel.add(nomField, gbc);

        // Catégorie
        gbc.gridy = 2;
        JLabel categorieLabel = createUltraModernFormLabel("Catégorie *", IconType.DIAMOND);
        formPanel.add(categorieLabel, gbc);

        gbc.gridy = 3;
        JTextField categorieField = createUltraModernTextField(matiere != null ? matiere.getCategorie() : "");
        categorieField.setPreferredSize(new Dimension(500, 50));
        formPanel.add(categorieField, gbc);

        // Coefficient
        gbc.gridy = 4;
        JLabel coefficientLabel = createUltraModernFormLabel("Coefficient *", IconType.STAR);
        formPanel.add(coefficientLabel, gbc);

        gbc.gridy = 5;
        JTextField coefficientField = createUltraModernTextField(matiere != null ? String.valueOf(matiere.getCoefficient()) : "");
        coefficientField.setPreferredSize(new Dimension(500, 50));
        coefficientField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateCoefficientField(coefficientField);
            }
        });
        formPanel.add(coefficientField, gbc);

        // Niveau académique
        gbc.gridy = 6;
        JLabel niveauLabel = createUltraModernFormLabel("Niveau Académique *", IconType.LEVEL);
        formPanel.add(niveauLabel, gbc);

        gbc.gridy = 7;
        JComboBox<AcademicLevel> niveauCombo = createUltraModernAcademicLevelComboBox();
        niveauCombo.setPreferredSize(new Dimension(500, 50));
        
        // Remplir avec tous les niveaux sauf "TOUS"
        niveauCombo.removeAllItems();
        for (Niveau niveau : allNiveaux) {
            AcademicLevel academicLevel = convertToAcademicLevel(niveau);
            niveauCombo.addItem(academicLevel);
        }
        
        if (isEdit && matiere != null && matiere.getNiveauId() != null) {
            for (int i = 0; i < niveauCombo.getItemCount(); i++) {
                AcademicLevel level = niveauCombo.getItemAt(i);
                if (level.getId().equals(matiere.getNiveauId())) {
                    niveauCombo.setSelectedItem(level);
                    break;
                }
            }
        }
        formPanel.add(niveauCombo, gbc);

        // Stocker les références pour la sauvegarde
        formPanel.putClientProperty("nomField", nomField);
        formPanel.putClientProperty("categorieField", categorieField);
        formPanel.putClientProperty("coefficientField", coefficientField);
        formPanel.putClientProperty("niveauCombo", niveauCombo);

        return formPanel;
    }

    private JLabel createUltraModernFormLabel(String text, IconType iconType) {
        JLabel label = new JLabel(text);
        label.setFont(getModernFont(Font.BOLD, 16));
        label.setForeground(TEXT_PRIMARY);
        label.setIcon(createVectorIcon(iconType, 18, PRIMARY_COLOR));
        label.setIconTextGap(12);
        return label;
    }

    private JTextField createUltraModernTextField(String text) {
        JTextField textField = new JTextField(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre plus prononcée
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, getBackground(),
                    0, getHeight(), new Color(248, 250, 252)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        textField.setFont(getModernFont(Font.PLAIN, 15));
        textField.setBackground(new Color(248, 250, 252));
        textField.setForeground(TEXT_PRIMARY);
        textField.setBorder(BorderFactory.createCompoundBorder(
            new UltraModernBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)));

        // Effet de focus amélioré
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    new UltraModernBorder(PRIMARY_COLOR),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    new UltraModernBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            }
        });

        return textField;
    }

    private JPanel createUltraModernDialogButtons(Matiere matiere, boolean isEdit) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(35, 0, 0, 0));

        JButton saveButton = createUltraModernButton("Enregistrer", IconType.SAVE, PRIMARY_COLOR, true);
        JButton cancelButton = createUltraModernButton("Annuler", IconType.CANCEL, ERROR_COLOR, true);

        saveButton.addActionListener(_ -> {
            if (validateAndSaveMatiere(matiere, isEdit)) {
                currentDialog.dispose();
            }
        });

        cancelButton.addActionListener(_ -> {
            if (currentDialog != null) {
                currentDialog.dispose();
            }
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        return buttonPanel;
    }

    @SuppressWarnings("unchecked")
    private boolean validateAndSaveMatiere(Matiere matiere, boolean isEdit) {
        try {
            // Récupérer les composants du formulaire
            Component[] components = currentDialog.getContentPane().getComponents();
            JPanel mainPanel = (JPanel) components[0];
            JScrollPane scrollPane = (JScrollPane) mainPanel.getComponent(1);
            JPanel formPanel = (JPanel) scrollPane.getViewport().getView();
            
            JTextField nomField = (JTextField) formPanel.getClientProperty("nomField");
            JTextField categorieField = (JTextField) formPanel.getClientProperty("categorieField");
            JTextField coefficientField = (JTextField) formPanel.getClientProperty("coefficientField");
            JComboBox<AcademicLevel> niveauCombo = (JComboBox<AcademicLevel>) formPanel.getClientProperty("niveauCombo");

            String nom = nomField.getText().trim();
            String categorie = categorieField.getText().trim();
            String coefficientText = coefficientField.getText().trim();
            AcademicLevel selectedLevel = (AcademicLevel) niveauCombo.getSelectedItem();

            if (nom.isEmpty() || categorie.isEmpty() || coefficientText.isEmpty() || selectedLevel == null) {
                showErrorNotification("Tous les champs marqués d'un * sont obligatoires.");
                return false;
            }

            int coefficient;
            try {
                coefficient = Integer.parseInt(coefficientText);
                if (coefficient <= 0) {
                    showErrorNotification("Le coefficient doit être un nombre entier positif.");
                    coefficientField.requestFocus();
                    return false;
                }
            } catch (NumberFormatException ex) {
                showErrorNotification("Veuillez saisir un coefficient valide (nombre entier).");
                coefficientField.requestFocus();
                return false;
            }

            Matiere newMatiere = new Matiere();
            newMatiere.setNom(nom);
            newMatiere.setCategorie(categorie);
            newMatiere.setCoefficient(coefficient);
            newMatiere.setNiveauId(selectedLevel.getId());

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
            showErrorNotification("Erreur lors de l'enregistrement: " + ex.getMessage());
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
                        new UltraModernBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)
                    ));
                    field.setToolTipText("Le coefficient doit être positif");
                } else {
                    field.setBorder(BorderFactory.createCompoundBorder(
                        new UltraModernBorder(SUCCESS_COLOR),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)
                    ));
                    field.setToolTipText("Coefficient valide ✓");
                }
            } catch (NumberFormatException e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new UltraModernBorder(ERROR_COLOR),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)
                ));
                field.setToolTipText("Veuillez saisir un nombre entier");
            }
        } else {
            field.setBorder(BorderFactory.createCompoundBorder(
                new UltraModernBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
            ));
            field.setToolTipText("");
        }
    }

    private void showUltraModernFormDialog(String title, String currentNom, String currentDescription,
            FormSaveAction saveAction, List<Mention> referenceList, Mention defaultMention, IconType iconType) {

        currentDialog = new UltraModernDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            title
        );
        currentDialog.setSize(600, 650);
        currentDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CARD_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        JPanel headerPanel = createUltraModernDialogHeader(title, "Veuillez remplir tous les champs requis", iconType);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 0, 18, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nomLabel = createUltraModernFormLabel("Nom *", IconType.EDIT);
        formPanel.add(nomLabel, gbc);

        gbc.gridy = 1;
        JTextField nomField = createUltraModernTextField(currentNom != null ? currentNom : "");
        nomField.setPreferredSize(new Dimension(450, 50));
        formPanel.add(nomField, gbc);

        gbc.gridy = 2;
        JLabel descriptionLabel = createUltraModernFormLabel("Description", IconType.SUBJECT);
        formPanel.add(descriptionLabel, gbc);

        gbc.gridy = 3;
        JTextField descriptionField = createUltraModernTextField(currentDescription != null ? currentDescription : "");
        descriptionField.setPreferredSize(new Dimension(450, 50));
        formPanel.add(descriptionField, gbc);

        JComboBox<Mention> refComboBox = null;
        if (referenceList != null && !referenceList.isEmpty()) {
            gbc.gridy = 4;
            JLabel refLabel = createUltraModernFormLabel("Mention *", IconType.CROWN);
            formPanel.add(refLabel, gbc);

            gbc.gridy = 5;
            refComboBox = createUltraModernMentionComboBox();
            refComboBox.setPreferredSize(new Dimension(450, 50));
            refComboBox.removeAllItems();
            for (Mention mention : referenceList) {
                refComboBox.addItem(mention);
            }
            if (defaultMention != null) {
                refComboBox.setSelectedItem(defaultMention);
            }
            formPanel.add(refComboBox, gbc);
        }

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(35, 0, 0, 0));

        JButton saveButton = createUltraModernButton("Enregistrer", IconType.SAVE, PRIMARY_COLOR, true);
        JButton cancelButton = createUltraModernButton("Annuler", IconType.CANCEL, ERROR_COLOR, true);

        JComboBox<Mention> finalRefComboBox = refComboBox;
        saveButton.addActionListener(_ -> {
            String nomText = nomField.getText().trim();
            String descriptionText = descriptionField.getText().trim();

            if (validateBasicFields(nomText, descriptionText) &&
                (finalRefComboBox == null || validateReferenceSelection(finalRefComboBox, "Mention"))) {
                try {
                    saveAction.save(nomText, descriptionText, finalRefComboBox);
                    currentDialog.dispose();
                } catch (ApiException ex) {
                    showErrorNotification("Erreur lors de l'enregistrement: " + ex.getMessage());
                }
            }
        });

        cancelButton.addActionListener(_ -> currentDialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        currentDialog.add(mainPanel);
        currentDialog.setVisible(true);
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
                    showUltraModernConfirmDialog(
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
                    showUltraModernConfirmDialog(
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
                    showUltraModernConfirmDialog(
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
                    showUltraModernConfirmDialog(
                        "Confirmer la suppression",
                        "Êtes-vous sûr de vouloir supprimer \"" + itemName + "\" ?",
                        "Cette action est irréversible.",
                        () -> performDelete(entityName, matiereId)
                    );
                }
                break;
        }
    }

    private void showUltraModernConfirmDialog(String title, String message, String subtitle, Runnable onConfirm) {
        JDialog dialog = new UltraModernDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            title
        );
        dialog.setSize(480, 320);
        dialog.setLocationRelativeTo(this);

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
        JLabel messageLabel = new JLabel("<html><center>" + message + "<br/>" + subtitle + "</center></html>");
        messageLabel.setFont(getModernFont(Font.PLAIN, 16));
        messageLabel.setForeground(TEXT_SECONDARY);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Boutons personnalisés
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonPanel.setBackground(CARD_COLOR);

        JButton yesButton = createUltraModernButton("Oui, supprimer", IconType.DELETE, ERROR_COLOR, true);
        JButton noButton = createUltraModernButton("Annuler", IconType.CANCEL, new Color(107, 114, 128), true);

        yesButton.addActionListener(_ -> {
            dialog.dispose();
            onConfirm.run();
        });

        noButton.addActionListener(_ -> dialog.dispose());

        buttonPanel.add(noButton);
        buttonPanel.add(yesButton);

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(messageLabel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
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

    // Classes pour les renderers et editors ultra-modernes
    private class UltraModernActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton;
        private JButton deleteButton;

        public UltraModernActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 8));
            setOpaque(false);

            editButton = createUltraActionButton(IconType.EDIT, WARNING_COLOR);
            deleteButton = createUltraActionButton(IconType.DELETE, ERROR_COLOR);

            add(editButton);
            add(deleteButton);
        }

        private JButton createUltraActionButton(IconType iconType, Color color) {
            JButton button = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Ombre
                    g2d.setColor(new Color(0, 0, 0, 20));
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                    
                    // Gradient background
                    if (getModel().isPressed()) {
                        g2d.setColor(color.darker());
                    } else if (getModel().isRollover()) {
                        GradientPaint gradient = new GradientPaint(
                            0, 0, color.brighter(),
                            0, getHeight(), color
                        );
                        g2d.setPaint(gradient);
                    } else {
                        GradientPaint gradient = new GradientPaint(
                            0, 0, color,
                            0, getHeight(), color.darker()
                        );
                        g2d.setPaint(gradient);
                    }
                    
                    g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };

            button.setIcon(createVectorIcon(iconType, 14, Color.WHITE));
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(36, 32));
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

    private class UltraModernActionButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton deleteButton;
        private final Runnable editAction;
        private final Runnable deleteAction;

        public UltraModernActionButtonEditor(Runnable editAction, Runnable deleteAction) {
            super(new JTextField());
            this.editAction = editAction;
            this.deleteAction = deleteAction;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
            panel.setOpaque(false);

            editButton = createUltraActionButton(IconType.EDIT, WARNING_COLOR);
            deleteButton = createUltraActionButton(IconType.DELETE, ERROR_COLOR);

            editButton.addActionListener(_ -> {
                fireEditingStopped();
                editAction.run();
            });

            deleteButton.addActionListener(_ -> {
                fireEditingStopped();
                deleteAction.run();
            });

            panel.add(editButton);
            panel.add(deleteButton);
        }

        private JButton createUltraActionButton(IconType iconType, Color color) {
            JButton button = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Ombre
                    g2d.setColor(new Color(0, 0, 0, 20));
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                    
                    // Gradient background
                    if (getModel().isPressed()) {
                        g2d.setColor(color.darker());
                    } else if (getModel().isRollover()) {
                        GradientPaint gradient = new GradientPaint(
                            0, 0, color.brighter(),
                            0, getHeight(), color
                        );
                        g2d.setPaint(gradient);
                    } else {
                        GradientPaint gradient = new GradientPaint(
                            0, 0, color,
                            0, getHeight(), color.darker()
                        );
                        g2d.setPaint(gradient);
                    }
                    
                    g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 10, 10);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };

            button.setIcon(createVectorIcon(iconType, 14, Color.WHITE));
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(36, 32));
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

    // Classe de bordure ultra-moderne
    private static class UltraModernBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;

        public UltraModernBorder() {
            this(BORDER_COLOR, 1);
        }

        public UltraModernBorder(Color color) {
            this(color, 2);
        }

        public UltraModernBorder(Color color, int thickness) {
            this.color = color;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2d.drawRoundRect(x, y, width - 1, height - 1, 12, 12);
            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 2, thickness + 2, thickness + 2, thickness + 2);
        }
    }

    // Notifications ultra-modernes
    private void showSuccessNotification(String message) {
        createUltraModernToastNotification(message, SUCCESS_COLOR, IconType.SAVE);
    }

    private void showErrorNotification(String message) {
        createUltraModernToastNotification(message, ERROR_COLOR, IconType.CANCEL);
    }

    private void createUltraModernToastNotification(String message, Color bgColor, IconType iconType) {
        JWindow toast = new JWindow();
        toast.setAlwaysOnTop(true);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre plus prononcée
                g2d.setColor(new Color(0, 0, 0, 120));
                g2d.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 18, 18);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, bgColor,
                    0, getHeight(), bgColor.darker()
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 18, 18);
                
                // Bordure brillante
                g2d.setColor(new Color(255, 255, 255, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 18, 18);
                g2d.dispose();
            }
        };

        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(18, 25, 18, 25));

        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        messagePanel.setOpaque(false);
        
        JLabel iconLabel = new JLabel(createVectorIcon(iconType, 20, Color.WHITE));
        messagePanel.add(iconLabel);
        
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(getModernFont(Font.BOLD, 15));
        messageLabel.setForeground(Color.WHITE);
        messagePanel.add(messageLabel);

        panel.add(messagePanel, BorderLayout.CENTER);
        toast.add(panel);
        toast.pack();

        // Position at top-right of screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        toast.setLocation(screenSize.width - toast.getWidth() - 40, 40);
        toast.setVisible(true);

        // Animation de disparition après 4 secondes
        Timer timer = new Timer(4000, _ -> {
            toast.setVisible(false);
            toast.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    @FunctionalInterface
    private interface FormSaveAction {
        void save(String nom, String description, JComboBox<?> refComboBox) throws ApiException;
    }
}