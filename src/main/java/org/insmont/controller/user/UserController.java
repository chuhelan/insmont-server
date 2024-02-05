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

package org.insmont.controller.user;

import com.google.gson.Gson;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.insmont.model.CodeMessage;
import org.insmont.model.CodeMessageData;
import org.insmont.service.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chuhelan
 * @version 1.0
 * @date Tuesday 30 January 2024 1:15 PM
 * @package: org.insmont.controller
 * @Desc:
 */

@RestController
@ResponseBody
@RequestMapping("/v1/")
public class UserController {

    Gson gson = new Gson();

    @Resource
    UserService userService;

    @SneakyThrows
    @PostMapping("register")
    public String registerUser(String key,String password){
        if (key == null || password == null || key.isEmpty() || password.isEmpty())
            return gson.toJson(new CodeMessage(400,"邮箱或手机号为空"));
        return switch (userService.register(key, password)) {
            case "400" -> gson.toJson(new CodeMessage(401,"手机号或邮箱格式错误"));
            case "403" -> gson.toJson(new CodeMessage(402,"账号已存在"));
            case "200" -> gson.toJson(new CodeMessage(200,"注册成功"));
            default -> gson.toJson(new CodeMessage(500,"未知错误"));
        };
    }

}
