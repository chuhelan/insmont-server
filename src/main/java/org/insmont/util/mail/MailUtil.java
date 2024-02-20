/*
 * Copyright (C) 2023 The Insmont Open Source Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.insmont.util.mail;

import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author chuhelan
 * @version 1.0
 * @date Monday 13 November 2023 7:34 PM
 * @package: org.insmont.util
 * @Desc:
 */

@Slf4j
@Component
public class MailUtil {

    private final String nickName = "Insmont 中国";
    @Resource
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    private TemplateEngine templateEngine;

    @SneakyThrows
    @Async
    public CompletableFuture<String> sendTemplateMail(String to, String subject, String emailTemplate, Map<String, Object> dataMap) {
        log.info("Attempting to send email to: {}", to);

        Context context = new Context();
        for (Map.Entry<String, Object> entry : dataMap.entrySet()) {
            context.setVariable(entry.getKey(), entry.getValue());
        }

        String templateContent = templateEngine.process(emailTemplate, context);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(nickName+'<'+from+'>');
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(templateContent, true);

            javaMailSender.send(message);
            log.info("Email sent successfully to: {}", to);
            return CompletableFuture.completedFuture("邮件发送成功");
        } catch (MailException e) {
            log.error("Failed to send email to: {}", to, e);
            return CompletableFuture.completedFuture("邮件发送失败: " + e.getMessage());
        }
    }
}
