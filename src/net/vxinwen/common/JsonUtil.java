package net.vxinwen.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.vxinwen.bean.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

    public Map<String,List<News>> jsonToNews(String jsonStr){
        //TODO 
        Map<String,List<News>> newses = new HashMap<String, List<News>>();
        try {
            JSONObject json = new JSONObject(jsonStr);
            Iterator it = json.keys();
            while(it.hasNext()){
                String cat = (String)it.next();
                // it is a JsonArray.
                JSONArray newsList = json.getJSONArray(cat);
                // TODO JSONArray to List.
                newses.put(cat, null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return newses;
    }
}
