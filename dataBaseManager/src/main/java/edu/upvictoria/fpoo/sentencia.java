package edu.upvictoria.fpoo;

import edu.upvictoria.fpoo.Exceptions.ErrorSintaxis;
import edu.upvictoria.fpoo.Exceptions.FileDoesntExist;

import java.io.*;
import java.util.ArrayList;

public class Sentencias {
    static String path = "/home/alexis/Desktop/iti-271215-poo-practica-1-ALEXIS23327/dataBaseManager/src/main/$PATH$";
    static BufferedReader lector = new BufferedReader(new InputStreamReader(System.in));

    public static void menu(BufferedReader lector) throws IOException {
        //SentenciasAcciones.listaComandos();
        System.out.println("Ingrese un comando");
        System.out.print("$PATH$:");
        String comando = obtenerComando(lector);
        //System.out.println("Comando ingresado: " + comando);

        if (comando.startsWith("DROP TABLE")) {
            if (!comando.startsWith("DROP TABLE")) {
                throw new ErrorSintaxis("ERROR DE SINTAXIS");
            }
            String tableName = comando.substring(11).trim();
            String nombre = tableName.substring(0, tableName.length());

            SentenciasAcciones.dropTable(nombre);
        } else if (comando.startsWith("CREATE TABLE")) {
            crearTabla(comando);
            if (!comando.startsWith("CREATE TABLE")) {
                throw new ErrorSintaxis("ERROR DE SINTAXIS");
            }
        } else if (comando.startsWith("SHOW TABLES")) {
            if (!comando.startsWith("SHOW TABLES")) {
                throw new ErrorSintaxis("ERROR DE SINTAXIS");
            }
            SentenciasAcciones.showTables();
        } else if (comando.startsWith("INSERT")) {
            if (!comando.startsWith("INSERT INTO")) {
                throw new ErrorSintaxis("ERROR DE SINTAXIS");
            }
            insertDatos(comando);
        } else if (comando.startsWith("SELECT")) {
            if (!comando.startsWith("SELECT * FROM")) {
                throw new ErrorSintaxis("ERROR DE SINTAXIS");
            }
            selectDatos(comando);
        } else if (comando.startsWith("UPDATE")) {
            if (!comando.startsWith("UPDATE ")) {
                throw new ErrorSintaxis("ERROR DE SINTAXIS");
            }
            updateDatos(comando);
        } else if (comando.startsWith("DELETE FROM")) {
            String[] partes = comando.split(" ");
            if (partes.length < 3 || !partes[1].equalsIgnoreCase("FROM")) {
                throw new ErrorSintaxis("ERROR DE SINTAXIS");
            }
            String tableName = partes[2].trim();
            String condition = null;
            if (comando.contains("WHERE")) {
                condition = comando.substring(comando.indexOf("WHERE") + 5).trim();
            }
            SentenciasAcciones.deleteFrom(tableName, condition);
        } else if (comando.startsWith("exit")) {
            System.exit(0);
        }

    }

    public static String obtenerComando(BufferedReader lector) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = lector.readLine()) != null) {
            sb.append(line.trim());
            if (line.trim().endsWith(";")) {
                break;
            }
        }
        return sb.substring(0, sb.length() - 1); // -(;)
    }

    public static void crearTabla(String comando) {
        String[] palabras = comando.split(" ");
        String tableName = palabras[2];
        System.out.println("Nombre de la tabla: " + tableName);

        String dentro = comando.substring(comando.indexOf("(") + 1, comando.lastIndexOf(")")).trim();
        String[] campos = dentro.split(",");


        // si no existe la tabla
        File archivo = new File(path + "/" + tableName + ".csv");
        if (!archivo.exists()) {
            SentenciasAcciones.createTable(tableName, campos);
        } else {
            System.out.println("El archivo o tabla " + tableName + ", ya existe.");
        }

    }

    public static void insertDatos(String comando) {
        try {
            String nombreTabla = comando.substring(12, comando.indexOf("(")).trim();
            String columnas = comando.substring(comando.indexOf("(") + 1, comando.indexOf(")"));
            String valores = comando.substring(comando.lastIndexOf("(") + 1, comando.lastIndexOf(")"));

            String[] aColumnas = columnas.split(",");
            String[] aValores = valores.split(",");

            ArrayList<String> nombreColumnas = new ArrayList<>();
            ArrayList<String> valoresColumnas = new ArrayList<>();

            for (String columna : aColumnas) {
                nombreColumnas.add(columna.trim());
            }

            for (String dato : aValores) {
                valoresColumnas.add(dato.trim());
            }

            SentenciasAcciones.insertInto(nombreTabla, valoresColumnas);
        } catch (FileDoesntExist e) {
            System.out.println(e.getMessage());
        }
    }

    public static void selectDatos(String comando) throws FileDoesntExist {
        try {
            String[] partes = comando.split("FROM");
            if (partes.length == 2) {
                String selectClausula = partes[0].substring(7).trim();
                String[] subPartes = partes[1].trim().split("WHERE");

                String tabla = subPartes[0].trim();
                String condition = (subPartes.length == 2) ? subPartes[1].trim() : null;

                String[] columnas = selectClausula.equals("*") ? new String[]{"*"} : selectClausula.split(",");

                SentenciasAcciones.selectFrom(tabla, columnas, condition);
            } else if (partes.length == 1) {
                // Si no hay cláusula WHERE
                String selectClausula = partes[0].substring(7).trim();
                String tabla = selectClausula.trim();
                String[] columnas = selectClausula.equals("*") ? new String[]{"*"} : selectClausula.split(",");

                SentenciasAcciones.selectFrom(tabla, columnas, null);
            } else {
                throw new ErrorSintaxis("ERROR SINTAXIS");
            }
        } catch (FileDoesntExist e) {
            System.out.println(e.getMessage());
        }
    }

    public static void updateDatos(String comando) throws FileDoesntExist {
        try {
            String tableName = comando.substring(7, comando.indexOf("SET")).trim();
            String setClause = comando.substring(comando.indexOf("SET") + 3, comando.indexOf("WHERE")).trim();
            String condition = comando.substring(comando.indexOf("WHERE") + 5).trim();

            SentenciasAcciones.updateTable(tableName, setClause, condition);
        } catch (FileDoesntExist e) {
            System.out.println(e.getMessage());
        }
    }

}
