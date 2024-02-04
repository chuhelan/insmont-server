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
import org.insmont.dao.mail.MailDao;
import org.insmont.service.mail.MailService;
import org.insmont.util.mail.MailUtil;
import org.insmont.util.string.StringUtil;
import org.springframework.stereotype.Service;

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
}
