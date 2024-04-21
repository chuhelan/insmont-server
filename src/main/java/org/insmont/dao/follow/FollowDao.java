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

package org.insmont.dao.follow;

import org.apache.ibatis.annotations.Mapper;
import org.insmont.beans.post.Follow;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

/**
 * @author chuhelan
 * @version 1.0
 * @date Saturday 09 March 2024 1:01 PM
 * @package: org.insmont.dao.follow
 * @Desc:
 */

@Mapper
@Repository
public interface FollowDao {
    Follow selectFollow(BigInteger follower, BigInteger following);

    void insertFollow(BigInteger follower, BigInteger following);

    void deleteFollow(BigInteger follower, BigInteger following);
}
