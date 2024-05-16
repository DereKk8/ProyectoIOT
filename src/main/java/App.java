import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.common.MqttException;

import java.io.BufferedReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class App {

    public static ArrayList<Empleado> empleados = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Bienvenido al sistema de control de accesos");
        System.out.println("Ingrese '1' para iniciar sistema");
        int opcion = scanner.nextInt();

        if (opcion == 1) {
            System.out.println("Iniciando el sistema...");
            cargarEmpleados();
            ManejadorAcceso manejadorAcceso = new ManejadorAcceso(empleados);
            try {
                MqttCliente sensores = new MqttCliente(new MqttClient("tcp://localhost:1883", "GestorAcceso"), "Sensor/Identidad", "");
                sensores.conectar();
            } catch (MqttException e) {
                throw new RuntimeException(e);
            }
            while(true) {
                // Obtener EL ID del lector
                if(1==1){
                    System.out.println("-------------------------------------------------------------------------------");
                    String id = "10";
                    // Obtener EL ID del lector

                    try {
                        int estadoAcceso = manejadorAcceso.validarAcceso(id);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                        String horaActual = LocalTime.now().format(formatter);

                        if (estadoAcceso == 1) {
                            //Mandar mensaje al broker
                            System.out.println("Acceso permitido de: [" + id + "]\n");
                            Empleado empleadoIngreso = buscarEmpleado(id);
                            System.out.println("Empleado: " + empleadoIngreso.getNombre() + " ha ingresado al edificio\n");
                            System.out.println("Hora de ingreso: " + horaActual + "\n");

                            String registro ="[" + id + "] " + empleadoIngreso.getNombre() + " HR: " + horaActual + " - [ACCESO PERMITIDO]";
                            ManejadorArchivos.escribirArchivo(registro);
                        }else if(estadoAcceso == 2) {
                            //Mandar mensaje al broker
                            System.out.println("Acceso permitido tarde de: [" + id + "]");
                            Empleado empleadoIngreso = buscarEmpleado(id);
                            System.out.println("Empleado: " + empleadoIngreso.getNombre() + " ha ingresado al edificio\n");
                            System.out.println("Hora de ingreso: " + horaActual + "\n");

                            String registro ="[" + id + "] " + empleadoIngreso.getNombre() + " HR: " + horaActual + " - [ACCESO PERMITIDO TARDE " + Duration.between(empleadoIngreso.getHorarioAdmitido().getHoraFin(), LocalTime.now()) + " minutos tarde]";
                            ManejadorArchivos.escribirArchivo(registro);
                        }else if(estadoAcceso == 3) {
                            //Mandar mensaje al broker
                            System.out.println("Acceso denegado de: [" + id + "]");
                            Empleado empleadoIngreso = buscarEmpleado(id);
                            System.out.println("Empleado: " + empleadoIngreso.getNombre() + " ha intentado ingresar al edificio\n");
                            System.out.println("Hora de ingreso: " + horaActual + "\n");

                            String registro ="[" + id + "] " + empleadoIngreso.getNombre() + " HR: " + horaActual + " - [ACCESO DENEGADO]";
                            ManejadorArchivos.escribirArchivo(registro);
                        }else if(estadoAcceso == 4) {
                            //Mandar mensaje al broker
                            System.out.println("Empleado no registrado [" + id + "]");

                            String registro ="[" + id + "]" + "N.R" + " HR: " + horaActual + " - [EMPLEADO NO REGISTRADO]";
                            ManejadorArchivos.escribirArchivo(registro);
                        }else{
                            System.out.println("Error en el sistema");
                            return;
                        }
                    } catch (EmpleadoExc e) {
                        System.out.println(e.getDetalle());
                        return;
                    }
                }else{
                }
            }
        }
        else
            System.out.println("Opción no válida");
        }

        public static boolean cargarEmpleados(){
            BufferedReader lec = ManejadorArchivos.leerArchivo();

            try{
                String linea = lec.readLine();
                while(linea != null){
                    String[] datos = linea.split(",");
                    LocalTime horaInicio = LocalTime.of(Integer.parseInt(datos[2]), Integer.parseInt(datos[3]));
                    LocalTime horafinal = LocalTime.of(Integer.parseInt(datos[4]), Integer.parseInt(datos[5]));
                    LocalTime horaTardeI = LocalTime.of(Integer.parseInt(datos[6]), Integer.parseInt(datos[7]));
                    LocalTime horaTardeF = LocalTime.of(Integer.parseInt(datos[8]), Integer.parseInt(datos[9]));
                    Empleado empleado = new Empleado(datos[0], datos[1], new Horario(horaInicio, horafinal, horaTardeI, horaTardeF));
                    empleados.add(empleado);
                    linea = lec.readLine();
                }
                lec.close();

                System.out.println("Empleados cargados correctamente");
                System.out.println("Empleados cargados: " + empleados.size());
                return true;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

        public static void mostrarEmpleados(){
            for(Empleado empleado : empleados){
                System.out.println(empleado);
            }
        }

        public static Empleado buscarEmpleado(String id){
            for(Empleado empleado : empleados){
                if(empleado.getId().equals(id)){
                    return empleado;
                }
            }
            return null;
        }
}