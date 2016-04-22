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
    public static List<String> getListData_UP(int offset,int maxResult){//分页
        List<String> listData = new ArrayList<String>();
        for (int i = 0; i <8 ; i++) {
            listData.add("上拉数据:" + i);
        }
        return listData;
    }

    /**
     * 下拉数据
     * @param offset
     * @param maxResult
     * @return
     */
    public static List<String> getListData_DOWN(int offset,int maxResult){//分页
        List<String> listData = new ArrayList<String>();
        for (int i = 0; i < 8 ; i++) {
            listData.add("下拉数据:" + i);
        }
        return listData;
    }

}
