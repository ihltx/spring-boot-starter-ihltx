package com.ihltx.utility.ueditor.controller;

import com.ihltx.utility.ueditor.ActionEnter;
import com.ihltx.utility.ueditor.config.UeditorConfig;
import com.ihltx.utility.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;

@Controller
@RequestMapping("ueditor")
public class UeditorController {

    @Autowired
    private UeditorConfig ueditorConfig;

    @ResponseBody
    @RequestMapping("config")
    public String config(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String rootPath = ueditorConfig.getRootPath().replaceAll("\\\\","/");
        if(rootPath.contains("classpath:")){
            rootPath = StringUtil.ltrim(StringUtil.trim(rootPath.replaceAll("classpath:" , "")),"/");
            URL url = this.getClass().getResource("/" +rootPath);
            if(url!=null){
                rootPath = url.getPath();
            }else{
                throw new Exception(rootPath + " not found.");
            }
        }
        return new ActionEnter( request, rootPath ).exec();
    }
}
