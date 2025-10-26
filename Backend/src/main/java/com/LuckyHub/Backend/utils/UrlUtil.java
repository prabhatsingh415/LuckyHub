package com.LuckyHub.Backend.utils;


import jakarta.servlet.http.HttpServletRequest;

public class UrlUtil {

    private UrlUtil() {
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
}
