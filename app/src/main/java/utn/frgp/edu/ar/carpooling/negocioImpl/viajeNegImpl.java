package utn.frgp.edu.ar.carpooling.negocioImpl;

import android.os.AsyncTask;
import android.os.Build;
import android.widget.ArrayAdapter;

import androidx.annotation.RequiresApi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import utn.frgp.edu.ar.carpooling.conexion.DataDB;
import utn.frgp.edu.ar.carpooling.entities.Ciudad;
import utn.frgp.edu.ar.carpooling.entities.Viaje;
import utn.frgp.edu.ar.carpooling.negocio.viajeNeg;
import utn.frgp.edu.ar.carpooling.utils.EnumsErrores;

public class viajeNegImpl implements viajeNeg {

    private Viaje objOrigViaje;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int validarDatosViaje(Viaje v) {
        int valor = -1;
        if(v.getProvDestino().getIdProvincia() == v.getProvOrigen().getIdProvincia() &&
                v.getCiudadDestino().getIdCiudad() == v.getCiudadOrigen().getIdCiudad()){
            valor = EnumsErrores.viaje_DestinoyOrigenIguales.ordinal();
        }

        LocalDateTime time = LocalDateTime.now(ZoneId.of("UTC-3"));

        //VOLVER A HABILITAR, AMI NO ME ANDA! JONNA
        if(v.getFechaHoraInicio().compareTo(time)<0){
            valor = EnumsErrores.viaje_FechayHoraAnteriorActual.ordinal();
        }
        
        return valor;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean validarViajeConductorEnRangoFechayHora(Viaje obj) throws ExecutionException, InterruptedException {
        objOrigViaje = obj;
        boolean hayConflictoConViajes = false;
        //UTILIZO EL GET PARA ESPERAR A QUE EL HILO TERMINE DE EJECUTARSE.
        ResultSet resultado = new buscarViajeEnRangoTiempo().execute().get();

        try {
            // SI EL ID DE VIAJE ESTA DEFINIDO (MAYOR A 0) SIFNIFICA Q ESTAMOS EDITANDO UN VIAJE EXISTENTE
            // ENTONCES AL TRAER LOS RESULTADOS, NO DEBE TENERSE EN CUENTA A SI MISMO SI SE ENCUENTRA EN EL RANGO HORARIO
            if (obj.getIdViaje() > 0) {
                while (resultado.next()) {
                    if (obj.getIdViaje() != resultado.getInt("Id")) hayConflictoConViajes = true;
                }
            } else {
                while (resultado.next()) {
                    hayConflictoConViajes = true;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return hayConflictoConViajes;
    }

    @Override
    public boolean validarSolicitudEnRangoFechayHora(Viaje obj) throws ExecutionException, InterruptedException {
        objOrigViaje = obj;
        boolean hayConflictoConViajes = false;
        //UTILIZO EL GET PARA ESPERAR A QUE EL HILO TERMINE DE EJECUTARSE.
        ResultSet resultado = new buscarSolicitudEnRangoTiempo().execute().get();

        try {
            if (obj.getIdViaje() > 0) {
                while (resultado.next()) {
                    if (obj.getIdViaje() != resultado.getInt("Id")) hayConflictoConViajes = true;
                }
            } else {
                while (resultado.next()) {
                    hayConflictoConViajes = true;
                }
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return hayConflictoConViajes;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean validarViajePasajeroEnRangoFechayHora(Viaje obj) throws ExecutionException, InterruptedException {
        objOrigViaje = obj;
        boolean vBoleana = false;
        //UTILIZO EL GET PARA ESPERAR A QUE EL HILO TERMINE DE EJECUTARSE.
        ResultSet resultado = new buscarViajePasajeroEnRangoTiempo().execute().get();

        try {
            while (resultado.next()) {
                vBoleana = true;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return vBoleana;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public LocalDate ObtenerFechaFinalizacionViaje(int NroViaje) throws ExecutionException, InterruptedException {
        Date fechaFinalizacion = null;
        objOrigViaje = new Viaje();
        objOrigViaje.setIdViaje(NroViaje);

        //UTILIZO EL GET PARA ESPERAR A QUE EL HILO TERMINE DE EJECUTARSE.
        ResultSet resultado = new ObtenerFechaFinalizacion().execute().get();

        try {
            while (resultado.next()) {
                fechaFinalizacion = resultado.getDate("FechaHoraFinalizacion");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return LocalDate.of(fechaFinalizacion.getYear(),fechaFinalizacion.getMonth(),fechaFinalizacion.getDay());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private class buscarViajeEnRangoTiempo extends AsyncTask<Void,Integer, ResultSet>{

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";

                //HABILITENLO SI NO MORIMOS TODOS!!!!!   YO NO LO PUEDO PROBAR. JONNA
                LocalDateTime fechaInicio,fechaFin;
                fechaInicio = objOrigViaje.getFechaHoraInicio().plusHours(-3);
                fechaFin = objOrigViaje.getFechaHoraInicio().plusHours(+3);

                //QUERY QUE HAY QUE HABILITAR!! YO NO LA PUEDO PROBAR. JONNA
                query = "SELECT * FROM `Viajes` WHERE (FechaHoraInicio BETWEEN '" + fechaInicio + "' AND '" + fechaFin + "') AND ConductorId = '" + objOrigViaje.getIdConductor() + "'" ;


                //QUERY PARA PODER PROBAR
                //query = "SELECT * FROM `Viajes` WHERE (FechaHoraInicio BETWEEN '2021-10-27 11:00:00' AND '2021-10-27 15:15:00') AND ConductorEmail = 'tobi@mail.com'" ;

                return st.executeQuery(query);

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private class buscarSolicitudEnRangoTiempo extends AsyncTask<Void,Integer, ResultSet>{

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";

                //HABILITENLO SI NO MORIMOS TODOS!!!!!   YO NO LO PUEDO PROBAR. JONNA
                LocalDateTime fechaInicio,fechaFin;
                fechaInicio = objOrigViaje.getFechaHoraInicio().plusHours(-3);
                fechaFin = objOrigViaje.getFechaHoraInicio().plusHours(+3);

                //QUERY QUE HAY QUE HABILITAR!! YO NO LA PUEDO PROBAR. JONNA
                query = "SELECT * FROM `Solicitudes` WHERE (FechaHoraInicio BETWEEN '" + fechaInicio + "' AND '" + fechaFin + "') AND PasajeroId = '" + objOrigViaje.getIdConductor() + "'" ;


                //QUERY PARA PODER PROBAR
                //query = "SELECT * FROM `Solicitudes` WHERE (FechaHoraInicio BETWEEN '2021-11-22 14:30:00' AND '2021-11-22 18:40:00') AND PasajeroEmail = 'tobi@mail.com'" ;

                return st.executeQuery(query);

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private class ObtenerFechaFinalizacion extends AsyncTask<Void,Integer, ResultSet>{

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";

                query = "SELECT * FROM Viajes WHERE Id = " + objOrigViaje.getIdViaje() ;

                return st.executeQuery(query);

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private class buscarViajePasajeroEnRangoTiempo extends AsyncTask<Void,Integer, ResultSet>{

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";

                //HABILITENLO SI NO MORIMOS TODOS!!!!!   YO NO LO PUEDO PROBAR. JONNA
                LocalDateTime fechaInicio,fechaFin;
                fechaInicio = objOrigViaje.getFechaHoraInicio().plusHours(-3);
                fechaFin = objOrigViaje.getFechaHoraInicio().plusHours(+3);

                //QUERY QUE HAY QUE HABILITAR!! YO NO LA PUEDO PROBAR. JONNA
                query = "SELECT * FROM PasajerosPorViaje pxv ";
                query += "INNER JOIN Viajes v ";
                query += "ON pxv.ViajeId = v.Id ";
                query += "WHERE (pxv.UsuarioId = '" + objOrigViaje.getIdConductor() + "' ";
                query += "AND (pxv.EstadoPasajero = 'Aceptado' OR pxv.EstadoPasajero = 'Pendiente')) ";
                query += "AND (v.EstadoViaje = 'En Espera' ";
                query += "AND (v.FechaHoraInicio BETWEEN '" + fechaInicio + "' AND '" + fechaFin + "'))";
                //QUERY PARA PODER PROBAR
                //query += "AND (v.FechaHoraInicio BETWEEN '2021-11-01 12:00:00' AND '2021-11-01 14:15:00'))";

                return st.executeQuery(query);

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }


}
