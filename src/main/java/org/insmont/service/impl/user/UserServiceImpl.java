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

package org.insmont.service.impl.user;

import com.google.gson.JsonObject;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.insmont.beans.user.Login_record;
import org.insmont.beans.user.Profile;
import org.insmont.beans.user.User;
import org.insmont.beans.verification.Verification_email;
import org.insmont.beans.verification.Verification_mobile;
import org.insmont.dao.mail.MailDao;
import org.insmont.dao.phone.PhoneDao;
import org.insmont.dao.user.UserDao;
import org.insmont.model.CodeMessageData;
import org.insmont.service.user.UserService;
import org.insmont.util.expire.TokenExpired;
import org.insmont.util.id.GenerateUid;
import org.insmont.util.identicon.IdentIconUtil;
import org.insmont.util.ip.DeviceDetector;
import org.insmont.util.ip.IpUtil;
import org.insmont.util.ip.RegionUtil;
import org.insmont.util.encryption.EncryptionUtil;
import org.insmont.util.string.StringUtil;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;

/**
 * @author chuhelan
 * @version 1.0
 * @date Tuesday 30 January 2024 1:25 PM
 * @package: org.insmont.service.impl
 * @Desc:
 */

@Service
public class UserServiceImpl implements UserService {

    StringUtil stringUtil = new StringUtil();
    GenerateUid generateUid = new GenerateUid();

    @Resource
    UserDao userDao;
    @Resource
    PhoneDao phoneDao;
    @Resource
    MailDao mailDao;

    @Resource
    IdentIconUtil identIconUtil;

    /**
     * 通过传入的key判断是手机号还是邮箱号
     * 验证数据库都否有相同的key
     * 存在的话返回403：用户存在 不存在的话返回200
     * 随机生成id和username
     * 最后将用户信息存储到数据库中：密码 注册日期 ipv4和ipv6 地区
     */
    @Override
    public String register(String key, String password) throws Exception {
        password = EncryptionUtil.encryptPassword(password);
        try {
            if (stringUtil.isMobilePhone(key)) {
                if (userDao.selectUserByPhone(key) != null) {
                    return "403";
                }
            } else if (stringUtil.isEmail(key)) {
                if (userDao.selectUserByEmail(key) != null) {
                    return "403";
                }
            } else {
                return "400";
            }

            User user = new User();
            user.setId(generateUid.generateUid());
            user.setUsername("小萌" + (generateUid.generateUid().toString()).substring(4));
            user.setPassword(password);
            String ipv4 = IpUtil.getCallerIp().get(0);
            user.setRegistration_ipv4(ipv4);
            user.setRegistration_ipv6(IpUtil.getCallerIp().get(1));
            user.setRegistration_region(RegionUtil.getRegion(ipv4));

            if (stringUtil.isMobilePhone(key)) {
                user.setMobile(key);
                user.setMobile_code("0086");
                System.out.println("user = " + user);
                userDao.insertUserWithPhone(user);
            } else if (stringUtil.isEmail(key)) {
                user.setEmail(key);
                System.out.println("user = " + user);
                userDao.insertUserWithEmail(user);
            }
            userDao.insertProfileAvatarWithId(user.getId(), identIconUtil.getUploadUrl(user.getUsername()));
            return "200";
        } catch (Exception e) {
            e.printStackTrace();
            return "500";
        }

    }


    /**
     * 使用用户登录特征，还有密码
     * 验证用户存在返回token，特征或密码错误返回401
     */
    @SneakyThrows
    @Override
    public String login(String key, String password) {

        password = EncryptionUtil.encryptPassword(password);

        User user = stringUtil.isMobilePhone(key) ? userDao.selectUserByPhone(key) : userDao.selectUserByEmail(key);
        String device = DeviceDetector.getDevice();

        if (user == null || !user.getPassword().equals(password)) {
            return "401";
        }

        TokenExpired tokenExpired = new TokenExpired();
        String token = tokenExpired.getTokenExpire().getToken();
        String newToken = token + "|" + user.getId();
        LocalDateTime expire = LocalDateTime.from(tokenExpired.getTokenExpire().getExpire());
        String ipv4 = IpUtil.getCallerIp().get(0);
        String ipv6 = IpUtil.getCallerIp().get(1);
        String location = RegionUtil.getRegion(ipv4);

        int recordLoginCode = recordLogin(user.getId());
        if (recordLoginCode == 201) {
            return "201";
        } else if (recordLoginCode == 200) {
            userDao.updateUserToken(user.getId(), token, expire);
            userDao.insertRecordInfo(user.getId(), device, ipv4, ipv6, location);
            userDao.updateProfileLocationWithId(user.getId(), location);
            return newToken;
        } else {
            return "500";
        }
    }


