package com.stpub.stppocket;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE ="hii";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    //** Called when the user taps the Login button */
    public void login(View view){
        Intent intent = new Intent(this, PublicationActivity.class);
        EditText username = (EditText) findViewById(R.id.etUserName);
        EditText password = (EditText) findViewById(R.id.etPassword);
        String user = username.getText().toString();
        String pwd = password.getText().toString();

        if(user.length() == 0){
            username.setError("Your email is required!");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(message).matches()){
            username.setError("Invaild email address!");
        } else if (pwd.length() == 0){
            password.setError("Password is required!");
        }
        else {
            intent.putExtra(EXTRA_MESSAGE, user);
            startActivity(intent);
        }
    }
}
