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
 * @date Friday 08 March 2024 12:04 AM
 * @package: org.insmont.beans.post
 * @Desc:
 */

@Data
public class Post_info {
    private BigInteger post_id;
    private String author;
    private String content;
    private String visibility;
    private Integer edit;
    private Integer comment_permission;
    private String location;
    private Integer total_likes;
    private Integer total_comments;
    private Integer total_images;
    private Date datetime;
}
