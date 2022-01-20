package utn.frgp.edu.ar.carpooling.negocio;

import java.time.LocalDate;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import utn.frgp.edu.ar.carpooling.entities.Viaje;

public interface viajeNeg {

    int validarDatosViaje(Viaje v);
    boolean validarViajeConductorEnRangoFechayHora(Viaje obj) throws ExecutionException, InterruptedException;
    boolean validarSolicitudEnRangoFechayHora(Viaje obj) throws ExecutionException, InterruptedException;
    boolean validarViajePasajeroEnRangoFechayHora(Viaje obj) throws ExecutionException, InterruptedException;
    LocalDate ObtenerFechaFinalizacionViaje(int NroViaje) throws ExecutionException, InterruptedException;
}
