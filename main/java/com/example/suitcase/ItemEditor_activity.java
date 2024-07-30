package com.example.suitcase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class ItemEditor_activity extends AppCompatActivity implements View.OnClickListener{

    int userid;
    LatLng item_geo;
    Item editItem;
    Boolean editMode;
    EditText itemname_text;
    EditText itemdescription_text;
    EditText itemprice_text;
    Switch itempurchased_switch;
    Button confirm_button;
    Button sms_button;
    Button map_button;
    final int MAP_ACTIVITY = 1;
    static final String MAP_RESULT = "result";
    static final String MAP_LATLNG = "latlng";

    //takes a phone number and message and attempts to use the devices SMS to send a message
    public void sendSMS(String phone, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
            Toast.makeText(getApplicationContext(), "Message Sent to: " + phone, Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            //informs user of the error that occurred if the text cannot be sent
            Toast.makeText(getApplicationContext(),ex.getMessage().toString(), Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_editor);

        Intent get_intent = getIntent();
        userid = get_intent.getIntExtra(HomepageActivity.EXTRA_MESSAGE, 0);
        editMode = get_intent.getBooleanExtra(HomepageActivity.EXTRA_MESSAGE_EDIT, false);

        itemname_text = (EditText) findViewById(R.id.edititemname);
        itemdescription_text = (EditText) findViewById(R.id.edititemdescription);
        itemprice_text = (EditText) findViewById(R.id.edititemprice);
        itempurchased_switch = (Switch) findViewById(R.id.switchpurchased);
        confirm_button = (Button) findViewById(R.id.confirmitembutton);
        sms_button = (Button) findViewById(R.id.smsbutton);
        map_button = (Button) findViewById(R.id.mapbutton);

        sms_button.setOnClickListener(this);
        confirm_button.setOnClickListener(this);
        map_button.setOnClickListener(this);

        //assuming not in edit mode hides the SMS button
        sms_button.setVisibility(View.GONE);

        if(editMode){
            //if this activity is in edit mode, fill inputs with item values and show SMS button
            editItem = (Item) get_intent.getParcelableExtra(HomepageActivity.EXTRA_MESSAGE_ITEM);
            itemname_text.setText(editItem.getName());
            itemdescription_text.setText(editItem.getDescription());
            itemprice_text.setText(Float.toString(editItem.getPrice()));
            itempurchased_switch.setChecked(editItem.getPurchased());
            confirm_button.setText("Confirm Edit");
            sms_button.setVisibility(View.VISIBLE);
        }
    }

    @Override public void onClick(View view){
        int id = view.getId();

        if(id == R.id.confirmitembutton){
            DatabaseHandler databaseHandler = new DatabaseHandler(this);

            if(editMode){
                //if in edit mode send an update request with all the inputs
                Item item = new Item(editItem.getID(), itemname_text.getText().toString(), itemdescription_text.getText().toString(), Float.parseFloat(itemprice_text.getText().toString()), itempurchased_switch.isChecked(), item_geo.latitude, item_geo.longitude);
                databaseHandler.updateItem(item);
                Toast.makeText(this, "You've updated "+editItem.getName()+" successfully!", Toast.LENGTH_LONG).show();
            }else {
                //if not in edit mode send an add request with all the inputs
                Item item = new Item(itemname_text.getText().toString(), itemdescription_text.getText().toString(), Float.parseFloat(itemprice_text.getText().toString()), itempurchased_switch.isChecked(), item_geo.latitude, item_geo.longitude);

                databaseHandler.addItem(item, userid);

                Toast.makeText(this, "Your new item has been created!", Toast.LENGTH_LONG).show();
            }
            finish();
        }
        else if(id == R.id.smsbutton){
            //create a dialog with a number keyboard input to type a phone number and send a message when the user clicks Send
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("SMS phone number for receiver?");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            input.setRawInputType(Configuration.KEYBOARD_12KEY);
            alert.setView(input);
            alert.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Item item = new Item(editItem.getID(), itemname_text.getText().toString(), itemdescription_text.getText().toString(), Float.parseFloat(itemprice_text.getText().toString()), itempurchased_switch.isChecked(), item_geo.latitude, item_geo.longitude);
                    sendSMS(input.getText().toString(), item.toString());
                }
            });
            alert.setNegativeButton("Cancel", null);
            alert.show();
        } else if (id == R.id.mapbutton) {
            //open the Google Map
            Intent map_activity = new Intent(this, MapsActivity.class);
            if(editMode){
                //open the Google Map in edit mode
                map_activity.putExtra(MAP_LATLNG, new double[]{editItem.getLat(), editItem.getLng()});
            }
            //starts the map activity with a result being expected back when it finsihes
            startActivityForResult(map_activity, MAP_ACTIVITY);
        }
    }

    //required function to get results from closed activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MAP_ACTIVITY) {
            if(resultCode == Activity.RESULT_OK){
                //get the maps LAT and LNG value when it closes
                item_geo = (LatLng) data.getParcelableExtra(MAP_RESULT);
            }
        }
    }
}