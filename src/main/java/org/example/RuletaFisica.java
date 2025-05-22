package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.imageio.ImageIO;

public class RuletaFisica extends JPanel implements ActionListener {

    private static final String  IMG_PATH     = "/ruleta/RULETA.png";
    private static final String  BOLA_PATH    = "/ruleta/bola.png";
    private static final int     FPS          = 60;
    private static final int     DUR_MS       = 7000;
    private static final double  FACTOR_SURCO = 0.65;
    private static final double  OFFSET_IMG   = 0;

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

    public RuletaFisica() throws Exception {
        try (var in = getClass().getResourceAsStream(IMG_PATH)) {
            if (in == null) throw new IllegalStateException("No se encontró " + IMG_PATH);
            img = ImageIO.read(in);
        }

        try (var inPelota = getClass().getResourceAsStream(BOLA_PATH)) {
            if (inPelota == null) throw new IllegalStateException("No se encontró " + BOLA_PATH);
            imgPelota = ImageIO.read(inPelota);
        }

        setPreferredSize(new Dimension(img.getWidth(), img.getHeight() + 80));
        setBackground(Color.WHITE);
        setLayout(null);

        JButton btn = new JButton("GIRAR");
        btn.addActionListener(e -> iniciar());
        btn.setBounds((img.getWidth() - 100) / 2, img.getHeight() + 10, 100, 40);
        add(btn);
    }

    /**
     * Inicia la animación de la ruleta y la bola.
     * Configura los ángulos finales de giro para ambos, asegurando que
     * la ruleta complete vueltas completas y que la bola termine en una posición aleatoria.
     */

    private void iniciar() {
        // Evita reiniciar si ya está en marcha

        if (timer.isRunning()) return;
        finCorreccion = false;

        t0 = System.currentTimeMillis();

        ruletaAngF = 6 * 2 * Math.PI; // ruleta gira 6 vueltas y termina en misma posición
        bolaAng    = 0;
        bolaAngF   = 6 * 2 * Math.PI + rnd.nextDouble() * 2 * Math.PI;

        //donde empieza la pelota en este caso el borde
        bolaRad    = radioExterior;

        timer.start();
    }

    /**
     * Función de desaceleración cúbica (ease-out cubic).
     * Modela un frenado suave hacia el final del movimiento.
     *
     * @param t Progreso normalizado entre 0 y 1.
     * @return Valor ajustado del progreso para un efecto de desaceleración.
     */
    private static double easeOut(double t) {
        return 1 - Math.pow(1 - t, 3);
    }

    /**
     * Función de rebote (ease-out bounce).
     * Simula el efecto de rebote de la bola al frenar.
     *
     * @param t Progreso normalizado entre 0 y 1.
     * @return Valor ajustado con rebote para un efecto más realista.
     */

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
            alinearBolaASeccion();
            anunciarGanador();
            timer.stop();
        }
        repaint();
    }

    // Calcula la diferencia angular entre la bola y la ruleta (ajustada por un offset),
    // normalizándola al rango [0, 2π), para alinear la bola con la sección correspondiente de la ruleta.
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
        int numero = NUMEROS[indice];

        System.out.println("Índice: " + indice + "  Número: " + numero);

        JOptionPane.showMessageDialog(this,
                "¡Ganó el número " + numero + "!",
                "Resultado", JOptionPane.INFORMATION_MESSAGE);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        if (centro == null) {
            centro = new Point2D.Double(getWidth() / 2.0, img.getHeight() / 2.0);
            radioExterior = img.getWidth() / 2.0 * 0.96;
            radioSurco = img.getWidth() / 2.0 * FACTOR_SURCO;
            bolaRad = radioExterior;
        }

        // Aplica una rotación al contexto gráfico alrededor del centro de la ruleta para dibujar la imagen girada,
        // luego restaura la transformación original para no afectar el resto del dibujo.
        AffineTransform old = g2.getTransform();
        g2.translate(centro.getX(), centro.getY());
        g2.rotate(ruletaAng);
        g2.drawImage(img, -img.getWidth() / 2, -img.getHeight() / 2, this);
        g2.setTransform(old);

        double bx = centro.getX() + bolaRad * Math.sin(bolaAng);
        double by = centro.getY() - bolaRad * Math.cos(bolaAng);

        //Aqui se calcula el tamaño de la bola y lo modificamos para que sea mas pequeña que la ruleta
        if (imgPelota != null) {
            double escala = 0.03;
            int bolaW = (int) (imgPelota.getWidth() * escala);
            int bolaH = (int) (imgPelota.getHeight() * escala);
            g2.drawImage(imgPelota, (int) bx - bolaW / 2, (int) by - bolaH / 2, bolaW, bolaH, this);        // Dibuja la bola centrada en las coordenadas calculadas (bx, by)
        }

        g2.dispose();
    }
    public class RuletaVentana {
        public static void main(String[] args) {
            SwingUtilities.invokeLater(() -> {
                try {
                    JFrame f = new JFrame("Ruleta física");
                    f.setContentPane(new RuletaFisica());
                    f.pack();
                    f.setLocationRelativeTo(null);
                    f.setVisible(true);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al iniciar la ruleta:\n" + ex.getMessage());
                }
            });
        }
    }

}