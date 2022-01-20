package utn.frgp.edu.ar.carpooling.negocioImpl;

import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutionException;
import utn.frgp.edu.ar.carpooling.conexion.DataDB;
import utn.frgp.edu.ar.carpooling.entities.Notificaciones;
import utn.frgp.edu.ar.carpooling.negocio.NotificacionesNeg;

public class NotificacionesNegImpl implements NotificacionesNeg {

    Notificaciones noti = new Notificaciones();
    @RequiresApi(api = Build.VERSION_CODES.O)

    public Boolean AñadirNotificacion(Notificaciones not) throws ExecutionException, InterruptedException {

        noti=not;
        //UTILIZO EL GET PARA ESPERAR A QUE EL HILO TERMINE DE EJECUTARSE.
        Boolean resultado =  new NotificacionesNegImpl.InsertarNotificacion().execute().get();


        return resultado;
    }

    private class InsertarNotificacion extends AsyncTask<Void,Integer, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += "INSERT INTO Notificaciones";
                query += "(UsuarioId,";
                query += "Mensaje,";
                query += "EstadoNotificacion,";
                query += "EstadoRegistro)";
                query += "VALUES";
                query += "(";
                query +=  "'" + noti.getUsuarioId()+ "',";
                query +=  "'" + noti.getMensaje()+ "',";
                query +=  "'" + noti.getEstadoNotificacion()+ "',";
                query +=  "'" + noti.getEstado() + "'";
                query += ")";

                int resultado = st.executeUpdate(query);

                if(resultado>0){
                    return true;
                }
                else {return false;}

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

}
