package com.example.mapa_georeferencialprueba;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Activity2 extends AppCompatActivity {

    EditText et_contrato;
    TextView mensaje1,mensaje2,tvresult,tv_long,txmostrarcontrato;
    Button botonGuardar,btn_buscar;
    ImageButton btn_mapasir;
    String param1;
    String param2;
    String param3;
    String mensaje;
    char param_coorx;
    char param_coordy;
    String response12, resultado3;
    SoapPrimitive resultString;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);
        /////////esta funcion sirve para permitir envio de datos
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
/////////////////////// Inicializamos variables conjunto a activity diseno

        mensaje1 = (TextView) findViewById(R.id.mensaje_id);
        mensaje2 = (TextView) findViewById(R.id.mensaje_id2);
        botonGuardar = (Button) this.findViewById(R.id.btnGuardar);
        et_contrato=(EditText)findViewById(R.id.et_contra);
        tvresult=(TextView)findViewById(R.id.tv_result);
        tv_long=(TextView)findViewById(R.id.textView_long);
        txmostrarcontrato=(TextView)findViewById(R.id.tx_mostrar);
        btn_buscar=(Button)findViewById(R.id.btn_search);
        btn_buscar=(Button)findViewById(R.id.btn_search);

///////////////////// mandar a los mapas la ubicacion del contrato

        btn_mapasir=(ImageButton)findViewById(R.id.button_mapas);
///////////////////// Creamos metodo para pasar a otro activity
        btn_mapasir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inten25 = new Intent(getApplicationContext(),MapasActivity.class);
                startActivity(inten25);
            }
        });
/////////////////////creamos metodo para obtener los parametros de tipeo y enviar los datos de coordenadas
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                param1=et_contrato.getText().toString();
                param2=mensaje1.getText().toString();
                param3=tv_long.getText().toString();
                segundoplano tarea =new segundoplano();
                tarea.execute();
            }
        });
 ///////////////////////// creamos metodo boton buscar

        btn_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                param1=et_contrato.getText().toString();
                //param_coorx='x';
                //param_coordy='y';
                tercerplano mt = new tercerplano();
                cuartoplano mt1 =new cuartoplano();
                mt1.execute();
                mt.execute();
            }
        });

/////////////////////////////////////// Pedir permiso para obtener la ubicacion del dispositivo en tiempo real
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }
    }

    /////////////////// clase publica convertir la cual receptara y enviara coord al web service
    public void convertir (){

        String URL="http://172.17.38.10:55025/Servicios.asmx";
        String SOAP_ACTION="http://tempuri.org/sp_ws_actualiza_cordenada";
        String NAME_SPACE="http://tempuri.org/";
        String METHOD_NAME="sp_ws_actualiza_cordenada";

        try {
            SoapObject Request= new SoapObject(NAME_SPACE,METHOD_NAME);
            Request.addProperty("contrato", param1);
            Request.addProperty("cordenada_x",param2);
            Request.addProperty("cordenada_y",param3);
            SoapSerializationEnvelope soapEnvelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapEnvelope.dotNet=true;
            soapEnvelope.setOutputSoapObject(Request);
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.call(SOAP_ACTION,soapEnvelope);
            resultString = (SoapPrimitive) soapEnvelope.getResponse();
            mensaje = "OK";
        } catch (Exception ex){

            mensaje = "Error"+ ex.getMessage();

        }

    }
