/**
 * blackduck-alert
 *
 * Copyright (c) 2019 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.alert.channel.email.template;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.gson.Gson;
import com.synopsys.integration.alert.common.AlertProperties;
import com.synopsys.integration.alert.common.message.model.MessageContentGroup;

@Component
public class EmailAttachmentFileCreator {
    private final Logger logger = LoggerFactory.getLogger(EmailAttachmentFileCreator.class);
    private final AlertProperties alertProperties;
    private final Gson gson;

    @Autowired
    public EmailAttachmentFileCreator(AlertProperties alertProperties, Gson gson) {
        this.alertProperties = alertProperties;
        this.gson = gson;
    }

    public Optional<File> createAttachmentFile(EmailAttachmentFormat attachmentFormat, MessageContentGroup message) {
        String alertEmailAttachmentsDirName = alertProperties.getAlertEmailAttachmentsDir();

        try {
            File emailAttachmentsDir = new File(alertEmailAttachmentsDirName);
            File formattedFile = createFormattedFile(attachmentFormat, message, emailAttachmentsDir);
            return Optional.ofNullable(formattedFile);
        } catch (SecurityException | IOException | JAXBException e) {
            logger.warn("Unable to create {} email attachment file", attachmentFormat.name());
            logger.debug("Attachment failure", e);
        }
        return Optional.empty();
    }

    private File createFormattedFile(EmailAttachmentFormat attachmentFormat, MessageContentGroup messageContentGroup, File writeDir) throws IOException, JAXBException {
        switch (attachmentFormat) {
            case CSV:
                return createCsvFile(messageContentGroup, writeDir);
            case JSON:
                return createJsonFile(messageContentGroup, writeDir);
            case XML:
                return createXmlFile(messageContentGroup, writeDir);
            default:
                return null;
        }
    }

    private File createJsonFile(MessageContentGroup messageContentGroup, File writeDir) throws IOException {
        String jsonMessage = gson.toJson(messageContentGroup);
        return createFile(jsonMessage, writeDir, "json");
    }

    private File createCsvFile(MessageContentGroup messageContentGroup, File writeDir) throws IOException {
        // FIXME implement
        return createFile("", writeDir, "csv");
    }

    private File createXmlFile(MessageContentGroup messageContentGroup, File writeDir) throws IOException {
        XmlMapper xmlMapper = new XmlMapper();
        String xmlMessage = xmlMapper.writeValueAsString(messageContentGroup);
        return createFile(xmlMessage, writeDir, "xml");
    }

    private File createFile(String messageString, File writeDir, String fileExtension) throws IOException {
        File messageFile = new File(writeDir, "message." + fileExtension);
        FileUtils.writeStringToFile(messageFile, messageString, Charset.defaultCharset());
        return messageFile;
    }

}
