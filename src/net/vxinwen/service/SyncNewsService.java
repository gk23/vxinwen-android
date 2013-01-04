package net.vxinwen.service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.vxinwen.bean.News;
import net.vxinwen.common.NetHttpClient;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class SyncNewsService {
    // android虚拟机会认为localhost或127.0.0.1为自己，可以用 10.0.2.2或PC实际IP代替本PC电脑IP的localhost或127.0.0.1
    private String url="http://192.168.90.241:8080/vnews/getNews?tags={tags}&ids={ids}";
    public Map<String,List<News>> getNews(long[] lastId,String[] category){
        // Get the json result of the service request.
        String idsString=lastId[0]+"",tagsString=category[0];
        for(int i=1;i<lastId.length;i++){
            idsString+="$$"+lastId[i];
            tagsString+="$$"+category[i];
        }
        url=url.replace("{ids}", idsString).replace("{tags}", tagsString);
        String newsesStr = NetHttpClient.getContent(url);
        
        // 解析JSON内容
        Map<String,List<News>> newses = jsonToNews(newsesStr);
        return newses;
    }
    
    /**
     * 
     * @param jsonString
     * @return
     */
    private Map<String,List<News>> jsonToNews(String jsonString){
        Map<String,List<News>> newses = new HashMap<String, List<News>>();
        if(jsonString!=null&&jsonString.trim().length()>0){
            JSONObject result = (JSONObject)JSONValue.parse(jsonString); 
            Iterator it = result.keySet().iterator();
            while(it.hasNext()){
                String category  = (String)it.next();
                JSONArray newsArray = (JSONArray)result.get(category);
                List<News> newsList = new ArrayList<News>(newsArray.size());
                for(int i=0;i<newsArray.size();i++){
                    JSONObject newsJson =  (JSONObject)newsArray.get(i);
                    News news = new News();
                    news.setId((Long)newsJson.get("id"));
                    news.setTitle(newsJson.get("title").toString());
                    // TODO 如果支持自定义tag功能，那category和news是多对多关系，则setCategoryId不正确
                    news.setPublishTime(stringToTimeStamp(newsJson.get("publishTime").toString()));
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
    private Timestamp stringToTimeStamp(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            return new Timestamp(sdf.parse(s).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
