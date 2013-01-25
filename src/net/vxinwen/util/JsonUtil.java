package net.vxinwen.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.vxinwen.bean.News;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.util.Log;

public class JsonUtil {

    /**
     * @param jsonString
     * @return
     */
    public static Map<String, List<News>> jsonToNews(String jsonString) {
        Map<String, List<News>> newses = new HashMap<String, List<News>>();
        if (jsonString != null && jsonString.trim().length() > 0) {
            JSONObject result = (JSONObject) JSONValue.parse(jsonString);
            Iterator it = result.keySet().iterator();
            while (it.hasNext()) {
                String category = (String) it.next();
                JSONArray newsArray = (JSONArray) result.get(category);
                List<News> newsList = new ArrayList<News>(newsArray.size());
                for (int i = 0; i < newsArray.size(); i++) {
                    JSONObject newsJson = (JSONObject) newsArray.get(i);
                    News news = new News();
                    Log.d(JsonUtil.class.getName(), "the news json is "+newsJson.toJSONString());
                    news.setId((Long) newsJson.get("id"));
                    Object title = newsJson.get("title");
                    news.setTitle(title==null?null:title.toString());
                    // TODO 如果支持自定义tag功能，那category和news是多对多关系，则setCategoryId不正确
                    news.setPublishTime(TimestampUtil.stringToTimeStamp(newsJson.get("publishTime")
                            .toString()));
                    Object imageAddress = newsJson.get("imageAddress");
                    news.setImageAddress(imageAddress==null?null:imageAddress.toString());
                    news.setSummary(newsJson.get("summary").toString());
                    Object url = newsJson.get("url");
                    news.setUrl(url==null?null:url.toString());
                    news.setCategory(newsJson.get("category").toString());
                    newsList.add(news);
                }
                newses.put(category, newsList);
            }
        }
        return newses;
    }
}
