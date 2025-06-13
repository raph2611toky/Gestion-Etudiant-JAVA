package com.studentmanagement.ui.auth;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import com.studentmanagement.ui.common.MainWindow;
import com.studentmanagement.model.ResponsableResponse;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class LoginFrame extends JPanel {
    private static final long serialVersionUID = 1L;
    private JTextField emailField;
    private JPasswordField passwordField;
    private MainWindow mainWindow;
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color backgroundColor = new Color(236, 240, 241);
    private RestTemplate restTemplate = new RestTemplate();

    public LoginFrame(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        setLayout(new BorderLayout());
        setBackground(backgroundColor);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        mainPanel.setBackground(backgroundColor);

        JPanel formPanel = createFormPanel();

        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalGlue());

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new SoftBevelBorder(BevelBorder.RAISED),
                BorderFactory.createEmptyBorder(20, 30, 30, 30)));

        JLabel titleLabel = new JLabel("Connexion Responsable");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(primaryColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 25)));

        JPanel emailPanel = new JPanel();
        emailPanel.setLayout(new BoxLayout(emailPanel, BoxLayout.Y_AXIS));
        emailPanel.setBackground(Color.WHITE);
        emailPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailPanel.setMaximumSize(new Dimension(350, 70));

        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(new Color(100, 100, 100));

        emailField = new JTextField();
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        emailField.setPreferredSize(new Dimension(300, 30));
        emailField.setMaximumSize(new Dimension(350, 30));

        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                emailField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, secondaryColor),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                emailField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            }
        });

        emailPanel.add(emailLabel);
        emailPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        emailPanel.add(emailField);

        panel.add(emailPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordPanel.setMaximumSize(new Dimension(350, 70));

        JLabel passwordLabel = new JLabel("Mot de passe");
        passwordLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordLabel.setForeground(new Color(100, 100, 100));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        passwordField.setPreferredSize(new Dimension(300, 30));
        passwordField.setMaximumSize(new Dimension(350, 30));

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, secondaryColor),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 2, 0, primaryColor),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            }
        });

        passwordPanel.add(passwordLabel);
        passwordPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        passwordPanel.add(passwordField);

        panel.add(passwordPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(350, 40));

        JButton loginButton = createStyledButton("Se connecter", primaryColor);
        JButton registerButton = createStyledButton("S'inscrire", new Color(52, 73, 94));

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                if (email.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Veuillez remplir tous les champs.", "Erreur",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    String url = "http://localhost:8080/api/responsables/login";
                    LoginRequest request = new LoginRequest();
                    request.setEmail(email);
                    request.setMotDePasse(password);
                    ResponsableResponse response = restTemplate.postForObject(url, request, ResponsableResponse.class);
                    if (response != null) {
                        mainWindow.setCurrentResponsable(response);
                        mainWindow.showPanel("Dashboard");
                        // JOptionPane.showMessageDialog(LoginFrame.this, "Connexion réussie !");
                    } else {
                        JOptionPane.showMessageDialog(LoginFrame.this, "Email ou mot de passe incorrect.", "Erreur",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (HttpClientErrorException ex) {
                    System.out.println("Email ou mot de passe incorrect.");
                    // JOptionPane.showMessageDialog(LoginFrame.this, "Email ou mot de passe
                    // incorrect.", "Erreur",
                    // JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    System.out.println("Erreur lors de la connexion : " + ex.getMessage());
                    // JOptionPane.showMessageDialog(LoginFrame.this, "Erreur lors de la connexion :
                    // " + ex.getMessage(),
                    // "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        registerButton.addActionListener(_ -> mainWindow.showPanel("Register"));

        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        buttonPanel.add(registerButton);

        panel.add(buttonPanel);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(150, 40));
        button.setPreferredSize(new Dimension(150, 40));

        return button;
    }

    class LoginRequest {
        private String email;
        private String motDePasse;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMotDePasse() {
            return motDePasse;
        }

        public void setMotDePasse(String motDePasse) {
            this.motDePasse = motDePasse;
        }
    }
}