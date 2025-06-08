package com.example.qtfood_desktop.Modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
/**
 * Representa una reserva realizada por un cliente para una fecha y hora determinadas.
 * Incluye información del cliente, número de personas, y el estado de la reserva.
 */
public class Reserva {
    private int idReserva;
    private String nombre;
    private String telefono;
    private LocalDateTime fechaHora;
    private int nPersonas;
    private String estado;

    public Reserva(int idReserva, String nombre, String telefono, LocalDateTime fechaHora, int nPersonas, String estado) {
        this.idReserva = idReserva;
        this.nombre = nombre;
        this.telefono = telefono;
        this.fechaHora = fechaHora;
        this.nPersonas = nPersonas;
        this.estado = estado;
    }

    public int getIdReserva() { return idReserva; }
    public String getNombre() { return nombre; }
    public String getTelefono() { return telefono; }
    public LocalDateTime getFechaHora() { return fechaHora; }

    // NUEVO: Método para obtener la fecha como LocalDate o String
    public String getFechaSolo() {
        return fechaHora.toLocalDate().toString();
    }

    // NUEVO: Método para obtener la hora como String
    public String getHoraSolo() {
        return fechaHora.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
    }

    public int getNPersonas() { return nPersonas; }
    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Reserva{" +
                "idReserva=" + idReserva +
                ", nombre='" + nombre + '\'' +
                ", telefono='" + telefono + '\'' +
                ", fechaHora=" + fechaHora +
                ", nPersonas=" + nPersonas +
                ", estado='" + estado + '\'' +
                '}';
    }
}
