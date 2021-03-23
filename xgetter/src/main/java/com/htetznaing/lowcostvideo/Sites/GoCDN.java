package com.htetznaing.lowcostvideo.Sites;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.FormBody;

public class GoCDN {

    public static void fetch(String url, final LowCostVideo.OnTaskCompleted onTaskCompleted){
        Log.d("ID?: " , url);
        String id = getSrc(url);
        if (id != null){
            AndroidNetworking.post("https://streamium.xyz/gocdn.php?v="+id).build().getAsJSONObject(new JSONObjectRequestListener() {
                @Override
                public void onResponse(JSONObject response) {
                    String result;
                    try {
                        result = response.getString("file");
                    } catch (JSONException e) {
                        result = null;
                    }
                    if (result != null) onTaskCompleted.onTaskCompleted(parse(result),false);
                    else onTaskCompleted.onError();
                }

                @Override
                public void onError(ANError anError) {
                    onTaskCompleted.onError();
                }
            });
        }
        else {
            onTaskCompleted.onError();
        }
    }

    private static String getSrc(String code){
        final String regex = "https:\\/\\/streamium\\.xyz\\/gocdn\\.html#(.*)";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static ArrayList<XModel> parse(String response){
            XModel xModel = new XModel();
            xModel.setUrl(response);
            xModel.setQuality("Normal");
            ArrayList<XModel> xModels = new ArrayList<>();
            xModels.add(xModel);
            return xModels;
    }

}