/////////////////////////// clase para procesos en segundo plano del metodo Convertir(Enviar coordenadas)
 private class segundoplano extends AsyncTask<Void,Void,Void> {
     @Override
     protected void onPreExecute(){

     }
     @Override
     protected Void doInBackground(Void... params) {
         convertir();
         return null;
     }
     @Override
     protected void onPostExecute(Void result){
         tvresult.setText("Coordenadas Agregadas Correctamente"+"  Contrato: "+param1+" Latitud: "+param2+" Longitud: "+param3);

     }
 }

 /////////Apartir de aqui empezamos a obtener la direciones y coordenadas
    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }

        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        mensaje1.setText("Localizacion agregada");
        mensaje2.setText("");
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();
                return;
            }
        }
    }
 //////////////clase para Obtener la direccion de la calle a partir de la latitud y la longitud
    public void setLocation(Location loc) {

        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(
                        loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    Address DirCalle = list.get(0);
                    mensaje2.setText(DirCalle.getAddressLine(0));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

/////////////// Clase para obtener la Localizacion en tiempo real

    public class Localizacion implements LocationListener {
        Activity2 mainActivity;
        public Activity2 getMainActivity() {
            return mainActivity;
        }

        public void setMainActivity(Activity2 mainActivity) {
            this.mainActivity = mainActivity;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            String Text = ""+ loc.getLatitude() ;
            String TextLong=""+loc.getLongitude();
            mensaje1.setText(Text);
            tv_long.setText(TextLong);
            this.mainActivity.setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            mensaje1.setText("GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            mensaje1.setText("GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }
    }
///////////////////////// Comenzamos el metodo para buscar clientes en el WebService

    public void buscar (){

        String URL="http://172.17.38.10:55025/Servicios.asmx";
        String SOAP_ACTION="http://tempuri.org/sp_sr_consulta_cliente_ws";
        String NAME_SPACE="http://tempuri.org/";
        String METHOD_NAME="sp_sr_consulta_cliente_ws";


        SoapObject Request1= new SoapObject(NAME_SPACE,METHOD_NAME);
        Request1.addProperty("contrato", param1);
        SoapSerializationEnvelope soapEnvelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.dotNet=true;
        soapEnvelope.setOutputSoapObject(Request1);
        HttpTransportSE transport1 = new HttpTransportSE(URL);

        try {
            transport1.call(SOAP_ACTION,soapEnvelope);
            SoapObject result, result12, result13,result14;
            SoapPrimitive contrato, nombre, direccion, referencia, estado, telefono;
            result= (SoapObject) soapEnvelope.getResponse();
            result12 = (SoapObject) result.getProperty("diffgram");
            result13 = (SoapObject) result12.getProperty("NewDataSet");
            result14 = (SoapObject) result13.getProperty("Table");
            contrato = (SoapPrimitive) result14.getProperty("CON_ID_CONTRATO");
            nombre = (SoapPrimitive) result14.getProperty("PER_NOMBRE");
            direccion = (SoapPrimitive) result14.getProperty("CON_DIRECCION");
            referencia = (SoapPrimitive) result14.getProperty("CON_REFERENCIA");
            estado = (SoapPrimitive) result14.getProperty("CON_ESTADO");
            telefono = (SoapPrimitive) result14.getProperty("CON_TELEFONO");

            System.out.println("Contrato: " + contrato);
            System.out.println("Nombre: " + nombre);
            System.out.println("Direccion: " + direccion);

            txmostrarcontrato.setText(" La Información describe al cliente:\n"
                    +"Contrato #: "+contrato.toString()+ "\n"+
                    "Cliente: " + nombre.toString() + "\n"+
                    "Direccion: " + direccion.toString()+ "\n"+
                    "Referencia: " + referencia.toString()+ "\n"+
                    "Estado: " + estado.toString()+ "\n"+
                    "Telefono: "+ telefono.toString());

        }
        catch (Exception exa){
            exa.printStackTrace();
        }
    }
    //////////// Clase tercer plano para el metodo Buscar
    private class tercerplano extends AsyncTask {
        @Override
        protected Void doInBackground(Object[] objects) {
            buscar();
            return null;
        }
        @Override
        protected void onPostExecute(Object o ){
            super.onPostExecute( o);

        }
    }
//////////////////////// Comenzamos metodo para obtener las Coordenadas del CLiente WebService
    public void mapas1 () {
        String URL="http://172.17.38.10:55025/Servicios.asmx";
        String SOAP_ACTION="http://tempuri.org/sp_sr_consulta_coordenadas_ws";
        String NAME_SPACE="http://tempuri.org/";
        String METHOD_NAME="sp_sr_consulta_coordenadas_ws";

        SoapObject Request4= new SoapObject(NAME_SPACE,METHOD_NAME);
        Request4.addProperty("LI_ID_CONTRATO", param1);
        SoapSerializationEnvelope soapEnvelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.dotNet=true;
        soapEnvelope.setOutputSoapObject(Request4);
        HttpTransportSE transport4 = new HttpTransportSE(URL);


        try {
            transport4.call(SOAP_ACTION,soapEnvelope);
            SoapObject resultado_coor=(SoapObject) soapEnvelope.bodyIn;
            resultado3=resultado_coor.getProperty(0).toString().substring(252);
          System.out.println(resultado3);

        }

        catch (Exception exa){

            exa.printStackTrace();

        }

    }
///////////////////// clase para Mapas
    private class cuartoplano extends AsyncTask {
        @Override
        protected Void doInBackground(Object[] objects) {
            mapas1();
            return null;
        }
        @Override
        protected void onPostExecute(Object o ){
            super.onPostExecute( o);

        }
    }

}

