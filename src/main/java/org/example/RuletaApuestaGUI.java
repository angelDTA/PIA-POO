package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class RuletaApuestaGUI extends JFrame {

    private RuletaFisica ruleta;

    private JTextField apuestaField;
    private JComboBox<String> tipoApuestaCombo;
    private JTextField numeroField;

    public RuletaApuestaGUI(RuletaFisica ruleta) {
        this.ruleta = ruleta;

        setTitle("Realizar Apuesta");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,8,8,8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Monto a apostar
        gbc.gridx = 0; gbc.gridy = 0;
        add(new JLabel("Monto a apostar:"), gbc);

        apuestaField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        add(apuestaField, gbc);

        // Tipo de apuesta
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Tipo de apuesta:"), gbc);

        tipoApuestaCombo = new JComboBox<>(new String[]{"numero", "rojo", "negro", "verde"});
        gbc.gridx = 1; gbc.gridy = 1;
        add(tipoApuestaCombo, gbc);

        // Número apostado (solo visible si tipo = numero)
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Número (0-36):"), gbc);

        numeroField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        add(numeroField, gbc);

        // Actualizar visibilidad del campo número según tipo de apuesta
        tipoApuestaCombo.addActionListener(e -> {
            if ("numero".equals(tipoApuestaCombo.getSelectedItem())) {
                numeroField.setEnabled(true);
            } else {
                numeroField.setEnabled(false);
                numeroField.setText("");
            }
        });

        JButton btnApostar = new JButton("Confirmar Apuesta");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        add(btnApostar, gbc);

        btnApostar.addActionListener(e -> realizarApuesta());
    }

    private void realizarApuesta() {
        try {
            double monto = Double.parseDouble(apuestaField.getText());
            if (monto <= 0) {
                JOptionPane.showMessageDialog(this, "El monto debe ser mayor a cero.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String tipo = (String) tipoApuestaCombo.getSelectedItem();
            int numero = -1;
            if ("numero".equals(tipo)) {
                String numStr = numeroField.getText();
                if (numStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe ingresar un número para la apuesta.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                numero = Integer.parseInt(numStr);
                if (numero < 0 || numero > 36) {
                    JOptionPane.showMessageDialog(this, "El número debe estar entre 0 y 36.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if (monto > Saldo.getSaldo()) {
                JOptionPane.showMessageDialog(this, "Saldo Insuficiente", "OK", JOptionPane.INFORMATION_MESSAGE);

            }else{
                ruleta.setApuesta(monto, tipo, numero);
                JOptionPane.showMessageDialog(this, "Apuesta realizada correctamente.", "OK", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ingrese un número válido en el monto y número de apuesta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
