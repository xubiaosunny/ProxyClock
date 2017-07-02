package xubiao.proxyclock;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import java.net.*;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import android.app.*;
import xubiao.proxyclock.utils.NetUtil;
import android.webkit.WebView;
import android.content.Context;
import org.json.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String mUserAgent;
    String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button btn_login = (Button) findViewById(R.id.button_login);
        Button btn_clock = (Button) findViewById(R.id.button_clock);
        Button btn_info = (Button) findViewById(R.id.button_info);
        btn_login.setOnClickListener(new MyListener());
        btn_clock.setOnClickListener(new MyListener());
        btn_info.setOnClickListener(new MyListener());

        WebView mWebView = new WebView(this);
        mUserAgent =mWebView.getSettings().getUserAgentString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            TextView textusername = (TextView) findViewById(R.id.text_username);
            String username = textusername.getText().toString();
            if (username.equals("biao.xu") || username.equals("wenzhe.hao")){
                switch (view.getId()){
                    case R.id.button_login:
                        login();
                        break;
                    case R.id.button_clock:
                        clock();
                        break;
                    case R.id.button_info:
                        getInfo();
                        break;
                }
            }
        }
    }

    public void login(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // url
                String url = "http://192.168.100.169/ws/getToken";
                // data
                TextView textusername = (TextView) findViewById(R.id.text_username);
                TextView textpassword = (TextView) findViewById(R.id.text_password);
                String username = textusername.getText().toString();
                String password = textpassword.getText().toString();
                JSONStringer jsonText = new JSONStringer();
                try {
                    jsonText.object();
                    jsonText.key("username");
                    jsonText.value(username);
                    jsonText.key("password");
                    jsonText.value(password);
                    jsonText.endObject();
                } catch (JSONException ex) {
                    throw new RuntimeException(ex);
                }
                String dataStr = jsonText.toString();
                // header
                HashMap<String,String> headers = new HashMap<String,String>();
                headers.put("Host", "192.168.100.169");
                headers.put("Connection", "keep-alive");
                headers.put("Content-Length", String.valueOf(dataStr.length()));
                headers.put("Accept", "application/json, text/plain, */*");
                headers.put("Origin", "file://");
                headers.put("User-Agent", mUserAgent);
                headers.put("Content-Type", "application/json;charset=UTF-8");
                headers.put("Accept-Encoding", "gzip, deflate");
                headers.put("Accept-Language", "zh-cn-#hans");

                final String state=NetUtil.doPost(url,dataStr.toString(),headers);
                //执行在主线程上
                runOnUiThread(new Runnable() {
                    public void run() {
                        //就是在主线程上操作,弹出结果
                        TextView textmessage = (TextView) findViewById(R.id.text_message);
                        textmessage.setText(textmessage.getText(), TextView.BufferType.EDITABLE);
                        System.out.println(state);
                        try {
                            JSONObject jsonObject = new JSONObject(state);
                            token = jsonObject.getString("TOKEN");
                            textmessage.append("Token: " + token + "\n");
                        } catch (Exception e) {
                            e.printStackTrace();
                            textmessage.append(e.toString() + "\n");
                        }
                        textmessage.append("-------------------------" + "\n");

                        getNotice();
                        getInfo();
                    }
                });

            }
        }).start();
    }

    public void clock(){
        if(token==null){
            TextView textmessage = (TextView) findViewById(R.id.text_message);
            textmessage.setText(textmessage.getText(), TextView.BufferType.EDITABLE);
            textmessage.append("please auth first" + "\n");
            textmessage.append("-------------------------" + "\n");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                // url
                String url = "http://192.168.100.169/ws/saveCheckInfo";
                // data
//                JSONStringer jsonText = new JSONStringer();
//                try {
//                    jsonText.object();
//                    jsonText.key("SLOC");
//                    ArrayList l = new ArrayList();
//                    l.add("电通创意广场");
//                    jsonText.value(l);
//                    jsonText.key("ISAPP");
//                    jsonText.value("1");
//                    jsonText.endObject();
//                } catch (JSONException ex) {
//                    throw new RuntimeException(ex);
//                }
//                String dataStr = jsonText.toString();
                String dataStr = "{\"SLOC\":[\"电通创意广场\"],\"ISAPP\":\"1\"}";
                // header
                HashMap<String,String> headers = new HashMap<String,String>();
                headers.put("Host", "192.168.100.169");
                headers.put("Connection", "keep-alive");
                headers.put("Content-Length", "43");
                headers.put("Accept", "application/json, text/plain, */*");
                headers.put("Origin", "file://");
                headers.put("User-Agent", mUserAgent);
                headers.put("Authorization", token);
                headers.put("Content-Type", "application/json;charset=UTF-8");
                headers.put("Accept-Encoding", "gzip, deflate");
                headers.put("Accept-Language", "zh-cn-#hans");

                final String state=NetUtil.doPost(url,dataStr.toString(),headers);
                //执行在主线程上
                runOnUiThread(new Runnable() {
                    public void run() {
                        //就是在主线程上操作,弹出结果
                        TextView textmessage = (TextView) findViewById(R.id.text_message);
                        textmessage.setText(textmessage.getText(), TextView.BufferType.EDITABLE);
                        textmessage.append(state + "\n");
                        getNotice();
                        getInfo();
                    }
                });

            }
        }).start();
    }

    public void getInfo(){
        if(token==null){
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                // url
                String url = "http://192.168.100.169/ws/getCheckInfo";
                // header
                HashMap<String,String> headers = new HashMap<String,String>();
                headers.put("Host", "192.168.100.169");
                headers.put("Connection", "keep-alive");
                headers.put("Accept", "application/json, text/plain, */*");
                headers.put("Authorization", token);
                headers.put("User-Agent", mUserAgent);
                headers.put("Accept-Encoding", "gzip, deflate");
                headers.put("Accept-Language", "zh-cn-#hans");

                final String state=NetUtil.doGet(url,headers);
                //执行在主线程上
                runOnUiThread(new Runnable() {
                    public void run() {
                        //就是在主线程上操作,弹出结果
                        TextView textmessage = (TextView) findViewById(R.id.text_message);
                        textmessage.setText(textmessage.getText(), TextView.BufferType.EDITABLE);
                        textmessage.append(state +"\n");
                        textmessage.append("-------------------------" + "\n");
                    }
                });

            }
        }).start();
    }

    public void getNotice(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // url
                String url = "http://192.168.100.169/ws/getNotice";
                // header
                HashMap<String,String> headers = new HashMap<String,String>();
                headers.put("Host", "192.168.100.169");
                headers.put("Connection", "keep-alive");
                headers.put("Accept", "application/json, text/plain, */*");
                headers.put("Authorization", token);
                headers.put("User-Agent", mUserAgent);
                headers.put("Accept-Encoding", "gzip, deflate");
                headers.put("Accept-Language", "zh-cn-#hans");

                final String state=NetUtil.doGet(url,headers);
                //执行在主线程上
                runOnUiThread(new Runnable() {
                    public void run() {
                        //就是在主线程上操作,弹出结果
                        TextView textmessage = (TextView) findViewById(R.id.text_message);
                        textmessage.setText(textmessage.getText(), TextView.BufferType.EDITABLE);
                        textmessage.append(state + "\n");
                        textmessage.append("-------------------------" + "\n");
                    }
                });

            }
        }).start();
    }
}
