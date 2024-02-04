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

package org.insmont.controller.ip;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.insmont.model.MessageData;
import org.insmont.util.ip.RegionUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author chuhelan
 * @version 1.0
 * @date Wednesday 31 January 2024 10:07 AM
 * @package: org.insmont.controller.ip
 * @Desc:
 */

@RestController
@ResponseBody
@RequestMapping("/v1/")
public class IpController {

    Gson gson = new Gson();

    @SneakyThrows
    @GetMapping("/ip/{ipAddress}")
    public String getRegions(@PathVariable List<String> ipAddress){
        JsonObject jsonObject = new JsonObject();
        for (String ip : ipAddress) {
            String region = RegionUtil.getRegion(ip);
            jsonObject.addProperty(ip, region);
        }
        return gson.toJson(new MessageData("success", jsonObject));
    }
}
