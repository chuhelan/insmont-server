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

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author chuhelan
 * @version 1.0
 * @date Wednesday 31 January 2024 12:57 AM
 * @package: org.insmont.util.ip
 * @Desc:
 */
public class IpCatchRegion {

    static String dbPath = "src/main/resources/ip2region.xdb";

    public static String cityRegion(String ip) throws Exception {
        byte[] vIndex = new byte[0];

        try {
            vIndex = Searcher.loadVectorIndexFromFile(dbPath);
        } catch (Exception e) {
            System.out.printf("failed to load vector index from `%s`: %s\n", dbPath, e);
        }

        Searcher searcher = null;
        try {
            searcher = Searcher.newWithVectorIndex(dbPath, vIndex);
        } catch (Exception e) {
            System.out.printf("failed to create vectorIndex cached searcher with `%s`: %s\n", dbPath, e);
        }

        try {
            long sTime = System.nanoTime();
            assert searcher != null;
            String region = searcher.search(ip);
            long cost = TimeUnit.NANOSECONDS.toMicros((long) (System.nanoTime() - sTime));
            System.out.printf("{region: %s, ioCount: %d, took: %d μs}\n", region, searcher.getIOCount(), cost);
            return region;
        } catch (Exception e) {
            System.out.printf("failed to search(%s): %s\n", ip, e);
        }
        assert searcher != null;
        searcher.close();
        return "未知";
    }

}
