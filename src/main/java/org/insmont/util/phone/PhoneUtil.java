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

package org.insmont.util.phone;

import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.aliyun.dysmsapi20170525.*;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * @author chuhelan
 * @version 1.0
 * @date Sunday 04 February 2024 10:01 PM
 * @package: org.insmont.util.phone
 * @Desc:
 */

@Slf4j
@Component
public class PhoneUtil {

    /**
     * 使用AK&SK初始化账号Client
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */

    @Value("${aliyun.accessKeyId}")
    private String keyId;

    @Value("${aliyun.accessKeySecret}")
    private String keySecret;

    private static Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new Client(config);
    }


    @SneakyThrows
    @Async
    public CompletableFuture<String> SendPhoneCode(String mobile, String code) {

        Client client = createClient(keyId, keySecret);

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(mobile)
                .setSignName("黑煤球")
                .setTemplateCode("SMS_257705731")
                .setTemplateParam("{\"code\":\"" + code + "\"}");

        RuntimeOptions runtime = new RuntimeOptions();
        try {
            client.sendSmsWithOptions(sendSmsRequest, runtime);
            return CompletableFuture.completedFuture("success");
        } catch (TeaException error) {
            System.out.println(error.getMessage());
            System.out.println(error.getData().get("Recommend"));
            Common.assertAsString(error.message);
            return CompletableFuture.completedFuture("failed");
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            System.out.println(error.getMessage());
            System.out.println(error.getData().get("Recommend"));
            Common.assertAsString(error.message);
            return CompletableFuture.completedFuture("failed");
        }
    }
}
