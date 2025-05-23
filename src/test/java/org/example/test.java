package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class BlackJackTest {

    private BlackJack game;

    @BeforeEach
    void setUp() {
        game = new BlackJack();
        game.buildBaraja();
    }

    @Test
    void testBarajaTiene52Cartas() {
        assertEquals(52, game.baraja.size(), "La baraja debe tener 52 cartas");
    }

    @Test
    void testBarajearMezclaCartas() {
        ArrayList<String> original = new ArrayList<>();
        for (BlackJack.Carta carta : game.baraja) {
            original.add(carta.toString());
        }

        game.barajearBaraja();

        ArrayList<String> mezclada = new ArrayList<>();
        for (BlackJack.Carta carta : game.baraja) {
            mezclada.add(carta.toString());
        }

        // No deben ser exactamente iguales después de mezclar
        assertNotEquals(original, mezclada, "La baraja debería estar mezclada");
    }

    @Test
    void testValorCartaNumerica() {
        BlackJack.Carta carta = game.new Carta("7", "H");
        assertEquals(7, carta.getValor());
    }

    @Test
    void testValorCartaFigura() {
        assertEquals(10, game.new Carta("K", "S").getValor());
        assertEquals(10, game.new Carta("Q", "H").getValor());
        assertEquals(10, game.new Carta("J", "D").getValor());
    }

    @Test
    void testValorAs() {
        BlackJack.Carta as = game.new Carta("A", "C");
        assertEquals(11, as.getValor());
        assertTrue(as.isAce());
    }

    @Test
    void testReduccionAsJugador() {
        game.jugadorSum = 22;
        game.jugadorACE = 1;

        int nuevoValor = game.redAceJd();
        assertEquals(12, nuevoValor, "El As debería reducir el valor en 10 si el jugador se pasa");
    }

    @Test
    void testReduccionAsDealer() {
        game.dealerSum = 23;
        game.dealerACE = 1;

        int nuevoValor = game.redAceD();
        assertEquals(13, nuevoValor, "El As debería reducir el valor en 10 si el dealer se pasa");
    }
}
