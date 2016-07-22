package com.damao.scrolltextview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        ScrollTextView st = (ScrollTextView) findViewById(R.id.text);
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<100;i++){
            sb.append("测试了"+i+","+i+"------>");
            sb.append("\n");
        }
        st.setText(sb);
    }
}
