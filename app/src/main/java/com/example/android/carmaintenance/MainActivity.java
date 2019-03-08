package com.example.android.carmaintenance;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements ServiceFragment.OnListFragmentInteractionListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */

    public static final int RC_SIGN_IN = 1;
    public static final String DISTANCE_KEY = "distance";

    private ViewPager mViewPager;

    private String mUserUid;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final EditText distanceField = findViewById(R.id.et_distance);
        distanceField.setText(String.valueOf(loadPreferences()));
        distanceField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0){
                    distanceField.setText(String.valueOf(loadPreferences()));

                }else{
                    if (Integer.parseInt(s.toString()) != loadPreferences()){
                        savePreferences(Integer.valueOf(s.toString()));

                        Intent intentBroad = new Intent(getApplicationContext(), NewAppWidget.class);
                        intentBroad.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
                        AppWidgetManager widgetManager = AppWidgetManager.getInstance(getApplicationContext());
                        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), NewAppWidget.class));
                        widgetManager.notifyAppWidgetViewDataChanged(ids, android.R.id.list);

                        intentBroad.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                        intentBroad.putExtra("distance",loadPreferences());
                        intentBroad.putExtra("username",mUserUid);
                        mViewPager.setAdapter(mSectionsPagerAdapter);

                    }

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog alertDialog;
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                View view1 = getLayoutInflater().inflate(R.layout.update_service_layout,null);
                final EditText serviceText = view1.findViewById(R.id.et_service_name);
                final EditText priceText = view1.findViewById(R.id.et_price);
                final EditText periodText = view1.findViewById(R.id.et_periodicity);
                final EditText lastText = view1.findViewById(R.id.et_last_time);
                final Button deleteButton = view1.findViewById(R.id.bn_delete);
                final Button discardButton = view1.findViewById(R.id.bn_discard);

                deleteButton.setEnabled(false);
                Button updateButton = view1.findViewById(R.id.bn_save);

                dialogBuilder.setView(view1);
                alertDialog = dialogBuilder.create();

                discardButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                updateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String serviceName = serviceText.getText().toString();
                        String servicePrice = priceText.getText().toString();
                        String periodDistance = periodText.getText().toString();
                        String lastService = lastText.getText().toString();
                        if (!serviceName.isEmpty() || !periodDistance.isEmpty() || !lastService.isEmpty()|| !servicePrice.isEmpty() ){

                            MaintenanceService maintenanceService = new MaintenanceService(serviceName,  Integer.parseInt(periodDistance), Integer.parseInt(lastService)
                                    ,true,  Double.parseDouble(servicePrice));

                            databaseReference.push().setValue(maintenanceService);
                            alertDialog.dismiss();

                        } else {
                            Toast.makeText(MainActivity.this,com.example.android.carmaintenance.R.string.empty_service,Toast.LENGTH_LONG).show();
                        }
                    }
                });

                alertDialog.show();

            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                /*Check if there is a logged in user or not*/

                if (firebaseUser != null){
                    onSignedInActions(firebaseUser.getUid());
                } else {
                    onSignedOutActions();
                    startActivityForResult(

                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == RC_SIGN_IN) {
                if (resultCode == RESULT_OK) {
                    // Sign-in succeeded
                    Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) {
                    // Sign in was canceled by the user, finish the activity
                    Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id) {
            case R.id.sign_out:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public void onListFragmentInteraction(final KeyService keyService) {

        final AlertDialog alertDialog;
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        View view1 = getLayoutInflater().inflate(R.layout.update_service_layout,null);
        final EditText serviceText = view1.findViewById(R.id.et_service_name);
        final EditText priceText = view1.findViewById(R.id.et_price);
        final EditText periodText = view1.findViewById(R.id.et_periodicity);
        final EditText lastText = view1.findViewById(R.id.et_last_time);
        Button updateButton = view1.findViewById(R.id.bn_save);
        final Button deleteButton = view1.findViewById(R.id.bn_delete);
        final Button discardButton = view1.findViewById(R.id.bn_discard);

        serviceText.setText(keyService.getMaintenanceService().getServiceName());
        priceText.setText(Double.toString(keyService.getMaintenanceService().getPrice()));
        periodText.setText(Integer.toString(keyService.getMaintenanceService().getPeriodicity()));
        lastText.setText(Integer.toString(keyService.getMaintenanceService().getLastTime()));

        dialogBuilder.setView(view1);
        alertDialog = dialogBuilder.create();

        discardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child(keyService.getKey()).removeValue();
                alertDialog.dismiss();
            }
        });
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String serviceName = serviceText.getText().toString();
                String servicePrice = priceText.getText().toString();
                String periodDistance = periodText.getText().toString();
                String lastService = lastText.getText().toString();
                if (!serviceName.isEmpty() || !periodDistance.isEmpty() || !lastService.isEmpty()|| !servicePrice.isEmpty() ){

                    MaintenanceService maintenanceService = new MaintenanceService(serviceName,  Integer.parseInt(periodDistance), Integer.parseInt(lastService)
                            ,true,  Double.parseDouble(servicePrice));

                    databaseReference.child(keyService.getKey()).setValue(maintenanceService);
                    alertDialog.dismiss();

                } else {
                    Toast.makeText(MainActivity.this,com.example.android.carmaintenance.R.string.empty_service,Toast.LENGTH_LONG).show();
                }
            }
        });

        alertDialog.show();

    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            if (position == 1){

                return ServiceFragment.newInstance(mUserUid);

            } else if (position == 2){
                return ArchiveFragment.newInstance(mUserUid);
            } else{
                return PlaceholderFragment.newInstance(position + 1);

            }
        }


        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
        detachDatabaseReadListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void onSignedOutActions(){
        detachDatabaseReadListener();
        Intent intentBroad = new Intent(getApplicationContext(), NewAppWidget.class);
        intentBroad.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(this);
        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(this, NewAppWidget.class));
        widgetManager.notifyAppWidgetViewDataChanged(ids, android.R.id.list);

        intentBroad.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intentBroad.putExtra("distance",loadPreferences());
        intentBroad.putExtra("username","");
        sendBroadcast(intentBroad);
    }

    private void onSignedInActions(String userUid){
        mUserUid = userUid;
        databaseReference = firebaseDatabase.getReference().child(userUid).child("services");
        Log.e(mUserUid,userUid);
        Intent intentBroad = new Intent(getApplicationContext(), NewAppWidget.class);
        intentBroad.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] ids = widgetManager.getAppWidgetIds(new ComponentName(getApplicationContext(), NewAppWidget.class));
        widgetManager.notifyAppWidgetViewDataChanged(ids, android.R.id.list);

        intentBroad.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intentBroad.putExtra("distance",loadPreferences());
        intentBroad.putExtra("username",userUid);

        sendBroadcast(intentBroad);

    }

    private void detachDatabaseReadListener(){
        if (childEventListener != null) {
            databaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }


    private void savePreferences( int value) {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(DISTANCE_KEY, value);
        editor.apply();
    }

    private int loadPreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        return sharedPreferences.getInt(DISTANCE_KEY, 0);

    }
}
