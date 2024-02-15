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

package org.insmont.util.identicon;

import com.jayway.jsonpath.JsonPath;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author chuhelan
 * @version 1.0
 * @date Thursday 15 February 2024 1:41 PM
 * @package: org.insmont.util.identIcon
 * @Desc:
 */


@Async
@Component
public class IdentIconUtil {

    private static final int SIZE = 420;
    private static final int GRID_SIZE = 5;
    private static final int PIXEL_SIZE = SIZE / GRID_SIZE;
    private static final long MASK = 0x1FFFFFFL;

    private static final String UPLOAD_URL = "https://image.chuhelan.com/api/v1/upload";
    private static final String BOUNDARY = "boundary";

    public static BufferedImage generateIdenticon(String username) {
        byte[] hash = generateHash(username);

        BufferedImage identicon = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = identicon.createGraphics();

        Color color = getColorFromHash(hash);

        long bits = (hash[0] & 0xFFL) << 24 | (hash[1] & 0xFFL) << 16 | (hash[2] & 0xFFL) << 8 | (hash[3] & 0xFFL);
        bits &= MASK;

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE / 2; j++) {
                g.setColor((bits & 1) == 1 ? color : Color.WHITE);
                g.fillRect(j * PIXEL_SIZE, i * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);

                int mirrorJ = GRID_SIZE - j - 1;
                g.setColor((bits & 1) == 1 ? color : Color.WHITE);
                g.fillRect(mirrorJ * PIXEL_SIZE, i * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);

                bits >>= 1;
            }
        }

        if (GRID_SIZE % 2 == 1) {
            int center = GRID_SIZE / 2;
            for (int i = 0; i < GRID_SIZE; i++) {
                g.setColor((bits & 1) == 1 ? color : Color.WHITE);
                g.fillRect(center * PIXEL_SIZE, i * PIXEL_SIZE, PIXEL_SIZE, PIXEL_SIZE);

                bits >>= 1;
            }
        }

        g.dispose();
        return identicon;
    }

    private static Color getColorFromHash(byte[] hash) {
        int red = hash[0] & 0xFF;
        int green = hash[1] & 0xFF;
        int blue = hash[2] & 0xFF;
        return new Color(red, green, blue);
    }

    private static byte[] generateHash(String username) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(username.getBytes());
        } catch (NoSuchAlgorithmException e) {
            return new byte[0];
        }
    }

    private static File convertBufferedImageToFile(BufferedImage bufferedImage) throws IOException {
        File tempFile = File.createTempFile("temp-image", ".png");
        ImageIO.write(bufferedImage, "png", tempFile);
        return tempFile;
    }

    public static String getUploadUrl(String username) {
        BufferedImage avatar = generateIdenticon(username);
        String result = "";
        try {
            File image = convertBufferedImageToFile(avatar);
            String imageUrl = uploadFile(image);
            System.out.println("Uploaded image URL: " + imageUrl);
            result = JsonPath.read(imageUrl, "$.data.links.url");
            Files.delete(image.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String uploadFile(File imageFile) throws IOException {
        URL url = new URL(UPLOAD_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

        try (OutputStream os = connection.getOutputStream();
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, "UTF-8"), true)) {

            writer.println("--" + BOUNDARY);
            writer.println("Content-Disposition: form-data; name=\"file\"; filename=\"" + imageFile.getName() + "\"");
            writer.println("Content-Type: image/png");
            writer.println();

            try (InputStream is = new FileInputStream(imageFile)) {
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
                return response.toString();
            }
        } else {
            throw new IOException("Failed to upload image. Response code: " + responseCode);
        }
    }
}