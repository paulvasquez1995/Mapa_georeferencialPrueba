package com.example.mapa_georeferencialprueba;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class MainActivity extends AppCompatActivity {
    TextView tv_mostrar;
    EditText et_contrasena1, et_usuario1;
    Button btn_ingresar1;
    String mensaje, usuarioparam,contrasenaparam ;
    Integer resultado_numero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /////////esta funcion sirve para permitir envio de datos
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new
                    StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
/////////////////////// Sigue el metodo OnCreate
        et_contrasena1=(EditText)findViewById(R.id.et_contrasena);
        et_usuario1=(EditText)findViewById(R.id.et_Usuario);
        tv_mostrar=(TextView)findViewById(R.id.tx_pruebamostrar);
        btn_ingresar1=(Button)findViewById(R.id.btn_ingresar);


        btn_ingresar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                usuarioparam=et_usuario1.getText().toString();
                contrasenaparam=et_contrasena1.getText().toString();
                segundoplano_loggin tarea =new segundoplano_loggin();

                tarea.execute();
            }
        });

    }
/////////////////////metodo segundo plano y lectura de web service

    public  void validar_usuario(){
        String URL="http://172.17.38.10:55025/Servicios.asmx";
        String SOAP_ACTION="http://tempuri.org/sp_se_ws_usuario_valida_clave";
        String NAME_SPACE="http://tempuri.org/";
        String METHOD_NAME="sp_se_ws_usuario_valida_clave";


        SoapObject Requerimiento= new SoapObject(NAME_SPACE,METHOD_NAME);
        Requerimiento.addProperty("AS_USU_USUARIO", usuarioparam);
        Requerimiento.addProperty("AS_CLAVE",contrasenaparam);

        SoapSerializationEnvelope soapEnvelope =new SoapSerializationEnvelope(SoapEnvelope.VER11);
        soapEnvelope.dotNet=true;
        soapEnvelope.setOutputSoapObject(Requerimiento);
        HttpTransportSE transport = new HttpTransportSE(URL);

        try {

            transport.call(SOAP_ACTION,soapEnvelope);
            SoapObject resultadologgin = (SoapObject) soapEnvelope.bodyIn;
            mensaje=resultadologgin.getPrimitiveProperty("sp_se_ws_usuario_valida_claveResult").toString();
            resultado_numero=Integer.parseInt(mensaje);
            System.out.println(mensaje);
           // tv_mostrar.setText("Resultado  :"+ resultado_numero);
            if (resultado_numero==5){
                tv_mostrar.setText("Informacion Correcta, Pulse el Boton Ingresar");
            }else if(resultado_numero==4){
                tv_mostrar.setText("Clave Incorrecta, Digite nuevamente su clave");
            } else if (resultado_numero==3){
                tv_mostrar.setText("Cuenta de Usuario Bloqueada, Comuniquese con el Administrador");
            } else if(resultado_numero==2){
                tv_mostrar.setText("Usuario No esta Activo, Comuniquese Con el Administrador");
            } else if(resultado_numero==1){
                tv_mostrar.setText("Usuario No Existe, Verifique el campo Usuario");
            }


        } catch (Exception ex){

            mensaje = "Error"+ ex.getMessage();

        }
}

    private class segundoplano_loggin extends AsyncTask {
        @Override
        protected Void doInBackground(Object[] objects) {
            validar_usuario();
            return null;
        }
        @Override
        protected void onPostExecute(Object o ){
            super.onPostExecute( o);

        }
    }
////////////////////////////////////////////////////////////////////////


    //metodo para boton pasar al otro activity
    public void siguiente (View view){

        if(resultado_numero==5){
            Intent ingresar = new Intent(this,Activity2.class);
            startActivity(ingresar);
            //mantener la pantalla ahi
            finish();
        }    else  {
            return;
        }
    }

}
