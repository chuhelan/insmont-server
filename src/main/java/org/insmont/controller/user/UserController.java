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
import com.google.gson.JsonObject;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.insmont.dao.user.UserDao;
import org.insmont.model.CodeMessage;
import org.insmont.model.CodeMessageData;
import org.insmont.service.user.UserService;
import org.springframework.web.bind.annotation.*;

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
    public String registerUser(String key, String password) {
        if (key == null || password == null || key.isEmpty() || password.isEmpty())
            return gson.toJson(new CodeMessage(400, "邮箱或手机号为空"));
        return switch (userService.register(key, password)) {
            case "400" -> gson.toJson(new CodeMessage(401, "手机号或邮箱格式错误"));
            case "403" -> gson.toJson(new CodeMessage(402, "账号已存在"));
            case "200" -> gson.toJson(new CodeMessage(200, "注册成功"));
            default -> gson.toJson(new CodeMessage(500, "未知错误"));
        };
    }

    @PostMapping("/login")
    public String login(String key, String password) {
        String status = userService.login(key, password);
        JsonObject returnData = new JsonObject();
        int code = 500;
        String message = "thread error";

        if (status.equals("401")) {
            code = 401;
            message = "Account or password error";
            returnData.addProperty("login object", key);
            return gson.toJson(new CodeMessageData(code, message, returnData));
        }

        if (status.equals("201")) {
            code = 201;
            message = "Double verification";
            returnData.addProperty("login object", key);
            return gson.toJson(new CodeMessageData(code, message, returnData));
        }

        if (status.length() > 3) {
            code = 200;
            message = "Login success";
            returnData.addProperty("token", status);
            returnData.addProperty("login object", key);
            return gson.toJson(new CodeMessageData(code, message, returnData));
        }

        return gson.toJson(new CodeMessageData(code, message, returnData));
    }

    @PostMapping("/login/2fa")
    public String login2fa(String key, String code) {
        String status = userService.login2fa(key, code);
        JsonObject returnData = new JsonObject();
        int returnCode = 500;
        String message = "thread error";

        if (status.equals("400")) {
            returnCode = 400;
            message = "email, phone or code is null";
            returnData.addProperty("login object", key);
            return gson.toJson(new CodeMessageData(returnCode, message, returnData));
        }

        if (status.equals("401")) {
            returnCode = 401;
            message = "email or phone is not valid";
            returnData.addProperty("login object", key);
            return gson.toJson(new CodeMessageData(returnCode, message, returnData));
        }

        if (status.equals("402")) {
            returnCode = 402;
            message = "send code failed";
            returnData.addProperty("login object", key);
            return gson.toJson(new CodeMessageData(returnCode, message, returnData));
        }

        if (status.equals("403")) {
            returnCode = 403;
            message = "verify failed";
            returnData.addProperty("login object", key);
            return gson.toJson(new CodeMessageData(returnCode, message, returnData));
        }

        if (status.equals("404")) {
            returnCode = 404;
            message = "user not found";
            returnData.addProperty("login object", key);
            return gson.toJson(new CodeMessageData(returnCode, message, returnData));
        }

        if (status.length() > 3) {
            returnCode = 200;
            message = "login success";
            returnData.addProperty("token", status);
            returnData.addProperty("login object", key);
            return gson.toJson(new CodeMessageData(returnCode, message, returnData));
        }
        return gson.toJson(new CodeMessageData(returnCode, message, returnData));
    }

    @PostMapping("/user/verify")
    public String verifyToken(String id,String token){
        int status = userService.verifyToken(id,token);
        if (status == 200){
            return gson.toJson(new CodeMessage(200,"success"));
        }else if(status == 401){
            return gson.toJson(new CodeMessage(401,"failed"));
        }else if(status == 404){
            return gson.toJson(new CodeMessage(404,"user not found"));
        }else {
            return gson.toJson(new CodeMessage(500,"unknown error"));
        }
    }

    @GetMapping("user/info")
    public String getUserInfo(String id){
        return gson.toJson(userService.getUserInfo(id));
    }


}
