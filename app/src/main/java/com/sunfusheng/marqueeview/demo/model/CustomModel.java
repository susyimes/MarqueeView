package com.sunfusheng.marqueeview.demo.model;

import com.sunfusheng.marqueeview.IMarqueeItem;

/**
 * @author by sunfusheng on 2019-04-25
 */
public class CustomModel implements IMarqueeItem {

    public int id;
    public Object title;
    public Object content;

    public CustomModel(int id, Object title, Object content) {
        this.id = id;
        this.title = title;
        this.content = content;
    }

    @Override
    public CharSequence marqueeMessage() {
//        if (title instanceof CharSequence){
//            return
//        }
        return title + "\n" + content;
    }
}
