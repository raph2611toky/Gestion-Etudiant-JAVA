package com.studentmanagement.ui.common;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.io.Serial;

import javax.swing.JFrame;
import javax.swing.JPanel;
import com.studentmanagement.ui.auth.LoginFrame;
import com.studentmanagement.ui.auth.RegisterFrame;
import com.studentmanagement.ui.dashboard.DashboardFrame;
import com.studentmanagement.ui.etudiant.StudentManagementFrame;
import com.studentmanagement.model.ResponsableResponse;

public class MainWindow extends JFrame {
    @Serial
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
        contentPane.add(new DashboardFrame(this), "Dashboard");
        contentPane.add(new StudentManagementFrame(this), "Students");

        cardLayout.show(contentPane, "Login");
    }

    public void showPanel(String panelName) {
        cardLayout.show(contentPane, panelName);
    }

    public void setCurrentResponsable(ResponsableResponse responsable) {
        this.currentResponsable = responsable;
        if (responsable != null) {
            contentPane.remove(2);
            contentPane.add(new DashboardFrame(this), "Dashboard");
            contentPane.remove(2);
            contentPane.add(new StudentManagementFrame(this), "Students");
        }
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