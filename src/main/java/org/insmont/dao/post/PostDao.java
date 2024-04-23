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

package org.insmont.dao.post;

import org.apache.ibatis.annotations.Mapper;
import org.insmont.beans.post.*;
import org.insmont.beans.post.comment.Comment;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author chuhelan
 * @version 1.0
 * @date Saturday 02 March 2024 8:31 PM
 * @package: org.insmont.dao.post
 * @Desc:
 */

@Mapper
@Repository
public interface PostDao {
    Integer insertPostImage(BigInteger post_id, List<String> imageUrls);

    void insertPost(Map<String, Object> params);

    Post_info selectPostInfoByPostId(BigInteger post_id);

    Post_like selectPostLikeByPostIdAndUserId(BigInteger post_id, String id);

    void insertPostLike(BigInteger post_id, String id);

    void deletePostLike(BigInteger post_id, String id);

    void deletePost(BigInteger post_d);

    User_info selectPostUserInfoByUserId(BigInteger id);

    List<Post_info> selectPostListWithId(BigInteger id, int offset, int pageSize);

    List<Post_info> selectPostListWithOutId(int offset, int pageSize);

    Post selectPostByPostId(BigInteger postId);

    void insertComment(BigInteger post_id, BigInteger id, String content);

    Post_image selectPostImageByPostId(BigInteger postId);

    List<Comment> getPostCommentsWithPostId(BigInteger postId);

    void updatePostPrivacy(BigInteger post_id, String visibility, int comment_permission);

    Comment getPostCommentWithCommentId(BigInteger comment_id);

    void deleteComment(BigInteger comment_id);

    Comment getPostCommentsWithPostIdAndUserId(BigInteger post_id, BigInteger id);
}
