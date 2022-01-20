package utn.frgp.edu.ar.carpooling.entities;

public class Ciudad {

    private int idCiudad;
    private int idProvincia;
    private String nombre;
    private boolean estadoCiudad;

    public Ciudad(){

    }

    public Ciudad(int idCiudad, int idProvincia, String nombre, boolean estadoCiudad) {
        this.idCiudad = idCiudad;
        this.idProvincia = idProvincia;
        this.nombre = nombre;
        this.estadoCiudad = estadoCiudad;
    }

    public int getIdCiudad() {
        return idCiudad;
    }

    public void setIdCiudad(int idCiudad) {
        this.idCiudad = idCiudad;
    }

    public int getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(int idProvincia) {
        this.idProvincia = idProvincia;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean getEstadoCiudad() {
        return estadoCiudad;
    }

    public void setEstadoCiudad(boolean estadoCiudad) {
        this.estadoCiudad = estadoCiudad;
    }

}
