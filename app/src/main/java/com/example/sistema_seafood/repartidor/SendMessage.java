package com.example.sistema_seafood.repartidor;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

public class SendMessage {
    private String numero;

    public SendMessage(String numero) {
        this.numero=numero;
    }

    public void sendMessage() {
        String token = "EAAX9oq30FEgBO3gHZA6Sj7Q9dB13OLP9ZCMZCfCdagbCWzrqGqpaYmZCHe45810n72slx7o0Bhaa4yS2MfJcKNWWhwy4muGi92fGkKfW0ZAMX9VpvsLf3utjH5cpcmPry0Tx2HTZAWy5mmrgLHKDvKZAmeOCC6dZCpcDs6H9pKnkV8nTrkXHZBik5iYQlIlGAErSeunRZAX7H7gBzvMwGkGKMZD";
        //NUESTRO TELEFONO
        String telefono = "9585857856";
        //IDENTIFICADOR DE NÚMERO DE TELÉFONO
        String idNumero = "104393359410884";
        //COLOCAMOS LA URL PARA ENVIAR EL MENSAJE
        new NetworkTask().execute("https://graph.facebook.com/v17.0/" + idNumero + "/messages");
    }

    private class NetworkTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String urlString = urls[0];

            URL url = null;
            try {
                url = new URL(urlString);
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }

            //INICIALIZAMOS EL CONTENEDOR DEL ENVIO
            HttpURLConnection httpConn;

            {
                try {
                    httpConn = (HttpURLConnection) url.openConnection();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            //EL TIPO DE ENVIO DE DATOS VA A SER VIA POST
            try {
                httpConn.setRequestMethod("POST");
            } catch (ProtocolException e) {
                throw new RuntimeException(e);
            }
            //CODIGO DE AUTORIZACION DE JAVA
            httpConn.setRequestProperty("Authorization", "Bearer " + "EAAX9oq30FEgBOZCZAoO7110qBfyJZAYuUOJFqIlvF5hfcfTPozZCGuZCtv0iedeS3kAWHk5w6AHbNeSWmIZAQWFRremNP9A8gc7fT3WbwZAOkhKbG42XtfmrZA69bAzZBjl6EfDR6xp97izteC2BI3onbdIzfufe9Bqajd7Nb4iL3g8LvADng1Ji2gejBunVbhIqgZBJYFFBPR7gaCawNZBu2oZD");
            //DEFINIMOS QUE LOS DATOS SERAN TRATADOS COMO JSON
            httpConn.setRequestProperty("Content-Type", "application/json; application/x-www-form-urlencoded; charset=UTF-8");
            //PREPARAMOS Y ENVIAMOS EL JSON
            httpConn.setDoOutput(true);
            OutputStreamWriter writer = null;
            try {
                writer = new OutputStreamWriter(httpConn.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                writer.write("{ "
                        + "\"messaging_product\": \"whatsapp\", "
                        + "\"to\": \"" + "52"+numero + "\", "
                        + "\"type\": \"template\", "
                        + "\"template\": "
                        + "  { \"name\": \"hello_world\", "
                        + "    \"language\": { \"code\": \"en_US\" } "
                        + "  } "
                        + "}");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //LIMPIAMOS LOS DATOS
            try {
                writer.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //CERRAMOS LOS DATOS
            try {
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //CERRAMOS LA CONEXION
            try {
                httpConn.getOutputStream().close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            //RECIBIMOS EL RESULTADO DEL ENVIO
            InputStream responseStream = null;
            try {
                responseStream = httpConn.getResponseCode() / 100 == 2
                        ? httpConn.getInputStream()
                        : httpConn.getErrorStream();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Scanner s = new Scanner(responseStream).useDelimiter("\\A");
            //OBTENEMOS LOS RESULTADOS
            String respuesta = s.hasNext() ? s.next() : "";
            System.out.println(respuesta);
            System.out.println("HELLO WORLD !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            return respuesta;
        }


        @Override
        protected void onPostExecute(String result) {
            // Aquí puedes manejar el resultado de la solicitud de red
            if (result != null) {
                // Procesar la respuesta aquí
            }
        }
    }
}

