package com.mpumd.poc.person.sb.application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

/**
 * Verify the protocol agnostic role based authorization of the use cases.
 * The service is a full mock behind the method security proxy : the business
 * code never runs, only the @PreAuthorize gate is under test.
 */
@SpringJUnitConfig(classes = {
        AuthorisationSecurityConfig.class,
        PersonAppSvcSecurityAuthorisationITest.Config.class
})
class PersonAppSvcSecurityAuthorisationITest {

    @TestConfiguration
    static class Config {
        // a mock as bean, not @MockitoBean : the latter would bypass the
        // post-processors and thus the method security proxy under test
        @Bean
        PersonAppSvc personAppSvc() {
            return mock(PersonAppSvc.class);
        }
    }

    @Autowired
    PersonAppSvc personAppSvc;

    @Test
    @WithMockUser(roles = "ADMIN")
    void register_OK_ADMIN() {
        assertThatCode(() -> personAppSvc.register(null))
                .doesNotThrowAnyException();
    }

    @Test
    @WithMockUser(roles = "USER")
    void register_KO_USER() {
        assertThatThrownBy(() -> personAppSvc.register(null))
                .isInstanceOf(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = {})
    void register_KO_NO_ROLE() {
        assertThatThrownBy(() -> personAppSvc.register(null))
                .isInstanceOf(AuthorizationDeniedException.class);
    }

    @Test
    @WithMockUser(roles = "USER")
    void changeSex_OK_USER() {
        assertThatCode(() -> personAppSvc.changeSex(null, null))
                .doesNotThrowAnyException();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void changeSex_KO_ADMIN() {
        assertThatThrownBy(() -> personAppSvc.changeSex(null, null))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(roles = {})
    void changeSex_KO_NO_ROLE() {
        assertThatThrownBy(() -> personAppSvc.changeSex(null, null))
                .isInstanceOf(AccessDeniedException.class);
    }
}
