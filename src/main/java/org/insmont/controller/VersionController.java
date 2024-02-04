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

package org.insmont.controller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.insmont.model.CodeMessage;
import org.insmont.model.CodeMessageData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author chuhelan
 * @version 1.0
 * @date Monday 16 October 2023 8:50 AM
 * @package: org.insmont.controller
 * @Desc:
 */

@Slf4j
@RestController
@RequestMapping("v1")
public class VersionController {

    private final Gson gson = new Gson();

    private final JsonObject jsonObject = new JsonObject();

    @Value("${version.current}")
    private String current_version;

    @GetMapping("/version")
    public String version() {
        jsonObject.addProperty("version", current_version);
        return gson.toJson(new CodeMessageData(200, "success", jsonObject));
    }
}
