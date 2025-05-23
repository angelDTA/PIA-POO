package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;


public class RuletaFisica extends JPanel implements ActionListener {

    private static final String  IMG_PATH     = "/ruleta/RULETA.png";
    private static final String  BOLA_PATH    = "/ruleta/bola.png";
    private static final int     FPS          = 60;
    private static final int     DUR_MS       = 7000;
    private static final double  FACTOR_SURCO = 0.65;
    private static final double  OFFSET_IMG   = 0;

    private Clip clipGiro;

    private static final int[] NUMEROS = { 0,32,15,19,4,21,2,25,17,34,6,27,13,36,11,
            30,8,23,10,5,24,16,33,1,20,14,31,9,22,18,
            29,7,28,12,35,3,26 };
    private static final int SECTORES = NUMEROS.length;

    private final BufferedImage img;
    private final BufferedImage imgPelota;
    private final Timer timer = new Timer(1000 / FPS, this);
    private final Random rnd  = new Random();

    private long   t0;
    private double ruletaAngF, bolaAngF;
    private boolean finCorreccion = false;

    private Point2D centro;
    private double  radioExterior;
    private double  radioSurco;
    private double  ruletaAng, bolaAng, bolaRad;

    private double montoApuesta = 0;
    private String tipoApuesta = null;
    private int numeroApostado = -1;

