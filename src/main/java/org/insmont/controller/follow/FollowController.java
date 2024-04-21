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

package org.insmont.controller.follow;

import com.google.gson.Gson;
import jakarta.annotation.Resource;
import org.insmont.model.CodeMessage;
import org.insmont.service.follow.FollowService;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

/**
 * @author chuhelan
 * @version 1.0
 * @date Saturday 09 March 2024 12:54 PM
 * @package: org.insmont.controller.follow
 * @Desc:
 */

@RestController
@ResponseBody
@RequestMapping("/v1/")
public class FollowController {
    Gson gson = new Gson();

    @Resource
    private FollowService followService;

    @PostMapping("/follow")
    public String follow(BigInteger follower, BigInteger following){
        int code = followService.follow(follower, following);
        return switch (code) {
            case 200 -> gson.toJson(new CodeMessage(200, "关注成功"));
            case 400 -> gson.toJson(new CodeMessage(400, "用户不存在"));
            case 401 -> gson.toJson(new CodeMessage(401, "用户已被封禁"));
            case 402 -> gson.toJson(new CodeMessage(402, "用户已被关注"));
            case 403 -> gson.toJson(new CodeMessage(403, "用户不能关注自己"));
            default -> gson.toJson(new CodeMessage(500, "未知错误"));
        };
    }

    @DeleteMapping("/unfollow")
    public String unfollow(BigInteger follower, BigInteger following){
        int code = followService.unfollow(follower, following);
        return switch (code) {
            case 200 -> gson.toJson(new CodeMessage(200, "取消关注成功"));
            case 400 -> gson.toJson(new CodeMessage(400, "用户不存在"));
            case 402 -> gson.toJson(new CodeMessage(402, "用户未被关注"));
            case 403 -> gson.toJson(new CodeMessage(403, "用户不能取消关注自己"));
            default -> gson.toJson(new CodeMessage(500, "未知错误"));
        };
    }

    @GetMapping("/follow/info")
    public String followInfo(BigInteger id){
        return gson.toJson(followService.followInfo(id));
    }
}
