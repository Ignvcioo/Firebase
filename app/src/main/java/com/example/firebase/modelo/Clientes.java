package com.example.firebase.modelo;

public class Clientes {
    String id;
    String email;
    String nombreUsuario;
    String telefono;

    public Clientes() {

    }

    public Clientes(String id, String email, String nombreUsuario, String telefono) {
        this.id = id;
        this.email = email;
        this.nombreUsuario = nombreUsuario;
        this.telefono = telefono;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
