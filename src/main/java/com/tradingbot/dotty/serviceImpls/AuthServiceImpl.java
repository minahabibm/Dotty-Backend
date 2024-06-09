package com.tradingbot.dotty.serviceImpls;

import com.nimbusds.jose.util.JSONObjectUtils;
import com.tradingbot.dotty.models.dto.AccessTokenAudAndJti;
import com.tradingbot.dotty.models.dto.UsersDTO;
import com.tradingbot.dotty.service.AuthService;
import com.tradingbot.dotty.service.UsersService;
import com.tradingbot.dotty.utils.ExternalApiRequests;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private ExternalApiRequests externalApiRequests;

    @Autowired
    UsersService usersService;

    @Override
    public String getRedirectUrl(HttpServletRequest httpRequest) {
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
        return redirectUrl.equals("webRedirectUrl") ? "http://localhost:8081" :  "com.anonymous.dotty-app://localhost:8081";
    }

    @Override
    public Map<String, Object> getJwtPayloadDecoder(String token) {
        try {
            Base64.Decoder decoder = Base64.getUrlDecoder();

            String[] chunks = token.split("\\.");
            String header = new String(decoder.decode(chunks[0]));
            String payload = new String(decoder.decode(chunks[1]));

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
    public void revokeToken(String token) {
        Map<String, Object> tokenPayload = getJwtPayloadDecoder(token);
        String mgmAccesstoken = externalApiRequests.getMGMApiAccessToken().getAccess_token();
        externalApiRequests.revokeAccessToken(mgmAccesstoken, tokenPayload.get("jti").toString());
    }

    @Override
    public boolean validateToken(String token) {
        return (isTokenBlacklisted(token)) ? false : true;
    }

    private boolean isTokenBlacklisted(String token) {
        String mgmAccesstoken = externalApiRequests.getMGMApiAccessToken().getAccess_token();
        AccessTokenAudAndJti[] revokedAccessTokens = externalApiRequests.getRevokedAccessTokens(mgmAccesstoken);

        String accessToken = getJwtPayloadDecoder(token).get("jti").toString();
        Map<String, AccessTokenAudAndJti> tokens = Arrays.stream(revokedAccessTokens).collect(Collectors.toMap(AccessTokenAudAndJti::getJti, tokenAudAndJti -> tokenAudAndJti));
        return tokens.containsKey(accessToken);
    }

    @Override
    public void addOrUpdateAuthenticatedUser(Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();                                         // attributes.forEach((key, value) -> System.out.println(key + ": " + value));
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        Optional<UsersDTO> user = usersService.getUserByEmail(email);
        if(!user.isPresent()) {
            UsersDTO usersDTO = new UsersDTO();
            usersDTO.setFirstName(attributes.get("given_name").toString());
            usersDTO.setLastName(attributes.get("family_name").toString());
            usersDTO.setEmailAddress(attributes.get("email").toString());
            usersDTO.setLoginType(attributes.get("sub").toString());
            usersDTO.setNickname(attributes.get("nickname").toString());
            usersDTO.setPictureUrl(attributes.get("picture").toString());
            usersService.insertUser(usersDTO);
        } else {
            UsersDTO usersDTO = user.get();
            usersDTO.setFirstName(attributes.get("given_name").toString());
            usersDTO.setLastName(attributes.get("family_name").toString());
            usersDTO.setLoginType(attributes.get("sub").toString());
            usersDTO.setNickname(attributes.get("nickname").toString());
            usersDTO.setPictureUrl(attributes.get("picture").toString());
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
    public void extractRequestDetails(HttpServletRequest servletRequest) {
        HttpServletRequest request = servletRequest;

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
