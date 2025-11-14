package com.LuckyHub.Backend.utils;

import java.util.Calendar;
import java.util.Date;

public class VerificationTokenUtil {
    // Utility method to calculate expiration
    public static Date calculateExpirationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 10); // expiration 10 minutes
        return calendar.getTime();
    }
}
