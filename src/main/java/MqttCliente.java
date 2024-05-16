import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttClient;
import org.eclipse.paho.mqttv5.client.MqttConnectionOptions;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

public class MqttCliente{

    MqttClient cliente;
    private String broker;
    private String clientId;
    private String topico;
    private int subQos;
    private int pubQos;
    private String accion;

    public MqttCliente(MqttClient cliente, String topico, String accion){
        this.cliente = cliente;
        this.broker = "tcp://localhost:1883";
        this.clientId = "GestorAcceso";
        this.topico = topico;
        this.subQos = 0;
        this.pubQos = 0;
        this.accion = accion;
    }

    public void conectar() throws MqttException {
        cliente = new MqttClient(broker, clientId);
        MqttConnectionOptions opciones = new MqttConnectionOptions();
        cliente.connect(opciones);
        System.out.println("Conectado a: " + broker);
    }

    public void suscribir() throws MqttException {
        cliente.setCallback(new MqttCallback() {
            public void connectComplete(boolean reconnect, String serverURI) {
                System.out.println("Conectado a: " + serverURI);
            }

            public void disconnected(MqttDisconnectResponse disconnectResponse) {
                System.out.println("Desconectado: " + disconnectResponse.getReasonString());
            }

            public void deliveryComplete(IMqttToken token) {
                System.out.println("Entrega completa: " + token.isComplete());
            }

            public void messageArrived(String topico, MqttMessage mensaje) throws Exception {
                System.out.println("Topico: " + topico);
                System.out.println("QoS: " + mensaje.getQos());
                System.out.println("Contenido del mensaje: " + new String(mensaje.getPayload()));
            }

            public void mqttErrorOccurred(MqttException excepcion) {
                System.out.println("Error de MQTT: " + excepcion.getMessage());
            }

            public void authPacketArrived(int reasonCode, MqttProperties propiedades) {
                System.out.println("Paquete de autenticación recibido");
            }
        });

        cliente.subscribe(topico, subQos);
    }
/*
    public static void main(String[] args) {
        String broker = "tcp://localhost:1883";
        String clientId = "Derek";
        String topic = "topic/test";
        int subQos = 1;
        int pubQos = 1;
        String msg = "Hola a Todo";

        try {
            MqttClient client = new MqttClient(broker, clientId);
            MqttConnectionOptions options = new MqttConnectionOptions();

            client.setCallback(new MqttCallback() {
                public void connectComplete(boolean reconnect, String serverURI) {
                    System.out.println("connected to: " + serverURI);
                }

                public void disconnected(MqttDisconnectResponse disconnectResponse) {
                    System.out.println("disconnected: " + disconnectResponse.getReasonString());
                }

                public void deliveryComplete(IMqttToken token) {
                    System.out.println("deliveryComplete: " + token.isComplete());
                }

                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    System.out.println("topic: " + topic);
                    System.out.println("qos: " + message.getQos());
                    System.out.println("message content: " + new String(message.getPayload()));
                }

                public void mqttErrorOccurred(MqttException exception) {
                    System.out.println("mqttErrorOccurred: " + exception.getMessage());
                }

                public void authPacketArrived(int reasonCode, MqttProperties properties) {
                    System.out.println("authPacketArrived");
                }
            });

            client.connect(options);

            client.subscribe(topic, subQos);

            for(int a = 0; a < 10; a++){
                MqttMessage message = new MqttMessage(msg.getBytes());
                message.setQos(pubQos);
                client.publish(topic, message);
                Thread.sleep(10000);
            }



            // client.disconnect();
            //  client.close();

        } catch (MqttException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

 */
}