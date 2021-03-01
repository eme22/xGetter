package com.htetznaing.lowcostvideo.Sites;

import com.htetznaing.lowcostvideo.LowCostVideo;
import com.htetznaing.lowcostvideo.Model.XModel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MegaNZ {

    private static LowCostVideo.OnTaskCompleted onTaskCompleted;

    private static String regex = "(?:https:\\/\\/mega\\.nz\\/)(?:file\\/?|embed#!\\/?|embed\\/)(.*?)(?:#|!)(.*)";

    public static void fetch(String url, final LowCostVideo.OnTaskCompleted onComplete) {
        onTaskCompleted = onComplete;

        final Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        final Matcher matcher = pattern.matcher(url);
        if (matcher.find()) url = "https://mega.nz/#!"+matcher.group(1)+"!"+matcher.group(2);
        else onTaskCompleted.onError();

        try
        {
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        result(url);
    }

    private static void result(String result) {
        System.out.println("Fucked: " + result);
        if (result != null && !result.isEmpty()) {
            ArrayList<XModel> xModels = new ArrayList<>();
            XModel model = new XModel();
            model.setUrl(result);
            model.setQuality("Mega");
            xModels.add(model);
            onTaskCompleted.onTaskCompleted(xModels, false);
        } else onTaskCompleted.onError();
    }

}
