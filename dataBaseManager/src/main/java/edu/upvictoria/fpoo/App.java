package edu.upvictoria.fpoo;

import edu.upvictoria.fpoo.Exceptions.ErrorSintaxis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
    public static void main(String[] args) throws IOException {
        BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));
        String op;
        String use;
        boolean continuar = true; // Variable para controlar la salida del bucle
        System.out.println("ESCRIBE [exit] para salir.");

        do {
            try {
                Sentencias.menu(lector);
                op = lector.readLine();
                if (op.equalsIgnoreCase("exit;")){
                    continuar = false; // Establecer la variable a false para salir del bucle
                }
            } catch (ErrorSintaxis e) {
                System.out.println("Error de sintaxis: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Ocurrió un error: " + e.getMessage());
            }
        } while (continuar); // La condición de salida ahora depende de la variable 'continuar'
    }
}
