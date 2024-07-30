package com.example.suitcase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.ArrayList;
import java.util.List;

public class HomepageActivity extends AppCompatActivity implements View.OnClickListener{
    static public String EXTRA_MESSAGE = "homepage_id";
    static public String EXTRA_MESSAGE_ITEM = "homepage_item";
    static public String EXTRA_MESSAGE_EDIT = "homepage_edit";
    int userid;
    Button create_item_button;

    LinearLayout scrollView;

    ConstraintLayout itemTemp;

    //this function updates the UI inside the scrollview
    public void UpdateUI(){

        //clear all items in the scrollview currently
        scrollView.removeAllViews();


        LayoutInflater inflater = LayoutInflater.from(this);

        ConstraintLayout layoutBuffer = (ConstraintLayout) inflater.inflate(R.layout.item_layout, null, false);
        layoutBuffer.setVisibility(View.INVISIBLE);
        scrollView.addView(layoutBuffer);   //buffer view added to prevent items being covered by the create item button

        DatabaseHandler databaseHandler = new DatabaseHandler(this);

        List<Item> itemList = databaseHandler.getItems(userid);

        //loops through all items found for this user and creates new instances of a predefined layout in the scrollview
        for(int i = 0; i < itemList.size(); i++){
            int finalI = i;

            ConstraintLayout layout = (ConstraintLayout) inflater.inflate(R.layout.item_layout, null, false);

            TextView itemt = (TextView) layout.getViewById(R.id.textitem);
            itemt.setText(itemList.get(i).getName());

            Switch purchaseS = (Switch) layout.getViewById(R.id.purchasedswitch);
            purchaseS.setChecked(itemList.get(i).getPurchased());

            purchaseS.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //updates this items purchased value when clicked
                    if(view.getId() == purchaseS.getId()){
                        Item item = new Item(itemList.get(finalI), purchaseS.isChecked());
                        databaseHandler.updateItem(item);
                        UpdateUI();
                    }
                }
            });

            Button editb = (Button) layout.getViewById(R.id.edititembutton);
            editb.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View view) {
                    //sets up edit button to open the item editor in edit mode with this layouts specific item
                    if(view.getId() ==  editb.getId()){
                        Intent create_intent = new Intent(HomepageActivity.this, ItemEditor_activity.class);
                        create_intent.putExtra(EXTRA_MESSAGE, userid);
                        create_intent.putExtra(EXTRA_MESSAGE_EDIT, true);
                        create_intent.putExtra(EXTRA_MESSAGE_ITEM, itemList.get(finalI));
                        startActivity(create_intent);
                    }
                }
            });

            AppCompatImageButton deleteb = (AppCompatImageButton) layout.getViewById(R.id.deletebutton);
            deleteb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //sets up delete button with a prompt to alert user to confirm they wish to delete this item
                    if(view.getId() ==  deleteb.getId()){
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomepageActivity.this);
                        builder.setTitle("Delete Item");
                        builder.setMessage("Are you sure you want to delete this item?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        databaseHandler.deleteItem(itemList.get(finalI));
                                        UpdateUI();
                                    }
                                });
                        builder.setNegativeButton("No", null);
                        builder.setIcon(android.R.drawable.ic_dialog_alert);
                        builder.show();
                    }
                }
            });

            //after all UI elements are edited add it to the scrollview
            scrollView.addView(layout);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        Intent get_intent = getIntent();
        userid = get_intent.getIntExtra(RegisterActivity.EXTRA_MESSAGE, 0);
        Log.d("ID", Integer.toString(userid));
        create_item_button = (Button) findViewById(R.id.createitembutton);
        create_item_button.setOnClickListener(this);

        scrollView = (LinearLayout) findViewById(R.id.scrollviewLayout);

        UpdateUI();
    }

    @Override public void onClick(View view){
        int id = view.getId();

        if(id == R.id.createitembutton){
            Intent create_intent = new Intent(this, ItemEditor_activity.class);
            create_intent.putExtra(EXTRA_MESSAGE, userid);
            startActivity(create_intent);
        }
    }

    @Override
    protected void onRestart() {
        //this function is called when the homepage is returned to from any activity
        super.onRestart();
        UpdateUI(); //once a new item is created or edited the UI is updated to display this change on return
    }
}