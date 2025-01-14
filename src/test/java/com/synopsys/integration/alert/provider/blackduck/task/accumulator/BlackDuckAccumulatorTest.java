package com.synopsys.integration.alert.provider.blackduck.task.accumulator;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.alert.common.event.EventManager;
import com.synopsys.integration.alert.common.event.NotificationReceivedEvent;
import com.synopsys.integration.alert.common.persistence.accessor.NotificationAccessor;
import com.synopsys.integration.alert.common.persistence.accessor.ProviderTaskPropertiesAccessor;
import com.synopsys.integration.alert.descriptor.api.BlackDuckProviderKey;
import com.synopsys.integration.alert.provider.blackduck.BlackDuckProperties;
import com.synopsys.integration.alert.provider.blackduck.task.accumulator.BlackDuckNotificationRetriever.BlackDuckNotificationPage;
import com.synopsys.integration.alert.provider.blackduck.validator.BlackDuckValidator;
import com.synopsys.integration.blackduck.api.manual.enumeration.NotificationType;
import com.synopsys.integration.blackduck.api.manual.view.NotificationView;
import com.synopsys.integration.blackduck.api.manual.view.ProjectNotificationView;
import com.synopsys.integration.exception.IntegrationException;

public class BlackDuckAccumulatorTest {
    private static final BlackDuckProviderKey BLACK_DUCK_PROVIDER_KEY = new BlackDuckProviderKey();

