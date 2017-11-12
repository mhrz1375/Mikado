package ir.Mikado;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowLogin extends AppCompatActivity {

    public static LinearLayout LinearLayoutHeaderUserProgress;
    public static LinearLayout LinearLayoutNoAdvertisement;
    public SimpleAdapter myAdapter;
    private TextView EditTextUserId;
    private TextView EditTextFirstName;
    private TextView EditTextLastName;
    private TextView EditTextPhoneNumber;
    private TextView EditTextEmail;
    private TextView EditTextPictureProfile;
    private TextView EditTextCreatedAt;
    private Button btnLogout;
    private Button btnshow;
    private boolean go_next = false;
    private String URLGetUserAdvertisement;
    private int UserId = 0;
    private List<HashMap<String, Object>> all_ads = new ArrayList<>();
    private ListView ShowUserAdvertisementList;
    private List<HashMap<String, Object>> all_cat;
    private ListView lv_cat2;
    private ListView lv_cat;
    private ImageView ImageViewPictureProfile;
    private String FirstName;
    private String LastName;
    private String PhoneNumber;
    private String Email;
    private String PictureProfile;
    private String CreatedAt;
    private ProgressDialog pDialog;
    private boolean Switch = false;
    private SQLiteHandler SqliteDataBase;
    private SessionManager session;
    private ListView ListDialogCategory;
    private RequestQueue requestQueue;
    private Button BtnRefreshUserAdvertisementList;
    private Button BtnAddUserAdvertisementList;
    private LinearLayout linearLayoutHeaderButtonUserRefresh;

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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_login_activity);
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        try {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_forward_white_24dp));
            setProgressBarIndeterminateVisibility(true);
        } catch (Exception e) {
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //What to do on back clicked
                finish();
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertMe(getString(R.string.TitleUserLogOut), getString(R.string.MessageUserLogOut), true);
            }
        });

        ImageViewPictureProfile = findViewById(R.id.ImageViewPictureProfileShowLogin);
        ShowUserAdvertisementList = findViewById(R.id.ShowUserAdvertisementList);

        EditTextFirstName = findViewById(R.id.LoginEditTextFirstName);
        EditTextPhoneNumber = findViewById(R.id.LoginEditTextPhoneNumber);
        EditTextEmail = findViewById(R.id.LoginEditTextEmail);
        EditTextCreatedAt = findViewById(R.id.LoginEditTextCreatedAt);

        LinearLayoutHeaderUserProgress = findViewById(R.id.linearLayoutHeaderProgressUserAdvertisement);


        LinearLayoutNoAdvertisement = findViewById(R.id.linearLayoutHeaderButtonUserNoAdvertisement);


        LinearLayoutHeaderUserProgress.setVisibility(View.VISIBLE);

        linearLayoutHeaderButtonUserRefresh = findViewById(R.id.linearLayoutHeaderButtonRefreshUserAdvertisement);


        BtnRefreshUserAdvertisementList = findViewById(R.id.BtnRefreshUserAdvertisementList);
        BtnRefreshUserAdvertisementList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutHeaderButtonUserRefresh.setVisibility(View.GONE);
                onResume();
                if (!isConnected()) {
                    Toast.makeText(ShowLogin.this, "لطفا اتصال اینترنت را چک کنید!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        BtnAddUserAdvertisementList = findViewById(R.id.BtnAddUserAdvertisementList);
        BtnAddUserAdvertisementList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BtnAddUserAdvertisementList.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), InsertAdvertisement.class);
                startActivity(intent);
                finish();


            }
        });
        // SqLite database handler
        SqliteDataBase = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            LogOutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = SqliteDataBase.getUserDetails();
        UserId = Integer.parseInt(user.get("user_id"));
        FirstName = user.get("first_name");
        LastName = user.get("last_name");
        PhoneNumber = user.get("phone_number");
        Email = user.get("email");
        PictureProfile = user.get("picture_profile");
        CreatedAt = user.get("created_at");

        EditTextFirstName.setText(FirstName + " " + LastName);
        EditTextPhoneNumber.setText(PhoneNumber);
        EditTextEmail.setText(Email);
        EditTextCreatedAt.setText(CreatedAt);
        Picasso.with(getApplicationContext()).load(AppConfig.URL_SITE + PictureProfile).into(ImageViewPictureProfile);
        ShowUserAdvertisementList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), ShowFullAdvertisement.class);
                        intent.putExtra("ads", all_ads.get(position));
                        startActivity(intent);
                    }
                }
        );

        URLGetUserAdvertisement = AppConfig.URL_GET_DATA_BY_USER_ID + UserId;

        // Displaying the user details on the screen

        // Logout button click event
        MakeUserAdvertisementList();

    }

    /// Alert method for ask user to logout
    public void AlertMe(String title, String body, boolean cancelable) {
        AlertDialog.Builder alert = new AlertDialog.Builder(ShowLogin.this);
        alert.setCancelable(cancelable);
        alert.setTitle(title);
        alert.setMessage(body);
        alert.setPositiveButton(R.string.BtnLogOutUserYes,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogOutUser();
                        ClearCache();
                        finish();
                    }
                }
        );
        alert.setNeutralButton(R.string.BtnLogOutUserNo,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
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
            LinearLayoutHeaderUserProgress.setVisibility(View.VISIBLE);
            linearLayoutHeaderButtonUserRefresh.setVisibility(View.GONE);

        } else {
            LinearLayoutHeaderUserProgress.setVisibility(View.GONE);
            linearLayoutHeaderButtonUserRefresh.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void LogOutUser() {
        session.setLogin(false);
        SqliteDataBase.deleteUsers();
        // Launching the login activity
        Intent intent = new Intent(ShowLogin.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void MakeUserAdvertisementList() {
        try {
            ShowLogin.DownloadTask dl = new ShowLogin.DownloadTask();
            dl.execute(URLGetUserAdvertisement);
        } catch (Exception e) {

            Log.i("ShowLogin", "error in ads in make_all_ads_list() -> " + e.toString());

        }
    }

    public void ClearCache() {
        try {
            File[] f = getBaseContext().getCacheDir().listFiles();

            for (File file : f) {
                file.delete();
            }
        } catch (Exception e) {

            Log.i("ShowLogin", "error in clear_cache() -> " + e.toString());

        }
    }

    public void BtnRefresh(View view) {
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String temp = "";

            try {
                JSONDownloader jd = new JSONDownloader();

                temp = jd.DownloadURL(params[0]);
            } catch (Exception e) {

                Log.i("ShowLogin", "error in DownloadTask -> " + e.toString());

            }

            return (temp);
        }

        @Override
        protected void onPostExecute(String s) {
            ShowLogin.ListViewLoaderTask loader = new ShowLogin.ListViewLoaderTask();
            loader.execute(s);
        }
    }

    private class ListViewLoaderTask extends AsyncTask<String, Void, SimpleAdapter> {
        @Override
        protected SimpleAdapter doInBackground(String... params) {
            try {
                JSONParserUserAdvertisement parser = new JSONParserUserAdvertisement();
                all_ads.addAll(parser.parse(params[0]));
            } catch (Exception e) {
                Log.i("ShowLogin", "error in ListViewLoaderTask -> " + e.toString());
            }

            String[] from = {"title", "description", "address", "image", "created_at_date", "category"};

            int[] to = {R.id.AdvertisementTitle, R.id.AdvertisementIntro, R.id.AdvertisementLocation,
                    R.id.AdvertisementImage, R.id.AdvertisementCreatedAtDate, R.id.AdvertisementCategory};

            SimpleAdapter simpleAdapterUserAdvertisementList = new SimpleAdapter(
                    getBaseContext(), all_ads, R.layout.advertisement_list, from, to
            );

            return simpleAdapterUserAdvertisementList;

        }

        @Override
        protected void onPostExecute(SimpleAdapter adapter) {
            ShowUserAdvertisementList.setAdapter(adapter);

            for (int i = 0; i < adapter.getCount(); i++) {
                HashMap<String, Object> hashMap =
                        (HashMap<String, Object>) adapter.getItem(i);

                String imgURL = (String) hashMap.get("image_path");

                HashMap<String, Object> forDownload = new HashMap<>();

                forDownload.put("image_path", imgURL);
                forDownload.put("position", i);

                ShowLogin.ImageDownloaderTask imgDownloader = new ShowLogin.ImageDownloaderTask();

                imgDownloader.execute(forDownload);
            }

        }
    }

    private class ImageDownloaderTask extends
            AsyncTask<HashMap<String, Object>, Void, HashMap<String, Object>> {
        @Override
        protected HashMap<String, Object> doInBackground(HashMap<String, Object>... params) {
            InputStream myStream;

            String imgUrl = (String) params[0].get("image_path");
            int position = (Integer) params[0].get("position");

            try {
                URL url = new URL(imgUrl);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setDoInput(true);

                connection.connect();

                myStream = connection.getInputStream();

                File cacheDirectory = getBaseContext().getCacheDir();

                File temp = new File(cacheDirectory.getPath()
                        + "/Image_" + position + "_" + "1" + ".jpg");

                FileOutputStream outStream = new FileOutputStream(temp);

                Bitmap b = BitmapFactory.decodeStream(myStream);

                b.compress(Bitmap.CompressFormat.JPEG, 100, outStream);

                outStream.flush();

                outStream.close();

                HashMap<String, Object> bitmap = new HashMap<>();

                bitmap.put("image", temp.getPath());
                bitmap.put("position", position);

                return (bitmap);

            } catch (Exception e) {

                Log.i("ShowLogin", "error in ImageDownloaderTask -> " + e.toString());

            }

            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> result) {
            String image = (String) result.get("image");

            int position = (Integer) result.get("position");

            SimpleAdapter simpleAdapterUserAdvertisementList = (SimpleAdapter) ShowUserAdvertisementList.getAdapter();

            HashMap<String, Object> hashMap = (HashMap<String, Object>) simpleAdapterUserAdvertisementList.getItem(position);

            hashMap.put("image", image);
            LinearLayoutHeaderUserProgress.setVisibility(View.GONE);
            simpleAdapterUserAdvertisementList.notifyDataSetChanged();
        }
    }


}
