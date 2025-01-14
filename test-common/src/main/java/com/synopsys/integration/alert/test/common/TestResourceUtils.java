/*
 * test-common
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.alert.test.common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

public final class TestResourceUtils {
    public static final String DEFAULT_PROPERTIES_FILE_LOCATION = "test.properties";
    public static final File BASE_TEST_RESOURCE_DIR = new File(TestResourceUtils.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "../../../../src/test/resources/");

    /**
     * @param resourcePath The path to the file resource. For example: If the file is in src/test/resources/dir1/dir2/file.ext, then use "dir1/dir2/file.ext"
     * @return The file contents, never null
     * @throws IOException Thrown by {@link FileUtils} if the file cannot be read
     */
    public static String readFileToString(String resourcePath) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(resourcePath);
        File jsonFile = classPathResource.getFile();
        return FileUtils.readFileToString(jsonFile, Charset.defaultCharset());
    }

    public static Properties loadProperties(String resourceLocation) throws IOException {
        Properties properties = new Properties();
        ClassPathResource classPathResource = new ClassPathResource(resourceLocation);
        try (InputStream iStream = classPathResource.getInputStream()) {
            properties.load(iStream);
        }
        return properties;
    }

    private TestResourceUtils() {
    }

}
