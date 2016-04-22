package com.alphabet.jack.listview_asnc;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jack on 2016/4/21.
 */
public class RefreshListView extends ListView implements AbsListView.OnScrollListener{

    public State state = State.NONE;
    //记录当前状态
    public enum  State{
        NONE,PULL,RELEASE,REFRESHING;
    }
    View header;
    int headerHeight;
    //ListView item个数
    int totalItemCount = 0;
    //最后可见的Item
    int lastVisibleItem = 0;
    //是否加载标示
    boolean isLoading = false;

    //标记当前滚动状态
    int scrollState;
    //屏幕第一个可见的view
    int firstVisibleItem;
    //标记当前位置是否在屏幕的第一个,是否刷新
    boolean isRefresh;
    //按下的Y
    double touchY;
    //移动中的Y
    double tempY;
    //停止移动的Y
    double stopY;
    //底部View
    private  View footerView;
    /**
     * 下拉刷新
     * 上拉加载更多
     */
    private OnRefreshListener onRefreshListener;
    public RefreshListView(Context context) {
        super(context);
        initView(context);
    }

    public RefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public RefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context){

        this.setOnScrollListener(this);
        /**
         * 增加头部
         */
        header = LayoutInflater.from(context).inflate(R.layout.top,null);
        measureView(header);//测量头部宽高
        headerHeight = header.getMeasuredHeight();

        //初始设置为隐藏
        topPadding(-headerHeight);
        //增加头部
        this.addHeaderView(header);

        /**
         * 增加页脚
         */
        LayoutInflater mInflater = LayoutInflater.from(context);
        footerView = mInflater.inflate(R.layout.listview_foot, null);
        footerView.setVisibility(View.GONE);
        //添加底部View
        this.addFooterView(footerView);

    }

    /**
     * 测量占位宽高
     */
    private void measureView(View view){
       ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null){
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
         int width = getChildMeasureSpec(0,0,p.width);

        int height;
        int tempHeight = p.height;
        //此处的作用是，如果自适应的高度为0，则测量高
        if (tempHeight > 0){
            height =  MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.EXACTLY);
        }else
             height = MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.UNSPECIFIED);

        view.measure(width,height);
    }

    /**
     *
     * @param topPadding 设置头部边距
     */
     public void topPadding(int topPadding){
         header.setPadding(header.getPaddingLeft(),topPadding,header.getPaddingRight(),header.getPaddingBottom());
         header.invalidate();
     }

    /**
     *
     * @param view
     * @param scrollState 滚动的状态
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;
        //当滑动到底端，并滑动状态为 not scrolling
        if(lastVisibleItem == totalItemCount && scrollState == SCROLL_STATE_IDLE){
            if(!isLoading){
                isLoading = true;
                //设置可见
                footerView.setVisibility(VISIBLE);
                //加载数据
                if (onRefreshListener != null)
                    onRefreshListener.onLoad();
            }
        }
    }

    /**
     *
     * @param view
     * @param firstVisibleItem 屏幕第一个可见的item
     * @param visibleItemCount 屏幕可见的数量
     * @param totalItemCount 总数量
     */

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;
        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;
//        Log.i("MainActivity", "onScroll滚动：\n"
//                + firstVisibleItem + "\t  " + visibleItemCount +"\t  " + totalItemCount);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (firstVisibleItem ==0){
                    isRefresh = true;
                    touchY = ev.getY();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;
            case MotionEvent.ACTION_UP:
                //
                //松开后
                if (state == State.RELEASE){
                    //正在刷新
                    reFreshViewState(state = State.REFRESHING);
                    if (onRefreshListener != null){
                        onRefreshListener.onRefresh();
                    }
                }else if (state == State.PULL){

                    isRefresh = false;
                    reFreshViewState(state =State.NONE);
                }
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     *
     * @param ev 移动过程
     */

    private void onMove(MotionEvent ev) {
        if (!isRefresh){
            return;
        }
        tempY = ev.getY();
        stopY = tempY - touchY;//从按下到移动的距离
        int movingTopPadding = (int)(stopY - headerHeight);
        switch (state){
            case NONE:
                if (stopY > 0){
                    //视图状态
                    reFreshViewState(state = State.PULL);
                }
                break;
            case PULL:
                topPadding(movingTopPadding);
                //移动的距离如果大于头部高度+50以上,则状态变为即将释放状态
                if (stopY > headerHeight + 50 && scrollState == SCROLL_STATE_TOUCH_SCROLL){  //状态?
                    //视图状态
                    reFreshViewState(state = State.RELEASE);
                }

                break;
            case RELEASE:
                topPadding(movingTopPadding);

                if (stopY < headerHeight + 30){
                    reFreshViewState(state = State.PULL);
                }else if(stopY <= 0) {
                    isRefresh = false;
                    reFreshViewState(state = State.NONE);
                }
                break;
            case REFRESHING:
                break;
        }

    }
    public RotateAnimation startAnimation(float fromDegress,float toDegress){
        //旋转180度
        RotateAnimation animation = new RotateAnimation(fromDegress,toDegress,
                RotateAnimation.RELATIVE_TO_SELF,0.5f,
                RotateAnimation.RELATIVE_TO_SELF,0.5f);

        animation.setDuration(500);
        animation.setFillAfter(true);
        return animation;

    }

    /**
     * 根据当前状态做出相应处理
     * @param state
     */
    private void reFreshViewState(State state) {
        TextView tip = (TextView) header.findViewById(R.id.tip);
        ImageView arrow = (ImageView) header.findViewById(R.id.arrow);
        ProgressBar progress = (ProgressBar) header.findViewById(R.id.progress_refresh);
        switch (state){
            case NONE:
                arrow.clearAnimation();
                topPadding(-headerHeight);
                break;
            case PULL:
                arrow.setVisibility(VISIBLE);
                progress.setVisibility(GONE);
                tip.setText("下拉刷新!");
                arrow.clearAnimation();
                arrow.setAnimation(startAnimation(0,180));
                break;
            case RELEASE:
                arrow.setVisibility(VISIBLE);
                progress.setVisibility(View.GONE);
                tip.setText("释放即将刷新!");
                arrow.clearAnimation();
                arrow.setAnimation(startAnimation(180,0));
                break;
            case REFRESHING:
                topPadding(50);
                arrow.setVisibility(View.GONE);
                progress.setVisibility(View.VISIBLE);
                tip.setText("正在刷新...");
                arrow.clearAnimation();
                break;
        }


    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener){
        this.onRefreshListener = onRefreshListener;

    }

    /**
     * 数据加载完成
     */
    public void refreshComplete(){
        isRefresh = false;
        reFreshViewState( state = State.NONE);
        TextView lastupdatetime = (TextView)header.findViewById(R.id.lastupdate_time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String time = format.format(date);
        lastupdatetime.setText(time);
    }

    /**
     * 上拉数据加载完成
     */
    public void loadComplete(){
        footerView.setVisibility(View.GONE);
        isLoading = false;
        this.invalidate();
    }

    public interface OnRefreshListener{
        void onRefresh();
        void onLoad();
    }

}
