package utn.frgp.edu.ar.carpooling;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

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
import java.util.regex.Pattern;

import utn.frgp.edu.ar.carpooling.conexion.DataDB;
import utn.frgp.edu.ar.carpooling.entities.Ciudad;
import utn.frgp.edu.ar.carpooling.entities.Provincia;
import utn.frgp.edu.ar.carpooling.entities.Rol;
import utn.frgp.edu.ar.carpooling.entities.Usuario;
import utn.frgp.edu.ar.carpooling.entities.Viaje;
import utn.frgp.edu.ar.carpooling.negocioImpl.viajeNegImpl;
import utn.frgp.edu.ar.carpooling.utils.EnumsErrores;
import utn.frgp.edu.ar.carpooling.utils.Validadores;

public class NuevoViaje extends AppCompatActivity {

    SharedPreferences spEdicion;
    private EditText fechaViaje;
    private EditText horaViaje;
    private Spinner spProvinciasOrigen;
    private Spinner spProvinciasDestino;
    private Spinner spCiudadesOrigen;
    private Spinner spCiudadesDestino;
    private Spinner spCantPasajeros;
    String emailUsuario, rolUsuario,nombreUsuario,apellidoUsuario, idUsuario;

    //LOS ARRAYS LIST SON PARA MOSTRAR LOS DATOS EN EL SPINNER
    //LOS LIST SON PARA PODER BUSCAR EL OBJETO CORRESPONDIENTE AL ITEM SELECCIONADO EN EL SPINNER
    List<Ciudad> itemsCiudadesOrigen;
    ArrayList<String> listaCiudadesOrigen;
    List<Ciudad> itemsCiudadesDestino;
    ArrayList<String> listaCiudadesDestino;

    List<Provincia> itemsProvincias;
    ArrayList<String> listaProvincias;

    //SON PARA PODER OBTENER EL ID DE LA PROVINCIA SELECCINADA Y BUSCAR SUS CIUDADES
    Provincia provOrigSelecc;
    Provincia provDestSelecc;

    //ES PARA PODER DAR DE ALTA EL NUEVO VIAJE
    Viaje nuevoViaje;

    private Context contexto;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (spEdicion.getBoolean("modoEdicion", false)) {
            SharedPreferences.Editor editor = spEdicion.edit();
            editor.putBoolean("modoEdicion", false);
            editor.commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_viaje);

        fechaViaje = (EditText) findViewById(R.id.edTextFecha);
        horaViaje = (EditText) findViewById(R.id.edTextHora);

        spCantPasajeros = (Spinner) findViewById(R.id.spCantPasajeros);

        spProvinciasOrigen = (Spinner) findViewById(R.id.spProvOrigen);
        spCiudadesOrigen = (Spinner) findViewById(R.id.spCiudadOrigen);

        spProvinciasDestino = (Spinner) findViewById(R.id.spProvDestino);
        spCiudadesDestino= (Spinner) findViewById(R.id.spCiudadDestino);

        contexto = this;
        SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        spEdicion = getSharedPreferences("DatosEdicion", Context.MODE_PRIVATE);
        nombreUsuario = spSesion.getString("Nombre", "No hay datos");
        apellidoUsuario = spSesion.getString("Apellido","No hay datos");
        emailUsuario = spSesion.getString("Email","No hay datos");
        rolUsuario = spSesion.getString("Rol","No hay datos");
        idUsuario = spSesion.getString("Id","No hay datos");

        String Rol="";
        int cantPasajerosMinima = 1;
        int cantAsientosMaxima = 4;
        if(rolUsuario.equals("CON")){
            Rol="Conductor";
        }else{
            Rol="Pasajero";
        }

        getSupportActionBar().setTitle(nombreUsuario+" "+ apellidoUsuario+" Rol: "+Rol);
        ArrayList<String> listaCantPasajeros = new ArrayList<String>();

