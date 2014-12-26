package com.tyf.smartsms;

import java.io.Serializable;
import java.util.Map;

public class SerializableMap implements Serializable { //SerializableMap类实现Serializable接口，用于将选择的联系人信息返回给上一级Activity

    private Map<String,String[]> map;

    public Map<String,String[]> getMap(){
        return map;
    }

    public void setMap(Map<String,String[]> m){
        map = m;
    }
}
