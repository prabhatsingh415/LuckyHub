package com.LuckyHub.Backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class VerificationToken {

     private static final int EXPIRATION_TIME = 10;

     @Id
     @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;
     private String token;
     private Date expirationTime;
     private Date lastTokenSendTime;
     int resendCount;

     @OneToOne(fetch = FetchType.EAGER)
     @JoinColumn(
             name = "userID",
             nullable = false
     )
     @JsonIgnore
     private User user;

    public VerificationToken(User user, String token, Date lastTokenSendTime) {
        this.user = user;
        this.token = token;
        this.expirationTime = calculateTime();
        this.lastTokenSendTime = lastTokenSendTime;
    }

    public VerificationToken(String token) {
        this.token = token;
        this.expirationTime = calculateTime();
    }

    private Date calculateTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, VerificationToken.EXPIRATION_TIME);
        return new Date(calendar.getTime().getTime());
    }

}
