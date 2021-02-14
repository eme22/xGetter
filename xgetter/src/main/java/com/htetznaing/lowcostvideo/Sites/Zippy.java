package com.htetznaing.lowcostvideo.Sites;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Zippy {

    public static void fetch(String url, final LowCostVideo.OnTaskCompleted onTaskCompleted) {
        
        AndroidNetworking.get(url).build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response)
            {
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
            public void onError(ANError anError)
            {
                onTaskCompleted.onError();
            }
        });
    }

    private static ArrayList<XModel> parseVideo(String response)
    {
        ArrayList<XModel> xModels = new ArrayList<>();
        XModel xModel = new XModel();
        xModel.setQuality("Normal");
        xModel.setUrl(response);
        xModels.add(xModel);
        return xModels;
    }

    private static String parseURL(String response)
    {
        final String regex = "<source src=\"(.*?)\"";
        final Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
        final Matcher matcher = pattern.matcher(response);
        if (matcher.find()) {
            String url = matcher.group(1);
            if (url != null && url.contains("&amp;"))
                url = url.replace("&amp;", "&");
            return  "https:"+url;
        }
        else return null;
    }
}
