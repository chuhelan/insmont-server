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

/**
 * @author chuhelan
 * @version 1.0
 * @date Monday 29 January 2024 2:32 PM
 * @package: org.insmont.beans
 * @Desc:
 */

@Data
public class Post_image {
    private BigInteger post_id;
    private String image_url1;
    private String image_url2;
    private String image_url3;
    private String image_url4;
    private String image_url5;
    private String image_url6;
    private String image_url7;
    private String image_url8;
    private String image_url9;
}
