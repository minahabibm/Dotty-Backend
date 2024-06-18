package com.tradingbot.dotty.serviceImpls;

import com.nimbusds.jose.util.JSONObjectUtils;
import com.tradingbot.dotty.models.dto.AccessTokenAudAndJti;
import com.tradingbot.dotty.models.dto.UserConfigurationDTO;
import com.tradingbot.dotty.models.dto.UsersDTO;
import com.tradingbot.dotty.service.AuthService;
import com.tradingbot.dotty.service.UserConfigurationService;
import com.tradingbot.dotty.service.UsersService;
import com.tradingbot.dotty.utils.ExternalApiRequests;
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
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

import static com.tradingbot.dotty.utils.LoggingConstants.*;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private ExternalApiRequests externalApiRequests;

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
    public String getRedirectUrl(HttpServletRequest httpRequest) {
        log.trace(USER_AUTHENTICATION_GETTING_REDIRECT_URL_TYPE);
        String redirectUrl = null;
        try {
            String userAgent = httpRequest.getHeader("User-Agent");
            boolean isMobile = userAgent != null && userAgent.toLowerCase().contains("mobile");
            if (isMobile)
                redirectUrl = "mobileRedirectUrl";
            else
                redirectUrl = "webRedirectUrl";
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.trace(USER_AUTHENTICATION_REDIRECT_URL_TYPE, redirectUrl);
        return redirectUrl.equals("webRedirectUrl") ? "http://localhost:8081" :  "com.anonymous.dotty-app://localhost:8081";
    }

    @Override
    public URI getResponseRedirectUri(HttpServletRequest httpRequest, Authentication authentication) throws URISyntaxException {
        String redirectUri = getRedirectUrl(httpRequest);                                                   // System.out.println(code + " " + state);

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
            addOrUpdateAuthenticatedUser(authentication);

            return new URI(redirectUri+"?access_token="+accessToken.getTokenValue()+"&refresh_token="+refreshToken.getTokenValue()+"&id_token="+idToken.getTokenValue());
        } else {
            return null;
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
        String mgmAccessToken = externalApiRequests.getMGMApiAccessToken().getAccess_token();
        if(isTokenExpired(mgmAccessToken)) {
            log.debug(CACHE_MGM_ACCESS_TOKEN_EXP);
            cacheManager.getCache("tokens").evictIfPresent("mgmAccessToken");
            return externalApiRequests.getMGMApiAccessToken().getAccess_token();
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
        externalApiRequests.revokeAccessToken(mgmAccesstoken, tokenPayload.get("jti").toString());
        cacheManager.getCache("tokens").evictIfPresent("revokedTokens");
    }

    @Override
    public boolean validateToken(String token) {
        log.trace(USER_AUTHENTICATION_TOKEN_VALIDATION, token);
        return !isTokenBlacklisted(token);
    }

    private boolean isTokenBlacklisted(String token) {
        log.trace(USER_AUTHENTICATION_TOKEN_GETTING_REVOKED_LIST);
        String mgmAccesstoken = getMGMAccessToken();
        AccessTokenAudAndJti[] revokedAccessTokens = externalApiRequests.getRevokedAccessTokens(mgmAccesstoken);

        String accessToken = getJwtPayloadDecoder(token).get("jti").toString();
        Map<String, AccessTokenAudAndJti> tokens = Arrays.stream(revokedAccessTokens).collect(Collectors.toMap(AccessTokenAudAndJti::getJti, tokenAudAndJti -> tokenAudAndJti));
        log.trace(USER_AUTHENTICATION_TOKEN_REVOKED_LIST, tokens);
        return tokens.containsKey(accessToken);
    }

    @Override
    public void addOrUpdateAuthenticatedUser(Authentication authentication) {
        log.trace(USER_AUTHENTICATION_DATA);
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();                                         // attributes.forEach((key, value) -> System.out.println(key + ": " + value));
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        Optional<UsersDTO> user = usersService.getUserByEmail(email);

        if(user.isEmpty()) {
            Long userConfigurationID = userConfigurationService.insertUserConfiguration(new UserConfigurationDTO());
            UsersDTO usersDTO = new UsersDTO();
            usersDTO.setFirstName(attributes.get("given_name").toString());
            usersDTO.setLastName(attributes.get("family_name").toString());
            usersDTO.setEmailAddress(attributes.get("email").toString());
            usersDTO.setLoginType(attributes.get("sub").toString());
            usersDTO.setNickname(attributes.get("nickname").toString());
            usersDTO.setPictureUrl(attributes.get("picture").toString());
            usersDTO.setUserConfigurationDTO(userConfigurationService.getUserConfiguration(userConfigurationID).get());

            log.debug(USER_AUTHENTICATION_CREATE, usersDTO);
            usersService.insertUser(usersDTO);
        } else {
            UsersDTO usersDTO = user.get();
            usersDTO.setFirstName(attributes.get("given_name").toString());
            usersDTO.setLastName(attributes.get("family_name").toString());
            usersDTO.setLoginType(attributes.get("sub").toString());
            usersDTO.setNickname(attributes.get("nickname").toString());
            usersDTO.setPictureUrl(attributes.get("picture").toString());

            log.debug(USER_AUTHENTICATION_UPDATE, usersDTO);
            usersService.updateUser(usersDTO);
        }
    }


    @Override
    public void getAuthenticUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication instanceof OAuth2AuthenticationToken) {
                // If the authentication is OAuth2, retrieve user details
                OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                DefaultOAuth2User user = (DefaultOAuth2User) oauthToken.getPrincipal();

                // Extract user attributes
                String name = user.getAttribute("name");
                String email = user.getAttribute("email");
                System.out.println(authentication.getDetails().toString());
                System.out.println(user.getAttributes());

                System.out.println("Authenticated user: " + name + ", Email: " + email);
            } else {
                // Handle other types of authentication if needed
                System.out.println("Authenticated user (non-OAuth2): " + authentication.getName());
            }
        } else {
            System.out.println ("No authenticated user");
        }
    }

    @Override
    public void getAuthorizationType() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            System.out.println("JwtAuthenticationToken");
            // Handle JWT-based authentication
            String token = jwtAuth.getToken().getTokenValue();
            // Process the JWT token
        } else if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauth2Auth = (OAuth2AuthenticationToken) authentication;
            System.out.println("OAuth2AuthenticationToken");
            // Handle OAuth2 client-based authentication
            String principalName = oauth2Auth.getPrincipal().getName();
            // Process the OAuth2 authentication
        } else {
            throw new IllegalArgumentException("Unexpected authentication type: " + authentication.getClass());
        }
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
