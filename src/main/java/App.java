import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

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
                    MqttClient client = new MqttClient("tcp://localhost:1883", "GestorAcceso");
                    MqttConnectionOptions options = new MqttConnectionOptions();
                    options.setAutomaticReconnect(true);
                    options.setCleanStart(true);
                    options.setConnectionTimeout(10);

                    client.setCallback(new MqttCallback() {
                        @Override
                        public void connectComplete(boolean reconnect, String serverURI) {
                            System.out.println("Conexión completada: " + serverURI);
                            System.out.println("----------------------------------------------------");
                        }

                        @Override
                        public void authPacketArrived(int i, MqttProperties mqttProperties) {

                        }

                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            String id = new String(message.getPayload());
                            System.out.println("ID recibido: " + id);

                            try {
                                int estadoAcceso = manejadorAcceso.validarAcceso(id);
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                                String horaActual = LocalTime.now().format(formatter);


                                if (estadoAcceso == 1) {
                                    enviarMensaje(client, "Sensor/Accion", "1");
                                    manejarAccesoPermitido(id, horaActual);
                                } else if (estadoAcceso == 2) {
                                    enviarMensaje(client, "Sensor/Accion", "1");
                                    manejarAccesoPermitidoTarde(id, horaActual);
                                } else if (estadoAcceso == 3) {
                                    enviarMensaje(client, "Sensor/Accion", "0");
                                    manejarAccesoDenegado(id, horaActual);
                                } else if (estadoAcceso == 4) {
                                    enviarMensaje(client, "Sensor/Accion", "0");
                                    manejarEmpleadoNoRegistrado(id, horaActual);
                                } else {
                                    System.out.println("Error en el sistema");
                                }

                            } catch (EmpleadoExc e) {
                                System.out.println(e.getDetalle());
                            }
                        }

                        @Override
                        public void deliveryComplete(IMqttToken iMqttToken) {

                        }

                        @Override
                        public void disconnected(MqttDisconnectResponse disconnectResponse) {
                            System.out.println("Desconectado: " + disconnectResponse.getReasonString());
                        }

                        @Override
                        public void mqttErrorOccurred(MqttException e) {
                            System.out.println("Error de MQTT: " + e.getMessage());
                        }
                    });

                    client.connect(options);
                    System.out.println("Conectado al broker MQTT");
                    System.out.println("----------------------------------------------------");
                    client.subscribe("Sensor/Identidad", 0);

                } catch (MqttException e) {
                    e.printStackTrace();
                }
        } else {
            System.out.println("Opción no válida");
        }
    }

    public static void enviarMensaje(MqttClient client, String topic, String mensaje) throws MqttException {
        client.publish(topic, new MqttMessage(mensaje.getBytes()));
    }

    public static void manejarAccesoPermitido(String id, String horaActual) throws EmpleadoExc {
        System.out.println("Acceso permitido de: [" + id + "]");
        Empleado empleadoIngreso = buscarEmpleado(id);
        if (empleadoIngreso != null) {
            System.out.println("Empleado: " + empleadoIngreso.getNombre() + " ha ingresado al edificio");
            System.out.println("Hora de ingreso: " + horaActual);
            System.out.println("-----------------------------------------");
            String registro = "[" + id + "] " + empleadoIngreso.getNombre() + " HR: " + horaActual + " - [ACCESO PERMITIDO]";
            ManejadorArchivos.escribirArchivo(registro);
        }
    }

    public static void manejarAccesoPermitidoTarde(String id, String horaActual) throws EmpleadoExc {
        System.out.println("Acceso permitido tarde de: [" + id + "]");
        Empleado empleadoIngreso = buscarEmpleado(id);
        if (empleadoIngreso != null) {
            System.out.println("Empleado: " + empleadoIngreso.getNombre() + " ha ingresado al edificio");
            System.out.println("Hora de ingreso: " + horaActual);
            System.out.println("-----------------------------------------");
            String registro = "[" + id + "] " + empleadoIngreso.getNombre() + " HR: " + horaActual + " - [ACCESO PERMITIDO TARDE " + Duration.between(empleadoIngreso.getHorarioAdmitido().getHoraFin(), LocalTime.now()).toMinutes() + " minutos tarde]";
            ManejadorArchivos.escribirArchivo(registro);
        }
    }

    public static void manejarAccesoDenegado(String id, String horaActual) throws EmpleadoExc {
        System.out.println("Acceso denegado de: [" + id + "]");
        Empleado empleadoIngreso = buscarEmpleado(id);
        if (empleadoIngreso != null) {
            System.out.println("Empleado: " + empleadoIngreso.getNombre() + " ha intentado ingresar al edificio");
            System.out.println("Hora de ingreso: " + horaActual);
            System.out.println("-----------------------------------------");
            String registro = "[" + id + "] " + empleadoIngreso.getNombre() + " HR: " + horaActual + " - [ACCESO DENEGADO]";
            ManejadorArchivos.escribirArchivo(registro);
        }
    }

    public static void manejarEmpleadoNoRegistrado(String id, String horaActual) {
        System.out.println("Empleado no registrado [" + id + "]");
        System.out.println("-----------------------------------------");
        String registro = "[" + id + "] N.R HR: " + horaActual + " - [EMPLEADO NO REGISTRADO]";
        ManejadorArchivos.escribirArchivo(registro);
    }

    public static boolean cargarEmpleados() {
        BufferedReader lec = ManejadorArchivos.leerArchivo();
        try {
            String linea = lec.readLine();
            while (linea != null) {
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

    public static Empleado buscarEmpleado(String id) {
        for (Empleado empleado : empleados) {
            if (empleado.getId().equals(id)) {
                return empleado;
            }
        }
        return null;
    }

    public static void mostrarHorarioEmpleado(String id) {
        Empleado empleado = buscarEmpleado(id);
        if (empleado != null) {
            System.out.println("Horario de " + empleado.getNombre());
            System.out.println("Horario de ingreso: " + empleado.getHorarioAdmitido().getHoraInicio());
            System.out.println("Horario de salida: " + empleado.getHorarioAdmitido().getHoraFin());
            System.out.println("Horario de ingreso tarde: " + empleado.getHorarioAdmitido().getHoraInicioTarde());
            System.out.println("Horario de salida tarde: " + empleado.getHorarioAdmitido().getHoraFinTarde());
        } else {
            System.out.println("Empleado no encontrado");
        }
    }
}