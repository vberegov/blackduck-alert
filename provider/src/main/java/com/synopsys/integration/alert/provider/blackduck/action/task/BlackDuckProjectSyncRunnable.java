package com.synopsys.integration.alert.provider.blackduck.action.task;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.alert.provider.blackduck.BlackDuckProperties;
import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.api.generated.view.ProjectView;
import com.synopsys.integration.blackduck.api.generated.view.UserView;
import com.synopsys.integration.blackduck.http.client.BlackDuckHttpClient;
import com.synopsys.integration.blackduck.service.BlackDuckService;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.blackduck.service.dataservice.ProjectService;
import com.synopsys.integration.blackduck.service.dataservice.ProjectUsersService;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.Slf4jIntLogger;

public class BlackDuckProjectSyncRunnable implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final BlackDuckProperties blackDuckProperties;
    private final Collection<String> blackDuckProjectNames;

    public BlackDuckProjectSyncRunnable(BlackDuckProperties blackDuckProperties, Collection<String> blackDuckProjectNames) {
        this.blackDuckProperties = blackDuckProperties;
        this.blackDuckProjectNames = blackDuckProjectNames;
    }

    @Override
    public void run() {
        try {
            Slf4jIntLogger intLogger = new Slf4jIntLogger(logger);
            BlackDuckHttpClient blackDuckHttpClient = blackDuckProperties.createBlackDuckHttpClient(intLogger);
            BlackDuckServicesFactory blackDuckServicesFactory = blackDuckProperties.createBlackDuckServicesFactory(blackDuckHttpClient, intLogger);

            BlackDuckService blackDuckService = blackDuckServicesFactory.getBlackDuckService();
            ProjectService projectService = blackDuckServicesFactory.createProjectService();
            ProjectUsersService projectUsersService = blackDuckServicesFactory.createProjectUsersService();

            UserView currentUser = blackDuckService.getResponse(ApiDiscovery.CURRENT_USER_LINK_RESPONSE);
            List<ProjectView> projectViews = requestAllProjectsByName(projectService, blackDuckProjectNames);
            updateBlackDuckProjectPermissions(projectUsersService, currentUser, projectViews);
        } catch (Exception e) {
            logger.warn("{} failed: {}", getClass().getSimpleName(), e.getMessage());
        }
    }

    // FIXME improve performance of this call
    private List<ProjectView> requestAllProjectsByName(ProjectService projectService, Collection<String> projectNames) throws IntegrationException {
        return projectService.getAllProjects()
                   .stream()
                   .filter(projectView -> projectNames.contains(projectView.getName()))
                   .collect(Collectors.toList());
    }

    private void updateBlackDuckProjectPermissions(ProjectUsersService projectUsersService, UserView userToAdd, List<ProjectView> projectViews) throws IntegrationException {
        for (ProjectView projectView : projectViews) {
            logger.debug("Adding user to Project {}", projectView.getName());
            projectUsersService.addUserToProject(projectView, userToAdd);
        }
    }

}
