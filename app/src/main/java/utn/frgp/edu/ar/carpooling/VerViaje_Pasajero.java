package utn.frgp.edu.ar.carpooling;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
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

public class VerViaje_Pasajero extends AppCompatActivity {

    Context contexto;
    String NroViaje;
    String EstadoViaje;
    String idUsuarioLog,idUsuarioViaje;
    String nombreUsuario, apellidoUsuario, emailUsuario, rolUsuario, dniUsuario;
    TextView Nombre,Telefono,CantidadCalificaciones;
    RatingBar Rating;
    GridView grillaVerViaje;
    Button botonQuieroUnirme;
    Button botonVolver;

    float CalificacionDada;
    boolean calificacionInicial;

    Usuario usuarioACalificar;

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
            Intent intent = new Intent(this, utn.frgp.edu.ar.carpooling.Notificaciones.class);
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
        //MenuItem currentOption = menu.findItem(R.id.misViajes);
        //currentOption.setVisible(false);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_viaje_pasajero);

        SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        contexto = this;
        nombreUsuario = spSesion.getString("Nombre","No hay datos");
        apellidoUsuario = spSesion.getString("Apellido","No hay datos");
        emailUsuario = spSesion.getString("Email","No hay datos");
        rolUsuario = spSesion.getString("Rol","No hay datos");
        dniUsuario = spSesion.getString("Dni","No hay datos");
        idUsuarioLog = spSesion.getString("Id","No hay datos");



        getSupportActionBar().setTitle(nombreUsuario+" "+ apellidoUsuario+" Rol: " + rolUsuario);


        NroViaje=getIntent().getStringExtra("NroViaje");
        EstadoViaje=getIntent().getStringExtra("EstadoViaje");
        idUsuarioViaje = getIntent().getStringExtra("ConductorId");

        Nombre=findViewById(R.id.TxtVpNombre);
        Telefono=findViewById(R.id.TxtVpNumero);
        Rating=findViewById(R.id.RBVpPasajero);
        CantidadCalificaciones=findViewById(R.id.TxtVPViajocon);
        grillaVerViaje=(GridView) findViewById(R.id.GrVpViaje);
        botonQuieroUnirme =findViewById(R.id.BtnVpQuieroUnirme);
        botonVolver = findViewById(R.id.btnVpVolver);

        calificacionInicial = true;
        CalificacionDada = 0;

        String pantallaPrevia = getIntent().getStringExtra("pPantallaPrev");

        if(pantallaPrevia.equals("pBuscar")){
            Rating.setIsIndicator(true);
        }
        else{
            if(EstadoViaje.equals("Finalizado")){
                botonQuieroUnirme.setVisibility(View.INVISIBLE);
            }
            if(EstadoViaje.equals("En Espera")){
                Rating.setIsIndicator(true);
                botonQuieroUnirme.setText("Quiero Abandonar el viaje");
            }
            if(EstadoViaje.equals("Cancelado")){
                Rating.setIsIndicator(true);
                botonQuieroUnirme.setVisibility(View.INVISIBLE);
            }
        }

        Rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                float nroEstrellas=0;
                String calificacion= String.valueOf(rating);

                if(calificacion.substring(1).equals(".0")){
                    calificacion = calificacion.substring(0,1);
                }

                AlertDialog.Builder vtnConfirmacion = new AlertDialog.Builder(contexto);
                vtnConfirmacion.setMessage("Esta seguro que quiere calificar al pasajero con "+ calificacion + " estrellas?");
                vtnConfirmacion.setCancelable(false);
                vtnConfirmacion.setTitle("Confirmacion de Calificacion");

