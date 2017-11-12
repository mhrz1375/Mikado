package ir.Mikado;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class InsertAdvertisement extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    public ProgressDialog pDialog;
    private String ImagePath = "";
    private ImageButton ImageButtonInsertImage;
    private Bitmap BitMapImage;
    private boolean flagCheck = false;
    private EditText EditTextInsertTitle;
    private EditText EditTextInsertAddress;
    private EditText EditTextInsertEmail;
    private EditText EditTextInsertPhoneNumber;
    private EditText EditTextInsertDescription;
    private EditText EditTextInsertSeller;
    private TextView TextViewSelectedCategory;
    private String UserID;
    private String FirstName;
    private String LastName;
    private String Seller;
    private String CategoryID;
    private String CategoryName;
    private String Title;
    private String Address;
    private String PhoneNumber;
    private String Description;
    private String Email;
    private String ImageString;
    private SQLiteHandler db;
    private Button BtnGoToCategoryList;
    private String categoryID;
    private SharedPreferences prefs;
    private String categoryName;
    private Bundle CategoryIdIntent;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.insert_advertisement_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_forward_white_24dp));

        } catch (Exception ignored) {
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });

        BtnGoToCategoryList = findViewById(R.id.BtnGoToCategoryList);
        ImageButtonInsertImage = findViewById(R.id.ImageButtonInsertImage);
        EditTextInsertTitle = findViewById(R.id.EditTextInsertTitle);
        EditTextInsertAddress = findViewById(R.id.EditTextInsertAddress);
        EditTextInsertEmail = findViewById(R.id.EditTextInsertEmail);
        EditTextInsertPhoneNumber = findViewById(R.id.EditTextInsertPhoneNumber);
        EditTextInsertSeller = findViewById(R.id.EditTextInsertSeller);
        EditTextInsertDescription = findViewById(R.id.EditTextInsertDescription);

        TextViewSelectedCategory = findViewById(R.id.TextViewSelectedCategory);

        CategoryIdIntent = getIntent().getExtras();

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from sqLite
        HashMap<String, String> user = db.getUserDetails();
        UserID = user.get("user_id").toString();
        FirstName = user.get("first_name");
        LastName = user.get("last_name");
        PhoneNumber = user.get("phone_number");
        Email = user.get("email");

        EditTextInsertSeller.setText(FirstName + " " + LastName, TextView.BufferType.EDITABLE);
        EditTextInsertEmail.setText(Email, TextView.BufferType.EDITABLE);
        EditTextInsertPhoneNumber.setText(PhoneNumber, TextView.BufferType.EDITABLE);

        /**Set Profile picture From image cropper to this ImageViewProfilePicture*/
        try {
            if (ImagePath != null) {
                Bundle ProfilePicture;
                ProfilePicture = getIntent().getExtras();
                ImagePath = ProfilePicture.getString("imagePath");
                OpenImageFromFilePath(ImagePath);

            }
        } catch (Exception ignored) {
        }


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        //Checked internet Connection


        super.onResume();


        ///prefs value for save selected id from category list
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        CategoryID = categoryID = prefs.getString("CategoryId", "noid"); //no id: default value
        CategoryName = categoryName = prefs.getString("CategoryName", null); //no id: default value
        TextViewSelectedCategory.setText(CategoryName);
        super.onResume();
    }

    ///open picture from file path and converted to bitMap image
    @Nullable
    private String OpenImageFromFilePath(String FilePath) {
        File ImageFile = new File(FilePath);
        if (ImageFile.exists()) {
            BitMapImage = BitmapFactory.decodeFile(ImageFile.getAbsolutePath());
            //set bitMap to image button
            ImageButtonInsertImage.setImageBitmap(BitMapImage);
            //encoding bit map image to base64 string
            ImageString = bitmapToBase64(BitMapImage);
        }
        return null;
    }

    ///set dialog message for delay of sending data
    protected void onPreExecute() {
        pDialog = new ProgressDialog(InsertAdvertisement.this);
        pDialog.setMessage(getString(R.string.PDialogMessageInsertData));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
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

    ///open image from image cropper
    public void OpenImage(View view) {
        Intent i = new Intent(getApplicationContext(),
                ImageCropper.class);
        flagCheck = true;
        i.putExtra("flag", flagCheck);
        startActivity(i);
    }

    ///start category list as dialog box
    public void SelectCategory(View view) {
        Intent intent = new Intent(getApplicationContext(), CategoryList.class);
        intent.putExtra("Category", "true");
        startActivity(intent);

    }

    ///override to sending ads
    public void OverrideAdvertisement(View view) {
        AlertMeOverrideAds(getString(R.string.TitleOverrideAds), getString(R.string.MessageOverrideAds), true);
    }

    ///button for send ads to server
    public void BtnInsertAdvertisement(View view) throws UnsupportedEncodingException {

        if (ImageString != null) {
            if (categoryID != null) {
                if (EditTextInsertSeller.getText().length() > 4) {
                    if (EditTextInsertTitle.getText().length() > 9) {
                        if (EditTextInsertAddress.getText().length() > 4) {
                            if (EditTextInsertEmail.getText().length() > 1) {
                                if (EditTextInsertPhoneNumber.getText().length() > 10) {
                                    if (EditTextInsertDescription.getText().length() > 49) {

                                        Title = EditTextInsertTitle.getText().toString().trim();
                                        Address = EditTextInsertAddress.getText().toString().trim();
                                        Description = EditTextInsertDescription.getText().toString().trim();
                                        Seller = EditTextInsertSeller.getText().toString().trim();
                                        Email = EditTextInsertEmail.getText().toString().trim();
                                        PhoneNumber = EditTextInsertPhoneNumber.getText().toString().trim();
                                        Title = URLEncoder.encode(Title + " ", "UTF-8");
                                        Address = URLEncoder.encode(Address + " ", "UTF-8");
                                        Description = URLEncoder.encode(Description + " ", "UTF-8");
                                        Seller = URLEncoder.encode(Seller + " ", "UTF-8");

                                        ImageString.trim();

                                        CategoryID = categoryID.trim();
                                        UserID.trim();
                                        onPreExecute();
                                        InsertAdvertisement(Title, Address, Description, ImageString,
                                                Seller, Email, PhoneNumber, CategoryID, UserID);

                                    } else {
                                        Drawable dr = getResources().getDrawable(R.drawable.ic_error_red_a700_24dp);
                                        //add an error icon to yur drawable files
                                        Toast.makeText(this, "حداقل 50 نویسه وارد کنید!", Toast.LENGTH_LONG).show();
                                        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                                        EditTextInsertDescription.setCompoundDrawables(dr, null, null, null);
                                    }
                                } else {

                                    Drawable dr = getResources().getDrawable(R.drawable.ic_error_red_a700_24dp);
                                    //add an error icon to yur drawable files
                                    Toast.makeText(this, "شماره تماس خود را به صورت صحیح وارد کنید!", Toast.LENGTH_LONG).show();
                                    dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                                    EditTextInsertPhoneNumber.setCompoundDrawables(dr, null, null, null);
                                }
                            } else {
                                Drawable dr = getResources().getDrawable(R.drawable.ic_error_red_a700_24dp);
                                //add an error icon to yur drawable files
                                Toast.makeText(this, "ایمیل خود را به صورت صحیح وارد کنید!", Toast.LENGTH_LONG).show();
                                dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                                EditTextInsertEmail.setCompoundDrawables(dr, null, null, null);
                            }

                        } else {
                            Drawable dr = getResources().getDrawable(R.drawable.ic_error_red_a700_24dp);
                            //add an error icon to yur drawable files
                            Toast.makeText(this, "آدرس خود را به صورت صحیح وارد کنید\n(5 نویسه)!", Toast.LENGTH_LONG).show();
                            dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                            EditTextInsertAddress.setCompoundDrawables(dr, null, null, null);
                        }
                    } else {
                        Drawable dr = getResources().getDrawable(R.drawable.ic_error_red_a700_24dp);
                        //add an error icon to yur drawable files
                        Toast.makeText(this, "حداقل 10 نویسه برای عنوان وارد کنید!", Toast.LENGTH_LONG).show();
                        dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                        EditTextInsertTitle.setCompoundDrawables(dr, null, null, null);
                    }
                } else {
                    Drawable dr = getResources().getDrawable(R.drawable.ic_error_red_a700_24dp);
                    //add an error icon to yur drawable files
                    Toast.makeText(this, "حداق 5 نویسه برای آگهی دهنده وارد کنید!", Toast.LENGTH_LONG).show();
                    dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                    EditTextInsertSeller.setCompoundDrawables(dr, null, null, null);
                }
            } else {
                Drawable dr = getResources().getDrawable(R.drawable.ic_error_red_a700_24dp);
                //add an error icon to yur drawable files
                Toast.makeText(this, "دسته بندی آگهی را انتخاب کنید!", Toast.LENGTH_LONG).show();
                dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
                BtnGoToCategoryList.setCompoundDrawables(dr, null, null, null);
            }
        } else {
            Drawable dr = getResources().getDrawable(R.drawable.ic_add_a_photo_red_a700_24dp);
            //add an error icon to yur drawable files
            Toast.makeText(this, "تصویری برای آگهی انتخاب کنید!", Toast.LENGTH_LONG).show();
            dr.setBounds(0, 0, dr.getIntrinsicWidth(), dr.getIntrinsicHeight());
            ImageButtonInsertImage.setImageDrawable(dr);
        }

    }

    private void InsertAdvertisement(final String Title, final String Address, final String Description, final String ImageString,
                                     final String Seller, final String Email, final String PhoneNumber,
                                     final String CategoryID, final String UserID) {

        // Tag used to cancel the request
        String tag_string_req = "req_register";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INSERT_DATA, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register Response: " + response.toString());
                String res = response.toString().trim();
                if (res.equals("\"done\"")) {
                    AlertMeResult(getString(R.string.ResultInsert_Title), getString(R.string.ResultOfInsertMessageSuccess), true);
                } else if (res.equals("\"failure_inserting_database!\"")) {
                    Toast.makeText(getApplicationContext(), R.string.FailureInsertingDatabase, Toast.LENGTH_LONG).show();
                    //  AlertMeResult(getString(R.string.ResultInsert_Title), getString(R.string.ResultOfInsertMessageFailure), true);

                } else if (res.equals("\"failure_post\"")) {
                    //  AlertMeResult(getString(R.string.ResultInsert_Title), getString(R.string.ResultOfInsertMessageFailure), true);

                    Toast.makeText(getApplicationContext(), R.string.FailureConnecting, Toast.LENGTH_LONG).show();
                }
                pDialog.hide();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Insert ads Error: " + error.getMessage());
                AlertMeResult(getString(R.string.ResultInsert_Title), getString(R.string.ResultOfInsertMessageFailure), true);

                pDialog.hide();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("title", Title);
                params.put("address", Address);
                params.put("description", Description);
                params.put("image", ImageString);
                params.put("seller", Seller);
                params.put("email", Email);
                params.put("phone_number", PhoneNumber);
                params.put("category_id", CategoryID);
                params.put("user_id", UserID);
                return params;

            }
        };


        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    /// Alert method for inserted ads or not
    public void AlertMeResult(String title, String body, boolean cancelable) {
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(InsertAdvertisement.this);
        alert.setCancelable(cancelable);
        alert.setTitle(title);
        alert.setMessage(body);
        alert.setPositiveButton(R.string.BtnDialogNewAdvertisement,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        onResume();
                    }
                }
        );
        alert.setNeutralButton(R.string.BtnDialogOk,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), ShowAllAdvertisement.class);
                        intent.putExtra("categoryId", "aaa");
                        startActivity(intent);
                        finish();
                    }
                });

        alert.create();
        alert.show();
    }

    /// Alert method for overriding inserted ads or not

    public void AlertMeOverrideAds(String title, String body, boolean cancelable) {
        android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(InsertAdvertisement.this);
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
}


