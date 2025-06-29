package com.studentmanagement.ui.dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.studentmanagement.ui.common.MainWindow;
import com.studentmanagement.ui.common.SidebarUtil;
import com.studentmanagement.service.StudentService;
import com.studentmanagement.service.ParameterService;
import com.studentmanagement.model.Etudiant;
import com.studentmanagement.model.Niveau;
import com.studentmanagement.model.Parcours;
import com.studentmanagement.model.Mention;
import com.studentmanagement.model.StudentAverageDTO;
import java.text.SimpleDateFormat;

public class DashboardFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private MainWindow mainWindow;
    
    // Couleurs modernes et épurées
    private Color primaryColor = new Color(59, 130, 246);      // Bleu moderne
    private Color sidebarColor = new Color(51, 65, 85);        // Gris foncé
    private Color backgroundColor = new Color(248, 250, 252);   // Gris très clair
    private Color cardColor = Color.WHITE;
    private Color textPrimary = new Color(15, 23, 42);
    private Color textSecondary = new Color(100, 116, 139);
    
    // Couleurs pour les bordures des cartes
    private Color accentBlue = new Color(59, 130, 246);
    private Color accentGreen = new Color(34, 197, 94);
    private Color accentOrange = new Color(251, 146, 60);
    private Color accentPurple = new Color(147, 51, 234);

    private StudentService studentService;
    private ParameterService parameterService;

    private int totalStudents = 0;
    private int activeCourses = 0;
    private int totalMentions = 0;
    private int totalParcours = 0;
    private JLabel[] statValueLabels;

    private JComboBox<String> chartPeriodComboBox;
    private JComboBox<String> chartTypeComboBox;
    private JPanel barChartPanel;
    private JPanel dynamicPieChartPanel;

    public DashboardFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout());
        setBackground(backgroundColor);

        statValueLabels = new JLabel[4];
        for (int i = 0; i < statValueLabels.length; i++) {
            statValueLabels[i] = new JLabel("0");
            statValueLabels[i].setFont(new Font("Segoe UI", Font.BOLD, 36));
            statValueLabels[i].setForeground(textPrimary);
        }

        if (mainWindow.getCurrentResponsable() != null) {
            String token = mainWindow.getCurrentResponsable().getToken();
            studentService = new StudentService();
            parameterService = new ParameterService();
            studentService.setJwtToken(token);
            parameterService.setJwtToken(token);
        } else {
            showErrorNotification("Erreur: Responsable non identifié.");
        }

        JPanel sidebar = SidebarUtil.createSidebar(mainWindow, "Dashboard");
        add(sidebar, BorderLayout.WEST);

        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);

        loadStatisticsAsync();
    }

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(backgroundColor);
        mainContent.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // En-tête
        JPanel header = createHeader();
        mainContent.add(header, BorderLayout.NORTH);

        // Contenu principal
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(backgroundColor);

        // Cartes de statistiques
        JPanel cardsPanel = createStatsCards();
        centerPanel.add(cardsPanel, BorderLayout.NORTH);

        // Section des graphiques
        JPanel chartsSection = createChartsSection();
        centerPanel.add(chartsSection, BorderLayout.CENTER);

        mainContent.add(centerPanel, BorderLayout.CENTER);
        return mainContent;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(backgroundColor);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel("Tableau de Bord");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(textPrimary);
        header.add(titleLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(new SimpleDateFormat("EEEE, d MMMM yyyy").format(new java.util.Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(textSecondary);
        header.add(dateLabel, BorderLayout.EAST);

        return header;
    }

    private JPanel createStatsCards() {
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(backgroundColor);
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        cardsPanel.add(createStatCard("👥", "Total Étudiants", statValueLabels[0], accentBlue));
        cardsPanel.add(createStatCard("📚", "Cours Actifs", statValueLabels[1], accentGreen));
        cardsPanel.add(createStatCard("🏫", "Mentions", statValueLabels[2], accentOrange));
        cardsPanel.add(createStatCard("📖", "Parcours", statValueLabels[3], accentPurple));

        return cardsPanel;
    }

    private JPanel createStatCard(String icon, String title, JLabel valueLabel, Color borderColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Ombre subtile
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 12, 12);
                
                // Fond de la carte
                g2d.setColor(cardColor);
                g2d.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 2, 12, 12);
                
                // Bordure gauche colorée
                g2d.setColor(borderColor);
                g2d.fillRoundRect(0, 0, 4, getHeight() - 2, 4, 4);
                
                g2d.dispose();
            }
        };

        card.setLayout(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createEmptyBorder(24, 20, 24, 20));

        // Panneau supérieur avec icône et titre
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(cardColor);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));
        topPanel.add(iconLabel, BorderLayout.WEST);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(textSecondary);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        topPanel.add(titleLabel, BorderLayout.CENTER);

        card.add(topPanel, BorderLayout.NORTH);

        // Valeur
        valueLabel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));
        card.add(valueLabel, BorderLayout.CENTER);

        // Effet hover
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(249, 250, 251));
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(cardColor);
                card.repaint();
            }
        });

        return card;
    }

    private JPanel createChartsSection() {
        JPanel chartsSection = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsSection.setBackground(backgroundColor);

        // Graphique en barres avec ses filtres
        JPanel barChartContainer = createBarChartContainer();
        chartsSection.add(barChartContainer);

        // Graphique en secteurs avec ses filtres
        JPanel pieChartContainer = createPieChartContainer();
        chartsSection.add(pieChartContainer);

        return chartsSection;
    }

    private JPanel createBarChartContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(cardColor);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // En-tête avec titre et filtre
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(cardColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Moyennes par Semestre");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(textPrimary);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Filtre période
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filterPanel.setBackground(cardColor);

        JLabel periodLabel = new JLabel("Période: ");
        periodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        periodLabel.setForeground(textSecondary);
        filterPanel.add(periodLabel);

        chartPeriodComboBox = createModernComboBox();
        chartPeriodComboBox.addItem("S1 2023-24");
        chartPeriodComboBox.addItem("S2 2023-24");
        chartPeriodComboBox.addItem("S1 2024-25");
        chartPeriodComboBox.addItem("S2 2024-25");
        chartPeriodComboBox.addActionListener(e -> updateCharts());
        filterPanel.add(chartPeriodComboBox);

        headerPanel.add(filterPanel, BorderLayout.EAST);
        container.add(headerPanel, BorderLayout.NORTH);

        // Graphique
        barChartPanel = createBarChartPanel();
        container.add(barChartPanel, BorderLayout.CENTER);

        return container;
    }

    private JPanel createPieChartContainer() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(cardColor);
        container.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // En-tête avec titre et filtre
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(cardColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Répartition des Étudiants");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(textPrimary);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Filtre type
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        filterPanel.setBackground(cardColor);

        JLabel typeLabel = new JLabel("Par: ");
        typeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        typeLabel.setForeground(textSecondary);
        filterPanel.add(typeLabel);

        chartTypeComboBox = createModernComboBox();
        chartTypeComboBox.addItem("Niveau");
        chartTypeComboBox.addItem("Parcours");
        chartTypeComboBox.addItem("Mention");
        chartTypeComboBox.addActionListener(e -> updateCharts());
        filterPanel.add(chartTypeComboBox);

        headerPanel.add(filterPanel, BorderLayout.EAST);
        container.add(headerPanel, BorderLayout.NORTH);

        // Graphique
        dynamicPieChartPanel = createDynamicPieChartPanel();
        container.add(dynamicPieChartPanel, BorderLayout.CENTER);

        return container;
    }

    private JComboBox<String> createModernComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        comboBox.setBackground(new Color(248, 250, 252));
        comboBox.setForeground(textPrimary);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return comboBox;
    }

    private JPanel createBarChartPanel() {
        JPanel barChart = new JPanel() {
            private String hoveredBar = null;
            private Point mousePosition = null;

            {
                addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        mousePosition = e.getPoint();
                        String newHoveredBar = getBarAtPosition(e.getX(), e.getY());
                        if (!java.util.Objects.equals(hoveredBar, newHoveredBar)) {
                            hoveredBar = newHoveredBar;
                            repaint();
                        }
                    }
                });

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hoveredBar = null;
                        mousePosition = null;
                        repaint();
                    }
                });
            }

            private String getBarAtPosition(int x, int y) {
                int width = getWidth();
                int height = getHeight();
                if (width <= 0 || height <= 0) return null;

                int barWidth = width / 4;
                int barIndex = x / barWidth;
                
                if (barIndex >= 0 && barIndex < 4 && y > 20 && y < height - 40) {
                    String[] labels = {"S1 2023-24", "S2 2023-24", "S1 2024-25", "S2 2024-25"};
                    return labels[barIndex];
                }
                return null;
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                if (width <= 0 || height <= 0) return;

                int barWidth = width / 4;
                int maxBarHeight = height - 80;

                String[] labels = {"S1 2023-24", "S2 2023-24", "S1 2024-25", "S2 2024-25"};
                double[] averages = new double[4];

                try {
                    if (studentService != null) {
                        List<StudentAverageDTO> s1_2023 = studentService.getStudentAverages("S1", "2023-2024");
                        List<StudentAverageDTO> s2_2023 = studentService.getStudentAverages("S2", "2023-2024");
                        List<StudentAverageDTO> s1_2024 = studentService.getStudentAverages("S1", "2024-2025");
                        List<StudentAverageDTO> s2_2024 = studentService.getStudentAverages("S2", "2024-2025");

                        averages[0] = s1_2023 != null ? s1_2023.stream().mapToDouble(StudentAverageDTO::getMoyenne).average().orElse(0) * 5 : 0;
                        averages[1] = s2_2023 != null ? s2_2023.stream().mapToDouble(StudentAverageDTO::getMoyenne).average().orElse(0) * 5 : 0;
                        averages[2] = s1_2024 != null ? s1_2024.stream().mapToDouble(StudentAverageDTO::getMoyenne).average().orElse(0) * 5 : 0;
                        averages[3] = s2_2024 != null ? s2_2024.stream().mapToDouble(StudentAverageDTO::getMoyenne).average().orElse(0) * 5 : 0;
                    }
                } catch (Exception e) {
                    // Données par défaut pour la démonstration
                    averages = new double[]{75, 82, 78, 85};
                }

                // Dessiner les barres
                for (int i = 0; i < 4; i++) {
                    int barHeight = (int) (averages[i] / 100.0 * maxBarHeight);
                    int x = i * barWidth + barWidth / 4;
                    int y = height - barHeight - 50;
                    int actualBarWidth = barWidth / 2;

                    // Couleur de la barre
                    Color barColor = primaryColor;
                    if (labels[i].equals(hoveredBar)) {
                        barColor = new Color(37, 99, 235); // Plus foncé au hover
                    }

                    g2d.setColor(barColor);
                    g2d.fillRoundRect(x, y, actualBarWidth, barHeight, 6, 6);

                    // Étiquette
                    g2d.setColor(textSecondary);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
                    FontMetrics fm = g2d.getFontMetrics();
                    int labelWidth = fm.stringWidth(labels[i]);
                    g2d.drawString(labels[i], x + (actualBarWidth - labelWidth) / 2, height - 25);

                    // Valeur au-dessus de la barre
                    String value = String.format("%.1f", averages[i] / 5);
                    int valueWidth = fm.stringWidth(value);
                    g2d.setColor(textPrimary);
                    g2d.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    g2d.drawString(value, x + (actualBarWidth - valueWidth) / 2, y - 8);
                }

                // Tooltip au hover
                if (hoveredBar != null && mousePosition != null) {
                    drawTooltip(g2d, hoveredBar, mousePosition);
                }

                g2d.dispose();
            }

            private void drawTooltip(Graphics2D g2d, String label, Point position) {
                String tooltipText = "Moyenne: " + label;
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                FontMetrics fm = g2d.getFontMetrics();
                int tooltipWidth = fm.stringWidth(tooltipText) + 16;
                int tooltipHeight = fm.getHeight() + 8;

                int x = position.x + 10;
                int y = position.y - tooltipHeight - 5;

                // Fond du tooltip
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.fillRoundRect(x, y, tooltipWidth, tooltipHeight, 6, 6);

                // Texte du tooltip
                g2d.setColor(Color.WHITE);
                g2d.drawString(tooltipText, x + 8, y + fm.getAscent() + 4);
            }
        };

        barChart.setBackground(cardColor);
        barChart.setPreferredSize(new Dimension(0, 300));
        return barChart;
    }

    private JPanel createDynamicPieChartPanel() {
        JPanel pieChart = new JPanel() {
            private String hoveredSlice = null;
            private Point mousePosition = null;

            {
                addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseMoved(MouseEvent e) {
                        mousePosition = e.getPoint();
                        String newHoveredSlice = getSliceAtPosition(e.getX(), e.getY());
                        if (!java.util.Objects.equals(hoveredSlice, newHoveredSlice)) {
                            hoveredSlice = newHoveredSlice;
                            repaint();
                        }
                    }
                });

                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseExited(MouseEvent e) {
                        hoveredSlice = null;
                        mousePosition = null;
                        repaint();
                    }
                });
            }

            private String getSliceAtPosition(int x, int y) {
                int width = getWidth();
                int height = getHeight();
                if (width <= 0 || height <= 0) return null;

                int diameter = Math.min(width - 200, height - 40);
                int centerX = diameter / 2 + 20;
                int centerY = height / 2;

                double dx = x - centerX;
                double dy = y - centerY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance <= diameter / 2) {
                    double angle = Math.atan2(dy, dx);
                    if (angle < 0) angle += 2 * Math.PI;
                    angle = angle * 180 / Math.PI;

                    String type = (String) chartTypeComboBox.getSelectedItem();
                    Map<String, Long> data = getDistributionData(type);
                    List<String> labels = new java.util.ArrayList<>(data.keySet());
                    List<Long> counts = new java.util.ArrayList<>(data.values());

                    double total = counts.stream().mapToLong(Long::longValue).sum();
                    if (total == 0) return null;

                    double currentAngle = 0;
                    for (int i = 0; i < labels.size(); i++) {
                        double arcAngle = (counts.get(i) / total) * 360;
                        if (angle >= currentAngle && angle < currentAngle + arcAngle) {
                            return labels.get(i);
                        }
                        currentAngle += arcAngle;
                    }
                }
                return null;
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                if (width <= 0 || height <= 0) return;

                int diameter = Math.min(width - 200, height - 40);
                int x = 20;
                int y = (height - diameter) / 2;

                String type = (String) chartTypeComboBox.getSelectedItem();
                Map<String, Long> data = getDistributionData(type);
                List<String> labels = new java.util.ArrayList<>(data.keySet());
                List<Long> counts = new java.util.ArrayList<>(data.values());
                
                Color[] colors = {accentBlue, accentGreen, accentOrange, accentPurple, 
                                new Color(236, 72, 153), new Color(14, 165, 233)};

                double total = counts.stream().mapToLong(Long::longValue).sum();
                if (total == 0) {
                    g2d.setColor(textSecondary);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    g2d.drawString("Aucune donnée disponible", width / 2 - 80, height / 2);
                    g2d.dispose();
                    return;
                }

                double startAngle = 0;
                for (int i = 0; i < labels.size(); i++) {
                    double arcAngle = (counts.get(i) / total) * 360;
                    
                    Color sliceColor = colors[i % colors.length];
                    if (labels.get(i).equals(hoveredSlice)) {
                        // Effet hover: couleur plus claire
                        sliceColor = new Color(
                            Math.min(255, sliceColor.getRed() + 30),
                            Math.min(255, sliceColor.getGreen() + 30),
                            Math.min(255, sliceColor.getBlue() + 30)
                        );
                    }
                    
                    g2d.setColor(sliceColor);
                    g2d.fill(new Arc2D.Double(x, y, diameter, diameter, startAngle, arcAngle, Arc2D.PIE));
                    
                    startAngle += arcAngle;
                }

                // Légende
                int legendX = x + diameter + 30;
                int legendY = y + 20;
                
                g2d.setColor(textPrimary);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2d.drawString("Légende", legendX, legendY);
                
                for (int i = 0; i < labels.size(); i++) {
                    int itemY = legendY + 30 + i * 25;
                    
                    // Carré de couleur
                    g2d.setColor(colors[i % colors.length]);
                    g2d.fillRoundRect(legendX, itemY - 10, 12, 12, 3, 3);
                    
                    // Texte
                    g2d.setColor(textPrimary);
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    String text = labels.get(i) + " (" + counts.get(i) + ")";
                    g2d.drawString(text, legendX + 20, itemY);
                }

                // Tooltip au hover
                if (hoveredSlice != null && mousePosition != null) {
                    drawTooltip(g2d, hoveredSlice, data.get(hoveredSlice), mousePosition);
                }

                g2d.dispose();
            }

            private void drawTooltip(Graphics2D g2d, String label, Long count, Point position) {
                String tooltipText = label + ": " + count + " étudiant(s)";
                g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                FontMetrics fm = g2d.getFontMetrics();
                int tooltipWidth = fm.stringWidth(tooltipText) + 16;
                int tooltipHeight = fm.getHeight() + 8;

                int x = position.x + 10;
                int y = position.y - tooltipHeight - 5;

                // Fond du tooltip
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.fillRoundRect(x, y, tooltipWidth, tooltipHeight, 6, 6);

                // Texte du tooltip
                g2d.setColor(Color.WHITE);
                g2d.drawString(tooltipText, x + 8, y + fm.getAscent() + 4);
            }
        };

        pieChart.setBackground(cardColor);
        pieChart.setPreferredSize(new Dimension(0, 300));
        return pieChart;
    }

    private Map<String, Long> getDistributionData(String type) {
        if ("Niveau".equals(type)) {
            return getStudentsByNiveau();
        } else if ("Parcours".equals(type)) {
            return getStudentsByParcours();
        } else if ("Mention".equals(type)) {
            return getStudentsByMention();
        }
        return Map.of();
    }

    private Map<String, Long> getStudentsByNiveau() {
        try {
            if (studentService != null && mainWindow.getCurrentResponsable() != null) {
                String responsableId = mainWindow.getCurrentResponsable().getId();
                List<Etudiant> students = studentService.getAllEtudiants(responsableId);
                List<Niveau> niveauList = parameterService.getAllNiveaux();

                Map<String, String> niveauNames = niveauList.stream()
                        .collect(Collectors.toMap(Niveau::getId, Niveau::getNom));

                return students.stream()
                        .filter(s -> s.getNiveauId() != null)
                        .collect(Collectors.groupingBy(s -> niveauNames.getOrDefault(s.getNiveauId(), "Inconnu"), Collectors.counting()));
            }
        } catch (Exception e) {
            showErrorNotification("Erreur lors du chargement des données des niveaux: " + e.getMessage());
        }
        // Données par défaut pour la démonstration
        return Map.of("L1", 45L, "L2", 38L, "L3", 52L, "M1", 28L);
    }

    private Map<String, Long> getStudentsByParcours() {
        try {
            if (studentService != null && mainWindow.getCurrentResponsable() != null) {
                String responsableId = mainWindow.getCurrentResponsable().getId();
                List<Etudiant> students = studentService.getAllEtudiants(responsableId);
                List<Parcours> parcoursList = parameterService.getAllParcours();
                List<Mention> mentionList = parameterService.getAllMentions();

                Map<String, String> mentionNames = mentionList.stream()
                        .collect(Collectors.toMap(Mention::getId, Mention::getNom));
                Map<String, String> parcoursNames = parcoursList.stream()
                        .collect(Collectors.toMap(Parcours::getId, p -> p.getNom()));

                return students.stream()
                        .filter(s -> s.getParcoursId() != null)
                        .collect(Collectors.groupingBy(s -> parcoursNames.getOrDefault(s.getParcoursId(), "Inconnu"), Collectors.counting()));
            }
        } catch (Exception e) {
            showErrorNotification("Erreur lors du chargement des données des parcours: " + e.getMessage());
        }
        // Données par défaut pour la démonstration
        return Map.of("Informatique", 65L, "Mathématiques", 42L, "Physique", 38L);
    }

    private Map<String, Long> getStudentsByMention() {
        try {
            if (studentService != null && mainWindow.getCurrentResponsable() != null) {
                String responsableId = mainWindow.getCurrentResponsable().getId();
                List<Etudiant> students = studentService.getAllEtudiants(responsableId);
                List<Parcours> parcoursList = parameterService.getAllParcours();
                List<Mention> mentionList = parameterService.getAllMentions();

                Map<String, String> parcoursToMention = parcoursList.stream()
                        .collect(Collectors.toMap(Parcours::getId, Parcours::getMentionId));
                Map<String, String> mentionNames = mentionList.stream()
                        .collect(Collectors.toMap(Mention::getId, Mention::getNom));

                return students.stream()
                        .filter(s -> s.getParcoursId() != null)
                        .filter(s -> parcoursToMention.get(s.getParcoursId()) != null)
                        .collect(Collectors.groupingBy(
                                s -> mentionNames.getOrDefault(parcoursToMention.get(s.getParcoursId()), "Inconnu"),
                                Collectors.counting()));
            }
        } catch (Exception e) {
            showErrorNotification("Erreur lors du chargement des données des mentions: " + e.getMessage());
        }
        // Données par défaut pour la démonstration
        return Map.of("Sciences", 85L, "Lettres", 35L, "Économie", 43L);
    }

    private void updateCharts() {
        if (barChartPanel != null) barChartPanel.repaint();
        if (dynamicPieChartPanel != null) dynamicPieChartPanel.repaint();
    }

    private void loadStatisticsAsync() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (studentService != null && parameterService != null && mainWindow.getCurrentResponsable() != null) {
                    String responsableId = mainWindow.getCurrentResponsable().getId();

                    totalStudents = studentService.getAllEtudiants(responsableId) != null
                            ? studentService.getAllEtudiants(responsableId).size() : 0;
                    activeCourses = parameterService.getAllMatieres() != null
                            ? parameterService.getAllMatieres().size() : 0;
                    totalMentions = parameterService.getAllMentions() != null
                            ? parameterService.getAllMentions().size() : 0;
                    totalParcours = parameterService.getAllParcours() != null
                            ? parameterService.getAllParcours().size() : 0;
                } else {
                    // Données par défaut pour la démonstration
                    totalStudents = 163;
                    activeCourses = 24;
                    totalMentions = 3;
                    totalParcours = 8;
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    statValueLabels[0].setText(String.valueOf(totalStudents));
                    statValueLabels[1].setText(String.valueOf(activeCourses));
                    statValueLabels[2].setText(String.valueOf(totalMentions));
                    statValueLabels[3].setText(String.valueOf(totalParcours));
                    updateCharts();
                    repaint();
                } catch (Exception e) {
                    showErrorNotification("Erreur lors du chargement des statistiques: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void showErrorNotification(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}