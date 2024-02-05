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

package org.insmont.service.impl.mail;

import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.insmont.beans.verification.Verification_email;
import org.insmont.dao.mail.MailDao;
import org.insmont.service.mail.MailService;
import org.insmont.util.mail.MailUtil;
import org.insmont.util.string.HideStringUtil;
import org.insmont.util.string.RandomCodeUtil;
import org.insmont.util.string.StringUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * @author chuhelan
 * @version 1.0
 * @date Wednesday 31 January 2024 3:50 PM
 * @package: org.insmont.service.impl.mail
 * @Desc:
 */

@Service
public class MailServiceImpl implements MailService {

    @Resource
    MailDao mailDao;

    StringUtil stringUtil = new StringUtil();

    @Resource
    MailUtil mailUtil;
    String subscribeEmailTemplate = "SubscribeEmail";
    String verifyEmailTemplate = "VerificationEmail";

    /*
     * 发送订阅成功邮件成功后添加到数据库
     * 200 订阅成功  400 email为空 401 email格式不正确 402 email已经订阅过
     */
    @SneakyThrows
    @Override
    public int subscribe(String email) {

        Map<String, Object> dataMap = new HashMap<>();

        if (stringUtil.isEmail(email)) {
            if (mailDao.selectEmail(email) == null) {

                CompletableFuture<String> future = mailUtil.sendTemplateMail(email, "订阅成功", subscribeEmailTemplate, dataMap);

                try {
                    String result = future.join();

                    if (result.equals("邮件发送成功")) {
                        mailDao.insertEmail(email);
                        return 200;
                    } else {
                        return 403;
                    }
                } catch (CompletionException ex) {
                    return 500;
                }
            } else {
                return 402;
            }
        } else {
            return 401;
        }
    }


    /*
     *       200 验证成功  400 email或code为空 401 email格式不正确 403 code已过期 404 code不匹配
     * */
    @Override
    public int verify(String email, String code) {

        if (email.isEmpty() || code.isEmpty()) {
            return 400;
        }

        if (!stringUtil.isEmail(email)) {
            return 401;
        }

        Verification_email verification_email = mailDao.selectEmailVerify(email, code);

        if (verification_email == null || !verification_email.getVerification_code().equals(code)) {
            return 404;
        }

        if (verification_email.getExpired().before(new Date())) {
            return 403;
        }
        return 200;
    }


    /*
     *      200 发送成功  400 email为空  403邮件发送失败 500 线程错误
     * */
    @Override
    public int sendCode(String email) {

        if (email.isEmpty()) {
            return 400;
        }

        if (!stringUtil.isEmail(email)) {
            return 401;
        }

        Map<String, Object> dataMap = new HashMap<>();
        String code = RandomCodeUtil.generateRandom6DigitCode();
        dataMap.put("user_email", HideStringUtil.hideEmail(email));
        dataMap.put("ver_code", code);
        Date expired = new Date(System.currentTimeMillis() + 5 * 60 * 1000);

        CompletableFuture<String> future = mailUtil.sendTemplateMail(email, "邮箱验证", verifyEmailTemplate, dataMap);

        try {
            String result = future.join();
            if (result.equals("邮件发送成功")) {
                if (mailDao.selectEmailVerifyWithoutCode(email) == null) {
                    mailDao.insertEmailVerify(email, code, expired);
                } else {
                    mailDao.updateEmailVerify(email, code, expired);
                }
                return 200;
            } else {
                return 403;
            }
        } catch (CompletionException ex) {
            return 500;
        }
    }
}
