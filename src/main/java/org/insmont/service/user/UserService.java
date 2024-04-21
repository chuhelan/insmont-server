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

package org.insmont.service.user;

import org.insmont.model.CodeMessageData;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @author  chuhelan
 * @date  Tuesday 30 January 2024 1:24 PM
 * @package: org.insmont.service
 * @version 1.0
 * @Desc:
 */

public interface UserService {

    String register(String key, String password) throws Exception;

    String login(String key, String password);

    String login2fa(String key, String code);

    int verifyToken(String id, String token);

    CodeMessageData getUserInfo(String id);

    List<String> getRecommendUser(BigInteger id);

    int updateAvatar(BigInteger id, MultipartFile avatar);

    int updateBio(BigInteger id, String bio);

    int updatePrivacy(BigInteger id, String search, String recommend);

    int updatePassword(BigInteger id, String password);

    int updateUserInfo(BigInteger id, String username, String gender, String birthday, String constellation);

    int deleteUser(BigInteger id);

    CodeMessageData getFollowingLatest(BigInteger id);

    int isFollowed(BigInteger id, BigInteger targetUser);
}
