package utn.frgp.edu.ar.carpooling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import static utn.frgp.edu.ar.carpooling.Home.NOTIFICACION_ID;

import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import utn.frgp.edu.ar.carpooling.conexion.DataDB;
import utn.frgp.edu.ar.carpooling.negocioImpl.NotificacionesNegImpl;

public class Notificaciones extends AppCompatActivity {
    ListView LvNotificacionLeidos;
    Context contexto;
    String nombreUsuario, apellidoUsuario, emailUsuario, rolUsuario, idUsuario,Mensaje;
    TextView TxtNotienesMensajes;
    ImageButton EliminarTodo;
    boolean VerificacionMensaje=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);
        contexto = this;
        LvNotificacionLeidos=findViewById(R.id.LvNotificacionesLeidos);
        TxtNotienesMensajes=findViewById(R.id.TxtNotienesMensajes);
        EliminarTodo=findViewById(R.id.IBeliminarTodo);
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

        NotificationManagerCompat notificationManagerCompat= NotificationManagerCompat.from(getApplicationContext());
        notificationManagerCompat.cancel(NOTIFICACION_ID);




        LvNotificacionLeidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                AlertDialog.Builder alerta= new AlertDialog.Builder(Notificaciones.this);
                 Mensaje=LvNotificacionLeidos.getItemAtPosition(i).toString();
                alerta.setMessage(Mensaje)
                        .setCancelable(false)
                        .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new EliminarNotificacion().execute();
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                            }
                        });
                        AlertDialog titulo= alerta.create();
                        titulo.setTitle("Notificacion");
                        titulo.show();

            }
        });



       new CargarNotificaciones().execute();

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
        MenuItem currentOption = menu.findItem(R.id.notificaciones);
        currentOption.setVisible(false);

        return true;
    }

    private class CargarNotificaciones extends AsyncTask<Void,Integer, ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " SELECT 	noti.Mensaje,noti.EstadoNotificacion";
                query += " FROM Notificaciones noti";
                query += " 	Where noti.EstadoRegistro=1 and  noti.UsuarioId=" + idUsuario;

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
                ArrayList<String> Mensajes= new ArrayList<String>();
                ArrayList<String> EstadoNotificacion= new ArrayList<String>();
                VerificacionMensaje=false;

                while (resultados.next()) {
                    VerificacionMensaje=true;
                    //Mensajes.add(resultados.getString("Mensaje")+" "+"["+resultados.getString("EstadoNotificacion")+"]");
                    Mensajes.add(resultados.getString("Mensaje"));
                    EstadoNotificacion.add(resultados.getString("EstadoNotificacion"));
                }

                ArrayAdapter<String> adapter= new ArrayAdapter<String>(contexto,R.layout.list_item_viajes,Mensajes){
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        View view= super.getView(position, convertView, parent);
                        for (String o : Mensajes){
                            EstadoNotificacion.get(position);
                            if(EstadoNotificacion.get(position).contains("L")){
                                view.setBackgroundColor(getResources().getColor(
                                        android.R.color.holo_green_light
                                ));

                            }else{
                                view.setBackgroundColor(getResources().getColor(
                                        android.R.color.holo_red_light
                                ));
                            }
                        }

                        return view;
                    }

                };

                if(VerificacionMensaje){
                    TxtNotienesMensajes.setVisibility(View.INVISIBLE);

                }else {TxtNotienesMensajes.setVisibility(View.VISIBLE);}

                LvNotificacionLeidos.setAdapter(adapter);
                new MensajeLeido().execute();


            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class MensajeLeido extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " UPDATE 	Notificaciones noti";
                query += "  	    SET";
                query += " 		    EstadoNotificacion='L'";
                query += " 	Where	noti.UsuarioId=" + idUsuario;


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

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado){

            }else{

            }
        }
    }

    private class EliminarNotificacion extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " UPDATE 	Notificaciones noti";
                query += "  	    SET";
                query += " 		    EstadoRegistro='0'";
                query += " 	Where noti.Mensaje='"+Mensaje+"' and noti.UsuarioId=" + idUsuario;


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

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado){
                Toast.makeText(contexto, "El mensaje fue eliminado correctamente", Toast.LENGTH_SHORT).show();
                new CargarNotificaciones().execute();
            }else{
                Toast.makeText(contexto, "El mensaje no se pudo eliminar correctamente", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void EliminarTodosLosMensajes(View view){

        if(VerificacionMensaje){

            AlertDialog.Builder EliminarLosMensajes= new AlertDialog.Builder(Notificaciones.this);
            EliminarLosMensajes.setMessage("¿Estas seguro que quieres eliminar todas las notificaciones?")
                    .setCancelable(false)
                    .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            new EliminarTodasLasNotificacion().execute();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            return;
                        }
                    });
            AlertDialog titulo= EliminarLosMensajes.create();
            titulo.setTitle("Notificacion");
            titulo.show();


        }else{
            Toast.makeText(contexto, "No tienes mensajes que eliminar.", Toast.LENGTH_SHORT).show();

        }



    }

    private class EliminarTodasLasNotificacion extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " UPDATE 	Notificaciones noti";
                query += "  	    SET";
                query += " 		    EstadoRegistro='0'";
                query += " 	Where  noti.UsuarioId=" + idUsuario;


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

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado){
                Toast.makeText(contexto, "El mensaje fue eliminado correctamente", Toast.LENGTH_SHORT).show();
                new CargarNotificaciones().execute();
            }else{
                Toast.makeText(contexto, "El mensaje no se pudo eliminar correctamente", Toast.LENGTH_SHORT).show();
            }
        }
    }


}