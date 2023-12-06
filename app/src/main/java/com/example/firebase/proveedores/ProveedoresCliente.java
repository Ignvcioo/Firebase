package com.example.firebase.proveedores;

import com.example.firebase.modelo.Clientes;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class ProveedoresCliente {
    DatabaseReference baseDatos;

    // Constructor de la clase. Inicializa la referencia a la ubicación "Usuarios/Clientes" en la base de datos.
    public ProveedoresCliente(){
        baseDatos = FirebaseDatabase.getInstance().getReference().child("Cliente");
    }

    // Método para crear un nuevo registro de cliente en la base de datos.
    public Task<Void> create(Clientes clientes){
        // Se crea un mapa (HashMap) para almacenar los datos del cliente.
        Map<String, Object> map = new HashMap<>();
        map.put("email", clientes.getEmail());
        map.put("telefono", clientes.getTelefono());
        map.put("nombreUsuario", clientes.getNombreUsuario());
        //Crea un nuevo registro de cliente en la base de datos o actualiza uno existente si el ID ya existe.
        return baseDatos.child(clientes.getId()).setValue(map);
    }

    public DatabaseReference obtenerClientePorId(String clienteId) {
        return baseDatos.child(clienteId);
    }

    public Task<Void> actualizarDatos(String clienteId, String nuevoNombre, String nuevoTelefono) {
        // Se crea un mapa (HashMap) para almacenar los nuevos valores.
        Map<String, Object> actualizacionMap = new HashMap<>();
        actualizacionMap.put("nombreUsuario", nuevoNombre);
        actualizacionMap.put("telefono", nuevoTelefono);

        // Actualiza el registro del cliente en la base de datos con los nuevos valores.
        return baseDatos.child(clienteId).updateChildren(actualizacionMap);
    }
}