        boolean esModoEdicion = spEdicion.getBoolean("modoEdicion", false);
        if (esModoEdicion) {
            TextView tvTitulo = findViewById(R.id.txtViewTitulo);
            Button btCrearViaje = findViewById(R.id.btnCrearViaje);
            tvTitulo.setText("Editar Viaje");
            btCrearViaje.setText("Actualizar viaje");
            fechaViaje.setEnabled(false);
            String fechaInicio = spEdicion.getString("fechaInicio","");
            fechaInicio = fechaInicio.split("/")[0] + "/" + fechaInicio.split("/")[1] + "/20" +fechaInicio.split("/")[2];
            fechaViaje.setText(fechaInicio);
            horaViaje.setText(spEdicion.getString("horaInicio",""));
            spProvinciasOrigen.setEnabled(false);
            spCiudadesOrigen.setEnabled(false);
            spProvinciasDestino.setEnabled(false);
            spCiudadesDestino.setEnabled(false);

            new CargarSpinnerPasajeros().execute();

            //if (spEdicion.getInt("cantPasajeros", 0) > cantPasajerosMinima)
            //    cantPasajerosMinima = spEdicion.getInt("cantPasajeros", 0);
            //    cantAsientosMaxima = spEdicion.getInt("cantAsientos", 4);
        }
        else {

            for (int i = 1; i <= 4; i++) {
                listaCantPasajeros.add(String.valueOf(i));
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, listaCantPasajeros);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCantPasajeros.setAdapter(adapter);
            }
        }

        provDestSelecc = null;
        provOrigSelecc = null;
        nuevoViaje = null;
        fechaViaje.setFocusable(false);
        fechaViaje.setFocusableInTouchMode(false);
        fechaViaje.setInputType(InputType.TYPE_NULL);
        fechaViaje.requestFocus();
        new CargarSpinnersProvincias().execute();
    }

    private class CargarSpinnerPasajeros extends AsyncTask<Void,Integer, ResultSet> {

        @Override
        protected ResultSet doInBackground(Void... voids) {

            try {
                SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();
                String query = "";
                query += " CALL ObtenerCantidadAsientosOcupados(" + spEdicion.getInt("idViaje", 0) + "); ";

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

                    String cantidadActualString = resultados.getString("CantidadActual");
                    Integer cantidadActual = Integer.parseInt(cantidadActualString == null ? "0" : cantidadActualString);
                    String asientosOcupadosString = resultados.getString("AsientosOcupados");
                    Integer asientosOcupados = Integer.parseInt(asientosOcupadosString == null ? "0" : asientosOcupadosString);
                    Integer indexCantidadActual = 0;
                    Integer contador = 0;

                    asientosOcupados = asientosOcupados == 0 ? 1 : asientosOcupados;

                    ArrayList<String> listaCantPasajeros = new ArrayList<String>();
                    for (int i = asientosOcupados; i <= 4; i++) {
                        if(i == cantidadActual) indexCantidadActual = contador;
                        contador++;
                        listaCantPasajeros.add(String.valueOf(i));
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, listaCantPasajeros);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCantPasajeros.setAdapter(adapter);
                    spCantPasajeros.setSelection(indexCantidadActual);

                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
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
        MenuItem currentOption = menu.findItem(R.id.crearViaje);
        currentOption.setVisible(false);

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickCrearViaje(View view) throws ExecutionException, InterruptedException {
        nuevoViaje = new Viaje();

        SharedPreferences spSesion = getSharedPreferences("Sesion", Context.MODE_PRIVATE);
        nuevoViaje.setIdConductor( Integer.parseInt(idUsuario) );
        nuevoViaje.setProvOrigen(itemsProvincias.get(spProvinciasOrigen.getSelectedItemPosition()));
        nuevoViaje.setCiudadOrigen(itemsCiudadesOrigen.get(spCiudadesOrigen.getSelectedItemPosition()));
        nuevoViaje.setProvDestino(itemsProvincias.get(spProvinciasDestino.getSelectedItemPosition()));
        nuevoViaje.setCiudadDestino(itemsCiudadesDestino.get(spCiudadesDestino.getSelectedItemPosition()));
        nuevoViaje.setCantPasajeros(Integer.parseInt(spCantPasajeros.getSelectedItem().toString()));
        nuevoViaje.setEstadoViaje("En Espera");

        boolean esModoEdicion = spEdicion.getBoolean("modoEdicion", false);
        if (esModoEdicion) {
            nuevoViaje.setIdViaje(spEdicion.getInt("idViaje", 0));
        }

        if(!Validadores.validarNacimiento(true,fechaViaje)) return;

        if(!Validadores.validarHoraViaje(true,horaViaje)) return;

        String separadorFecha = Pattern.quote("/");
        String separadorHora = Pattern.quote(":");

        int dia = Integer.parseInt(fechaViaje.getText().toString().split(separadorFecha)[0]);
        int mes = Integer.parseInt(fechaViaje.getText().toString().split(separadorFecha)[1]);
        int anio = Integer.parseInt(fechaViaje.getText().toString().split(separadorFecha)[2]);
        int hora = Integer.parseInt(horaViaje.getText().toString().split(separadorHora)[0]);
        int minuto = Integer.parseInt(horaViaje.getText().toString().split(separadorHora)[1]);

        //ESTA COMENTADO PORQUE AMI NO ME FUNCIONA, NO OLVIDAR ACTIVARLO NUEVAMENTE!!!!  JONNA.
        nuevoViaje.setFechaHoraInicio(LocalDateTime.of(anio,mes,dia,hora,minuto));

        viajeNegImpl vNegImpl = new viajeNegImpl();

        if(vNegImpl.validarDatosViaje(nuevoViaje) == EnumsErrores.viaje_DestinoyOrigenIguales.ordinal()){
            Toast.makeText(contexto, "El lugar de origen y destino no pueden ser los mismos", Toast.LENGTH_LONG).show();
            return;
        }

        //VOLVER A HABILITAR, AMI NO ME ANDA!! JONA
        if(vNegImpl.validarDatosViaje(nuevoViaje) == EnumsErrores.viaje_FechayHoraAnteriorActual.ordinal()){
            Toast.makeText(contexto, "Ingrese una fecha superior a la actual", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean hayViajesEnRango = vNegImpl.validarViajeConductorEnRangoFechayHora(nuevoViaje);
        if (hayViajesEnRango) {
            Toast.makeText(contexto, "Ya tiene un viaje pendiente en el rango horario +- 3hs para la misma fecha", Toast.LENGTH_LONG).show();
        } else {
            if (esModoEdicion) new ActualizarViaje().execute();
            else new AltaNuevoViaje().execute();
        }
    }

    public void onClickFechaViaje(View view) {

        DatePickerViajeFragment newFragment = DatePickerViajeFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                String selectedDate =
                        (day < 10 ? "0" + day :  day) + "/" +
                                (month + 1 < 10 ? "0" + (month+1) : (month+1)) +"/" +
                                year;
                fechaViaje.setText(selectedDate);
                fechaViaje.setError(null);
            }
        });

        newFragment.show(getSupportFragmentManager(), "datePicker");

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

                while (resultados.next()) {
                    Ciudad ciudad = new Ciudad(resultados.getInt("Id"),resultados.getInt("ProvinciaId"),resultados.getString("Nombre"),resultados.getBoolean("EstadoRegistro"));
                    listaCiudadesOrigen.add(ciudad.getNombre());
                    //CARGO LA LISTA GLOBAL PARA DESPUES PODES BUSCAR EL ELEMENTO SELECCIONADO
                    itemsCiudadesOrigen.add(ciudad);

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, listaCiudadesOrigen);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCiudadesOrigen.setAdapter(adapter);

                if (!spEdicion.getString("ciudadOrigen", "false").equals("false")) {
                    spCiudadesOrigen.setSelection(adapter.getPosition(spEdicion.getString("ciudadOrigen", "false")));
                    spCiudadesDestino.setSelection(adapter.getPosition(spEdicion.getString("ciudadDestino", "false")));
                }
            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class CargarSpinnersCiudadesDestino extends AsyncTask<Void,Integer, ResultSet>  {

        @Override
        protected ResultSet doInBackground(Void... voids) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";

                //SI EL OBJETO DE PROV SELECCIONADA FUE CARGADO, SIGNIFICA QUE FUE SELECCIONADO DEL SPINNER.
                if(provDestSelecc != null){
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
                listaCiudadesDestino = new ArrayList<String>();
                itemsCiudadesDestino = new ArrayList<Ciudad>();

                while (resultados.next()) {
                    Ciudad ciudad = new Ciudad(resultados.getInt("Id"),resultados.getInt("ProvinciaId"),resultados.getString("Nombre"),resultados.getBoolean("EstadoRegistro"));
                    listaCiudadesDestino.add(ciudad.getNombre());
                    //CARGO LA LISTA GLOBAL PARA DESPUES PODES BUSCAR EL ELEMENTO SELECCIONADO
                    itemsCiudadesDestino.add(ciudad);

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, listaCiudadesDestino);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spCiudadesDestino.setAdapter(adapter);

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class CargarSpinnersProvincias extends AsyncTask<Void,Integer, ResultSet>{

        @Override
        protected ResultSet doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query = "SELECT * FROM Provincias";

                return st.executeQuery(query);

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(ResultSet resultados) {
            super.onPostExecute(resultados);
            try {
                listaProvincias = new ArrayList<String>();
                itemsProvincias = new ArrayList<Provincia>();

                while (resultados.next()) {
                    Provincia provincia = new Provincia(resultados.getInt("Id"),resultados.getString("Nombre"),resultados.getBoolean("EstadoRegistro"));
                    listaProvincias.add(provincia.getNombre());

                    //CARGO LA LISTA GLOBAL PARA DESPUES PODES BUSCAR EL ELEMENTO SELECCIONADO
                    itemsProvincias.add(provincia);

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(contexto, android.R.layout.simple_spinner_item, listaProvincias);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spProvinciasOrigen.setAdapter(adapter);
                spProvinciasDestino.setAdapter(adapter);
                // PARA EL 2do PARAMETRO DEL getString, TENGO QUE PONER OBLIGADAMENTE UN VALOR POR DEFAULT ("false") SI NO ENCUETRO provinciasOrigen
                // ENTONCES APROVECHO ESO PARA CHEQUEAR QUE SI ES DISTINTO DE FALSE, SIGNIFICA QUE HAY DATO PARA provinciasOrigen
                if (!spEdicion.getString("provinciaOrigen", "false").equals("false")) {
                    spProvinciasOrigen.setSelection(adapter.getPosition(spEdicion.getString("provinciaOrigen", "false")));
                    spProvinciasDestino.setSelection(adapter.getPosition(spEdicion.getString("provinciaDestino", "false")));
                }

                spProvinciasOrigen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //POR MEDIO DE LA POS DEL ITEM SELECCIONADO EN EL SPINNER, OBTENGO EL OBJETO CARGADO DE MI LISTA DE OBJETOS EN LA MISMA POS
                        provOrigSelecc = itemsProvincias.get(position);
                        //CARGO EL SPINNER CON LOS DATOS DE LAS CIUDADES PERTENECIENTES A LA PROV SELECCIONADA.
                        new CargarSpinnersCiudadesOrigen().execute();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                spProvinciasDestino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //POR MEDIO DE LA POS DEL ITEM SELECCIONADO EN EL SPINNER, OBTENGO EL OBJETO CARGADO DE MI LISTA DE OBJETOS EN LA MISMA POS
                        provDestSelecc = itemsProvincias.get(position);
                        //CARGO EL SPINNER CON LOS DATOS DE LAS CIUDADES PERTENECIENTES A LA PROV SELECCIONADA.
                        new CargarSpinnersCiudadesDestino().execute();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }
            catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private class AltaNuevoViaje extends AsyncTask<Void,Integer,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";


                try {

                    query = "CALL FechaActualEsMenor('" + nuevoViaje.getFechaHoraInicio()  + "');";
                    ResultSet rs = st.executeQuery(query);
                    rs.next();
                    String resultado = rs.getString("Resultado");
                    if(resultado.equals("FALSE")) {
                        return false;
                    }

                } catch(Exception e) {

                }

                query = "";
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
                query +=  "'" + nuevoViaje.getProvOrigen().getIdProvincia()+ "',";
                query +=  "'" + nuevoViaje.getCiudadOrigen().getIdCiudad()+ "',";
                query +=  "'" + nuevoViaje.getProvDestino().getIdProvincia() + "',";
                query +=  "'" + nuevoViaje.getCiudadDestino().getIdCiudad() + "',";
                //query +=  "'2021-10-11 00:00:00',";
                query +=  "'" + nuevoViaje.getFechaHoraInicio() + "',"; //VOLVER HABILITAR, AMI NO ME FUNCIONA. JONNA
                query +=  "'" + nuevoViaje.getCantPasajeros() + "',";
                query +=  "'" + nuevoViaje.getEstadoViaje() + "'";
                query += ")";

                int resultado = st.executeUpdate(query);
                return resultado > 0;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if(resultado){
                Toast.makeText(contexto, "El nuevo viaje ha sido creado", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(contexto, "No se pudo generar el nuevo viaje, intente nuevamente.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ActualizarViaje extends AsyncTask<Void,Integer,Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection con = DriverManager.getConnection(DataDB.urlMySQL, DataDB.user, DataDB.pass);
                Statement st = con.createStatement();

                String query = "";
                query += "UPDATE Viajes SET ";
                query += "ConductorId='" + idUsuario + "',";
                query += "ProvinciaOrigenId='" + nuevoViaje.getProvOrigen().getIdProvincia()+ "',";
                query += "CiudadOrigenId='" + nuevoViaje.getCiudadOrigen().getIdCiudad()+ "',";
                query += "ProvinciaDestinoId='" + nuevoViaje.getProvDestino().getIdProvincia() + "',";
                query += "CiudadDestinoId='" + nuevoViaje.getCiudadDestino().getIdCiudad() + "',";
                query += "FechaHoraInicio='" + nuevoViaje.getFechaHoraInicio() + "',";
                query += "CantidadPasajeros='" + nuevoViaje.getCantPasajeros() + "',";
                query += "EstadoViaje='" + nuevoViaje.getEstadoViaje() + "'";
                query += " WHERE Id = " + nuevoViaje.getIdViaje();

                int resultado = st.executeUpdate(query);
                return resultado > 0;
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                return false;
            }
        }
        @Override
        protected void onPostExecute(Boolean resultado) {
            super.onPostExecute(resultado);
            if (resultado) {
                Toast.makeText(contexto, "El viaje fué actualizado correctamente", Toast.LENGTH_LONG).show();
                finish();
            }
            else Toast.makeText(contexto, "Ocurrio un error, intentelo nuevamente", Toast.LENGTH_LONG).show();
        }
    }
}