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
                    news.setId((Long) newsJson.get("id"));
                    news.setTitle(newsJson.get("title").toString());
                    // TODO 如果支持自定义tag功能，那category和news是多对多关系，则setCategoryId不正确
                    news.setPublishTime(TimestampUtil.stringToTimeStamp(newsJson.get("publishTime")
                            .toString()));
                    news.setImageAddress(newsJson.get("imageAddress").toString());
                    news.setSummary(newsJson.get("summary").toString());
                    news.setUrl(newsJson.get("url").toString());
                    newsList.add(news);
                }
                newses.put(category, newsList);
            }
        }
        return newses;
    }
}
