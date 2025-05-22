package org.example;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class BlackJack {
    private class Carta {
        String valor;
        String tipo;

        Carta(String valor, String tipo) {
            this.valor = valor;
            this.tipo = tipo;
        }

        public String toString() {
            return valor + "-" + tipo;
        }

        public int getValor() {
            if ("AJQK".contains(valor)) {
                if (valor.equals("A")) {
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(valor);
        }

        public boolean isAce() {
            return valor.equals("A");
        }

        public String rutaImagen() {
            return "/cards/" + toString() + ".png";
        }
    }

    ArrayList<Carta> baraja;
    Random rand = new Random();

    Carta CartaOculta;
    ArrayList<Carta> manoDealer;
    int dealerSum;
    int dealerACE;

    ArrayList<Carta> manoJugador;
    int jugadorSum;
    int jugadorACE;

    int ancho = 600;
    int alutra = ancho;
    int anchoCarta = 110;
    int altoCarta = 154;

    JFrame frame = new JFrame("Black Jack");
    JPanel gamePanel;
    JPanel botPanel = new JPanel();
    JButton presButton = new JButton("Pulsar");
    JButton stayButtton = new JButton("Quedarse");
    JButton resetButton = new JButton("Nueva Partida");

    boolean partidaTerminada = false;
    String mensajeFinal = "";

    BlackJack() {
        gamePanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);

                //Estos metodos sirven para dibujar las cartas tanto del dealer como del jugador, tambien dibuja la carta oculta del dealer
                try {
                    Image cartaOcultaIm = new ImageIcon(getClass().getResource("/cards/BACK.png")).getImage();
                    if (!stayButtton.isEnabled()) {
                        cartaOcultaIm = new ImageIcon(getClass().getResource(CartaOculta.rutaImagen())).getImage();
                    }
                    g.drawImage(cartaOcultaIm, 20, 20, anchoCarta, altoCarta, null);

                    for (int i = 0; i < manoDealer.size(); i++) {
                        Carta carta = manoDealer.get(i);
                        Image cartImage = new ImageIcon(getClass().getResource(carta.rutaImagen())).getImage();
                        g.drawImage(cartImage, anchoCarta + 25 + (anchoCarta + 5) * i, 20, anchoCarta, altoCarta, null);
                    }

                    for (int i = 0; i < manoJugador.size(); i++) {
                        Carta carta = manoJugador.get(i);
                        Image cartImage = new ImageIcon(getClass().getResource(carta.rutaImagen())).getImage();
                        g.drawImage(cartImage, 20 + (anchoCarta + 5) * i, 320, anchoCarta, altoCarta, null);
                    }

                    if (!stayButtton.isEnabled() && !partidaTerminada) {
                        dealerACE = redAceD();
                        jugadorACE = redAceJd();

                        if (jugadorSum > 21) {
                            mensajeFinal = "Perdiste";
                        } else if (dealerSum > 21) {
                            mensajeFinal = "Ganaste";
                        } else if (jugadorSum == dealerSum) {
                            mensajeFinal = "Empate";
                        } else if (jugadorSum > dealerSum) {
                            mensajeFinal = "Ganaste";
                        } else {
                            mensajeFinal = "Perdiste";
                        }

                        g.setFont(new Font("Othello", Font.PLAIN, 30));
                        g.setColor(Color.white);
                        g.drawString(mensajeFinal, 220, 250);

                        partidaTerminada = true;
                        resetButton.setEnabled(true);

                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(frame, mensajeFinal, "Resultado", JOptionPane.INFORMATION_MESSAGE);
                        });
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        startGame();

        frame.setVisible(true);
        frame.setSize(ancho, alutra);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

        presButton.setFocusPainted(false);
        botPanel.add(presButton);

        stayButtton.setFocusPainted(false);
        botPanel.add(stayButtton);

        resetButton.setFocusPainted(false);
        resetButton.setEnabled(false);
        botPanel.add(resetButton);

        frame.add(botPanel, BorderLayout.SOUTH);

        presButton.addActionListener(e -> {
            Carta carta = baraja.remove(baraja.size() - 1);
            jugadorSum += carta.getValor();
            jugadorACE += carta.isAce() ? 1 : 0;
            manoJugador.add(carta);
            if (redAceJd() > 21) {
                presButton.setEnabled(false);
                stayButtton.setEnabled(false);
            }
            gamePanel.repaint();
        });

        stayButtton.addActionListener(e -> {
            presButton.setEnabled(false);
            stayButtton.setEnabled(false);

            while (dealerSum < 17) {
                Carta carta = baraja.remove(baraja.size() - 1);
                dealerSum += carta.getValor();
                dealerACE += carta.isAce() ? 1 : 0;
                manoDealer.add(carta);
            }

            gamePanel.repaint();
        });

        resetButton.addActionListener(e -> {
            presButton.setEnabled(true);
            stayButtton.setEnabled(true);
            resetButton.setEnabled(false);
            partidaTerminada = false;
            mensajeFinal = "";
            startGame();
            gamePanel.repaint();
        });

        gamePanel.repaint();
    }

    public void startGame() {
        buildbBraja();
        barajearBaraja();

        manoDealer = new ArrayList<>();
        dealerSum = 0;
        dealerACE = 0;

        CartaOculta = baraja.remove(baraja.size() - 1);
        dealerSum += CartaOculta.getValor();
        dealerACE += CartaOculta.isAce() ? 1 : 0;

        Carta carta = baraja.remove(baraja.size() - 1);
        dealerSum += carta.getValor();
        dealerACE += carta.isAce() ? 1 : 0;
        manoDealer.add(carta);

        manoJugador = new ArrayList<>();
        jugadorSum = 0;
        jugadorACE = 0;

        for (int i = 0; i < 2; i++) {
            carta = baraja.remove(baraja.size() - 1);
            jugadorSum += carta.getValor();
            jugadorACE += carta.isAce() ? 1 : 0;
            manoJugador.add(carta);
        }
    }

    public void buildbBraja() {
        baraja = new ArrayList<>();
        String[] valores = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};
        String[] tipos = {"C", "D", "H", "S"};

        for (String tipo : tipos) {
            for (String valor : valores) {
                baraja.add(new Carta(valor, tipo));
            }
        }
    }

    public void barajearBaraja() {
        for (int i = 0; i < baraja.size(); i++) {
            int j = rand.nextInt(baraja.size());
            Carta temp = baraja.get(i);
            baraja.set(i, baraja.get(j));
            baraja.set(j, temp);
        }
    }

    //en ester metodo ajuta el valor de las ases si el valor supera 21

    public int redAceJd() {
        while (jugadorSum > 21 && jugadorACE > 0) {
            jugadorSum -= 10;
            jugadorACE -= 1;
        }
        return jugadorSum;
    }
    //en ester caso lo mismo pero apra el dealer
    public int redAceD() {
        while (dealerSum > 21 && dealerACE > 0) {
            dealerSum -= 10;
            dealerACE -= 1;
        }
        return dealerSum;
    }

}