//CET343 Codie Heaney bh97rt
package com.example.suitcase;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button register_button;
    Button login_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_button = (Button) findViewById(R.id.loginbutton);
        register_button = (Button) findViewById(R.id.registerbutton);

        login_button.setOnClickListener(this);
        register_button.setOnClickListener(this);
    }
    @Override public void onClick(View view){
        int id = view.getId();
        if(id == R.id.registerbutton){
            //register new account
            Intent register_intent = new Intent(this, RegisterActivity.class);
            startActivity(register_intent);
        }else if(id == R.id.loginbutton){
            //login to existing account
            Intent login_intent = new Intent(this, LoginActivity.class);
            startActivity(login_intent);
        }
    }
}