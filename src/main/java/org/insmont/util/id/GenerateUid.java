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

package org.insmont.util.id;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author chuhelan
 * @version 1.0
 * @date Monday 29 January 2024 3:47 PM
 * @package: org.insmont.util
 * @Desc:
 */
public class GenerateUid {
    private static long lastTimestamp = 0;
    public synchronized String generatePrefix() {
        SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyMM");
        return yearMonthFormat.format(new Date());
    }


    /**
     * generate a unique id with 7 digits
     * then add a prefix with 4 digits
     * join them together
     */
    public BigInteger generateUid(){

        long currentTimestamp = System.currentTimeMillis();
        while (currentTimestamp <= lastTimestamp) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            currentTimestamp = System.currentTimeMillis();
        }

        lastTimestamp = currentTimestamp;
        long unique7Digit = currentTimestamp % 10000000;

        return new BigInteger(generatePrefix() + String.valueOf(unique7Digit));
    }
}
