package utn.frgp.edu.ar.carpooling.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Viaje {

    private int idViaje;
    private Integer idConductor;
    private Provincia provOrigen;
    private Ciudad ciudadOrigen;
    private Provincia provDestino;
    private Ciudad ciudadDestino;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private int CantPasajeros;
    private String estadoViaje;
    private boolean estadoRegistro;

    public Viaje(){

    }

    public Viaje(int idViaje, Integer idConductor, Provincia provOrigen, Ciudad ciudadOrigen, Provincia provDestino, Ciudad ciudadDestino, LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin, int cantPasajeros, String estadoViaje, boolean estadoRegistro) {
        this.idViaje = idViaje;
        this.idConductor = idConductor;
        this.provOrigen = provOrigen;
        this.ciudadOrigen = ciudadOrigen;
        this.provDestino = provDestino;
        this.ciudadDestino = ciudadDestino;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        CantPasajeros = cantPasajeros;
        this.estadoViaje = estadoViaje;
        this.estadoRegistro = estadoRegistro;
    }

    public int getIdViaje() {
        return idViaje;
    }

    public void setIdViaje(int idViaje) {
        this.idViaje = idViaje;
    }

    public Integer getIdConductor() {
        return idConductor;
    }

    public void setIdConductor(Integer idConductor) {
        this.idConductor = idConductor;
    }

    public Provincia getProvOrigen() {
        return provOrigen;
    }

    public void setProvOrigen(Provincia provOrigen) {
        this.provOrigen = provOrigen;
    }

    public Ciudad getCiudadOrigen() {
        return ciudadOrigen;
    }

    public void setCiudadOrigen(Ciudad ciudadOrigen) {
        this.ciudadOrigen = ciudadOrigen;
    }

    public Provincia getProvDestino() {
        return provDestino;
    }

    public void setProvDestino(Provincia provDestino) {
        this.provDestino = provDestino;
    }

    public Ciudad getCiudadDestino() {
        return ciudadDestino;
    }

    public void setCiudadDestino(Ciudad ciudadDestino) {
        this.ciudadDestino = ciudadDestino;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public int getCantPasajeros() {
        return CantPasajeros;
    }

    public void setCantPasajeros(int cantPasajeros) {
        CantPasajeros = cantPasajeros;
    }

    public String getEstadoViaje() {
        return estadoViaje;
    }

    public void setEstadoViaje(String estadoViaje) {
        this.estadoViaje = estadoViaje;
    }

    public boolean isEstadoRegistro() {
        return estadoRegistro;
    }

    public void setEstadoRegistro(boolean estadoRegistro) {
        this.estadoRegistro = estadoRegistro;
    }
}
