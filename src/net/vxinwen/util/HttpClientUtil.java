package net.vxinwen.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class HttpClientUtil {
    public static boolean interceptFlag = false;

    private static HttpClient client;

    static {
        init();
    }

    private static void init() {
        client = new DefaultHttpClient();
        HttpParams params = client.getParams();
        HttpConnectionParams.setConnectionTimeout(params, 10000);
        HttpConnectionParams.setSoTimeout(params, 30000);
    }

    public static String getContent(String url) {
        synchronized (client) {
            try {
                HttpGet get = new HttpGet(url);
                setHeader(get);
                HttpResponse response = client.execute(get);
                if (response.getStatusLine().getStatusCode() == 200) {
                    InputStream is = response.getEntity().getContent();
                    return inputStreamToString(is);
                }
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static void setHeader(HttpGet get) {
        get.setHeader("type", "apk");
        get.setHeader("Connection", "Keep-Alive");
        get.setHeader("Charset", "UTF-8");
        get.setHeader("platform", "android");
        // get.setHeader("version", Config.getVersionName());
    }

    @SuppressWarnings("finally")
    protected static String inputStreamToString(InputStream is) {

        String value = null;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            StringBuffer buf = new StringBuffer();
            String str;

            while ((str = reader.readLine()) != null) {
                buf.append(str);
            }
            value = buf.toString();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            value = value.replaceAll(" ", " ");
            value = value.trim();
            // Logger.i("NetHttpClient 返回语句" + value);
            return value;
        }

    }

}
