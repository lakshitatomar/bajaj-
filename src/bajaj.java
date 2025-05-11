
package com.example.webhooksql;

import com.example.webhooksql.service.WebhookService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class WebhookSqlApplication {

    private final WebhookService webhookService;

    public WebhookSqlApplication(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    public static void main(String[] args) {
        SpringApplication.run(WebhookSqlApplication.class, args);
    }

    @PostConstruct
    public void runOnStartup() {
        webhookService.handleWebhookProcess();
    }
}

// WebhookService.java
package com.example.webhooksql.service;

import com.example.webhooksql.model.WebhookResponse;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WebhookService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void handleWebhookProcess() {
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = Map.of(
                "name", "Lakshita Tomar",
                "regNo", "REG12347",
                "email", "lakshitatomar0405@gmail.com"
        );

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                generateUrl, HttpMethod.POST, entity, WebhookResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            String webhookUrl = response.getBody().getWebhook();
            String accessToken = response.getBody().getAccessToken();

            // Replace this with your actual SQL solution
            String finalSqlQuery = "SELECT department, COUNT(*) AS total_employees FROM employee GROUP BY department HAVING COUNT(*) > 5;";

            sendSolution(webhookUrl, accessToken, finalSqlQuery);
        } else {
            System.err.println("❌ Failed to generate webhook.");
        }
    }

    private void sendSolution(String webhookUrl, String token, String sqlQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        Map<String, String> body = Map.of("finalQuery", sqlQuery);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            System.out.println("✅ SQL query submitted successfully.");
        } else {
            System.err.println("❌ Submission failed: " + response.getStatusCode());
        }
    }
}

// WebhookResponse.java
package com.example.webhooksql.model;

public class WebhookResponse {
    private String webhook;
    private String accessToken;

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}

// application.properties
# Empty or include custom configuration if needed

// pom.xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>webhook-sql</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>jar</packaging>

    <name>webhook-sql</name>
    <description>Spring Boot Project for SQL Webhook Submission</description>

    <properties>
        <java.version>17</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>