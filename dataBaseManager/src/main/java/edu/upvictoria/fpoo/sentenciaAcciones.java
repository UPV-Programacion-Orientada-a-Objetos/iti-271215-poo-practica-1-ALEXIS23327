package edu.upvictoria.fpoo;

import edu.upvictoria.fpoo.Exceptions.ErrorSintaxis;
import edu.upvictoria.fpoo.Exceptions.FileDoesntExist;

import java.io.*;
import java.util.ArrayList;

public class SentenciasAcciones {
    static String path = "/home/alexis/Desktop/iti-271215-poo-practica-1-ALEXIS23327/dataBaseManager/src/main/$PATH$";

    public static BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
    public static String obtenerRutaArchivo(String nombreTabla) {
        return path + "/" + nombreTabla + ".csv";
    }
    public static void createTable(String nombreTabla, String[] campos) {
        String filePath = path + "/" + nombreTabla + ".csv";
        try (FileWriter writer = new FileWriter(filePath)) {
            for (String campo : campos) {
                writer.write(campo + ",");
            }
            writer.write("\n");
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public static void showTables() {
        System.out.println("         LISTA DE TABLAS        ");
        File directorio = new File(path);
        File[] archivos = directorio.listFiles();

        if (archivos != null) {
            for (File archiv0 : archivos) {
                System.out.println(archiv0.getName());
            }
        } else {
            System.out.println("No hay tablas que mostrar.");
        }

    }

    public static void dropTable(String nombreTabla) {

        String filePath = path + "/" + nombreTabla + ".csv";
        File file = new File(filePath);

        try {
            if (!file.exists()) {
                throw new FileDoesntExist("El archivo " + nombreTabla + " no existe");
            }

            System.out.println("$PATH$: ¿ELIMINAR LA TABLA?");
            System.out.println("$PATH$: INGRESE [0] PARA CONFIRMAR");
            String op = bf.readLine();
            if (op.equals("0")) {
                if (file.delete()) {
                    System.out.println("$PATH$: LA TABLA " + nombreTabla + " SE HA ELIMINADO CORRECTAMENTE");
                } else {
                    System.out.println("ERROR");
                }
            }
        } catch (FileDoesntExist e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public static void insertInto(String tableName, ArrayList<String> valores) throws FileDoesntExist {
        String filePath = path + "/" + tableName + ".csv";
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileDoesntExist("El archivo " + tableName + " no existe");
        }

        try (FileWriter writer = new FileWriter(filePath, true)) {
            for (String valorCampo : valores) {
                writer.write(valorCampo + ",");
            }
            writer.write("\n");
            System.out.println("SE HAN INSERTADO CORRECTAMENTE");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void selectFrom(String nombreTabla, String[] columns, String condicion) throws FileDoesntExist {
        String filePath = path + "/" + nombreTabla + ".csv";
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileDoesntExist("El archivo " + nombreTabla + " no existe");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            String[] headers = headerLine.split(",");
            ArrayList<Integer> columnas = new ArrayList<>();

            if (columns[0].equals("*")) {
                for (int i = 0; i < headers.length; i++) {
                    columnas.add(i);
                }
            } else {
                for (String column : columns) {
                    boolean encontrado = false;
                    for (int i = 0; i < headers.length; i++) {
                        if (headers[i].trim().equals(column.trim())) {
                            columnas.add(i);
                            encontrado = true;
                            break;
                        }
                    }

                    if (!encontrado) {
                        System.out.println("La columna " + column + " no existe.");
                        return;
                    }
                }
            }

            String line;
            while ((line = reader.readLine()) != null) {
                String[] valores = line.split(",");
                if (condicion == null || validarCondicion(valores, headers, condicion)) {
                    for (int index : columnas) {
                        System.out.print(headers[index] + ": " + valores[index] + " ");
                    }
                    System.out.println();
                }
            }
        } catch (FileDoesntExist e) {
            System.out.println("El archivo " + filePath + " no se ha encontrado.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void deleteFrom(String nombreTabla, String condicion) throws FileDoesntExist {
        String filePath = path + "/" + nombreTabla + ".csv";
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileDoesntExist("El archivo " + nombreTabla + " no existe");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            String[] headers = headerLine.split(",");
            ArrayList<Integer> columnasSeleccionadas = new ArrayList<>();
            for (int i = 0; i < headers.length; i++) {
                columnasSeleccionadas.add(i);
            }

            String line;
            StringBuilder resultad0 = new StringBuilder();
            resultad0.append(headerLine).append("\n"); // Mantener el encabezado
            boolean realizado = false;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                if (validarCondicion(values, headers, condicion)) {
                    realizado = true;
                    continue;
                }
                resultad0.append(line).append("\n");
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(resultad0.toString());
            }
            if (!realizado) {
                System.out.println("NO EXISTE UN REGISTRO QUE CUMPLA CON LA CONDICIÓN");
            } else {
                System.out.println("DATOS ELIMINADOS CORRECTAMENTE - ["+nombreTabla+"]");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static boolean validarCondicion(String[] valores, String[] headers, String condition) {
        String[] condiciones = condition.split("\\s+(?i)OR\\s+");

        for (String condicion : condiciones) {
            String[] partesCondicion = condicion.split("=");

            if (partesCondicion.length != 2) {
                System.out.println("Error en la condición: " + condicion);
                return false;
            }

            String campo = partesCondicion[0].trim();
            String valor = partesCondicion[1].trim().replace("'", ""); // Elimina comillas si están presentes

            int indiceHeader = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].trim().equals(campo)) {
                    indiceHeader = i;
                    break;
                }
            }

            if (indiceHeader == -1) {
                System.out.println("Campo " + campo + " no encontrado.");
                return false;
            }

            if (valores[indiceHeader].trim().equals(valor)) {
                return true;
            }
        }

        return false; //nada es verdadero
    }





    public static void updateTable(String nombreTabla, String clausula, String condicion) throws FileDoesntExist {
        String filePath = path + "/" + nombreTabla + ".csv";
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileDoesntExist("El archivo " + nombreTabla + " no existe");
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String headerLine = reader.readLine();
            String[] headers = headerLine.split(",");

            String[] sets = clausula.split(",");
            String[] asignarColumnas = new String[sets.length];
            String[] asignarValores = new String[sets.length];

            for (int i = 0; i < sets.length; i++) {
                String[] asignarPar = sets[i].split("=");
                asignarColumnas[i] = asignarPar[0].trim();
                asignarValores[i] = asignarPar[1].trim().replace("'", "");
            }

            StringBuilder result = new StringBuilder();
            result.append(headerLine).append("\n");
            boolean actualizado = false;

            String line;
            while ((line = reader.readLine()) != null) {
                String[] valores = line.split(",");

                if (validarCondicion(valores, headers, condicion)) {
                    for (int i = 0; i < headers.length; i++) {
                        for (int j = 0; j < asignarColumnas.length; j++) {
                            if (headers[i].trim().equals(asignarColumnas[j])) {
                                valores[i] = asignarValores[j];
                                actualizado = true;
                                break;
                            }
                        }
                    }
                }

                result.append(String.join(",", valores)).append("\n");
            }

            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(result.toString());
            }

            if (actualizado) {
                System.out.println("REGISTRO ACTUALIZADO EN -[" + nombreTabla +"]");
            } else {
                System.out.println("No se encontraron registros que cumplan la condición");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