    private void reproducirMusicaGiro() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    getClass().getResource("/sonidos/Ruletita.wav")
            );
            clipGiro = AudioSystem.getClip();
            clipGiro.open(audioInputStream);
            clipGiro.start();
            clipGiro.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void detenerMusicaGiro() {
        if (clipGiro != null && clipGiro.isRunning()) {
            clipGiro.stop();
            clipGiro.close();
        }
    }

    public RuletaFisica() throws Exception {
        try (var in = getClass().getResourceAsStream(IMG_PATH)) {
            if (in == null) throw new IllegalStateException("No se encontró " + IMG_PATH);
            img = ImageIO.read(in);
        }

        try (var inPelota = getClass().getResourceAsStream(BOLA_PATH)) {
            if (inPelota == null) throw new IllegalStateException("No se encontró " + BOLA_PATH);
            imgPelota = ImageIO.read(inPelota);
        }

        setPreferredSize(new Dimension(img.getWidth(), img.getHeight() + 110));
        setBackground(Color.WHITE);
        setLayout(null);

        JButton btnGirar = new JButton("GIRAR");
        btnGirar.addActionListener(e -> iniciar());
        btnGirar.setBounds((img.getWidth() - 100) / 2, img.getHeight() + 10, 100, 40);
        add(btnGirar);

        JButton btnApostar = new JButton("APOSTAR");
        btnApostar.setBounds((img.getWidth() - 100) / 2, img.getHeight() + 60, 100, 40);
        btnApostar.addActionListener(e -> abrirGUIApuesta());
        add(btnApostar);
    }

    private void abrirGUIApuesta() {
        SwingUtilities.invokeLater(() -> {
            new RuletaApuestaGUI(this).setVisible(true);
        });
    }

    public void setApuesta(double monto, String tipo, int numero) {
        this.montoApuesta = monto;
        this.tipoApuesta = tipo;
        this.numeroApostado = numero;
        System.out.println("Apuesta recibida: $" + monto + ", tipo: " + tipo + ", numero: " + numero);
    }

    private void iniciar() {
        if (timer.isRunning()) {
            JOptionPane.showMessageDialog(this, "La ruleta ya está girando.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (montoApuesta <= 0 || tipoApuesta == null) {
            JOptionPane.showMessageDialog(this, "Por favor, realiza una apuesta antes de girar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Saldo.descontar(montoApuesta)) {
            JOptionPane.showMessageDialog(this, "Saldo insuficiente para esa apuesta.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        finCorreccion = false;
        t0 = System.currentTimeMillis();

        ruletaAngF = 6 * 2 * Math.PI;
        bolaAng    = 0;
        bolaAngF   = 6 * 2 * Math.PI + rnd.nextDouble() * 2 * Math.PI;
        bolaRad    = radioExterior;
        reproducirMusicaGiro();
        timer.start();
    }

    private static double easeOut(double t) {
        return 1 - Math.pow(1 - t, 3);
    }

    private static double easeOutBounce(double t){
        if (t < 4/11.0)        return (121*t*t)/16;
        if (t < 8/11.0)        return (363/40.0*t*t)-(99/10.0*t)+17/5.0;
        if (t < 9/10.0)        return (4356/361.0*t*t)-(35442/1805.0*t)+16061/1805.0;
        return (54/5.0*t*t)-(513/25.0*t)+268/25.0;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        double t = Math.min(1, (System.currentTimeMillis() - t0) / (double) DUR_MS);

        ruletaAng = easeOut(t) * ruletaAngF;
        bolaAng   = easeOutBounce(t) * bolaAngF;
        bolaRad   = radioExterior - (radioExterior - radioSurco) * t;

        if (t >= 1 && !finCorreccion) {
            finCorreccion = true;
            detenerMusicaGiro();
            alinearBolaASeccion();
            anunciarGanador();
            timer.stop();
        }
        repaint();
    }

    private void alinearBolaASeccion() {
        double tamañoSector = 2 * Math.PI / SECTORES;
        double diff = (bolaAng - ruletaAng - OFFSET_IMG) % (2 * Math.PI);
        if (diff < 0) diff += 2 * Math.PI;

        int indice = (int) Math.round(diff / tamañoSector) % SECTORES;
        bolaAng = ruletaAng + OFFSET_IMG + indice * tamañoSector;
    }

    private void anunciarGanador() {
        double tamañoSector = 2 * Math.PI / SECTORES;
        double angRelativo = (bolaAng - ruletaAng) % (2 * Math.PI);
        if (angRelativo < 0) angRelativo += 2 * Math.PI;

        int indice = (int) Math.round(angRelativo / tamañoSector) % SECTORES;
        int numeroGanador = NUMEROS[indice];

        System.out.println("Número ganador: " + numeroGanador);

        String colorGanador = obtenerColor(numeroGanador);

        JOptionPane.showMessageDialog(this,
                "¡Ganó el número " + numeroGanador + " (" + colorGanador + ")!",
                "Resultado", JOptionPane.INFORMATION_MESSAGE);

        double ganancia = calcularPago(numeroGanador, colorGanador);
        if (ganancia > 0) {
            Saldo.agregarGanancia(ganancia);
            JOptionPane.showMessageDialog(this, String.format("¡Ganaste $%.2f!", ganancia), "Ganancia", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "No ganaste esta vez.", "Resultado", JOptionPane.INFORMATION_MESSAGE);
        }

        montoApuesta = 0;
        tipoApuesta = null;
        numeroApostado = -1;
    }

    private String obtenerColor(int numero) {
        if (numero == 0) return "verde";

        int[] rojos = {32, 19, 21, 25, 34, 27, 36, 30, 23, 5, 16, 1, 14, 9, 18, 7, 12, 3, 20, 29};
        for (int r : rojos) {
            if (r == numero) return "rojo";
        }
        return "negro";
    }

    private double calcularPago(int numeroGanador, String colorGanador) {
        if (tipoApuesta == null) return 0;

        switch (tipoApuesta) {
            case "numero":
                if (numeroGanador == numeroApostado) {
                    return montoApuesta * 35;
                }
                break;
            case "rojo":
                if ("rojo".equals(colorGanador)) {
                    return montoApuesta * 2;
                }
                break;
            case "negro":
                if ("negro".equals(colorGanador)) {
                    return montoApuesta * 2;
                }
                break;
            case "verde":
                if ("verde".equals(colorGanador)) {
                    return montoApuesta * 35;
                }
                break;
        }
        return 0;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        if (centro == null) {
            centro        = new Point2D.Double(getWidth() / 2.0, img.getHeight() / 2.0);
            radioExterior = img.getWidth() / 2.0 * 0.96;
            radioSurco    = img.getWidth() / 2.0 * FACTOR_SURCO;
            bolaRad       = radioExterior;
        }

        AffineTransform old = g2.getTransform();
        g2.translate(centro.getX(), centro.getY());
        g2.rotate(ruletaAng);
        g2.drawImage(img, -img.getWidth() / 2, -img.getHeight() / 2, this);
        g2.setTransform(old);

        double bx = centro.getX() + bolaRad * Math.sin(bolaAng);
        double by = centro.getY() - bolaRad * Math.cos(bolaAng);

        if (imgPelota != null) {
            double escala = 0.03;
            int bolaW = (int) (imgPelota.getWidth() * escala);
            int bolaH = (int) (imgPelota.getHeight() * escala);
            g2.drawImage(imgPelota, (int) (bx - bolaW / 2), (int) (by - bolaH / 2), bolaW, bolaH, this);
        }

        g2.dispose();

        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String saldoTexto = String.format("Saldo: $%.2f", Saldo.getSaldo());
        g.drawString(saldoTexto, 10, getHeight() - 10);
    }
}
