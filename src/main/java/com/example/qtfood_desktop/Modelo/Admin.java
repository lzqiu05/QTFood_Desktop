package com.example.qtfood_desktop.Modelo;

import java.io.Serializable;

public class Admin  implements Serializable {
    private String usuario;
    private String password;

    public Admin() {
    }

    public Admin(String usuario, String password) {
        this.usuario = usuario;
        this.password = password;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "usuario='" + usuario + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
