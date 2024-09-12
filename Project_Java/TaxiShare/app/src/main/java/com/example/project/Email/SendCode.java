package com.example.project.Email;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendCode {
    private static  final  String TAG = "SendCode";

    private String userEmail, code;

    private static final String adminEmail = "taeyoungkim0707@gmail.com";
    private static final String adminPassword = "umhu hyjb kymb onqh";

    // 생성자 //
    public SendCode(String userEmail, String code){
        this.userEmail = userEmail;
        this.code = code;
    }
    
    public String getUserEmail(){return userEmail;}
    public String getCode(){return code;}
    public String getAdminEmail(){return adminEmail;}
    public String getAdminPassword(){return adminPassword;}

    // 이메일 코드 전송 함수 //
    public boolean sendEmail(){
        try{
            String strHost = "smtp.gmail.com";
            Properties properties = System.getProperties();

            properties.put("mail.smtp.host", strHost);
            properties.put("mail.smtp.port", "465");
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.auth", "true");

            //이메일 유효성 검사
            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(adminEmail, adminPassword);
                }
            });
            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(userEmail));

            //전송 메세지 내용
            message.setSubject("인증번호");
            message.setText("인증번호는 [" + code + "] 입니다. 인증번호를 입력해주세요."); 
            
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(message);  //이메일 전송
                    } catch (MessagingException e) {e.printStackTrace();}
                }
            });
            thread.start();
            return true;
        }
        catch (AddressException e){e.printStackTrace();}
        catch (MessagingException e){e.printStackTrace();}

        return false;
    }
}
