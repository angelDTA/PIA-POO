package org.example;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class Slot extends JFrame {

    ImageIcon[] symbolIcons;
    JLabel slot1, slot2, slot3;
    JLabel resultLabel;
    Timer spinTimer;
    int spinCount = 0;
    int maxSpins = 30;
    Random rand = new Random();
    Clip spinSound;
    double apuestaActual = 0;

    public Slot() {
        setTitle("🎰 Mini Slot");
        setSize(1024, 1024);
        setLayout(null);
        setResizable(false);

        symbolIcons = new ImageIcon[] {
                getScaledIcon("src/main/resources/img/sandia.png"),
                getScaledIcon("src/main/resources/img/siete.png"),
                getScaledIcon("src/main/resources/img/cereza.png"),
                getScaledIcon("src/main/resources/img/diamante.png"),
                getScaledIcon("src/main/resources/img/campana.png"),
        };

        // Fondo
        JLabel background = new JLabel(new ImageIcon("src/main/resources/img/SLOT.png"));
        background.setBounds(0, 0, 1024, 1024);
        background.setLayout(null);
        add(background);

        JLabel fondo = new JLabel(new ImageIcon("src/main/resources/img/Fondo.png"));
        fondo.setBounds(0, 0, 1024, 1024);
        fondo.setLayout(null);
        add(fondo);

        Color slotColor = new Color(40, 40, 40);

        // Crear los slots
        slot1 = createStyledSlot(slotColor);
        slot1.setBounds(281, 335, 150, 290);
        slot2 = createStyledSlot(slotColor);
        slot2.setBounds(432, 335, 140, 290);
        slot3 = createStyledSlot(slotColor);
        slot3.setBounds(572, 335, 140, 290);

        background.add(slot1);
        background.add(slot2);
        background.add(slot3);

        // JLabel para mostrar resultado debajo de los slots
        resultLabel = new JLabel("", SwingConstants.CENTER);
        resultLabel.setForeground(Color.YELLOW);
        resultLabel.setBounds(281, 140, 430, 50);

        // Cargar fuente personalizada
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/main/resources/casino.ttf")).deriveFont(40f);
            resultLabel.setFont(customFont);
        } catch (FontFormatException | IOException e) {
            resultLabel.setFont(new Font("Arial Black", Font.BOLD, 36));
        }

        background.add(resultLabel);

        JLabel spinButton = new JLabel();
        spinButton.setBounds(430, 710, 160, 60);
        spinButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        spinButton.setOpaque(false);

        spinButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (spinTimer != null && spinTimer.isRunning()) return;

                if (apuestaActual <= 0) {
                    JOptionPane.showMessageDialog(null, "Primero realiza una apuesta.");
                    return;
                }

                if (!Saldo.descontar(apuestaActual)) {
                    JOptionPane.showMessageDialog(null, "Saldo insuficiente para seguir apostando.");
                    return;
                }

                spinCount = 0;
                resultLabel.setText("");
                startSpinAnimation();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                spinButton.setToolTipText("¡Girar! 🎰");
            }
        });

        background.add(spinButton);

        JButton apostarBtn = new JButton("Apostar 💰");
        apostarBtn.setBounds(840, 20, 140, 40);
        apostarBtn.addActionListener(e -> {
            ApuestaGUI apuestaGUI = new ApuestaGUI(this);
            apuestaGUI.setVisible(true);
        });
        background.add(apostarBtn);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void setApuesta(double monto) {
        this.apuestaActual = monto;
    }

    private Clip playSound(String path, boolean loop) {
        return playSound(path, loop, 0);
    }

    private Clip playSound(String path, boolean loop, int durationMs) {
        try {
            File soundFile = new File(path);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();

            if (loop) clip.loop(Clip.LOOP_CONTINUOUSLY);

            if (durationMs > 0) {
                new Timer(durationMs, e -> {
                    clip.stop();
                    clip.close();
                }).start();
            }

            return clip;
        } catch (Exception e) {
            return null;
        }
    }

    private JLabel createStyledSlot(Color bgColor) {
        JLabel label = new JLabel();
        label.setOpaque(true);
        label.setBackground(bgColor);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setVerticalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 20), 2));
        return label;
    }

    /* Inicia la animación de giro de los rodillos de una máquina tragamonedas.
     Utiliza un temporizador para cambiar periódicamente los símbolos mostrados en los
     tres slots, simulando un efecto de giro. El giro se detiene automáticamente
     después de un número definido de repeticiones y luego evalúa el resultado.
     Detiene también el sonido del giro si está activo.
     */
    private void startSpinAnimation() {
        spinTimer = new Timer(100, e -> {
            ImageIcon icon1 = randomSymbol();
            ImageIcon icon2 = randomSymbol();
            ImageIcon icon3 = randomSymbol();

            slot1.setIcon(icon1);
            slot2.setIcon(icon2);
            slot3.setIcon(icon3);

            spinCount++;
            if (spinCount >= maxSpins) {
                spinTimer.stop();
                if (spinSound != null) spinSound.stop();
                checkResult();
            }
        });

        if (spinSound != null && spinSound.isRunning()) {
            spinSound.stop();
        }

        spinSound = playSound("src/main/resources/sonidos/play.wav", true);
        spinTimer.start();
    }

    private ImageIcon randomSymbol() {
        return symbolIcons[rand.nextInt(symbolIcons.length)];
    }

    private void checkResult() {
        Icon s1 = slot1.getIcon();
        Icon s2 = slot2.getIcon();
        Icon s3 = slot3.getIcon();

        if (s1.equals(s2) && s2.equals(s3)) {
            double multiplicador = 1.5;

            if (s1.toString().contains("campana")) multiplicador = 2.0;
            else if (s1.toString().contains("diamante")) multiplicador = 3.0;
            else if (s1.toString().contains("siete")) multiplicador = 5.0;

            double ganancia = apuestaActual * multiplicador;
            Saldo.agregarGanancia(ganancia);

            resultLabel.setText("🎉 ¡GANASTE! +" + String.format("$%.2f", ganancia));
            resultLabel.setForeground(Color.GREEN);
            playSound("src/main/resources/sonidos/ganar.wav", false, 3000);
        } else {
            resultLabel.setText("😢 Intenta de nuevo.");
            resultLabel.setForeground(Color.BLACK);
            playSound("src/main/resources/sonidos/pierde.wav", false);
        }
    }

    /**
     * @param path Ruta de la imagen que se desea cargar
     */
    private ImageIcon getScaledIcon(String path) {
        ImageIcon originalIcon = new ImageIcon(path);
        Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}
