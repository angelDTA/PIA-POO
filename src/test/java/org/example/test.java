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


}
