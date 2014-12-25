package com.tyf.smartsms;

import java.io.Serializable;
import java.util.Map;

public class SerializableMap implements Serializable {
	
	private Map<String,String[]> map;  
	
    public Map<String,String[]> getMap(){  
        return map;  
    }  
    
    public void setMap(Map<String,String[]> m){  
        map = m;  
    }  
}
