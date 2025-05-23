package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

class Hilo implements Runnable {
    Thread hilo;
    String nombre;
    JLabel personaje;
    JLabel labelfinal;
    Ventana ventana;

    public static int lugar = 1;

    public Hilo(String nombre, JLabel personaje, JLabel labelfinal, Ventana ventana) {
        this.nombre = nombre;
        this.personaje = personaje;
        this.labelfinal = labelfinal;
        this.ventana = ventana;
        hilo = new Thread(this, nombre);
        hilo.start();
    }

    @Override
    public void run() {
        int retardo;

        try {
            retardo = (int) (Math.random() * 5) + 1;
            labelfinal.setVisible(false);
            personaje.setVisible(true);

            for (int i = personaje.getX(); i < 900; i++) {
                personaje.setLocation(i, personaje.getY());
                Thread.sleep(retardo);
            }

            personaje.setVisible(false);
            labelfinal.setText(nombre + " ha llegado en la posición: " + lugar);
            labelfinal.setVisible(true);
            ventana.posiciones[lugar - 1] = nombre;

            lugar++;

            ventana.carrilTerminado();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}

class Ventana extends JFrame {
    JButton botonjInicio;
    int corredoresTerminados = 0;
    public int apuestaCarro = -1;
    public double montoApostado = 0;
    String[] posiciones = new String[3];

    public Ventana() {
        super("Carrera de caballos");

        setSize(1000, 450);
        setLayout(null);

        JLabel background = new JLabel(new ImageIcon("src/main/resources/carrera/pista.png"));
        background.setBounds(0, 0, 1000, 400);
        background.setLayout(null);
        add(background);

        Image imagen_C1 = new ImageIcon("src/main/resources/carrera/C1.png").getImage();
        ImageIcon Icon_C1 = new ImageIcon(imagen_C1.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        JLabel C1 = new JLabel(Icon_C1);
        C1.setBounds(50, 50, 60, 60);
        C1.setOpaque(false);

        JLabel C1_pos = new JLabel();
        C1_pos.setBounds(50, 120, 350, 20);
        C1_pos.setForeground(Color.WHITE);
        C1_pos.setVisible(false);

        Image imagen_C2 = new ImageIcon("src/main/resources/carrera/C2.png").getImage();
        ImageIcon Icon_C2 = new ImageIcon(imagen_C2.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        JLabel C2 = new JLabel(Icon_C2);
        C2.setBounds(50, 150, 60, 60);
        C2.setOpaque(false);

        JLabel C2_pos = new JLabel();
        C2_pos.setBounds(50, 210, 350, 20);
        C2_pos.setForeground(Color.WHITE);
        C2_pos.setVisible(false);

        Image imagen_C3 = new ImageIcon("src/main/resources/carrera/C3.png").getImage();
        ImageIcon Icon_C3 = new ImageIcon(imagen_C3.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        JLabel C3 = new JLabel(Icon_C3);
        C3.setBounds(50, 290, 60, 60);
        C3.setOpaque(false);

        JLabel C3_pos = new JLabel();
        C3_pos.setBounds(50, 350, 350, 20);
        C3_pos.setForeground(Color.WHITE);
        C3_pos.setVisible(false);

        botonjInicio = new JButton();
        botonjInicio.setBounds(200, 350, 280, 40);
        botonjInicio.setOpaque(false);
        botonjInicio.setContentAreaFilled(false);
        botonjInicio.setBorderPainted(false);
        botonjInicio.setFocusPainted(false);
        botonjInicio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        botonjInicio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                botonjInicio.setEnabled(false);

                //aqui reproduce la musica de inicio antes de lanzar los hilos
                new Thread(() -> {
                    reproducirSonido("src/main/resources/sonidos/inicio.wav");

                    try {
                        Thread.sleep(4000); // Espera mientras se reproduce el sonido
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }

                    SwingUtilities.invokeLater(() -> {
                        Hilo.lugar = 1;
                        corredoresTerminados = 0;
                        posiciones = new String[3];

                        C1.setLocation(50, C1.getY());
                        C2.setLocation(50, C2.getY());
                        C3.setLocation(50, C3.getY());

                        C1_pos.setVisible(false);
                        C2_pos.setVisible(false);
                        C3_pos.setVisible(false);

                        new Hilo("C1", C1, C1_pos, Ventana.this);
                        new Hilo("C2", C2, C2_pos, Ventana.this);
                        new Hilo("C3", C3, C3_pos, Ventana.this);
                    });
                }).start();
            }
        });

        JButton botonApostar = new JButton();
        botonApostar.setBounds(530, 350, 280, 40);
        botonApostar.setOpaque(false);
        botonApostar.setContentAreaFilled(false);
        botonApostar.setBorderPainted(false);
        botonApostar.setFocusPainted(false);
        botonApostar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        botonApostar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CarreraApostarGUI(Ventana.this);
            }
        });

        background.add(C1); background.add(C1_pos);
        background.add(C2); background.add(C2_pos);
        background.add(C3); background.add(C3_pos);
        background.add(botonjInicio);
        background.add(botonApostar);

        setVisible(true);
    }

    public synchronized void carrilTerminado() {
        corredoresTerminados++;

        if (corredoresTerminados == 3) {
            botonjInicio.setEnabled(true);

            if (apuestaCarro != -1 && montoApostado > 0) {
                String carroApostado = "C" + (apuestaCarro + 1);

                if (posiciones[0].equals(carroApostado)) {
                    double ganancia = montoApostado * 3;
                    Saldo.agregarGanancia(ganancia);
                    JOptionPane.showMessageDialog(this,
                            "¡Felicidades! Tu coche " + carroApostado + " ganó.\nGanaste $" + ganancia,
                            "¡Ganaste!", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Lo siento, tu coche " + carroApostado + " no ganó.\nPerdiste $" + montoApostado,
                            "Perdiste", JOptionPane.ERROR_MESSAGE);
                }
            }

            montoApostado = 0;
            apuestaCarro = -1;
            posiciones = new String[3];
        }
    }

    public void setApuesta(int carro, double monto) {
        this.apuestaCarro = carro;
        this.montoApostado = monto;
        System.out.println("Apuesta al coche C" + (carro + 1) + " por $" + monto);
    }

    private void reproducirSonido(String ruta) {
        try {
            File archivoSonido = new File(ruta);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoSonido);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("No se pudo reproducir el sonido: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new Ventana();
    }
}
