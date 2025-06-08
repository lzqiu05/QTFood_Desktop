package com.example.qtfood_desktop.Modelo;

import java.time.LocalDateTime;

/**
 * Representa un producto del sistema con su información básica como nombre, precio,
 * descripción, stock, imagen, categoría y estado.
 */
public class Producto {
    private int idProducto;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private String imagenUrl;
    private String categoria;
    private String estado;

    /**
     * Constructor vacío.
     */
    public Producto() {
    }

    /**
     * Constructor con todos los atributos del producto.
     *
     * @param idProducto ID del producto
     * @param nombre Nombre del producto
     * @param descripcion Descripción del producto
     * @param precio Precio del producto
     * @param stock Cantidad disponible en stock
     * @param imagenUrl URL de la imagen del producto
     * @param categoria Categoría del producto
     * @param estado Estado del producto (activo/inactivo)
     */
    public Producto(int idProducto, String nombre, String descripcion, double precio, int stock, String imagenUrl, String categoria, String estado) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.imagenUrl = imagenUrl;
        this.categoria = categoria;
        this.estado = estado;
    }

    /** @return ID del producto */
    public int getIdProducto() {
        return idProducto;
    }

    /** @param idProducto ID del producto */
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    /** @return Nombre del producto */
    public String getNombre() {
        return nombre;
    }

    /** @param nombre Nombre del producto */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /** @return Descripción del producto */
    public String getDescripcion() {
        return descripcion;
    }

    /** @param descripcion Descripción del producto */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /** @return Precio del producto */
    public double getPrecio() {
        return precio;
    }

    /** @param precio Precio del producto */
    public void setPrecio(double precio) {
        this.precio = precio;
    }

    /** @return Cantidad en stock */
    public int getStock() {
        return stock;
    }

    /** @param stock Cantidad en stock */
    public void setStock(int stock) {
        this.stock = stock;
    }

    /** @return URL de la imagen del producto */
    public String getImagenUrl() {
        return imagenUrl;
    }

    /** @param imagenUrl URL de la imagen del producto */
    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    /** @return Categoría del producto */
    public String getCategoria() {
        return categoria;
    }

    /** @param categoria Categoría del producto */
    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    /** @return Estado del producto */
    public String getEstado() {
        return estado;
    }

    /** @param estado Estado del producto */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * @return Cadena con todos los detalles del producto
     */
    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio=" + precio +
                ", imagenUrl='" + imagenUrl + '\'' +
                ", categoría='" + categoria + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
