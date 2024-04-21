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

package org.insmont.service.impl.follow;

import com.google.gson.JsonObject;
import jakarta.annotation.Resource;
import org.insmont.beans.post.Follow;
import org.insmont.beans.user.Profile;
import org.insmont.dao.follow.FollowDao;
import org.insmont.dao.user.UserDao;
import org.insmont.service.follow.FollowService;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
 * @author chuhelan
 * @version 1.0
 * @date Saturday 09 March 2024 12:57 PM
 * @package: org.insmont.service.impl.follow
 * @Desc:
 */

@Service
public class FollowServiceImpl implements FollowService {

    @Resource
    UserDao userDao;

    @Resource
    FollowDao followDao;


    /**
     * @param follower  id
     * @param following 关注了谁
     * @return 200 关注成功
     * 400 用户不存在
     * 401 用户已被封禁
     * 402 用户已被关注
     * 403 用户不能关注自己
     * 500 未知错误
     */
    @Override
    public int follow(BigInteger follower, BigInteger following) {

        if (follower.equals(following)) {
            return 403;
        }

        Profile followingProfile = userDao.getUserProfileWithId(String.valueOf(following));
        if (followingProfile == null) {
            return 400;
        }

        if (followingProfile.getState().equals("Inactive")
                || followingProfile.getState().equals("Suspended")
                || followingProfile.getState().equals("Closed")
                || followingProfile.getState().equals("Restricted")
                || followingProfile.getState().equals("Expired")
                || followingProfile.getState().equals("Deleted")) {
            return 401;
        }

        Follow follow_info = followDao.selectFollow(follower, following);
        if (follow_info != null) {
            return 402;
        }

        followDao.insertFollow(follower, following);
        return 200;
    }

    /**
     * @param follower  id
     * @param following 关注了谁
     * @return  200 取消关注成功
     *          400 用户不存在
     *          402 用户未被关注
     *          403 用户不能取消关注自己
     *          500 未知错误
     */
    @Override
    public int unfollow(BigInteger follower, BigInteger following) {

            if (follower.equals(following)) {
                return 403;
            }

            Profile followingProfile = userDao.getUserProfileWithId(String.valueOf(following));
            if (followingProfile == null) {
                return 400;
            }

            Follow follow_info = followDao.selectFollow(follower, following);
            if (follow_info == null) {
                return 402;
            }

            followDao.deleteFollow(follower, following);
            return 200;
    }

    @Override
    public JsonObject followInfo(BigInteger id) {
//        return followDao.selectFollowInfo(id);
        return null;
    }
}
