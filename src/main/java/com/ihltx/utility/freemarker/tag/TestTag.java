package com.ihltx.utility.freemarker.tag;

import java.io.IOException;
import java.util.Map;

import com.ihltx.utility.util.entity.Result;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

@Component
public class TestTag implements TemplateDirectiveModel {

	 //定义<@blogTag>标签中的参数名称
    private static final String pageSize = "pageSize";
    
    
    public Object recentBlog(String pageSize){
    	Result res[]=null;
        if(!StringUtils.isEmpty(pageSize)){
        	res = new Result[Integer.valueOf(pageSize)];
        }else {
        	res = new Result[10];
        }
    	for(int i=0;i<res.length;i++) {
    		res[i] = new Result(i+"", "msg " + i);
    	}
        return res;
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
       String pageSize = map.get(TestTag.pageSize).toString();
       environment.setVariable("recentBlog", getModel(recentBlog(pageSize)));
       //遇到一个坑，如果页面是这样写的<@blogTag  method="recentBlog"  pageSize="3" ></@blogTag>
       //中间没有任何内容，这里会一直报空指针异常
       templateDirectiveBody.render(environment.getOut());
   }

   private DefaultObjectWrapper getBuilder() {
       return new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_25).build();
   }

   private TemplateModel getModel(Object o) throws TemplateModelException {
       return this.getBuilder().wrap(o);
   }
	
}
