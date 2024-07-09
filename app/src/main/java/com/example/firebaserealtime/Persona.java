package com.example.firebaserealtime;
public class Persona {
    private String cedula;
    private String nombre;
    private String provincia;
    private String genero;
    private String pais;
    private String email;

    public Persona() {
        // Constructor vacío requerido por Firebase Realtime Database
    }

    public Persona(String cedula, String nombre, String provincia, String genero, String pais, String email) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.provincia = provincia;
        this.genero = genero;
        this.pais = pais;
        this.email = email;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getGenero() {
        return genero;
    }

    public String getPais() {
        return pais;
    }

    public String getEmail() {
        return email;
    }
}


