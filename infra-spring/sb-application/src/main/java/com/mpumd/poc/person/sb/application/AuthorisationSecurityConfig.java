package com.mpumd.poc.person.sb.application;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

/**
 * Enable @PreAuthorize on the application services, whatever the in-adapter
 * (REST, SOAP, gRPC, ...) which triggers the use case.
 */
@Configuration
@EnableMethodSecurity
class AuthorisationSecurityConfig {
}
