package ir.Mikado;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class JSONParserCategory {


    List<HashMap<String, Object>> parse(String json) {
        List<HashMap<String, Object>> all_cats =
                new ArrayList<>();

        try {
            JSONObject jObj = new JSONObject(json);

            JSONArray jArr = jObj.getJSONArray("category");

            for (int i = 0; i < jArr.length(); i++) {
                HashMap<String, Object> cat = new HashMap<>();

                JSONObject temp = (JSONObject) jArr.get(i);

                cat.put("id", temp.getString("id"));
                cat.put("name", temp.getString("name"));
                cat.put("amount", "[" + temp.getString("amount") + "]");
                //  cat.put( "image" , R.drawable.pic1 );


                all_cats.add(cat);
            }
        } catch (Exception e) {


            String log_label = "ErrorMessage";
            Log.i(log_label, "error in CatParser in parser() -> " + e.toString());

        }

        return (all_cats);
    }
}