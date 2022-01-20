package utn.frgp.edu.ar.carpooling.entities;

public class Notificaciones {


    private int id;
    private Integer UsuarioId;
    private String Mensaje;
    private String EstadoNotificacion;
    private int estado;

    public Notificaciones() {
    }

    public Notificaciones(int id, Integer usuarioId, String usuarioRolId, String mensaje, String estadoNotificacion, int estado) {
        this.id = id;
        UsuarioId = usuarioId;
        Mensaje = mensaje;
        EstadoNotificacion = estadoNotificacion;
        this.estado = estado;
    }

    public String getEstadoNotificacion() {
        return EstadoNotificacion;
    }

    public void setEstadoNotificacion(String estadoNotificacion) {
        EstadoNotificacion = estadoNotificacion;
    }

    public int getId() {
        return id;
    }

    public String getMensaje() {
        return Mensaje;
    }

    public int getEstado() {
        return estado;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setMensaje(String mensaje) {
        Mensaje = mensaje;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public Integer getUsuarioId() {
        return UsuarioId;
    }

    public void setUsuarioId(Integer usuarioId) {
        UsuarioId = usuarioId;
    }
}
