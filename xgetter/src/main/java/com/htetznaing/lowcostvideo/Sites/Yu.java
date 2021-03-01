package com.htetznaing.lowcostvideo.Sites;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;
import com.htetznaing.lowcostvideo.Utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.htetznaing.lowcostvideo.Utils.Utils.sortMe;

public class Yu {
    public static void fetch(String url, final LowCostVideo.OnTaskCompleted onComplete){
        AndroidNetworking.get(url)
                .addHeaders("User-Agent","Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.99 Safari/537.36")
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ArrayList<XModel> xModels = parse(response, url);
                        if (xModels!=null){
                            onComplete.onTaskCompleted(xModels,false);
                        }else onComplete.onError();
                    }

                    @Override
                    public void onError(ANError anError) {
                        onComplete.onError();
                    }
                });
    }

    private static ArrayList<XModel> parse(String response, String referer){
        String src = getUrl(response);
        if (null!=src){
            XModel xModel = new XModel();
            xModel.setUrl(src);
            xModel.setQuality("Normal");
            xModel.setHeaders(new HashMap<String, String>() {{ put("Referer", referer); }});
            ArrayList<XModel> xModels = new ArrayList<>();
            xModels.add(xModel);
            return xModels;
        }
        return null;
    }

    private static String getUrl(String html){

        Pattern pattern = Pattern.compile("file: '(http.*vidcache.*mp4)'");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()){
            return matcher.group(1);
        }
        return null;
    }
}
