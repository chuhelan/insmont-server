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

package org.insmont.dao.user;

import org.apache.ibatis.annotations.Mapper;
import org.insmont.beans.post.Follow;
import org.insmont.beans.user.*;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * @author chuhelan
 * @version 1.0
 * @date Tuesday 30 January 2024 1:26 PM
 * @package: org.insmont.dao
 * @Desc:
 */

@Mapper
@Repository
public interface UserDao {
    User selectUserByPhone(String phone);

    User selectUserByEmail(String email);

    int insertUserWithPhone(User user);

    int insertUserWithEmail(User user);

    int updateUserToken(BigInteger id, String token, LocalDateTime expired);

    Login_record selectLatestRecordInfoByUserId(BigInteger id);

    int insertRecordInfo(BigInteger id, String device, String ipv4, String ipv6, String location);

    int insertProfileAvatarWithId(BigInteger id, String avatar);

    int updateProfileLocationWithId(BigInteger id, String location);

    User selectUserTokenById(String id);

    User getAllUserInfoWithId(String id);

    Profile getUserProfileWithId(String id);

    int insertPrivacyWithId(BigInteger id);

    List<String> selectRecommendUser(BigInteger id);

    int updateProfileAvatarWithId(BigInteger id, String avatar);

    Privacy getUserPrivacyWithId(String id);

    int updateProfileBioWithId(BigInteger id, String bio);

    int updatePrivacyWithId(BigInteger id, String search, String recommend);

    int updateUserPassword(BigInteger id, String password);

    int updateUserInfoTableUser(BigInteger id, String username);

    int updateUserInfoTableProfile(BigInteger id, String gender, String birthday, String constellation);

    int deleteUser(BigInteger id);

    List<Profile> selectLatestFollowingInfoByUserIdWithoutPrivacy(BigInteger id);

    Follow getFollowed(BigInteger id, BigInteger following);

    List<User> getPostLikeUsersWithPostId(BigInteger postId);
}
