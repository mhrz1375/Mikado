package ir.Mikado;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONDownloader {

    private final String log_label = "ErrorMessage";

    public String DownloadURL(String strUrl) {

        String data = "";

        InputStream myStream;

        try {
            URL url = new URL(strUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setReadTimeout(10000);

            connection.setConnectTimeout(15000);

            connection.setRequestMethod("GET");

            connection.setDoInput(true);


            connection.connect();

            myStream = connection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(myStream));

            StringBuilder stringBuilder = new StringBuilder();

            String line;

            while ((line = bufferedReader.readLine()) != null) {

                stringBuilder.append(line);

            }

            data = stringBuilder.toString();

            bufferedReader.close();
            connection.disconnect();
            myStream.close();
        } catch (Exception e) {
            Log.i(log_label, " Error In JSONDownloader " + e.getMessage());
        }
        return (data);
    }


}
