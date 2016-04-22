package com.alphabet.jack.listview_asnc;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页加载
 */
public class MainActivity extends Activity implements RefreshListView.OnRefreshListener {

    RefreshListView listView;
    List<String> data = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    private int totalPage = 5;//总共有多少页
    private int nummber = DataBean.getListData_DOWN(0,20).size()/totalPage;//每次获取多少条数据

    private static final int UP = 1;
    private static final int DOWN = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (RefreshListView)findViewById(R.id.listView1);
        listView.setOnRefreshListener(this);
        showListView();
    }

    private void showListView(){
        if (arrayAdapter == null){
            arrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                    R.layout.item,R.id.text, DataBean.getListData_UP(0,1) );
            listView.setAdapter(arrayAdapter);
        }else
        arrayAdapter.notifyDataSetChanged();

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what){
                case UP:
                    listView.loadComplete();
                    for (String string: DataBean.getListData_UP(0,1)) {
                        arrayAdapter.add(string);
                    }
                    arrayAdapter.notifyDataSetChanged();

                    break;
                case DOWN:
                    listView.refreshComplete();
                    arrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                            R.layout.item,R.id.text, DataBean.getListData_DOWN(0,1) );
                    listView.setAdapter(arrayAdapter);

                    break;
            }

        }
    };

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(handler.obtainMessage(DOWN,DataBean.getListData_DOWN(0,1)));

            }
        }).start();
    }

    @Override
    public void onLoad() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
              handler.sendMessage(handler.obtainMessage(UP,DataBean.getListData_UP(0,1)));

            }
        }).start();

    }
}
