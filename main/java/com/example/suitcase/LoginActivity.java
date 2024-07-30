package com.example.suitcase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    EditText username_text;
    EditText password_text;
    Button login_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        username_text = (EditText) findViewById(R.id.editusername);
        password_text = (EditText) findViewById(R.id.editpassword);
        login_button = (Button) findViewById(R.id.loginbuttonLA);

        login_button.setOnClickListener(this);
    }

    //checks whether a login with username and password match
    private Boolean CheckLogin(String username, String password){
        DatabaseHandler databaseHandler = new DatabaseHandler(LoginActivity.this);
        if(databaseHandler.getLogin(username, password) == 0){
            return false;
        }

        return true;
    }
    @Override public void onClick(View view){
        int id = view.getId();
        if(id == R.id.loginbuttonLA){
            if(CheckLogin(username_text.getText().toString(), password_text.getText().toString())){
                //if correct username and password open the homepage with this users ID passed to it
                DatabaseHandler databaseHandler = new DatabaseHandler(this);
                Intent homepage_intent = new Intent(this, HomepageActivity.class);
                homepage_intent.putExtra(RegisterActivity.EXTRA_MESSAGE, databaseHandler.getUserID(username_text.getText().toString()));
                startActivity(homepage_intent);
            }else{
                //if username or password is incorrect inform user and clear password field
                password_text.setText("");
                Toast.makeText(this, "Username or Password is incorrect", Toast.LENGTH_LONG).show();
            }
        }
    }
}