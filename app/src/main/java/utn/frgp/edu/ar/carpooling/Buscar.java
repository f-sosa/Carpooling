package utn.frgp.edu.ar.carpooling;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;

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
import utn.frgp.edu.ar.carpooling.entities.Ciudad;
import utn.frgp.edu.ar.carpooling.entities.Provincia;

public class Buscar extends AppCompatActivity {

    AlertDialog filtroDialog, filtroDialog2;
    View dialogFragmentView, dialogFragmentView1;
    String nombreUsuario, apellidoUsuario, emailUsuario, rolUsuario, dniUsuario,idUsuarioLog;
    GridView grillaViajes;
    Context context;
    TextView filtroRecorrido, filtroRecorridoDestino, filtroFecha, filtroFechaQuery;
    Spinner spFiltroProvinciaOrigen, spFiltroCiudadesOrigen, spFiltroProvinciaDestino, spFiltroCiudadesDestino;
    ArrayList<String> listaCiudadesOrigen;
    List<Ciudad> itemsCiudadesOrigen;
    Provincia provOrigSelecc;
    List<Provincia> itemsProvincias;
    Chip chOrigen,chDestino, chFecha;
    ArrayAdapter<String> adapterProvincias;

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
        //MenuItem currentOption = menu.findItem(R.id.misViajes);
        //currentOption.setVisible(false);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setTitle("Busqueda");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);

        SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);

        nombreUsuario = spSesion.getString("Nombre","No hay datos");
        apellidoUsuario = spSesion.getString("Apellido","No hay datos");
        emailUsuario = spSesion.getString("Email","No hay datos");
        rolUsuario = spSesion.getString("Rol","No hay datos");
        dniUsuario = spSesion.getString("Dni","No hay datos");
        idUsuarioLog = spSesion.getString("Id","No hay datos");

        String Rol="";
        if(rolUsuario.equals("CON")){
            Rol="Conductor";
        }else{
            Rol="Pasajero";
        }

        getSupportActionBar().setTitle(nombreUsuario+" "+ apellidoUsuario+" Rol: "+Rol);
        grillaViajes = (GridView) findViewById(R.id.gvHomeProximosVIajes);
        context = this;

        LayoutInflater inflater = this.getLayoutInflater();
        dialogFragmentView = inflater.inflate(R.layout.fragment_filtro_provincia_ciudad, null);
        spFiltroProvinciaOrigen = dialogFragmentView.findViewById(R.id.spFragmentFiltroProvinciaCiudadProvincias);
        spFiltroCiudadesOrigen = dialogFragmentView.findViewById(R.id.spFragmentFiltroProvinciaCiudadCiudades);
        spFiltroProvinciaDestino = dialogFragmentView.findViewById(R.id.spFragmentFiltroProvinciaCiudadProvincias3);
        spFiltroCiudadesDestino = dialogFragmentView.findViewById(R.id.spFragmentFiltroProvinciaCiudadProvincias4);

        //filtroRecorrido = (TextView) findViewById(R.id.textView19);
        //filtroRecorridoDestino = (TextView) findViewById(R.id.textView20);
        //filtroFecha = (TextView) findViewById(R.id.textView25);
        filtroFechaQuery = (TextView) findViewById(R.id.textView22);
        filtroFechaQuery.setText(" ");


        spFiltroProvinciaOrigen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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

        spFiltroProvinciaDestino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //POR MEDIO DE LA POS DEL ITEM SELECCIONADO EN EL SPINNER, OBTENGO EL OBJETO CARGADO DE MI LISTA DE OBJETOS EN LA MISMA POS

                if(position == 0) {
                    ResetSpinnerCiudadesDestino();
                    return;
                }

                provOrigSelecc = itemsProvincias.get(position - 1);
                //CARGO EL SPINNER CON LOS DATOS DE LAS CIUDADES PERTENECIENTES A LA PROV SELECCIONADA.
                new CargarSpinnersCiudadesDestino().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




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
                Intent pagVerBusqueda= new Intent(context,Ver_Busqueda.class);
                pagVerBusqueda.putExtra("NroViaje",part3);
                pagVerBusqueda.putExtra("EstadoViaje", estadoViaje);
                startActivity(pagVerBusqueda);
                }
                else{
                    Intent pagPeticionViaje= new Intent(context,VerViaje_Pasajero.class);
                    pagPeticionViaje.putExtra("NroViaje",part3);
                    pagPeticionViaje.putExtra("EstadoViaje", estadoViaje);
//
                    String idConductorViaje = Texto.split("ConductorId=")[1].replace("}","");

                    pagPeticionViaje.putExtra("ConductorId", idConductorViaje);
                    pagPeticionViaje.putExtra("pPantallaPrev", "pBuscar");
                    startActivity(pagPeticionViaje);

                }

            }
        });

        crearFiltroDialog();

        if(rolUsuario.equals("PAS")){
            new CargarViajesFiltrados("").execute();
        }
        else{
            new CargarSolicitudesFiltradas("").execute();
        }

        new CargarFiltroProvinciaSpinners().execute();
    }

    public void onClickFecha(View view) {

        DatePickerViajeFragment newFragment = DatePickerViajeFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                String selectedDate =
                        (day < 10 ? "0" + day :  day) + "/" +
                                (month + 1 < 10 ? "0" + (month+1) : (month+1)) +"/" +
                                year;
                //filtroFecha.setText("Despues del " + selectedDate);
                filtroFechaQuery.setText(year + "-" + (month + 1) + "-" + day);
                onClickBuscar();
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");

    }

    public void onClickBuscar(View... view) {

        String filtro = "";
        boolean flag = true; // el uso de este flag ya no tiene sentido, pero lo dejo por las dudas

        if(!spFiltroProvinciaOrigen.getSelectedItem().equals(" ")) {
            filtro += flag ? " AND " : " WHERE ";
            filtro += " po.Nombre = '" + spFiltroProvinciaOrigen.getSelectedItem().toString() + "' ";
            flag = true;
        }

        if(spFiltroCiudadesOrigen.getAdapter() != null && !spFiltroCiudadesOrigen.getSelectedItem().equals(" ")) {
            filtro += flag ? " AND " : " WHERE ";
            filtro += " co.Nombre = '" + spFiltroCiudadesOrigen.getSelectedItem().toString() + "' ";
            flag = true;
        }

        if(spFiltroProvinciaDestino.getAdapter() != null && !spFiltroProvinciaDestino.getSelectedItem().equals(" ")) {
            filtro += flag ? " AND " : " WHERE ";
            filtro += " pd.Nombre = '" + spFiltroProvinciaDestino.getSelectedItem().toString() + "' ";
            flag = true;
        }

        if(spFiltroCiudadesDestino.getAdapter() != null && !spFiltroCiudadesDestino.getSelectedItem().equals(" ")) {
            filtro += flag ? " AND " : " WHERE ";
            filtro += " cd.Nombre = '" + spFiltroCiudadesDestino.getSelectedItem().toString() + "' ";
            flag = true;
        }

        if(!filtroFechaQuery.equals(" ")) {
            filtro += flag ? " AND " : " WHERE ";
            filtro += " vj.FechaHoraInicio > '" + filtroFechaQuery.getText() + "' ";
        }

        if(rolUsuario.equals("PAS")){
           CargarViajesFiltrados task = new CargarViajesFiltrados(filtro);
            task.execute();
        }
        else{
            CargarSolicitudesFiltradas task = new CargarSolicitudesFiltradas(filtro);
            task.execute();
        }




    }

    public void onClickLimpiarFiltros(View view) {

        spFiltroProvinciaOrigen.setAdapter(null);
        spFiltroCiudadesOrigen.setAdapter(null);
        spFiltroProvinciaDestino.setAdapter(null);
        spFiltroProvinciaDestino.setAdapter(null);
        filtroFechaQuery.setText(" ");
        filtroRecorrido.setText("Desde cualquier origen");
        filtroRecorridoDestino.setText("Hacia cualquier destino");
        filtroFecha.setText("Cualquier fecha");

        new CargarFiltroProvinciaSpinners().execute();

        if(rolUsuario.equals("PAS")){
            new CargarViajesFiltrados("").execute();
        }
        else{
            new CargarSolicitudesFiltradas("").execute();
        }
    }

    // Dialogo de busuqeda de pronvicia
    public void onClickFiltrarOrigen (View view) {
        filtroDialog.show();
    }

    private void crearFiltroDialog () {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogFragmentView)
                .setPositiveButton(R.string.Aplicar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(context,"Texto: " + (spFiltroProvinciaOrigen.getSelectedItem() == null ? "null" : "not null"),Toast.LENGTH_LONG);

                        Object item = spFiltroProvinciaOrigen.getSelectedItem();

                        onClickBuscar();

                        /*if(!spFiltroProvinciaOrigen.getSelectedItem().equals(" "))
                            filtroRecorrido.setText("Desde " + spFiltroCiudadesOrigen.getSelectedItem().toString() + ", " + spFiltroProvinciaOrigen.getSelectedItem().toString());
                        else filtroRecorrido.setText("Desde cualquier origen");
                        if(!spFiltroProvinciaDestino.getSelectedItem().equals(" "))
                            filtroRecorridoDestino.setText("Hacia " + spFiltroCiudadesDestino.getSelectedItem().toString() + ", " + spFiltroProvinciaDestino.getSelectedItem().toString());
                        else filtroRecorridoDestino.setText("Hacia cualquier destino");*/
                    }
                })
                .setNegativeButton(R.string.Cancelar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });
        filtroDialog = builder.create();
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
                provincias.add(" ");

                while (resultados.next()) {
                    provincias.add(resultados.getString("Nombre"));
                    itemsProvincias.add(new Provincia(resultados.getInt("Id"),resultados.getString("Nombre"),resultados.getBoolean("EstadoRegistro")));
                }

                adapterProvincias = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, provincias);
                adapterProvincias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFiltroProvinciaOrigen.setAdapter(adapterProvincias);
                spFiltroProvinciaDestino.setAdapter(adapterProvincias);
            }
            catch (Exception e) {
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

                listaCiudadesOrigen.add(" ");

                while (resultados.next()) {
                    Ciudad ciudad = new Ciudad(resultados.getInt("Id"),resultados.getInt("ProvinciaId"),resultados.getString("Nombre"),resultados.getBoolean("EstadoRegistro"));
                    listaCiudadesOrigen.add(ciudad.getNombre());
                    //CARGO LA LISTA GLOBAL PARA DESPUES PODES BUSCAR EL ELEMENTO SELECCIONADO
                    itemsCiudadesOrigen.add(ciudad);

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listaCiudadesOrigen);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFiltroCiudadesOrigen.setAdapter(adapter);

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
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

                listaCiudadesOrigen.add(" ");

                while (resultados.next()) {
                    Ciudad ciudad = new Ciudad(resultados.getInt("Id"),resultados.getInt("ProvinciaId"),resultados.getString("Nombre"),resultados.getBoolean("EstadoRegistro"));
                    listaCiudadesOrigen.add(ciudad.getNombre());
                    //CARGO LA LISTA GLOBAL PARA DESPUES PODES BUSCAR EL ELEMENTO SELECCIONADO
                    itemsCiudadesOrigen.add(ciudad);
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, listaCiudadesOrigen);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spFiltroCiudadesDestino.setAdapter(adapter);

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class CargarSolicitudesFiltradas extends AsyncTask<String,Integer, ResultSet> {

        String filtro = "";

        public CargarSolicitudesFiltradas(String _filtro) {
            this.filtro = _filtro;
        }

        @Override
        protected ResultSet doInBackground(String... queries) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += " SELECT vj.Id, po.Nombre ProvinciaOrigen, co.Nombre CiudadOrigen, pd.Nombre ProvinciaDestino, cd.Nombre CiudadDestino, vj.FechaHoraInicio, vj.EstadoSolicitud ";
                query += " FROM Solicitudes vj ";
                query += " LEFT JOIN Provincias po ";
                query += " 	ON vj.ProvinciaOrigenId = po.Id ";
                query += " LEFT JOIN Ciudades co ";
                query += " 	ON vj.CiudadOrigenId = co.Id ";
                query += " LEFT JOIN Provincias pd ";
                query += " 	ON vj.ProvinciaDestinoId = pd.Id ";
                query += " LEFT JOIN Ciudades cd  ";
                query += " 	ON vj.CiudadDestinoId = cd.Id ";
                query += " LEFT JOIN Usuarios us ";
                query += " 	ON vj.PasajeroId = us.Id ";
                query += " WHERE vj.FechaHoraInicio > now() AND us.Dni <> '" + dniUsuario + "' and vj.EstadoSolicitud='Pendiente'";
                query += filtro;
                query += " ORDER BY vj.FechaHoraInicio ASC";


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
                    item.put("estado",resultados.getString("EstadoSolicitud"));
                    itemsGrilla.add(item);
                }

                String[] from = {"NroViaje","origen", "destino", "fecha", "hora","estado"};
                int[] to = {R.id.tvGridItemViajeNroViaje,R.id.tvGridItemViajeOrigen, R.id.tvGridItemViajeDestino, R.id.tvGridItemViajeOrigenFecha, R.id.tvGridItemViajeOrigenHora,R.id.tvGridItemEstadoViaje};
                SimpleAdapter simpleAdapter = new SimpleAdapter(context, itemsGrilla, R.layout.grid_item_viaje, from, to);
                grillaViajes.setAdapter(simpleAdapter);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class CargarViajesFiltrados extends AsyncTask<String,Integer, ResultSet> {

        String filtro = "";

        public CargarViajesFiltrados(String _filtro) {
            this.filtro = _filtro;
        }

        @Override
        protected ResultSet doInBackground(String... queries) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += " SELECT vj.Id,vj.ConductorId, po.Nombre ProvinciaOrigen, co.Nombre CiudadOrigen, pd.Nombre ProvinciaDestino, cd.Nombre CiudadDestino, vj.FechaHoraInicio, vj.EstadoViaje ";
                query += " FROM Viajes vj ";
                query += " LEFT JOIN Provincias po ";
                query += " 	ON vj.ProvinciaOrigenId = po.Id ";
                query += " LEFT JOIN Ciudades co ";
                query += " 	ON vj.CiudadOrigenId = co.Id ";
                query += " LEFT JOIN Provincias pd ";
                query += " 	ON vj.ProvinciaDestinoId = pd.Id ";
                query += " LEFT JOIN Ciudades cd  ";
                query += " 	ON vj.CiudadDestinoId = cd.Id ";
                query += " LEFT JOIN Usuarios us ";
                query += " 	ON vj.ConductorId = us.Id ";
                query += " WHERE vj.FechaHoraInicio > now() AND  vj.EstadoRegistro=1 AND vj.EstadoViaje = 'En Espera' AND us.Dni <> '" + dniUsuario + "' ";
                query += " AND vj.Id NOT IN (SELECT ViajeId FROM `PasajerosPorViaje` WHERE UsuarioId = '" + idUsuarioLog + "') ";
                query += filtro;
                query += " ORDER BY vj.FechaHoraInicio ASC";


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
                    item.put("estado",resultados.getString("EstadoViaje"));
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

    public void ResetSpinnerCiudadesOrigen() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(" ");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
        spFiltroCiudadesOrigen.setAdapter(adapter);
    }

    public void ResetSpinnerCiudadesDestino() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(" ");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, list);
        spFiltroCiudadesDestino.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        if(rolUsuario.equals("PAS")){
            new CargarViajesFiltrados("").execute();
        }
        else{
            new CargarSolicitudesFiltradas("").execute();
        }

    }

}