package com.xyz.upload.app.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class HttpUtil {

	private static Logger logger = Logger.getLogger(HttpUtil.class);

	public static String doGet(String url) {
        try {
        	HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String strResult = EntityUtils.toString(response.getEntity());

                return strResult;
            }
        }
        catch (IOException e) {
        	e.printStackTrace();
        }

        return null;
	}

	public static String doPost(String url, Map params){

		BufferedReader in = null;
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost request = new HttpPost();
            request.setURI(new URI(url));

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            for (Iterator iter = params.keySet().iterator(); iter.hasNext();) {
    			String name = (String) iter.next();
    			String value = String.valueOf(params.get(name));
    			nvps.add(new BasicNameValuePair(name, value));

    			//System.out.println(name +"-"+value);
    		}
            request.setEntity(new UrlEncodedFormEntity(nvps,HTTP.UTF_8));

            HttpResponse response = client.execute(request);
            int code = response.getStatusLine().getStatusCode();
            if(code == 200){
            	in = new BufferedReader(new InputStreamReader(response.getEntity()
                        .getContent(),"utf-8"));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = in.readLine()) != null) {
                    sb.append(line + NL);
                }

                in.close();

                return sb.toString();
            }
            else{	//
            	System.out.println("閻樿埖锟戒胶鐖滈敍锟�" + code);
            	return null;
            }
        }
        catch(Exception e){
        	e.printStackTrace();

        	return null;
        }
	}

	public static String doPost(String url, String params) throws Exception {

		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);// 閸掓稑缂揾ttpPost
    	httpPost.setHeader("Accept", "application/json");
    	httpPost.setHeader("Content-Type", "application/json");
    	String charSet = "UTF-8";
    	StringEntity entity = new StringEntity(params, charSet);
    	httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        String jsonString = null;
        try {

        	response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
            	HttpEntity responseEntity = response.getEntity();
            	jsonString = EntityUtils.toString(responseEntity);
            }
            else{
				/* logger.error("请求错误:"+state+"("+url+")");*/
            	HttpEntity responseEntity = response.getEntity();
            	jsonString = EntityUtils.toString(responseEntity);
			}
        }
        finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
        return jsonString;
	}

}
