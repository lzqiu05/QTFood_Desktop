package com.example.qtfood_desktop.Modelo;

import java.time.LocalDateTime;

import java.time.LocalDateTime;

import java.time.LocalDateTime;

/**
 * Representa un pedido realizado por un usuario, incluyendo la fecha,
 * el total, método de pago, dirección de entrega y estado actual.
 */
public class Pedido {
    private int idPedido;
    private String Usuario;
    private LocalDateTime fecha;
    private double total;
    private String metodoPago;
    private String direccion;
    private String estado;

    /** Constructor vacío. */
    public Pedido() {
    }

    /**
     * Constructor con todos los atributos del pedido.
     *
     * @param idPedido ID del pedido
     * @param Usuario Nombre del usuario que realizó el pedido
     * @param fecha Fecha y hora del pedido
     * @param total Total a pagar
     * @param metodoPago Método de pago utilizado
     * @param direccion Dirección de entrega
     * @param estado Estado del pedido (ej: Pendiente, Enviado, Entregado)
     */
    public Pedido(int idPedido, String Usuario, LocalDateTime fecha, double total, String metodoPago, String direccion, String estado) {
        this.idPedido = idPedido;
        this.Usuario = Usuario;
        this.fecha = fecha;
        this.total = total;
        this.metodoPago = metodoPago;
        this.direccion = direccion;
        this.estado = estado;
    }

    /** @return ID del pedido */
    public int getIdPedido() {
        return idPedido;
    }

    /** @param idPedido ID del pedido */
    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    /** @return Usuario que realizó el pedido */
    public String getUsuario() {
        return Usuario;
    }

    /** @param usuario Usuario que realizó el pedido */
    public void setUsuario(String usuario) {
        this.Usuario = usuario;
    }

    /** @return Fecha y hora del pedido */
    public LocalDateTime getFecha() {
        return fecha;
    }

    /** @param fecha Fecha y hora del pedido */
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    /** @return Total del pedido */
    public double getTotal() {
        return total;
    }

    /** @param total Total del pedido */
    public void setTotal(double total) {
        this.total = total;
    }

    /** @return Método de pago */
    public String getMetodoPago() {
        return metodoPago;
    }

    /** @param metodoPago Método de pago */
    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    /** @return Dirección de entrega */
    public String getDireccion() {
        return direccion;
    }

    /** @param direccion Dirección de entrega */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /** @return Estado actual del pedido */
    public String getEstado() {
        return estado;
    }

    /** @param estado Estado actual del pedido */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * @return Representación en texto del pedido
     */
    @Override
    public String toString() {
        return "Pedido{" +
                "idPedido=" + idPedido +
                ", Usuario=" + Usuario +
                ", fecha=" + fecha +
                ", total=" + total +
                ", metodoPago='" + metodoPago + '\'' +
                ", direccion='" + direccion + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
