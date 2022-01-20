package utn.frgp.edu.ar.carpooling.entities;

import java.time.LocalDate;
import java.util.Date;

public class Usuario {

    private Integer Id;
    private String Email;
    private Rol Rol;
    private String Password;
    private String Nombre;
    private String Apellido;
    private LocalDate Nacimiento;
    private String Telefono;
    private String Dni;
    private boolean EstadoRegistro;

    public Usuario() {
    }

    public Usuario(String email, Rol rol, String password, String nombre, String apellido, LocalDate nacimiento, String telefono, String dni, boolean estadoRegistro) {
        Email = email;
        Rol = rol;
        Password = password;
        Nombre = nombre;
        Apellido = apellido;
        Nacimiento = nacimiento;
        Telefono = telefono;
        Dni = dni;
        EstadoRegistro = estadoRegistro;
    }

    public Usuario(Integer id, String email, Rol rol, String password, String nombre, String apellido, LocalDate nacimiento, String telefono, String dni, boolean estadoRegistro) {
        Id = id;
        Email = email;
        Rol = rol;
        Password = password;
        Nombre = nombre;
        Apellido = apellido;
        Nacimiento = nacimiento;
        Telefono = telefono;
        Dni = dni;
        EstadoRegistro = estadoRegistro;
    }

    public Integer getId() {
        return Id;
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public utn.frgp.edu.ar.carpooling.entities.Rol getRol() {
        return Rol;
    }

    public void setRol(utn.frgp.edu.ar.carpooling.entities.Rol rol) {
        Rol = rol;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellido() {
        return Apellido;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public LocalDate getNacimiento() {
        return Nacimiento;
    }

    public void setNacimiento(LocalDate nacimiento) {
        Nacimiento = nacimiento;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String telefono) {
        Telefono = telefono;
    }

    public String getDni() {
        return Dni;
    }

    public void setDni(String dni) {
        Dni = dni;
    }

    public boolean isEstadoRegistro() {
        return EstadoRegistro;
    }

    public void setEstadoRegistro(boolean estadoRegistro) {
        EstadoRegistro = estadoRegistro;
    }
}
