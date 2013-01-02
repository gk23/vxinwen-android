package net.vxinwen.service;

import java.util.ArrayList;
import java.util.List;

import net.vxinwen.bean.News;
import net.vxinwen.common.NetHttpClient;

public class SyncNewsService {
    private String url="http://localhost:8080/vnews/getNews?tags={tags}&ids={ids}";
    public List<News> getNews(long[] lastId,String[] category){
        // get the json result of the service request.
        String idsString=lastId[0]+"",tagsString=category[0];
        for(int i=1;i<lastId.length;i++){
            idsString+="$$"+lastId[i];
            tagsString+="$$"+category[i];
        }
        url=url.replace("{ids}", idsString).replace("{tags}", tagsString);
        String newsesStr = NetHttpClient.getContent(url);
        
        // 解析JSON内容
        List<News> newses = new ArrayList<News>(); 
        
        return newses;
    }

}
