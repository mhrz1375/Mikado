package ir.Mikado;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.HashMap;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ShowFullAdvertisement extends AppCompatActivity {
    public ImageView FullAdsImage;
    public TextView FullAdsTitle;
    public String CallPhoneNumber;
    public String TextSeller;
    public String StringFullImage;
    public String SendMailTo;
    public String SendMessageTo;
    public TextView TextDialogSeller;
    private Button BtnDialogCallText;
    private Button BtnDialogEmailText;
    private Button BtnDialogMessageText;
    private SessionManager session;


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_full_advertisement_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            //noinspection ConstantConditions
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
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialog(fab);
            }
        });
        setTitle("آگهی ها");
        Bundle data = getIntent().getExtras();

        @SuppressWarnings({"unchecked", "ConstantConditions"}) HashMap<String, Object> hm =
                (HashMap<String, Object>) data.get("ads");

        FullAdsImage = findViewById(R.id.FullAdsImage);
        FullAdsTitle = findViewById(R.id.FullAdsTitle);
        TextView FullAdsLocation = findViewById(R.id.FullAdsLocation);
        TextView FullAdsDescription = findViewById(R.id.FullAdsDescription);

        TextView FullAdsCategory = findViewById(R.id.FullAdsCategory);
        TextView FullAdsDate = findViewById(R.id.FullAdsDate);
        boolean flag;

        try {
            @SuppressWarnings("ConstantConditions") double t = Double.parseDouble(hm.get("image").toString());
            flag = true;
        } catch (Exception e) {
            flag = false;
        }

        if (flag == false) {
            @SuppressWarnings("ConstantConditions") File imgFile = new File(hm.get("image").toString());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                FullAdsImage.setImageBitmap(myBitmap);
            } else {
                FullAdsImage.setImageResource(R.drawable.pic1);
            }
        } else {
            FullAdsImage.setImageResource(R.drawable.pic1);
        }

        setTitle(hm.get("title").toString());
        FullAdsTitle.setText((String) hm.get("title"));
        FullAdsLocation.setText((String) hm.get("address"));
        FullAdsDescription.setText((String) hm.get("description"));
        TextSeller = hm.get("seller").toString();
        CallPhoneNumber = hm.get("phone_number").toString();
        SendMailTo = hm.get("email").toString();
        SendMessageTo = hm.get("phone_number").toString();
        FullAdsCategory.setText((String) hm.get("category"));
        FullAdsDate.setText((String) hm.get("created_at_date"));
        StringFullImage = hm.get("image").toString();


    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void GoToCallPhoneNumber(View view) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + CallPhoneNumber));
        Toast.makeText(this, "در حال تماس با " + CallPhoneNumber, Toast.LENGTH_SHORT).show();
        startActivity(callIntent);
    }

    public void GoToSendMessage(View view) {
        Intent sendMessageIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + SendMessageTo));
        startActivity(sendMessageIntent);
        Toast.makeText(this, "ارسال پیامک به  " + SendMessageTo, Toast.LENGTH_SHORT).show();

    }

    public void GoToSendEmail(View view) {
        Intent sendEmailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", SendMailTo.toString(), null));
        startActivity(sendEmailIntent);
        Toast.makeText(this, "ارسال ایمیل به  " + SendMailTo, Toast.LENGTH_SHORT).show();

    }

    public void GoToShowFullScreen(View view) {
        Intent fullScreenIntent = new Intent(getApplicationContext(), FullscreenActivity.class);
        fullScreenIntent.putExtra("StringFullImage", StringFullImage);

        startActivity(fullScreenIntent);

    }

    public void Dialog(View v) {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);

            LayoutInflater inflater = (LayoutInflater)
                    this.getSystemService(LAYOUT_INFLATER_SERVICE);

            @SuppressWarnings("ConstantConditions") View layout = inflater.inflate(R.layout.show_full_advertisement_content_dialog,
                    (ViewGroup) findViewById(R.id.show_full_advertisement_content_dialog));
            TextDialogSeller = layout.findViewById(R.id.DialogSeller);
            TextDialogSeller.setText("آگهی دهنده: " + TextSeller);

            BtnDialogCallText = layout.findViewById(R.id.DialogCall);
            BtnDialogCallText.setText(" تماس با : " + CallPhoneNumber);

            BtnDialogEmailText = layout.findViewById(R.id.DialogEmail);
            BtnDialogEmailText.setText(" ارسال ایمیل به: " + SendMailTo);

            BtnDialogMessageText = layout.findViewById(R.id.DialogMessage);
            BtnDialogMessageText.setText(" ارسال پیامک به: " + SendMessageTo);

            builder.setView(layout);

            builder.setCancelable(false);

            builder.setPositiveButton(R.string.btn_Back_to_home,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
            );

            builder.create();

            builder.show();
        } catch (Exception e) {
        }

    }
}
