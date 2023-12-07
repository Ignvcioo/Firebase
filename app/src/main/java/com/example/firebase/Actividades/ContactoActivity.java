package com.example.firebase.Actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.firebase.R;
import com.example.firebase.modelo.Contacto;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ContactoActivity extends AppCompatActivity {

    TextInputEditText nombre, apellido, correo, idBuscar;
    Button btnBuscar, btnAgregar, btnActualizar, btnEliminar, btnLista, btnVerPerfil, btnMqtt;
    FirebaseDatabase database;
    DatabaseReference referencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacto);

        // Obtener referencia a la base de datos
        database = FirebaseDatabase.getInstance();
        // Habilitar la persistencia antes de realizar cualquier otra operación
        // Obtener la referencia después de habilitar la persistencia
        referencia = database.getReference("Contacto");

        nombre = findViewById(R.id.txtInputNombre);
        apellido = findViewById(R.id.txtInputApellido);
        correo = findViewById(R.id.txtInputCorreo);
        idBuscar = findViewById(R.id.txtInputBuscarPorId);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnAgregar = findViewById(R.id.btnAgregar);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnLista = findViewById(R.id.btnLista);
        btnVerPerfil = findViewById(R.id.btnPerfil);
        btnMqtt = findViewById(R.id.btnMQTTCONEXION);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uidContacto = idBuscar.getText().toString().trim();

                referencia.orderByChild("id").equalTo(uidContacto).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // En este caso, ya que podría haber varios resultados, itera sobre ellos
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                Contacto contacto = childSnapshot.getValue(Contacto.class);
                                nombre.setText(contacto.getNombre());
                                apellido.setText(contacto.getApellido());
                                correo.setText(contacto.getCorreo());
                            }
                        } else {
                            Toast.makeText(ContactoActivity.this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los datos del contacto
                String nombreContacto = nombre.getText().toString().trim();
                String apellidoContacto = apellido.getText().toString().trim();
                String correoContacto = correo.getText().toString().trim();

                // Validar que se haya ingresado un correo
                if (correoContacto.isEmpty()) {
                    Toast.makeText(ContactoActivity.this, "Por favor, ingresa un correo", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Generar un ID sencillo basado en algún criterio (puedes ajustarlo según tus necesidades)
                String nuevoId = generarIdSencillo(nombreContacto, apellidoContacto);

                // Crear un nuevo objeto Contacto con el ID generado
                Contacto nuevoContacto = new Contacto(nuevoId, nombreContacto, apellidoContacto, correoContacto);

                // Agregar el nuevo contacto a la base de datos
                referencia.child(nuevoId).setValue(nuevoContacto);

                Toast.makeText(ContactoActivity.this, "Contacto agregado correctamente", Toast.LENGTH_SHORT).show();
                idBuscar.setText("");
                nombre.setText("");
                apellido.setText("");
                correo.setText("");
            }
        });

        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uidContacto = idBuscar.getText().toString().trim();

                // Obtener los nuevos datos del contacto
                String nuevoNombre = nombre.getText().toString().trim();
                String nuevoApellido = apellido.getText().toString().trim();
                String nuevoCorreo = correo.getText().toString().trim();

                // Verificar que el contacto exista antes de actualizar
                referencia.orderByChild("id").equalTo(uidContacto).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Iterar sobre los resultados (puede haber varios si hay duplicados)
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                // Obtener la referencia del contacto que se va a actualizar
                                DatabaseReference contactoRef = childSnapshot.getRef();

                                // Actualizar los datos del contacto
                                contactoRef.child("nombre").setValue(nuevoNombre);
                                contactoRef.child("apellido").setValue(nuevoApellido);
                                contactoRef.child("correo").setValue(nuevoCorreo);

                                Toast.makeText(ContactoActivity.this, "Contacto actualizado correctamente", Toast.LENGTH_SHORT).show();
                                idBuscar.setText("");
                                nombre.setText("");
                                apellido.setText("");
                                correo.setText("");
                            }
                        } else {
                            Toast.makeText(ContactoActivity.this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Manejar errores si es necesario
                    }
                });
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uidContacto = idBuscar.getText().toString().trim();

                // Verificar que el contacto exista antes de eliminar
                referencia.orderByChild("id").equalTo(uidContacto).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Iterar sobre los resultados (puede haber varios si hay duplicados)
                            for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                                // Obtener la referencia del contacto que se va a eliminar
                                DatabaseReference contactoRef = childSnapshot.getRef();

                                // Eliminar el contacto de la base de datos
                                contactoRef.removeValue();

                                // Limpiar los campos después de eliminar
                                nombre.setText("");
                                apellido.setText("");
                                correo.setText("");

                                Toast.makeText(ContactoActivity.this, "Contacto eliminado correctamente", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ContactoActivity.this, "El usuario no existe", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Manejar errores si es necesario
                    }
                });
            }
        });

        btnLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactoActivity.this, ListaActivity.class);
                startActivity(intent);
                idBuscar.setText("");
                nombre.setText("");
                apellido.setText("");
                correo.setText("");
            }
        });

        btnVerPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactoActivity.this, PerfilActivity.class);
                startActivity(intent);
            }
        });

        btnMqtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContactoActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });
    }

    private String generarIdSencillo(String nombre, String apellido) {
        return nombre.toLowerCase() + "_" + apellido.toLowerCase();
    }
}