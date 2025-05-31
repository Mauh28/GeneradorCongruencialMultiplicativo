import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class GeneradorCongruencialMultiplicativo {

    // Parámetros óptimos para cada módulo: {m, a, nombre}
    public static final Object[][] PARAMETROS = {
            {32057L, 171L, "32,057"},
            {32537L, 173L, "32,537"},
            {32537L, 173L, "32,537"},
            {32687L, 172L, "32,687"},
            {32603L, 170L, "32,603"},
            {32707L, 172L, "32,707"},
            {32933L, 175L, "32,933"}
    };

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        long X0 = 12345L;

        System.out.println("¿Qué módulo desea generar? (Ingrese índice 0-6):");
        for (int i = 0; i < PARAMETROS.length; i++) {
            System.out.printf("%d → %s%n", i, PARAMETROS[i][2]);
        }
        int indice = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer

        if (indice < 0 || indice >= PARAMETROS.length) {
            System.out.println("Índice inválido");
            return;
        }

        long m = ((Number) PARAMETROS[indice][0]).longValue();
        long a = ((Number) PARAMETROS[indice][1]).longValue();
        String nombreModulo = (String) PARAMETROS[indice][2];

        System.out.print("¿Generar archivo con todos los números? (S/N): ");
        boolean completo = scanner.nextLine().equalsIgnoreCase("S");

        String nombreArchivo = String.format("rng_%s.txt", nombreModulo.replace(",", ""));

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
            Generador generador = new Generador(a, X0, m);
            generador.generarArchivo(writer, completo);
            System.out.println("✓ Archivo generado: " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Error al escribir archivo: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}

class Generador {
    private final long a;
    private long Xn;
    private final long m;
    private final long X0;

    public Generador(long a, long X0, long m) {
        this.a = a;
        this.m = m;
        this.X0 = X0;
        this.Xn = X0;
    }

    public void generarArchivo(BufferedWriter writer, boolean completo) throws IOException {
        Set<Long> valoresUnicos = new HashSet<>();
        reiniciar();
        long iteracion = 0;

        writer.write("Módulo: " + m + "\n");
        writer.write("Multiplicador (a): " + a + "\n");
        writer.write("Semilla (X0): " + X0 + "\n\n");

        // Generación y verificación
        while (iteracion < m - 1) {
            long valor = siguienteEntero();
            if (!valoresUnicos.add(valor)) {
                writer.write("\n❌ PERÍODO INCOMPLETO: Repetición en iteración " + iteracion);
                break;
            }
            iteracion++;
        }

        if (valoresUnicos.size() == m - 1) {
            writer.write("✅ PERÍODO COMPLETO: " + (m - 1) + " valores únicos\n\n");
        }

        // Escritura de números
        reiniciar();
        if (completo) {
            writer.write("SECUENCIA COMPLETA:\n");
            for (long i = 0; i < m - 1; i++) {
                writer.write(String.format("%d → %.6f%n", i + 1, siguiente()));
            }
        } else {
            writer.write("MUESTRA (primeros y últimos 100 valores):\n");
            writer.write("--- PRIMEROS 100 ---\n");
            for (int i = 0; i < 100; i++) {
                writer.write(String.format("%d → %.6f%n", i + 1, siguiente()));
            }

            reiniciar();
            for (long i = 0; i < m - 101; i++) {
                siguiente();
            }
            writer.write("\n--- ÚLTIMOS 100 ---\n");
            for (long i = m - 100; i < m; i++) {
                writer.write(String.format("%d → %.6f%n", i, siguiente()));
            }
        }
    }

    private long siguienteEntero() {
        Xn = (a * Xn) % m;
        return Xn;
    }

    private double siguiente() {
        return (double) siguienteEntero() / m;
    }

    private void reiniciar() {
        this.Xn=X0;
    }
}
