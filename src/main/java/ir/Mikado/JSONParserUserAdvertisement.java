package ir.Mikado;

import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by MHRZ on 2016-12-23.
 */

public class JSONParserUserAdvertisement {

    private final String log_label = "ErrorMessage";
    public String StringNoThing = "";

    public List<HashMap<String, Object>> parse(String json) throws JSONException {
        List<HashMap<String, Object>> all_ads = new ArrayList<>();

        try {
            JSONObject jObj = new JSONObject(json);


            JSONArray jArr = jObj.getJSONArray("advertisement");


            for (int i = 0; i < jArr.length(); i++) {
                JSONObject jsonObject = jArr.getJSONObject(i);

                HashMap<String, Object> Advertisement = new HashMap<>();

                Advertisement.put("id", jsonObject.getString("id"));
                Advertisement.put("title", jsonObject.getString("title"));
                Advertisement.put("address", jsonObject.getString("address"));
                Advertisement.put("description", jsonObject.getString("description"));
                Advertisement.put("image", R.drawable.pic1);
                Advertisement.put("image_path", jsonObject.getString("image"));
                Advertisement.put("seller", jsonObject.getString("seller"));
                Advertisement.put("email", jsonObject.getString("email"));
                Advertisement.put("phone_number", jsonObject.getString("phone_number"));
                Advertisement.put("created_at_date", jsonObject.getString("created_at_date"));
                Advertisement.put("category", jsonObject.getString("category"));
                Advertisement.put("category_id", jsonObject.getString("category_id"));
                Advertisement.put("user_id", jsonObject.getString("user_id"));

                all_ads.add(Advertisement);
            }

        } catch (Exception e) {

            ShowLogin.LinearLayoutHeaderUserProgress.setVisibility(View.GONE);
            Log.i(log_label, "error in AdsUsreParser in parse() ->( " + StringNoThing + ")" + e.toString());

        }
        return (all_ads);
    }


}
