package com.synopsys.integration.alert.channel.email.action;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.gson.Gson;
import com.synopsys.integration.alert.channel.email.attachment.EmailAttachmentFileCreator;
import com.synopsys.integration.alert.channel.email.attachment.MessageContentGroupCsvCreator;
import com.synopsys.integration.alert.channel.email.distribution.EmailAddressGatherer;
import com.synopsys.integration.alert.channel.email.distribution.EmailChannelMessageSender;
import com.synopsys.integration.alert.common.action.TestAction;
import com.synopsys.integration.alert.common.channel.template.FreemarkerTemplatingService;
import com.synopsys.integration.alert.common.enumeration.ConfigContextEnum;
import com.synopsys.integration.alert.common.enumeration.EmailPropertyKeys;
import com.synopsys.integration.alert.common.exception.AlertException;
import com.synopsys.integration.alert.common.message.model.MessageResult;
import com.synopsys.integration.alert.common.persistence.accessor.ConfigurationAccessor;
import com.synopsys.integration.alert.common.persistence.accessor.FieldUtility;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationFieldModel;
import com.synopsys.integration.alert.common.persistence.model.ConfigurationModel;
import com.synopsys.integration.alert.common.rest.model.FieldModel;
import com.synopsys.integration.alert.common.rest.model.FieldValueModel;
import com.synopsys.integration.alert.descriptor.api.model.ChannelKeys;
import com.synopsys.integration.alert.test.common.MockAlertProperties;
import com.synopsys.integration.alert.test.common.TestProperties;
import com.synopsys.integration.alert.test.common.TestPropertyKey;
import com.synopsys.integration.alert.test.common.TestTags;

public class EmailGlobalTestActionTest {
    @Test
    public void testConfigValidTest() throws AlertException {
        EmailChannelMessageSender emailChannelMessageSender = Mockito.mock(EmailChannelMessageSender.class);
        Mockito.when(emailChannelMessageSender.sendMessages(Mockito.any(), Mockito.any(), Mockito.anyList())).thenReturn(new MessageResult("PASS"));

        EmailGlobalTestAction emailGlobalTestAction = new EmailGlobalTestAction(emailChannelMessageSender);

        FieldModel validFieldModel = createFieldModelToTest("noreply@synopsys.com");

        try {
            MessageResult messageResult = emailGlobalTestAction.testConfig("0", validFieldModel, new FieldUtility(Map.of()));
            assertFalse(messageResult.hasErrors(), "Expected the message result to not have errors");
            assertFalse(messageResult.hasWarnings(), "Expected the message result to not have warnings");
        } catch (AlertException e) {
            fail("An exception was thrown where none was expected", e);
        }
    }

    @Test
    public void testConfigMissingDestinationTest() {
        ConfigurationModel globalConfig = new ConfigurationModel(0L, 0L, null, null, ConfigContextEnum.GLOBAL);
        ConfigurationAccessor configurationAccessor = Mockito.mock(ConfigurationAccessor.class);
        Mockito.when(configurationAccessor.getConfigurationsByDescriptorKeyAndContext(Mockito.eq(ChannelKeys.EMAIL), Mockito.eq(ConfigContextEnum.GLOBAL))).thenReturn(List.of(globalConfig));

        EmailAddressGatherer emailAddressGatherer = Mockito.mock(EmailAddressGatherer.class);
        Mockito.when(emailAddressGatherer.gatherEmailAddresses(Mockito.any(), Mockito.any())).thenReturn(Set.of());

        FreemarkerTemplatingService freemarkerTemplatingService = new FreemarkerTemplatingService();
        EmailChannelMessageSender emailChannelMessageSender = new EmailChannelMessageSender(ChannelKeys.EMAIL, null, emailAddressGatherer, null, freemarkerTemplatingService, configurationAccessor);

        EmailGlobalTestAction emailGlobalTestAction = new EmailGlobalTestAction(emailChannelMessageSender);

        FieldModel validFieldModel = createFieldModelToTest("");

        try {
            emailGlobalTestAction.testConfig("0", validFieldModel, new FieldUtility(Map.of()));
            fail("Expected an exception to be thrown");
        } catch (AlertException e) {
            // Pass
        }
    }

    @Test
    public void testConfigInvalidDestinationTest() {
        EmailGlobalTestAction emailGlobalTestAction = new EmailGlobalTestAction(null);

        FieldModel validFieldModel = createFieldModelToTest("not a valid email address");

        try {
            emailGlobalTestAction.testConfig("0", validFieldModel, new FieldUtility(Map.of()));
            fail("Expected an exception to be thrown");
        } catch (AlertException e) {
            // Pass
        }
    }

