package com.ihltx.utility.mail.service;

import com.ihltx.utility.mail.config.MailConfig;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.io.File;
import java.util.Map;

public interface JavaMailSenderUtil {

    /**
     * 获取邮件配置
     * @return
     */
    MailConfig getMailConfig();


    /**
     * 设置邮件配置
     * @param mailConfig
     */
    void setMailConfig(MailConfig mailConfig);

    /**
     * 获取 JavaMailSenderImpl 组件
     * @return
     */
    JavaMailSenderImpl getJavaMailSenderImpl();

    /**
     * 设置 JavaMailSenderImpl 组件
     * @return
     */
    void setJavaMailSenderImpl(JavaMailSenderImpl javaMailSenderImpl);

    /**
     * 发送简单内容邮件
     * @param subject           邮件主题
     * @param to                收件人，多个收件人以,间隔
     * @param from              发件人，允许以,间隔同时指定邮箱及姓名，格式如： 84611913@qq.com,刘林
     * @param content           内容
     * @return  true  --  成功  false -- 失败
     */
    Boolean send(String subject, String to, String from, String content);

    /**
     * 发送邮件，允许指定内容为html
     * @param subject           邮件主题
     * @param to                收件人，多个收件人以,间隔
     * @param from              发件人，允许以,间隔同时指定邮箱及姓名，格式如： 84611913@qq.com,刘林
     * @param content           内容
     * @param isHtml            是否html内容
     * @return  true  --  成功  false -- 失败
     */
    Boolean send(String subject, String to, String from, String content, Boolean isHtml);

    /**
     * 发送邮件，允许指定内容为html，允许指定附件
     * @param subject           邮件主题
     * @param to                收件人，多个收件人以,间隔
     * @param from              发件人，允许以,间隔同时指定邮箱及姓名，格式如： 84611913@qq.com,刘林
     * @param content           内容
     * @param isHtml            是否html内容
     * @param attachments        附件File数组
     * @return  true  --  成功  false -- 失败
     */
    Boolean send(String subject, String to, String from, String content, Boolean isHtml, File[] attachments);


    /**
     * 发送邮件，允许指定内容为html，允许指定附件，允许指定正文图片
     *
     * @param subject           邮件主题
     * @param to                收件人，多个收件人以,间隔
     * @param from              发件人，允许以,间隔同时指定邮箱及姓名，格式如： 84611913@qq.com,刘林
     * @param content           内容
     * @param isHtml            是否html内容
     * @param attachments       附件File数组
     * @param bodyImages        正文图片列表，格式：
     *                          {
     *                              "正文引用名称": File,
     *                              ...,
     *                              "正文引用名称": File
     *                          }
     *            正文引用格式：<img src='cid:正文引用名称'></img>
     * @return  true  --  成功  false -- 失败
     */
    Boolean send(String subject, String to, String from, String content, Boolean isHtml, File[] attachments, Map<String, File> bodyImages);


    /**
     * 发送邮件，允许指定内容为html，允许指定附件，允许指定正文图片
     *
     * @param subject           邮件主题
     * @param to                收件人，多个收件人以,间隔
     * @param cc                抄送收件人,多个收件人以;间隔
     * @param bcc               暗送收件人,多个收件人以;间隔
     * @param from              发件人，允许以,间隔同时指定邮箱及姓名，格式如： 84611913@qq.com,刘林
     * @param content           内容
     * @param isHtml            是否html内容
     * @param attachments       附件File数组
     * @param bodyImages        正文图片列表，格式：
     *                          {
     *                              "正文引用名称": File,
     *                              ...,
     *                              "正文引用名称": File
     *                          }
     *            正文引用格式：<img src='cid:正文引用名称'></img>
     * @return  true  --  成功  false -- 失败
     */
    Boolean send(String subject, String to, String cc, String bcc, String from, String content, Boolean isHtml, File[] attachments, Map<String, File> bodyImages);

    /**
     * 基于配置中的发件人信息,发送简单内容邮件
     * @param subject           邮件主题
     * @param to                收件人，多个收件人以,间隔
     * @param content           内容
     * @return  true  --  成功  false -- 失败
     */
    Boolean send(String subject, String to, String content);

    /**
     * 基于配置中的发件人信息,发送邮件，允许指定内容为html
     * @param subject           邮件主题
     * @param to                收件人，多个收件人以,间隔
     * @param content           内容
     * @param isHtml            是否html内容
     * @return  true  --  成功  false -- 失败
     */
    Boolean send(String subject, String to, String content, Boolean isHtml);

    /**
     * 基于配置中的发件人信息,发送邮件，允许指定内容为html，允许指定附件
     * @param subject           邮件主题
     * @param to                收件人，多个收件人以,间隔
     * @param content           内容
     * @param isHtml            是否html内容
     * @param attachments        附件File数组
     * @return  true  --  成功  false -- 失败
     */
    Boolean send(String subject, String to, String content, Boolean isHtml, File[] attachments);


    /**
     * 基于配置中的发件人信息,发送邮件，允许指定内容为html，允许指定附件，允许指定正文图片
     *
     * @param subject           邮件主题
     * @param to                收件人，多个收件人以,间隔
     * @param content           内容
     * @param isHtml            是否html内容
     * @param attachments       附件File数组
     * @param bodyImages        正文图片列表，格式：
     *                          {
     *                              "正文引用名称": File,
     *                              ...,
     *                              "正文引用名称": File
     *                          }
     *            正文引用格式：<img src='cid:正文引用名称'></img>
     * @return  true  --  成功  false -- 失败
     */
    Boolean send(String subject, String to, String content, Boolean isHtml, File[] attachments, Map<String, File> bodyImages);


    /**
     * 基于配置中的发件人信息,发送邮件，允许指定内容为html，允许指定附件，允许指定正文图片
     *
     * @param subject           邮件主题
     * @param to                收件人，多个收件人以,间隔
     * @param cc                抄送收件人,多个收件人以;间隔
     * @param bcc               暗送收件人,多个收件人以;间隔
     * @param content           内容
     * @param isHtml            是否html内容
     * @param attachments       附件File数组
     * @param bodyImages        正文图片列表，格式：
     *                          {
     *                              "正文引用名称": File,
     *                              ...,
     *                              "正文引用名称": File
     *                          }
     *            正文引用格式：<img src='cid:正文引用名称'></img>
     * @return  true  --  成功  false -- 失败
     */
    Boolean send(String subject, String to, String cc, String bcc, String content, Boolean isHtml, File[] attachments, Map<String, File> bodyImages);

}
