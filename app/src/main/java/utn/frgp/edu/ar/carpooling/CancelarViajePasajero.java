package utn.frgp.edu.ar.carpooling;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import utn.frgp.edu.ar.carpooling.conexion.DataDB;
import utn.frgp.edu.ar.carpooling.entities.Notificaciones;
import utn.frgp.edu.ar.carpooling.negocioImpl.NotificacionesNegImpl;

public class CancelarViajePasajero extends AppCompatActivity {
    Context contexto;
    String nombreUsuario, apellidoUsuario, emailUsuario, rolUsuario, idUsuario, conductorId;
    GridView GrViajeSolicitado;
    String NroViaje,EstadoViaje,EmailConductor;
    RatingBar ratingBar;
    TextView NombreConductor,CelularConductor,Trasladoa;
    Button BotonCancelarViaj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelar_viaje_pasajero);
        contexto = this;
        GrViajeSolicitado=findViewById(R.id.GrViajeCancelarViajePasajero);
        ratingBar=findViewById(R.id.ratingBarCanViajPas);
        Trasladoa=findViewById(R.id.TxtTrasladoACanPas);
        NombreConductor=findViewById(R.id.TxtNombreConductorCanviaj);
        CelularConductor=findViewById(R.id.TxtCelularConductorCanViaj);
        BotonCancelarViaj=findViewById(R.id.BtnCancelarVIajPas);

        BotonCancelarViaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CancelarleViaje().execute();
            }
        });

        SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        nombreUsuario = spSesion.getString("Nombre","No hay datos");
        apellidoUsuario = spSesion.getString("Apellido","No hay datos");
        emailUsuario = spSesion.getString("Email","No hay datos");
        rolUsuario = spSesion.getString("Rol","No hay datos");
        idUsuario = spSesion.getString("Id","No hay datos");

        String Rol="";
        if(rolUsuario.equals("CON")){
            Rol="Conductor";
        }else{
            Rol="Pasajero";
        }
        getSupportActionBar().setTitle(nombreUsuario+" "+ apellidoUsuario+" Rol: "+Rol);
        NroViaje=getIntent().getStringExtra("NroViaje");
        EstadoViaje=getIntent().getStringExtra("EstadoViaje");
        new CargarViajeSeleccionado().execute();
    }
    private class CargarViajeSeleccionado extends AsyncTask<Void,Integer, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " SELECT 	vj.FechaHoraInicio,";
                query += "  	vj.Id,";
                query += " 		    pr1.Nombre ProvinciaOrigen,";
                query += "          ci1.Nombre CiudadOrigen,";
                query += "          pr2.Nombre ProvinciaDestino,";
                query += "          ci2.Nombre CiudadDestino,";
                query += "          vj.FechaHoraFinalizacion,";
                query += "          vj.CantidadPasajeros,";
                query += "          vj.EstadoViaje,";
                query += "          vj.ConductorId";
                query += " FROM Viajes vj";
                query += " LEFT JOIN Provincias pr1";
                query += " 	ON pr1.Id = vj.ProvinciaOrigenId";
                query += " LEFT JOIN Provincias pr2";
                query += " 	ON pr2.Id = vj.ProvinciaDestinoId";
                query += " LEFT JOIN Ciudades ci1";
                query += " 	ON ci1.Id = vj.CiudadOrigenId";
                query += " LEFT JOIN Ciudades ci2";
                query += " 	ON ci2.Id = vj.CiudadDestinoId";
                query += " 	Where	vj.Id='" + NroViaje + "'";
                query += " ORDER BY FechaHoraInicio ASC";

                return st.executeQuery(query);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResultSet resultados) {
            super.onPostExecute(resultados);
            try {
                List<Map<String, String>> itemsGrilla = new ArrayList<Map<String, String>>();

                while (resultados.next()) {
                    Map<String, String> item = new HashMap<String, String>();
                    item.put("NroViaje", resultados.getString("Id"));
                    item.put("origen", resultados.getString("CiudadOrigen") + ", " + resultados.getString("ProvinciaOrigen"));
                    item.put("destino", resultados.getString("CiudadDestino") + ", " + resultados.getString("ProvinciaDestino"));
                    item.put("fecha", resultados.getString("FechaHoraInicio").substring(8,10) + "/" + resultados.getString("FechaHoraInicio").substring(5,7) + "/" + resultados.getString("FechaHoraInicio").substring(2,4));
                    item.put("hora", resultados.getString("FechaHoraInicio").substring(11,13) + ":" + resultados.getString("FechaHoraInicio").substring(14,16));
                    item.put("estado", resultados.getString("EstadoViaje"));

                    conductorId = resultados.getString("ConductorId");

                    itemsGrilla.add(item);
                    //EmailConductor=resultados.getString("ConductorEmail");
                }

                String[] from = {"NroViaje","origen", "destino", "fecha", "hora","estado"};
                int[] to = {R.id.tvGridItemViajeNroViaje,R.id.tvGridItemViajeOrigen, R.id.tvGridItemViajeDestino, R.id.tvGridItemViajeOrigenFecha, R.id.tvGridItemViajeOrigenHora, R.id.tvGridItemEstadoViaje};
                SimpleAdapter simpleAdapter = new SimpleAdapter(contexto, itemsGrilla, R.layout.grid_item_viaje, from, to);
                GrViajeSolicitado.setAdapter(simpleAdapter);

                //cargo los datos del conductor de ese viaje

                new CargarDatos().execute();


            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private class CargarDatos extends AsyncTask<Void,Integer,ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " SELECT 	usu.Nombre,";
                query += "  	    usu.Apellido,";
                query += " 		    usu.Telefono,";
                query += " 		    usu.Email,";
                query += " 		    usu.Rol";
                query += " From Usuarios usu";
                query += " 	Where	 usu.Id='" + conductorId + "'";

                return st.executeQuery(query);

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResultSet resultados) {
            super.onPostExecute(resultados);
            try {

                while (resultados.next()) {
                    NombreConductor.setText(resultados.getString("Nombre")+" "+resultados.getString("Apellido"));
                    CelularConductor.setText(resultados.getString("Telefono"));

                }
                new CargarCalificaciones().execute();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private class CargarCalificaciones extends AsyncTask<Void,Integer, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += "SELECT AVG(cal.Calificacion) as promedio FROM Calificaciones cal inner join Usuarios usu on usu.Email=cal.UsuarioEmail  Where	usu.Id='" + conductorId + "'";


                return st.executeQuery(query);

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResultSet resultados) {
            super.onPostExecute(resultados);
            try {
                Float promedio = null;
                while (resultados.next()) {
                    promedio = resultados.getFloat("promedio");
                }

                if(promedio == 0 ){

                    ratingBar.setIsIndicator(true);
                    return;
                }

                //Le agrego el promedio al rating para que pueda mostrarlo
                ratingBar.setRating(promedio);
                //No se puede calificarlo
                ratingBar.setIsIndicator(true);

                new ContarCalificaciones().execute();
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private class ContarCalificaciones extends AsyncTask<Void,Integer,ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += "SELECT COUNT(cal.Calificacion) as cantidad FROM Calificaciones cal inner join Usuarios usu on usu.Email=cal.UsuarioEmail  Where	usu.Id='" + conductorId + "'";


                return st.executeQuery(query);

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResultSet resultados) {
            super.onPostExecute(resultados);
            try {
                Integer cantidad = null;
                while (resultados.next()) {
                    cantidad = resultados.getInt("cantidad");
                }

                Trasladoa.setText(cantidad > 0 ? cantidad.toString()  + " Pasajeros lo calificaron" : "No Traslado a ningun pasajero");

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private class CancelarleViaje extends AsyncTask<Void,Integer,Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += "UPDATE PasajerosPorViaje SET ";
                query += "EstadoPasajero='Rechazado'";
                query += " WHERE ViajeId='" + NroViaje + "'and UsuarioEmail = '" + emailUsuario+"'";

                int resultado = st.executeUpdate(query);
                return resultado > 0;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if (resultado) {
                if(EstadoViaje.equals("Aceptado")){
                NotificacionesNegImpl NotiNeg = new NotificacionesNegImpl();
                utn.frgp.edu.ar.carpooling.entities.Notificaciones Noti = new Notificaciones();
                Noti.setUsuarioId(Integer.parseInt(conductorId));
                Noti.setMensaje("El pasajero "+nombreUsuario+" "+apellidoUsuario+" ha abandonado el viaje "+NroViaje+" ");
                Noti.setEstadoNotificacion("P");
                Noti.setEstado(1);
                try {
                    NotiNeg.AñadirNotificacion(Noti);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }}
                Toast.makeText(contexto, "Se envió la notificacion al conductor", Toast.LENGTH_SHORT).show();
            }
            else System.out.println("No se pudo Abandonar el viaje");
        }
    }

}