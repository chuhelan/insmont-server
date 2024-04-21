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

package org.insmont.util.time;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author chuhelan
 * @version 1.0
 * @date Friday 19 April 2024 9:30 AM
 * @package: org.insmont.util.time
 * @Desc:
 */
public class TimeConversionUtil {
    public static String convertDateToISO8601(Date date) {
        // Convert Date to Instant
        Instant instant = date.toInstant();
        // Create OffsetDateTime with CST offset
        OffsetDateTime offsetDateTime = instant.atOffset(ZoneOffset.ofHours(8)); // CST offset is +08:00
        // Format OffsetDateTime to ISO 8601
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX");
        return offsetDateTime.format(formatter);
    }

}
