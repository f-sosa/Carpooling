package utn.frgp.edu.ar.carpooling;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.*;
import java.util.*;

import utn.frgp.edu.ar.carpooling.conexion.DataDB;
import utn.frgp.edu.ar.carpooling.entities.Ciudad;
import utn.frgp.edu.ar.carpooling.entities.Provincia;

public class MisViajes extends AppCompatActivity {
    AlertDialog filtroDialog;
    View dialogFragmentView;
    Spinner spFiltroProvOrigen;
    Spinner spFiltroProvDestino;
    Spinner spFiltroCiudOrigen;
    Spinner spFiltroCiudDestino;
    Spinner spFiltroEstado;
    GridView grillaViajes;
    String emailUsuario, rolUsuario,nombreUsuario,apellidoUsuario, idUsuario;
    Context contexto;
    String filterDate = "";
    FloatingActionButton botonNuevoViaje;
    List<Provincia> itemsProvincias;
    Provincia provOrigSelecc;
    Provincia provDestSelecc;
    ArrayList<String> listaCiudadesOrigen;
    List<Ciudad> itemsCiudadesOrigen;
    boolean shouldExecuteOnResume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_viajes);

        SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        contexto = this;
        grillaViajes = (GridView) findViewById(R.id.gvMisViajes);
        nombreUsuario = spSesion.getString("Nombre", "No hay datos");
        apellidoUsuario = spSesion.getString("Apellido","No hay datos");
        emailUsuario = spSesion.getString("Email","No hay datos");
        rolUsuario = spSesion.getString("Rol","No hay datos");
        idUsuario = spSesion.getString("Id","No hay datos");
        shouldExecuteOnResume = false;

        botonNuevoViaje = findViewById(R.id.floatingActionButton2);

        String Rol="";
        if(rolUsuario.equals("CON")){
            Rol="Conductor";
        }else{
            Rol="Pasajero";
            botonNuevoViaje.setVisibility(View.INVISIBLE);
        }

        getSupportActionBar().setTitle(nombreUsuario+" "+ apellidoUsuario+" Rol: "+Rol);

        grillaViajes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if(rolUsuario.equals("CON")) {
                    String Texto = "";
                    Texto = adapterView.getItemAtPosition(position).toString();

                    String[] parts = Texto.split("NroViaje=");
                    String part2 = parts[1];

                    //Para obtener el id del viaje
                    String[] partspt2 = part2.split(",");
                    String part3 = partspt2[0]; // 123

                    String estadoViaje = Texto.split("estado=")[1].split(",")[0];

                    Intent pagVerViaje = new Intent(contexto, Ver_Viajes.class);
                    pagVerViaje.putExtra("NroViaje", part3);
                    pagVerViaje.putExtra("EstadoViaje", estadoViaje);
                    startActivity(pagVerViaje);
                } else {
                    String Texto = "";
                    Texto = adapterView.getItemAtPosition(position).toString();

                    String[] parts = Texto.split("NroViaje=");
                    String part2 = parts[1];

                    //Para obtener el id del viaje
                    String[] partspt2 = part2.split(",");
                    String part3 = partspt2[0]; // 123

                    String estadoViaje = Texto.split("estado=")[1].split(",")[0];

                    Intent pagVerViaje = new Intent(contexto, VerViaje_Pasajero.class);
                    String idConductorViaje = Texto.split("ConductorId=")[1].replace("}","");
                    pagVerViaje.putExtra("NroViaje", part3);
                    pagVerViaje.putExtra("EstadoViaje", estadoViaje);
                    pagVerViaje.putExtra("ConductorId", idConductorViaje);
                    pagVerViaje.putExtra("pPantallaPrev", "pGeneral");
                    startActivity(pagVerViaje);
                }


                //Para viaje finalizado
               /* Intent pagVerViajeFinalizado= new Intent(contexto,VerVIajeFinalizado.class);
                pagVerViajeFinalizado.putExtra("NroViaje",part3);
                startActivity(pagVerViajeFinalizado);
                finish();*/
            }
        });
        LayoutInflater inflater = this.getLayoutInflater();
        dialogFragmentView = inflater.inflate(R.layout.fragment_filtrar_viajes_dialog, null);
        spFiltroProvOrigen = dialogFragmentView.findViewById(R.id.spFiltroProvOrigen);
        spFiltroProvDestino = dialogFragmentView.findViewById(R.id.spFiltroProvDestino);
        spFiltroCiudOrigen = dialogFragmentView.findViewById(R.id.spFiltroCiudOrigen);
        spFiltroCiudDestino = dialogFragmentView.findViewById(R.id.spFiltroCiudDestino);
        spFiltroEstado = dialogFragmentView.findViewById(R.id.spFiltroEstado);



        spFiltroProvOrigen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //POR MEDIO DE LA POS DEL ITEM SELECCIONADO EN EL SPINNER, OBTENGO EL OBJETO CARGADO DE MI LISTA DE OBJETOS EN LA MISMA POS

                if(position == 0) {
                    ResetSpinnerCiudadesOrigen();
                    return;
                }

                provOrigSelecc = itemsProvincias.get(position - 1);
                //CARGO EL SPINNER CON LOS DATOS DE LAS CIUDADES PERTENECIENTES A LA PROV SELECCIONADA.
                new CargarSpinnersCiudadesOrigen().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spFiltroProvDestino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //POR MEDIO DE LA POS DEL ITEM SELECCIONADO EN EL SPINNER, OBTENGO EL OBJETO CARGADO DE MI LISTA DE OBJETOS EN LA MISMA POS

                if(position == 0) {
                    ResetSpinnerCiudadesDestino();
                    return;
                }

                provDestSelecc = itemsProvincias.get(position - 1);
                //CARGO EL SPINNER CON LOS DATOS DE LAS CIUDADES PERTENECIENTES A LA PROV SELECCIONADA.
                new CargarSpinnersCiudadesDestino().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        crearFiltroDialog(); // CREO EL DIALOG PERO NO LO ABRO. ASI NO CREAMOS UN DIALOG DE CERO CADA VEZ QUE ABRIMOS EL FILTRO
        cargarSpinnerEstado();
        ResetSpinnerCiudadesOrigen();
        ResetSpinnerCiudadesDestino();

        new CargarViajesFiltrados().execute(generateQuery(new HashMap<String, String>()));
        new CargarFiltroProvinciaSpinners().execute();
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
        SharedPreferences sp = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        String rol = sp.getString("Rol","No hay datos");

        if(sp.equals("CON")){
            MenuItem currentOption = menu.findItem(R.id.misViajes);
            currentOption.setVisible(false);
        }

        return true;
    }

    public void ClickAgregarNuevoViaje(View view){

        SharedPreferences sharedPreference = getSharedPreferences("DatosEdicion", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putBoolean("modoEdicion", false);
        editor.commit();

        Intent i = new Intent(this,NuevoViaje.class);
        startActivity(i);
    }

    public void crearFiltrarClickListener (View view) {
        filtroDialog.show();
    }

    private void crearFiltroDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogFragmentView)
                .setPositiveButton(R.string.Aplicar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HashMap<String, String> filtros = new HashMap<String, String>();
                        filtros.put("provinciaOrigen", spFiltroProvOrigen.getSelectedItem().toString());
                        filtros.put("provinciaDestino", spFiltroProvDestino.getSelectedItem().toString());
                        filtros.put("ciudadOrigen", spFiltroCiudOrigen.getSelectedItem().toString());
                        filtros.put("ciudadDestino", spFiltroCiudDestino.getSelectedItem().toString());
                        filtros.put("estado", spFiltroEstado.getSelectedItem().toString());
                        new CargarViajesFiltrados().execute(generateQuery(filtros));
                    }
                })
                .setNegativeButton(R.string.Cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
        filtroDialog = builder.create();
    }

    private void cargarSpinnerEstado () {
        Spinner spFiltroEstado = dialogFragmentView.findViewById(R.id.spFiltroEstado);
        String[] datos = new String[] {"--NINGUNO--", "Cancelado", "Finalizado", "En Espera"};
        spFiltroEstado.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, datos));
    }

    private class CargarFiltroCiudadSpinners extends AsyncTask<String,Integer, ResultSet> {
        @Override
        protected ResultSet doInBackground(String... strings) {
            return ejecutarQuery("SELECT Nombre FROM Ciudades");
        }

        @Override
        protected void onPostExecute(ResultSet resultados) {
            super.onPostExecute(resultados);
            try {
                List<String> ciudades = new ArrayList<String>();
                ciudades.add("--NINGUNA--");

                while (resultados.next()) { ciudades.add(resultados.getString("Nombre")); }

                ArrayAdapter<String> adapterCiudades = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, ciudades);
                adapterCiudades.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFiltroCiudOrigen.setAdapter(adapterCiudades);
                spFiltroCiudDestino.setAdapter(adapterCiudades);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CargarFiltroProvinciaSpinners extends AsyncTask<String,Integer, ResultSet> {
        @Override
        protected ResultSet doInBackground(String... strings) {
            return ejecutarQuery("SELECT * FROM Provincias");
        }

        @Override
        protected void onPostExecute(ResultSet resultados) {
            super.onPostExecute(resultados);
            try {
                List<String> provincias = new ArrayList<String>();
                itemsProvincias = new ArrayList<Provincia>();
                provincias.add("--NINGUNA--");

                while (resultados.next()) {
                    provincias.add(resultados.getString("Nombre"));
                    itemsProvincias.add(new Provincia(resultados.getInt("Id"),resultados.getString("Nombre"),resultados.getBoolean("EstadoRegistro")));
                }

                ArrayAdapter<String> adapterProvincias = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, provincias);
                adapterProvincias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFiltroProvOrigen.setAdapter(adapterProvincias);
                spFiltroProvDestino.setAdapter(adapterProvincias);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CargarViajesFiltrados extends AsyncTask<String,Integer, ResultSet> {
        @Override
        protected ResultSet doInBackground(String... queries) {
            return ejecutarQuery(queries[0]);
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
                    item.put("estado",resultados.getString("EstadoViaje"));
                    item.put("ConductorId", resultados.getString("ConductorId"));
                    itemsGrilla.add(item);
                }

                String[] from = {"NroViaje","origen", "destino", "fecha", "hora","estado","ConductorId"};
                int[] to = {R.id.tvGridItemViajeNroViaje,R.id.tvGridItemViajeOrigen, R.id.tvGridItemViajeDestino, R.id.tvGridItemViajeOrigenFecha, R.id.tvGridItemViajeOrigenHora,R.id.tvGridItemEstadoViaje};
                SimpleAdapter simpleAdapter = new SimpleAdapter(contexto, itemsGrilla, R.layout.grid_item_viaje, from, to);
                grillaViajes.setAdapter(simpleAdapter);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String generateQuery (HashMap<String, String> filtros) {
        String query = "";
        query += " SELECT 	vj.FechaHoraInicio,";
        query += "  	vj.Id,";
        query += "  	vj.ConductorId,";
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
        query += rolUsuario.equals("PAS") ? " WHERE ppv.UsuarioId = '" + idUsuario + "' AND ppv.EstadoPasajero = 'Aceptado'" : " WHERE vj.ConductorId = '" + idUsuario + "' ";

        if (!filtros.isEmpty()) {
            if (!filtros.get("provinciaOrigen").equals("--NINGUNA--")) {
                query += " AND pr1.Nombre = '" + filtros.get("provinciaOrigen") + "'";
            }
            if (!filtros.get("provinciaDestino").equals("--NINGUNA--")) {
                query += " AND pr2.Nombre = '" + filtros.get("provinciaDestino") + "'";
            }
            if (!filtros.get("ciudadOrigen").equals("--NINGUNA--")) {
                query += " AND ci1.Nombre = '" + filtros.get("ciudadOrigen") + "'";
            }
            if (!filtros.get("ciudadDestino").equals("--NINGUNA--")) {
                query += " AND ci2.Nombre = '" + filtros.get("ciudadDestino") + "'";
            }
            if (!filtros.get("estado").equals("--NINGUNO--")) {
                query += " AND vj.EstadoViaje = '" + filtros.get("estado") + "'";
            }
        }
        query += " ORDER BY FechaHoraInicio ASC";

        return query;
    }

    private class CargarSpinnersCiudadesOrigen extends AsyncTask<Void,Integer, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";

                //SI EL OBJETO DE PROV SELECCIONADA FUE CARGADO, SIGNIFICA QUE FUE SELECCIONADO DEL SPINNER.
                if(provOrigSelecc != null){
                    query = "SELECT * FROM Ciudades WHERE ProvinciaId=" + provOrigSelecc.getIdProvincia();
                }
                else{
                    query = "SELECT * FROM Ciudades";
                }


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
                listaCiudadesOrigen = new ArrayList<String>();
                itemsCiudadesOrigen = new ArrayList<Ciudad>();

                listaCiudadesOrigen.add("--NINGUNA--");

                while (resultados.next()) {
                    Ciudad ciudad = new Ciudad(resultados.getInt("Id"),resultados.getInt("ProvinciaId"),resultados.getString("Nombre"),resultados.getBoolean("EstadoRegistro"));
                    listaCiudadesOrigen.add(ciudad.getNombre());
                    //CARGO LA LISTA GLOBAL PARA DESPUES PODES BUSCAR EL ELEMENTO SELECCIONADO
                    itemsCiudadesOrigen.add(ciudad);

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, listaCiudadesOrigen);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFiltroCiudOrigen.setAdapter(adapter);

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private ResultSet ejecutarQuery (String query) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
            Statement st = con.createStatement();

            return st.executeQuery(query);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private class CargarSpinnersCiudadesDestino extends AsyncTask<Void,Integer, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";

                //SI EL OBJETO DE PROV SELECCIONADA FUE CARGADO, SIGNIFICA QUE FUE SELECCIONADO DEL SPINNER.
                if(provOrigSelecc != null){
                    query = "SELECT * FROM Ciudades WHERE ProvinciaId=" + provDestSelecc.getIdProvincia();
                }
                else{
                    query = "SELECT * FROM Ciudades";
                }


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
                listaCiudadesOrigen = new ArrayList<String>();
                itemsCiudadesOrigen = new ArrayList<Ciudad>();

                listaCiudadesOrigen.add("--NINGUNA--");

                while (resultados.next()) {
                    Ciudad ciudad = new Ciudad(resultados.getInt("Id"),resultados.getInt("ProvinciaId"),resultados.getString("Nombre"),resultados.getBoolean("EstadoRegistro"));
                    listaCiudadesOrigen.add(ciudad.getNombre());
                    //CARGO LA LISTA GLOBAL PARA DESPUES PODES BUSCAR EL ELEMENTO SELECCIONADO
                    itemsCiudadesOrigen.add(ciudad);

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, listaCiudadesOrigen);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFiltroCiudDestino.setAdapter(adapter);

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void ResetSpinnerCiudadesOrigen() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("--NINGUNA--");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, list);
        spFiltroCiudOrigen.setAdapter(adapter);
    }

    public void ResetSpinnerCiudadesDestino() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("--NINGUNA--");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, list);
        spFiltroCiudDestino.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(shouldExecuteOnResume){
            finish();
            Intent pagVerViaje= new Intent(contexto,MisViajes.class);
            startActivity(pagVerViaje);
        } else{
            shouldExecuteOnResume = true;
        }

    }
}