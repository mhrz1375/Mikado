package ir.Mikado;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class LoginActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button BtnLogin;
    private Button BtnLinkToRegister;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private String User, Pass;
    private String UserLocal, PassLocal;
    private RequestQueue requestQueue;

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("ConstantConditions") NetworkInfo[] ni = cm.getAllNetworkInfo();
        for (int i = 0; i < ni.length; i++) {
            if (ni[i].getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }

        return false;
    }

    public void AlertMe(String title, String body, boolean cancelable) {
        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
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
                        Toast.makeText(LoginActivity.this, R.string.ToastSetMobileDataTurnOn, Toast.LENGTH_LONG).show();
                        onDestroy();
                    }
                }
        );
        alert.setNegativeButton(R.string.BtnTryToConnect,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(LoginActivity.this, R.string.ToastTryToConnect, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        if (isConnected()) {

        } else {
            AlertMe(getString(R.string.InternetErrorTitle), getString(R.string.InternetErrorMessage), true);

        }
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);


        requestQueue = Volley.newRequestQueue(this);

        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        BtnLogin = findViewById(R.id.BtnLogin);
        BtnLinkToRegister = findViewById(R.id.BtnLinkToRegisterScreen);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, ShowLogin.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        BtnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                UserLocal = inputEmail.getText().toString().trim();
                PassLocal = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!UserLocal.isEmpty() && !PassLocal.isEmpty()) {
                    // login user
                    checkLogin(UserLocal, PassLocal);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "ایمیل و رمز عبور خود را وارد نمایید.", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen
        BtnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });


/**User registered and now auto login*/
        try {
            Bundle UserAndPass;
            UserAndPass = getIntent().getExtras();
            //noinspection ConstantConditions
            User = UserAndPass.getString("user");
            Pass = UserAndPass.getString("pass");
            inputEmail.setText(User, TextView.BufferType.EDITABLE);
            inputPassword.setText(Pass, TextView.BufferType.EDITABLE);
        } catch (Exception e) {
        }
    }


    /**
     * function to verify login details in mysql db
     */
    public void checkLogin(final String Email, final String Password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("در حال بررسی ...");
        showDialog();


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String error = jObj.getString("error");

                    // Check for error node in json
                    if (!error.equals("1")) {
                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        String uid = jObj.getString("uid");


                        JSONObject user = jObj.getJSONObject("user");
                        int UserID = user.getInt("user_id");
                        String FirstName = URLDecoder.decode(user.getString("first_name"), "UTF-8");
                        String LastName = URLDecoder.decode(user.getString("last_name"), "UTF-8");
                        String PhoneNumber = user.getString("phone_number");
                        String Email = user.getString("email");
                        String PictureProfile = user.getString("picture_profile");
                        String Created_At = user.getString("created_at");

                        // Inserting row in users table
                        db.addUser(UserID, FirstName, LastName, Email, PhoneNumber, uid, PictureProfile, Created_At);


                        // Launch main activity
                        Intent intent = new Intent(getApplicationContext(),
                                ShowAllAdvertisement.class);
                        intent.putExtra("categoryId", "aaa");
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | UnsupportedEncodingException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", Email);
                params.put("password", Password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}