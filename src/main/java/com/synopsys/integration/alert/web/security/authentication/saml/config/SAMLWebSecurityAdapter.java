/**
 * blackduck-alert
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.alert.web.security.authentication.saml.config;

import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;

import org.apache.activemq.broker.BrokerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.synopsys.integration.alert.common.AlertProperties;
import com.synopsys.integration.alert.common.descriptor.accessor.AuthorizationUtility;
import com.synopsys.integration.alert.common.persistence.model.UserRoleModel;
import com.synopsys.integration.alert.web.security.HttpPathManager;
import com.synopsys.integration.alert.web.security.authentication.saml.AlertFilterChainProxy;
import com.synopsys.integration.alert.web.security.authentication.saml.SAMLContext;
import com.synopsys.integration.alert.web.security.authentication.saml.SamlAntMatcher;

@Configuration
@Order(110)
public class SAMLWebSecurityAdapter extends WebSecurityConfigurerAdapter implements InitializingBean {
    private final HttpPathManager httpPathManager;
    private final AlertProperties alertProperties;
    private final Logger logger = LoggerFactory.getLogger(SAMLWebSecurityAdapter.class);
    private final AuthorizationUtility authorizationUtility;
    private final CsrfTokenRepository csrfTokenRepository;
    private final SAMLContext samlContext;
    private final SAMLAuthenticationProvider samlAuthenticationProvider;
    private final SAMLEntryPoint samlEntryPoint;
    private final MetadataGeneratorFilter metadataGeneratorFilter;
    private final SAMLLogoutFilter samlLogoutFilter;
    private final SAMLLogoutProcessingFilter samlLogoutProcessingFilter;
    private final SAMLProcessingFilter samlWebSSOProcessingFilter;

    @Autowired
    public SAMLWebSecurityAdapter(HttpPathManager httpPathManager, AlertProperties alertProperties, AuthorizationUtility authorizationUtility, CsrfTokenRepository csrfTokenRepository,
        SAMLContext samlContext, SAMLAuthenticationProvider samlAuthenticationProvider, SAMLEntryPoint samlEntryPoint, MetadataGeneratorFilter metadataGeneratorFilter, SAMLLogoutFilter samlLogoutFilter,
        SAMLLogoutProcessingFilter samlLogoutProcessingFilter, SAMLProcessingFilter samlWebSSOProcessingFilter) {
        this.httpPathManager = httpPathManager;
        this.alertProperties = alertProperties;
        this.authorizationUtility = authorizationUtility;
        this.csrfTokenRepository = csrfTokenRepository;
        this.samlContext = samlContext;
        this.samlAuthenticationProvider = samlAuthenticationProvider;
        this.samlEntryPoint = samlEntryPoint;
        this.metadataGeneratorFilter = metadataGeneratorFilter;
        this.samlLogoutFilter = samlLogoutFilter;
        this.samlLogoutProcessingFilter = samlLogoutProcessingFilter;
        this.samlWebSSOProcessingFilter = samlWebSSOProcessingFilter;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // this is needed to set the authentication manager to the correct instance that will allow access.
        // the bean in AuthenticationHandler is used only to avoid a circular dependency and it isn't the
        // authentication manager we want to use at runtime.
        logger.info("Updating authentication manager for SSO processing filter.");
        samlWebSSOProcessingFilter.setAuthenticationManager(super.authenticationManagerBean());
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(samlAuthenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        configureActiveMQProvider();
        configureWithSSL(http);
        configureH2Console(http);
        http.authorizeRequests()
            .requestMatchers(createAllowedPathMatchers()).permitAll()
            .and().authorizeRequests().anyRequest().authenticated()
            .and().exceptionHandling().authenticationEntryPoint(samlEntryPoint)
            .and().csrf().csrfTokenRepository(csrfTokenRepository).ignoringRequestMatchers(createCsrfIgnoreMatchers())
            .and().addFilterBefore(metadataGeneratorFilter, ChannelProcessingFilter.class)
            .addFilterAfter(samlFilter(), BasicAuthenticationFilter.class)
            .authorizeRequests().withObjectPostProcessor(createRoleProcessor())
            .and().logout().logoutSuccessUrl("/");
    }

    private void configureActiveMQProvider() {
        // Active MQ initializes the Bouncy Castle provider in a static constructor of the Broker Service
        // static initialization of the Bouncy Castle provider breaks SAML support over SSL
        // https://stackoverflow.com/questions/53906154/spring-boot-2-1-embedded-tomcat-keystore-password-was-incorrect
        try {
            ClassLoader loader = BrokerService.class.getClassLoader();
            Class<?> clazz = loader.loadClass("org.bouncycastle.jce.provider.BouncyCastleProvider");
            Provider bouncycastle = (Provider) clazz.getDeclaredConstructor().newInstance();
            Security.removeProvider(bouncycastle.getName());
            logger.info("Alert Application Configuration: Removing Bouncy Castle provider");
            Security.addProvider(bouncycastle);
            logger.info("Alert Application Configuration: Adding Bouncy Castle provider to the end of the provider list");

        } catch (Exception e) {
            // nothing needed here if that provider does not exist
            logger.info("Alert Application Configuration: Bouncy Castle provider not found");
        }
    }

    private void configureH2Console(HttpSecurity http) throws Exception {
        if (alertProperties.getH2ConsoleEnabled()) {
            ignorePaths(HttpPathManager.PATH_H2_CONSOLE);
            http.headers().frameOptions().disable();
        }
    }

    private void configureWithSSL(HttpSecurity http) throws Exception {
        if (alertProperties.getSslEnabled()) {
            http.requiresChannel().anyRequest().requiresSecure();
        }
    }

    private void ignorePaths(String... paths) {
        for (String path : paths) {
            httpPathManager.addAllowedPath(path);
            httpPathManager.addSamlAllowedPath(path);
        }
    }

    private RequestMatcher[] createCsrfIgnoreMatchers() {
        return createRequestMatcherArray();
    }

    private RequestMatcher[] createAllowedPathMatchers() {
        return createRequestMatcherArray();
    }

    private RequestMatcher[] createRequestMatcherArray() {
        return new RequestMatcher[] {
            new SamlAntMatcher(samlContext, httpPathManager.getSamlAllowedPaths(), httpPathManager.getAllowedPaths())
        };
    }

    private ObjectPostProcessor<AffirmativeBased> createRoleProcessor() {
        return new ObjectPostProcessor<>() {
            @Override
            public AffirmativeBased postProcess(AffirmativeBased affirmativeBased) {
                WebExpressionVoter webExpressionVoter = new WebExpressionVoter();
                DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
                expressionHandler.setRoleHierarchy(authorities -> {
                    String[] allAlertRoles = retrieveAllowedRoles();
                    return AuthorityUtils.createAuthorityList(allAlertRoles);
                });
                webExpressionVoter.setExpressionHandler(expressionHandler);
                affirmativeBased.getDecisionVoters().add(webExpressionVoter);
                return affirmativeBased;
            }
        };
    }

    private String[] retrieveAllowedRoles() {
        return authorizationUtility.getRoles()
                   .stream()
                   .map(UserRoleModel::getName)
                   .toArray(String[]::new);
    }

    //
    // SAML Beans
    //

    @Bean
    public FilterChainProxy samlFilter() {
        List<SecurityFilterChain> chains = new ArrayList<>();

        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"), samlEntryPoint));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"), samlWebSSOProcessingFilter));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"), samlLogoutFilter));
        chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"), samlLogoutProcessingFilter));
        return new AlertFilterChainProxy(chains, samlContext);
    }
}
