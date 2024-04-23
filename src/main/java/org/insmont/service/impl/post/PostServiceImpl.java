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

package org.insmont.service.impl.post;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.insmont.beans.post.*;
import org.insmont.beans.post.comment.Comment;
import org.insmont.beans.user.Profile;
import org.insmont.beans.user.User;
import org.insmont.dao.post.PostDao;
import org.insmont.dao.user.UserDao;
import org.insmont.model.CodeMessage;
import org.insmont.model.CodeMessageData;
import org.insmont.service.post.PostService;
import org.insmont.util.reflection.ReflectionUtil;
import org.insmont.util.upload.UploadImage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.insmont.util.time.TimeConversionUtil.convertDateToISO8601;

/**
 * @author chuhelan
 * @version 1.0
 * @date Tuesday 27 February 2024 10:27 PM
 * @package: org.insmont.service.impl.post
 * @Desc:
 */

@Slf4j
@Service
public class PostServiceImpl implements PostService {

    @Resource
    UploadImage uploadImage;

    @Resource
    PostDao postDao;

    @Resource
    UserDao userDao;

    private static final Set<String> DISABLED_STATE = new HashSet<>();

    static {
        DISABLED_STATE.add("Inactive");
        DISABLED_STATE.add("Suspended");
        DISABLED_STATE.add("Closed");
        DISABLED_STATE.add("Restricted");
        DISABLED_STATE.add("Expired");
        DISABLED_STATE.add("Deleted");
    }

    /**
     * @param id 用户id
     *           content 发布内容
     *           images  图片数组
     * @return 200 发布成功
     * 400 内容为空 401 图片格式错误 402 图片数量超限 403 用户没有权限 500 未知错误
     */
    @Override
    public int post(String id, String content, MultipartFile[] images, String datetime, String visibility, int comment_permission) throws Exception {
        if (content.isEmpty()) {
            return 400;
        }

        if (images.length > 9) {
            return 402;
        }

        for (MultipartFile image : images) {
            String originalFilename = image.getOriginalFilename();
            if (!isImageFile(originalFilename)) {
                return 401;
            }
        }

        Profile profile = userDao.getUserProfileWithId(id);
        if (DISABLED_STATE.contains(profile.getState()) || profile.getCredit() <= 2.8) {
            return 403;
        }

        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String imageUrl = uploadImage.uploadImage(image);
            imageUrls.add(imageUrl);
        }
        log.info("IMAGE_URLS_ARE: {}", imageUrls);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date time = null;
        try {
            time = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("content", content);
        params.put("datetime", time);
        params.put("visibility", visibility);
        params.put("comment_permission", comment_permission);

        postDao.insertPost(params);

        BigInteger postId = (BigInteger) params.get("post_id");
        log.info("POST_ID_IS: {}", postId);
        postDao.insertPostImage(postId, imageUrls);
        return 200;
    }


