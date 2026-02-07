package com.LuckyHub.Backend.utils;


import jakarta.servlet.http.HttpServletRequest;

public class URLUtil {

    private URLUtil() {
        // Prevent instantiation
    }

    public static String buildBaseUrl(HttpServletRequest request) {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int port = request.getServerPort();
        String contextPath = request.getContextPath();

        StringBuilder baseUrl = new StringBuilder();
        baseUrl.append(scheme)
                .append("://")
                .append(serverName);

        if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
            baseUrl.append(":").append(port);
        }

        if (contextPath != null && !contextPath.isEmpty()) {
            baseUrl.append(contextPath);
        }

        return baseUrl.toString();
    }

    public static String generatePasswordResetURL(String url, String token) {
        return url
                + "/reset-password?token="
                + token;
    }
}
