package com.LuckyHub.Backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class PasswordToken {
        private static final int EXPIRATION_TIME = 10;

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String token;
        private Date expirationTime;

        @OneToOne(fetch = FetchType.EAGER)
        @JoinColumn(
                name = "userID",
                nullable = false
        )
        private User user;

        public PasswordToken(User user, String token) {
            this.user = user;
            this.token = token;
            this.expirationTime = calculateTime(EXPIRATION_TIME);
        }
        public PasswordToken(String token) {
            this.token = token;
            this.expirationTime = calculateTime(EXPIRATION_TIME);
        }

        private Date calculateTime(int expirationTime) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(new Date().getTime());
            calendar.add(Calendar.MINUTE, expirationTime);
            return new Date(calendar.getTime().getTime());
        }
}

