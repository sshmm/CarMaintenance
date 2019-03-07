package com.example.android.carmaintenance;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.carmaintenance.dummy.DummyContent;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity  {

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

    private ViewPager mViewPager;

    private String mUserUid;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    //TODO private OnFragmentInteractionListner listner;

    private ArrayList<MaintenanceService> maintenanceServices = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


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
                Button updateButton = view1.findViewById(R.id.bn_save);

                dialogBuilder.setView(view1);
                alertDialog = dialogBuilder.create();

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
                    //TODO change toast
                    // Sign-in succeeded
                    Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) {
                    // Sign in was canceled by the user, finish the activity
                    Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            Log.e("ssssssssssssssssss",maintenanceServices.get(0).getServiceName());

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
            case R.id.action_settings:
                return true;
            case R.id.sign_out:
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
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
            if (position != 0){

                return ServiceFragment.newInstance(mUserUid);

            }else {
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

        //TODO Detach any listner here
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void onSignedOutActions(){
        //TODO if signed out actions
        detachDatabaseReadListener();
    }

    private void onSignedInActions(String userUid){
        mUserUid = userUid;
        //TODO on sign in actions
        databaseReference = firebaseDatabase.getReference().child(userUid).child("services");
      //TODO  attachDatabaseReadListener();

    }


/*TODO
    private void attachDatabaseReadListener(){
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    MaintenanceService maintenanceService = dataSnapshot.getValue(MaintenanceService.class);
                    Log.e("ddddddddddd",maintenanceService.getServiceName());
                    maintenanceServices.add(maintenanceService);
                    Log.e("fffffffffff",maintenanceServices.get((maintenanceServices.size())-1).getServiceName());

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            databaseReference.addChildEventListener(childEventListener);


        }
    }*/

    private void detachDatabaseReadListener(){
        if (childEventListener != null) {
            databaseReference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }
/*TODO
    public interface OnFragmentInteractionListner{
        void onDataReceived(ArrayList<MaintenanceService> maintenanceServices);
    }

    public void setOnFragmentInteractionListner(OnFragmentInteractionListner listner){
        this.listner = listner;
    }*/
}
