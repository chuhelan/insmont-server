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

package org.insmont.service.post;

import org.insmont.beans.post.User_info;
import org.insmont.model.CodeMessage;
import org.insmont.model.CodeMessageData;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.Date;
import java.util.Optional;

/**
 * @author chuhelan
 * @version 1.0
 * @date Tuesday 27 February 2024 10:27 PM
 * @package: org.insmont.service.post
 * @Desc:
 */
public interface PostService {
    int post(String id, String content, MultipartFile[] images, String datetime, String visibility, int comment_permission) throws Exception;

    int postWithoutImages(String id, String content, String datetime, String visibility, int comment_permission);

    int like(String id, BigInteger postId);

    int unlike(String id, BigInteger postId);

    int delete(String id, BigInteger postId);

    User_info getPostUserInfo(BigInteger id);

    CodeMessageData showPost(Optional<BigInteger> id, int page);

    int comment(BigInteger postId, BigInteger id, String content);

    CodeMessageData getPostInfo(BigInteger postId);

    int updatePostPrivacy(BigInteger id, BigInteger post_id, String visibility, int comment_permission);

    int verifyPostWithUserId(BigInteger id, BigInteger postId);

    int deleteCommentByUserId(BigInteger comment_id, BigInteger id);

    CodeMessage getRecentlyCommentInfoByUserId(BigInteger post_id, BigInteger id);
}
