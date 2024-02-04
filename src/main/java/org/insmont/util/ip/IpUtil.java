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

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chuhelan
 * @version 1.0
 * @date Tuesday 30 January 2024 11:04 AM
 * @package: org.insmont.util
 * @Desc:
 */
public class IpUtil {

    public static List<String> getCallerIp() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return getIpAddresses(request);
    }

    private static List<String> getIpAddresses(HttpServletRequest request) {
        List<String> ipAddresses = new ArrayList<>();

        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            if (inetAddress instanceof Inet6Address) {
                ipAddresses.add(convertIPv6ToIPv4(ip));
                ipAddresses.add(ip);
            } else {
                ipAddresses.add(ip);
                ipAddresses.add(null);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return ipAddresses;
    }

    private static String convertIPv6ToIPv4(String ipv6) {
        try {
            InetAddress inetAddress = InetAddress.getByName(ipv6);
            byte[] ipv4Bytes = Inet6Address.getByName(ipv6).getAddress();
            return InetAddress.getByAddress(ipv4Bytes).getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return null;
        }
    }
}