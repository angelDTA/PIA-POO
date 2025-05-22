package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
            //en esta parte configuramos la velocidad para que sea aleatoria y simule movimiento
            retardo = (int)(Math.random() * 5) + 1;
            labelfinal.setVisible(false);
            personaje.setVisible(true);

            for (int i = personaje.getX(); i < 900; i++) {
                personaje.setLocation(i, personaje.getY());
                Thread.sleep(retardo);
            }

            personaje.setVisible(false);
            labelfinal.setText(nombre + " ha llegado en la posición: " + lugar);
            labelfinal.setVisible(true);
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

    public Ventana() {
        super("Carrera de caballos");

        setSize(1000, 450);
        setLayout(null);


        JLabel background = new JLabel(new ImageIcon("src/main/resources/carrera/pista.png"));
        background.setBounds(0, 0, 1000, 400);
        background.setLayout(null);
        add(background);

        // Carro C1
        Image imagen_C1 = new ImageIcon("src/main/resources/carrera/C1.png").getImage();
        ImageIcon Icon_C1 = new ImageIcon(imagen_C1.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        JLabel C1 = new JLabel(Icon_C1);
        C1.setBounds(50, 50, 60, 60);
        C1.setOpaque(false);

        JLabel C1_pos = new JLabel();
        C1_pos.setBounds(50, 120, 350, 20);
        C1_pos.setForeground(Color.WHITE);
        C1_pos.setVisible(false);

        // Carro C2
        Image imagen_C2 = new ImageIcon("src/main/resources/carrera/C2.png").getImage();
        ImageIcon Icon_C2 = new ImageIcon(imagen_C2.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        JLabel C2 = new JLabel(Icon_C2);
        C2.setBounds(50, 150, 60, 60);
        C2.setOpaque(false);

        JLabel C2_pos = new JLabel();
        C2_pos.setBounds(50, 210, 350, 20);
        C2_pos.setForeground(Color.WHITE);
        C2_pos.setVisible(false);

        // Carro C3
        Image imagen_C3 = new ImageIcon("src/main/resources/carrera/C3.png").getImage();
        ImageIcon Icon_C3 = new ImageIcon(imagen_C3.getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        JLabel C3 = new JLabel(Icon_C3);
        C3.setBounds(50, 290, 60, 60);
        C3.setOpaque(false);

        JLabel C3_pos = new JLabel();
        C3_pos.setBounds(50, 350, 350, 20);
        C3_pos.setForeground(Color.WHITE);
        C3_pos.setVisible(false);

        // Botón de inicio
        botonjInicio = new JButton();
        botonjInicio.setBounds(200, 350, 280, 40);
        botonjInicio.setOpaque(false);
        botonjInicio.setContentAreaFilled(false);
        botonjInicio.setBorderPainted(false);
        botonjInicio.setFocusPainted(false);
        botonjInicio.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        //Aqui es la accion al pulsar el boton reinicia la carrera y lanza los hilos

        botonjInicio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Hilo.lugar = 1;
                corredoresTerminados = 0;
                botonjInicio.setEnabled(false);

                C1.setLocation(50, C1.getY());
                C2.setLocation(50, C2.getY());
                C3.setLocation(50, C3.getY());

                C1_pos.setVisible(false);
                C2_pos.setVisible(false);
                C3_pos.setVisible(false);

                new Hilo("C1", C1, C1_pos, Ventana.this);
                new Hilo("C2", C2, C2_pos, Ventana.this);
                new Hilo("C3", C3, C3_pos, Ventana.this);
            }
        });

        background.add(C1);
        background.add(C1_pos);
        background.add(C2);
        background.add(C2_pos);
        background.add(C3);
        background.add(C3_pos);
        background.add(botonjInicio);

        setVisible(true);
    }
    // Se llama cada vez que un corredor termina. Cuando los tres han terminado, se habilita de nuevo el botón.

    public synchronized void carrilTerminado() {
        corredoresTerminados++;
        if (corredoresTerminados == 3) {
            botonjInicio.setEnabled(true);
        }
    }

}