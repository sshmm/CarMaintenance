package com.example.android.carmaintenance;

import com.google.firebase.database.FirebaseDatabase;

public class FireBaseStart extends android.app.Application  {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
