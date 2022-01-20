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
import utn.frgp.edu.ar.carpooling.negocioImpl.NotificacionesNegImpl;

public class CancelarPeticionPasajero extends AppCompatActivity {
    Context contexto;
    GridView GrCancelarPeticion;
    String emailUsuario, rolUsuario,nombreUsuario,apellidoUsuario,idUsuario;
    String NroViaje,ConductorId;
    TextView NombreConductor,CelularConductor,ViajoCon;
    RatingBar ratingBar;
    Button CancelarPeticion;
    String estado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelar_peticion_pasajero);
        contexto = this;
        SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        nombreUsuario = spSesion.getString("Nombre", "No hay datos");
        apellidoUsuario = spSesion.getString("Apellido","No hay datos");
        emailUsuario = spSesion.getString("Email","No hay datos");
        rolUsuario = spSesion.getString("Rol","No hay datos");
        idUsuario = spSesion.getString("Id","No hay datos");
        GrCancelarPeticion = (GridView) findViewById(R.id.GrCancelarPeticion);
        NombreConductor=findViewById(R.id.TxtCancelarPetNombre);
        CelularConductor=findViewById(R.id.TxtCancelarPetCelular);
        ratingBar=findViewById(R.id.ratingBarCancelarPet);
        ViajoCon=findViewById(R.id.TxtViajoConCanPet);
        CancelarPeticion=findViewById(R.id.BtnCancelarPeticion);
        String Rol="";
        if(rolUsuario.equals("CON")){
            Rol="Conductor";
        }else{
            Rol="Pasajero";
        }



        getSupportActionBar().setTitle(nombreUsuario+" "+ apellidoUsuario+" Rol: "+Rol);

        NroViaje=getIntent().getStringExtra("NroViaje");
        estado=getIntent().getStringExtra("EstadoViaje"); // es en realidad el estado del pasajero

        if(estado.equals("Rechazado")) CancelarPeticion.setEnabled(false);

        CancelarPeticion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder vtnConfirmacion = new AlertDialog.Builder(contexto);

                vtnConfirmacion.setMessage("¿Esta seguro que quiere enviar una peticion para unirse al viaje?");
                vtnConfirmacion.setCancelable(false);
                vtnConfirmacion.setTitle("Confirmacion de Asignacion a viaje");

                vtnConfirmacion.setPositiveButton("Si",new DialogInterface.OnClickListener(){

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new CancelarPeticion().execute();
                    }
                });

                vtnConfirmacion.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog alerta = vtnConfirmacion.create();
                alerta.show();


            }
        });



        new CargarViajeSeleccionado().execute();
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
        MenuItem miPerfil = menu.findItem(R.id.miperfil);
        miPerfil.setVisible(false);

        return true;
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
                query += "          vj.ConductorId,";
                query += "          vj.EstadoViaje";
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
                    item.put("estado", estado);

                    ConductorId = resultados.getString("ConductorId");
                    itemsGrilla.add(item);
                    //localDateviaje=resultados.getString("FechaHoraFinalizacion");
                    //cantidadDeAsientos=resultados.getString("CantidadPasajeros");
                }

                String[] from = {"NroViaje","origen", "destino", "fecha", "hora","estado"};
                int[] to = {R.id.tvGridItemViajeNroViaje,R.id.tvGridItemViajeOrigen, R.id.tvGridItemViajeDestino, R.id.tvGridItemViajeOrigenFecha, R.id.tvGridItemViajeOrigenHora, R.id.tvGridItemEstadoViaje};
                SimpleAdapter simpleAdapter = new SimpleAdapter(contexto, itemsGrilla, R.layout.grid_item_viaje, from, to);
                GrCancelarPeticion.setAdapter(simpleAdapter);
                new CargarDatosConductor().execute();

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    private class CargarDatosConductor extends AsyncTask<Void,Integer,ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " SELECT 	usu.Nombre,";
                query += "  	    usu.Apellido,";
                query += " 		    usu.Telefono";
                query += " FROM Usuarios usu";
                query += " 	Where	usu.Id='" + ConductorId + "'";


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
                query += "SELECT AVG(Calificacion) as promedio FROM Calificaciones WHERE UsuarioId = " + ConductorId;

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

                //Le agrego el promedio al rating para que pueda mostrarlo
                ratingBar.setRating(promedio);
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
                query += "SELECT COUNT(Calificacion) as cantidad FROM Calificaciones WHERE UsuarioId = " + ConductorId;

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

                ViajoCon.setText(cantidad > 0 ? cantidad.toString()  + " calificaciones recibidas" : "Sin calificacion");

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private class CancelarPeticion extends AsyncTask<Void,Integer,Boolean> {

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
                query += " 	Where	vj.UsuarioId='" + idUsuario + "' and vj.ViajeId='" + NroViaje + "'";


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
                NotificacionesNegImpl NotiNeg = new NotificacionesNegImpl();
                utn.frgp.edu.ar.carpooling.entities.Notificaciones Noti = new Notificaciones();
                Noti.setUsuarioId(Integer.parseInt(ConductorId));
                Noti.setMensaje("El pasajero "+nombreUsuario+"  "+ apellidoUsuario+" ha cancelado la peticion al viaje."+NroViaje);
                Noti.setEstadoNotificacion("P");
                Noti.setEstado(1);
                try {
                    NotiNeg.AñadirNotificacion(Noti);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(contexto, "La petición ha sido cancelada.", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(contexto, "No se pudo canelar la petición. Intente nuevamente.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}