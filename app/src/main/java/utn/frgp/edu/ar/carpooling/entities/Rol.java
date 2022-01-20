package utn.frgp.edu.ar.carpooling.entities;

public class Rol {
    private String Id;
    private String Nombre;
    private boolean EstadoRegistro;

    public Rol() {
    }

    public Rol(String id) {
        Id = id;
    }

    public Rol(String id, String nombre, boolean estadoRegistro) {
        Id = id;
        Nombre = nombre;
        EstadoRegistro = estadoRegistro;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public boolean isEstadoRegistro() {
        return EstadoRegistro;
    }

    public void setEstadoRegistro(boolean estadoRegistro) {
        EstadoRegistro = estadoRegistro;
    }
}
