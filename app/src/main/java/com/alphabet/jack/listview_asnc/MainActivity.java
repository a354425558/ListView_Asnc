package com.alphabet.jack.listview_asnc;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * ListView分页加载数据(只有分页加载,没有下拉刷新)
 */
public class MainActivity extends Activity implements AdapterView.OnItemClickListener{

    ListView listView;
    List<String> data =  new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    View foot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView)findViewById(R.id.listView1);

        listView.setOnScrollListener(onScrollListener);//监听滚动事件

        foot = ((LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.listview_foot,null);
        data.addAll(DataBean.getListData(0,20));
        arrayAdapter = new ArrayAdapter<String>(this,R.layout.item,R.id.text,data);
        listView.addFooterView(foot);
        listView.setAdapter(arrayAdapter);
        listView.removeFooterView(foot);

        listView.setOnItemClickListener(this);

    }
    private int totalPage = 5;//总共有多少页
    private int nummber = DataBean.getListData(0,20).size()/totalPage;//每次获取多少条数据
    private boolean isFinish = true;
    /**
     * ListView滚动中d监听
     */
    private AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {

//            Log.i("MainActivity", "onScrollStateChanged滚动状态改变：\n"+ view +"\t" + scrollState);
        }

        /**
         * @param view
         * @param firstVisibleItem 可见的第一个
         * @param visibleItemCount 可见的数量
         * @param totalItemCount 当前总数量
         */
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,  int totalItemCount) {
//         Log.i("MainActivity", "onScroll正在滚动中：\n" + firstVisibleItem +
//                 "\t" + visibleItemCount +"\t" + totalItemCount);

            final int mtotalItemCount = totalItemCount;
            int lastVisblePosition = listView.getLastVisiblePosition();//屏幕上最后一条索引的id
            if ((lastVisblePosition +1) == totalItemCount){//到当前页数据的最后一条记录
                Log.i("MainActivity","屏幕最后可见的是："+ (lastVisblePosition +1) );
                Log.i("MainActivity","当前总数是："+ totalItemCount);
                if (totalItemCount > 0){
                    int currentPage = totalItemCount % nummber ==0 ?
                            totalItemCount % nummber:totalItemCount % nummber +1;
                    int nextPage = currentPage +1;

//                    Log.i("MainActivity","当前页是："+ currentPage );
//                    Log.i("MainActivity","下一页是："+ nextPage );
                    if (nextPage <= 5 && isFinish){
                        if (nextPage ==5)
                        Toast.makeText(MainActivity.this,"呵呵" , Toast.LENGTH_SHORT).show();
                        isFinish = false;
                        listView.addFooterView(foot);
                        //在这里异步加载网络数据
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
         handler.sendMessage(handler.obtainMessage(100,DataBean.getListData(mtotalItemCount,20)));
                            }
                        }).start();
                    }
                }

            }

        }
    };
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 100){

                data.addAll((List<String>)msg.obj);

                arrayAdapter.notifyDataSetChanged();

                if (listView.getFooterViewsCount() > 0){
                    listView.removeFooterView(foot);
                }
                isFinish = true;
            }
        }
    };

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, AdActivity.class);
        startActivity(intent);
    }
}
