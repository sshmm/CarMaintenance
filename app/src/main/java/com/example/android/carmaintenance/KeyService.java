package com.example.android.carmaintenance;

import android.os.Parcel;
import android.os.Parcelable;

public class KeyService {

    String key;
    MaintenanceService maintenanceService;

    public KeyService(String key, MaintenanceService maintenanceService) {
        this.key = key;
        this.maintenanceService = maintenanceService;
    }

    public String getKey() {
        return key;
    }

    public MaintenanceService getMaintenanceService() {
        return maintenanceService;
    }
}
