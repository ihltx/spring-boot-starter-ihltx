package com.ihltx.utility.freemarker.tag;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;


import com.ihltx.utility.i18n.service.I18nUtil;
import com.ihltx.utility.util.StringUtil;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LanguageTag implements TemplateDirectiveModel {


	@Autowired
	private I18nUtil i18nUtil;
	public I18nUtil getI18nUtil() {
		return i18nUtil;
	}

	public void setI18nUtil(I18nUtil i18nUtil) {
		this.i18nUtil = i18nUtil;
	}

	

	/**
    *
    * @param environment 运行的环境
    * @param map 方法参数map  方法名和值
    * @param templateModels
    * @param templateDirectiveBody 输出形式
    * @throws TemplateException
    * @throws IOException
    */
   @SuppressWarnings("rawtypes")
   @Override
   public void execute(Environment environment, Map map, TemplateModel[] templateModels, TemplateDirectiveBody templateDirectiveBody) throws TemplateException, IOException {
	   String code = map.get("code").toString();
	   String[] params = null;
	   if(map.containsKey("params") && map.get("params")!=null) {
		   params =  map.get("params").toString().split(",");
	   }
	   String message = StringUtil.formatString(code, params);
	   if(i18nUtil!=null){
		   message =i18nUtil.getMessage(code, params);
	   }
	   Writer out = environment.getOut();
	   out.write(message);
   }

}
