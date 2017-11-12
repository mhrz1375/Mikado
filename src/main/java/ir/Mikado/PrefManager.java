package ir.Mikado;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 *@author mhrz  Created by MHRZ on 2016/12/20.
 */
class PrefManager {
    // Shared preferences file name
    private static final String PREF_NAME = "mikado-welcome";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    PrefManager(Context context) {
        int PRIVATE_MODE = 0;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    void setFirstTimeLaunch() {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, false);
        editor.commit();
    }


}
