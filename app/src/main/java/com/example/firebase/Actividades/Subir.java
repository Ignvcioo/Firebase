package com.example.firebase.Actividades;

public class Subir {
    private String nombre;
    private String imagenUrl;

    public Subir() {

    }

    public Subir(String nombre, String imagenUrl) {
        if (nombre.trim().equals("")) {
            nombre = "No nombre";
        }
        nombre = nombre;
        imagenUrl = imagenUrl;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }
}
