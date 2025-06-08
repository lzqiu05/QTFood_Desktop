package com.example.qtfood_desktop.Modelo;

import java.io.Serializable;

import java.io.Serializable;

/**
 * Representa un administrador del sistema con credenciales de acceso.
 * Implementa {@link Serializable} para permitir su serialización.
 */
public class Admin implements Serializable {

    private String usuario;
    private String password;

    /** Constructor vacío. */
    public Admin() {
    }

    /**
     * Constructor con usuario y contraseña.
     *
     * @param usuario Nombre de usuario
     * @param password Contraseña del administrador
     */
    public Admin(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }

    /** @return Nombre de usuario */
    public String getUsuario() {
        return usuario;
    }

    /** @param usuario Nombre de usuario */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /** @return Contraseña del administrador */
    public String getPassword() {
        return password;
    }

    /** @param password Contraseña del administrador */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return Representación textual del administrador
     */
    @Override
    public String toString() {
        return "Admin{" +
                "usuario='" + usuario + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
