package ir.Mikado;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SplashActivity extends Activity {

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] ni = cm.getAllNetworkInfo();
        for (int i = 0; i < ni.length; i++) {
            if (ni[i].getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        /****** Create Thread that will sleep for 5 seconds *************/
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 5 seconds
                    sleep(3 * 1000);

                    // After 5 seconds redirect to another intent
                    Intent i = new Intent(getBaseContext(), ShowAllAdvertisement.class);

                    Intent intent = new Intent(getBaseContext(), WelcomeActivity.class);
                    i.putExtra("categoryId", "aaa");
                    startActivity(intent);
                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };
        if (isConnected() == true) {

            background.start();

        } else {
            AlertMe(getString(R.string.InternetErrorTitle), getString(R.string.InternetErrorMessage), true);

        }
        super.onResume();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void AlertMe(String title, String body, boolean cancelable) {
        AlertDialog.Builder alert = new AlertDialog.Builder(SplashActivity.this);
        alert.setCancelable(cancelable);
        alert.setTitle(title);
        alert.setMessage(body);
        alert.setPositiveButton(R.string.BtnOpenNetWorkSetting,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent in = new Intent();
                        in.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                        startActivity(in);
                        Toast.makeText(SplashActivity.this, R.string.ToastSetMobileDataTurnOn, Toast.LENGTH_LONG).show();
                        onDestroy();
                    }
                }
        );
        alert.setNegativeButton(R.string.BtnTryToConnect,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(SplashActivity.this, R.string.ToastTryToConnect, Toast.LENGTH_SHORT).show();
                        onResume();
                    }
                });
        alert.setNeutralButton(R.string.BtnExit,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });

        alert.create();
        alert.show();
    }

}