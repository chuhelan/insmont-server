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

package org.insmont.util.ip;

/**
 * @author chuhelan
 * @version 1.0
 * @date Wednesday 31 January 2024 1:13 AM
 * @package: org.insmont.util.ip
 * @Desc:
 */
public class RegionUtil {

    public static String getRegion(String ip) throws Exception {
        String cityInfo = IpCatchRegion.cityRegion(ip);
        if (!cityInfo.isEmpty()) {
            cityInfo = cityInfo.replace("|", " ");
            String[] cityList = cityInfo.split(" ");
            if (cityList.length > 0) {
                if ("中国".equals(cityList[0]) && "0".equals(cityList[1])) {
                    if (cityList[2].equals("北京") || cityList[2].equals("天津") || cityList[2].equals("上海") || cityList[2].equals("重庆")) {
                        return cityList[2];
                    } else if (cityList[2].equals("香港")||cityList[2].equals("澳门")||cityList[2].equals("台湾")) {
                        return cityList[0]+cityList[2];
                    } else {
                        return cityList[2] + cityList[3];
                    }
                }else if ("0".equals(cityList[0])) {
                    return "未知" ;
                }
                return cityList[0];
            }
        }
        return "未知";
    }
}
