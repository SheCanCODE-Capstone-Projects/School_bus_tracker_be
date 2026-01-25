package org.example.school_bus_tracker_be.serviceimpl;

import org.example.school_bus_tracker_be.Service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private final RestTemplate restTemplate;
    private final String brevoApiKey;
    private final String fromEmail;
    private final String fromName;
    private final String frontendUrl;
    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    public EmailServiceImpl(
            @Value("${brevo.api.key}") String brevoApiKey,
            @Value("${app.email.from}") String fromEmail,
            @Value("${app.email.from.name:School Bus Tracker}") String fromName,
            @Value("${app.frontend.url:http://localhost:3000}") String frontendUrl) {
        this.restTemplate = new RestTemplate();
        this.brevoApiKey = brevoApiKey;
        this.fromEmail = fromEmail;
        this.fromName = fromName;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void sendPasswordResetCode(String to, String code) {
        String subject = "Password Reset Verification Code - School Bus Tracker";
        String textContent = buildPasswordResetEmailContent(code);
        String htmlContent = buildPasswordResetEmailHtmlContent(code);
        
        sendEmail(to, subject, textContent, htmlContent);
    }

    @Override
    public void sendNotificationEmail(String to, String subject, String messageBody) {
        sendEmail(to, subject, messageBody, null);
    }

    private void sendEmail(String to, String subject, String textContent, String htmlContent) {
        try {
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", brevoApiKey);

            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            
            // Sender
            Map<String, String> sender = new HashMap<>();
            sender.put("name", fromName);
            sender.put("email", fromEmail);
            requestBody.put("sender", sender);

            // Recipient
            Map<String, String> recipient = new HashMap<>();
            recipient.put("email", to);
            requestBody.put("to", List.of(recipient));

            // Subject and content
            requestBody.put("subject", subject);
            requestBody.put("textContent", textContent);
            
            if (htmlContent != null && !htmlContent.isEmpty()) {
                requestBody.put("htmlContent", htmlContent);
            }

            // Create HTTP entity
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Send request
            ResponseEntity<Map> response = restTemplate.exchange(
                    BREVO_API_URL,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            // Check response
            if (response.getStatusCode().is2xxSuccessful()) {
                // Success - Brevo API returns 201 Created on success
                return;
            } else {
                String errorDetails = response.getBody() != null ? response.getBody().toString() : "No error details";
                throw new RuntimeException("Brevo API returned status: " + response.getStatusCode() + ". Details: " + errorDetails);
            }

        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Handle 4xx errors (bad request, unauthorized, etc.)
            String errorBody = e.getResponseBodyAsString();
            throw new RuntimeException("Brevo API error: " + e.getStatusCode() + " - " + errorBody, e);
        } catch (org.springframework.web.client.RestClientException e) {
            // Handle network errors, timeouts, etc.
            throw new RuntimeException("Failed to send email via Brevo API: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
    }

    private String buildPasswordResetEmailContent(String code) {
        return String.format(
            "Hello,\n\n" +
            "You have requested to reset your password for School Bus Tracker.\n\n" +
            "Your verification code is: %s\n\n" +
            "Please enter this code in the password reset form to proceed.\n\n" +
            "This code will expire in 10 minutes.\n\n" +
            "If you did not request this password reset, please ignore this email.\n\n" +
            "Best regards,\n" +
            "School Bus Tracker Team",
            code
        );
    }

    private String buildPasswordResetEmailHtmlContent(String code) {
        return String.format(
            "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "<style>" +
            "  body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
            "  .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
            "  .code-box { background-color: #f4f4f4; border: 2px solid #007bff; border-radius: 5px; padding: 15px; text-align: center; margin: 20px 0; }" +
            "  .code { font-size: 24px; font-weight: bold; color: #007bff; letter-spacing: 3px; }" +
            "  .footer { margin-top: 30px; font-size: 12px; color: #666; }" +
            "</style>" +
            "</head>" +
            "<body>" +
            "<div class=\"container\">" +
            "  <h2>Password Reset Verification Code</h2>" +
            "  <p>Hello,</p>" +
            "  <p>You have requested to reset your password for School Bus Tracker.</p>" +
            "  <div class=\"code-box\">" +
            "    <p style=\"margin: 0; color: #666;\">Your verification code is:</p>" +
            "    <p class=\"code\">%s</p>" +
            "  </div>" +
            "  <p>Please enter this code in the password reset form to proceed.</p>" +
            "  <p><strong>This code will expire in 10 minutes.</strong></p>" +
            "  <p>If you did not request this password reset, please ignore this email.</p>" +
            "  <div class=\"footer\">" +
            "    <p>Best regards,<br>School Bus Tracker Team</p>" +
            "  </div>" +
            "</div>" +
            "</body>" +
            "</html>",
            code
        );
    }
}
