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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import utn.frgp.edu.ar.carpooling.conexion.DataDB;
import utn.frgp.edu.ar.carpooling.entities.Ciudad;
import utn.frgp.edu.ar.carpooling.entities.Notificaciones;
import utn.frgp.edu.ar.carpooling.entities.Provincia;
import utn.frgp.edu.ar.carpooling.entities.Viaje;
import utn.frgp.edu.ar.carpooling.negocioImpl.NotificacionesNegImpl;

public class Ver_Busqueda extends AppCompatActivity {
    Context contexto;
    String nombreUsuario, apellidoUsuario, emailUsuario, rolUsuario, idUsuario;
    String NroViaje,PasajeroEmail;
    String EstadoViaje;
    Spinner cantidadAsientos;
    GridView grillaverbusqueda;
    RatingBar RbVerbusqueda;
    Button AceptarViaje;
    Spinner CantAsientos;
    TextView Nombre,Celular,ViajoCon,cantidadAcompaniantes;
    Viaje viaj;
    String idviaje="",emailpasajero, idPasajero;
    int cantidadAcompañantes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_busqueda);
        contexto = this;
        SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        nombreUsuario = spSesion.getString("Nombre","No hay datos");
        apellidoUsuario = spSesion.getString("Apellido","No hay datos");
        emailUsuario = spSesion.getString("Email","No hay datos");
        rolUsuario = spSesion.getString("Rol","No hay datos");
        idUsuario = spSesion.getString("Id","No hay datos");
        cantidadAsientos = findViewById(R.id.spinnerVerbusqueda);
        String Rol="";
        if(rolUsuario.equals("CON")){
            Rol="Conductor";
        }else{
            Rol="Pasajero";
        }

        getSupportActionBar().setTitle(nombreUsuario+" "+ apellidoUsuario+" Rol: "+Rol);

        NroViaje=getIntent().getStringExtra("NroViaje");
        EstadoViaje=getIntent().getStringExtra("EstadoViaje");


        grillaverbusqueda=findViewById(R.id.GrVerBusqueda);
        RbVerbusqueda=findViewById(R.id.RatBarVer_Busq);
        AceptarViaje=findViewById(R.id.BtnVerBusqAcept);
        CantAsientos=findViewById(R.id.spinnerVerbusqueda);
        Nombre=findViewById(R.id.TxtNombreVerBusq);
        Celular=findViewById(R.id.TxtCelVerBusque);
        ViajoCon=findViewById(R.id.TxtViajoVerBusq);
        cantidadAcompaniantes=findViewById(R.id.tvVerBusquedaCantidadAcompaniantes);


        AceptarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CargardatosparaAgregar().execute();

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

    private class CargarDatos extends AsyncTask<Void,Integer,ResultSet> {
        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += "CALL InfoParaVerBusqueda (" + NroViaje + ");";

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
                Integer cantidad = null;
                List<Map<String, String>> itemsGrilla = new ArrayList<Map<String, String>>();



                while (resultados.next()) {
                    Nombre.setText(resultados.getString("Nombre")+" "+resultados.getString("Apellido"));
                    Celular.setText(resultados.getString("Telefono"));
                    cantidadAcompañantes=resultados.getInt("CantidadAcompaniantes");
                    cantidadAcompaniantes.setText(
                            cantidadAcompañantes == 0 ?
                                "No tiene acompañantes" : "Viaja con " + cantidadAcompañantes + " acompañantes"
                    );
                    promedio = resultados.getFloat("Promedio");
                    if(promedio > 0) {
                        RbVerbusqueda.setRating(promedio);
                        RbVerbusqueda.setIsIndicator(true);
                    }

                    cantidad = resultados.getInt("Cantidad");
                    ViajoCon.setText(cantidad > 0 ? cantidad.toString()  + " Conductores lo calificaron" : "No Viajo con ningun conductor");

                    Map<String, String> item = new HashMap<String, String>();
                    item.put("NroViaje", resultados.getString("Id"));
                    item.put("origen", resultados.getString("CiudadOrigen") + ", " + resultados.getString("ProvinciaOrigen"));
                    item.put("destino", resultados.getString("CiudadDestino") + ", " + resultados.getString("ProvinciaDestino"));
                    item.put("fecha", resultados.getString("FechaHoraInicio").substring(8,10) + "/" + resultados.getString("FechaHoraInicio").substring(5,7) + "/" + resultados.getString("FechaHoraInicio").substring(2,4));
                    item.put("hora", resultados.getString("FechaHoraInicio").substring(11,13) + ":" + resultados.getString("FechaHoraInicio").substring(14,16));
                    item.put("estado",resultados.getString("EstadoSolicitud"));
                    itemsGrilla.add(item);

                }

                String[] from = {"NroViaje","origen", "destino", "fecha", "hora","estado"};
                int[] to = {R.id.tvGridItemViajeNroViaje,R.id.tvGridItemViajeOrigen, R.id.tvGridItemViajeDestino, R.id.tvGridItemViajeOrigenFecha, R.id.tvGridItemViajeOrigenHora,R.id.tvGridItemEstadoViaje};
                SimpleAdapter simpleAdapter = new SimpleAdapter(contexto, itemsGrilla, R.layout.grid_item_viaje, from, to);
                grillaverbusqueda.setAdapter(simpleAdapter);

                ArrayList<String> listaCantPasajeros = new ArrayList<String>();
                for(int i = cantidadAcompañantes; i<=4; i++){
                    listaCantPasajeros.add(String.valueOf(i));
                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, listaCantPasajeros);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                CantAsientos.setAdapter(adapter);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CargardatosparaAgregar extends AsyncTask<String,Integer, ResultSet> {

        @Override
        protected ResultSet doInBackground(String... queries) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += " SELECT vj.Id,vj.ProvinciaOrigenId, po.Nombre ProvinciaOrigen, vj.CiudadOrigenId,co.Nombre CiudadOrigen, vj.ProvinciaDestinoId,pd.Nombre ProvinciaDestino, vj.CiudadDestinoId ,cd.Nombre CiudadDestino, vj.FechaHoraInicio, vj.EstadoSolicitud, vj.PasajeroId ";
                query += " FROM Solicitudes vj ";
                query += " LEFT JOIN Provincias po ";
                query += " 	ON vj.ProvinciaOrigenId = po.Id ";
                query += " LEFT JOIN Ciudades co ";
                query += " 	ON vj.CiudadOrigenId = co.Id ";
                query += " LEFT JOIN Provincias pd ";
                query += " 	ON vj.ProvinciaDestinoId = pd.Id ";
                query += " LEFT JOIN Ciudades cd  ";
                query += " 	ON vj.CiudadDestinoId = cd.Id ";
                query += " 	Where	vj.Id='" + NroViaje + "'";



                return st.executeQuery(query);

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(ResultSet resultados) {
            super.onPostExecute(resultados);
            try {
                List<Map<String, String>> itemsGrilla = new ArrayList<Map<String, String>>();
                viaj= new Viaje();
                Provincia ProvinciaOrigen= new Provincia();
                Provincia ProvinciaDestino= new Provincia();
                Ciudad CiudadOrigen = new Ciudad();
                Ciudad CiudadDestino = new Ciudad();
                while (resultados.next()) {
                    ProvinciaOrigen.setIdProvincia(resultados.getInt("ProvinciaOrigenId"));
                    ProvinciaOrigen.setNombre("ProvinciaOrigen");
                    CiudadOrigen.setIdCiudad(resultados.getInt("CiudadOrigenId"));
                    CiudadOrigen.setNombre(resultados.getString("CiudadOrigen"));
                    ProvinciaDestino.setIdProvincia(resultados.getInt("ProvinciaDestinoId"));
                    ProvinciaDestino.setNombre(resultados.getString("ProvinciaDestino"));
                    CiudadDestino.setIdCiudad(resultados.getInt("CiudadDestinoId"));
                    CiudadDestino.setNombre(resultados.getString("CiudadDestino"));
                    viaj.setProvOrigen(ProvinciaOrigen);
                    viaj.setCiudadOrigen(CiudadOrigen);
                    viaj.setProvDestino(ProvinciaDestino);
                    viaj.setCiudadDestino(CiudadDestino);
                    viaj.setIdConductor(Integer.parseInt(idUsuario));
                    //emailpasajero=resultados.getString("PasajeroEmail");
                    idPasajero=resultados.getString("PasajeroId");
                    String fechaInicio = resultados.getString("FechaHoraInicio"); // 2021-11-22 12:30:00.0
                    LocalDateTime inicioViaje = LocalDateTime.of(
                            Integer.parseInt(fechaInicio.substring(0,4)),
                            Integer.parseInt(fechaInicio.substring(5,7)),
                            Integer.parseInt(fechaInicio.substring(8,10)),
                            Integer.parseInt(fechaInicio.substring(11,13)),
                            Integer.parseInt(fechaInicio.substring(14,16)),
                            Integer.parseInt(fechaInicio.substring(17,19))
                    );

                    viaj.setFechaHoraInicio(inicioViaje);
                    viaj.setCantPasajeros(4);
                    //PasajeroEmail=resultados.getString("PasajeroEmail");

                }

              new AgregarViaje().execute();


            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class AgregarViaje extends AsyncTask<Void,Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";

                query += "INSERT INTO Viajes";
                query += "(ConductorId,";
                query += "ProvinciaOrigenId,";
                query += "CiudadOrigenId,";
                query += "ProvinciaDestinoId,";
                query += "CiudadDestinoId,";
                query += "FechaHoraInicio,";
                query += "CantidadPasajeros,";
                query += "EstadoViaje)";
                query += "VALUES";
                query += "(";
                query +=  "'" + idUsuario + "',";
                query +=  "'" + viaj.getProvOrigen().getIdProvincia()+ "',";
                query +=  "'" + viaj.getCiudadOrigen().getIdCiudad()+ "',";
                query +=  "'" + viaj.getProvDestino().getIdProvincia() + "',";
                query +=  "'" + viaj.getCiudadDestino().getIdCiudad() + "',";
                query +=  "'" + viaj.getFechaHoraInicio().toString() + "',";
                query +=  "'" + viaj.getCantPasajeros() + "',";
                query +=  "'En Espera'";
                query += ")";

                int resultado = st.executeUpdate(query);
                return resultado > 0;

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado){
                Toast.makeText(contexto, "El nuevo viaje ha sido creado", Toast.LENGTH_SHORT).show();
                new BuscarDatosparaInsertarElPasajero().execute();
            }else{
                Toast.makeText(contexto, "No se pudo generar el nuevo viaje, intente nuevamente.", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private class BuscarDatosparaInsertarElPasajero extends AsyncTask<Void,Integer, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += "SELECT Id FROM Viajes where ConductorId='" + idUsuario + "' order by Id DESC limit 1 ";

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

             int c=0;
                while (resultados.next()) {
                    c++;
                    idviaje=resultados.getString("Id");

                }
            if(c!=0){
                new AgregarPasajeroxViaje().execute();
            }else{
              System.out.println("Un error al mandarle el id");
                }

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    private class AgregarPasajeroxViaje extends AsyncTask<Void,Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += "INSERT INTO `PasajerosPorViaje`";
                query += "(ViajeId,";
                query += "UsuarioId,";
                query += "EstadoRegistro,";
                query += "EstadoPasajero,";
                query += "cantAcompañantes)";
                query += "VALUES";
                query += "(";
                query +=  "'" + idviaje + "',";
                query +=  "'" + idPasajero + "',";
                query +=  "'1',";
                query +=  "'Aceptado',";
                query +=  "'" + cantidadAcompañantes + "'";
                query += ")";


                int resultado = st.executeUpdate(query);
                return resultado > 0;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado){
                new ActualizarSolicitud().execute();
            }else{
                Toast.makeText(contexto, "No se pudo agregar el pasajero al viaje, intente nuevamente.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private class ActualizarSolicitud extends AsyncTask<Void,Integer,Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += "UPDATE Solicitudes SET ";
                query += "EstadoRegistro='0',";
                query += "EstadoSolicitud='Cerrada'";
                query += " WHERE Id = " + NroViaje;

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
                NotificacionesNegImpl NotiNeg = new NotificacionesNegImpl();
                Notificaciones Noti = new Notificaciones();
                Noti.setUsuarioId(Integer.parseInt(idPasajero));
                Noti.setMensaje("Tu solicitud "+NroViaje+" fue aceptada. Ahora estas adherido al viaje "+idviaje);
                Noti.setEstadoNotificacion("P");
                Noti.setEstado(1);
                try {
                    NotiNeg.AñadirNotificacion(Noti);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else System.out.println("No se pudo cambiar la solicitud");

            finish();
        }
    }


}