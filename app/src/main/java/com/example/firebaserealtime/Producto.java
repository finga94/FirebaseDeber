package com.example.firebaserealtime;

public class Producto {

    private String codigo;
    private String nombre;
    private int stock;
    private double precioCosto;
    private double precioVenta;

    public Producto() {
        // Constructor vacío requerido por Firebase
    }

    public Producto(String codigo, String nombre, int stock, double precioCosto, double precioVenta) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.stock = stock;
        this.precioCosto = precioCosto;
        this.precioVenta = precioVenta;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrecioCosto() {
        return precioCosto;
    }

    public void setPrecioCosto(double precioCosto) {
        this.precioCosto = precioCosto;
    }

    public double getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(double precioVenta) {
        this.precioVenta = precioVenta;
    }
}