    /**
     * This test should simulate a normal run of the accumulator with notifications present.
     */
    @Test
    public void runTest() throws IntegrationException {
        ProviderTaskPropertiesAccessor taskPropertiesAccessor = Mockito.mock(ProviderTaskPropertiesAccessor.class);
        BlackDuckProperties blackDuckProperties = createBlackDuckProperties();
        BlackDuckValidator validator = createBlackDuckValidator(blackDuckProperties, true);

        BlackDuckNotificationPage notificationPage = createMockNotificationPage();
        BlackDuckNotificationRetriever notificationRetriever = Mockito.mock(BlackDuckNotificationRetriever.class);
        Mockito.when(notificationRetriever.retrievePageOfFilteredNotifications(Mockito.any(), Mockito.anyList())).thenReturn(notificationPage);
        BlackDuckNotificationRetrieverFactory notificationRetrieverFactory = createBlackDuckNotificationRetrieverFactory(blackDuckProperties, notificationRetriever);

        NotificationAccessor notificationAccessor = Mockito.mock(NotificationAccessor.class);
        Mockito.when(notificationAccessor.saveAllNotifications(Mockito.anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        EventManager eventManager = Mockito.mock(EventManager.class);
        Mockito.doNothing().when(eventManager).sendEvent(Mockito.any(NotificationReceivedEvent.class));

        BlackDuckAccumulator accumulator = new BlackDuckAccumulator(BLACK_DUCK_PROVIDER_KEY, null, notificationAccessor, taskPropertiesAccessor, blackDuckProperties, validator, eventManager, notificationRetrieverFactory);
        accumulator.run();

        Mockito.verify(notificationAccessor, Mockito.times(1)).saveAllNotifications(Mockito.anyList());
    }

    @Test
    public void runValidateFalseTest() {
        BlackDuckProperties invalidProperties = createBlackDuckProperties();
        BlackDuckValidator validator = createBlackDuckValidator(invalidProperties, false);
        BlackDuckNotificationRetrieverFactory notificationRetrieverFactory = Mockito.mock(BlackDuckNotificationRetrieverFactory.class);

        BlackDuckAccumulator accumulator = new BlackDuckAccumulator(BLACK_DUCK_PROVIDER_KEY, null, null, null, invalidProperties, validator, null, notificationRetrieverFactory);
        accumulator.run();

        Mockito.verify(notificationRetrieverFactory, Mockito.times(0)).createBlackDuckNotificationRetriever(invalidProperties);
    }

    @Test
    public void runCreateNotificationRetrieverEmptyTest() {
        ProviderTaskPropertiesAccessor taskPropertiesAccessor = Mockito.mock(ProviderTaskPropertiesAccessor.class);
        BlackDuckProperties blackDuckProperties = createBlackDuckProperties();
        BlackDuckValidator validator = createBlackDuckValidator(blackDuckProperties, true);
        BlackDuckNotificationRetrieverFactory notificationRetrieverFactory = createBlackDuckNotificationRetrieverFactory(blackDuckProperties, null);

        BlackDuckAccumulator accumulator = new BlackDuckAccumulator(BLACK_DUCK_PROVIDER_KEY, null, null, taskPropertiesAccessor, blackDuckProperties, validator, null, notificationRetrieverFactory);
        accumulator.run();

        Mockito.verify(taskPropertiesAccessor, Mockito.times(0)).getTaskProperty(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void runNotificationRetrieverThrowsException() throws IntegrationException {
        ProviderTaskPropertiesAccessor taskPropertiesAccessor = Mockito.mock(ProviderTaskPropertiesAccessor.class);
        BlackDuckProperties blackDuckProperties = createBlackDuckProperties();
        BlackDuckValidator validator = createBlackDuckValidator(blackDuckProperties, true);

        BlackDuckNotificationRetriever notificationRetriever = Mockito.mock(BlackDuckNotificationRetriever.class);
        Mockito.when(notificationRetriever.retrievePageOfFilteredNotifications(Mockito.any(), Mockito.anyList())).thenThrow(new IntegrationException("Test Exception"));
        BlackDuckNotificationRetrieverFactory notificationRetrieverFactory = createBlackDuckNotificationRetrieverFactory(blackDuckProperties, notificationRetriever);

        NotificationAccessor notificationAccessor = Mockito.mock(NotificationAccessor.class);

        BlackDuckAccumulator accumulator = new BlackDuckAccumulator(BLACK_DUCK_PROVIDER_KEY, null, notificationAccessor, taskPropertiesAccessor, blackDuckProperties, validator, null, notificationRetrieverFactory);
        accumulator.run();

        Mockito.verify(notificationAccessor, Mockito.times(0)).saveAllNotifications(Mockito.anyList());
    }

    private BlackDuckProperties createBlackDuckProperties() {
        BlackDuckProperties blackDuckProperties = Mockito.mock(BlackDuckProperties.class);
        Mockito.when(blackDuckProperties.getConfigId()).thenReturn(0L);
        return blackDuckProperties;
    }

    private BlackDuckValidator createBlackDuckValidator(BlackDuckProperties blackDuckProperties, boolean validationResult) {
        BlackDuckValidator validator = Mockito.mock(BlackDuckValidator.class);
        Mockito.when(validator.validate(Mockito.eq(blackDuckProperties))).thenReturn(validationResult);
        return validator;
    }

    private BlackDuckNotificationRetrieverFactory createBlackDuckNotificationRetrieverFactory(BlackDuckProperties blackDuckProperties, @Nullable BlackDuckNotificationRetriever notificationRetriever) {
        BlackDuckNotificationRetrieverFactory notificationRetrieverFactory = Mockito.mock(BlackDuckNotificationRetrieverFactory.class);
        Mockito.when(notificationRetrieverFactory.createBlackDuckNotificationRetriever(blackDuckProperties)).thenReturn(Optional.ofNullable(notificationRetriever));
        return notificationRetrieverFactory;
    }

    private BlackDuckNotificationPage createMockNotificationPage() throws IntegrationException {
        BlackDuckNotificationPage notificationPage = Mockito.mock(BlackDuckNotificationPage.class);
        Mockito.when(notificationPage.isEmpty()).thenReturn(false, true);
        Mockito.when(notificationPage.retrieveNextPage()).thenReturn(notificationPage);

        NotificationView notificationView = createMockNotificationView();
        Mockito.when(notificationPage.getCurrentNotifications()).thenReturn(List.of(notificationView));

        return notificationPage;
    }

    private NotificationView createMockNotificationView() {
        NotificationView notificationView = Mockito.mock(ProjectNotificationView.class);
        Mockito.when(notificationView.getCreatedAt()).thenReturn(new Date());
        Mockito.when(notificationView.getType()).thenReturn(NotificationType.PROJECT);
        Mockito.when(notificationView.getJson()).thenReturn("{}");
        return notificationView;
    }

}
