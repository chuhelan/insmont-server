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

package org.insmont.beans.user;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

/**
 * @author chuhelan
 * @version 1.0
 * @date Sunday 28 January 2024 11:19 PM
 * @package: org.insmont.beans
 * @Desc:
 */

@Data
public class Login_record {
    private BigInteger id;
    private String device;
    private String ipv4;
    private String ipv6;
    private String location;
    private Date datetime;
}
