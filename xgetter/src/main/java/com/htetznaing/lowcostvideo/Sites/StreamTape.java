package com.htetznaing.lowcostvideo.Sites;


import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreamTape {

    private static final Map<String,String> headers = Collections.singletonMap("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36 Edg/87.0.664.66");

    public static void fetch(String url, final LowCostVideo.OnTaskCompleted onTaskCompleted){
        AndroidNetworking.get(url)
                .addHeaders(headers)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d(LowCostVideo.TAG,response);
                        System.out.println(response);
                        response = parseURL(response);
                        ArrayList<XModel> xModels = null;
                        if (response != null) {
                            xModels = parseVideo(response);
                        }
                        else onTaskCompleted.onError();
                        if (xModels == null || xModels.isEmpty()){
                            onTaskCompleted.onError();
                        }else onTaskCompleted.onTaskCompleted(xModels, false);

                    }
                    @Override
                    public void onError(ANError anError) {
                        onTaskCompleted.onError();
                    }
                });
    }

    private static String parseURL(String response) {
        final String regex = "videolink['\"].+?innerHTML\\s*=\\s*['\"]([^'\"]+)";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            return  "https:"+matcher.group(1)+"&stream=1";
        }
        else return parseURL2(response);
    }

    private static String parseURL2(String response) {
        final String regex = "'vid'\\+'eolink'.*?\".*?(.*)'";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String a = matcher.group(1);
            if (a != null) {
                a = a.replace("\" + '","").replace("'","");
            }
            return  "https:"+a+"&stream=1";
        }
        else return null;
    }

    private static ArrayList<XModel> parseVideo(String real_url){
        ArrayList<XModel> xModels = new ArrayList<>();
        XModel xModel = new XModel();
        xModel.setQuality("Normal");
        xModel.setUrl(real_url);
        xModels.add(xModel);
        return xModels;
    }
}