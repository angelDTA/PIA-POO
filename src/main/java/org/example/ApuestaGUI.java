package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ApuestaGUI extends JFrame {

    private JLabel saldoLabel;
    private JTextField apuestaField;
    private JButton apostarButton;
    private Slot slotRef;

    public ApuestaGUI(Slot slot) {
        this.slotRef = slot;
        setTitle("Realizar Apuesta");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        saldoLabel = new JLabel(String.format("Saldo actual: $%.2f", Saldo.getSaldo()));
        saldoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        saldoLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JLabel apuestaLabel = new JLabel("Monto a apostar:");
        apuestaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        apuestaField = new JTextField();
        apuestaField.setMaximumSize(new Dimension(200, 30));
        apuestaField.setHorizontalAlignment(JTextField.CENTER);

        apostarButton = new JButton("Apostar");
        apostarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        apostarButton.setFocusPainted(false);

        apostarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                realizarApuesta();
            }
        });

        panelCentral.add(saldoLabel);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 15)));
        panelCentral.add(apuestaLabel);
        panelCentral.add(apuestaField);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 15)));
        panelCentral.add(apostarButton);

        add(panelCentral, BorderLayout.CENTER);
    }

    private void realizarApuesta() {
        try {
            double monto = Double.parseDouble(apuestaField.getText());
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (Saldo.getSaldo() < monto) {
                JOptionPane.showMessageDialog(this, "Saldo insuficiente.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }


            saldoLabel.setText(String.format("Saldo actual: $%.2f", Saldo.getSaldo()));
            apuestaField.setText("");
            slotRef.setApuesta(monto);
            JOptionPane.showMessageDialog(this, "¡Apuesta realizada con éxito!");
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
