package com.alphabet.jack.listview_asnc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jack on 2016/4/20.
 */
public class DataBean {
    //获取分页
    /**
     * @param offset 偏移量
     * @param maxResult 最大数据
     * @return
     */
    public static List<String> getListData(int offset,int maxResult){//分页
        List<String> listData = new ArrayList<String>();
        for (int i = 0; i <60 ; i++) {
            listData.add("item的数据是:" + i);
        }
        return listData;
    }
}