    @Test
    @Tags(value = {
        @Tag(TestTags.DEFAULT_INTEGRATION),
        @Tag(TestTags.CUSTOM_EXTERNAL_CONNECTION)
    })
    public void testConfigITTest() {
        TestProperties testProperties = new TestProperties();
        String emailAddress = testProperties.getProperty(TestPropertyKey.TEST_EMAIL_RECIPIENT);

        EmailChannelMessageSender emailChannelMessageSender = createValidEmailChannelMessageSender(emailAddress);

        EmailGlobalTestAction emailGlobalTestAction = new EmailGlobalTestAction(emailChannelMessageSender);

        FieldModel validFieldModel = createFieldModelToTest(emailAddress);
        FieldUtility validFieldUtility = createValidEmailGlobalFieldUtility(testProperties);

        try {
            MessageResult messageResult = emailGlobalTestAction.testConfig("0", validFieldModel, validFieldUtility);
            assertFalse(messageResult.hasErrors(), "Expected the message result to not have errors");
            assertFalse(messageResult.hasWarnings(), "Expected the message result to not have warnings");
        } catch (AlertException e) {
            fail("An exception was thrown where none was expected", e);
        }
    }

    private FieldModel createFieldModelToTest(String destinationValue) {
        Map<String, FieldValueModel> keyToValues = new HashMap<>();

        Set<String> values = null != destinationValue ? Set.of(destinationValue) : Set.of();

        FieldValueModel destinationFieldValueModel = new FieldValueModel(values, false);
        keyToValues.put(TestAction.KEY_DESTINATION_NAME, destinationFieldValueModel);

        return new FieldModel(null, null, keyToValues);
    }

    private EmailChannelMessageSender createValidEmailChannelMessageSender(String emailAddress) {
        MockAlertProperties testAlertProperties = new MockAlertProperties();

        EmailAddressGatherer emailAddressGatherer = Mockito.mock(EmailAddressGatherer.class);
        Mockito.when(emailAddressGatherer.gatherEmailAddresses(Mockito.any(), Mockito.any())).thenReturn(Set.of(emailAddress));

        Gson gson = new Gson();
        MessageContentGroupCsvCreator messageContentGroupCsvCreator = new MessageContentGroupCsvCreator();
        EmailAttachmentFileCreator emailAttachmentFileCreator = new EmailAttachmentFileCreator(testAlertProperties, messageContentGroupCsvCreator, gson);
        FreemarkerTemplatingService freemarkerTemplatingService = new FreemarkerTemplatingService();

        return new EmailChannelMessageSender(ChannelKeys.EMAIL, testAlertProperties, emailAddressGatherer, emailAttachmentFileCreator, freemarkerTemplatingService, null);
    }

    private FieldUtility createValidEmailGlobalFieldUtility(TestProperties testProperties) {
        Map<String, ConfigurationFieldModel> configuredFields = new HashMap<>();
        addConfigurationFieldToMap(configuredFields, EmailPropertyKeys.JAVAMAIL_HOST_KEY.getPropertyKey(), testProperties.getProperty(TestPropertyKey.TEST_EMAIL_SMTP_HOST));
        addConfigurationFieldToMap(configuredFields, EmailPropertyKeys.JAVAMAIL_FROM_KEY.getPropertyKey(), testProperties.getProperty(TestPropertyKey.TEST_EMAIL_SMTP_FROM));

        testProperties.getOptionalProperty(TestPropertyKey.TEST_EMAIL_SMTP_USER).ifPresent(prop -> addConfigurationFieldToMap(configuredFields, EmailPropertyKeys.JAVAMAIL_USER_KEY.getPropertyKey(), prop));
        testProperties.getOptionalProperty(TestPropertyKey.TEST_EMAIL_SMTP_PASSWORD).ifPresent(prop -> addConfigurationFieldToMap(configuredFields, EmailPropertyKeys.JAVAMAIL_PASSWORD_KEY.getPropertyKey(), prop));
        testProperties.getOptionalProperty(TestPropertyKey.TEST_EMAIL_SMTP_EHLO).ifPresent(prop -> addConfigurationFieldToMap(configuredFields, EmailPropertyKeys.JAVAMAIL_EHLO_KEY.getPropertyKey(), prop));
        testProperties.getOptionalProperty(TestPropertyKey.TEST_EMAIL_SMTP_AUTH).ifPresent(prop -> addConfigurationFieldToMap(configuredFields, EmailPropertyKeys.JAVAMAIL_AUTH_KEY.getPropertyKey(), prop));
        testProperties.getOptionalProperty(TestPropertyKey.TEST_EMAIL_SMTP_PORT).ifPresent(prop -> addConfigurationFieldToMap(configuredFields, EmailPropertyKeys.JAVAMAIL_PORT_KEY.getPropertyKey(), prop));

        return new FieldUtility(configuredFields);
    }

    private void addConfigurationFieldToMap(Map<String, ConfigurationFieldModel> configuredFields, String key, String value) {
        ConfigurationFieldModel configurationFieldModel = ConfigurationFieldModel.create(key);
        configurationFieldModel.setFieldValue(value);
        configuredFields.put(key, configurationFieldModel);
    }

}
