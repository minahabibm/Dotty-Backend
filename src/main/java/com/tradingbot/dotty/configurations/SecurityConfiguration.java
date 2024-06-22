package com.tradingbot.dotty.configurations;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSAlgorithmFamilyJWSKeySelector;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.tradingbot.dotty.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.RequestFilter;
import org.apache.juli.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.*;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.ForwardAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.CookieClearingLogoutHandler;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static com.tradingbot.dotty.utils.LoggingConstants.*;

// TODO Update Access token when refreshed
// TODO user redirects to login page

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private AuthService authService;

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUrl;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(getAuthorizationManagerRequestMatcherRegistryCustomizer())
            .oauth2Login(getoAuth2LoginConfigurerCustomizer())                                                          // withDefaults()
            .oauth2ResourceServer(getoAuth2ResourceServerConfigurerCustomizer())
            .sessionManagement(getSessionManagementConfigurerCustomizer())
            .securityContext(getSecurityContextConfigurerCustomizer(securityContextRepository()))
            .exceptionHandling(customizeExceptionHandling())
            .logout(getLogoutCustomizer())
            .headers(getHeadersConfigurerCustomizer())                                                                  // Allow frames from the same origin
            .cors(getCorsConfigurerCustomizer())                                                                        // provide cors
            .csrf(AbstractHttpConfigurer::disable)                                                                      // provide a csrf token
            .addFilterBefore(getRequestFilter(), BearerTokenAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public HttpSessionSecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public JwtDecoder jwtDecoder() throws MalformedURLException, KeySourceException {

        JWSKeySelector<SecurityContext> jwsKeySelector = JWSAlgorithmFamilyJWSKeySelector.fromJWKSetURL(new URL(jwkSetUrl));
        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();
        jwtProcessor.setJWSKeySelector(jwsKeySelector);
        jwtProcessor.setJWSTypeVerifier(new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType("at+jwt")));

        return new NimbusJwtDecoder(jwtProcessor);
    }

    private RequestFilter getRequestFilter() {
        return new RequestFilter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                log.debug(USER_AUTHORIZATION);
                HttpServletRequest request = (HttpServletRequest) servletRequest;
                final String authorizationHeader = request.getHeader("Authorization");
                Boolean isAuthorizationHeader = (authorizationHeader != null && authorizationHeader.startsWith("Bearer "));
                log.debug(USER_AUTHORIZATION_TOKEN_STATUS, isAuthorizationHeader);
                if (isAuthorizationHeader) {
                    String token = authorizationHeader.split(" ")[1];
                    if (!authService.validateToken(token)) {
                        servletResponse.setContentType("application/json");
                        ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        servletResponse.getWriter().write("Invalid or expired token");
                        return;
                    }
                }
                filterChain.doFilter(servletRequest, servletResponse);
            }

            @Override
            protected Log getLogger() {
                return null;
            }
        };
    }

    private Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> getAuthorizationManagerRequestMatcherRegistryCustomizer() {
        return authorizeRequests -> authorizeRequests                                                                   //  RequestMatcher refreshToken = new AntPathRequestMatcher( "/api/dotty/oauth2/auth0/refresh");
                .anyRequest().authenticated();                                                                          // .requestMatchers(url).permitAll()
                                                                                                                        // .anyRequest().permitAll();
    }

    private Customizer<OAuth2LoginConfigurer<HttpSecurity>> getoAuth2LoginConfigurerCustomizer() {
        return oauth2 -> oauth2
                .successHandler(new ForwardAuthenticationSuccessHandler("/api/dotty/oauth2/auth0/login"))
                .addObjectPostProcessor(new ObjectPostProcessor<OAuth2LoginAuthenticationFilter>() {
                    @Override
                    public <O extends OAuth2LoginAuthenticationFilter> O postProcess(O filter) {
                        filter.setSecurityContextRepository(new HttpSessionSecurityContextRepository());
                        return filter;
                    }
                });
    }

    private Customizer<OAuth2ResourceServerConfigurer<HttpSecurity>> getoAuth2ResourceServerConfigurerCustomizer() {
        return (oauth2ResourceServer -> oauth2ResourceServer.jwt(Customizer.withDefaults()));                           // Customizer -> Customizer.jwtAuthenticationConverter(jwtAuthenticationConverter))
    }

    private Customizer<SessionManagementConfigurer<HttpSecurity>> getSessionManagementConfigurerCustomizer() {
        return httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private Customizer<SecurityContextConfigurer<HttpSecurity>> getSecurityContextConfigurerCustomizer(HttpSessionSecurityContextRepository securityContextRepository) {
        return securityContext -> securityContext.securityContextRepository(securityContextRepository);
    }

    private Customizer<ExceptionHandlingConfigurer<HttpSecurity>> customizeExceptionHandling() {
        return exceptionHandlingCustomizer -> exceptionHandlingCustomizer
                .authenticationEntryPoint((request, response, authException) -> {
                    // Custom handling for authentication exceptions
                    Cookie[] cookies = request.getCookies() ;
                    cookies = null;

                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
//                    response.sendRedirect("http://localhost:8080/oauth2/authorization/auth0");
                })
                .accessDeniedHandler((request, response, authException)-> {
                    System.out.println("response");
                });
    }

    private Customizer<HeadersConfigurer<HttpSecurity>> getHeadersConfigurerCustomizer() {
        return headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
    }

    private Customizer<LogoutConfigurer<HttpSecurity>> getLogoutCustomizer() {
        CookieClearingLogoutHandler cookies = new CookieClearingLogoutHandler("JSESSIONID");
        HeaderWriterLogoutHandler clearSiteData = new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ClearSiteDataHeaderWriter.Directive.ALL));
        LogoutSuccessHandler oidcLogoutSuccessHandler = (request, response, authentication) -> {
            log.debug(USER_AUTHENTICATION_LOGOUT);
            String redirectUrl = request.getRequestURI();
            String queryString = request.getQueryString();
            log.debug(USER_AUTHENTICATION_LOGOUT_TOKEN);
            if(queryString != null) {
                String[] queryParams = request.getQueryString().split("=");
                if(queryParams.length >= 2)
                    authService.revokeToken(queryParams[1]);
            }

            OidcClientInitiatedLogoutSuccessHandler oidcClientInitiatedLogoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(this.clientRegistrationRepository);
            oidcClientInitiatedLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUrl}/api/dotty/oauth2/auth0/logout");
            oidcClientInitiatedLogoutSuccessHandler.setDefaultTargetUrl(authService.getRedirectUrl(request));
            oidcClientInitiatedLogoutSuccessHandler.onLogoutSuccess(request, response, authentication);
        };

        return (logout) -> logout
                .logoutUrl("/api/dotty/oauth2/auth0/logout")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID")
                .addLogoutHandler(cookies)
                .addLogoutHandler(clearSiteData)
                .logoutSuccessHandler(oidcLogoutSuccessHandler);
    }

    private Customizer<CorsConfigurer<HttpSecurity>> getCorsConfigurerCustomizer() {
        return cors -> cors
                .configurationSource(request -> {                                                                       // config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));                            // Specify allowed methods
                    CorsConfiguration config = new CorsConfiguration();                                                 // config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));                           // Specify allowed headers
                    config.setAllowedOrigins(List.of("*"));                                                         // Specify allowed origins
                    return config;
                });
    }

}

/*
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientRepository authorizedClientRepository) {

        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .authorizationCode()
                        .refreshToken()
                        .build();

        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
                new DefaultOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientRepository);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
*/

/* googleOAuth2AuthenticationToken
  @Autowired
  private OAuth2AuthorizedClientService authorizedClientService;

  private void  googleOAuth2AuthenticationToken(HttpServletResponse response, Authentication authentication) {
    if(authentication instanceof OAuth2AuthenticationToken oauthToken) {
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(oauthToken.getAuthorizedClientRegistrationId(), oauthToken.getName());

        System.out.println(authorizedClient.getAccessToken().getTokenValue());

        // Set the Authorization header with the Bearer token
        response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authorizedClient.getAccessToken().getTokenValue());
    }
  }
*/

