package ir.Mikado;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AboutPearl extends AppCompatActivity {
    private LinearLayout linearLayoutHeaderButtonRefresh;
    private LinearLayout LinearLayoutHeaderProgress;

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressWarnings("ConstantConditions") NetworkInfo[] ni = cm.getAllNetworkInfo();
        for (NetworkInfo aNi : ni) {
            if (aNi.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }

        return false;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_pearl_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_forward_white_24dp));
            setProgressBarIndeterminateVisibility(true);

        } catch (Exception ignored) {
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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                builderSingle(view);

            }
        });


        WebView myWebView = findViewById(R.id.webViewAboutPearl);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.loadUrl(AppConfig.URL_GET_ABOUT_PEARL);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            public void onPageFinished(WebView view, String url) {
                LinearLayoutHeaderProgress.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {

            }
        });
        LinearLayoutHeaderProgress = findViewById(R.id.linearLayoutHeaderProgressAboutPearl);


        LinearLayoutHeaderProgress.setVisibility(View.VISIBLE);

        linearLayoutHeaderButtonRefresh = findViewById(R.id.linearLayoutHeaderButtonRefreshAboutPearl);
        Button btnRefresh = findViewById(R.id.BtnRefreshAboutPearl);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutHeaderButtonRefresh.setVisibility(View.GONE);
                onResume();
                if (!isConnected()) {
                    Toast.makeText(AboutPearl.this, "لطفا اتصال اینترنت را چک کنید!", Toast.LENGTH_SHORT).show();
                }

            }
        });

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

    public void builderSingle(View view) {

        final AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                AboutPearl.this,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("mhrz.dev@gmail.com");
        arrayAdapter.add("09394062047");

        builderSingle.setIcon(R.drawable.pearl_logo);
        builderSingle.setTitle("یکی از گزینه ها را انتخاب کنید:");

        builderSingle.setNegativeButton(R.string.BtnExit,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.show();

    }
}
