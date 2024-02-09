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

package org.insmont.util.expire;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author chuhelan
 * @version 1.0
 * @date Thursday 08 February 2024 9:15 PM
 * @package: org.insmont.util.expire
 * @Desc:
 */

public class TokenExpired {

    /**
     *  生成一个过期时间14天的token
     */

    public tokenExpire getTokenExpire() {

        String sessionToken = generateSessionToken();
        LocalDateTime expireDateTime = LocalDateTime.now().plusDays(14);

        tokenExpire tokenExpire = new tokenExpire();
        tokenExpire.setToken(sessionToken);
        tokenExpire.setExpire(expireDateTime);
        return tokenExpire;
    }

    private String generateSessionToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Data
    public static class tokenExpire {
        String token;
        LocalDateTime expire;
    }

}
