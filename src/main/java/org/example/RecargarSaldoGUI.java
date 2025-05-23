package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RecargarSaldoGUI extends JFrame {

    private JLabel saldoLabel;
    private JTextField montoField;
    private JButton recargarButton;

    public RecargarSaldoGUI() {
        setTitle("Recargar Saldo");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        saldoLabel = new JLabel("Saldo actual: " + Saldo.getSaldo());
        saldoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        saldoLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JLabel montoLabel = new JLabel("Monto a recargar:");
        montoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        montoField = new JTextField();
        montoField.setMaximumSize(new Dimension(200, 30));
        montoField.setHorizontalAlignment(JTextField.CENTER);

        recargarButton = new JButton("Recargar");
        recargarButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        recargarButton.setFocusPainted(false);

        recargarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recargarSaldo();
            }
        });


        panelCentral.add(saldoLabel);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 15)));
        panelCentral.add(montoLabel);
        panelCentral.add(montoField);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 15)));
        panelCentral.add(recargarButton);

        add(panelCentral, BorderLayout.CENTER);
        setVisible(true);
    }

    private void recargarSaldo() {
        try {
            double monto = Double.parseDouble(montoField.getText());
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Saldo.recargar(monto);
            saldoLabel.setText(String.format("Saldo actual: $%.2f", Saldo.getSaldo()));
            montoField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor ingresa un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


}
