package com.stpub.stppocket;

import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.List;

import com.stpub.stppocket.data.DBHandler;
import com.stpub.stppocket.data.TableData;
import com.stpub.stppocket.helper.Helper;

import static android.R.attr.handle;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks {
    EditText username;
    EditText password;
    ProgressBar progressBar;
    Button btnLogin, btnBrowseLocal;

    public static final String EXTRA_MESSAGE = null;

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

    private long mLastClickTime = 0; // Record the last click time when clicking, to prevent double click.


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

        username.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
              if(!hasFocus){
                  if (username.getText().length() == 0 || password.getText().length() == 0){
                      btnLogin.setEnabled(false);
                  } else {
                      btnLogin.setEnabled(true);
                  }
              }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(!hasFocus){
                    if (username.getText().length() != 0 && password.getText().length() != 0){
                        btnLogin.setEnabled(true);
                    } else {
                        btnLogin.setEnabled(false);
                    }
                }
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(username.getText().length() != 0 && password.getText().length() != 0){
                    btnLogin.setEnabled(true);
                } else {
                    btnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(username.getText().length() != 0 && password.getText().length() != 0){
                    btnLogin.setEnabled(true);
                } else {
                    btnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnBrowseLocal = (Button) findViewById(R.id.btnBrowseLocal);
        btnLogin.setOnClickListener(this);
        btnBrowseLocal.setOnClickListener(this);

        if(hasOfflineData()){
            btnBrowseLocal.setEnabled(true);
        } else {
            btnBrowseLocal.setEnabled(false);
        }

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

                        if(status.isSuccess()){
                            // Auto sign-in success
                            handleCredential(credentialRequestResult.getCredential());
                        } else if (status.getStatusCode() == CommonStatusCodes.RESOLUTION_REQUIRED && shouldResolve){
                            // Getting credential needs to show some UI, start resolution
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

        if(IdentityProviders.GOOGLE.equals(credential.getAccountType())){
            // Google account, rebuild GoogleApiClient to set account name and then try
        } else {
            // Email/password account
            String status = String.format("Signed in as %s", credential.getId());
            username.setText(credential.getId());
            password.setText(credential.getPassword());
            btnLogin.setEnabled(true);
        }
    }


    private void saveCredentialIfConnected(Credential credential){
        if (credential == null){
            Log.i("INFO", "No credential, return.");
            return;
        }

        // Save Credential if the GoogleApiClient is connected, otherwise
        // the Credential is cached and will be saved when onConnected is next called.
        mCredentialToSave = credential;
        if(mGoogleApiClient.isConnected()){
            Auth.CredentialsApi.save(mGoogleApiClient, mCredentialToSave).setResultCallback(
                    new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if(!status.isSuccess()){
                                if(status.hasResolution()){
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
                status.startResolutionForResult(MainActivity.this, requestCode);
                mIsResolving = true;
            } catch (IntentSender.SendIntentException e) {
                Log.e(TAG, "Failed to send Credentials intent.", e);
                mIsResolving = false;
            }
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


    @Override
    public void onClick(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        switch(view.getId()){
            case R.id.btnLogin:
                if(!validateUI()) {
                    Toast.makeText(getBaseContext(), "Invalid email or password!", Toast.LENGTH_LONG).show();
                    return;
                }
                // call AsynTask to perform network operation on separate thread
                ((Helper)this.getApplication()).setOffline(false);
                HttpAsyncTask myTask = new HttpAsyncTask(this);
                myTask.execute(username.getText().toString(), password.getText().toString());
                break;
            case R.id.btnBrowseLocal:
                // Check if there are local data.
                if (hasOfflineData()){
                    Intent intent = new Intent(this, PublicationActivity.class);
                    intent.putExtra(EXTRA_MESSAGE, "offline");
                    ((Helper)this.getApplication()).setOffline(true);
                    startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), "No publications downloaded.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }


    private boolean hasOfflineData(){
        DBHandler db = DBHandler.getInstance(this);
        SQLiteDatabase stpDb = db.getReadableDatabase();
        List<TableData> pubs = db.getAllPublications(stpDb);

        if (pubs.size() > 0)
            return true;
        else
            return false;
    }

    private class HttpAsyncTask extends AsyncTask<String, String, HttpAsyncTask.AsyncTaskResult<String>> {
        private Exception exception = null;
        private Context context;

        public HttpAsyncTask(Context context){
            this.context = context;
        }

        protected void onPreExecute(){
            progressBar.setVisibility(View.VISIBLE);
        }

        protected  void  onPostExecute(AsyncTaskResult<String> result) {
            progressBar.setVisibility(View.GONE);

            if (result.getError() != null){
                Log.i("DEBUG", result.getError().getMessage() + getBaseContext().toString());
                Toast.makeText(getBaseContext(), result.getError().getMessage(), Toast.LENGTH_SHORT).show();

                return;
            }

           // Log.i("INFO", response);
            try {
                JSONArray jsonArray = new JSONArray(result.getResult());
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                int userid = jsonObject.getInt("userid");
                Intent intent = new Intent(context, PublicationActivity.class);
                intent.putExtra(EXTRA_MESSAGE, String.valueOf(userid));

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
                Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected AsyncTaskResult<String> doInBackground(String... args){
            return POST(getString(R.string.URL_END_POINT) + "users", args[0], args[1]);
        }


        public  AsyncTaskResult<String> POST(String urlString, String user, String pwd) {

            try {
                URL url = new URL(urlString);
                HttpURLConnection connection =(HttpURLConnection) url.openConnection();

                try {
                    connection.setDoOutput(true);
                    connection.setDoInput(true);
                    connection.setRequestProperty("User-Agent", "");
                    connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setRequestMethod("POST");

                    String json = "{\"email\":\"" + user +"\",\"password\":\"" + pwd + "\"}";

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

                        return new AsyncTaskResult<String> (sb.toString());
                    }
                    else if(HttpResult == HttpURLConnection.HTTP_NOT_FOUND){
                        return new AsyncTaskResult<String>(new Exception("Bad email or password.") );
                    }
                    else {
                        Log.e("Error", "Call POST failed: " + connection.getResponseMessage());
                        return new AsyncTaskResult<String>(new Exception(connection.getResponseMessage()) );
                    }
                }
                catch (Exception e){
                    Log.e("ManiActivity", "ee" + e.getMessage());
                    e.printStackTrace();
                    return new AsyncTaskResult<String> (e);
                }
                finally {
                    connection.disconnect();
                }
            }
            catch (IOException e) {
                Log.e("ManiActivity", "dee" + e.getMessage());
                e.printStackTrace();
                this.exception = e;
                return new AsyncTaskResult<String> (e);
            }
        }


        protected class AsyncTaskResult<String> {
            private String result;
            private Exception error;

            public String getResult() {
                return result;
            }

            public Exception getError() {
                return error;
            }

            public AsyncTaskResult(String result) {
                super();
                this.result = result;
            }

            public AsyncTaskResult(Exception error) {
                super();
                this.error = error;
            }
        }
    }
}