                vtnConfirmacion.setPositiveButton("Si",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String calificacion= String.valueOf(rating);
                        if(calificacion.substring(1).equals(".0")){
                            calificacion = calificacion.substring(0,1);
                        }
                        CalificacionDada = Float.valueOf(calificacion);
                        new CalificarUsuario().execute();

                    }
                });

                vtnConfirmacion.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                if(calificacionInicial == false){
                    AlertDialog alerta = vtnConfirmacion.create();
                    alerta.show();
                }

            }
        });

        new CargarDatos().execute();
    }

    public void onClickQuieroUnirme(View view){
        AlertDialog.Builder vtnConfirmacion = new AlertDialog.Builder(contexto);

        if(botonQuieroUnirme.getText().equals("Quiero unirme al viaje")){
            vtnConfirmacion.setMessage("¿Esta seguro que quiere enviar una peticion para unirse al viaje?");
            vtnConfirmacion.setCancelable(false);
            vtnConfirmacion.setTitle("Confirmacion de Asignacion a viaje");

            vtnConfirmacion.setPositiveButton("Si",new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new GenerarPeticionViaje().execute();
                }
            });

            vtnConfirmacion.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        else{
            vtnConfirmacion.setMessage("¿Esta seguro que quiere abandonar el viaje? (No podrá volver a ser incluido)");
            vtnConfirmacion.setCancelable(false);
            vtnConfirmacion.setTitle("Confirmacion de Abandono de viaje");

            vtnConfirmacion.setPositiveButton("Si",new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new CancelarPasajero().execute();
                }
            });

            vtnConfirmacion.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }


        AlertDialog alerta = vtnConfirmacion.create();
        alerta.show();
    }

    public void ClickVolver(View view){
        finish();
    }

    private class CargarDatos extends AsyncTask<Void,Integer, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " CALL InfoParaVerPasajero('" + idUsuarioViaje + "'," + NroViaje + ",'" + idUsuarioLog + "');";

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
                List<Map<String, String>> itemsGrilla = new ArrayList<Map<String, String>>();
                while (resultados.next()) {

                    usuarioACalificar.setNombre(resultados.getString("Nombre"));
                    usuarioACalificar.setApellido(resultados.getString("Apellido"));
                    usuarioACalificar.setTelefono(resultados.getString("Telefono"));
                    Rol r = new Rol();
                    r.setId(resultados.getString("IdRol"));
                    r.setNombre(resultados.getString("NombreRol"));
                    usuarioACalificar.setRol(r);
                    usuarioACalificar.setDni(resultados.getString("Dni"));
                    promedio = resultados.getFloat("Promedio");
                    cantidad = resultados.getInt("cantidad");
                    CantidadCalificaciones.setText(cantidad > 0 ? cantidad.toString()  + " calificaciones recibidas" : "No recibió calificaciones");

                    Nombre.setText(usuarioACalificar.getNombre() + " " + usuarioACalificar.getApellido());
                    Telefono.setText(usuarioACalificar.getTelefono());
                    Rating.setRating(promedio);

                    calificacionInicial=false;

                    Integer idCalificacion = resultados.getInt("IdCalificacion");

                    if(idCalificacion > 0 ) {
                        Rating.setIsIndicator(true);
                    }

                    Map<String, String> item = new HashMap<String, String>();
                    item.put("NroViaje", resultados.getString("Id"));
                    item.put("origen", resultados.getString("CiudadOrigen") + ", " + resultados.getString("ProvinciaOrigen"));
                    item.put("destino", resultados.getString("CiudadDestino") + ", " + resultados.getString("ProvinciaDestino"));
                    item.put("fecha", resultados.getString("FechaHoraInicio").substring(8,10) + "/" + resultados.getString("FechaHoraInicio").substring(5,7) + "/" + resultados.getString("FechaHoraInicio").substring(2,4));
                    item.put("hora", resultados.getString("FechaHoraInicio").substring(11,13) + ":" + resultados.getString("FechaHoraInicio").substring(14,16));
                    item.put("estado", resultados.getString("EstadoViaje"));

                    itemsGrilla.add(item);
                }

                String[] from = {"NroViaje","origen", "destino", "fecha", "hora", "estado"};
                int[] to = {R.id.tvGridItemViajeNroViaje,R.id.tvGridItemViajeOrigen, R.id.tvGridItemViajeDestino, R.id.tvGridItemViajeOrigenFecha, R.id.tvGridItemViajeOrigenHora,R.id.tvGridItemEstadoViaje};
                SimpleAdapter simpleAdapter = new SimpleAdapter(contexto, itemsGrilla, R.layout.grid_item_viaje, from, to);
                grillaVerViaje.setAdapter(simpleAdapter);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CalificarUsuario extends AsyncTask<Void,Integer,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += "INSERT INTO `Calificaciones` ";
                query += "(UsuarioId, ";
                //query += "UsuarioRol, ";
                query += "CalificadorId, ";
                //query += "CalificadorRol, ";
                query += "ViajeId, ";
                query += "Calificacion) ";
                query += "VALUES";
                query += "(";
                query +=  "'" + idUsuarioViaje + "',";
                query +=  "'" + idUsuarioLog + "',";
                query +=  "'" + NroViaje+ "',";
                query +=  "'" + CalificacionDada+ "'";
                query += ")";

                int resultado = st.executeUpdate(query);


                if(resultado>0){
                    return true;
                }
                else {return false;}

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado){

                String calificacion= String.valueOf(CalificacionDada);
                if(calificacion.substring(1).equals(".0")){
                    calificacion = calificacion.substring(0,1);
                }

                NotificacionesNegImpl NotiNeg = new NotificacionesNegImpl();
                utn.frgp.edu.ar.carpooling.entities.Notificaciones Noti = new Notificaciones();
                Noti.setUsuarioId(Integer.parseInt(idUsuarioViaje));
                Noti.setMensaje("El usuario " + nombreUsuario + " " + apellidoUsuario + "te ha calificado con " + calificacion + "estrellas. Por el viaje: " + NroViaje);
                Noti.setEstadoNotificacion("P");
                Noti.setEstado(1);

                try {
                    NotiNeg.AñadirNotificacion(Noti);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Rating.setIsIndicator(true);
                Toast.makeText(contexto,"Calificó con: " + calificacion + " estrellas.",Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(contexto, "No se pudo calificar al usuario intente nuevamente.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class GenerarPeticionViaje extends AsyncTask<Void,Integer,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += "INSERT INTO `PasajerosPorViaje` ";
                query += "(ViajeId, ";
                query += "UsuarioId, ";
                query += "EstadoPasajero, ";
                query += "cantAcompañantes) ";
                query += "VALUES";
                query += "(";
                query +=  "'" + NroViaje + "',";
                query +=  "'" + idUsuarioLog + "',";
                query +=  "'Pendiente',";
                query +=  "'0'";
                query += ")";

                int resultado = st.executeUpdate(query);


                if(resultado>0){
                    return true;
                }
                else {return false;}

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado){

                NotificacionesNegImpl NotiNeg = new NotificacionesNegImpl();
                Notificaciones Noti = new Notificaciones();
                Noti.setUsuarioId(Integer.parseInt(idUsuarioViaje));
                Noti.setMensaje("El Usuario "+nombreUsuario+" "+apellidoUsuario+" quiere unirse al viaje "+NroViaje);
                Noti.setEstadoNotificacion("P");
                Noti.setEstado(1);
                try {
                    NotiNeg.AñadirNotificacion(Noti);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(contexto,"Se generó correctamente la peticion. Te avisaremos cuando el conductor te conteste.", Toast.LENGTH_LONG).show();
                botonQuieroUnirme.setEnabled(false);
            }else{
                Toast.makeText(contexto, "No se pudo generar la peticion intente nuevamente.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private class CancelarPasajero extends AsyncTask<Void,Integer,Boolean> {

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
                query += " 	Where	vj.UsuarioId='" + idUsuarioLog + "' and vj.ViajeId='" + NroViaje + "'";


                int resultado = st.executeUpdate(query);

                if(resultado > 0){



                    return true;
                }
                else {return false;}

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado){
                NotificacionesNegImpl NotiNeg = new NotificacionesNegImpl();
                Notificaciones Noti = new Notificaciones();
                Noti.setUsuarioId(Integer.parseInt(idUsuarioViaje));
                Noti.setMensaje("El usuario " + nombreUsuario + " " + apellidoUsuario +  " ha abandonado el viaje "+NroViaje);
                Noti.setEstadoNotificacion("P");
                Noti.setEstado(1);
                try {
                    NotiNeg.AñadirNotificacion(Noti);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(contexto, "Has sido desasignado de este viaje", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(contexto, "No se pudo desasignar del viaje intente nuevamente.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}