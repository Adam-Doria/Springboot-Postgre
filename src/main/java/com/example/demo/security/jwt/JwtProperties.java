package com.example.demo.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propriétés de configuration pour JWT
 */
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    /**
     * Clé secrète utilisée pour signer les tokens JWT
     */
    private String secretKey = "defaultSecretKeyForDevEnvironmentOnlyDoNotUseInProduction";
    
    /**
     * Durée de validité du token JWT en millisecondes (24h par défaut)
     */
    private long expirationMs = 86400000; // 24 heures
    
    /**
     * Préfixe du token dans le header Authorization
     */
    private String tokenPrefix = "Bearer ";
    
    /**
     * Nom du header contenant le token
     */
    private String headerString = "Authorization";

    // Getters et Setters
    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public void setExpirationMs(long expirationMs) {
        this.expirationMs = expirationMs;
    }

    public String getTokenPrefix() {
        return tokenPrefix;
    }

    public void setTokenPrefix(String tokenPrefix) {
        this.tokenPrefix = tokenPrefix;
    }

    public String getHeaderString() {
        return headerString;
    }

    public void setHeaderString(String headerString) {
        this.headerString = headerString;
    }
} 