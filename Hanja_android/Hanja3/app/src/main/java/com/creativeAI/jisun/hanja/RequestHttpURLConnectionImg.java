package com.creativeAI.jisun.hanja;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RequestHttpURLConnectionImg {
    public String request(String _cmd, String _user, String _theme, String _word, String _img_path, String _img_name) {
        String cmd = _cmd;
        String user = _user;
        String theme = _theme;
        String word = _word;
        String img_path = _img_path;
        String img_name = _img_name;

        File img_file = null;
        img_file = new File(img_path+"/"+img_name);

        if(!img_file.isFile()) return "파일이 존재하지 않습니다.";

        String border = "----7MA4YWxkTrZu0gW";
        String lineEnd = "\r\n";
        String twoHyphens = "--";

        URL url = null;
        HttpURLConnection conn = null;
        HttpURLConnection re_conn = null;
        DataOutputStream dos = null;
        FileInputStream fis = null;

        try{
            // 1. URL 연결(웹페이지 URL 연결)
            url = new URL("http://175.123.138.125:8000/obj_detect");
            conn = (HttpURLConnection) url.openConnection();

            // 2. conn 설정
            conn.setDoInput(true);         // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            conn.setDoOutput(true);        // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            //urlConn.setConnectTimeout(10000);   // TimeOut 시간 (서버 접속시 연결 시간)
            //urlConn.setReadTimeout(15000);
            conn.setRequestMethod("POST");                               // URL 요청 방식 설정 : POST.
            conn.setRequestProperty("Accept-Charset", "UTF-8");      // 입력받은 데이터를 서버에서 처리할 수 있는 문자셋 설정.
            conn.setRequestProperty("Accept", "application/text");    // 서버 Response Data를 JSON 형식의 타입으로 요청.
            conn.setRequestProperty("Cache-Control", "no-cache");     // 컨트롤 캐쉬 설정
            conn.setRequestProperty("Connection", "Keep-Alive");      // HTTP를 한 번 연결하고 끊고, 재연결시 재사용 할 수 있도록 한다. (웹서버에서 지원시에만 가능)
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + border);
            conn.connect();

            // 3. Request Body에 Data 담기.
            dos = new DataOutputStream(conn.getOutputStream());

            // 3-1. 텍스트 전송(cmd)
            dos.writeBytes(twoHyphens + border + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"cmd\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(cmd);
            dos.writeBytes(lineEnd);

            // 3-2. 텍스트 전송(user)
            dos.writeBytes(twoHyphens + border + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"user\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(user);
            dos.writeBytes(lineEnd);

            // 3-3. 텍스트 전송(theme)
            dos.writeBytes(twoHyphens + border + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"theme\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(theme);
            dos.writeBytes(lineEnd);

            // 3-4. 텍스트 전송(word)
            dos.writeBytes(twoHyphens + border + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"word\"" + lineEnd);
            dos.writeBytes(lineEnd);
            dos.writeBytes(word);
            dos.writeBytes(lineEnd);

            Log.d("cmd,user,theme,word : ", "업로드 완료");

            // 3-5. 이미지 파일 전송
            dos.writeBytes(twoHyphens + border + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"img\"; filename=" + img_name + lineEnd);
            dos.writeBytes("Content-Type: image/jpeg" + lineEnd);
            dos.writeBytes(lineEnd);

//            fis = new FileInputStream(img_path + "/" + img_name);
            fis = new FileInputStream(img_file);
            int bytesAvailable = fis.available();
            int maxBufferSize = 1024 * 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);  // 인자 중 최솟값 반환
            byte[] buffer = new byte[bufferSize];

            int bytesRead = fis.read(buffer, 0, bufferSize);
            // 0이 되면(더이상 읽을 게 없으면) while 문 벗어남
            while (bytesRead > 0) {   // 파일 업로드 부분
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fis.available();  // 현재 읽을 수 있는 바이트 수
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fis.read(buffer, 0, bufferSize);
            }
//            dos.writeBytes(lineEnd + twoHyphens + border + twoHyphens + lineEnd);
            dos.writeBytes(lineEnd + twoHyphens + border + twoHyphens);
            dos.flush();

            Log.d("img : ", "업로드 완료");

            // 4. 서버에서 응답 받기
            int resultcode = conn.getResponseCode();

            // 4-1. 서버의 응답이 200이 아닌경우
            if (resultcode != HttpURLConnection.HTTP_OK){
                Log.d("resultCode : ", Integer.toString(resultcode));
                return "Server status : " + Integer.toString(resultcode);
            }

            // 4-2. 서버의 응답이 200인 경우
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            Log.d("resultCode : ", Integer.toString(resultcode));

            String line;
            String page = "";

            while ((line = reader.readLine()) != null) {
                page += line;
            }
            return page;
        } catch (MalformedURLException e) {  // url 오류
            e.printStackTrace();
        } catch (IOException e) {  // connection 오류
            e.printStackTrace();
        } finally {
            if (conn != null)
                try {
                    fis.close();
                    dos.close();
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            Log.d("Connection : ", "접속 해지");
        }
        return "Server error";
    }
}