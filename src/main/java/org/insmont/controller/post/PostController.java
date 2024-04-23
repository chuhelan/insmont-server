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

package org.insmont.controller.post;

import com.google.gson.Gson;
import jakarta.annotation.Resource;
import org.insmont.model.CodeMessage;
import org.insmont.model.CodeMessageData;
import org.insmont.service.post.PostService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Date;
import java.util.Optional;

/**
 * @author chuhelan
 * @version 1.0
 * @date Tuesday 27 February 2024 10:24 PM
 * @package: org.insmont.controller.post
 * @Desc:
 */

@RestController
@ResponseBody
@RequestMapping("/v1/")
public class PostController {

    Gson gson = new Gson();

    @Resource
    PostService postService;

    @PostMapping("post")
    public String post(String id, String content, @RequestParam(name = "images", required = false) MultipartFile[] images, String datetime, String visibility, int comment_permission) throws Exception {
        if (images != null && images.length > 0) {
            int code = postService.post(id, content, images, datetime, visibility, comment_permission);
            return switch (code) {
                case 200 -> gson.toJson(new CodeMessage(200, "发布成功"));
                case 400 -> gson.toJson(new CodeMessage(400, "内容为空"));
                case 401 -> gson.toJson(new CodeMessage(401, "图片格式错误"));
                case 402 -> gson.toJson(new CodeMessage(402, "图片数量超限"));
                case 403 -> gson.toJson(new CodeMessage(403, "用户没有权限"));
                default -> gson.toJson(new CodeMessage(500, "未知错误"));
            };
        } else {
            int code = postService.postWithoutImages(id, content, datetime, visibility, comment_permission);
            return switch (code) {
                case 200 -> gson.toJson(new CodeMessage(200, "发布成功"));
                case 400 -> gson.toJson(new CodeMessage(400, "内容为空"));
                case 403 -> gson.toJson(new CodeMessage(403, "用户没有权限"));
                default -> gson.toJson(new CodeMessage(500, "未知错误"));
            };
        }
    }

    @PostMapping("post/like")
    public String post_like(String id, BigInteger post_id) {
        int code = postService.like(id, post_id);
        return switch (code) {
            case 200 -> gson.toJson(new CodeMessage(200, "点赞成功"));
            case 401 -> gson.toJson(new CodeMessage(401, "已经点赞"));
            case 403 -> gson.toJson(new CodeMessage(403, "用户没有权限"));
            case 404 -> gson.toJson(new CodeMessage(404, "帖子不存在"));
            default -> gson.toJson(new CodeMessage(500, "未知错误"));
        };
    }


    @DeleteMapping("post/unlike")
    public String post_unlike(String id, BigInteger post_id) {
        int code = postService.unlike(id, post_id);
        return switch (code) {
            case 200 -> gson.toJson(new CodeMessage(200, "取消点赞成功"));
            case 401 -> gson.toJson(new CodeMessage(401, "未点赞"));
            case 403 -> gson.toJson(new CodeMessage(403, "用户没有权限"));
            case 404 -> gson.toJson(new CodeMessage(404, "帖子不存在"));
            default -> gson.toJson(new CodeMessage(500, "未知错误"));
        };
    }


    @DeleteMapping("post/delete")
    public String post_delete(String id, BigInteger post_id) {
        int code = postService.delete(id, post_id);
        return switch (code) {
            case 200 -> gson.toJson(new CodeMessage(200, "删除成功"));
            case 401 -> gson.toJson(new CodeMessage(401, "用户没有权限"));
            case 404 -> gson.toJson(new CodeMessage(404, "帖子不存在"));
            default -> gson.toJson(new CodeMessage(500, "未知错误"));
        };

    }

    @GetMapping("post/userinfo")
    public String post_info(BigInteger id) {
        return gson.toJson(postService.getPostUserInfo(id));
    }

//      TODO 推文的编辑功能
//    @PatchMapping("post/edit")


//      TODO 推文的举报功能
//    @PostMapping("post/report")


//      TODO 推文的分享功能
//    @PostMapping("post/share")

    @GetMapping("post/like/verify")
    public String post_like_verify(BigInteger id, BigInteger post_id) {
        return switch (postService.verifyPostWithUserId(id, post_id)) {
            case 200 -> gson.toJson(new CodeMessage(200, "还没有点赞"));
            case 201 -> gson.toJson(new CodeMessage(201, "已经点赞"));
            case 400 -> gson.toJson(new CodeMessage(400, "不能输入空值"));
            case 401 -> gson.toJson(new CodeMessage(403, "用户没有权限"));
            case 404 -> gson.toJson(new CodeMessage(404, "帖子不存在"));
            default -> gson.toJson(new CodeMessage(500, "未知错误"));
        };
    }

    @PostMapping("post/comment")
    public String post_comment(BigInteger post_id, BigInteger id, String content) {
        int code = postService.comment(post_id, id, content);
        return switch (code) {
            case 200 -> gson.toJson(new CodeMessage(200, "评论成功"));
            case 400 -> gson.toJson(new CodeMessage(400, "内容为空"));
            case 401 -> gson.toJson(new CodeMessage(403, "用户没有权限"));
            case 404 -> gson.toJson(new CodeMessage(404, "用户或帖子不存在"));
            default -> gson.toJson(new CodeMessage(500, "未知错误"));
        };
    }

    @DeleteMapping("post/comment/delete")
    public String deleteComment(BigInteger comment_id, BigInteger id) {
        return switch (postService.deleteCommentByUserId(comment_id, id)) {
            case 200 -> gson.toJson(new CodeMessage(200, "删除成功"));
            case 400 -> gson.toJson(new CodeMessage(400, "不能输入空值"));
            case 401 -> gson.toJson(new CodeMessage(401, "用户没有权限"));
            case 404 -> gson.toJson(new CodeMessage(404, "评论不存在"));
            default -> gson.toJson(new CodeMessage(500, "未知错误"));
        };
    }

    @GetMapping("post/show/{page}")
    public String getLatestPostList(Optional<BigInteger> id, @PathVariable int page) {
        return gson.toJson(postService.showPost(id, page));
    }

    @GetMapping("post/info")
    public String getPostInfo(BigInteger post_id) {
        return gson.toJson(postService.getPostInfo(post_id));
    }

    @PatchMapping("post/privacy/update")
    public String updatePostPrivacy(BigInteger id, BigInteger post_id, String visibility, int comment_permission) {
        return switch (postService.updatePostPrivacy(id, post_id, visibility, comment_permission)) {
            case 200 -> gson.toJson(new CodeMessage(200, "修改成功"));
            case 401 -> gson.toJson(new CodeMessage(401, "用户没有权限"));
            case 404 -> gson.toJson(new CodeMessage(404, "帖子或用户不存在"));
            default -> gson.toJson(new CodeMessage(500, "未知错误"));
        };
    }

    @GetMapping("post/comment/recently")
    public String getRecentlyComment(BigInteger post_id, BigInteger id){
        return gson.toJson(postService.getRecentlyCommentInfoByUserId(post_id,id));
    }
}
