package ir.Mikado;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowAllAdvertisement extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = ShowAllAdvertisement.class.getSimpleName();


    public TextView NavDrawerEditTextFirstNameLastName;
    public TextView NavDrawerEditTextEmail;
    private int current_page = 0;
    private List<HashMap<String, Object>> AllAdvertisement = new ArrayList<>();
    private ListView lv;
    private String Email;
    private String PictureProfile;
    private String CreatedAt;
    private ProgressDialog pDialog;
    private boolean Switch = false;
    private SQLiteHandler db;
    private SessionManager session;
    private ListView ListDialogCategory;
    private RequestQueue requestQueue;
    private String URLAdvertisement = AppConfig.URL_GET_DATA;
    private String CategoryId;
    private LinearLayout linearLayoutHeaderButtonRefresh;
    private LinearLayout LinearLayoutHeaderProgress;

    private Button BtnRefreshAdvertisementList;

    public ShowAllAdvertisement() {
    }

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo[] ni = cm.getAllNetworkInfo();
        for (NetworkInfo aNi : ni) {
            if (aNi.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }

        return false;
    }


    @SuppressLint({"SetTextI18n", "NewApi"})
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.Str_ShowAds);
        setContentView(R.layout.show_all_advertisement_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requestQueue = Volley.newRequestQueue(this);
        try {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            setProgressBarIndeterminateVisibility(true);

        } catch (Exception ignored) {
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session = new SessionManager(getApplicationContext());
                if (isConnected()) {
                    if (session.isLoggedIn()) {
                        Intent intent = new Intent(getApplicationContext(), InsertAdvertisement.class);
                        startActivity(intent);

                    } else {
                        Snackbar.make(view, "جهت ثبت آگهی به حساب کاربری خود وارد شوید.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } else {
                    Toast.makeText(ShowAllAdvertisement.this, getString(R.string.InternetErrorTitle), Toast.LENGTH_SHORT).show();
                }
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        LinearLayoutHeaderProgress = findViewById(R.id.linearLayoutHeaderProgress);


        LinearLayoutHeaderProgress.setVisibility(View.VISIBLE);
        NavDrawerEditTextFirstNameLastName = headerView.findViewById(R.id.NavDrawerEditTextFirstNameLastName);
        NavDrawerEditTextEmail = headerView.findViewById(R.id.NavDrawerEditTextEmail);
        ImageView navDrawerImageViewPictureProfile = headerView.findViewById(R.id.NavDrawerImageViewPictureProfile);


        linearLayoutHeaderButtonRefresh = findViewById(R.id.linearLayoutHeaderButtonRefreshAdsList);
        BtnRefreshAdvertisementList = findViewById(R.id.BtnRefreshAdvertisementList);
        BtnRefreshAdvertisementList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutHeaderButtonRefresh.setVisibility(View.GONE);
                onResume();
                if (!isConnected()) {
                    Toast.makeText(ShowAllAdvertisement.this, "لطفا اتصال اینترنت را چک کنید!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());


        // Fetching user details from Sql lite
        HashMap<String, String> user = db.getUserDetails();
        String userId = user.get("user_id");
        String firstName = user.get("first_name");
        String lastName = user.get("last_name");
        String phoneNumber = user.get("phone_number");
        Email = user.get("email");
        PictureProfile = user.get("picture_profile");
        CreatedAt = user.get("created_at");

        if (session.isLoggedIn()) {
            NavDrawerEditTextFirstNameLastName.setText(firstName + " " + lastName, TextView.BufferType.EDITABLE);
            NavDrawerEditTextEmail.setText(Email, TextView.BufferType.EDITABLE);
        } else {
            NavDrawerEditTextFirstNameLastName.setText("کاربر مهمان", TextView.BufferType.EDITABLE);
            NavDrawerEditTextEmail.setText("ثبت نام کنید/واردشوید", TextView.BufferType.EDITABLE);
        }


        Picasso.with(getApplicationContext()).load(AppConfig.URL_SITE + PictureProfile).into(navDrawerImageViewPictureProfile);


        Bundle address = getIntent().getExtras();


        assert address != null;
        if (Objects.equals(address.getString("categoryId"), "aaa")) {

            MakeAllAdvertisementList();

        } else {
            CategoryId = address.getString("categoryId");
            make_ads_list_by_cat();
        }

        lv = findViewById(R.id.ShowAllAdvertisementList);

        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getApplicationContext(), ShowFullAdvertisement.class);
                        intent.putExtra("ads", AllAdvertisement.get(position));
                        startActivity(intent);
                    }
                }
        );

    }

    public void BtnRefreshAdvertisementList() {

    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        if (isConnected()) {
            LinearLayoutHeaderProgress.setVisibility(View.VISIBLE);
            linearLayoutHeaderButtonRefresh.setVisibility(View.GONE);

        } else {
            LinearLayoutHeaderProgress.setVisibility(View.GONE);
            linearLayoutHeaderButtonRefresh.setVisibility(View.VISIBLE);
        }
        if (Switch) {

            finish();
            startActivity(getIntent());
        }


        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        clear_cache();
    }

    @Override
    protected void onStop() {
        Switch = true;
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_all_advertisement, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            intent = new Intent(getApplicationContext(), AboutUs.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.NavUserAccount) {
            // Handle the camera action
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            onStop();

        } else if (id == R.id.NavNewAdvertisement) {
            session = new SessionManager(getApplicationContext());

            if (session.isLoggedIn()) {

                Intent intent = new Intent(getApplicationContext(), InsertAdvertisement.class);
                startActivity(intent);

            } else {
                Toast.makeText(this, "به حساب کاربری خود وارد شوید", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.NavCategory) {
            Intent intent = new Intent(getApplicationContext(), CategoryList.class);
            intent.putExtra("Category", "false");
            startActivity(intent);
        } else if (id == R.id.NavAboutUs) {
            Intent intent = new Intent(getApplicationContext(), AboutPearl.class);
            startActivity(intent);
        } else if (id == R.id.NavRules) {
            Intent in = new Intent(getApplicationContext(), Rules.class);
            startActivity(in);


        } else if (id == R.id.NavFAQ) {
            builderSingle();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void MakeAllAdvertisementList() {
        try {
            DownloadTask dl = new DownloadTask();

            dl.execute(URLAdvertisement + current_page);

            current_page++;
        } catch (Exception e) {

            Log.i(TAG, "error in ads in MakeAllAdvertisementList() -> " + e.toString());

        }
    }

    public void make_ads_list_by_cat() {
        try {
            DownloadTask dl = new DownloadTask();

            dl.execute(AppConfig.URL_GET_DATA_BY_CATEGORY + CategoryId);
        } catch (Exception e) {

            Log.i(TAG, "error in ads in make_ads_list_by_cat() -> " + e.toString());

        }
    }

    public void clear_cache() {
        try {
            File[] f = getBaseContext().getCacheDir().listFiles();

            for (File file : f) {
                file.delete();
            }
        } catch (Exception e) {

            Log.i(TAG, "error in clear_cache() -> " + e.toString());

        }
    }


    public void builderSingle() {

        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                ShowAllAdvertisement.this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("mhrz.dev@gmail.com");
        arrayAdapter.add("09394062047");

        builderSingle.setTitle("از یکی از روش های زیر میتوانید با ما در ارتباط باشید");

        builderSingle.setNegativeButton(R.string.BtnExit,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog.Builder builder = builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                assert strName != null;
                if (strName.length() > 11) {
                    try {
                        Intent sendEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mail to", strName, null));
                        startActivity(sendEmailIntent);
                    } catch (Exception ignored) {

                    }
                } else {
                    Intent sendMessageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:09394062047"));
                    startActivity(sendMessageIntent);
                }
            }

        });
        builderSingle.show();

    }

    @SuppressLint("StaticFieldLeak")
    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String temp = "";

            try {
                JSONDownloader jd = new JSONDownloader();

                temp = jd.DownloadURL(params[0]);
            } catch (Exception e) {

                Log.i(TAG, "error in DownloadTask -> " + e.toString());

            }

            return (temp);
        }

        @Override
        protected void onPostExecute(String s) {
            ListViewLoaderTask loader = new ListViewLoaderTask();

            loader.execute(s);

        }
    }

    @SuppressLint("StaticFieldLeak")
    private class ListViewLoaderTask extends AsyncTask<String, Void, SimpleAdapter> {
        @Override
        protected SimpleAdapter doInBackground(String... params) {
            try {
                JSONParserAdvertisement parser = new JSONParserAdvertisement();

                AllAdvertisement.addAll(parser.parse(params[0]));
            } catch (Exception e) {

                Log.i(TAG, "error in ListViewLoaderTask -> " + e.toString());

            }

            String[] from = {"title", "description", "address", "image", "created_at_date", "category"};

            int[] to = {R.id.AdvertisementTitle, R.id.AdvertisementIntro, R.id.AdvertisementLocation,
                    R.id.AdvertisementImage, R.id.AdvertisementCreatedAtDate, R.id.AdvertisementCategory};

            return new SimpleAdapter(
                    getBaseContext(), AllAdvertisement, R.layout.advertisement_list, from, to
            );

        }

        @Override
        protected void onPostExecute(SimpleAdapter adapter) {

            for (int i = 0; i < adapter.getCount(); i++) {
                @SuppressWarnings("unchecked") HashMap<String, Object> hm = (HashMap<String, Object>) adapter.getItem(i);

                String imgURL = (String) hm.get("image_path");

                HashMap<String, Object> forDownload = new HashMap<>();

                forDownload.put("image_path", imgURL);
                forDownload.put("position", i);

                ImageDownloaderTask imgDownloader = new ImageDownloaderTask();

                //noinspection unchecked
                imgDownloader.execute(forDownload);
            }
            lv.setAdapter(adapter);


        }
    }


    // show The Image in a ImageView
    @SuppressLint("StaticFieldLeak")
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
                        + "/Image_" + position + "_" + current_page + ".jpg");

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

                Log.i(TAG, "error in ImageDownloaderTask -> " + e.toString());

            }
            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> result) {
            try {
                String image = (String) result.get("image");

                int position = (Integer) result.get("position");

                SimpleAdapter adb = (SimpleAdapter) lv.getAdapter();

                @SuppressWarnings("unchecked") HashMap<String, Object> hm = (HashMap<String, Object>) adb.getItem(position);

                hm.put("image", image);

                adb.notifyDataSetChanged();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            LinearLayoutHeaderProgress.setVisibility(View.GONE);

        }
    }
}
