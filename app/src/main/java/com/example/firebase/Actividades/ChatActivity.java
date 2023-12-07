package com.example.firebase.Actividades;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.firebase.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class ChatActivity extends AppCompatActivity {

    private static final String BROKER_URL = "tcp://test.mosquitto.org:1883";
    private static final String CLIENT_ID = "AndroidFirebase";
    private MqttHandler mqttHandler;
    private Button sendButton;
    private EditText messageEditText;
    private DatabaseReference databaseReference;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Mensajes");

        mqttHandler = new MqttHandler();
        mqttHandler.connect(BROKER_URL, CLIENT_ID);

        // Inicializar vistas después de inflar la vista con setContentView
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener el mensaje del EditText
                String message = messageEditText.getText().toString();

                // Verificar si el mensaje no está vacío
                if (!message.isEmpty()) {
                    // Obtener el tema al que se enviará el mensaje
                    String topic = "chat";

                    // Identificar el origen del mensaje (en este caso, Android)
                    String origin = "Android";

                    // Construir el mensaje completo con información adicional
                    String fullMessage = origin + ":" + message;

                    // Suscribirse al tema antes de publicar (para recibir tus propios mensajes)
                    subscribeTopic(topic);

                    // Publicar el mensaje
                    publicMessage(topic, fullMessage);

                    // Limpiar el EditText después de enviar el mensaje
                    messageEditText.setText("");
                } else {
                    Toast.makeText(ChatActivity.this, "Por favor, escribe un mensaje", Toast.LENGTH_SHORT).show();
                }
            }
        });



        // Suscribirse al tema de chat
        subscribeTopic("chat");

        // Manejar mensajes entrantes
        mqttHandler.setCallback(new MqttCallback() {
            @Override
            public void messageArrived(String topic, MqttMessage message) {
                // Este método se llama cuando se recibe un mensaje en el tema suscrito
                String incomingMessage = new String(message.getPayload());

                // Dividir el mensaje en dos partes: origen y contenido
                String[] parts = incomingMessage.split(":", 2);

                // Verificar si el mensaje tiene el formato esperado (origen:contenido)
                if (parts.length == 2) {
                    String sender = parts[0].trim();
                    String messageContent = parts[1].trim();

                    // Actualizar la interfaz de usuario con el remitente y el contenido del mensaje
                    updateChatView(sender, messageContent);
                } else {
                    // Si no se puede dividir el mensaje correctamente, asumir que proviene de MyMQTT
                    String sender = "MyMQTT";
                    String messageContent = incomingMessage.trim();

                    // Actualizar la interfaz de usuario con el remitente y el contenido del mensaje
                    updateChatView(sender, messageContent);
                }
            }


            @Override
            public void connectionLost(Throwable cause) {
                // Manejar la pérdida de conexión (si es necesario)
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                // Manejar la entrega completa (si es necesario)
            }
        });
    }

    @Override
    protected void onDestroy() {
        mqttHandler.disconnect();
        super.onDestroy();
    }

    private void publicMessage(String topic, String message) {
        // Publicar el mensaje
        mqttHandler.publish(topic, message);
    }

    private void subscribeTopic(String topic) {
        // Suscribirse al tema
        mqttHandler.subscribe(topic);
    }

    private void updateChatView(final String sender, final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Obtén el layout donde se mostrarán los mensajes
                LinearLayout chatMessageLayout = findViewById(R.id.chatMessageLayout);

                // Crea un nuevo TextView para el mensaje
                TextView messageTextView = new TextView(ChatActivity.this);

                // Configura el texto con el remitente y el mensaje
                String formattedMessage = sender + ": " + message;
                messageTextView.setText(formattedMessage);

                // Crea un nuevo parámetro de diseño para el TextView
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                // Ajusta la gravedad del texto según el remitente
                if (sender.equals("MyMQTT")) {
                    // Si el mensaje proviene de MyMQTT, alinea a la derecha
                    layoutParams.gravity = Gravity.END;
                } else {
                    // Si el mensaje proviene de Android, alinea a la izquierda
                    layoutParams.gravity = Gravity.START;
                }

                // Establece los parámetros de diseño en el TextView
                messageTextView.setLayoutParams(layoutParams);

                // Añade el TextView al layout, al final para que aparezca al final de la lista
                chatMessageLayout.addView(messageTextView);

                // Desplázate hacia abajo para mostrar el mensaje más reciente
                ScrollView scrollView = findViewById(R.id.scrollView);
                scrollView.fullScroll(View.FOCUS_DOWN);
                saveMessageToFirebase(sender, message);
            }
        });
    }

    private void saveMessageToFirebase(String sender, String message) {
        // Crea un nuevo nodo con un identificador único para cada mensaje
        String messageId = databaseReference.push().getKey();

        // Guarda el mensaje en el nodo correspondiente
        databaseReference.child(messageId).child("sender").setValue(sender);
        databaseReference.child(messageId).child("message").setValue(message);
    }

}
