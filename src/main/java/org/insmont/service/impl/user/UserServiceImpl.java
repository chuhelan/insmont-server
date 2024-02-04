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

import jakarta.annotation.Resource;
import org.insmont.beans.user.User;
import org.insmont.dao.user.UserDao;
import org.insmont.service.user.UserService;
import org.insmont.util.id.GenerateUid;
import org.insmont.util.ip.IpUtil;
import org.insmont.util.ip.RegionUtil;
import org.insmont.util.string.StringUtil;
import org.springframework.stereotype.Service;

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

    /**
     * 通过传入的key判断是手机号还是邮箱号
     * 验证数据库都否有相同的key
     * 存在的话返回403：用户存在 不存在的话返回200
     * 随机生成id和username
     * 最后将用户信息存储到数据库中：密码 注册日期 ipv4和ipv6 地区
     */
    @Override
    public String register(String key, String password) throws Exception {
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
            return "200";
        }catch (Exception e){
            e.printStackTrace();
            return "500";
        }

    }
}
