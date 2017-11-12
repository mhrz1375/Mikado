package ir.Mikado;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class CategoryList extends AppCompatActivity {
    private Bundle address;
    private List<HashMap<String, Object>> AllCategory = new ArrayList<>();
    private LinearLayout LinearLayoutHeaderProgress;
    private LinearLayout linearLayoutHeaderButtonRefresh;
    private Button BtnRefresh;

    private ListView ListViewCategory;

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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list_activity);
        setTitle(R.string.label_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_forward_black_24dp));
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
        address = getIntent().getExtras();

        LinearLayoutHeaderProgress = findViewById(R.id.linearLayoutHeaderProgressCategoryList);

        LinearLayoutHeaderProgress.setVisibility(View.VISIBLE);
        linearLayoutHeaderButtonRefresh = findViewById(R.id.linearLayoutHeaderButtonCategoryList);

        MakeCategoryList();
        ListViewCategory = findViewById(R.id.ListViewCategory);
        BtnRefresh = findViewById(R.id.BtnRefreshCategoryList);
        BtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutHeaderButtonRefresh.setVisibility(View.GONE);
                onResume();
                if (!isConnected()) {
                    Toast.makeText(CategoryList.this, "لطفا اتصال اینترنت را چک کنید!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        ListViewCategory.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (address.getString("Category").equals("true")) {

                            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(CategoryList.this);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("CategoryId", AllCategory.get(position).get("id").toString()); //InputString: from the EditText
                            editor.putString("CategoryName", AllCategory.get(position).get("name").toString()); //InputString: from the EditText

                            editor.commit();

                            finish();
                        } else if (address.getString("Category").equals("false")) {
                            Intent i = new Intent(getApplicationContext(), ShowAllAdvertisement.class);
                            i.putExtra("categoryId", AllCategory.get(position).get("id").toString());
                            startActivity(i);
                            finish();
                        }


                    }
                }
        );

    }

    public void MakeCategoryList() {
        DownloadCategory downloadCategory = new DownloadCategory();

        downloadCategory.execute(AppConfig.URL_GET_CATEGORY);
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

        super.onResume();
    }

    private class DownloadCategory extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String data = "";

            try {
                JSONDownloader jd = new JSONDownloader();

                data = jd.DownloadURL(params[0]);
            } catch (Exception e) {

                Log.i("MatiMessage", "error in DownloadCats -> " + e.toString());

            }

            return data;
        }

        @Override
        protected void onPostExecute(String json) {
            JSONParserCategory parser = new JSONParserCategory();

            AllCategory = parser.parse(json);

            String[] from = {"name", "amount"};

            int[] to = {R.id.NameCategory, R.id.AmountCategory};

            SimpleAdapter myAdapter = new SimpleAdapter(
                    getBaseContext(), AllCategory,
                    R.layout.category_list_content_layout, from, to
            );

            ListViewCategory.setAdapter(myAdapter);
            LinearLayoutHeaderProgress.setVisibility(View.GONE);

        }
    }

}
