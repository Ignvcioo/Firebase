package com.example.firebase.Actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.firebase.R;
import com.example.firebase.modelo.Contacto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ListaActivity extends AppCompatActivity {
    private List<Contacto> listaContacto = new ArrayList<>();
    ArrayAdapter<Contacto> contactoArrayAdapter;
    FirebaseDatabase database;
    DatabaseReference referencia;
    ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        database = FirebaseDatabase.getInstance();
        referencia = database.getReference("Contacto");
        lista = findViewById(R.id.listContacto);
        listarDatos();
    }

    private void listarDatos() {
        referencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listaContacto.clear(); // Limpiar la lista antes de agregar nuevos datos

                for (DataSnapshot objSnapshot : snapshot.getChildren()) {
                    Contacto contacto = objSnapshot.getValue(Contacto.class);
                    listaContacto.add(contacto);
                }

                // Crear un adaptador personalizado y asignarlo a la lista
                contactoArrayAdapter = new ContactoArrayAdapter(ListaActivity.this, listaContacto);
                lista.setAdapter(contactoArrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Manejar errores de lectura de la base de datos
            }
        });
    }

}