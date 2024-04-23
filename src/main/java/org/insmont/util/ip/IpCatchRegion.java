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

import org.lionsoul.ip2region.xdb.Searcher;

import java.io.*;
import java.util.concurrent.TimeUnit;

/**
 * @author chuhelan
 * @version 1.0
 * @date Wednesday 31 January 2024 12:57 AM
 * @package: org.insmont.util.ip
 * @Desc:
 */
public class IpCatchRegion {

    public static String cityRegion(String ip) throws Exception {

        if (ip.equals("0:0:0:0:0:0:0:1") || ip.equals("::1")) {
            return "Insmont 数据中心";
        }

        try (InputStream inputStream = IpCatchRegion.class.getResourceAsStream("/ip2region.xdb")) {

            File tempFile = File.createTempFile("ip2region", "temp");
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            try (RandomAccessFile raf = new RandomAccessFile(tempFile, "r")) {
                byte[] vIndex = new byte[(int) raf.length()];

                int totalBytesRead = 0;
                int bytesRead;
                while (totalBytesRead < vIndex.length && (bytesRead = raf.read(vIndex, totalBytesRead, vIndex.length - totalBytesRead)) != -1) {
                    totalBytesRead += bytesRead;
                }

                Searcher searcher = new Searcher(tempFile.getAbsolutePath(), vIndex, vIndex);
                try {
                    long sTime = System.nanoTime();
                    String region = searcher.search(ip);
                    long cost = TimeUnit.NANOSECONDS.toMicros((long) (System.nanoTime() - sTime));
                    System.out.printf("{region: %s, ioCount: %d, took: %d μs}\n", region, searcher.getIOCount(), cost);
                    return region != null ? region : "Unknown";
                } finally {
                    searcher.close();
                }
            } finally {
                tempFile.delete();
            }
        }
    }
}