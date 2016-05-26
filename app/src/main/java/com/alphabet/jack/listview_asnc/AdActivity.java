package com.alphabet.jack.listview_asnc;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.iflytek.voiceads.AdError;
import com.iflytek.voiceads.IFLYAdListener;
import com.iflytek.voiceads.IFLYAdSize;
import com.iflytek.voiceads.IFLYBannerAd;

/**
 *  横幅_旗帜广告
 * Created by Jack on 2016/5/19.
 */
public class AdActivity extends Activity{
    private LinearLayout layout_ads;
    private IFLYBannerAd bannerView;
    private TextView mTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        mTextView = ((TextView)findViewById(R.id.txtView_tip));
        createBannerAd();

        mTextView.setText("requesting");
    }

    public void createBannerAd() {
        //创建旗帜广告，传入广告位ID
        // demo:4F97BF180932EB2EC6C765F34C87552E
        //  My : CD1F9AED2BEDDE38BA5ED44D2C698020
        String adUnitId = "CD1F9AED2BEDDE38BA5ED44D2C698020";

        bannerView = IFLYBannerAd.createBannerAd(this, adUnitId);
        //设置请求的广告尺寸
        bannerView.setAdSize(IFLYAdSize.BANNER);
        //设置下载广告前，弹窗提示
//		bannerView.setParameter(AdKeys.DOWNLOAD_ALERT, "true");

        //请求广告，添加监听器
        bannerView.loadAd(mAdListener);
        //将广告添加到布局
        layout_ads = (LinearLayout)findViewById(R.id.layout_adview);
        layout_ads.removeAllViews();
        layout_ads.addView(bannerView);

    }

    IFLYAdListener mAdListener = new IFLYAdListener(){

        /**
         * 广告请求成功
         */
        @Override
        public void onAdReceive() {
            //展示广告
            bannerView.showAd();

            mTextView.setText("success");
            Log.d("Ad_Android_Demo", "onAdReceive");
        }

        /**
         * 广告请求失败
         */
        @Override
        public void onAdFailed(AdError error) {
            mTextView.setText("failed:"+error.getErrorCode()+","+
                    error.getErrorDescription());
            Log.d("Ad_Android_Demo", "onAdFailed");
        }

        /**
         * 广告被点击
         */
        @Override
        public void onAdClick() {
            mTextView.setText("ad click");
            Log.d("Ad_Android_Demo", "onAdClick");
        }

        /**
         * 广告被关闭
         */
        @Override
        public void onAdClose() {
            mTextView.setText("ad close");
            Log.d("Ad_Android_Demo", "onAdClose");
        }

        @Override
        public void onAdExposure() {
            // TODO Auto-generated method stub

        }
    };
}
