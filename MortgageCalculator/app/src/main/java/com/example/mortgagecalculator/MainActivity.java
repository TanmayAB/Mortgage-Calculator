package com.example.mortgagecalculator;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mortgagecalculator.Activities.ShowInMap;
import com.example.mortgagecalculator.Databases.HomeDatabase;
import com.example.mortgagecalculator.Databases.HomeDatabaseHelper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Member variables from Layout

    private EditText mDownPayment;
    private EditText mPropertyAmount;
    private EditText mRate;
    private Spinner mTerms;
    private Button mCalculate;
    private EditText mAnswer;
    private LinearLayout mSaveForm;
    private TextView mSaveHome;
    private Button mSave;
    private Spinner mType;
    private EditText mAddress;
    private EditText mCity;
    private EditText mZip;
    private Spinner mState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Navigation Drawer code ...

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initializing all the member variables ...

        mType = (Spinner) findViewById(R.id.spinner_propertytype);
        mAddress = (EditText) findViewById(R.id.editText_address);
        mCity = (EditText) findViewById(R.id.editText_city);
        mState = (Spinner) findViewById(R.id.spinner_states);
        mZip = (EditText) findViewById(R.id.editText_zipcode);

        mPropertyAmount = (EditText) findViewById(R.id.editText_propertyprice);
        mDownPayment = (EditText) findViewById(R.id.editText_downpayment);
        mRate = (EditText) findViewById(R.id.editText_rate);
        mTerms = (Spinner) findViewById(R.id.spinner_terms);
        mAnswer = (EditText) findViewById(R.id.editText_answer);

        mCalculate = (Button) findViewById(R.id.button_calculate);
        mSave = (Button) findViewById(R.id.button_save);

        // Listener for Mortgage Calculation ...

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Variable declaration...

                String propertyprice = mPropertyAmount.getText().toString();
                String d = mDownPayment.getText().toString();
                String r = mRate.getText().toString();
                String t = mTerms.getSelectedItem().toString();

                float property_price;
                float downPayment;
                float rate;
                int terms;


                //validation of Input...

                if(propertyprice.isEmpty() || propertyprice == "")
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Property Price can't be empty";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                else
                {   property_price = Float.parseFloat(propertyprice);

                    if(d.isEmpty() || d=="")
                        downPayment = 0;
                    else
                        downPayment = Float.parseFloat(mDownPayment.getText().toString());
                    if(r.isEmpty() || r=="")
                    {
                        rate = 0;
                        Context context = getApplicationContext();
                        CharSequence text = "Rate ZERO ? Great..!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                    else
                        rate = Float.parseFloat(r);

                    // Mortgage Calculation

                    terms = Integer.parseInt(t);
                    float principal = property_price - downPayment;
                    rate = rate / 1200;
                    terms = terms * 12;
                    int installment = (int) ((rate * principal) / (1 - Math.pow(1 + rate, terms * -1)));
                    mAnswer.setText(installment + "");
                }
            }
        };

        mSaveForm = (LinearLayout) findViewById(R.id.linearlayout_saveform);
        mSaveHome = (TextView) findViewById(R.id.textview_savehome);
        mCalculate.setOnClickListener(listener);

        // Listener for Saving Home form ...

        View.OnClickListener listener1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSaveForm.setVisibility(View.VISIBLE);

            }
        };
        mSaveHome.setOnClickListener(listener1);

        // Listener for Saving Entries in Database ...

        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initializing fields ...

                String type = mType.getSelectedItem().toString();
                String address = mAddress.getText().toString();
                String city = mCity.getText().toString();
                String state = mState.getSelectedItem().toString();
                String  zip = mZip.getText().toString();
                String  propertyprice= mPropertyAmount.getText().toString();
                String  downpayment= mDownPayment.getText().toString();
                String  rate= mRate.getText().toString();
                String  terms= mTerms.getSelectedItem().toString();
                String  monthlyPayments= mAnswer.getText().toString();

                String full_address = address + "," + city + "," + state + "," + zip;


                // Verifying address ...

                List<Address> temp = null;
                Address location = null;

                Geocoder address_checker = new Geocoder(getApplicationContext());
                try {
                    temp = address_checker.getFromLocationName(full_address, 1);
                    if(temp == null || temp.isEmpty())
                    {
                        Context context = getApplicationContext();
                        CharSequence text = "Couldn't verify address.! Please correct Error";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                    else {
                        location = temp.get(0);
                        HomeDatabaseHelper mDbHelper = new HomeDatabaseHelper(getApplicationContext());
                        SQLiteDatabase db = mDbHelper.getWritableDatabase();

                        mDbHelper.onCreate(db);

                        // Mapping columns to fields ...

                        ContentValues values = new ContentValues();
                        values.put(HomeDatabase.COLUMN1_NAME_TITLE, type);
                        values.put(HomeDatabase.COLUMN2_NAME_TITLE, address);
                        values.put(HomeDatabase.COLUMN3_NAME_TITLE, city);
                        values.put(HomeDatabase.COLUMN4_NAME_TITLE, state);
                        values.put(HomeDatabase.COLUMN5_NAME_TITLE, zip);
                        values.put(HomeDatabase.COLUMN6_NAME_TITLE, propertyprice);
                        values.put(HomeDatabase.COLUMN7_NAME_TITLE, downpayment);
                        values.put(HomeDatabase.COLUMN8_NAME_TITLE, rate);
                        values.put(HomeDatabase.COLUMN9_NAME_TITLE, terms);
                        values.put(HomeDatabase.COLUMN10_NAME_TITLE, monthlyPayments);

                        // Insert the new row ...

                        long newRowId = db.insert(HomeDatabase.TABLE_NAME, null, values);

                        Context context = getApplicationContext();
                        CharSequence text = "Address Successfully Saved.";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                        db.close();
                    }
                }catch (IOException ioe)
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Couldn't verify address.! Please correct Error";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

            }
        };
        mSave.setOnClickListener(listener2);

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {

            HomeDatabaseHelper mDbHelper = new HomeDatabaseHelper(getApplicationContext());
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            mDbHelper.truncateTable(db);
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.calculate_mortgage) {

        } else if (id == R.id.showhomes_inmap) {
            startActivity(new Intent(MainActivity.this, ShowInMap.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

