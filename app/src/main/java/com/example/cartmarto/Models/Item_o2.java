package com.example.cartmarto.Models;

import com.example.cartmarto.Models.Item_h;

public class Item_o2 {

    String str_item_id, str_count, str_category;

    public Item_o2(String str_item_id, String str_count, String str_category) {
        this.str_item_id = str_item_id;
        this.str_count = str_count;
        this.str_category = str_category;
    }

    public Item_o2() {
    }

    public String getStr_category() {
        return str_category;
    }

    public void setStr_category(String str_category) {
        this.str_category = str_category;
    }

    public String getStr_item_id() {
        return str_item_id;
    }

    public void setStr_item_id(String str_item_id) {
        this.str_item_id = str_item_id;
    }

    public String getStr_count() {
        return str_count;
    }

    public void setStr_count(String str_count) {
        this.str_count = str_count;
    }
}
