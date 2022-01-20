package utn.frgp.edu.ar.carpooling;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.sql.*;
import java.util.*;

import utn.frgp.edu.ar.carpooling.conexion.DataDB;
import utn.frgp.edu.ar.carpooling.utils.Helper;

public class Home extends AppCompatActivity {

    TextView Info, cantidadCalificaciones;
    String nombreUsuario, apellidoUsuario, emailUsuario, rolUsuario, idUsuario;
    Context context;
    ImageView st1, st2, st3, st4, st5;
    RatingBar ratingBarconductor;
    private final static String CHANNEL_ID="NOTIFICACION";
    public final static int NOTIFICACION_ID=0;
    private PendingIntent pendingIntent;
    GridView grillaViajes;
    Button btnRedireccionarAMisViajes,btnRedireccionarABusqueda;
    boolean shouldExecuteOnResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        shouldExecuteOnResume = false;
        setContentView(R.layout.activity_home);
        context = this;
        st1 = (ImageView) findViewById(R.id.ivHomeStar1);
        st2 = (ImageView) findViewById(R.id.ivHomeStar2);
        st3 = (ImageView) findViewById(R.id.ivHomeStar3);
        st4 = (ImageView) findViewById(R.id.ivHomeStar4);
        st5 = (ImageView) findViewById(R.id.ivHomeStar5);
        grillaViajes = (GridView) findViewById(R.id.gvHomeProximosVIajes);
        cantidadCalificaciones = (TextView)findViewById(R.id.ivHomeCalificaciones);
        cantidadCalificaciones.setText("");
        btnRedireccionarAMisViajes=findViewById(R.id.btnHomeRedireccionarAViajes);
        btnRedireccionarABusqueda=findViewById(R.id.btnHomeRedireccionarABusqueda);
        Info = findViewById(R.id.tvEditarPerfilInformacionPersona);
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

        getSupportActionBar().setTitle(rolUsuario.equals("CON") ? "Home conductor" : "Home pasajero");

        if(rolUsuario.equals("CON")){
            btnRedireccionarAMisViajes.setText("Mis viajes");
            btnRedireccionarABusqueda.setText("Buscar solicitudes");
        }
        else{
            btnRedireccionarAMisViajes.setText("Mis viajes");
            btnRedireccionarABusqueda.setText("Buscar viajes");

        }

        Info.setText(nombreUsuario + " " + apellidoUsuario);

        new Home.CargarCalificaciones().execute();
        new Home.ContarCalificaciones().execute();
        new Home.CargarProximosViajes().execute();
        new Home.VerificarNotificacion().execute();



        grillaViajes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String Texto="";
                Texto=adapterView.getItemAtPosition(position).toString();
                String[] parts = Texto.split("NroViaje=");
                String part2 = parts[1];

                //Para obtener el id del viaje
                String[] partspt2 = part2.split(",");
                String part3 = partspt2[0]; // 123

                String estadoViaje = Texto.split("estado=")[1].split(",")[0];

