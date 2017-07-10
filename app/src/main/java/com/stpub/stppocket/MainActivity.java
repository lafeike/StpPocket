package com.stpub.stppocket;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialRequest;
import com.google.android.gms.auth.api.credentials.CredentialRequestResult;
import com.google.android.gms.auth.api.credentials.IdentityProviders;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

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

import static android.R.attr.handle;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks {
    EditText username;
    EditText password;
    ProgressBar progressBar;
    Button btnLogin, btnBrowseLocal;

    public static final String EXTRA_MESSAGE ="hii";

    private GoogleApiClient mGoogleApiClient;
    private boolean mIsResolving = false;
    private Credential mCredential;
    private Credential mCredentialToSave;


    private static final String TAG = "MainActivity";
    private static final String KEY_IS_RESOLVING = "is_resolving";
    private static final String KEY_CREDENTIAL = "key_credential";
    private static final String KEY_CREDENTIAL_TO_SAVE = "key_credential_to_save";

    private static final int RC_SIGN_IN = 1;
    private static final int RC_CREDENTIALS_READ = 2;
    private static final int RC_CREDENTIALS_SAVE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mIsResolving = savedInstanceState.getBoolean(KEY_IS_RESOLVING, false);
            mCredential = savedInstanceState.getParcelable(KEY_CREDENTIAL);
            mCredentialToSave = savedInstanceState.getParcelable(KEY_CREDENTIAL_TO_SAVE);
        }

        username = (EditText) findViewById(R.id.etUserName);
        password = (EditText) findViewById(R.id.etPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnBrowseLocal = (Button) findViewById(R.id.btnBrowseLocal);
        btnLogin.setOnClickListener(this);
        btnBrowseLocal.setOnClickListener(this);

        // create an instance of GoogleApiClient to request stored credentials
        buildGoogleApiClient(null);
    }

    private void buildGoogleApiClient(String accountName){
        GoogleSignInOptions.Builder gsoBuilder = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail();

        if (accountName != null){
            gsoBuilder.setAccountName(accountName);
        }

        if(mGoogleApiClient != null){
            mGoogleApiClient.stopAutoManage(this);
        }

        GoogleApiClient.Builder builder = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gsoBuilder.build());

        mGoogleApiClient = builder.build();
        //requestCredentials(true, true);
    }


    private  void requestCredentials(final boolean shouldResolve, boolean onlyPasswords){
        CredentialRequest.Builder crBuilder = new CredentialRequest.Builder()
                .setPasswordLoginSupported(true);

        if(!onlyPasswords){
            crBuilder.setAccountTypes(IdentityProviders.GOOGLE);
        }

       // showProgress();
        Auth.CredentialsApi.request(mGoogleApiClient, crBuilder.build()).setResultCallback(
                new ResultCallback<CredentialRequestResult>() {
                    @Override
                    public void onResult(@NonNull CredentialRequestResult credentialRequestResult) {
                       // hideProgress();
                        Status status = credentialRequestResult.getStatus();
                        Log.d(TAG, "credential status:" + status);
                        if(status.isSuccess()){
                            // Auto sign-in success
                            handleCredential(credentialRequestResult.getCredential());
                        } else if (status.getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED && shouldResolve){
                            // Getting credential needs to show some UI, start resolution
                            Log.w(TAG, "start resolution");
                            resolveResult(status, RC_CREDENTIALS_READ);
                        }
                    }
                }
        );
    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_IS_RESOLVING, mIsResolving);
        outState.putParcelable(KEY_CREDENTIAL, mCredential);
        outState.putParcelable(KEY_CREDENTIAL_TO_SAVE, mCredentialToSave);
    }


    @Override
    public void onStart() {
        super.onStart();

        if(!mIsResolving) {
            requestCredentials(true, true);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + requestCode + ":" + data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult gsr = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
           // handleGoogleSignIn(gsr);
        } else if(requestCode == RC_CREDENTIALS_READ){
            mIsResolving = false;
            if(resultCode == RESULT_OK) {
                Credential credential = data.getParcelableExtra(Credential.EXTRA_KEY);
                handleCredential(credential);
            }
        } else if(requestCode == RC_CREDENTIALS_SAVE) {
            mIsResolving = false;
            if (requestCode == RESULT_OK) {
                Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "Credential save failed.");
            }
        }
    }

    private void handleCredential(Credential credential){
        mCredential = credential;

        Log.d(TAG, "handleCredential:" + credential.getAccountType() + ":" + credential.getId());
        if(IdentityProviders.GOOGLE.equals(credential.getAccountType())){
            // Google account, rebuild GoogleApiClient to set account name and then try
        } else {
            // Email/password account
            String status = String.format("Signed in as %s", credential.getId());
            ((TextView)findViewById(R.id.etUserName)).setText(credential.getId());
            ((TextView)findViewById(R.id.etPassword)).setText(credential.getPassword());
        }
    }


    private void saveCredentialIfConnected(Credential credential){
        Log.d(TAG, "start saveCredentialIfConnected.");
        if (credential == null){
            Log.i("INFO", "No credential, return.");
            return;
        }

        // Save Credential if the GoogleApiClient is connected, otherwise
        // the Credential is cached and will be saved when onConnected is next called.

        mCredentialToSave = credential;
        if(mGoogleApiClient.isConnected()){
            Log.d(TAG, "start to save credential:" + mCredentialToSave);
            Auth.CredentialsApi.save(mGoogleApiClient, mCredentialToSave).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if(status.isSuccess()){
                                Log.d(TAG, "SAVE: OK");
                            } else {
                                if(status.hasResolution()){
                                    Log.d(TAG, "has resolution.");
                                    // Prompt the user if the credential is new.
                                    resolveResult(status, RC_CREDENTIALS_SAVE);
                                }else {
                                    Log.d(TAG, "Save failed.");
                                }
                            }
                        }
                    });
        }
    }


    @Override
    public void onConnected(Bundle bundle){
        saveCredentialIfConnected(mCredentialToSave);
    }

    @Override
    public void onConnectionSuspended(int i){}


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult){
        Log.w(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "An error has occurred.", Toast.LENGTH_SHORT).show();
    }

    private void resolveResult(Status status, int requestCode) {
        if (!mIsResolving) {
            try {
                Log.d(TAG, "resolveResult:" + requestCode);
                status.startResolutionForResult(MainActivity.this, requestCode);
                mIsResolving = true;
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Failed to send Credentials intent.", e);
                mIsResolving = false;
            }
        }
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
           // Log.i("INFO", response);
            try {
                JSONArray jsonArray = new JSONArray(response);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                int userid = jsonObject.getInt("userid");
                Intent intent = new Intent(context, PublicationActivity.class);
                intent.putExtra(EXTRA_MESSAGE, String.valueOf(userid));

                Log.i("DEBUG", "to save Credential...");
                // Save Credential for next login
                String user = username.getText().toString();
                String pwd = password.getText().toString();
                Credential credential = new Credential.Builder(user)
                        .setPassword(pwd)
                        .build();
                saveCredentialIfConnected(credential);

                // Navigate to next page
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
