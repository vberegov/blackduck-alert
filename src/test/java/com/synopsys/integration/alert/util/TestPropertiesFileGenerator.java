package com.synopsys.integration.alert.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.alert.test.common.TestPropertyKey;
import com.synopsys.integration.alert.test.common.TestResourceUtils;

public class TestPropertiesFileGenerator {
    @Test
    @Disabled("This test is to generate the test.properties for new developers.")
    public void generatePropertiesFile() throws IOException {
        String propertiesFileName = TestResourceUtils.BASE_TEST_RESOURCE_DIR + "/" + TestResourceUtils.DEFAULT_PROPERTIES_FILE_LOCATION;
        System.out.println("Generating file: " + propertiesFileName + "..");

        File testPropertiesFile = new File(propertiesFileName);
        if (!testPropertiesFile.exists()) {
            boolean successfullyCreated = testPropertiesFile.createNewFile();
            if (!successfullyCreated) {
                System.out.println("There was a problem creating the file '" + propertiesFileName + "'.");
                return;
            }

            StringBuilder dataBuilder = new StringBuilder();
            for (TestPropertyKey propertyKey : TestPropertyKey.values()) {
                dataBuilder.append(propertyKey.getPropertyKey());
                dataBuilder.append('=');
                dataBuilder.append(System.lineSeparator());
            }
            FileUtils.write(testPropertiesFile, dataBuilder.toString(), Charset.defaultCharset(), false);
        } else {
            System.out.println("The file '" + propertiesFileName + "' already exists, please rename or back it up.");
        }
    }

}
