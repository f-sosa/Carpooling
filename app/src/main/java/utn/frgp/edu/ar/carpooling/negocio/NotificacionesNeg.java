package utn.frgp.edu.ar.carpooling.negocio;
import java.util.concurrent.ExecutionException;

import utn.frgp.edu.ar.carpooling.entities.Notificaciones;
public interface NotificacionesNeg {

    public Boolean AñadirNotificacion(Notificaciones not) throws ExecutionException, InterruptedException;
}
