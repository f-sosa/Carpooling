package utn.frgp.edu.ar.carpooling.entities;

public class Provincia {

    private int idProvincia;
    private String nombre;
    private boolean estadoProvincia;


    public Provincia(){

    }

    public Provincia(int idProvincia, String nombre, boolean estadoProvincia) {
        this.idProvincia = idProvincia;
        this.nombre = nombre;
        this.estadoProvincia = estadoProvincia;
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

    public boolean isEstadoProvincia() {
        return estadoProvincia;
    }

    public void setEstadoProvincia(boolean estadoProvincia) {
        this.estadoProvincia = estadoProvincia;
    }

}
