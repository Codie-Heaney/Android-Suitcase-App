package com.example.suitcase;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import java.util.regex.Matcher;

import com.google.android.gms.maps.model.LatLng;

public class Item implements Parcelable {

    private int id;
    private String name;
    private String description;
    private float price;
    private boolean purchased;
    private int image_id;
    private double lat;
    private double lng;

    public Item(int id, String name, String description, float price, boolean purchased, double lat, double lng){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.purchased = purchased;
        this.lat = lat;
        this.lng = lng;
    }
    public Item(String name, String description, float price, boolean purchased, double lat, double lng){
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.purchased = purchased;
        this.lat = lat;
        this.lng = lng;
    }
    public Item(Item item, boolean purchased){
        this.id = item.getID();
        this.name = item.getName();
        this.description = item.getDescription();
        this.price = item.getPrice();
        this.lat = item.lat;
        this.lng = item.lng;
        this.purchased = purchased;
    }

    //required to pass this object as an intent
    protected Item(Parcel in) {
        id = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readFloat();
        purchased = in.readByte() != 0;
        image_id = in.readInt();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    //required to pass this object as an intent
    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public int getID(){ return id; }
    public String getName(){ return name; }
    public String getDescription(){ return description; }
    public float getPrice(){ return price; }
    public boolean getPurchased(){ return purchased; }
    public Double getLat(){ return lat; }
    public Double getLng(){ return lng; }

    //required to pass this object as an intent
    @Override
    public int describeContents() {
        return 0;
    }
    //required to pass this object as an intent
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeFloat(price);
        dest.writeByte((byte) (purchased ? 1 : 0));
        dest.writeInt(image_id);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    //Return string with information about this item
    @Override public String toString(){
        String purchasedYN;
        if(purchased){
            purchasedYN = "Yes";
        }else{
            purchasedYN = "No";
        }
        return " Name: " + name + " Description: " + description + " Price: " + Float.toString(price) + " Purchased: " + purchasedYN;
    }
}
