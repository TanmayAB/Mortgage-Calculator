package com.example.mortgagecalculator.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.mortgagecalculator.Databases.HomeDatabase;
import com.example.mortgagecalculator.Databases.HomeDatabaseHelper;
import com.example.mortgagecalculator.MainActivity;
import com.example.mortgagecalculator.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Student on 3/16/17.
 */

public class ShowInMap extends AppCompatActivity implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener {

    private List<LatLng> homeList = new ArrayList<LatLng>();
    private List<String> snippets = new ArrayList<String>();
    private List<String> titles = new ArrayList<String>();

    private List<Address> temp = null;
    private Address location = null;
    private LatLng p1 = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showinmap);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Navigation Drawer Code ...

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Geocoder for validating address ...

        Geocoder geoCoder = new Geocoder(getApplicationContext());

        // Getting Readable Database ...

        HomeDatabaseHelper mDbHelper = new HomeDatabaseHelper(getApplicationContext());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db = mDbHelper.getReadableDatabase();


        // Creating a cursor and reading all the rows from Database ...

        Cursor c = db.rawQuery("SELECT * FROM "+ HomeDatabase.TABLE_NAME, null);

        if(c.moveToFirst()){
            do {

                // Assigning row fields to variables ...

                String column1 = c.getString(0);
                String type = c.getString(1);
                String address = c.getString(2);
                String city = c.getString(3);
                String state = c.getString(4);
                String zip = c.getString(5);
                String price = c.getString(6);
                String downpayment = c.getString(7);
                String rate = c.getString(8);
                String terms = c.getString(9);
                String monthly_payment = c.getString(10);

                Log.e("record + ", type);
                Log.e("record + ", address);
                Log.e("record + ", city);
                Log.e("record + ", state);
                Log.e("record + ", zip);
                Log.e("record + ", downpayment);
                Log.e("record + ", rate);
                Log.e("record + ", terms);
                Log.e("record + ", monthly_payment);


                // Generating full address ...

                String full_address = address + "," + city + "," + state + "," + zip;
                Log.e("record ", address);

                try {

                    // Verifying the address ...
                    temp = geoCoder.getFromLocationName(full_address, 1);
                    if (temp == null) {
                        Context context = getApplicationContext();
                        CharSequence text = "Couldn't locate a Home";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        db.close();
                    } else {

                        // Getting Longitude and Latitude ...

                        System.out.println("temp : " + temp.get(0).toString());
                        location = temp.get(0);
                        p1 = new LatLng(location.getLatitude(), location.getLongitude());
                        homeList.add(p1);
                        titles.add(type);
                        snippets.add("Property Prize : " + price + "\nMonthly Payment : " + monthly_payment);
                    }
                } catch (Exception ee) {
                    Context context = getApplicationContext();
                    CharSequence text = "Couldn't locate a Home..!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                    db.close();
                }
            }while(c.moveToNext());
        }
        c.close();
    }
    @Override
    public void onMapReady(GoogleMap map) {
        if(homeList.size() == 0)
        {

            // If not found than displaying US ...
            Geocoder america = new Geocoder(getApplicationContext());
            try {
                temp = america.getFromLocationName("California, United States of America", 1);
                location = temp.get(0);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 5.0f));
            }catch (IOException e){
                Log.e("Exception","0 result found");
            }

            Context context = getApplicationContext();
            CharSequence text = "No Saved Homes..!!";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        else {

            // Displaying Homes as Markers ...

            System.out.println("Long lat : " + homeList.get(0).toString());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 10.0f));
            for (int i = 0; i < homeList.size(); i++) {

                map.addMarker(new MarkerOptions()
                        .position(homeList.get(i))
                        .title(titles.get(i)))
                        .setSnippet(snippets.get(i));
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

            // Handle the camera action
        if (id == R.id.calculate_mortgage) {
            startActivity(new Intent(ShowInMap.this,MainActivity.class));
        } else if (id == R.id.showhomes_inmap) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
