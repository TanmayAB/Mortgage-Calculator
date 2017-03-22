package com.example.mortgagecalculator.Activities;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
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
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mortgagecalculator.Databases.HomeDatabaseHelper;
import com.example.mortgagecalculator.Datafields;
import com.example.mortgagecalculator.R;
import com.google.gson.Gson;


import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Member variables from Layout

    private MenuItem reset_action;
    private EditText mDownPayment;
    private EditText mPropertyAmount;
    private EditText mRate;
    private Spinner mTerms;
    private Button mCalculate;
    private TextView mAnswer;
    private LinearLayout mSaveForm;
    private TextView mSaveHome;
    private Button mSave;
    private Spinner mType;
    private EditText mAddress;
    private EditText mCity;
    private EditText mZip;
    private Spinner mState;
    private Datafields datafields;
    private ScrollView mScrollView;
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

        mScrollView = (ScrollView) findViewById(R.id.scrollview);
        mType = (Spinner) findViewById(R.id.spinner_propertytype);
        mAddress = (EditText) findViewById(R.id.editText_address);
        mCity = (EditText) findViewById(R.id.editText_city);
        mState = (Spinner) findViewById(R.id.spinner_states);
        mZip = (EditText) findViewById(R.id.editText_zipcode);

        mPropertyAmount = (EditText) findViewById(R.id.editText_propertyprice);
        mDownPayment = (EditText) findViewById(R.id.editText_downpayment);
        mRate = (EditText) findViewById(R.id.editText_rate);
        mTerms = (Spinner) findViewById(R.id.spinner_terms);

        mAnswer = (TextView) findViewById(R.id.textview_answer);

        mCalculate = (Button) findViewById(R.id.button_calculate);
        mSave = (Button) findViewById(R.id.button_save);

        if(getIntent().getStringExtra("home") != null) {
            String jsonString = getIntent().getStringExtra("home");
            Gson gson = new Gson();
            Datafields home = gson.fromJson(jsonString, Datafields.class);

            int position = 0;
            String[] temp_type =  getResources().getStringArray(R.array.propertytype);
            for(int i=0; i < temp_type.length; i++)
                if(temp_type[i].equals(home.getState()))
                    position = i;

            mType.setSelection(position);

            mAddress.setText(home.getAddress());
            mCity.setText(home.getCity());

            position = 0;
            String[] temp_states =  getResources().getStringArray(R.array.states);
            for(int i=0; i < temp_states.length; i++)
                if(temp_states[i].equals(home.getState()))
                    position = i;

            mState.setSelection(position);

            mZip.setText(home.getZip());
            mPropertyAmount.setText(home.getPropertyprice());
            mDownPayment.setText(home.getDownpayment());
            mRate.setText(home.getRate());

            position = 0;
            String[] temp_terms =  getResources().getStringArray(R.array.terms);
            for(int i=0; i < temp_terms.length; i++)
                if(temp_terms[i].equals(home.getTerms()))
                    position = i;

            mTerms.setSelection(position);

            mAnswer.setText(home.getMonthlyPayments());

        }

        datafields = new Datafields();
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
                mScrollView.scrollBy(0,1000);
            }
        };
        mSaveHome.setOnClickListener(listener1);

        // Listener for Saving Entries in Database ...

        View.OnClickListener listener2 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Initializing fields ...
                String[] fields = new String[10];
                fields[0] = mType.getSelectedItem().toString();
                fields[1] = mAddress.getText().toString();
                fields[2] = mCity.getText().toString();
                fields[3] = mState.getSelectedItem().toString();
                fields[4] = mZip.getText().toString();
                fields[5] = mPropertyAmount.getText().toString();
                fields[6] = mDownPayment.getText().toString();
                fields[7] = mRate.getText().toString();
                fields[8] = mTerms.getSelectedItem().toString();
                fields[9] = mAnswer.getText().toString();


                datafields.setFields(fields);

                String full_address = datafields.getFullAddress();


                // Verifying address ...

                List<Address> temp = null;
                Address location = null;

                ConnectivityManager connectivityManager
                        = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                    Geocoder address_checker = new Geocoder(getApplicationContext());
                    try {
                        temp = address_checker.getFromLocationName(full_address, 1);
                        if (temp == null || temp.isEmpty() || datafields.getFullAddress() == "") {
                            Context context = getApplicationContext();
                            CharSequence text = "Couldn't verify address.! Please correct Error in Address";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } else {
                            location = temp.get(0);
                            HomeDatabaseHelper mDbHelper = new HomeDatabaseHelper(getApplicationContext());

                            // Mapping columns to fields ...

                            try {
                                if(getIntent().getStringExtra("home") != null) {

                                    fields[0] = mType.getSelectedItem().toString();
                                    fields[1] = mAddress.getText().toString();
                                    fields[2] = mCity.getText().toString();
                                    fields[3] = mState.getSelectedItem().toString();
                                    fields[4] = mZip.getText().toString();
                                    fields[5] = mPropertyAmount.getText().toString();
                                    fields[6] = mDownPayment.getText().toString();
                                    fields[7] = mRate.getText().toString();
                                    fields[8] = mTerms.getSelectedItem().toString();
                                    fields[9] = mAnswer.getText().toString();

                                    mDbHelper.updateHome(fields,fields[1]);
                                    Context context = getApplicationContext();
                                    CharSequence text = "Address Updated Successfully.";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                                else {
                                    mDbHelper.insertHome(datafields);
                                    Context context = getApplicationContext();
                                    CharSequence text = "Address Successfully Saved.";
                                    int duration = Toast.LENGTH_SHORT;
                                    Toast toast = Toast.makeText(context, text, duration);
                                    toast.show();
                                }
                            }catch (Exception e)
                            {
                                Log.e("Exception ","e");
                                System.out.println("Exception " + e);
                            }
                        }
                    } catch (IOException ioe) {
                        Context context = getApplicationContext();
                        CharSequence text = "Couldn't verify address.! Please correct Error";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
                else
                {
                    Context context = getApplicationContext();
                    CharSequence text = "Couldn't verify Address.! Please connect to Internet";
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

            }
        };
        mSave.setOnClickListener(listener2);

        View.OnClickListener listener3 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSaveForm.setVisibility(View.VISIBLE);
                mScrollView.scrollBy(0,1000);
            }
        };
        mSaveHome.setOnClickListener(listener3);
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
        reset_action = menu.findItem(R.id.action_reset);
        reset_action.setTitle("Start New Calculation");
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

            mType.setSelection(0);
            mAddress.setText("");
            mCity.setText("");
            mState.setSelection(0);
            mZip.setText("");
            mPropertyAmount.setText("");
            mDownPayment.setText("");
            mRate.setText("");
            mTerms.setSelection(0);
            mAnswer.setText("");

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

