package com.ihltx.utility.mail.service.impl;

import com.ihltx.utility.mail.config.MailConfig;
import com.ihltx.utility.mail.service.JavaMailSenderUtil;
import com.ihltx.utility.util.FileUtil;
import com.ihltx.utility.util.StringUtil;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class JavaMailSenderUtilImpl implements JavaMailSenderUtil {

    private MailConfig mailConfig;
    public MailConfig getMailConfig() {
        return mailConfig;
    }

    public void setMailConfig(MailConfig mailConfig) {
        this.mailConfig = mailConfig;
    }

    private JavaMailSenderImpl javaMailSenderImpl;
    public JavaMailSenderImpl getJavaMailSenderImpl() {
        return javaMailSenderImpl;
    }

    public void setJavaMailSenderImpl(JavaMailSenderImpl javaMailSenderImpl) {
        this.javaMailSenderImpl = javaMailSenderImpl;
    }

    @Override
    public Boolean send(String subject, String to, String from, String content) {
        return send(subject, to, from, content,false);
    }

    @Override
    public Boolean send(String subject, String to, String from, String content, Boolean isHtml) {
        return send(subject, to, from, content,isHtml, null);
    }

    @Override
    public Boolean send(String subject, String to, String from, String content, Boolean isHtml, File[] attachments) {
        return send(subject, to, from, content,isHtml, attachments , null);
    }

    @Override
    public Boolean send(String subject, String to, String from, String content, Boolean isHtml, File[] attachments, Map<String, File> bodyImages) {

        return send(subject, to, null , null , from, content,isHtml, attachments , null);
    }


    @Override
    public Boolean send(String subject, String to, String content) {
        return send(subject, to, content,false);
    }

    @Override
    public Boolean send(String subject, String to, String content, Boolean isHtml) {
        return send(subject, to, content,isHtml, null);
    }

    @Override
    public Boolean send(String subject, String to, String content, Boolean isHtml, File[] attachments) {
        return send(subject, to, content,isHtml, attachments , null);
    }

    @Override
    public Boolean send(String subject, String to, String content, Boolean isHtml, File[] attachments, Map<String, File> bodyImages) {

        return send(subject, to, null , null , content,isHtml, attachments , null);
    }

    @Override
    public Boolean send(String subject, String to, String cc, String bcc, String content, Boolean isHtml, File[] attachments, Map<String, File> bodyImages) {
        return send(subject, to, cc , bcc , null, content,isHtml, attachments , null);
    }

    @Override
    public Boolean send(String subject , String to , String cc , String bcc , String from , String content , Boolean isHtml, File[] attachments, Map<String, File> bodyImages){
        try{
            MimeMessage mimeMessage = javaMailSenderImpl.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true , mailConfig.getDefaultEncoding());

            if (null != cc && !cc.isEmpty()) {
                InternetAddress[] internetAddressCC = new InternetAddress().parse(cc);
                helper.setCc(internetAddressCC);
            }

            if (null != bcc && !bcc.isEmpty()) {
                @SuppressWarnings("static-access")
                InternetAddress[] internetAddressBCC = new InternetAddress().parse(bcc);
                helper.setBcc(internetAddressBCC);
            }

            helper.setSubject(subject);
            if(bodyImages!=null && !bodyImages.isEmpty()){
                for(String key: bodyImages.keySet()){
                    if(bodyImages.get(key).isFile()){
                        helper.addInline(key , new FileSystemResource(bodyImages.get(key)), "image/jpeg");
                    }
                }
            }
            helper.setText(content,isHtml);
            helper.setTo(to.split(","));
            if(StringUtil.isNullOrEmpty(from)){
                if(StringUtil.isNullOrEmpty(mailConfig.getFromName())){
                    if(StringUtil.isNullOrEmpty(mailConfig.getFrom())){
                        helper.setFrom(mailConfig.getUsername());
                    }else{
                        helper.setFrom(mailConfig.getFrom());
                    }
                }else{
                    if(StringUtil.isNullOrEmpty(mailConfig.getFrom())){
                        helper.setFrom(mailConfig.getUsername() , mailConfig.getFromName());
                    }else{
                        helper.setFrom(mailConfig.getFrom() , mailConfig.getFromName());
                    }
                }
            }else{
                String[] froms  = from.split(",");
                if(froms.length<=1){
                    helper.setFrom(froms[0]);
                }else{
                    helper.setFrom(froms[0],froms[1]);
                }
            }
            if(attachments!=null && attachments.length>0){
                for(File file: attachments){
                    if(file.isFile()){
                        helper.addAttachment(FileUtil.getFileBaseName(file.getName()),file);
                    }
                }
            }
            javaMailSenderImpl.send(mimeMessage);
            return true;
        }catch (MailException e){
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return false;
    }



}
