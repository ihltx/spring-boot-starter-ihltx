package com.ihltx.utility.urlrewrite;

import com.ihltx.utility.urlrewrite.config.UrlRewriteConfig;
import com.ihltx.utility.util.FileUtil;
import com.ihltx.utility.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.tuckey.web.filters.urlrewrite.Conf;
import org.tuckey.web.filters.urlrewrite.UrlRewriteFilter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@EnableConfigurationProperties(UrlRewriteConfig.class)
@ConditionalOnProperty(prefix = "ihltx.urlrewrite", name = "path" , matchIfMissing = false)
public class UrlRewriteAutoConfiguration extends UrlRewriteFilter {
    @Autowired
    private UrlRewriteConfig urlRewriteConfig;

    // Override the loadUrlRewriter method, and write your own implementation
    protected void loadUrlRewriter(FilterConfig filterConfig) throws ServletException {
        InputStream fis = null;
        try {
            if(urlRewriteConfig.getPath().contains("classpath:")){
                //类路径下
                String fileName = StringUtil.trim(urlRewriteConfig.getPath().replaceAll("classpath:[ ]*" , ""));
                fis = this.getClass().getResourceAsStream(fileName);
            }else{
                //非类路径下
                if(!FileUtil.isFile(urlRewriteConfig.getPath())){
                    throw  new IOException(urlRewriteConfig.getPath() + " not found.");
                }
                fis = new FileInputStream(urlRewriteConfig.getPath());
            }
            Conf conf = new Conf(filterConfig.getServletContext(), fis, urlRewriteConfig.getPath(),
                    "@@traceability@@");
            checkConf(conf);
        } catch (IOException ex) {
            ex.printStackTrace();
//            throw new ServletException("Unable to load URL rewrite configuration file from " + fileName, ex);
        }finally {
            if(fis!=null){
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
