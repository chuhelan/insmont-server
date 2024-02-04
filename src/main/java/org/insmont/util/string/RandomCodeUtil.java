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

package org.insmont.util.string;

/**
 * @author chuhelan
 * @version 1.0
 * @date Monday 16 October 2023 8:01 AM
 * @package: org.insmont.util
 * @Desc:
 */

/**
 * This class, RandomCodeUtil, provides a method for generating a random 6-digit numeric code.
 */
public class RandomCodeUtil {

    /**
     * Generates and returns a random 6-digit numeric code.
     *
     * @return A randomly generated 6-digit numeric code as a string.
     */
    public static String generateRandom6DigitCode() {

        StringBuilder random_code = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            int randomDigit = (int) (Math.random() * 10);
            random_code.append(randomDigit);
        }

        return random_code.toString();
    }
}
