package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;


public class MENU extends JPanel implements MouseListener, MouseMotionListener {

    private Image background;

    private final Rectangle blackjackRect = new Rectangle(80, 180, 250, 200);
    private final Rectangle ruletaRect = new Rectangle(370, 180, 250, 200);
    private final Rectangle tragamonedasRect = new Rectangle(80, 400, 250, 200);
    private final Rectangle caballosRect = new Rectangle(370, 400, 250, 200);
    private final Rectangle recargarRect = new Rectangle(370, 40, 220, 40);

    private Clip clip;
    private FloatControl volumeControl;

    private void reproducirMusica() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    getClass().getResource("/sonidos/ambiente.wav")
            );
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();

            // Obtener control de volumen
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error cargando música: " + e.getMessage());
        }
    }

    public MENU() {
        this.setPreferredSize(new Dimension(700, 700));
        this.background = new ImageIcon("src/main/resources/img/menu.png").getImage();
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        reproducirMusica();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, this.getWidth(), this.getHeight(), this);

    }

    public void setVolume(float decibels) {
        if (volumeControl != null) {
            volumeControl.setValue(decibels);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if (blackjackRect.contains(x, y)) {
            setVolume(-15.0f);
            BlackJack blackjack = new BlackJack();
        } else if (ruletaRect.contains(x, y)) {
            setVolume(-15.0f);
            try {
                RuletaFisica ruleta = new RuletaFisica();
                JFrame ventanaRuleta = new JFrame("Ruleta");
                ventanaRuleta.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                ventanaRuleta.setContentPane(ruleta);
                ventanaRuleta.pack();
                ventanaRuleta.setLocationRelativeTo(null);
                ventanaRuleta.setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al abrir la ruleta: " + ex.getMessage());
            }
        } else if (tragamonedasRect.contains(x, y)) {
            setVolume(-15.0f);
            Slot slot = new Slot();
        } else if (caballosRect.contains(x, y)) {
            setVolume(-15.0f);
            Ventana ventana = new Ventana();
        }
        else if (recargarRect.contains(x, y)) {
            new RecargarSaldoGUI();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();

        if (blackjackRect.contains(x, y) || ruletaRect.contains(x, y) ||
                tragamonedasRect.contains(x, y) || caballosRect.contains(x, y) || recargarRect.contains(x, y) ) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Casino Royale");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MENU());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
