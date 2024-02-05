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

package org.insmont.service.impl.phone;

import jakarta.annotation.Resource;
import org.insmont.dao.phone.PhoneDao;
import org.insmont.service.phone.PhoneService;
import org.insmont.util.phone.PhoneUtil;
import org.insmont.util.string.RandomCodeUtil;
import org.insmont.util.string.StringUtil;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * @author chuhelan
 * @version 1.0
 * @date Sunday 04 February 2024 10:47 PM
 * @package: org.insmont.service.impl.phone
 * @Desc:
 */

@Service
public class PhoneServiceImpl implements PhoneService {

    @Resource
    PhoneDao phoneDao;

    @Resource
    PhoneUtil phoneUtil;

    StringUtil stringUtil = new StringUtil();

    /*
     *   200 发送成功  400 手机号为空 401 手机号格式不正确 402 发送失败 403 api限制调用60秒 500 未知错误
     * */
    @Override
    public int phoneCode(String phone) {

        if (phone.isEmpty()) {
            return 400;
        }
        if (!stringUtil.isMobilePhone(phone)) {
            return 401;
        }

        String code = RandomCodeUtil.generateRandom6DigitCode();

        /*
         *          判断数据库中是否已经存在该手机号
         *           存在：发送一遍并update
         *           不存在：发送一遍并insert
         */
        CompletableFuture<String> future = phoneUtil.SendPhoneCode(phone, code);
        Date expired = new Date(System.currentTimeMillis() + 5 * 60 * 1000);
        Date apiFailed = new Date(System.currentTimeMillis() + 4 * 60 * 1000);
        try {
            String result = future.join();

            if (result.equals("success")) {
                if (phoneDao.selectVerificationMobileByPhone(phone) == null) {
                    phoneDao.insertVerificationMobile(phone, code, expired);
                } else if (phoneDao.selectVerificationMobileByPhone(phone).getExpired().before(apiFailed)) {
                    phoneDao.updateVerificationMobile(phone, code, expired);
                } else {
                    return 403;
                }
                return 200;
            } else {
                return 402;
            }
        } catch (CompletionException ex) {
            return 500;
        }
    }


    /*
     *   200 验证成功  400 手机号或验证码为空 401 手机格式错误 403 验证码错误 500 未知错误
     * */
    @Override
    public int phoneVerifyCode(String phone, String code) {

            if (phone.isEmpty() || code.isEmpty()) {
                return 400;
            }

            if (!stringUtil.isMobilePhone(phone)) {
                return 401;
            }

            if (phoneDao.selectVerificationMobileByPhone(phone) == null || !phoneDao.selectVerificationMobileByPhone(phone).getVerification_code().equals(code)) {
                return 403;
            }

            if (phoneDao.selectVerificationMobileByPhone(phone).getExpired().before(new Date())) {
                return 403;
            }
            return 200;
    }
}
