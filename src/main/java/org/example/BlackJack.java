package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;

public class BlackJack {

    boolean juegoActivo = false;

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
    int altura = ancho;
    int anchoCarta = 110;
    int altoCarta = 154;

    JFrame frame = new JFrame("Black Jack");
    JPanel gamePanel;
    JPanel botPanel = new JPanel();

    JButton presButton = new JButton("Pulsar");
    JButton stayButtton = new JButton("Quedarse");
    JButton resetButton = new JButton("Nueva Partida");

    JLabel saldoLabel = new JLabel("Saldo: $" + Saldo.getSaldo());
    JTextField apuestaField = new JTextField("10", 5);
    double apuestaActual = 0;

    boolean partidaTerminada = false;
    String mensajeFinal = "";

    public BlackJack() {
        gamePanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);

                try {
                    if (manoDealer == null || manoJugador == null) return;

                    // Estos métodos sirven para dibujar las cartas tanto del dealer como del jugador,
                    // también dibuja la carta oculta del dealer
                    Image cartaOcultaIm = new ImageIcon(getClass().getResource("/cards/BACK.png")).getImage();
                    if (!stayButtton.isEnabled() && CartaOculta != null) {
                        cartaOcultaIm = new ImageIcon(getClass().getResource(CartaOculta.rutaImagen())).getImage();
                    }
                    g.drawImage(cartaOcultaIm, 20, 20, anchoCarta, altoCarta, null);

                    for (int i = 0; i < manoDealer.size(); i++) {
                        Carta carta = manoDealer.get(i);
                        Image img = new ImageIcon(getClass().getResource(carta.rutaImagen())).getImage();
                        g.drawImage(img, anchoCarta + 25 + (anchoCarta + 5) * i, 20, anchoCarta, altoCarta, null);
                    }

                    for (int i = 0; i < manoJugador.size(); i++) {
                        Carta carta = manoJugador.get(i);
                        Image img = new ImageIcon(getClass().getResource(carta.rutaImagen())).getImage();
                        g.drawImage(img, 20 + (anchoCarta + 5) * i, 320, anchoCarta, altoCarta, null);
                    }

                    if (partidaTerminada) {
                        g.setColor(Color.WHITE);
                        g.setFont(new Font("Arial", Font.BOLD, 24));
                        FontMetrics fm = g.getFontMetrics();
                        int mensajeWidth = fm.stringWidth(mensajeFinal);
                        int x = (getWidth() - mensajeWidth) / 2;
                        int y = getHeight() / 2;
                        g.drawString(mensajeFinal, x, y);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        frame.setVisible(true);
        frame.setSize(ancho, altura);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53, 101, 77));
        frame.add(gamePanel);

        botPanel.add(new JLabel("Apuesta:"));
        botPanel.add(apuestaField);
        botPanel.add(presButton);
        botPanel.add(stayButtton);
        botPanel.add(resetButton);
        botPanel.add(saldoLabel);
        frame.add(botPanel, BorderLayout.SOUTH);

        presButton.setFocusPainted(false);
        stayButtton.setFocusPainted(false);
        resetButton.setFocusPainted(false);
        resetButton.setEnabled(false);

        presButton.addActionListener(e -> {
            Carta carta = baraja.remove(baraja.size() - 1);
            jugadorSum += carta.getValor();
            jugadorACE += carta.isAce() ? 1 : 0;
            manoJugador.add(carta);

            jugadorSum = redAceJd();

            if (jugadorSum > 21) {
                mensajeFinal = "Perdiste";
                partidaTerminada = true;
                presButton.setEnabled(false);
                stayButtton.setEnabled(false);
                resetButton.setEnabled(true);
                actualizarSaldoLabel();
                gamePanel.repaint();

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(frame, mensajeFinal, "Resultado", JOptionPane.INFORMATION_MESSAGE);
                });
            } else {
                gamePanel.repaint();
            }
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

            dealerSum = redAceD();
            jugadorSum = redAceJd();

            boolean blackjack = (jugadorSum == 21 && manoJugador.size() == 2);

            if (jugadorSum > 21) {
                mensajeFinal = "Perdiste";
            } else if (dealerSum > 21 || jugadorSum > dealerSum) {
                mensajeFinal = blackjack ? "Blackjack!" : "Ganaste";
                double ganancia = apuestaActual * (blackjack ? 2.5 : 2);
                Saldo.agregarGanancia(ganancia);
            } else if (jugadorSum == dealerSum) {
                mensajeFinal = "Empate";
                Saldo.agregarGanancia(apuestaActual);
            } else {
                mensajeFinal = "Perdiste";
            }

            actualizarSaldoLabel();
            partidaTerminada = true;
            resetButton.setEnabled(true);
            gamePanel.repaint();

            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(frame, mensajeFinal, "Resultado", JOptionPane.INFORMATION_MESSAGE);
            });
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

        startGame();
    }

    private void limpiarJuego() {
        manoDealer = new ArrayList<>();
        manoJugador = new ArrayList<>();
        dealerSum = 0;
        dealerACE = 0;
        jugadorSum = 0;
        jugadorACE = 0;
        mensajeFinal = "";
        CartaOculta = null;
        juegoActivo = false;
        gamePanel.repaint();
    }

    public void startGame() {
        partidaTerminada = false;
        mensajeFinal = "";
        juegoActivo = false;

        buildBaraja();
        barajearBaraja();

        try {
            apuestaActual = Double.parseDouble(apuestaField.getText());
            if (apuestaActual <= 0 || !Saldo.descontar(apuestaActual)) {
                JOptionPane.showMessageDialog(frame, "Apuesta inválida o saldo insuficiente.");
                presButton.setEnabled(false);
                stayButtton.setEnabled(false);
                resetButton.setEnabled(true);
                actualizarSaldoLabel();
                limpiarJuego();
                return;
            }
            actualizarSaldoLabel();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Ingresa un número válido.");
            presButton.setEnabled(false);
            stayButtton.setEnabled(false);
            resetButton.setEnabled(true);
            actualizarSaldoLabel();
            limpiarJuego();
            return;
        }

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

        juegoActivo = true;
    }

    public void buildBaraja() {
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

    // En este método ajusta el valor de los ases si el valor supera 21 (jugador)
    public int redAceJd() {
        while (jugadorSum > 21 && jugadorACE > 0) {
            jugadorSum -= 10;
            jugadorACE--;
        }
        return jugadorSum;
    }

    // En este caso lo mismo pero para el dealer
    public int redAceD() {
        while (dealerSum > 21 && dealerACE > 0) {
            dealerSum -= 10;
            dealerACE--;
        }
        return dealerSum;
    }

    public void actualizarSaldoLabel() {
        saldoLabel.setText("Saldo: $" + String.format("%.2f", Saldo.getSaldo()));
    }
}
