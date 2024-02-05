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
 * @date Monday 16 October 2023 8:14 AM
 * @package: org.insmont.util
 * @Desc:
 */

/**
 * This class, HideStringUtil, provides methods for hiding sensitive information like phone numbers, email addresses, and ID cards.
 */
public class HideStringUtil {

    /**
     * Hides the middle digits of a phone number by replacing them with asterisks.
     *
     * @param phoneNumber The input phone number to hide.
     * @return The phone number with middle digits replaced by asterisks.
     */
    public static String hidePhone(String phoneNumber) {
        return phoneNumber.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    /**
     * Hides part of an email address by replacing characters with asterisks.
     *
     * @param email The input email address to hide.
     * @return The email address with parts replaced by asterisks.
     */
    public static String hideEmail(String email) {
        return email.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
    }

    /**
     * Hides the middle digits of an ID card by replacing them with asterisks.
     *
     * @param idCard The input ID card number to hide.
     * @return The ID card number with middle digits replaced by asterisks.
     */
    public static String hideIdCard(String idCard) {
        return idCard.replaceAll("(\\d{4})\\d{10}(\\w{4})", "$1*****$2");
    }
}
