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
import android.widget.GridView;
import android.widget.ImageButton;
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

import utn.frgp.edu.ar.carpooling.conexion.DataDB;

public class VerSolicitud extends AppCompatActivity {

    Context contexto;
    GridView grillaverViaje;
    String nroSolicitud;
    String nombreUsuario, apellidoUsuario, emailUsuario, rolUsuario, idUsuario, estadoViaje, localDateviaje;
    String nroViaje;
    ImageButton btnCancelar, btnEditar;
    TextView tv1, tv2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_solicitud);
        contexto = this;

        SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        nombreUsuario = spSesion.getString("Nombre","No hay datos");
        apellidoUsuario = spSesion.getString("Apellido","No hay datos");
        emailUsuario = spSesion.getString("Email","No hay datos");
        rolUsuario = spSesion.getString("Rol","No hay datos");
        idUsuario = spSesion.getString("Id","No hay datos");

        btnCancelar = findViewById(R.id.imageButton5);
        btnEditar = findViewById(R.id.btEditarSolicitud);
        tv1 = findViewById(R.id.textView12);
        tv2 = findViewById(R.id.textView26);

        getSupportActionBar().setTitle(nombreUsuario+" "+ apellidoUsuario+" Rol: " + rolUsuario);

        nroSolicitud = getIntent().getStringExtra("NroSolicitud");
        String estadoSolicitud = getIntent().getStringExtra("estado");

        if(estadoSolicitud.equals("Cerrada")) {
            btnCancelar.setVisibility(View.INVISIBLE);
            btnEditar.setVisibility(View.INVISIBLE);
            tv1.setVisibility(View.INVISIBLE);
            tv2.setVisibility(View.INVISIBLE);
        }

        grillaverViaje = findViewById(R.id.GrVerSolicitud);

        new CargarSolicitudSeleccionada().execute();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        new CargarSolicitudSeleccionada().execute();
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

    public void onClickEditarSolicitud (View view) {
        String nroViaje = ((TextView)findViewById(R.id.tvGridItemViajeNroViaje)).getText().toString();
        String origenHora = ((TextView)findViewById(R.id.tvGridItemViajeOrigenHora)).getText().toString();
        String origenFecha = ((TextView)findViewById(R.id.tvGridItemViajeOrigenFecha)).getText().toString();
        String origenProvinciaCiudad = ((TextView)findViewById(R.id.tvGridItemViajeOrigen)).getText().toString();
        String destinoProvinciaCiudad = ((TextView)findViewById(R.id.tvGridItemViajeDestino)).getText().toString();

        SharedPreferences sharedPreference = getSharedPreferences("DatosEdicion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString("fechaInicio", origenFecha);
        editor.putString("horaInicio", origenHora);
        editor.putString("ciudadOrigen", origenProvinciaCiudad.split(",")[0].trim());
        editor.putString("provinciaOrigen", origenProvinciaCiudad.split(",")[1].trim());
        editor.putString("ciudadDestino", destinoProvinciaCiudad.split(",")[0].trim());
        editor.putString("provinciaDestino", destinoProvinciaCiudad.split(",")[1].trim());
        editor.putInt("idViaje", Integer.parseInt(nroViaje));
        editor.putInt("cantPasajeros", 2);
        editor.putBoolean("modoEdicion", true);
        editor.commit();
        Intent pagEditarSolicitud = new Intent(contexto, NuevaSolicitud.class);
        startActivity(pagEditarSolicitud);
    }


    public void onClickCancelarSolicitud (View view) {
         nroViaje = ((TextView)findViewById(R.id.tvGridItemViajeNroViaje)).getText().toString();
        AlertDialog.Builder EliminarSolicitud= new AlertDialog.Builder(VerSolicitud.this);
        EliminarSolicitud.setMessage("¿Estas seguro que quieres eliminar tu Solicitud?")
                .setCancelable(false)
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new CancelarSolicitud().execute();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        return;
                    }
                });
        AlertDialog titulo= EliminarSolicitud.create();
        titulo.setTitle("Solicitud");
        titulo.show();

    }
    private class CancelarSolicitud extends AsyncTask<Void,Integer,Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " UPDATE 	Solicitudes Sol";
                query += "  	    SET";
                query += "  	    EstadoSolicitud='Cerrada',";
                query += " 		    EstadoRegistro='0'";
                query += " 	Where  Sol.Id=" + nroViaje;


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
                Toast.makeText(contexto, "La solicitud fué eliminada correctamente", Toast.LENGTH_SHORT).show();
                Intent pagVerMisSolicitudes = new Intent(contexto, MisViajesModoPasajero.class);
                startActivity(pagVerMisSolicitudes);
            }else{
                Toast.makeText(contexto, "La Solicitud no se pudo eliminar correctamente", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class CargarSolicitudSeleccionada extends AsyncTask<Void,Integer, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " SELECT 	vj.FechaHoraInicio,";
                query += "  	    vj.Id,";
                query += " 		    pr1.Nombre ProvinciaOrigen,";
                query += "          ci1.Nombre CiudadOrigen,";
                query += "          pr2.Nombre ProvinciaDestino,";
                query += "          ci2.Nombre CiudadDestino,";
                query += "          vj.EstadoSolicitud";
                query += " FROM Solicitudes vj";
                query += " LEFT JOIN Provincias pr1";
                query += " 	ON pr1.Id = vj.ProvinciaOrigenId";
                query += " LEFT JOIN Provincias pr2";
                query += " 	ON pr2.Id = vj.ProvinciaDestinoId";
                query += " LEFT JOIN Ciudades ci1";
                query += " 	ON ci1.Id = vj.CiudadOrigenId";
                query += " LEFT JOIN Ciudades ci2";
                query += " 	ON ci2.Id = vj.CiudadDestinoId";
                query += " 	Where	vj.Id='" + nroSolicitud + "'";

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
                    item.put("NroSolicitud", resultados.getString("Id"));
                    item.put("origen", resultados.getString("CiudadOrigen") + ", " + resultados.getString("ProvinciaOrigen"));
                    item.put("destino", resultados.getString("CiudadDestino") + ", " + resultados.getString("ProvinciaDestino"));
                    item.put("fecha", resultados.getString("FechaHoraInicio").substring(8,10) + "/" + resultados.getString("FechaHoraInicio").substring(5,7) + "/" + resultados.getString("FechaHoraInicio").substring(2,4));
                    item.put("hora", resultados.getString("FechaHoraInicio").substring(11,13) + ":" + resultados.getString("FechaHoraInicio").substring(14,16));
                    item.put("estado", resultados.getString("EstadoSolicitud"));

                    itemsGrilla.add(item);
                }

                String[] from = {"NroSolicitud","origen", "destino", "fecha", "hora","estado"};
                int[] to = {R.id.tvGridItemViajeNroViaje,R.id.tvGridItemViajeOrigen, R.id.tvGridItemViajeDestino, R.id.tvGridItemViajeOrigenFecha, R.id.tvGridItemViajeOrigenHora, R.id.tvGridItemEstadoViaje};
                SimpleAdapter simpleAdapter = new SimpleAdapter(contexto, itemsGrilla, R.layout.grid_item_viaje, from, to);
                grillaverViaje.setAdapter(simpleAdapter);

            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}