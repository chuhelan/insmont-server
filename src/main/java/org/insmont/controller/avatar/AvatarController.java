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

package org.insmont.controller.avatar;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.insmont.model.CodeMessageData;
import org.insmont.util.identicon.IdentIconUtil;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chuhelan
 * @version 1.0
 * @date Thursday 15 February 2024 10:55 PM
 * @package: org.insmont.controller.avatar
 * @Desc:
 */

@Slf4j
@RestController
@ResponseBody
@RequestMapping("/v1/")
public class AvatarController {

    Gson gson = new Gson();


    @GetMapping("/avatar")
    public String getAvatarUrl(String username) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(username, IdentIconUtil.getUploadUrl(username));
        return gson.toJson(new CodeMessageData(200, "success", jsonObject));
    }
}
