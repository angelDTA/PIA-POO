package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;

class SlotTest {

    private Slot slot;

    @BeforeEach
    void setUp() {
        slot = new Slot();
    }

    @Test
    void testRandomSymbolNotNull() {
        ImageIcon icon = slot.symbolIcons[0];
        assertNotNull(icon, "El símbolo no debería ser null");
    }

    @Test
    void testGameResultWinCondition() {
        Icon icon = slot.symbolIcons[0];
        String result = slot.getGameResult(icon, icon, icon);
        assertEquals("WIN", result);
    }

    @Test
    void testGameResultLoseCondition() {
        Icon icon1 = slot.symbolIcons[0];
        Icon icon2 = slot.symbolIcons[1];
        Icon icon3 = slot.symbolIcons[2];
        String result = slot.getGameResult(icon1, icon2, icon3);
        assertEquals("LOSE", result);
    }


}
