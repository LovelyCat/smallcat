package com.smallcat.controller.email;

import com.smallcat.common.bean.ResultVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.mail.internet.MimeMessage;

/**
 * 发送外部邮件
 *
 * @author dongyb
 * @version 2017-12-18
 */
@Controller
@RequestMapping("/email")
public class EmailController {

    private static Logger logger = LoggerFactory.getLogger(EmailController.class);
    public static final String LOGGER_PREFIX = "[message-center-api][emailBiz]";
    private static String EMAIL_SPLIT = ";";

    @Value("${mail.smtp.from}")
    private String fromEmail;

    @Resource
    private JavaMailSenderImpl javaMailSender;

    @ResponseBody
    @RequestMapping("/sendEmail")
    public ResultVo sendMail(EmailParamDto emailParamDto) {
        ResultVo resultVo = new ResultVo();
        Long emailId = 0L;
        try {
            // 发送邮件
            logger.info(LOGGER_PREFIX + "[sendEmail]准备发送邮件。threadId={}", Thread.currentThread().getId());
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(fromEmail);
            simpleMailMessage.setSubject(emailParamDto.getTitle());
            simpleMailMessage.setText(emailParamDto.getContent());

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            messageHelper.setFrom(simpleMailMessage.getFrom());
            messageHelper.setText(emailParamDto.getContent(), true);
            messageHelper.setSubject(simpleMailMessage.getSubject());
            if (emailParamDto.getTitle() != null) {
                messageHelper.setSubject(emailParamDto.getTitle());
            }

            messageHelper.setTo(emailParamDto.getToEmails().split(EMAIL_SPLIT));
            if(emailParamDto.getCcEmails() != null && !"".contentEquals(emailParamDto.getCcEmails())){
                messageHelper.setCc(emailParamDto.getCcEmails().split(EMAIL_SPLIT));
            }
            javaMailSender.send(message);
            logger.info(LOGGER_PREFIX + "[sendEmail]成功发送邮件。threadId={}", Thread.currentThread().getId());
        } catch (Exception e) {
            logger.error(LOGGER_PREFIX + "[sendEmail]邮件发送异常。。threadId={}", Thread.currentThread().getId(), e);
        }
        return resultVo;
    }

}
