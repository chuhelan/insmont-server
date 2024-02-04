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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
  @author chuhelan
 * @version 1.0
 * @date Monday 16 October 2023 7:52 AM
 * @package: org.insmont.util
 * @Desc:
 */

/**
 * This Java code provides functions to check if a given input string is either an email address or a mobile phone number.
 * <p>
 * - The isEmail method uses a regular expression to validate email addresses.<p>
 * - The isMobilePhone method uses a regular expression to validate mobile phone numbers.<p>
 * - The is_email_or_phone method combines these checks and returns "email" for email addresses, "phone" for mobile phone numbers, and "error" for neither.
 */
public class StringUtil {

    public boolean isEmail(String value) {
        String emailPattern = "^([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)*@([a-zA-Z0-9]*[-_]?[a-zA-Z0-9]+)+[\\.][A-Za-z]{2,3}([\\.][A-Za-z]{2})?$";
        Pattern p = Pattern.compile(emailPattern);
        Matcher m = p.matcher(value);
        return m.matches();
    }

    public boolean isMobilePhone(String value) {
        String mobilePhonePattern = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";
        Pattern p = Pattern.compile(mobilePhonePattern);
        Matcher m = p.matcher(value);
        return m.matches();
    }

    public String is_email_or_phone(String value) {
        if (isEmail(value)) {
            return "email";
        } else if (isMobilePhone(value)) {
            return "phone";
        } else {
            return "error";
        }
    }
}