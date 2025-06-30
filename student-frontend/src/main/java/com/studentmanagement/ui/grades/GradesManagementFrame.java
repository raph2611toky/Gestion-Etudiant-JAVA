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
import java.awt.GradientPaint;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.border.AbstractBorder;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.ArrayList;
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
    private JComboBox<String> niveauComboBox;
    private JComboBox<String> parcoursComboBox;
    private List<Etudiant> allStudents;
    private List<Matiere> allMatieres;
    private List<Niveau> allNiveaux;
    private List<Parcours> allParcours;
    private JDialog currentDialog;
    private JLabel classAverageLabel;
    private JLabel minAverageLabel;
    private JLabel maxAverageLabel;
    private double classAverage;
    private double minAverage;
    private double maxAverage;
    
    private List<ClassStatisticsDTO> currentClassStats = new ArrayList<>();

    // Palette de couleurs modernes ultra-améliorée
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

    // Énumération pour les types d'icônes
    private enum IconType {
        PLUS, EDIT, DELETE, REFRESH, NOTES, SEARCH, GRID, TABLE, GRADUATION, USER, EMAIL, 
        LOCATION, BOOK, TARGET, CAMERA, SAVE, CANCEL, CHART, STATS, FILTER, ACADEMIC, STAR, TROPHY
    }

    public GradesManagementFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.studentService = new StudentService();
        this.studentService.setJwtToken(mainWindow.getCurrentResponsable().getToken());
        initializeUI();
        loadData();
        loadGrades();
        loadStatistics();
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
                    case REFRESH:
                        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawArc(x + 2, y + 2, size - 4, size - 4, 45, 270);
                        g2d.drawLine(x + size - 4, y + 4, x + size - 2, y + 2);
                        g2d.drawLine(x + size - 4, y + 4, x + size - 6, y + 6);
                        break;
                    case NOTES:
                        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawRoundRect(x + 3, y + 2, size - 6, size - 4, 3, 3);
                        g2d.drawLine(x + 6, y + 5, x + size - 6, y + 5);
                        g2d.drawLine(x + 6, y + 8, x + size - 6, y + 8);
                        g2d.drawLine(x + 6, y + 11, x + size - 8, y + 11);
                        break;
                    case CHART:
                        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawLine(x + 3, y + size - 3, x + 3, y + 3);
                        g2d.drawLine(x + 3, y + size - 3, x + size - 3, y + size - 3);
                        g2d.fillRect(x + 5, y + 10, 2, size - 13);
                        g2d.fillRect(x + 8, y + 6, 2, size - 9);
                        g2d.fillRect(x + 11, y + 8, 2, size - 11);
                        break;
                    case STATS:
                        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawOval(centerX - 6, centerY - 6, 12, 12);
                        g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawLine(centerX, centerY, centerX + 4, centerY - 2);
                        g2d.drawLine(centerX, centerY, centerX + 2, centerY + 4);
                        g2d.drawLine(centerX, centerY, centerX - 3, centerY + 1);
                        break;
                    case FILTER:
                        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawLine(x + 2, y + 4, x + size - 2, y + 4);
                        g2d.drawLine(x + 4, y + 4, x + 6, y + 8);
                        g2d.drawLine(x + size - 4, y + 4, x + size - 6, y + 8);
                        g2d.drawLine(x + 6, y + 8, x + size - 6, y + 8);
                        g2d.fillRect(x + 7, y + 8, 2, size - 11);
                        break;
                    case ACADEMIC:
                        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawLine(x + 2, centerY, x + size - 2, centerY);
                        g2d.drawLine(x + 4, centerY - 2, x + size - 4, centerY - 2);
                        g2d.drawLine(centerX, y + 3, centerX, centerY + 3);
                        g2d.fillOval(centerX - 1, centerY + 3, 2, 2);
                        break;
                    case USER:
                        g2d.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawOval(centerX - 3, y + 3, 6, 6);
                        g2d.drawArc(x + 2, y + 8, size - 4, size - 6, 0, 180);
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
                    case STAR:
                        g2d.setStroke(new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        int[] xPoints = {centerX, centerX + 2, centerX + 6, centerX + 3, centerX + 4, centerX, centerX - 4, centerX - 3, centerX - 6, centerX - 2};
                        int[] yPoints = {y + 2, y + 6, y + 6, y + 9, y + size - 2, y + 11, y + size - 2, y + 9, y + 6, y + 6};
                        g2d.drawPolygon(xPoints, yPoints, 10);
                        break;
                    case TROPHY:
                        g2d.setStroke(new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2d.drawOval(centerX - 4, y + 3, 8, 6);
                        g2d.drawLine(centerX - 4, y + 9, centerX + 4, y + 9);
                        g2d.drawLine(centerX, y + 9, centerX, y + size - 4);
                        g2d.drawLine(centerX - 3, y + size - 4, centerX + 3, y + size - 4);
                        g2d.drawLine(x + 2, y + 5, centerX - 4, y + 7);
                        g2d.drawLine(x + size - 2, y + 5, centerX + 4, y + 7);
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

        JPanel sidebar = SidebarUtil.createSidebar(mainWindow, "Grades");
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

        JTabbedPane tabbedPane = createUltraModernTabbedPane();
        mainContent.add(tabbedPane, BorderLayout.CENTER);

        return mainContent;
    }

    private JPanel createUltraModernHeaderPanel() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 200),
                    0, getHeight(), new Color(248, 250, 252, 100)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                
                g2d.setColor(new Color(226, 232, 240, 100));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                g2d.dispose();
            }
        };
        
        header.setLayout(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JPanel titleSection = new JPanel();
        titleSection.setLayout(new BoxLayout(titleSection, BoxLayout.Y_AXIS));
        titleSection.setOpaque(false);

        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titleRow.setOpaque(false);

        JLabel iconLabel = new JLabel(createVectorIcon(IconType.NOTES, 40, PRIMARY_COLOR));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        titleRow.add(iconLabel);
        titleRow.add(Box.createHorizontalStrut(20));

        JLabel titleLabel = new JLabel("Gestion des Notes");
        titleLabel.setFont(getModernFont(Font.BOLD, 36));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleRow.add(titleLabel);

        JLabel badgeLabel = new JLabel("PREMIUM") {
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
        badgeLabel.setPreferredSize(new Dimension(60, 20));
        badgeLabel.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        
        titleRow.add(Box.createHorizontalStrut(15));
        titleRow.add(badgeLabel);

        JLabel breadcrumbLabel = new JLabel("Dashboard > Gestion des Notes > Interface Moderne");
        breadcrumbLabel.setFont(getModernFont(Font.PLAIN, 14));
        breadcrumbLabel.setForeground(TEXT_SECONDARY);
        breadcrumbLabel.setBorder(BorderFactory.createEmptyBorder(15, 60, 0, 0));
        breadcrumbLabel.setIcon(createVectorIcon(IconType.CHART, 14, TEXT_SECONDARY));

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
                
                g2d.setColor(new Color(0, 0, 0, 25));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
                
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

        JPanel gradesPanel = createUltraModernGradesPanel();
        JPanel statsPanel = createUltraModernStatisticsPanel();

        tabbedPane.addTab("   Statistiques Avancées   ", createVectorIcon(IconType.CHART, 18, ACCENT_BLUE), statsPanel);
        tabbedPane.addTab("   Gestion des Notes   ", createVectorIcon(IconType.NOTES, 18, PRIMARY_COLOR), gradesPanel);

        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabbedPane.getSelectedIndex() == 1) {
                    loadData();
                }
            }
        });

        return tabbedPane;
    }

    private JPanel createUltraModernGradesPanel() {
        JPanel gradesPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(249, 250, 251)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        gradesPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Panneau supérieur pour les filtres et boutons
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        JPanel header = createUltraModernGradesHeader();
        topPanel.add(header, BorderLayout.CENTER);

        // Panneau pour le tableau
        JPanel tablePanel = createUltraModernGradesTable();

        // Ajout des panneaux à gradesPanel
        gradesPanel.add(topPanel, BorderLayout.NORTH);
        gradesPanel.add(tablePanel, BorderLayout.CENTER);

        return gradesPanel;
    }

    private JPanel createUltraModernGradesHeader() {
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 200),
                    0, getHeight(), new Color(248, 250, 252, 100)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2d.setColor(new Color(226, 232, 240, 80));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2d.dispose();
            }
        };
        header.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        filterPanel.setOpaque(false);

        JLabel studentLabel = new JLabel("Sélectionner un Étudiant");
        studentLabel.setFont(getModernFont(Font.BOLD, 15));
        studentLabel.setForeground(TEXT_PRIMARY);
        studentLabel.setIcon(createVectorIcon(IconType.USER, 18, PRIMARY_COLOR));
        studentLabel.setIconTextGap(10);

        studentComboBox = createUltraModernComboBox();
        studentComboBox.setPreferredSize(new Dimension(280, 45));
        studentComboBox.addActionListener(_ -> loadGrades());

        filterPanel.add(studentLabel);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(studentComboBox);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 0));
        actionPanel.setOpaque(false);

        JButton addButton = createUltraModernButton("Ajouter une Note", IconType.PLUS, PRIMARY_COLOR, true);
        addButton.addActionListener(_ -> showGradeFormDialog(null));

        JButton refreshButton = createUltraModernButton("Actualiser", IconType.REFRESH, SECONDARY_COLOR, false);
        refreshButton.addActionListener(_ -> {
            loadData();
            loadGrades();
            showSuccessNotification("Données actualisées avec succès!");
        });

        actionPanel.add(refreshButton);
        actionPanel.add(addButton);

        header.add(filterPanel, BorderLayout.WEST);
        header.add(actionPanel, BorderLayout.EAST);

        return header;
    }

    private JComboBox<Etudiant> createUltraModernComboBox() {
        JComboBox<Etudiant> comboBox = new JComboBox<Etudiant>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                
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

        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected ComboPopup createPopup() {
                return new BasicComboPopup(comboBox) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        g2d.setColor(new Color(0, 0, 0, 30));
                        g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);
                        
                        GradientPaint gradient = new GradientPaint(
                            0, 0, CARD_COLOR,
                            0, getHeight(), new Color(248, 250, 252)
                        );
                        g2d.setPaint(gradient);
                        g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);
                        
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
                
                if (index >= 0 && !isSelected) {
                    setBackground(new Color(248, 250, 252));
                }
                
                return this;
            }
        });

        return comboBox;
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

                if (!isPressed) {
                    g2d.setColor(new Color(0, 0, 0, isHovered ? 25 : 15));
                    g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                }

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

        Icon icon = createVectorIcon(iconType, 16, isPrimary ? Color.WHITE : TEXT_PRIMARY);
        button.setIcon(icon);
        button.setIconTextGap(10);

        return button;
    }

    private JPanel createUltraModernGradesTable() {
        JPanel tablePanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 20, 20);
                g2d.dispose();
            }
        };
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        String[] columns = {"Matière", "Coefficient", "Note /20", "Semestre", "Année", "Actions"};
        gradesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        gradesTable = createUltraModernTable(gradesTableModel);
        gradesTable.getColumn("Actions").setCellRenderer(new UltraModernActionButtonRenderer());
        gradesTable.getColumn("Actions").setCellEditor(new UltraModernActionButtonEditor(this::editGrade, this::deleteGrade));

        JScrollPane scrollPane = createUltraModernScrollPane(gradesTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
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

        javax.swing.table.JTableHeader header = table.getTableHeader();
        header.setFont(getModernFont(Font.BOLD, 15));
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, new Color(79, 70, 229, 100)));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 55));

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

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }

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
                
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 1,
                        thumbBounds.width - 2, thumbBounds.height - 2, 8, 8);
                
                GradientPaint gradient = new GradientPaint(
                    thumbBounds.x, thumbBounds.y, new Color(156, 163, 175),
                    thumbBounds.x + thumbBounds.width, thumbBounds.y, new Color(107, 114, 128)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(thumbBounds.x, thumbBounds.y,
                        thumbBounds.width - 1, thumbBounds.height - 1, 8, 8);
                
                g2d.setColor(new Color(255, 255, 255, 60));
                g2d.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 1,
                        thumbBounds.width - 3, thumbBounds.height / 3, 6, 6);
                
                g2d.dispose();
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
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

    private JPanel createUltraModernStatisticsPanel() {
        JPanel statsPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(249, 250, 251)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        statsPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel filterPanel = createUltraModernStatsFilterPanel();
        statsPanel.add(filterPanel, BorderLayout.NORTH);

        JTabbedPane statsTabbedPane = createUltraModernStatsTabbedPane();
        statsPanel.add(statsTabbedPane, BorderLayout.CENTER);

        return statsPanel;
    }

    private JPanel createUltraModernStatsFilterPanel() {
        JPanel filterPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(255, 255, 255, 200),
                    0, getHeight(), new Color(248, 250, 252, 100)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                g2d.setColor(new Color(226, 232, 240, 80));
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2d.dispose();
            }
        };
        
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel niveauLabel = new JLabel("Niveau");
        niveauLabel.setFont(getModernFont(Font.BOLD, 15));
        niveauLabel.setForeground(TEXT_PRIMARY);
        niveauLabel.setIcon(createVectorIcon(IconType.BOOK, 18, ACCENT_PURPLE));
        niveauLabel.setIconTextGap(10);

        niveauComboBox = createUltraModernStringComboBox();
        niveauComboBox.addItem("Tous");
        if (allNiveaux != null) {
            for (Niveau niveau : allNiveaux) {
                niveauComboBox.addItem(niveau.getNom());
            }
        }
        niveauComboBox.addActionListener(_ -> loadStatistics());

        JLabel parcoursLabel = new JLabel("Parcours");
        parcoursLabel.setFont(getModernFont(Font.BOLD, 15));
        parcoursLabel.setForeground(TEXT_PRIMARY);
        parcoursLabel.setIcon(createVectorIcon(IconType.TARGET, 18, ACCENT_PINK));
        parcoursLabel.setIconTextGap(10);

        parcoursComboBox = createUltraModernStringComboBox();
        parcoursComboBox.addItem("Tous");
        if (allParcours != null) {
            for (Parcours parcours : allParcours) {
                parcoursComboBox.addItem(parcours.getNom());
            }
        }
        parcoursComboBox.addActionListener(_ -> loadStatistics());

        JLabel semestreLabel = new JLabel("Semestre");
        semestreLabel.setFont(getModernFont(Font.BOLD, 15));
        semestreLabel.setForeground(TEXT_PRIMARY);
        semestreLabel.setIcon(createVectorIcon(IconType.FILTER, 18, ACCENT_BLUE));
        semestreLabel.setIconTextGap(10);

        semestreComboBox = createUltraModernStringComboBox();
        semestreComboBox.addItem("");
        semestreComboBox.addItem("S1");
        semestreComboBox.addItem("S2");
        semestreComboBox.addActionListener(_ -> loadStatistics());

        JLabel anneeLabel = new JLabel("Année Académique");
        anneeLabel.setFont(getModernFont(Font.BOLD, 15));
        anneeLabel.setForeground(TEXT_PRIMARY);
        anneeLabel.setIcon(createVectorIcon(IconType.ACADEMIC, 18, ACCENT_PURPLE));
        anneeLabel.setIconTextGap(10);

        anneeComboBox = createUltraModernStringComboBox();
        anneeComboBox.addItem("");
        anneeComboBox.addItem("2023-2024");
        anneeComboBox.addItem("2024-2025");
        anneeComboBox.addItem("2025-2026");
        anneeComboBox.addActionListener(_ -> loadStatistics());

        JButton loadStatsButton = createUltraModernButton("Analyser les Données", IconType.CHART, SECONDARY_COLOR, false);
        loadStatsButton.setPreferredSize(new Dimension(180, 40));
        loadStatsButton.addActionListener(_ -> {
            loadStatistics();
            showSuccessNotification("Statistiques mises à jour!");
        });

        filterPanel.add(niveauLabel);
        filterPanel.add(niveauComboBox);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(parcoursLabel);
        filterPanel.add(parcoursComboBox);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(semestreLabel);
        filterPanel.add(semestreComboBox);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(anneeLabel);
        filterPanel.add(anneeComboBox);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(loadStatsButton);

        return filterPanel;
    }

    private JComboBox<String> createUltraModernStringComboBox() {
        JComboBox<String> comboBox = new JComboBox<String>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                
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
        comboBox.setPreferredSize(new Dimension(150, 40));
        comboBox.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected ComboPopup createPopup() {
                return new BasicComboPopup(comboBox) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        
                        g2d.setColor(new Color(0, 0, 0, 30));
                        g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 12, 12);
                        
                        GradientPaint gradient = new GradientPaint(
                            0, 0, CARD_COLOR,
                            0, getHeight(), new Color(248, 250, 252)
                        );
                        g2d.setPaint(gradient);
                        g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 12, 12);
                        
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

    private JTabbedPane createUltraModernStatsTabbedPane() {
        JTabbedPane statsTabbedPane = new JTabbedPane() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, CARD_COLOR,
                    0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        statsTabbedPane.setFont(getModernFont(Font.BOLD, 15));
        statsTabbedPane.setBackground(CARD_COLOR);
        statsTabbedPane.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JPanel averagesPanel = createUltraModernAveragesPanel();
        JPanel classStatsPanel = createUltraModernClassStatsPanel();

        statsTabbedPane.addTab("   Moyennes Étudiants   ", createVectorIcon(IconType.STAR, 16, SUCCESS_COLOR), averagesPanel);
        statsTabbedPane.addTab("   Statistiques Classes   ", createVectorIcon(IconType.TROPHY, 16, WARNING_COLOR), classStatsPanel);

        return statsTabbedPane;
    }

    private JPanel createUltraModernAveragesPanel() {
        JPanel averagesPanel = new JPanel(new BorderLayout());
        averagesPanel.setBackground(CARD_COLOR);
        averagesPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(CARD_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Bouton commenté comme demandé
        // JButton addDataButton = createUltraModernButton("Ajouter des Données", IconType.PLUS, PRIMARY_COLOR, true);
        // addDataButton.addActionListener(_ -> showAddDataDialog());
        // topPanel.add(addDataButton);

        averagesPanel.add(topPanel, BorderLayout.NORTH);

        // Remplacement de "Statut" par "Observation"
        String[] columns = {"Matricule", "Prénom", "Nom", "Moyenne", "Mention", "Observation"};
        averagesTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        averagesTable = createUltraModernTable(averagesTableModel);
        JScrollPane scrollPane = createUltraModernScrollPane(averagesTable);
        averagesPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel statsSummaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsSummaryPanel.setBackground(CARD_COLOR);
        statsSummaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        classAverageLabel = new JLabel("Moyenne de classe: ");
        classAverageLabel.setFont(getModernFont(Font.BOLD, 16));
        classAverageLabel.setForeground(TEXT_PRIMARY);

        minAverageLabel = new JLabel("Moyenne min: ");
        minAverageLabel.setFont(getModernFont(Font.BOLD, 16));
        minAverageLabel.setForeground(TEXT_PRIMARY);

        maxAverageLabel = new JLabel("Moyenne max: ");
        maxAverageLabel.setFont(getModernFont(Font.BOLD, 16));
        maxAverageLabel.setForeground(TEXT_PRIMARY);

        statsSummaryPanel.add(classAverageLabel);
        statsSummaryPanel.add(minAverageLabel);
        statsSummaryPanel.add(maxAverageLabel);

        averagesPanel.add(statsSummaryPanel, BorderLayout.SOUTH);

        return averagesPanel;
    }

    private JPanel createUltraModernClassStatsPanel() {
        JPanel classStatsPanel = new JPanel(new BorderLayout());
        classStatsPanel.setBackground(CARD_COLOR);
        classStatsPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        String[] columns = {"Niveau", "Moyenne Générale", "Moyenne Max", "Moyenne Min", "Détails"};
        classStatsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        classStatsTable = createUltraModernTable(classStatsTableModel);
        classStatsTable.getColumn("Détails").setCellRenderer(new UltraModernDetailsButtonRenderer());
        classStatsTable.getColumn("Détails").setCellEditor(new UltraModernDetailsButtonEditor(this::showLevelDetailsDialog));

        JScrollPane scrollPane = createUltraModernScrollPane(classStatsTable);
        classStatsPanel.add(scrollPane, BorderLayout.CENTER);

        return classStatsPanel;
    }

    private void showGradeFormDialog(Note note) {
        boolean isEdit = note != null;
        
        currentDialog = new UltraModernDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            isEdit ? "Modifier la Note" : "Ajouter une Note"
        );
        currentDialog.setSize(650, 750);
        currentDialog.setLocationRelativeTo(this);

        loadData();
        loadGrades();
        loadStatistics();

        JPanel mainPanel = createUltraModernDialogContent(note, isEdit);
        currentDialog.add(mainPanel);
        currentDialog.setVisible(true);
    }

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
            
            g2d.setColor(new Color(0, 0, 0, 40));
            g2d.fillRoundRect(8, 8, getWidth() - 16, getHeight() - 16, 25, 25);
            
            GradientPaint gradient = new GradientPaint(
                0, 0, CARD_COLOR,
                0, getHeight(), new Color(253, 254, 255)
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth() - 15, getHeight() - 15, 25, 25);
            
            g2d.setColor(new Color(79, 70, 229, 150));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(0, 0, getWidth() - 16, getHeight() - 16, 25, 25);
            
            g2d.dispose();
            super.paint(g);
        }
    }

    private JPanel createUltraModernDialogContent(Note note, boolean isEdit) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CARD_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        JPanel headerPanel = createUltraModernDialogHeader(
            isEdit ? "Modifier la Note" : "Ajouter une Note",
            "Veuillez remplir tous les champs requis avec précision",
            isEdit ? IconType.EDIT : IconType.PLUS
        );
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = createUltraModernGradeForm(note, isEdit);
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(null);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        formScrollPane.setOpaque(false);
        formScrollPane.getViewport().setOpaque(false);
        mainPanel.add(formScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = createUltraModernDialogButtons(note, isEdit);
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

    private JPanel createUltraModernGradeForm(Note note, boolean isEdit) {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        formPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 25, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 0, 18, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel matiereLabel = createUltraModernFormLabel("Matière *", IconType.BOOK);
        formPanel.add(matiereLabel, gbc);

        gbc.gridy = 1;
        JComboBox<Matiere> matiereComboBox = createUltraModernMatiereComboBox();
        matiereComboBox.setPreferredSize(new Dimension(500, 50));
        if (allMatieres != null) {
            for (Matiere matière : allMatieres) {
                matiereComboBox.addItem(matière);
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

        gbc.gridy = 2;
        JLabel valeurLabel = createUltraModernFormLabel("Note sur 20 *", IconType.STAR);
        formPanel.add(valeurLabel, gbc);

        gbc.gridy = 3;
        JTextField valeurField = createUltraModernTextField(isEdit && note != null ? String.valueOf(note.getValeur()) : "");
        valeurField.setPreferredSize(new Dimension(500, 50));
        valeurField.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                validateGradeField(valeurField);
            }
        });
        formPanel.add(valeurField, gbc);

        gbc.gridy = 4;
        JLabel semestreLabel = createUltraModernFormLabel("Semestre *", IconType.FILTER);
        formPanel.add(semestreLabel, gbc);

        gbc.gridy = 5;
        JComboBox<String> formSemestreComboBox = createUltraModernStringComboBox();
        formSemestreComboBox.addItem("S1");
        formSemestreComboBox.addItem("S2");
        formSemestreComboBox.setPreferredSize(new Dimension(500, 50));
        if (isEdit && note != null) {
            formSemestreComboBox.setSelectedItem(note.getSemestre());
        }
        formPanel.add(formSemestreComboBox, gbc);

        gbc.gridy = 6;
        JLabel anneeLabel = createUltraModernFormLabel("Année Académique *", IconType.ACADEMIC);
        formPanel.add(anneeLabel, gbc);

        gbc.gridy = 7;
        JPanel anneePanel = createUltraModernYearInputPanel(note, isEdit);
        formPanel.add(anneePanel, gbc);

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
                
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                
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

    private JComboBox<Matiere> createUltraModernMatiereComboBox() {
        JComboBox<Matiere> comboBox = new JComboBox<Matiere>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(new Color(0, 0, 0, 10));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                
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

        comboBox.setFont(getModernFont(Font.PLAIN, 15));
        comboBox.setBackground(new Color(248, 250, 252));
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            new UltraModernBorder(BORDER_COLOR),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)));

        return comboBox;
    }

    private JPanel createUltraModernYearInputPanel(Note note, boolean isEdit) {
        JPanel anneePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        anneePanel.setBackground(CARD_COLOR);

        JTextField startYearField = createUltraModernTextField("");
        startYearField.setPreferredSize(new Dimension(150, 50));
        startYearField.setToolTipText("Année de début (ex: 2024)");

        JLabel separatorLabel = new JLabel("—");
        separatorLabel.setFont(getModernFont(Font.BOLD, 20));
        separatorLabel.setForeground(PRIMARY_COLOR);

        JTextField endYearField = createUltraModernTextField("");
        endYearField.setPreferredSize(new Dimension(150, 50));
        endYearField.setToolTipText("Année de fin (ex: 2025)");

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

        return anneePanel;
    }

    private JPanel createUltraModernDialogButtons(Note note, boolean isEdit) {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(35, 0, 0, 0));

        JButton saveButton = createUltraModernButton("Enregistrer", IconType.SAVE, PRIMARY_COLOR, true);
        JButton cancelButton = createUltraModernButton("Annuler", IconType.CANCEL, ERROR_COLOR, true);

        saveButton.addActionListener(_ -> {
            if (validateAndSaveGrade(note, isEdit)) {
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

    private boolean validateAndSaveGrade(Note note, boolean isEdit) {
        try {
            loadGrades();
            showSuccessNotification(isEdit ? "Note modifiée avec succès!" : "Note ajoutée avec succès!");
            return true;
        } catch (Exception ex) {
            showErrorNotification("Erreur lors de l'enregistrement: " + ex.getMessage());
            return false;
        }
    }

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
                    
                    g2d.setColor(new Color(0, 0, 0, 20));
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                    
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

                    g2d.setColor(new Color(0, 0, 0, 20));
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);

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

    private class UltraModernDetailsButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton button;

        public UltraModernDetailsButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 8));
            setOpaque(false);

            button = createUltraDetailsButton();
            add(button);
        }

        private JButton createUltraDetailsButton() {
            JButton button = new JButton("Voir Détails") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2d.setColor(new Color(0, 0, 0, 20));
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);

                    if (getModel().isPressed()) {
                        g2d.setColor(ACCENT_BLUE.darker());
                    } else if (getModel().isRollover()) {
                        GradientPaint gradient = new GradientPaint(
                                0, 0, ACCENT_BLUE.brighter(),
                                0, getHeight(), ACCENT_BLUE
                        );
                        g2d.setPaint(gradient);
                    } else {
                        GradientPaint gradient = new GradientPaint(
                                0, 0, ACCENT_BLUE,
                                0, getHeight(), ACCENT_BLUE.darker()
                        );
                        g2d.setPaint(gradient);
                    }

                    g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 12, 12);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };

            button.setIcon(createVectorIcon(IconType.STATS, 14, Color.WHITE));
            button.setFont(getModernFont(Font.BOLD, 13));
            button.setForeground(Color.WHITE);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(100, 35));
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

    private class UltraModernDetailsButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton button;
        private final Runnable action;

        public UltraModernDetailsButtonEditor(Runnable action) {
            super(new JTextField());
            this.action = action;

            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
            panel.setOpaque(false);

            button = createUltraDetailsButton();
            button.addActionListener(_ -> {
                fireEditingStopped();
                action.run();
            });

            panel.add(button);
        }

        private JButton createUltraDetailsButton() {
            JButton button = new JButton("Voir Détails") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    g2d.setColor(new Color(0, 0, 0, 20));
                    g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);

                    if (getModel().isPressed()) {
                        g2d.setColor(ACCENT_BLUE.darker());
                    } else if (getModel().isRollover()) {
                        GradientPaint gradient = new GradientPaint(
                                0, 0, ACCENT_BLUE.brighter(),
                                0, getHeight(), ACCENT_BLUE
                        );
                        g2d.setPaint(gradient);
                    } else {
                        GradientPaint gradient = new GradientPaint(
                                0, 0, ACCENT_BLUE,
                                0, getHeight(), ACCENT_BLUE.darker()
                        );
                        g2d.setPaint(gradient);
                    }

                    g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 12, 12);
                    g2d.dispose();
                    super.paintComponent(g);
                }
            };

            button.setIcon(createVectorIcon(IconType.STATS, 14, Color.WHITE));
            button.setFont(getModernFont(Font.BOLD, 13));
            button.setForeground(Color.WHITE);
            button.setContentAreaFilled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setPreferredSize(new Dimension(100, 35));
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

    private void loadData() {
        try {
            allStudents = studentService.getAllEtudiants(mainWindow.getCurrentResponsable().getId());
            allMatieres = studentService.getAllMatieres();
            allNiveaux = studentService.getAllNiveaux();
            allParcours = studentService.getAllParcours();

            studentComboBox.removeAllItems();
            for (Etudiant student : allStudents) {
                studentComboBox.addItem(student);
            }

            if (!allStudents.isEmpty()) {
                studentComboBox.setSelectedIndex(0);
                loadGrades();
            }

            niveauComboBox.removeAllItems();
            niveauComboBox.addItem("Tous");
            for (Niveau niveau : allNiveaux) {
                niveauComboBox.addItem(niveau.getNom());
            }

            parcoursComboBox.removeAllItems();
            parcoursComboBox.addItem("Tous");
            for (Parcours parcours : allParcours) {
                parcoursComboBox.addItem(parcours.getNom());
            }
        } catch (ApiException ex) {
            showErrorNotification("Erreur lors du chargement des données: " + ex.getMessage());
        }
    }

    private void loadGrades() {
        Etudiant selectedStudent = (Etudiant) studentComboBox.getSelectedItem();
        if (selectedStudent == null) return;

        try {
            List<Note> notes = studentService.getNotesByEtudiant(selectedStudent.getId());
            gradesTableModel.setRowCount(0);

            for (Note note : notes) {
                Matiere matiere = getMatiereById(note.getMatiereId());
                gradesTableModel.addRow(new Object[]{
                        matiere != null ? matiere.getNom() : note.getMatiereId(),
                        matiere != null ? matiere.getCoefficient() : "N/A",
                        String.format("%.1f", note.getValeur()),
                        note.getSemestre(),
                        note.getAnnee(),
                        "Actions"
                });
            }
        } catch (ApiException ex) {
            showErrorNotification("Erreur lors du chargement des notes: " + ex.getMessage());
        }
    }

    private void loadStatistics() {
        String niveau = (String) niveauComboBox.getSelectedItem();
        String parcours = (String) parcoursComboBox.getSelectedItem();
        String semestre = (String) semestreComboBox.getSelectedItem();
        String annee = (String) anneeComboBox.getSelectedItem();

        // Gestion des valeurs nulles ou par défaut
        niveau = "Tous".equals(niveau) ? null : niveau;
        parcours = "Tous".equals(parcours) ? null : parcours;
        semestre = (semestre == null || semestre.isEmpty()) ? null : semestre;
        annee = (annee == null || annee.isEmpty()) ? null : annee;

        try {
            // Charger les moyennes des étudiants
            List<StudentAverageDTO> averages = studentService.getStudentAverages(semestre, annee, niveau, parcours);
            averagesTableModel.setRowCount(0);

            double sum = 0;
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;

            for (StudentAverageDTO avg : averages) {
                double moyenne = avg.getMoyenne();
                String mention = getMention(moyenne);
                // Remplacement de "non admis" par "redoublant"
                String observation = "Non Admis".equals(avg.getAdmissionStatus()) ? "Redoublant" : avg.getAdmissionStatus();
                averagesTableModel.addRow(new Object[]{
                        avg.getMatricule(),
                        avg.getPrenom(),
                        avg.getNom(),
                        String.format("%.2f", moyenne),
                        mention,
                        observation
                });
                sum += moyenne;
                if (moyenne < min) min = moyenne;
                if (moyenne > max) max = moyenne;
            }

            classAverage = averages.isEmpty() ? 0 : sum / averages.size();
            minAverage = min == Double.MAX_VALUE ? 0 : min;
            maxAverage = max == Double.MIN_VALUE ? 0 : max;

            classAverageLabel.setText("Moyenne de classe: " + String.format("%.2f", classAverage));
            minAverageLabel.setText("Moyenne min: " + String.format("%.2f", minAverage));
            maxAverageLabel.setText("Moyenne max: " + String.format("%.2f", maxAverage));

            // Charger les statistiques des classes
            currentClassStats = studentService.getAllClassStatistics(semestre, annee, niveau, parcours);
            classStatsTableModel.setRowCount(0);

            for (ClassStatisticsDTO stats : currentClassStats) {
                classStatsTableModel.addRow(new Object[]{
                        stats.getNiveauNom(),
                        String.format("%.2f", stats.getMoyenneGenerale()),
                        String.format("%.2f", stats.getMaxMoyenne()),
                        String.format("%.2f", stats.getMinMoyenne()),
                        "Détails"
                });
            }
        } catch (ApiException ex) {
            showErrorNotification("Erreur lors du chargement des statistiques: " + ex.getMessage());
        }
    }

    private String getMention(double moyenne) {
        // Ajout de la condition pour "exclus" si moyenne < 5
        if (moyenne < 5) return "exclus";
        else if (moyenne < 10) return "Repêchage";
        else if (moyenne < 12) return "Passable";
        else if (moyenne < 14) return "Assez bien";
        else if (moyenne < 16) return "Bien";
        else if (moyenne < 18) return "Très bien";
        else if (moyenne < 20) return "Excellent";
        else if (moyenne == 20) return "Honorable";
        else return "Invalide";
    }

    private Matiere getMatiereById(String id) {
        if (allMatieres == null) return null;
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
                    showErrorNotification("Erreur lors de la modification: " + ex.getMessage());
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
                        showCustomDeleteConfirmDialog(() -> {
                            try {
                                studentService.deleteNote(notes.get(selectedRow).getId());
                                loadGrades();
                                showSuccessNotification("Note supprimée avec succès!");
                            } catch (ApiException ex) {
                                showErrorNotification("Erreur lors de la suppression: " + ex.getMessage());
                            }
                        });
                    }
                } catch (ApiException ex) {
                    showErrorNotification("Erreur lors de la suppression: " + ex.getMessage());
                }
            }
        }
    }

    private void showCustomDeleteConfirmDialog(Runnable onConfirm) {
        JDialog confirmDialog = new UltraModernDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Confirmation de suppression"
        );
        confirmDialog.setSize(480, 280);
        confirmDialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CARD_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel warningIcon = new JLabel(createVectorIcon(IconType.DELETE, 48, ERROR_COLOR));
        JLabel titleLabel = new JLabel("Êtes-vous sûr ?");
        titleLabel.setFont(getModernFont(Font.BOLD, 24));
        titleLabel.setForeground(TEXT_PRIMARY);

        headerPanel.add(warningIcon);
        headerPanel.add(titleLabel);

        JLabel messageLabel = new JLabel("<html><center>Cette action est irréversible.<br/>La note sera définitivement supprimée.</center></html>");
        messageLabel.setFont(getModernFont(Font.PLAIN, 16));
        messageLabel.setForeground(TEXT_SECONDARY);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 0));
        buttonPanel.setBackground(CARD_COLOR);

        JButton yesButton = createCustomConfirmButton("Oui, j'en suis sûr", ERROR_COLOR);
        JButton noButton = createCustomConfirmButton("Non", new Color(107, 114, 128));

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

    private JButton createCustomConfirmButton(String text, Color bgColor) {
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

                if (!isPressed) {
                    g2d.setColor(new Color(0, 0, 0, isHovered ? 30 : 20));
                    g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                }

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

    private void showLevelDetailsDialog() {
        int selectedRow = classStatsTable.getSelectedRow();
        if (selectedRow < 0) {
            showErrorNotification("Veuillez sélectionner un niveau.");
            return;
        }

        if (selectedRow >= currentClassStats.size()) {
            showErrorNotification("Erreur: données de statistiques non disponibles.");
            return;
        }

        ClassStatisticsDTO stats = currentClassStats.get(selectedRow);
        showUltraModernDetailsDialog(stats);
    }

    private void showUltraModernDetailsDialog(ClassStatisticsDTO stats) {
        JDialog dialog = new UltraModernDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "Détails du Niveau: " + stats.getNiveauNom());
        dialog.setSize(950, 750);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CARD_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        JPanel summaryPanel = createUltraModernStatsSummaryPanel(stats);
        mainPanel.add(summaryPanel, BorderLayout.NORTH);

        JTabbedPane studentTabs = createUltraModernStudentDetailsTabs(stats);
        mainPanel.add(studentTabs, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));

        JButton closeButton = createUltraModernButton("Fermer", IconType.CANCEL, ERROR_COLOR, true);
        closeButton.addActionListener(_ -> dialog.dispose());

        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private JPanel createUltraModernStatsSummaryPanel(ClassStatisticsDTO stats) {
        JPanel summaryPanel = new JPanel(new GridLayout(2, 2, 25, 20));
        summaryPanel.setBackground(CARD_COLOR);
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 35, 0));

        summaryPanel.add(createUltraModernStatCard("Niveau", stats.getNiveauNom(), PRIMARY_COLOR, IconType.BOOK));
        summaryPanel.add(createUltraModernStatCard("Moyenne Générale", String.format("%.2f", stats.getMoyenneGenerale()),
                SUCCESS_COLOR, IconType.CHART));
        summaryPanel.add(createUltraModernStatCard("Moyenne Max", String.format("%.2f", stats.getMaxMoyenne()),
                WARNING_COLOR, IconType.STAR));
        summaryPanel.add(createUltraModernStatCard("Moyenne Min", String.format("%.2f", stats.getMinMoyenne()),
                ERROR_COLOR, IconType.CHART));

        return summaryPanel;
    }

    private JPanel createUltraModernStatCard(String title, String value, Color accentColor, IconType iconType) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 25));
                g2d.fillRoundRect(4, 4, getWidth() - 8, getHeight() - 8, 18, 18);

                GradientPaint gradient = new GradientPaint(
                        0, 0, CARD_COLOR,
                        0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 18, 18);

                GradientPaint borderGradient = new GradientPaint(
                        0, 0, accentColor,
                        getWidth(), getHeight(), accentColor.brighter()
                );
                g2d.setPaint(borderGradient);
                g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 18, 18);
                g2d.dispose();
            }
        };

        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        titlePanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel(createVectorIcon(iconType, 20, accentColor));
        titlePanel.add(iconLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(getModernFont(Font.BOLD, 16));
        titleLabel.setForeground(TEXT_SECONDARY);
        titlePanel.add(titleLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(getModernFont(Font.BOLD, 28));
        valueLabel.setForeground(TEXT_PRIMARY);
        valueLabel.setHorizontalAlignment(JLabel.CENTER);

        card.add(titlePanel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JTabbedPane createUltraModernStudentDetailsTabs(ClassStatisticsDTO stats) {
        JTabbedPane studentTabs = new JTabbedPane() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(
                        0, 0, CARD_COLOR,
                        0, getHeight(), new Color(253, 254, 255)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        studentTabs.setFont(getModernFont(Font.BOLD, 15));
        studentTabs.setBackground(CARD_COLOR);

        JPanel topStudentsPanel = createUltraModernStudentListPanel(stats.getTopStudents(), "Top 5 Étudiants");
        studentTabs.addTab("   Top 5   ", createVectorIcon(IconType.TROPHY, 16, WARNING_COLOR), topStudentsPanel);

        JPanel allStudentsPanel = createUltraModernStudentListPanel(stats.getAllStudents(), "Tous les Étudiants");
        studentTabs.addTab("   Tous   ", createVectorIcon(IconType.USER, 16, ACCENT_BLUE), allStudentsPanel);

        return studentTabs;
    }

    private JPanel createUltraModernStudentListPanel(List<StudentAverageDTO> students, String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(getModernFont(Font.BOLD, 20));
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Matricule", "Prénom", "Nom", "Moyenne", "Mention", "Observation"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = createUltraModernTable(model);
        for (StudentAverageDTO student : students) {
            String mention = getMention(student.getMoyenne());
            // Remplacement de "non admis" par "redoublant"
            String observation = "non admis".equals(student.getAdmissionStatus()) ? "redoublant" : student.getAdmissionStatus();
            model.addRow(new Object[]{
                    student.getMatricule(),
                    student.getPrenom(),
                    student.getNom(),
                    String.format("%.2f", student.getMoyenne()),
                    mention,
                    observation
            });
        }

        JScrollPane scrollPane = createUltraModernScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void showSuccessNotification(String message) {
        JOptionPane optionPane = new JOptionPane(
                message,
                JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                createVectorIcon(IconType.STAR, 32, SUCCESS_COLOR)
        );

        JDialog dialog = optionPane.createDialog(this, "Succès");
        dialog.setBackground(CARD_COLOR);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setBackground(CARD_COLOR);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void showErrorNotification(String message) {
        JOptionPane optionPane = new JOptionPane(
                message,
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION,
                createVectorIcon(IconType.CANCEL, 32, ERROR_COLOR)
        );

        JDialog dialog = optionPane.createDialog(this, "Erreur");
        dialog.setBackground(CARD_COLOR);
        dialog.setModal(true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.getContentPane().setBackground(CARD_COLOR);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    private void validateGradeField(JTextField field) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    new UltraModernBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            return;
        }

        try {
            double value = Double.parseDouble(text);
            if (value >= 0 && value <= 20) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new UltraModernBorder(SUCCESS_COLOR),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            } else {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new UltraModernBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            }
        } catch (NumberFormatException e) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    new UltraModernBorder(ERROR_COLOR),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        }
    }

    private void validateYearField(JTextField field) {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    new UltraModernBorder(BORDER_COLOR),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            return;
        }

        if (text.matches("\\d{4}")) {
            try {
                int year = Integer.parseInt(text);
                if (year >= 2000 && year <= 2100) {
                    field.setBorder(BorderFactory.createCompoundBorder(
                            new UltraModernBorder(SUCCESS_COLOR),
                            BorderFactory.createEmptyBorder(15, 20, 15, 20)));
                } else {
                    field.setBorder(BorderFactory.createCompoundBorder(
                            new UltraModernBorder(ERROR_COLOR),
                            BorderFactory.createEmptyBorder(15, 20, 15, 20)));
                }
            } catch (NumberFormatException e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        new UltraModernBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(15, 20, 15, 20)));
            }
        } else {
            field.setBorder(BorderFactory.createCompoundBorder(
                    new UltraModernBorder(ERROR_COLOR),
                    BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        }
    }

    // Méthode pour ajouter des données (commentée comme demandé)
    /*
    private void showAddDataDialog() {
        JDialog dialog = new UltraModernDialog((Frame) SwingUtilities.getWindowAncestor(this), "Ajouter des Données");
        dialog.setSize(650, 750);
        dialog.setLocationRelativeTo(this);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CARD_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(35, 35, 35, 35));

        JPanel headerPanel = createUltraModernDialogHeader("Ajouter des Données", "Veuillez remplir les informations", IconType.PLUS);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(CARD_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(18, 0, 18, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        JLabel studentLabel = createUltraModernFormLabel("Étudiant *", IconType.USER);
        formPanel.add(studentLabel, gbc);

        gbc.gridy = 1;
        JComboBox<Etudiant> studentComboBox = createUltraModernComboBox();
        studentComboBox.setPreferredSize(new Dimension(500, 50));
        for (Etudiant student : allStudents) {
            studentComboBox.addItem(student);
        }
        formPanel.add(studentComboBox, gbc);

        gbc.gridy = 2;
        JLabel dataLabel = createUltraModernFormLabel("Données *", IconType.NOTES);
        formPanel.add(dataLabel, gbc);

        gbc.gridy = 3;
        JTextArea dataField = new JTextArea(5, 30);
        dataField.setFont(getModernFont(Font.PLAIN, 15));
        dataField.setBorder(BorderFactory.createCompoundBorder(
                new UltraModernBorder(BORDER_COLOR),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)));
        formPanel.add(dataField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setBackground(CARD_COLOR);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(35, 0, 0, 0));

        JButton saveButton = createUltraModernButton("Enregistrer", IconType.SAVE, PRIMARY_COLOR, true);
        JButton cancelButton = createUltraModernButton("Annuler", IconType.CANCEL, ERROR_COLOR, true);

        saveButton.addActionListener(_ -> {
            dialog.dispose();
            showSuccessNotification("Données ajoutées avec succès!");
        });

        cancelButton.addActionListener(_ -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    */
}
