package utn.frgp.edu.ar.carpooling;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import utn.frgp.edu.ar.carpooling.conexion.DataDB;
import utn.frgp.edu.ar.carpooling.entities.Notificaciones;
import utn.frgp.edu.ar.carpooling.entities.Rol;
import utn.frgp.edu.ar.carpooling.entities.Usuario;
import utn.frgp.edu.ar.carpooling.negocioImpl.NotificacionesNegImpl;

public class ResponderSolicitud extends AppCompatActivity {
    Context contexto;
    TextView Nombre,viajocon, Numero, tvNoFreeSeats;
    String Email,NroViaje,Asientos,Pasajeros,IdSolicitante;
    RatingBar Rating;
    Button botoncancelar,botonaceptar;
    GridView grillaVerViaje;
    Usuario usuarioACalificar;
    String nombreUsuario,apellidoUsuario,emailUsuario,rolUsuario,idUsuario, fechaHoraInicioString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responder_solicitud);
        contexto = this;

        NroViaje=getIntent().getStringExtra("NroViaje");
        Email=getIntent().getStringExtra("Email");
        IdSolicitante=getIntent().getStringExtra("IdSolicitante");
        Nombre=findViewById(R.id.TxtNombreRespSol);
        Rating=findViewById(R.id.ratingBarResponderSoli);
        viajocon=findViewById(R.id.TxtViajoRespSol);
        grillaVerViaje=(GridView) findViewById(R.id.GrResponderSoli);
        botoncancelar=findViewById(R.id.btnResponderSoliRechazar);
        botonaceptar=findViewById(R.id.btnRespSoliAceptar);
        Numero=findViewById(R.id.textView17);
        tvNoFreeSeats=findViewById(R.id.textView20);


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

        botoncancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new RechazarPasajero().execute();
               // Intent pagVerViaje= new Intent(contexto,Ver_Viajes.class);
                //pagVerViaje.putExtra("NroViaje",NroViaje);
                //pagVerViaje.putExtra("EstadoViaje", EstadoViaje);
               // startActivity(pagVerViaje);
            }
        });

        botonaceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CargarAsientosDisponibles().execute();


                // Intent pagVerViaje= new Intent(contexto,Ver_Viajes.class);
                //pagVerViaje.putExtra("NroViaje",NroViaje);
                //pagVerViaje.putExtra("EstadoViaje", EstadoViaje);
                // startActivity(pagVerViaje);
            }
        });

        new CargarDatos().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu miMenu) {
        getMenuInflater().inflate(R.menu.menu_conductor, miMenu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem opcionMenu) {
        int id = opcionMenu.getItemId();

        if(id == R.id.miperfil) {
            finish();
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }
        if(id == R.id.misViajes) {
            finish();
            Intent intent = new Intent(this, MisViajes.class);
            startActivity(intent);
        }
        if(id == R.id.crearViaje) {
            finish();
            Intent intent = new Intent(this, NuevoViaje.class);
            startActivity(intent);
        }
        if(id == R.id.notificaciones) {
            finish();
            Intent intent = new Intent(this, utn.frgp.edu.ar.carpooling.Notificaciones.class);
            startActivity(intent);
        }

        if(id == R.id.cerrarSesion) {

            SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = spSesion.edit();
            editor.clear();
            editor.commit();
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(opcionMenu);
    }

    private class CargarDatos extends AsyncTask<Void,Integer, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " CALL InfoParaResponderSolicitud('" + IdSolicitante + "'," + NroViaje + ");";

                return st.executeQuery(query);

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(ResultSet resultados) {
            super.onPostExecute(resultados);
            usuarioACalificar = new Usuario();
            try {
                Float promedio = 0f;
                Integer cantidad = null;
                Integer asientosLibres = 0;
                List<Map<String, String>> itemsGrilla = new ArrayList<Map<String, String>>();
                while (resultados.next()) {

                    Nombre.setText(resultados.getString("Nombre") + " " + resultados.getString("Apellido"));
                    Numero.setText(resultados.getString("Telefono"));

                    Rol r = new Rol();
                    r.setId(resultados.getString("IdRol"));
                    r.setNombre(resultados.getString("NombreRol"));
                    usuarioACalificar.setRol(r);
                    usuarioACalificar.setDni(resultados.getString("Dni"));
                    promedio = resultados.getFloat("Promedio");
                    cantidad = resultados.getInt("Cantidad");
                    asientosLibres = resultados.getInt("EspaciosDisponibles");

                    if(asientosLibres <= 0) {
                        botonaceptar.setEnabled(false);
                        tvNoFreeSeats.setVisibility(View.VISIBLE);
                    }

                    viajocon.setText(cantidad > 0 ? cantidad.toString()  + " calificaciones recibidas" : "No Viajo con ningun conductor");
                    Rating.setRating(promedio);

                    Rating.setIsIndicator(true);
                    fechaHoraInicioString = resultados.getString("FechaHoraInicio");

                    Map<String, String> item = new HashMap<String, String>();
                    item.put("NroViaje", resultados.getString("Id"));
                    item.put("origen", resultados.getString("CiudadOrigen") + ", " + resultados.getString("ProvinciaOrigen"));
                    item.put("destino", resultados.getString("CiudadDestino") + ", " + resultados.getString("ProvinciaDestino"));
                    item.put("fecha", resultados.getString("FechaHoraInicio").substring(8,10) + "/" + resultados.getString("FechaHoraInicio").substring(5,7) + "/" + resultados.getString("FechaHoraInicio").substring(2,4));
                    item.put("hora", resultados.getString("FechaHoraInicio").substring(11,13) + ":" + resultados.getString("FechaHoraInicio").substring(14,16));
                    item.put("estado", resultados.getString("EstadoViaje"));

                    itemsGrilla.add(item);
                }
                String[] from = {"NroViaje","origen", "destino", "fecha", "hora","estado"};
                int[] to = {R.id.tvGridItemViajeNroViaje,R.id.tvGridItemViajeOrigen, R.id.tvGridItemViajeDestino, R.id.tvGridItemViajeOrigenFecha, R.id.tvGridItemViajeOrigenHora, R.id.tvGridItemEstadoViaje};
                SimpleAdapter simpleAdapter = new SimpleAdapter(contexto, itemsGrilla, R.layout.grid_item_viaje, from, to);
                grillaVerViaje.setAdapter(simpleAdapter);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class RechazarPasajero extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " UPDATE 	PasajerosPorViaje vj";
                query += "  	    SET";
                query += " 		    EstadoPasajero='Rechazado'";
                query += " 	Where	vj.UsuarioId='" + IdSolicitante + "' and vj.ViajeId='" + NroViaje + "'";

                int resultado = st.executeUpdate(query);
                return resultado > 0;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if (resultado) {

                NotificacionesNegImpl NotiNeg = new NotificacionesNegImpl();
                Notificaciones Noti = new Notificaciones();
                Noti.setUsuarioId(Integer.parseInt(IdSolicitante));
                Noti.setMensaje("Han rechazado tu solicitud para el viaje "+NroViaje);
                Noti.setEstadoNotificacion("P");
                Noti.setEstado(1);
                try {
                    NotiNeg.AñadirNotificacion(Noti);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                Toast.makeText(contexto, "La solicitud fué rechazada correctamente.", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(contexto, "No se pudo rechazar la solicitud  intente nuevamente.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class AceptarPasajero extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " UPDATE 	PasajerosPorViaje vj";
                query += "  	    SET";
                query += " 		    EstadoPasajero='Aceptado'";
                query += " 	Where	vj.UsuarioId = '" + IdSolicitante + "' and vj.ViajeId='" + NroViaje + "'";

                int resultado = st.executeUpdate(query);
                return resultado > 0;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if (resultado) {
                NotificacionesNegImpl NotiNeg = new NotificacionesNegImpl();
                Notificaciones Noti = new Notificaciones();
                Noti.setUsuarioId(Integer.parseInt(IdSolicitante));
                Noti.setMensaje("Has sido aceptado en el viaje "+NroViaje);
                Noti.setEstadoNotificacion("P");
                Noti.setEstado(1);
                try {
                    NotiNeg.AñadirNotificacion(Noti);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(contexto, "La solicitud fué aceptada correctamente.", Toast.LENGTH_SHORT).show();
                new EliminarSolicitudesEnRangoOcupado().execute();
                finish();
            } else {
                Toast.makeText(contexto, "No se pudo Aceptar la solicitud  intente nuevamente.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class EliminarSolicitudesEnRangoOcupado extends AsyncTask<Void, Integer, Integer> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected Integer doInBackground(Void... voids) {
            try {
                int dia = Integer.parseInt(fechaHoraInicioString.substring(8, 10));
                int mes = Integer.parseInt(fechaHoraInicioString.substring(5, 7));
                int anio = Integer.parseInt(fechaHoraInicioString.substring(0, 4));
                int hora = Integer.parseInt(fechaHoraInicioString.substring(11, 13));
                int minuto = Integer.parseInt(fechaHoraInicioString.substring(14, 16));

                LocalDateTime rangoFechaInicio, rangoFechaFin, fechaHoraInicial;
                fechaHoraInicial = LocalDateTime.of(anio, mes, dia, hora, minuto);
                rangoFechaInicio = fechaHoraInicial.plusHours(-3);
                rangoFechaFin = fechaHoraInicial.plusHours(+3);

                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "UPDATE PasajerosPorViaje pxv";
                query += " INNER JOIN Viajes v ON v.Id = pxv.ViajeId";
                query += " SET EstadoPasajero = 'Rechazado'";
                query += " WHERE pxv.UsuarioId = " + IdSolicitante;
                query += " AND pxv.ViajeId != " + NroViaje;
                query += " AND (v.FechaHoraInicio BETWEEN '" + rangoFechaInicio + "' AND '" + rangoFechaFin + "')";

                return st.executeUpdate(query);
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private class CargarAsientosDisponibles extends AsyncTask<Void,Integer, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " SELECT 	vj.CantidadPasajeros,";
                query += "  	  Count(pv.UsuarioId) as Pasajeros";
                query += " FROM Viajes vj";
                query += " Inner join PasajerosPorViaje pv";
                query += " ON pv.ViajeId = vj.Id";
                query += " 	Where	pv.EstadoPasajero='Aceptado' and vj.Id='" + NroViaje + "'";
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
                    Asientos=resultados.getString("CantidadPasajeros");
                    Pasajeros=resultados.getString("Pasajeros");
                }
                int resultado=(Integer.parseInt(Asientos)-Integer.parseInt(Pasajeros))-1;
                if(resultado >= 0 && resultado <= Integer.parseInt(Asientos)){
                    new AceptarPasajero().execute();
                } else {
                    Toast.makeText(contexto, "No se pudo Aceptar la solicitud  ya que la cantidad de asientos", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}