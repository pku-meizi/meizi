package com.meiziaccess.CommandTool;

/**
 * Created by user-u1 on 2016/12/12.
 */
import com.meiziaccess.secure.AbstractSecuredController;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;


public class MyHttpUtil {

    /*
        默认的一些参数
    */
    public static final String ADD_URL = "http://61.48.40.126:8081/clientAppLogin";
    private static final String appid  = "vendor_system";
    private static final String appSecret  = "b62fdc77-3506-49bf-900f-93ac354fd23a";

    /**
     *
     * @param username
     * @param password
     * @return
     */
    public static JSONObject post( String username, String password) {
        JSONObject object = null;
        //生成签名
        String ts = String.valueOf(System.currentTimeMillis());
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("appid", appid);
        paramMap.put("ts", ts);
        String sign = "";
        try {
            sign = AbstractSecuredController.generateSign(paramMap, appid, appSecret);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            //创建连接
            URL url = new URL(ADD_URL);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type",
                    "application/json");

            connection.connect();

            //POST请求
            DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream());
            JSONObject obj = new JSONObject();
            obj.element("username", username);
            obj.element("password", password);
            obj.element("sign", sign);
            obj.element("appid", appid);
            obj.element("ts", ts);
            System.out.println(obj.toString());
            out.writeBytes(obj.toString());
            out.flush();
            out.close();

            //读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            System.out.println(sb.toString());
            object = JSONObject.fromObject(sb.toString());
            reader.close();
            // 断开连接
            connection.disconnect();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return object;
    }


//    public static void main(String[] args) {
//        JSONObject object = MyHttpUtil.post("lhq", "lhqlhq");
//        System.out.println(object.get("code"));
//    }

}