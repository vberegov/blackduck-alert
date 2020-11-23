package com.synopsys.integration.alert.component.tasks.web;

import java.net.URI;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.synopsys.integration.alert.util.AlertIntegrationTest;

public class TaskControllerTestIT extends AlertIntegrationTest {
    @Autowired
    protected WebApplicationContext webApplicationContext;

    @Autowired
    protected HttpSessionCsrfTokenRepository csrfTokenRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(SecurityMockMvcConfigurers.springSecurity()).build();
    }

    @Test
    @WithMockUser(roles = AlertIntegrationTest.ROLE_ALERT_ADMIN)
    public void testRead() throws Exception {
        String url = TaskController.TASK_BASE_PATH;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI(url))
                                                    .with(SecurityMockMvcRequestPostProcessors.user("admin").roles(AlertIntegrationTest.ROLE_ALERT_ADMIN))
                                                    .with(SecurityMockMvcRequestPostProcessors.csrf());
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testReadInvalidUser() throws Exception {
        String url = TaskController.TASK_BASE_PATH;
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get(new URI(url))
                                                    .with(SecurityMockMvcRequestPostProcessors.user("admin"))
                                                    .with(SecurityMockMvcRequestPostProcessors.csrf());
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isForbidden());
    }
}