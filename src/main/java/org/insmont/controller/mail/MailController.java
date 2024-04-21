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

package org.insmont.controller.mail;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.insmont.model.CodeMessageData;
import org.insmont.service.mail.MailService;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

/**
 * @author chuhelan
 * @version 1.0
 * @date Wednesday 31 January 2024 3:48 PM
 * @package: org.insmont.controller.mail
 * @Desc:
 */


@Slf4j
@RestController
@ResponseBody
@RequestMapping("/v1/")
public class MailController {

    Gson gson = new Gson();

    @Resource
    private MailService mailService;

    @PostMapping("/subscribe")
    public String subscribe(String email) {

        int code = mailService.subscribe(email);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);
        if (email.isEmpty()) {
            return gson.toJson(new CodeMessageData(400, "email is null", jsonObject));
        } else {
            return switch (code) {
                case 200 -> gson.toJson(new CodeMessageData(200, "success", jsonObject));
                case 401 -> gson.toJson(new CodeMessageData(401, "email is not valid", jsonObject));
                case 402 -> gson.toJson(new CodeMessageData(402, "email is already subscribed", jsonObject));
                case 403 -> gson.toJson(new CodeMessageData(403, "send failed", jsonObject));
                default -> gson.toJson(new CodeMessageData(500, "thread error", jsonObject));
            };
        }
    }

    @PostMapping("/mailCode")
    public String mailCode(String email) {

        int code = mailService.sendCode(email);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);

        return switch (code) {
            case 200 -> gson.toJson(new CodeMessageData(200, "success", jsonObject));
            case 400 -> gson.toJson(new CodeMessageData(400, "email is null", jsonObject));
            case 401 -> gson.toJson(new CodeMessageData(401, "email is not valid", jsonObject));
            case 403 -> gson.toJson(new CodeMessageData(403, "send failed", jsonObject));
            default -> gson.toJson(new CodeMessageData(500, "thread error", jsonObject));
        };
    }

    @PostMapping("/mailVerifyCode")
    public String mailVerifyCode(String email, String code) {
        int result = mailService.verify(email, code);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("email", email);

        return switch (result) {
            case 200 -> gson.toJson(new CodeMessageData(200, "success", jsonObject));
            case 400 -> gson.toJson(new CodeMessageData(400, "email or code is null", jsonObject));
            case 401 -> gson.toJson(new CodeMessageData(401, "email is not valid", jsonObject));
            case 403 -> gson.toJson(new CodeMessageData(403, "code is expired", jsonObject));
            case 404 -> gson.toJson(new CodeMessageData(404, "code is not match", jsonObject));
            default -> gson.toJson(new CodeMessageData(500, "thread error", jsonObject));
        };
    }
}