/*
 * Preferanser is a program to simulate and calculate Russian Preferans Card game deals.
 *
 *     Copyright (C) 2013  Yuriy Lazarev <Yuriy.Lazarev@gmail.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see [http://www.gnu.org/licenses/].
 */

package com.preferanser.server.client;

import com.google.common.collect.Lists;
import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

/**
 * Creates predefined deals on the backend
 */
public class DealUploader {

    private static final Logger logger = LoggerFactory.getLogger(DealUploader.class);

    private List<File> jsonFiles;
    private String url;
    private final String authCookie;

    public static void main(String[] args) throws IOException, URISyntaxException {
        if (args.length < 3) {
            System.out.println("Usage: DealUploader <server-url> <auth-cookie> <json-dir>");
            System.exit(1);
        }
        new DealUploader(args[0], args[1], args[2]).start();
    }

    public DealUploader(String url, String authCookie, String jsonDealsPath) throws IOException, URISyntaxException {
        this.url = url;
        this.authCookie = authCookie;
        jsonFiles = discoverJsonFiles(jsonDealsPath);
    }

    private void start() throws IOException {
        for (File jsonFile : jsonFiles) {
            logger.info("Posting json from file {} ...", jsonFile);
            HttpResponse response = Request
                .Post(url)
                .addHeader("Cookie", authCookie)
                .bodyFile(jsonFile, ContentType.APPLICATION_JSON)
                .execute()
                .returnResponse();
            logger.info("Response: {}", response);
        }
    }

    private List<File> discoverJsonFiles(String jsonDealsPath) throws URISyntaxException, IOException {
        final List<File> jsonFiles = Lists.newArrayList();
        Path path = Paths.get(DealUploader.class.getResource(jsonDealsPath).toURI());
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                File file = path.toFile();
                if (attrs.isRegularFile() && file.getName().endsWith(".json"))
                    jsonFiles.add(file);
                return super.visitFile(path, attrs);
            }
        });
        return jsonFiles;
    }

}
