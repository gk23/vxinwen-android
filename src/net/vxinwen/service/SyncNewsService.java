package net.vxinwen.service;

import java.util.List;
import java.util.Map;

import net.vxinwen.bean.News;
import net.vxinwen.util.HttpClientUtil;
import net.vxinwen.util.JsonUtil;

public class SyncNewsService {

    // android虚拟机会认为localhost或127.0.0.1为自己，可以用
    // 10.0.2.2或PC实际IP代替本PC电脑IP的localhost或127.0.0.1
    private String url = "http://192.168.90.241:8080/vnews/getNews?tags={tags}&ids={ids}";

    public Map<String, List<News>> getNews(long[] lastId, String[] category) {
        // Get the json result of the service request.
        String idsString = lastId[0] + "", tagsString = category[0];
        for (int i = 1; i < lastId.length; i++) {
            idsString += "$$" + lastId[i];
            tagsString += "$$" + category[i];
        }
        url = url.replace("{ids}", idsString).replace("{tags}", tagsString);
        String newsesStr = HttpClientUtil.getContent(url);

        // 解析JSON内容
        Map<String, List<News>> newses = JsonUtil.jsonToNews(newsesStr);
        return newses;
    }
}
