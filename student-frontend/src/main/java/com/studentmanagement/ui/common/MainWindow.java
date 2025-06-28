package com.studentmanagement.ui.common;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JPanel;
import com.studentmanagement.ui.auth.LoginFrame;
import com.studentmanagement.ui.auth.RegisterFrame;
import com.studentmanagement.ui.dashboard.DashboardFrame;
import com.studentmanagement.ui.etudiant.StudentManagementFrame;
import com.studentmanagement.ui.parameters.ParametersFrame;
import com.studentmanagement.ui.grades.GradesManagementFrame;
import com.studentmanagement.model.ResponsableResponse;

public class MainWindow extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private CardLayout cardLayout;
    private ResponsableResponse currentResponsable;

    public MainWindow() {
        setTitle("Gestion des Étudiants");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 1400, 900);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        contentPane = new JPanel(cardLayout);
        setContentPane(contentPane);

        contentPane.add(new LoginFrame(this), "Login");
        contentPane.add(new RegisterFrame(this), "Register");

        cardLayout.show(contentPane, "Login");
    }

    public void showPanel(String panelName) {
        cardLayout.show(contentPane, panelName);
    }

    public void setCurrentResponsable(ResponsableResponse responsable) {
        this.currentResponsable = responsable;
        if (responsable != null) {
            if (contentPane.getComponentCount() <= 2) {
                contentPane.add(new DashboardFrame(this), "Dashboard");
                contentPane.add(new StudentManagementFrame(this), "Students");
                contentPane.add(new ParametersFrame(this), "Settings");
                contentPane.add(new GradesManagementFrame(this), "Grades");
            }
        } else {
            removePanel("Dashboard");
            removePanel("Students");
            removePanel("Settings");
            removePanel("Grades");
            showPanel("Login");
        }
    }

    private void removePanel(String panelName) {
        Component[] components = contentPane.getComponents();
        for (Component comp : components) {
            String name = getComponentName(comp);
            if (panelName.equals(name)) {
                contentPane.remove(comp);
                break;
            }
        }
    }

    private String getComponentName(Component comp) {
        Component[] components = contentPane.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == comp) {
                String[] names = new String[]{"Login", "Register", "Dashboard", "Students", "Settings", "Grades"};
                if (i < names.length) {
                    return names[i];
                }
            }
        }
        return null;
    }

    public ResponsableResponse getCurrentResponsable() {
        return currentResponsable;
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                MainWindow frame = new MainWindow();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}