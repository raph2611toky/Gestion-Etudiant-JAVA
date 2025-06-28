package com.studentmanagement.ui.dashboard;

import javax.swing.*;
import java.awt.*;
import com.studentmanagement.ui.common.MainWindow;
import com.studentmanagement.ui.common.SidebarUtil;
import com.studentmanagement.service.StudentService;
import com.studentmanagement.service.ParameterService;
import com.studentmanagement.model.StudentAverageDTO;
import java.util.List;
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

    // Services for fetching statistics
    private StudentService studentService;
    private ParameterService parameterService;

    // Statistics counters
    private int totalStudents = 0;
    private int activeCourses = 0;
    private int upcomingExams = 0;
    private int graduatedStudents = 0;
    private JLabel[] statValueLabels; // To update card values dynamically

    public DashboardFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        statValueLabels = new JLabel[4]; // For the four stat cards

        // Initialize services
        if (mainWindow.getCurrentResponsable() != null) {
            String token = mainWindow.getCurrentResponsable().getToken();
            studentService = new StudentService();
            parameterService = new ParameterService();
            studentService.setJwtToken(token);
            parameterService.setJwtToken(token);
        }

        // Use SidebarUtil to create the sidebar
        JPanel sidebar = SidebarUtil.createSidebar(mainWindow, "Dashboard");
        add(sidebar, BorderLayout.WEST);

        // Main content
        JPanel mainContent = createMainContent();
        add(mainContent, BorderLayout.CENTER);

        // Load statistics
        loadStatistics();
    }

    private JPanel createMainContent() {
        loadStatistics();
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

        // Date and time
        JLabel dateLabel = new JLabel(new SimpleDateFormat("EEEE, d MMMM yyyy").format(new java.util.Date()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateLabel.setForeground(new Color(120, 120, 120));
        header.add(dateLabel, BorderLayout.EAST);

        mainContent.add(header, BorderLayout.NORTH);

        // Main panel with statistics and charts
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(backgroundColor);

        // Statistics cards panel
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(backgroundColor);

        statValueLabels[0] = new JLabel(String.valueOf(totalStudents));
        statValueLabels[1] = new JLabel(String.valueOf(activeCourses));
        statValueLabels[2] = new JLabel(String.valueOf(upcomingExams));
        statValueLabels[3] = new JLabel(String.valueOf(graduatedStudents));

        cardsPanel.add(createStatCard("👥 Total Étudiants", statValueLabels[0], primaryColor));
        cardsPanel.add(createStatCard("📚 Cours Actifs", statValueLabels[1], accentColor1));
        cardsPanel.add(createStatCard("📋 Examens à Venir", statValueLabels[2], accentColor2));
        cardsPanel.add(createStatCard("🎓 Diplômés", statValueLabels[3], accentColor3));

        centerPanel.add(cardsPanel, BorderLayout.NORTH);

        // Recent activity panel
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(backgroundColor);
        activityPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JLabel activityLabel = new JLabel("Performance des Étudiants");
        activityLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        activityLabel.setForeground(sidebarColor);
        activityPanel.add(activityLabel, BorderLayout.NORTH);

        JPanel chartPanel = createChartPanel();
        activityPanel.add(chartPanel, BorderLayout.CENTER);

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

        loadStatistics();

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(100, 100, 100));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        valueLabel.setForeground(accentColor);

        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.setBackground(cardColor);
        JLabel trendLabel = new JLabel(""); // Dynamic trend
        trendLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        trendLabel.setForeground(new Color(46, 204, 113));
        progressPanel.add(trendLabel, BorderLayout.WEST);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(cardColor);

        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        progressPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        contentPanel.add(progressPanel);

        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createChartPanel() {

        loadStatistics();
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRoundRect(3, 3, getWidth() - 6, getHeight() - 6, 15, 15);

                g2d.setColor(cardColor);
                g2d.fillRoundRect(0, 0, getWidth() - 3, getHeight() - 3, 15, 15);

                g2d.dispose();
                super.paintComponent(g);
            }
        };

        chartPanel.setLayout(new BorderLayout());
        chartPanel.setBackground(cardColor);
        chartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        chartPanel.setPreferredSize(new Dimension(0, 300));

        JPanel barChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int barWidth = width / 4; // Four bars for S1/S2 of two years
                int maxBarHeight = height - 40;

                String[] labels = { "S1 2023-24", "S2 2023-24", "S1 2024-25", "S2 2024-25" };
                double[] averages = new double[4];

                try {
                    if (studentService != null) {
                        List<StudentAverageDTO> s1_2023 = studentService.getStudentAverages("S1", "2023-2024");
                        List<StudentAverageDTO> s2_2023 = studentService.getStudentAverages("S2", "2023-2024");
                        List<StudentAverageDTO> s1_2024 = studentService.getStudentAverages("S1", "2024-2025");
                        List<StudentAverageDTO> s2_2024 = studentService.getStudentAverages("S2", "2024-2025");

                        averages[0] = s1_2023.stream().mapToDouble(StudentAverageDTO::getMoyenne).average().orElse(0)
                                * 5; // Scale for display
                        averages[1] = s2_2023.stream().mapToDouble(StudentAverageDTO::getMoyenne).average().orElse(0)
                                * 5;
                        averages[2] = s1_2024.stream().mapToDouble(StudentAverageDTO::getMoyenne).average().orElse(0)
                                * 5;
                        averages[3] = s2_2024.stream().mapToDouble(StudentAverageDTO::getMoyenne).average().orElse(0)
                                * 5;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < 4; i++) {
                    int barHeight = (int) (averages[i] / 100.0 * maxBarHeight);
                    int x = i * barWidth + 10;
                    int y = height - barHeight - 30;

                    g2d.setColor(new Color(41, 128, 185, 180));
                    g2d.fillRoundRect(x, y, barWidth - 10, barHeight, 6, 6);

                    g2d.setColor(new Color(100, 100, 100));
                    g2d.drawString(labels[i], x, height - 10);

                    g2d.drawString(String.format("%.1f", averages[i] / 5), x + 5, y - 5);
                }

                g2d.dispose();
            }
        };
        barChart.setBackground(cardColor);
        chartPanel.add(barChart, BorderLayout.CENTER);

        return chartPanel;
    }

    private void loadStatistics() {
        try {
            if (studentService != null && parameterService != null && mainWindow.getCurrentResponsable() != null) {
                String responsableId = mainWindow.getCurrentResponsable().getId();

                // Fetch total students
                totalStudents = studentService.getAllEtudiants(responsableId).size();

                // Fetch active courses
                activeCourses = parameterService.getAllMatieres().size();

                // Fetch upcoming exams (notes in current academic year as proxy)
                Calendar now = Calendar.getInstance();
                int currentYear = now.get(Calendar.YEAR);
                String currentAnnee = currentYear + "-" + (currentYear + 1);
                upcomingExams = studentService.getAllNotes(null, currentAnnee).size();

                // Fetch graduated students (average >= 10 in S2 2024-2025)
                List<StudentAverageDTO> averages = studentService.getStudentAverages("S2", "2024-2025");
                graduatedStudents = (int) averages.stream().filter(avg -> avg.getMoyenne() >= 10).count();

                // Update UI
                statValueLabels[0].setText(String.valueOf(totalStudents));
                statValueLabels[1].setText(String.valueOf(activeCourses));
                statValueLabels[2].setText(String.valueOf(upcomingExams));
                statValueLabels[3].setText(String.valueOf(graduatedStudents));

                repaint();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur lors du chargement des statistiques : " + e.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}