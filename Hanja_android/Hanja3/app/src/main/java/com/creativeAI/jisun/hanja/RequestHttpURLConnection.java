package com.creativeAI.jisun.hanja;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestHttpURLConnection {
    String tag;

    // url에 더할 태그 받아오기
    public RequestHttpURLConnection (String tag) {
        this.tag = tag;
    }

    public String request(JSONObject jsonObj){

        String jsonStr = jsonObj.toString();
        String result = "";

        OutputStream os = null;
        InputStream is = null;
        HttpURLConnection conn = null;
        URL url = null;

        try{
            // 1. URL 연결
            url = new URL("http://175.123.138.125:8000/" + tag);  // url 변경해라!!
            conn = (HttpURLConnection) url.openConnection();

            // 2. conn 설정
            conn.setDoInput(true);         // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            conn.setDoOutput(true);        // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            //urlConn.setConnectTimeout(10000);   // TimeOut 시간 (서버 접속시 연결 시간)
            //urlConn.setReadTimeout(15000);      // TimeOut 시간 (Read시 연결 시간)

            conn.setRequestMethod("POST");                               // URL 요청 방식 설정 : POST.
            conn.setRequestProperty("Accept-Charset", "UTF-8");      // 입력받은 데이터를 서버에서 처리할 수 있는 문자셋 설정.
            conn.setRequestProperty("Accept", "application/json");
            conn.setRequestProperty("Cache-Control", "no-cache");     // 컨트롤 캐쉬 설정
            conn.setRequestProperty("Connection", "Keep-Alive");      // HTTP를 한 번 연결하고 끊고, 재연결시 재사용(웹서버에서 지원시에만 가능)
            conn.setRequestProperty("Content-Type", "application/json");
            conn.connect();

            // 3. json 스트림 << NEW!
            os = conn.getOutputStream();
            os.write(jsonStr.getBytes("UTF-8"));
            os.flush();  // 출력 스트림 비우고 모든 출력 바이트 강제 실행
            int responseCode = conn.getResponseCode();

            // 4. response 받기
            if(responseCode != HttpURLConnection.HTTP_OK) {
                Log.d("resultCode", "is not 200");
                return "server status : " + responseCode;
            }

//            is = conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            // 출력물의 라인과 그 합에 대한 변수.
            String line;
            String page = "";

            // 서버에서 라인 단위로 읽는다.
            while ((line = reader.readLine()) != null){
                page += line;
            }
            return page;
        } catch (MalformedURLException e) { // for URL.
            e.printStackTrace();
        } catch (IOException e) { // for openConnection().
            e.printStackTrace();
        } finally {
            try {
                os.close();
                conn.disconnect();  // 접속해지
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("Connection", "접속 해지");
        }
        return "Server Error";
    }
}