                if(rolUsuario.equals("CON")){
                    Intent pagVerViaje = new Intent(context, Ver_Viajes.class);
                    pagVerViaje.putExtra("NroViaje", part3);
                    pagVerViaje.putExtra("EstadoViaje", estadoViaje);
                    startActivity(pagVerViaje);
                }
                else{
                    Intent pagPeticionViaje= new Intent(context,VerViaje_Pasajero.class);
                    pagPeticionViaje.putExtra("NroViaje",part3);
                    pagPeticionViaje.putExtra("EstadoViaje", estadoViaje);

                    String idConductorViaje = Texto.split("ConductorId=")[1].replace("}","");

                    pagPeticionViaje.putExtra("ConductorId", idConductorViaje);
                    pagPeticionViaje.putExtra("pPantallaPrev", "pGeneral");
                    startActivity(pagPeticionViaje);

                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu miMenu) {
        SharedPreferences sp = getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        if(sp.getString("Rol","No hay datos").equals("CON")) {
            getMenuInflater().inflate(R.menu.menu_conductor, miMenu);
        }

        if(sp.getString("Rol","No hay datos").equals("PAS")) {
            getMenuInflater().inflate(R.menu.menu_pasajero, miMenu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem opcionMenu) {
        int id = opcionMenu.getItemId();

        SharedPreferences sp = getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        if(sp.getString("Rol","No hay datos").equals("CON")) {

            if (id == R.id.misViajes) {
                Intent intent = new Intent(this, MisViajes.class);
                startActivity(intent);
            }

            if (id == R.id.crearViaje) {
                Intent intent = new Intent(this, NuevoViaje.class);
                startActivity(intent);
            }

        }

        if(sp.getString("Rol","No hay datos").equals("PAS")) {
            if (id == R.id.misSolicitudes) {
                Intent intent = new Intent(this, MisViajesModoPasajero.class);
                startActivity(intent);
            }

            if (id == R.id.crearSolicitud) {
                Intent intent = new Intent(this, NuevaSolicitud.class);
                startActivity(intent);
            }
            if (id == R.id.misPeticiones) {
                Intent intent = new Intent(this, MisPeticionesPasajero.class);
                startActivity(intent);
            }
        }

        if (id == R.id.miperfil) {
            finish();
            Intent intent = new Intent(this, Home.class);
            startActivity(intent);
        }

        if (id == R.id.notificaciones) {
            Intent intent = new Intent(this, Notificaciones.class);
            startActivity(intent);
        }

        if (id == R.id.editarPerfil) {
            Intent intent = new Intent(this, EditarPerfil.class);
            startActivity(intent);
        }

        if (id == R.id.cerrarSesion) {

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

    public boolean onPrepareOptionsMenu(Menu menu)
    {
        MenuItem miPerfil = menu.findItem(R.id.miperfil);
        miPerfil.setVisible(false);

        return true;
    }

    public void onClickMisViajes (View view) {
        Intent pagMisViajes= new Intent(context, MisViajes.class);
        startActivity(pagMisViajes);
    }

    public void onClickBuscar (View view) {

        Intent intent= new Intent(context, Buscar.class);
        startActivity(intent);
    }

    private class CargarCalificaciones extends AsyncTask<Void,Integer, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += "SELECT AVG(Calificacion) as promedio FROM Calificaciones WHERE UsuarioId = " + idUsuario;

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

                if(promedio == 0 ) return;
                Helper.MostrarEstrellas(st1,st2,st3,st4,st5,promedio);
                //Le agrego el promedio al rating para que pueda mostrarlo
                //ratingBarconductor.setRating(promedio);
                //funciona pero cuando lo deshabilito la puntuacion son todas las mitad de las estrellas
                //ratingBarconductor.setEnabled(false);

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
                query += "SELECT COUNT(Calificacion) as cantidad FROM Calificaciones WHERE UsuarioId = " + idUsuario;

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

                cantidadCalificaciones.setText(cantidad > 0 ? cantidad.toString()  + " calificaciones recibidas" : "Sin calificacion");

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class CargarProximosViajes extends AsyncTask<Void,Integer,ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += " SELECT 	vj.FechaHoraInicio,";
                query += "  	vj.Id,";
                query += "      vj.ConductorId,";
                query += " 		    pr1.Nombre ProvinciaOrigen,";
                query += "          ci1.Nombre CiudadOrigen,";
                query += "          pr2.Nombre ProvinciaDestino,";
                query += "          ci2.Nombre CiudadDestino,";
                query += "          vj.EstadoViaje";
                query += " FROM Viajes vj";
                query += rolUsuario.equals("PAS") ? " INNER JOIN PasajerosPorViaje ppv ON ppv.ViajeId = vj.Id" : "";
                query += " LEFT JOIN Provincias pr1";
                query += " 	ON pr1.Id = vj.ProvinciaOrigenId";
                query += " LEFT JOIN Provincias pr2";
                query += " 	ON pr2.Id = vj.ProvinciaDestinoId";
                query += " LEFT JOIN Ciudades ci1";
                query += " 	ON ci1.Id = vj.CiudadOrigenId";
                query += " LEFT JOIN Ciudades ci2";
                query += " 	ON ci2.Id = vj.CiudadDestinoId";
                query += rolUsuario.equals("PAS") ? " WHERE ppv.UsuarioId = '" + idUsuario + "' AND ppv.EstadoPasajero = 'Aceptado' AND" : "";
                query += rolUsuario.equals("CON") ? " WHERE 	vj.ConductorId = '" + idUsuario + "' AND" : "";
                query += " 		vj.EstadoViaje IN ('1','En Espera')";
                query += " AND FechaHoraInicio > now()";
                query += " ORDER BY FechaHoraInicio ASC";
                if(rolUsuario.equals("CON"))
                    query += " LIMIT 3";

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
                    item.put("ConductorId", resultados.getString("ConductorId"));
                    itemsGrilla.add(item);
                }

                String[] from = {"NroViaje","origen", "destino", "fecha", "hora","estado","ConductorId"};
                int[] to = {R.id.tvGridItemViajeNroViaje,R.id.tvGridItemViajeOrigen, R.id.tvGridItemViajeDestino, R.id.tvGridItemViajeOrigenFecha, R.id.tvGridItemViajeOrigenHora,R.id.tvGridItemEstadoViaje};
                SimpleAdapter simpleAdapter = new SimpleAdapter(context, itemsGrilla, R.layout.grid_item_viaje, from, to);
                grillaViajes.setAdapter(simpleAdapter);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class VerificarNotificacion extends AsyncTask<Void,Integer, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " SELECT 	*";
                query += " FROM Notificaciones noti";
                query += " 	Where noti.EstadoNotificacion='P'and noti.UsuarioId = " + idUsuario;

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

                boolean verificacion=false;
                while (resultados.next()) {
                verificacion=true;


                }



               if(verificacion){
                   setPendingIntent();
                   CrearAlertaChannel();
                   CrearAlerta();

               }



            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private void setPendingIntent(){
        Intent intent = new Intent(context,Notificaciones.class);
        TaskStackBuilder stackBuilder= TaskStackBuilder.create(context);
        stackBuilder.addParentStack(Notificaciones.class);
        stackBuilder.addNextIntent(intent);
        pendingIntent= stackBuilder.getPendingIntent(1,PendingIntent.FLAG_UPDATE_CURRENT);

    }
    private void CrearAlertaChannel(){
     if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
         CharSequence name= "Notificacion";
         NotificationChannel notificationChannel= new NotificationChannel(CHANNEL_ID,name, NotificationManager.IMPORTANCE_DEFAULT);
         NotificationManager notificationManager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
         notificationManager.createNotificationChannel(notificationChannel);


     }

    }

    private void CrearAlerta(){
        NotificationCompat.Builder builder= new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
        builder.setSmallIcon(R.mipmap.car);
        builder.setContentTitle("Carpooling");
        builder.setContentText("Tienes una notificacion");
        builder.setColor(Color.BLUE);
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        builder.setLights(Color.MAGENTA,1000,1000);
        builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
        builder.setDefaults(Notification.DEFAULT_SOUND);

        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat= NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.notify(NOTIFICACION_ID,builder.build());

    }

    @Override
    public void onResume() {
        super.onResume();

        if(shouldExecuteOnResume){
            finish();
            Intent pagVerViaje= new Intent(context,Home.class);
            startActivity(pagVerViaje);
        } else{
            shouldExecuteOnResume = true;
        }

    }


}