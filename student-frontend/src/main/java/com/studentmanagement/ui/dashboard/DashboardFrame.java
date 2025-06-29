package com.studentmanagement.ui.dashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.List;
// import java.util.ArrayList;
import com.studentmanagement.ui.common.MainWindow;
import com.studentmanagement.ui.common.SidebarUtil;
import com.studentmanagement.service.StudentService;
import com.studentmanagement.service.ParameterService;
import com.studentmanagement.model.StudentAverageDTO;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DashboardFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private MainWindow mainWindow;
    private Color primaryColor = new Color(41, 128, 185);
    private Color sidebarColor = new Color(52, 73, 94);
    private Color backgroundColor = new Color(245, 247, 250);
    private Color cardColor = Color.WHITE;
    private Color accentColor1 = new Color(46, 204, 113);
    private Color accentColor2 = new Color(230, 126, 34);
    private Color accentColor3 = new Color(155, 89, 182);
    private Color accentColor4 = new Color(255, 99, 132); // For pie chart

    // Services for fetching statistics
    private StudentService studentService;
    private ParameterService parameterService;

    // Statistics counters
    private int totalStudents = 0;
    private int activeCourses = 0;
    private int upcomingExams = 0;
    private int graduatedStudents = 0;
    private JLabel[] statValueLabels;

    // Chart data
    private JComboBox<String> chartPeriodComboBox;
    private JPanel barChartPanel;
    private JPanel pieChartPanel;

    public DashboardFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout());
        setBackground(backgroundColor);

        // Initialize statValueLabels
        statValueLabels = new JLabel[4];
        for (int i = 0; i < statValueLabels.length; i++) {
            statValueLabels[i] = new JLabel("0");
            statValueLabels[i].setFont(new Font("Segoe UI", Font.BOLD, 42));
            statValueLabels[i].setForeground(primaryColor);
        }

        // Initialize services
        if (mainWindow.getCurrentResponsable() != null) {
            String token = mainWindow.getCurrentResponsable().getToken();
            studentService = new StudentService();
            parameterService = new ParameterService();
            studentService.setJwtToken(token);
            parameterService.setJwtToken(token);
        } else {
            showErrorNotification("Erreur: Responsable non identifié.");
        }

        // Create sidebar
        JPanel sidebar = SidebarUtil.createSidebar(mainWindow, "Dashboard");
        add(sidebar, BorderLayout.WEST);

        // Create main content
        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);

        // Load statistics asynchronously
        loadStatisticsAsync();
    }

    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(backgroundColor);
        mainContent.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(backgroundColor);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        JLabel titleLabel = new JLabel("Tableau de Bord");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(sidebarColor);
        header.add(titleLabel, BorderLayout.WEST);

        JLabel dateLabel = new JLabel(new SimpleDateFormat("EEEE, d MMMM yyyy").format(new java.util.Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateLabel.setForeground(new Color(120, 120, 120));
        header.add(dateLabel, BorderLayout.EAST);

        mainContent.add(header, BorderLayout.NORTH);

        // Center panel with stats and charts
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(backgroundColor);

        // Statistics cards panel
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(backgroundColor);
        cardsPanel.add(createStatCard("👥 Total Étudiants", statValueLabels[0], primaryColor));
        cardsPanel.add(createStatCard("📚 Cours Actifs", statValueLabels[1], accentColor1));
        cardsPanel.add(createStatCard("📋 Examens à Venir", statValueLabels[2], accentColor2));
        cardsPanel.add(createStatCard("🎓 Diplômés", statValueLabels[3], accentColor3));

        centerPanel.add(cardsPanel, BorderLayout.NORTH);

        // Charts panel
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(backgroundColor);
        activityPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel activityLabel = new JLabel("Performance des Étudiants");
        activityLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        activityLabel.setForeground(sidebarColor);
        activityPanel.add(activityLabel, BorderLayout.NORTH);

        JPanel chartContainer = createChartPanel();
        activityPanel.add(chartContainer, BorderLayout.CENTER);

        centerPanel.add(activityPanel, BorderLayout.CENTER);
        mainContent.add(centerPanel, BorderLayout.CENTER);

        return mainContent;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);
                g2d.setColor(cardColor);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);
                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, 8, getHeight() - 3, 4, 4);
                g2d.dispose();
                super.paintComponent(g);
            }
        };

        card.setLayout(new BorderLayout());
        card.setBackground(cardColor);
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(100, 100, 100));

        valueLabel.setForeground(accentColor);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(cardColor);

        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(valueLabel);

        card.add(contentPanel, BorderLayout.CENTER);
        return card;
    }

    private JPanel createChartPanel() {
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(cardColor);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Period selector for pie chart
        JPanel selectorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectorPanel.setBackground(cardColor);
        JLabel periodLabel = new JLabel("Période: ");
        periodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        selectorPanel.add(periodLabel);

        chartPeriodComboBox = new JComboBox<>(new String[] { "S1 2023-24", "S2 2023-24", "S1 2024-25", "S2 2024-25" });
        chartPeriodComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        chartPeriodComboBox.addActionListener(_ -> updateCharts());
        selectorPanel.add(chartPeriodComboBox);

        chartPanel.add(selectorPanel, BorderLayout.NORTH);

        // Charts container
        JPanel chartsContainer = new JPanel(new GridLayout(1, 2, 20, 0));
        chartsContainer.setBackground(cardColor);

        // Bar chart (histogram)
        barChartPanel = createBarChartPanel();
        chartsContainer.add(barChartPanel);

        // Pie chart
        pieChartPanel = createPieChartPanel();
        chartsContainer.add(pieChartPanel);

        chartPanel.add(chartsContainer, BorderLayout.CENTER);
        return chartPanel;
    }

    private JPanel createBarChartPanel() {
        JPanel barChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int barWidth = width / 4;
                int maxBarHeight = height - 40;

                String[] labels = { "S1 2023-24", "S2 2023-24", "S1 2024-25", "S2 2024-25" };
                double[] averages = new double[4];

                try {
                    if (studentService != null) {
                        List<StudentAverageDTO> s1_2023 = studentService.getStudentAverages("S1", "2023-2024");
                        List<StudentAverageDTO> s2_2023 = studentService.getStudentAverages("S2", "2023-2024");
                        List<StudentAverageDTO> s1_2024 = studentService.getStudentAverages("S1", "2024-2025");
                        List<StudentAverageDTO> s2_2024 = studentService.getStudentAverages("S2", "2024-2025");

                        averages[0] = s1_2023 != null
                                ? s1_2023.stream().mapToDouble(StudentAverageDTO::getMoyenne).average().orElse(0) * 5
                                : 0;
                        averages[1] = s2_2023 != null
                                ? s2_2023.stream().mapToDouble(StudentAverageDTO::getMoyenne).average().orElse(0) * 5
                                : 0;
                        averages[2] = s1_2024 != null
                                ? s1_2024.stream().mapToDouble(StudentAverageDTO::getMoyenne).average().orElse(0) * 5
                                : 0;
                        averages[3] = s2_2024 != null
                                ? s2_2024.stream().mapToDouble(StudentAverageDTO::getMoyenne).average().orElse(0) * 5
                                : 0;
                    }
                } catch (Exception e) {
                    showErrorNotification("Erreur lors du chargement des données de l'histogramme: " + e.getMessage());
                }

                for (int i = 0; i < 4; i++) {
                    int barHeight = (int) (averages[i] / 100.0 * maxBarHeight);
                    int x = i * barWidth + 10;
                    int y = height - barHeight - 30;

                    g2d.setColor(new Color(41, 128, 185, 180));
                    g2d.fillRoundRect(x, y, barWidth - 10, barHeight, 6, 6);

                    g2d.setColor(new Color(100, 100, 100));
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2d.drawString(labels[i], x, height - 10);
                    g2d.drawString(String.format("%.1f", averages[i] / 5), x + 5, y - 5);
                }

                g2d.dispose();
            }
        };
        barChart.setBackground(cardColor);
        barChart.setPreferredSize(new Dimension(0, 300));
        barChart.setBorder(BorderFactory.createTitledBorder("Moyennes par Semestre"));
        return barChart;
    }

    private JPanel createPieChartPanel() {
        JPanel pieChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int diameter = Math.min(width, height) - 40;
                int x = (width - diameter) / 2;
                int y = (height - diameter) / 2;

                double[] categories = new double[4]; // Excellent (>=15), Good (12-15), Average (10-12), Below Average
                                                     // (<10)
                String[] categoryLabels = { "Excellent", "Bon", "Moyen", "Faible" };
                Color[] categoryColors = { accentColor1, accentColor2, accentColor3, accentColor4 };

                String selectedPeriod = (String) chartPeriodComboBox.getSelectedItem();
                String[] parts = selectedPeriod != null ? selectedPeriod.split(" ")
                        : new String[] { "S2", "2024-2025" };
                String semestre = parts[0];
                String annee = parts[1];

                try {
                    if (studentService != null) {
                        List<StudentAverageDTO> averages = studentService.getStudentAverages(semestre, annee);
                        if (averages != null) {
                            for (StudentAverageDTO avg : averages) {
                                double moyenne = avg.getMoyenne();
                                if (moyenne >= 15)
                                    categories[0]++;
                                else if (moyenne >= 12)
                                    categories[1]++;
                                else if (moyenne >= 10)
                                    categories[2]++;
                                else
                                    categories[3]++;
                            }
                        }
                    }
                } catch (Exception e) {
                    showErrorNotification("Erreur lors du chargement des données du camembert: " + e.getMessage());
                }

                double total = 0;
                for (double count : categories)
                    total += count;
                if (total == 0)
                    total = 1; // Avoid division by zero

                double startAngle = 0;
                for (int i = 0; i < categories.length; i++) {
                    double arcAngle = (categories[i] / total) * 360;
                    g2d.setColor(categoryColors[i]);
                    g2d.fill(new Arc2D.Double(x, y, diameter, diameter, startAngle, arcAngle, Arc2D.PIE));
                    startAngle += arcAngle;
                }

                // Draw legend
                int legendX = x + diameter + 20;
                int legendY = y;
                for (int i = 0; i < categoryLabels.length; i++) {
                    g2d.setColor(categoryColors[i]);
                    g2d.fillRect(legendX, legendY + i * 20, 15, 15);
                    g2d.setColor(new Color(100, 100, 100));
                    g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g2d.drawString(categoryLabels[i] + ": " + (int) categories[i], legendX + 20, legendY + i * 20 + 12);
                }

                g2d.dispose();
            }
        };
        pieChart.setBackground(cardColor);
        pieChart.setPreferredSize(new Dimension(0, 300));
        pieChart.setBorder(BorderFactory.createTitledBorder("Répartition des Performances"));
        return pieChart;
    }

    private void updateCharts() {
        if (barChartPanel != null)
            barChartPanel.repaint();
        if (pieChartPanel != null)
            pieChartPanel.repaint();
    }

    private void loadStatisticsAsync() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (studentService != null && parameterService != null && mainWindow.getCurrentResponsable() != null) {
                    String responsableId = mainWindow.getCurrentResponsable().getId();

                    totalStudents = studentService.getAllEtudiants(responsableId) != null
                            ? studentService.getAllEtudiants(responsableId).size()
                            : 0;
                    activeCourses = parameterService.getAllMatieres() != null
                            ? parameterService.getAllMatieres().size()
                            : 0;

                    Calendar now = Calendar.getInstance();
                    int currentYear = now.get(Calendar.YEAR);
                    String currentAnnee = currentYear + "-" + (currentYear + 1);
                    upcomingExams = studentService.getAllNotes(null, currentAnnee) != null
                            ? studentService.getAllNotes(null, currentAnnee).size()
                            : 0;

                    List<StudentAverageDTO> averages = studentService.getStudentAverages("S2", "2024-2025");
                    graduatedStudents = averages != null
                            ? (int) averages.stream().filter(avg -> avg.getMoyenne() >= 10).count()
                            : 0;
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    statValueLabels[0].setText(String.valueOf(totalStudents));
                    statValueLabels[1].setText(String.valueOf(activeCourses));
                    statValueLabels[2].setText(String.valueOf(upcomingExams));
                    statValueLabels[3].setText(String.valueOf(graduatedStudents));
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
        JOptionPane.showMessageDialog(this, message, "Erreur affiche > ", JOptionPane.ERROR_MESSAGE);
    }
}