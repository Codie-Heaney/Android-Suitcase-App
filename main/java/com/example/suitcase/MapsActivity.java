package com.example.suitcase;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.suitcase.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener, View.OnClickListener{

    private GoogleMap mMap;
    private LatLng current_latlng;
    private ActivityMapsBinding binding;
    private Intent resultIntent;
    Button confirm_button;
    double[] preset_latlng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //get the intent value sent if this activity is in edit mode
        Intent get_intent = getIntent();
        preset_latlng = get_intent.getDoubleArrayExtra(ItemEditor_activity.MAP_LATLNG);

        resultIntent = new Intent();    //prepare intent to be received by they homepage
        confirm_button = (Button) findViewById(R.id.confirmlocationbutton);
        confirm_button.setOnClickListener(this);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);

        //if intent passed a value this map is in edit mode, if not it returns null
        if(preset_latlng != null){
            //set marker to where user placed it when the item was created/edited
            LatLng preset = new LatLng(preset_latlng[0], preset_latlng[1]);
            mMap.addMarker(new MarkerOptions().position(preset).title("Geotagged location!"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(preset));
            current_latlng = preset;    //if unchanged on confirm it keeps the preset value
        }
    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        mMap.clear();

        //create marker where user tapped and set current_latlng to where the user tapped
        MarkerOptions marker = new MarkerOptions()
                .position(latLng);
        mMap.addMarker(marker);
        current_latlng = latLng;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.confirmlocationbutton){
            //prepare result and end activity
            resultIntent.putExtra(ItemEditor_activity.MAP_RESULT, current_latlng);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }
}