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

package org.insmont.util.upload;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chuhelan
 * @version 1.0
 * @date Saturday 02 March 2024 10:56 PM
 * @package: org.insmont.util.upload
 * @Desc:
 */

@Slf4j
@Component
public class UploadImage {

    @Value("${oss.upload.url}")
    private String UPLOAD_URL;

    @Value("${oss.upload.authorization}")
    private String AUTHORIZATION;

    private static final String BOUNDARY = "boundary";


    public String uploadImage(MultipartFile imageFile) throws Exception {
        URL url = new URL(UPLOAD_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
        connection.setRequestProperty("Authorization", "Bearer " + AUTHORIZATION);

        try (OutputStream os = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true)) {

            writer.println("--" + BOUNDARY);
            writer.println("Content-Disposition: form-data; name=\"file\"; filename=\"" + imageFile.getOriginalFilename() + "\"");
            writer.println("Content-Type: image/png");
            writer.println();

            try (InputStream is = imageFile.getInputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            }

            writer.println();
            writer.println("--" + BOUNDARY + "--");
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                log.info("上传图像的url为: " + JsonPath.read(response.toString(), "$.data.links.url"));
                return JsonPath.read(response.toString(), "$.data.links.url");
            }
        } else {
            throw new IOException("Failed to upload image. Response code: " + responseCode);
        }
    }
}
