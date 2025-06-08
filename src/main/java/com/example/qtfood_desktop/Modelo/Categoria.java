package com.example.qtfood_desktop.Modelo;

/**
 * Representa una categoría de productos
 */
public class Categoria {

    private String nombre;

    /** Constructor vacío. */
    public Categoria() {
    }

    /**
     * Constructor con nombre de la categoría.
     *
     * @param nombre Nombre de la categoría
     */
    public Categoria(String nombre) {
        this.nombre = nombre;
    }

    /** @return Nombre de la categoría */
    public String getNombre() {
        return nombre;
    }

    /** @param nombre Nombre de la categoría */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return Representación textual de la categoría
     */
    @Override
    public String toString() {
        return "Categoria{" +
                "nombre='" + nombre + '\'' +
                '}';
    }
}

