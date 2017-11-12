package ir.Mikado;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = RegisterActivity.class.getSimpleName();

    private Button btnRegister;
    private Button btnLinkToLogin;
    private Button BtnOverrideRegisterUser;
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputPhoneNumber;
    private EditText inputEmail;
    private EditText inputPassword;
    private EditText inputPasswordAgain;
    private TextView TextViewNameProfile;

    private String AppBarTitle = "ثبت حساب کاربری";
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private String ImagePath = "";
    private ImageView ImageViewPictureProfile;
    private Bitmap BitMapImage;
    private String FirstName;
    private String LastName;
    private String PhoneNumber;
    private String Email;
    private String Password;
    private String PictureProfile;
    private boolean flagCheck;

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

    public void AlertMe(String title, String body, boolean cancelable) {
        AlertDialog.Builder alert = new AlertDialog.Builder(RegisterActivity.this);
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
                        Toast.makeText(RegisterActivity.this, R.string.ToastSetMobileDataTurnOn, Toast.LENGTH_LONG).show();
                        onDestroy();
                    }
                }
        );
        alert.setNegativeButton(R.string.BtnTryToConnect,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(RegisterActivity.this, R.string.ToastTryToConnect, Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.register_scrolling);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setTitle(AppBarTitle);
        setSupportActionBar(toolbar);
        try {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_forward_white_24dp));

        } catch (Exception e) {
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /** * Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                 .setAction("Action", null).show();*/
                flagCheck = false;
                Intent i = new Intent(getApplicationContext(),
                        ImageCropper.class);
                i.putExtra("flag1", flagCheck);
                startActivity(i);
                finish();

            }
        });
        inputFirstName = findViewById(R.id.EditTextFirstName);
        inputLastName = findViewById(R.id.EditTextLastName);
        inputEmail = findViewById(R.id.EditTextEmail);
        inputPhoneNumber = findViewById(R.id.EditTextPhoneNumber);
        inputPassword = findViewById(R.id.EditTextPassword);
        inputPasswordAgain = findViewById(R.id.EditTextPasswordAgain);
        btnRegister = findViewById(R.id.BtnRegister);
        btnLinkToLogin = findViewById(R.id.BtnLinkToLogin);
        BtnOverrideRegisterUser = findViewById(R.id.BtnOverrideRegisterUser);
        ImageViewPictureProfile = findViewById(R.id.ImageViewPictureProfile);
        TextViewNameProfile = findViewById(R.id.TextViewNameProfile);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(RegisterActivity.this,
                    ShowAllAdvertisement.class);
            startActivity(intent);
            finish();
        }
        // Register Button Click event


        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
        ///override to sending Register

        BtnOverrideRegisterUser.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                AlertMeOverrideRegister(getString(R.string.TitleOverrideRegister), getString(R.string.MessageOverrideRegister), true);

            }
        });

        /**Set Profile picture From image cropper to this ImageViewProfilePicture*/
        try {
            if (ImagePath != null) {
                Bundle ProfilePicture;
                ProfilePicture = getIntent().getExtras();
                ImagePath = ProfilePicture.getString("imagePath");
                OpenImageFromFilePath(ImagePath);

            }
        } catch (Exception e) {

        }

    }

    public void BtnRegister(View view) throws UnsupportedEncodingException {
        if (inputFirstName.getText().length() > 1) {
            if (inputLastName.getText().length() > 1) {
                TextViewNameProfile.setText(inputFirstName.getText().toString() + " " + inputLastName.getText().toString(), TextView.BufferType.EDITABLE);
                if (inputPhoneNumber.getText().length() > 1) {
                    if (inputEmail.getText().length() > 1) {
                        if (inputPassword.getText().length() > 1) {
                            if (inputPasswordAgain.getText().length() > 1) {
                                if (inputPasswordAgain.getText().toString().equals(inputPassword.getText().toString())) {
                                    FirstName = inputFirstName.getText().toString().trim();
                                    LastName = inputLastName.getText().toString().trim();
                                    PhoneNumber = inputPhoneNumber.getText().toString().trim();
                                    Email = inputEmail.getText().toString().trim();
                                    Password = inputPassword.getText().toString().trim();

                                    FirstName = URLEncoder.encode(FirstName + " ", "UTF-8");
                                    LastName = URLEncoder.encode(LastName + " ", "UTF-8");

                                    try {
                                        PictureProfile = bitmapToBase64(BitMapImage).trim();
                                    } catch (Exception e) {
                                        PictureProfile = "";
                                    }
                                    if (!FirstName.isEmpty() && !LastName.isEmpty() && !PhoneNumber.isEmpty() && !Email.isEmpty() && !Password.isEmpty()) {
                                        registerUser(FirstName, LastName, PhoneNumber, PictureProfile, Email, Password);
                                    }
                                } else {
                                    Toast.makeText(RegisterActivity.this,
                                            "رمز عبور همخوانی ندارد!",
                                            Toast.LENGTH_SHORT).show();

                                    Drawable dr = getResources().getDrawable(R.drawable.ic_error_red_a700_24dp);
                                    //add an error icon to yur drawable files
                                    dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                                    inputPassword.setCompoundDrawables(dr, null, null, null);
                                    inputPasswordAgain.setCompoundDrawables(dr, null, null, null);
                                }
                            } else {
                                Toast.makeText(RegisterActivity.this,
                                        "رمز خود را مجددا وارد نمایید!",
                                        Toast.LENGTH_SHORT).show();
                                Drawable dr = getResources().getDrawable(R.drawable.ic_error_red_a700_24dp);
                                //add an error icon to yur drawable files
                                dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                                inputPasswordAgain.setCompoundDrawables(dr, null, null, null);

                            }
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "حداقل 8 کاراکتر وارد کنید!",
                                    Toast.LENGTH_SHORT).show();
                            Drawable dr = getResources().getDrawable(R.drawable.ic_error_red_a700_24dp);
                            //add an error icon to yur drawable files
                            dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());

                            inputPassword.setCompoundDrawables(dr, null, null, null);
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this,
                                "لطفاایمیل خود را بدرستی وارد نمایید!",
                                Toast.LENGTH_SHORT).show();
                        Drawable dr = getResources().getDrawable(R.drawable.ic_error_red_a700_24dp);
                        //add an error icon to yur drawable files
                        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());

                        inputEmail.setCompoundDrawables(dr, null, null, null);
                    }
                } else {
                    Toast.makeText(RegisterActivity.this,
                            "اطلاعات خود را واردنمایید!",
                            Toast.LENGTH_SHORT).show();
                    Drawable dr = getResources().getDrawable(R.drawable.ic_error_red_a700_24dp);
                    //add an error icon to yur drawable files
                    dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());

                    inputPhoneNumber.setCompoundDrawables(dr, null, null, null);
                }
            } else {
                Toast.makeText(RegisterActivity.this,
                        "اطلاعات خود را واردنمایید!",
                        Toast.LENGTH_SHORT).show();
                Drawable dr = getResources().getDrawable(R.drawable.ic_error_red_a700_24dp);
                //add an error icon to yur drawable files
                dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());

                inputLastName.setCompoundDrawables(dr, null, null, null);
            }
        } else {
            Toast.makeText(RegisterActivity.this,
                    "اطلاعات خود را واردنمایید!",
                    Toast.LENGTH_SHORT).show();
            Drawable dr = getResources().getDrawable(R.drawable.ic_error_red_a700_24dp);
            //add an error icon to yur drawable files
            dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());

            inputFirstName.setCompoundDrawables(dr, null, null, null);
        }
    }

    @Nullable
    private String OpenImageFromFilePath(String FilePath) {
        File ImageFile = new File(FilePath);
        if (ImageFile.exists()) {
            BitMapImage = BitmapFactory.decodeFile(ImageFile.getAbsolutePath());

            ImageViewPictureProfile.setImageBitmap(BitMapImage);
        }

        return null;
    }

    /**
     * Convert Bitmap image to Base64 string
     */
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String stringBitmapToBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return stringBitmapToBase64;

    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     */
    private void registerUser(final String FirstName, final String LastName, final String PhoneNumber, final String PictureProfile, final String Email,
                              final String Password) {


        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("در حال بررسی ...");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                AppConfig.URL_REGISTER, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                String res = response.toString().trim();

                hideDialog();


                if (res.equals("\"done\"")) {

                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("user", inputEmail.getText().toString());
                    intent.putExtra("pass", inputPassword.getText().toString());
                    startActivity(intent);
                    finish();

                }
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
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
                    }

                    /** Error occurred in registration. Get the error message  */

                    else if (res.equals("\"failure_post\"")) {
                        Toast.makeText(RegisterActivity.this, R.string.FailureConnecting, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    if (res.equals("\"user_already_existed\"")) {
                        Toast.makeText(RegisterActivity.this, R.string.UserAlreadyExisted, Toast.LENGTH_LONG).show();
                    } else if (res.equals("\"failure_creating_image\"")) {
                        Toast.makeText(RegisterActivity.this, R.string.FailureCreatingImage, Toast.LENGTH_LONG).show();
                    } else if (res.equals("\"failure_connecting\"")) {
                        Toast.makeText(RegisterActivity.this, R.string.FailureConnecting, Toast.LENGTH_LONG).show();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), "ثبت نام انجام نشد.", Toast.LENGTH_LONG).show();

                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("first_name", FirstName);
                params.put("last_name", LastName);
                params.put("email", Email);
                params.put("phone_number", PhoneNumber);
                params.put("picture_profile", PictureProfile);
                params.put("password", Password);
                return params;
            }
        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


/// Alert method for overriding Register or not

    public void AlertMeOverrideRegister(String title, String body, boolean cancelable) {
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(RegisterActivity.this);
        alert.setCancelable(cancelable);
        alert.setTitle(title);
        alert.setMessage(body);
        alert.setPositiveButton(R.string.BtnNoOverrideAds,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                }
        );
        alert.setNeutralButton(R.string.BtnYesOverrideAds,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alert.create();
        alert.show();
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