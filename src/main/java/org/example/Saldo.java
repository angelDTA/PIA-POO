package org.example;

public class Saldo {
    private static double saldo = 0;

    public static double getSaldo() {
        return saldo;
    }

    public static void recargar(double monto) {
        saldo += monto;
    }

    public static boolean descontar(double monto) {
        if (saldo >= monto) {
            saldo -= monto;
            return true;
        }
        return false;
    }

    public static void agregarGanancia(double monto) {
        saldo += monto;
    }
}
