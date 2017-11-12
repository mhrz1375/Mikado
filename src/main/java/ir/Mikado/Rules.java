package ir.Mikado;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Rules extends AppCompatActivity {

    private LinearLayout linearLayoutHeaderButtonRefresh;
    private LinearLayout LinearLayoutHeaderProgress;
    private Button BtnRefresh;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rules_activity);


        WebView myWebView = findViewById(R.id.webViewRules);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.loadUrl(AppConfig.URL_GET_RULES);
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
        LinearLayoutHeaderProgress = findViewById(R.id.linearLayoutHeaderProgressRules);


        LinearLayoutHeaderProgress.setVisibility(View.VISIBLE);

        linearLayoutHeaderButtonRefresh = findViewById(R.id.linearLayoutHeaderButtonRefreshRules);
        BtnRefresh = findViewById(R.id.BtnRefreshRules);
        BtnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearLayoutHeaderButtonRefresh.setVisibility(View.GONE);
                onResume();
                if (!isConnected()) {
                    Toast.makeText(Rules.this, "لطفا اتصال اینترنت را چک کنید!", Toast.LENGTH_SHORT).show();
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
}
