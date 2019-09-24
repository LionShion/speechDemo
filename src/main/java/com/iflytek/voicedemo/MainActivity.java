package com.iflytek.voicedemo;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.SpeechUtility;
import com.iflytek.voicedemo.faceonline.OnlineFaceDemo;
import com.iflytek.voicedemo.vocalverify.VocalVerifyDemo;

import static com.iflytek.speech.setting.UrlSettings.PREFER_NAME;

/**
 * MainActivity
 *
 * @blame Android Team
 */
public class MainActivity extends Activity implements OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private Toast mToast;
    private final int URLREQUESTCODE = 0X001;

    @Override
    @SuppressLint("ShowToast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
        TextView editText = findViewById(R.id.edit_text);
        StringBuffer buf = new StringBuffer();
        buf.append("当前APPID为：")
                .append(getString(R.string.app_id))
                .append("\n")
                .append(getString(R.string.example_explain));
        editText.setText(buf);
        requestPermissions();
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        //mscInit(null);//采用sdk默认url
        SimpleAdapter listItemAdapter = new SimpleAdapter();
        ((ListView) findViewById(R.id.listview_main)).setAdapter(listItemAdapter);


    }

    @Override
    public void onClick(View view) {
        int tag = Integer.parseInt(view.getTag().toString());
        Intent intent = null;
        switch (tag) {
            case 0:
                // 语音转写
                intent = new Intent(MainActivity.this, IatDemo.class);
                break;
            case 1:
                // 语法识别
                intent = new Intent(MainActivity.this, AsrDemo.class);
                break;
            case 2:
                // 语音合成
                intent = new Intent(MainActivity.this, TtsDemo.class);
                break;
            case 3:
                // 语音评测
                intent = new Intent(MainActivity.this, IseDemo.class);
                break;
            case 4:
                // 声纹
                intent = new Intent(MainActivity.this, VocalVerifyDemo.class);
                break;
            case 5:
                intent = new Intent(MainActivity.this, OnlineFaceDemo.class);
                break;
            default:
                break;
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    /**
     * Menu 列表
     * ,"重置域名"
     */
    String[] items = {"立刻体验语音听写", "立刻体验语法识别",  "立刻体验语音合成",
            "立刻体验语音评测",  "立刻体验声纹密码", "立刻体验人脸识别"};

    private class SimpleAdapter extends BaseAdapter {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                LayoutInflater factory = LayoutInflater.from(MainActivity.this);
                @SuppressLint("InflateParams") View mView = factory.inflate(R.layout.list_items, null);
                convertView = mView;
            }

            Button btn = (Button) convertView.findViewById(R.id.btn);
            btn.setOnClickListener(MainActivity.this);
            btn.setTag(position);
            btn.setText(items[position]);

            return convertView;
        }

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
    private void requestPermissions() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.LOCATION_HARDWARE, Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.WRITE_SETTINGS, Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS}, 0x0010);
                }

                if (permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION}, 0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void mscUninit() {
        if (SpeechUtility.getUtility() != null) {
            SpeechUtility.getUtility().destroy();
            try {
                Thread.sleep(40);
            } catch (InterruptedException e) {
                Log.w(TAG, "msc uninit failed" + e.toString());
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (URLREQUESTCODE == requestCode) {
            Log.d(TAG, "onActivityResult>>");
            try {
                SharedPreferences pref = getSharedPreferences(PREFER_NAME, MODE_PRIVATE);
                String serverUrl = pref.getString("url_preference", "");
                String domain = pref.getString("url_edit", "");
                Log.d(TAG, "onActivityResult>>domain = " + domain);
                if (!TextUtils.isEmpty(domain)) {
                    serverUrl = "http://" + domain + "/msp.do";
                }
                Log.d(TAG, "onActivityResult>>serverUrl = " + serverUrl);
                mscUninit();
                Thread.sleep(40);
                //mscInit(serverUrl);
            } catch (Exception e) {
                showTip("reset url failed");
            }

        }
    }
}
