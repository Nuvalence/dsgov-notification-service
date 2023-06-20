package io.nuvalence.platform.notification.service.config;

import io.nuvalence.auth.token.SelfSignedTokenAuthenticationProvider;
import io.nuvalence.auth.token.TokenFilter;
import io.nuvalence.auth.util.RsaKeyUtility;
import io.nuvalence.logging.filter.LoggingContextFilter;
import io.nuvalence.platform.notification.service.utils.JacocoIgnoreInGeneratedReport;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Configures TokenFilter.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Profile("!test")
@JacocoIgnoreInGeneratedReport(
        reason =
                "Initialization has side effects making unit tests difficult. Tested in acceptance"
                        + " tests.")
public class WebSecurityConfig {
    @Value("${spring.cloud.gcp.project-id}")
    private String gcpProjectId;

    @Value("${auth.token-filter.self-signed.issuer}")
    private String selfSignIssuer;

    @Value("${auth.token-filter.self-signed.public-key}")
    private String selfSignPublicKey;

    /**
     * Allows unauthenticated access to API docs.
     *
     * @param http Spring HttpSecurity configuration.
     * @return Configured SecurityFilterChain
     * @throws Exception If any erroes occur during configuration
     */
    @Bean
    @Order(0)
    public SecurityFilterChain apidocs(HttpSecurity http) throws Exception {
        return http.requestMatchers(
                        (matchers) ->
                                matchers.antMatchers(
                                        "/",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**"))
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().permitAll())
                .requestCache()
                .disable()
                .securityContext()
                .disable()
                .sessionManagement()
                .disable()
                .build();
    }

    /**
     * Sets up API token filter.
     *
     * @param http Spring HttpSecurity configuration.
     * @return Configured SecurityFilterChain
     * @throws Exception If any erroes occur during configuration
     */
    @Bean
    @Order(1)
    public SecurityFilterChain defaultSecurityFilterChain(final HttpSecurity http)
            throws Exception {
        return http.csrf()
                .disable()
                .authorizeHttpRequests(
                        (authorize) -> authorize.antMatchers("/actuator/health").permitAll())
                .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
                .sessionManagement(
                        (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(new LoggingContextFilter(), BasicAuthenticationFilter.class)
                .addFilterAfter(
                        new TokenFilter(
                                new SelfSignedTokenAuthenticationProvider(
                                        selfSignIssuer,
                                        RsaKeyUtility.getPublicKeyFromString(selfSignPublicKey))),
                        LoggingContextFilter.class)
                .build();
    }
}
