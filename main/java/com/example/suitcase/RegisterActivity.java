package com.example.suitcase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    public static String EXTRA_MESSAGE = "registeractivity.message";
    DatabaseHandler databaseHandler = new DatabaseHandler(RegisterActivity.this);
    EditText fullname_text;
    EditText email_text;
    EditText username_text;
    EditText password_text;
    EditText confpassword_text;
    Button create_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fullname_text = (EditText) findViewById(R.id.editnameregister);
        email_text = (EditText) findViewById(R.id.editemailregister);
        username_text = (EditText) findViewById(R.id.editusernameregister);
        password_text = (EditText) findViewById(R.id.editpasswordregister);
        confpassword_text = (EditText) findViewById(R.id.editpasswordconfregister);
        create_button = (Button) findViewById(R.id.createaccountbutton);

        create_button.setOnClickListener(this);
    }

    private Boolean RegisterNewUser(RegisterUser user){
        String conf_password = confpassword_text.getText().toString();

        if(!(user.getPassword().equals(conf_password))){
            //passwords must match to create an account
            Toast.makeText(this,"Passwords must match!", Toast.LENGTH_SHORT).show();
            return false;
        }else{
            databaseHandler.addUser(user);
            return true;
        }
    }

    @Override public void onClick(View view){
        int id = view.getId();
        RegisterUser new_user = new RegisterUser(fullname_text.getText().toString(), username_text.getText().toString(), password_text.getText().toString(), email_text.getText().toString());
        if(id == R.id.createaccountbutton){
            if(RegisterNewUser(new_user)){
                //open homepage with newly registered users id
                Intent homepage_intent = new Intent(this, HomepageActivity.class);
                homepage_intent.putExtra(EXTRA_MESSAGE, databaseHandler.getUserID(new_user.getUsername()));
                startActivity(homepage_intent);
            }
        }
    }
}