    /**
     * @param id 用户id
     *           content 发布内容
     * @return 200 发布成功
     * 400 内容为空 403 用户没有权限 500 未知错误
     */
    @Override
    public int postWithoutImages(String id, String content, String datetime, String visibility, int comment_permission) {
        if (content.isEmpty()) {
            return 400;
        }

        Profile profile = userDao.getUserProfileWithId(id);
        if (DISABLED_STATE.contains(profile.getState()) || profile.getCredit() <= 2.8) {
            return 403;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date time = null;
        try {
            time = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Map<String, Object> params = new HashMap<>();
        params.put("id", id);
        params.put("content", content);
        params.put("datetime", time);
        params.put("visibility", visibility);
        params.put("comment_permission", comment_permission);

        postDao.insertPost(params);

        BigInteger postId = (BigInteger) params.get("post_id");
        log.info("POST_ID_IS: {}", postId);
        return 200;
    }

    /**
     * @param id 用户id
     *           postId  帖子id
     *           return 200 点赞成功
     *           401 已经点赞
     *           403 用户没有权限
     *           404 帖子不存在
     *           500 未知错误
     */
    @Override
    public int like(String id, BigInteger postId) {

        Profile profile = userDao.getUserProfileWithId(id);
        if (DISABLED_STATE.contains(profile.getState()) || profile.getCredit() <= 2.8) {
            return 403;
        }

        Post_info info = postDao.selectPostInfoByPostId(postId);

        if (info == null) {
            return 404;
        }

        Post_like postLike = postDao.selectPostLikeByPostIdAndUserId(postId, id);
        if (postLike != null) {
            return 401;
        }

        postDao.insertPostLike(postId, id);
        return 200;
    }

    /**
     * @param id 用户id
     *           postId  帖子id
     *           return 200 取消点赞成功
     *           401 未点赞
     *           403 用户没有权限
     *           404 帖子不存在
     *           500 未知错误
     */
    @Override
    public int unlike(String id, BigInteger postId) {

        Profile profile = userDao.getUserProfileWithId(id);
        if (DISABLED_STATE.contains(profile.getState()) || profile.getCredit() <= 2.8) {
            return 403;
        }

        Post_info info = postDao.selectPostInfoByPostId(postId);
        if (info == null) {
            return 404;
        }

        Post_like postLike = postDao.selectPostLikeByPostIdAndUserId(postId, id);
        if (postLike == null) {
            return 401;
        }

        postDao.deletePostLike(postId, id);
        return 200;
    }

    /**
     * @param id 用户id
     *           postId  帖子id
     *           return 200 删除成功
     *           401 用户没有权限
     *           404 帖子不存在
     *           500 未知错误
     */
    @Override
    public int delete(String id, BigInteger postId) {
        Post_info info = postDao.selectPostInfoByPostId(postId);
        if (info == null) {
            return 404;
        }

        if (!info.getAuthor().equals(id)) {
            return 401;
        }

        postDao.deletePost(postId);
        return 200;
    }

    @Override
    public User_info getPostUserInfo(BigInteger id) {
        return postDao.selectPostUserInfoByUserId(id);
    }

    @Override
    public CodeMessageData showPost(Optional<BigInteger> id, int page) {
        int pageSize = 5;
        int offset = (page - 1) * pageSize;

        List<Post_info> posts;
        if (id.isPresent()) {
            posts = postDao.selectPostListWithId(id.get(), offset, pageSize);
        } else {
            posts = postDao.selectPostListWithOutId(offset, pageSize);
        }

        JsonObject returnData = new JsonObject();
        JsonArray postList = new JsonArray();

        for (Post_info post : posts) {
            JsonObject postInfo = new JsonObject();
            Profile personalInfo = userDao.getUserProfileWithId(post.getAuthor());
            User userInfo = userDao.getAllUserInfoWithId(post.getAuthor());
            postInfo.addProperty("post_id", post.getPost_id());
            postInfo.addProperty("author", post.getAuthor());
            postInfo.addProperty("author_name", userInfo.getUsername());
            postInfo.addProperty("author_avatar", personalInfo.getAvatar());
            postInfo.addProperty("author_verification", personalInfo.getVerification());
            postInfo.addProperty("content", post.getContent());
            postInfo.addProperty("visibility", post.getVisibility());
            postInfo.addProperty("edit", post.getEdit());
            postInfo.addProperty("comment_permission", post.getComment_permission());
            postInfo.addProperty("location", post.getLocation());
            postInfo.addProperty("datetime", convertDateToISO8601(post.getDatetime()));
            postInfo.addProperty("total_likes", post.getTotal_likes());
            postInfo.addProperty("total_comments", post.getTotal_comments());
            postInfo.addProperty("total_images", post.getTotal_images());

            Post_image postImage = postDao.selectPostImageByPostId(post.getPost_id());
            JsonArray imageList = new JsonArray();
            if (postImage != null) {
                for (int i = 1; i <= 9; i++) {
                    String imageUrl = (String) ReflectionUtil.getFieldValue(postImage, "image_url" + i);
                    if (imageUrl != null) {
                        imageList.add(imageUrl);
                    }
                }
            }
            postInfo.add("images", imageList);

            JsonArray post_likes = new JsonArray();
            List<User> postLikeUser = userDao.getPostLikeUsersWithPostId(post.getPost_id());
            for (User user : postLikeUser) {
                JsonObject likeUser = new JsonObject();
                likeUser.addProperty("id", user.getId());
                likeUser.addProperty("username", user.getUsername());
                likeUser.addProperty("avatar", userDao.getUserProfileWithId(String.valueOf(user.getId())).getAvatar());
                post_likes.add(likeUser);
            }
            postInfo.add("likes", post_likes);

            JsonArray post_comments = new JsonArray();
            List<Comment> postComment = postDao.getPostCommentsWithPostId(post.getPost_id());
            for (Comment comment : postComment) {
                JsonObject commentInfo = new JsonObject();
                commentInfo.addProperty("comment_id", comment.getComment_id());
                commentInfo.addProperty("id", userDao.getAllUserInfoWithId(comment.getId().toString()).getId());
                commentInfo.addProperty("username", userDao.getAllUserInfoWithId(comment.getId().toString()).getUsername());
                commentInfo.addProperty("avatar", userDao.getUserProfileWithId(comment.getId().toString()).getAvatar());
                commentInfo.addProperty("content", comment.getContent());
                commentInfo.addProperty("location", comment.getLocation());
                commentInfo.addProperty("datetime", convertDateToISO8601(comment.getDatetime()));
                post_comments.add(commentInfo);
            }
            postInfo.add("comments", post_comments);

            postList.add(postInfo);
        }

        returnData.add("post_list", postList);
        return new CodeMessageData(200, id.map(Object::toString).orElse("success, page: " + page), returnData);
    }


    @Override
    public int comment(BigInteger postId, BigInteger id, String content) {

        if (content.isEmpty()) {
            return 400;
        }

        Profile profile = userDao.getUserProfileWithId(id.toString());
        if (DISABLED_STATE.contains(profile.getState()) || profile.getCredit() <= 2.8) {
            return 403;
        }

        Post post = postDao.selectPostByPostId(postId);
        if (post == null) {
            return 404;
        }

        postDao.insertComment(postId, id, content);
        return 200;
    }

    @Override
    public CodeMessageData getPostInfo(BigInteger postId) {
        JsonObject postList = new JsonObject();
        Post_info post = postDao.selectPostInfoByPostId(postId);
        log.info("POST_INFO_IS: {}", post);

        if (post == null) {
            return new CodeMessageData(404, "post not found", null);
        }

        JsonObject posting = new JsonObject();

        User userInfo = userDao.getAllUserInfoWithId(post.getAuthor());
        Profile personalInfo = userDao.getUserProfileWithId(post.getAuthor());

        posting.addProperty("post_id", post.getPost_id());
        posting.addProperty("author", post.getAuthor());
        posting.addProperty("author_name", userInfo.getUsername());
        posting.addProperty("author_avatar", personalInfo.getAvatar());
        posting.addProperty("author_verification", personalInfo.getVerification());
        posting.addProperty("content", post.getContent());
        posting.addProperty("visibility", post.getVisibility());
        posting.addProperty("edit", post.getEdit());
        posting.addProperty("comment_permission", post.getComment_permission());
        posting.addProperty("location", post.getLocation());
        posting.addProperty("datetime", convertDateToISO8601(post.getDatetime()));
        posting.addProperty("total_likes", post.getTotal_likes());
        posting.addProperty("total_comments", post.getTotal_comments());
        posting.addProperty("total_images", post.getTotal_images());

        Post_image postImage = postDao.selectPostImageByPostId(post.getPost_id());
        JsonArray imageList = new JsonArray();
        if (postImage != null) {
            for (int i = 1; i <= 9; i++) {
                String imageUrl = (String) ReflectionUtil.getFieldValue(postImage, "image_url" + i);
                if (imageUrl != null) {
                    imageList.add(imageUrl);
                }
            }
        }
        posting.add("images", imageList);

        JsonArray post_likes = new JsonArray();
        List<User> postLikeUser = userDao.getPostLikeUsersWithPostId(post.getPost_id());
        for (User user : postLikeUser) {
            JsonObject likeUser = new JsonObject();
            likeUser.addProperty("id", user.getId());
            likeUser.addProperty("username", user.getUsername());
            likeUser.addProperty("avatar", userDao.getUserProfileWithId(String.valueOf(user.getId())).getAvatar());
            post_likes.add(likeUser);
        }
        posting.add("likes", post_likes);

        JsonArray post_comments = new JsonArray();
        List<Comment> postComment = postDao.getPostCommentsWithPostId(post.getPost_id());
        for (Comment comment : postComment) {
            JsonObject commentInfo = new JsonObject();
            User userinfo = userDao.getAllUserInfoWithId(comment.getId().toString());
            Profile userProfile = userDao.getUserProfileWithId(comment.getId().toString());
            commentInfo.addProperty("comment_id", comment.getComment_id());
            commentInfo.addProperty("id", userinfo.getId());
            commentInfo.addProperty("username", userinfo.getUsername());
            commentInfo.addProperty("avatar", userProfile.getAvatar());
            commentInfo.addProperty("verification", userProfile.getVerification());
            commentInfo.addProperty("content", comment.getContent());
            commentInfo.addProperty("location", comment.getLocation());
            commentInfo.addProperty("datetime", convertDateToISO8601(comment.getDatetime()));
            post_comments.add(commentInfo);
        }
        posting.add("comments", post_comments);

        postList.add("post", posting);
        return new CodeMessageData(200, "success", postList);
    }

    @Override
    public int updatePostPrivacy(BigInteger id, BigInteger post_id, String visibility, int comment_permission) {
        User user = userDao.getAllUserInfoWithId(id.toString());
        Post_info post = postDao.selectPostInfoByPostId(post_id);

        if (user == null || post == null) {
            return 404;
        }

        if (!post.getAuthor().equals(id.toString())) {
            return 401;
        }

        postDao.updatePostPrivacy(post_id, visibility, comment_permission);
        return 200;
    }

    @Override
    public int verifyPostWithUserId(BigInteger id, BigInteger postId) {

        if (id == null || postId == null) {
            return 400;
        }

        Profile userinfo = userDao.getUserProfileWithId(id.toString());
        Post_info post = postDao.selectPostInfoByPostId(postId);

        if (post == null || userinfo == null) {
            return 404;
        }

        if (DISABLED_STATE.contains(userinfo.getState()) || userinfo.getCredit() <= 2.8) {
            return 403;
        }

        Post_like postLike = postDao.selectPostLikeByPostIdAndUserId(postId, id.toString());
        if (postLike == null) {
            return 200;
        } else {
            return 201;
        }
    }

    @Override
    public int deleteCommentByUserId(BigInteger comment_id, BigInteger id) {
        if (comment_id == null || id == null) {
            return 400;
        }

        Comment commentInfo = postDao.getPostCommentWithCommentId(comment_id);

        if (commentInfo == null) {
            return 404;
        }

        Post commentFromPost = postDao.selectPostByPostId(commentInfo.getPost_id());
        if (Objects.equals(commentFromPost.getId(), id) || Objects.equals(commentInfo.getId(), id)) {
            postDao.deleteComment(comment_id);
            return 200;
        } else {
            return 401;
        }
    }

    @Override
    public CodeMessage getRecentlyCommentInfoByUserId(BigInteger post_id, BigInteger id) {
        if (post_id == null || id == null) {
            return new CodeMessage(400, "cannot input null value");
        }
        if (postDao.selectPostInfoByPostId(post_id) == null || userDao.getAllUserInfoWithId(id.toString()) == null) {
            return new CodeMessage(404, "post or user not found");
        }

        Comment recentlyComment = postDao.getPostCommentsWithPostIdAndUserId(post_id, id);
        if (recentlyComment == null) {
            return new CodeMessage(403, "comment not found");
        }else {
            return new CodeMessage(200, recentlyComment.getComment_id().toString());
        }
    }


    private boolean isImageFile(String fileName) {
        return fileName != null && (fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".gif") || fileName.endsWith(".bmp") || fileName.endsWith(".webp") || fileName.endsWith(".svg"));
    }
}
