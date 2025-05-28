package com.example.qtfood_desktop.Modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Oferta {
    private  int id_Oferta;
    private  String productoOferta;
    private  String descripcion;
    private  double descuento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;

    public Oferta() {
    }

    public Oferta(int id_Oferta, String productoOferta, String descripcion, double descuento, LocalDate fechaInicio, LocalDate fechaFin, String estado) {
        this.id_Oferta = id_Oferta;
        this.productoOferta = productoOferta;
        this.descripcion = descripcion;
        this.descuento = descuento;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    public int getId_Oferta() {
        return id_Oferta;
    }

    public void setId_Oferta(int id_Oferta) {
        this.id_Oferta = id_Oferta;
    }

    public String getProductoOferta() {
        return productoOferta;
    }

    public void setProductoOferta(String productoOferta) {
        this.productoOferta = productoOferta;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Oferta{" +
                "id_Oferta=" + id_Oferta +
                ", productoOferta='" + productoOferta + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", descuento=" + descuento +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", estado='" + estado + '\'' +
                '}';
    }
}
