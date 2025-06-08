package com.example.qtfood_desktop.Modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.LocalDate;

/**
 * Representa una oferta aplicada a un producto, incluyendo el descuento,
 * el periodo de validez y su estado.
 */
public class Oferta {
    private int id_Oferta;
    private String productoOferta;
    private String descripcion;
    private double descuento;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;

    /** Constructor vacío. */
    public Oferta() {
    }

    /**
     * Constructor con todos los atributos de la oferta.
     *
     * @param id_Oferta ID de la oferta
     * @param productoOferta Nombre del producto en oferta
     * @param descripcion Descripción de la oferta
     * @param descuento Porcentaje de descuento aplicado
     * @param fechaInicio Fecha de inicio de la oferta
     * @param fechaFin Fecha de finalización de la oferta
     * @param estado Estado actual de la oferta (activa/inactiva)
     */
    public Oferta(int id_Oferta, String productoOferta, String descripcion, double descuento, LocalDate fechaInicio, LocalDate fechaFin, String estado) {
        this.id_Oferta = id_Oferta;
        this.productoOferta = productoOferta;
        this.descripcion = descripcion;
        this.descuento = descuento;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    /** @return ID de la oferta */
    public int getId_Oferta() {
        return id_Oferta;
    }

    /** @param id_Oferta ID de la oferta */
    public void setId_Oferta(int id_Oferta) {
        this.id_Oferta = id_Oferta;
    }

    /** @return Nombre del producto en oferta */
    public String getProductoOferta() {
        return productoOferta;
    }

    /** @param productoOferta Nombre del producto en oferta */
    public void setProductoOferta(String productoOferta) {
        this.productoOferta = productoOferta;
    }

    /** @return Descripción de la oferta */
    public String getDescripcion() {
        return descripcion;
    }

    /** @param descripcion Descripción de la oferta */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /** @return Porcentaje de descuento */
    public double getDescuento() {
        return descuento;
    }

    /** @param descuento Porcentaje de descuento */
    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    /** @return Fecha de inicio de la oferta */
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    /** @param fechaInicio Fecha de inicio de la oferta */
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /** @return Fecha de finalización de la oferta */
    public LocalDate getFechaFin() {
        return fechaFin;
    }

    /** @param fechaFin Fecha de finalización de la oferta */
    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    /** @return Estado actual de la oferta */
    public String getEstado() {
        return estado;
    }

    /** @param estado Estado actual de la oferta */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * @return Representación textual de la oferta
     */
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
