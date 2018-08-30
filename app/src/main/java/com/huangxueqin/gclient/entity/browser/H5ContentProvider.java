package com.huangxueqin.gclient.entity.browser;

import android.webkit.WebResourceResponse;

import com.google.gson.Gson;
import com.huangxueqin.gclient.http.SwiftGet;
import com.huangxueqin.gclient.model.ZhihuDailyModel;
import com.huangxueqin.gclient.utils.TaskScheduler;

import java.io.IOException;

public class H5ContentProvider {

    public WebResourceResponse getContent(String url) {
        return null;
    }

    private void loadZhihuDailyData(String url) {
        TaskScheduler.getInstance().executeAsync(()->{
            try {
                String html = new SwiftGet(url).getData().toString();
                ZhihuDailyModel model = new Gson().fromJson(html, ZhihuDailyModel.class);
                TaskScheduler.getInstance().runOnUIThread(()->{
//                    mWebView.loadDataWithBaseURL("x-data://base", buildHtml(model), "text/html", "UTF-8", "");
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private String buildHtml(ZhihuDailyModel content) {

        StringBuffer sb = new StringBuffer();
        sb.append("<div class=\"img-wrap\">")
                .append("<h1 class=\"headline-title\">").append(content.title).append("</h1>")
                .append("<span class=\"img-source\">").append(content.imageSource).append("</span>")
                .append("<img src=\"").append(content.image).append("\" alt=\"\">")
                .append("<div class=\"img-mask\"></div>");
        String body = content.body.replace("<div class=\"img-place-holder\"></div>", "");

        String[] js = content.js;
        String[] css = content.css;

        StringBuffer newsHtml = new StringBuffer();
        newsHtml.append("<html><head>");
        for(int i = 0; i < css.length; i++) {
            newsHtml.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"")
                    .append(css[i])
                    .append("\">");
        }
        newsHtml.append("</head><title>" + content.title + "</title><body>");
        newsHtml.append(body);
        newsHtml.append("</body></html>");

        return newsHtml.toString();
    }
}
