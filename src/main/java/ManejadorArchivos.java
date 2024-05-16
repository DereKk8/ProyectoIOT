import java.io.*;

public class ManejadorArchivos {

    public static BufferedReader leerArchivo(){
        File archivo = new File("empleados.txt");
        FileReader fr = null;
        try {
            fr = new FileReader(archivo);
        }catch (FileNotFoundException e) {
            System.out.println("Error al abrir el archivo de empleados");
        }
        BufferedReader flujoLectura = new BufferedReader(fr);
        return flujoLectura;
    }


    public static void escribirArchivo(String contenido) {
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter("accesos.txt", true));
            bw.write(contenido);
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo de accesos");
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    System.out.println("Error al cerrar el BufferedWriter");
                }
            }
        }
    }
}
