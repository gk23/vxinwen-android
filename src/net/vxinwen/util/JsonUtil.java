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
                    News entity = new News();
                    Log.d(JsonUtil.class.getName(), "the news json is "+newsJson.toJSONString());
                    entity.setId((Long) newsJson.get("id"));
                    Object title = newsJson.get("title");
                    entity.setTitle(title==null?null:title.toString());
                    // TODO 如果支持自定义tag功能，那category和news是多对多关系，则setCategoryId不正确
                    entity.setPublishTime(TimestampUtil.stringToTimeStamp(newsJson.get("publishTime")
                            .toString()));
                    Object imageAddress = newsJson.get("image");
                    entity.setImage(imageAddress==null?null:imageAddress.toString());
                    entity.setBody(newsJson.get("body").toString());
                    if(category.equals("段子")){
                        // 段子没有摘要，直接显示原内容
                        entity.setSummary(newsJson.get("body").toString());
                    }else{
                        entity.setSummary(newsJson.get("summary").toString());
                    }
                    Object url = newsJson.get("url");
                    entity.setUrl(url==null?null:url.toString());
                    entity.setCategory(newsJson.get("category").toString());
                    Object source = newsJson.get("source");
                    entity.setSource(source==null?null:source.toString());
                    newsList.add(entity);
                }
                newses.put(category, newsList);
            }
        }
        return newses;
    }
}
