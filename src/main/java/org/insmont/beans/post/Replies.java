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

package org.insmont.beans.post;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

/**
 * @author chuhelan
 * @version 1.0
 * @date Monday 29 January 2024 2:58 PM
 * @package: org.insmont.beans
 * @Desc:
 */

@Data
public class Replies {
    private BigInteger reply_id;
    private BigInteger comment_id;
    private BigInteger id;
    private String content;
    private String location;
    private Date datetime;
}
