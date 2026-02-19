package com.LuckyHub.Backend.service;

import com.LuckyHub.Backend.model.MailType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Value("${BREVO_API_KEY}")
    private String brevoApiKey;

    @Value("${EMAIL_USERNAME}")
    private String senderEmail;

    private final OkHttpClient client = new OkHttpClient();
    private static final String API_URL = "https://api.brevo.com/v3/smtp/email";

    @Override
    public void sendEmail(String to, String subject, String body, MailType type) {
        executeRequest(to, subject, body, type);
    }

    @Override
    @Async
    public void sendAsyncEmail(String to, String subject, String body, MailType type) {
        executeRequest(to, subject, body, type);
    }

    private void executeRequest(String to, String subject, String content, MailType type) {
        log.info("[BREVO-REST] Sending {} email to: {}", type, to);

        String htmlBody = generateHtmlTemplate(subject, content, type);

        JsonObject json = getJsonObject(to, subject, htmlBody);

        RequestBody requestBody = RequestBody.create(
                json.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("accept", "application/json")
                .addHeader("api-key", brevoApiKey)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                log.info("[BREVO-SUCCESS] Email sent! Status: {}", response.code());
            } else {
                log.error("[BREVO-ERROR] Code: {}, Msg: {}", response.code(),
                        response.body() != null ? response.body().string() : "No response body");
            }
        } catch (IOException e) {
            log.error("[BREVO-CRITICAL] Network Error: {}", e.getMessage());
        }
    }

    private String generateHtmlTemplate(String title, String message, MailType type) {
        String actionHtml = "";
        String themeColor = "#f97316";

        actionHtml = switch (type) {
            case VERIFICATION, RESEND_VERIFICATION, FORGOT_PASSWORD -> {
                String btnText = (type == MailType.FORGOT_PASSWORD) ? "Reset Password" : "Verify Email";
                yield "<div style='text-align: center; margin: 30px 0;'>" +
                        "<a href='" + message + "' style='background: " + themeColor + "; color: white; padding: 14px 28px; text-decoration: none; border-radius: 8px; font-weight: bold; display: inline-block; font-size: 16px; box-shadow: 0 4px 6px rgba(249, 115, 22, 0.2);'>" + btnText + "</a>" +
                        "</div><p style='font-size: 12px; color: #94a3b8; text-align: center;'>If the button doesn't work, copy-paste this: <br>" + message + "</p>";
            }
            case ACCOUNT_DELETE -> {
                themeColor = "#ef4444";
                yield "<div style='text-align: center; margin: 25px 0; background: #fef2f2; border: 2px dashed #ef4444; padding: 25px; border-radius: 12px;'>" +
                        "<p style='margin:0 0 10px 0; color:#ef4444; font-size: 12px; font-weight:bold; text-transform:uppercase;'>Security Code</p>" +
                        "<span style='font-size: 36px; font-weight: bold; letter-spacing: 8px; color: #ef4444;'>" + message + "</span>" +
                        "</div><p style='text-align:center; color:#64748b; font-size: 13px;'>Warning: Entering this code will permanently delete your account data.</p>";
            }
            case PAYMENT_SUCCESS -> {
                themeColor = "#22c55e";
                yield "<div style='background: #f0fdf4; padding: 25px; border-radius: 12px; border-left: 5px solid #22c55e; margin: 20px 0;'>" +
                        "<p style='font-size: 15px; color: #166534; margin: 0; line-height: 1.6; white-space: pre-line;'>" +
                        message +
                        "</p>" +
                        "</div>";
            }
            case PAYMENT_REFUND -> {
                themeColor = "#3b82f6";
                yield "<div style='background: #eff6ff; padding: 25px; border-radius: 12px; border-left: 5px solid #3b82f6; margin: 20px 0;'>" +
                        "<p style='font-size: 15px; color: #1e40af; margin: 0; line-height: 1.6; white-space: pre-line;'>" +
                        "ℹ️ <b>Refund Notice:</b><br><br>" + message +
                        "</p>" +
                        "</div>";
            }
        };

        return "<html><body style='font-family: \"Segoe UI\", Roboto, Arial, sans-serif; background-color: #f8fafc; margin: 0; padding: 40px 20px;'>" +
                "<div style='max-width: 550px; margin: auto; background: white; border-radius: 20px; overflow: hidden; box-shadow: 0 10px 25px rgba(0,0,0,0.05); border: 1px solid #e2e8f0;'>" +
                "  <div style='background: " + themeColor + "; padding: 35px; text-align: center;'>" +
                "    <h1 style='color: white; margin: 0; font-size: 32px; letter-spacing: 1px; font-weight: 800;'>LuckyHub</h1>" +
                "  </div>" +
                "  <div style='padding: 45px; text-align: left;'>" +
                "    <h2 style='color: #1e293b; margin-top: 0; font-size: 22px; font-weight: 700;'>" + title + "</h2>" +
                "    <div style='margin: 20px 0; color: #475569; font-size: 16px; line-height: 1.6;'>" +
                (type == MailType.ACCOUNT_DELETE ? "You have requested to delete your account. Use the following code to confirm:" : "Hi there, here are the details regarding your request:") +
                "    </div>" +
                "    " + actionHtml +
                "  </div>" +
                "  <div style='background: #f1f5f9; padding: 25px; text-align: center; font-size: 12px; color: #64748b; border-top: 1px solid #e2e8f0;'>" +
                "    This is an automated message from the LuckyHub platform.<br>© 2026 LuckyHub. All rights reserved." +
                "  </div>" +
                "</div></body></html>";
    }

    @NotNull
    private JsonObject getJsonObject(String to, String subject, String htmlContent) {
        JsonObject json = new JsonObject();
        JsonObject sender = new JsonObject();
        sender.addProperty("name", "LuckyHub");
        sender.addProperty("email", senderEmail);
        json.add("sender", sender);

        JsonObject recipient = new JsonObject();
        recipient.addProperty("email", to);
        JsonArray toArray = new JsonArray();
        toArray.add(recipient);
        json.add("to", toArray);

        json.addProperty("subject", subject);
        json.addProperty("htmlContent", htmlContent);
        return json;
    }
}