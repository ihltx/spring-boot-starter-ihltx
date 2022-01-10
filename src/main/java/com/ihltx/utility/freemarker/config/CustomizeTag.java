package com.ihltx.utility.freemarker.config;

import lombok.Data;

import java.util.Map;

@Data
public class CustomizeTag {
    private String clazz;
    private Map<String,String> autowireds;
}
