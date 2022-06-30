package com.executor.googlesinginapp;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    SharedPreferences moSharedPreferences;

    public SharedPreferenceManager(Context foContext) {
        moSharedPreferences = android.preference.PreferenceManager
                .getDefaultSharedPreferences(foContext.getApplicationContext());
    }

    public String getAccountName() {
        if (moSharedPreferences != null) {
            return moSharedPreferences.getString("accountName", null);
        }
        return null;
    }
    public void setAccountName(String fsAccountName) {
        if (moSharedPreferences != null) {
            SharedPreferences.Editor loEditor = moSharedPreferences.edit();
            loEditor.putString("accountName", fsAccountName);
            loEditor.commit();
        }
    }

}
