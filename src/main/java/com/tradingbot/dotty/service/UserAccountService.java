package com.tradingbot.dotty.service;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface UserAccountService {
    void verifyGoogleIdToken(String idTokenString) throws GeneralSecurityException, IOException;
}
