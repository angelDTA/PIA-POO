package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.io.IOException;

public class CarreraApostarGUI extends JFrame {
    private JTextField montoField;
    private JButton aceptarBtn;
    private JLabel mensajeLabel;
    private int carroSeleccionado = -1;

    public CarreraApostarGUI(Ventana ventanaPrincipal) {
        setTitle("Apuesta");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        FondoPanel fondo = new FondoPanel();
        fondo.setLayout(null);
        setContentPane(fondo);

        montoField = new JTextField();
        montoField.setBounds(175, 230, 390, 30);
        montoField.setOpaque(false);
        montoField.setBorder(null);
        montoField.setForeground(Color.BLACK);
        fondo.add(montoField);

        mensajeLabel = new JLabel("", SwingConstants.CENTER);
        mensajeLabel.setBounds(10, 300, 400, 30);
        fondo.add(mensajeLabel);

        aceptarBtn = new JButton();
        aceptarBtn.setOpaque(false);
        aceptarBtn.setContentAreaFilled(false);
        aceptarBtn.setBorderPainted(false);
        aceptarBtn.setFocusPainted(false);
        aceptarBtn.setBounds(300, 280, 250, 50);
        fondo.add(aceptarBtn);

        aceptarBtn.addActionListener(e -> {
            try {
                double monto = Double.parseDouble(montoField.getText());
                if (carroSeleccionado == -1) {
                    JOptionPane.showMessageDialog(this, "Selecciona un coche.", "Error", JOptionPane.WARNING_MESSAGE);
                } else if (Saldo.descontar(monto)) {
                    ventanaPrincipal.setApuesta(carroSeleccionado, monto);
                    JOptionPane.showMessageDialog(this, "¡Apuesta registrada!", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Saldo insuficiente.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Monto inválido.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnC1 = new JButton("🚗 C1");
        btnC1.setOpaque(false);
        btnC1.setContentAreaFilled(false);
        btnC1.setBorderPainted(false);
        btnC1.setFocusPainted(false);
        btnC1.setBounds(0, 110, 200, 90);
        fondo.add(btnC1);

        JButton btnC2 = new JButton("🚙 C2");
        btnC2.setOpaque(false);
        btnC2.setContentAreaFilled(false);
        btnC2.setBorderPainted(false);
        btnC2.setFocusPainted(false);
        btnC2.setBounds(200, 110, 175, 90);
        fondo.add(btnC2);

        JButton btnC3 = new JButton("🏎 C3");
        btnC3.setOpaque(false);
        btnC3.setContentAreaFilled(false);
        btnC3.setBorderPainted(false);
        btnC3.setFocusPainted(false);
        btnC3.setBounds(375, 110, 200, 90);
        fondo.add(btnC3);

        btnC1.addActionListener(e -> seleccionarCarro(0));
        btnC2.addActionListener(e -> seleccionarCarro(1));
        btnC3.addActionListener(e -> seleccionarCarro(2));

        btnC1.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnC2.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnC3.setCursor(new Cursor(Cursor.HAND_CURSOR));
        aceptarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        setVisible(true);
    }

    private void seleccionarCarro(int index) {
        carroSeleccionado = index;
        mensajeLabel.setText("Coche seleccionado: C" + (index + 1));
    }

    class FondoPanel extends JPanel {
        private Image imagen;

        public FondoPanel() {
            try {
                imagen = ImageIO.read(getClass().getResource("/Apostar.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (imagen != null) {
                g.drawImage(imagen, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
