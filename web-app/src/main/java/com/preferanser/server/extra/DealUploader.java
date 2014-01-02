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

package com.preferanser.server.extra;

import org.apache.http.HttpResponse;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Creates predefined deals on the backend
 */
public class DealUploader {

    private static final Logger logger = LoggerFactory.getLogger(DealUploader.class);

    private List<File> jsonFiles;
    private String url;
    private final String authCookie;

    public DealUploader(String url, String authCookie, String jsonDealsPath) throws IOException {
        this.url = url;
        this.authCookie = authCookie;
        jsonFiles = newArrayList();

        Files.walkFileTree(new File(jsonDealsPath).toPath(), new SimpleFileVisitor<Path>() {
            @Override public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                File file = path.toFile();
                if (attrs.isRegularFile() && file.getName().endsWith(".json")) {
                    jsonFiles.add(file);
                }
                return super.visitFile(path, attrs);
            }
        });
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

    public static void main(String[] args) throws IOException {
        if (args.length < 3) {
            System.out.println("Usage: DealUploader <server-url> ('deal.json')+");
            System.exit(1);
        }
        DealUploader dealUploader = new DealUploader(args[0], args[1], args[2]);
        dealUploader.start();
    }

}
