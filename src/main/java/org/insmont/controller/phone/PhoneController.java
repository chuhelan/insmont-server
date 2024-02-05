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

package org.insmont.controller.phone;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.insmont.model.CodeMessageData;
import org.insmont.service.phone.PhoneService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chuhelan
 * @version 1.0
 * @date Sunday 04 February 2024 9:35 PM
 * @package: org.insmont.controller.phone
 * @Desc:
 */

@Slf4j
@RestController
@ResponseBody
@RequestMapping("/v1/")
public class PhoneController {

    Gson gson = new Gson();

    @Resource
    private PhoneService phoneService;

    @PostMapping("/phoneCode")
    public String phoneCode(String phone){
        int code = phoneService.phoneCode(phone);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", phone);
        return switch (code){
            case 200 -> gson.toJson(new CodeMessageData(200, "success", jsonObject));
            case 400 -> gson.toJson(new CodeMessageData(400, "phone is null", jsonObject));
            case 401 -> gson.toJson(new CodeMessageData(401, "phone is error", jsonObject));
            case 402 -> gson.toJson(new CodeMessageData(402, "failed to send", jsonObject));
            case 403 -> gson.toJson(new CodeMessageData(403, "api limit", jsonObject));
            default -> gson.toJson(new CodeMessageData(500, "error", jsonObject));
        };
    }

    @PostMapping("/phoneVerifyCode")
    public String phoneVerifyCode(String phone, String code){
        int result = phoneService.phoneVerifyCode(phone, code);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("phone", phone);
        return switch (result){
            case 200 -> gson.toJson(new CodeMessageData(200, "success", jsonObject));
            case 400 -> gson.toJson(new CodeMessageData(400, "phone or code is null", jsonObject));
            case 401 -> gson.toJson(new CodeMessageData(401, "phone is error", jsonObject));
            case 403 -> gson.toJson(new CodeMessageData(403, "verify failed", jsonObject));
            default -> gson.toJson(new CodeMessageData(500, "error", jsonObject));
        };
    }

}
