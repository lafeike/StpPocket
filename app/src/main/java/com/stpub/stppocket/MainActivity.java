package com.stpub.stppocket;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;



public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    EditText username;
    EditText password;
    ProgressBar progressBar;
    Button btnLogin, btnBrowseLocal;

    public static final String EXTRA_MESSAGE ="hii";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.etUserName);
        password = (EditText) findViewById(R.id.etPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnBrowseLocal = (Button) findViewById(R.id.btnBrowseLocal);
        btnLogin.setOnClickListener(this);
        btnBrowseLocal.setOnClickListener(this);
    }

    public static String POST(String urlString) {
        InputStream inputStream = null;
        String result = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            try {
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setRequestProperty("User-Agent", "");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestMethod("POST");

                String json = "{\"email\":\"rafyz@stpub.com\",\"password\":\"slcye2yd\"}";

                OutputStream os = connection.getOutputStream();
                os.write(json.getBytes("UTF-8"));
                os.close();

                StringBuilder sb = new StringBuilder();
                int HttpResult = connection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();

                    result = sb.toString();
                    System.out.println("Got response from server: " + result);

                } else {
                    System.out.println(connection.getResponseMessage());
                }
                return result;
            }
            finally {
                connection.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private boolean validateUI(){
        boolean uiValid = true;

        String user = username.getText().toString();
        String pwd = password.getText().toString();

        if(user.length() == 0){
            username.setError("Your email is required!");
            uiValid = false;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(user).matches()){
            username.setError("Invalid email address!");
            uiValid = false;
        }
        if(pwd.length() == 0){
            password.setError("Password is required!");
            uiValid = false;
        }
        return uiValid;
    }

    private void checkLogin(String user, String password) {

    }

    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.btnLogin:
                if(!validateUI())
                    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                // call AsynTask to perform network operation on separate thread
                HttpAsyncTask myTask = new HttpAsyncTask(this);
                myTask.execute("a", "b");
                break;
        }

    }

    private class HttpAsyncTask extends AsyncTask<String, String, String> {
        private Exception exception;
        private Context context;

        public HttpAsyncTask(Context context){
            this.context = context;
        }

        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
        }

        protected  void  onPostExecute(String response) {
            if (response == null){
                response = "There was an error.";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);
            try {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                int userid = jsonObject.getInt("userid");
                Intent intent = new Intent(context, PublicationActivity.class);
                intent.putExtra(EXTRA_MESSAGE, String.valueOf(userid));
                startActivity(intent);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... args){
            String user = args[0];
            String pass = args[1];
            String URL_END_POINT = getString(R.string.URL_END_POINT);

            return POST(URL_END_POINT + "users");
        }
    }
}