    /**
     * 200 成功 返回token
     * 400 内容为空
     * 401 邮箱或手机号格式错误
     * 402 验证码发送失败
     * 403 验证失败 -> 不匹配或者过期
     * 404 用户不存在
     * 500 未知错误
     */
    @SneakyThrows
    @Override
    public String login2fa(String key, String code) {

        if (key == null || code == null || key.isEmpty() || code.isEmpty()) {
            return "400";
        }

        if (!stringUtil.isMobilePhone(key) && !stringUtil.isEmail(key)) {
            return "401";
        }

        User user = stringUtil.isMobilePhone(key) ? userDao.selectUserByPhone(key) : userDao.selectUserByEmail(key);

        if (user == null) {
            return "404";
        }

        Verification_mobile verificationMobile = new Verification_mobile();
        Verification_email verificationEmail = new Verification_email();

        if (stringUtil.isMobilePhone(key)) {
            verificationMobile = phoneDao.selectVerificationMobileByPhone(key);
            if (verificationMobile == null) {
                return "402";
            } else if ((!verificationMobile.getVerification_code().equals(code) || verificationMobile.getExpired().before(new Date()))) {
                return "403";
            }
        } else if (stringUtil.isEmail(key)) {
            verificationEmail = mailDao.selectEmailVerifyWithoutCode(key);
            if (verificationEmail == null) {
                return "402";
            } else if ((!verificationEmail.getVerification_code().equals(code) || verificationEmail.getExpired().before(new Date()))) {
                return "403";
            }
        }

        TokenExpired tokenExpired = new TokenExpired();
        String token = tokenExpired.getTokenExpire().getToken();
        String newToken = token + "|" + user.getId();
        LocalDateTime expire = LocalDateTime.from(tokenExpired.getTokenExpire().getExpire());

        String device = DeviceDetector.getDevice();
        String ipv4 = IpUtil.getCallerIp().get(0);
        String ipv6 = IpUtil.getCallerIp().get(1);
        String location = RegionUtil.getRegion(ipv4);

        userDao.updateUserToken(user.getId(), token, expire);
        userDao.insertRecordInfo(user.getId(), device, ipv4, ipv6, location);
        userDao.updateProfileLocationWithId(user.getId(), location);
        return newToken;
    }

    @Override
    public int verifyToken(String id, String token) {
        User user = userDao.selectUserTokenById(id);
        if(user == null){
            return 404;
        }else if(user.getSession().equals(token)){
            return 200;
        }else{
            return 401;
        }
    }

    @Override
    public CodeMessageData getUserInfo(String id) {
        Profile profileInfo = userDao.getUserProfileWithId(id);

        if (profileInfo == null) {
            return new CodeMessageData(404, "user not found", null);
        }

        JsonObject returnData = new JsonObject();
        returnData.addProperty("username", userDao.getAllUserInfoWithId(id).getUsername());
        returnData.addProperty("avatar", Optional.ofNullable(profileInfo.getAvatar()).orElse(""));
        returnData.addProperty("bio", Optional.ofNullable(profileInfo.getBio()).orElse(""));
        returnData.addProperty("location", Optional.ofNullable(profileInfo.getLocation()).orElse(""));
        returnData.addProperty("gender", Optional.ofNullable(profileInfo.getGender()).orElse(""));
        returnData.addProperty("birthday", Optional.ofNullable(profileInfo.getBirthday()).map(String::valueOf).orElse(""));
        returnData.addProperty("constellation", Optional.ofNullable(profileInfo.getConstellation()).orElse(""));
        returnData.addProperty("certification", Optional.ofNullable(profileInfo.getCertification()).orElse(""));
        returnData.addProperty("state", Optional.ofNullable(profileInfo.getState()).orElse(""));
        returnData.addProperty("credit", profileInfo.getCredit());
        returnData.addProperty("verification", profileInfo.getVerification());

        return new CodeMessageData(200, "success", returnData);
    }

    public int recordLogin(BigInteger id) throws Exception {
        String device = DeviceDetector.getDevice();
        String ipv4 = IpUtil.getCallerIp().get(0);
        String ipv6 = IpUtil.getCallerIp().get(1);
        String location = RegionUtil.getRegion(ipv4);

        Login_record login_record = userDao.selectLatestRecordInfoByUserId(id);

        if (login_record == null) {
            userDao.insertRecordInfo(id, device, ipv4, ipv6, location);
            return 200;
        }

        if (!login_record.getDevice().equals(device) || !login_record.getLocation().equals(location)) {
            return 201;
        } else {
            userDao.insertRecordInfo(id, device, ipv4, ipv6, location);
            return 200;
        }
    }
}