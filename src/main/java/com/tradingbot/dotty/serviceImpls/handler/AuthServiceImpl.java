package com.tradingbot.dotty.serviceImpls.handler;

import com.nimbusds.jose.util.JSONObjectUtils;
import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.UsersDTO;
import com.tradingbot.dotty.service.handler.AuthService;
import com.tradingbot.dotty.service.UserConfigurationService;
import com.tradingbot.dotty.service.UsersService;
import com.tradingbot.dotty.utils.ExternalAPi.Auth0Util;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.*;

import static com.tradingbot.dotty.utils.constants.LoggingConstants.*;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private Auth0Util auth0Util;

    @Autowired
    private UsersService usersService;

    @Autowired
    private UserConfigurationService userConfigurationService;

    @Autowired
    private CacheManager cacheManager;

    private final OAuth2AuthorizedClientService authorizedClientService;


    public AuthServiceImpl(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public <T> T getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication instanceof JwtAuthenticationToken jwtAuth) {
                // Handle JWT-based authentication
                log.trace(USER_AUTHENTICATION_TYPE, "JwtAuthenticationToken");                                                           //  JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) authentication;
                return (T) jwtAuth;
            } else if (authentication instanceof OAuth2AuthenticationToken oauth2Auth) {
                // Handle OAuth2 client-based authentication                                                            // DefaultOAuth2User user = (DefaultOAuth2User) oauth2Auth.getPrincipal(); /  DefaultOidcUser oidcUser
                log.trace(USER_AUTHENTICATION_TYPE, "OAuth2AuthenticationToken");                                                        // Extract user attributes user.getAttribute("name"),  user.getAttribute("email");
                return (T) oauth2Auth;
            }  else {
                throw new IllegalArgumentException("Unexpected authentication type: " + authentication.getClass());
            }
        } else {
            throw new IllegalStateException("No authenticated user or authentication is not available.");
        }
    }

    @Override
    public String getRedirectUrl(HttpServletRequest httpRequest) {
        log.trace(USER_AUTHENTICATION_GETTING_REDIRECT_URL_TYPE);
        try {
            String userAgent = httpRequest.getHeader("User-Agent");
            boolean isMobile = userAgent != null && userAgent.toLowerCase().contains("mobile");

            log.trace(USER_AUTHENTICATION_REDIRECT_URL_TYPE, isMobile ? "mobileRedirectUrl" : "webRedirectUrl");
            return isMobile ? "com.anonymous.dotty-app://localhost:8081" : "http://localhost:8081";
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    @Override
    public URI getResponseRedirectUri(HttpServletRequest httpRequest) throws URISyntaxException {
        Authentication authentication = getAuthenticatedUser();
        String redirectUri = getRedirectUrl(httpRequest);                                                               // System.out.println(code + " " + state);

        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) authentication;
        String clientRegistrationId = authenticationToken.getAuthorizedClientRegistrationId();
        String principalName = authenticationToken.getName();
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(clientRegistrationId, principalName);
        if (authorizedClient != null) {
            log.info(USER_AUTHENTICATION_LOGIN_ACCESS_REFRESH_TOKENS);
            OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
            OAuth2RefreshToken refreshToken = authorizedClient.getRefreshToken();
            log.info(USER_AUTHENTICATION_LOGIN_ID_TOKENS);
            DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
            OidcIdToken idToken = oidcUser.getIdToken();
            log.info(USER_AUTHENTICATION_LOGIN_USER_TO_API);
            addOrUpdateAuthenticatedUser();

            return new URI(redirectUri+"?access_token="+accessToken.getTokenValue()+"&refresh_token="+refreshToken.getTokenValue()+"&id_token="+idToken.getTokenValue());
        } else {
            return null;
        }
    }

    @Override
    public void addOrUpdateAuthenticatedUser() {
        log.trace(USER_AUTHENTICATION_DATA);
        Authentication authentication = getAuthenticatedUser();
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();                                             // attributes.forEach((key, value) -> System.out.println(key + ": " + value));
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        Optional<UsersDTO> user = usersService.getUserByEmail(email);

        if(user.isPresent()) {
            UsersDTO usersDTO = user.get();
            usersDTO.setFirstName(attributes.get("given_name").toString());
            usersDTO.setLastName(attributes.get("family_name").toString());
            usersDTO.setLoginUid(attributes.get("sub").toString());
            usersDTO.setNickname(attributes.get("nickname").toString());
            usersDTO.setPictureUrl(attributes.get("picture").toString());

            log.debug(USER_AUTHENTICATION_UPDATE, usersDTO);
            usersService.updateUser(usersDTO);
        } else {
            Optional<UserConfigurationDTO> userConfigurationDTO = userConfigurationService.insertUserConfiguration(new UserConfigurationDTO());
            if(userConfigurationDTO.isPresent()) {
                UsersDTO usersDTO = new UsersDTO();
                usersDTO.setFirstName(attributes.get("given_name").toString());
                usersDTO.setLastName(attributes.get("family_name").toString());
                usersDTO.setEmailAddress(attributes.get("email").toString());
                usersDTO.setLoginUid(attributes.get("sub").toString());
                usersDTO.setNickname(attributes.get("nickname").toString());
                usersDTO.setPictureUrl(attributes.get("picture").toString());
                usersDTO.setUserConfigurationDTO(userConfigurationDTO.get());
                log.debug(USER_AUTHENTICATION_CREATE, usersDTO);
                usersService.insertUser(usersDTO);
            }
        }
    }


    @Override
    public Map<String, Object> getJwtPayloadDecoder(String token) {
        log.trace(USER_AUTHENTICATION_TOKEN_PAYLOAD);
        try {
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String[] chunks = token.split("\\.");
            String header = new String(decoder.decode(chunks[0]));
            String payload = new String(decoder.decode(chunks[1]));
            log.trace(USER_AUTHENTICATION_TOKEN_DETAILS, header, payload);
            return JSONObjectUtils.parse(payload);
        } catch (JwtException e) {
            // Handle any exceptions that occur during decoding or verification
            System.err.println("Invalid JWT: " + e.getMessage());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public String getMGMAccessToken() {
        log.debug(CACHE_MGM_ACCESS_TOKEN);
        String mgmAccessToken = auth0Util.getMGMApiAccessToken().getAccess_token();
        if(isTokenExpired(mgmAccessToken)) {
            log.debug(CACHE_MGM_ACCESS_TOKEN_EXP);
            cacheManager.getCache("tokens").evictIfPresent("mgmAccessToken");
            return auth0Util.getMGMApiAccessToken().getAccess_token();
        } else {
            return mgmAccessToken;
        }
    }

    private Boolean isTokenExpired(String token) {
        Date tokenExpiration = new Date(Long.parseLong(getJwtPayloadDecoder(token).get("exp").toString()) * 1000);
        return tokenExpiration.before(new Date());
    }

    @Override
    public void revokeToken(String token) {
        log.trace(USER_AUTHENTICATION_TOKEN_REVOKE, token);
        Map<String, Object> tokenPayload = getJwtPayloadDecoder(token);
        String mgmAccesstoken = getMGMAccessToken();
        //  auth0Util.revokeAccessToken(mgmAccesstoken, tokenPayload.get("jti").toString());
        //  cacheManager.getCache("tokens").evictIfPresent("revokedTokens");
    }

    @Override
    public boolean validateToken(String token) {
        log.trace(USER_AUTHENTICATION_TOKEN_VALIDATION, token);
        return !isTokenBlacklisted(token);
    }

    private boolean isTokenBlacklisted(String token) {
        log.trace(USER_AUTHENTICATION_TOKEN_GETTING_REVOKED_LIST);
        String mgmAccesstoken = getMGMAccessToken();
        String accessToken = getJwtPayloadDecoder(token).get("jti").toString();

        //  AccessTokenAudAndJti[] revokedAccessTokens = auth0Util.getRevokedAccessTokens(mgmAccesstoken);
        //  Map<String, AccessTokenAudAndJti> tokens = Arrays.stream(revokedAccessTokens).collect(Collectors.toMap(AccessTokenAudAndJti::getJti, tokenAudAndJti -> tokenAudAndJti));
        //  log.trace(USER_AUTHENTICATION_TOKEN_REVOKED_LIST, tokens);
        //  tokens.containsKey(accessToken);
        return false;
    }


    @Override
    public void extractRequestDetails(HttpServletRequest request) {

        System.out.println("1 - preHandle() : Before sending request to the Controller");
        System.out.println("Method Type: " + request.getMethod());
        System.out.println("Request URL: " + request.getRequestURL().toString());

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.out.println("Header: " + headerName + " = " + headerValue);
        }

        // Access parameters
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String paramName = parameterNames.nextElement();
            String paramValue = request.getParameter(paramName);
            System.out.println("Parameter: " + paramName + " = " + paramValue);
        }
    }

}
