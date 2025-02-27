package com.pdfservice.pdf.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class PdfServices {

    public String viewTextPdf(MultipartFile file) throws IOException {
        PDDocument document = PDDocument.load(file.getInputStream());
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return text;
    }

  private final String apiUrl = "https://api.groq.com/openai/v1/chat/completions"; // Set your API URL
    private final String apiKey = "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"; // Set your API Key

    public String processPdf(MultipartFile file,int line) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String text = pdfStripper.getText(document).trim();

            if (text.isEmpty()) {
                return "PDF contains only images or no readable text.";
            }

            // Limit text to avoid API length restrictions
            String trimmedText = text.length() > 500 ? text.substring(0, 500) + "..." : text;
            System.out.println(trimmedText);
            return callGroqApi(trimmedText,line);
        } catch (IOException e) {
            return "Error processing PDF: " + e.getMessage();
        }
    }

    private String callGroqApi(String text,int lines) {
        try {
            //ref
            // https://console.groq.com/docs/api-reference#chat-create
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            // Construct request payload
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(Map.of(
                    "model", "llama-3.3-70b-versatile",
                    "messages", List.of(Map.of(
                            "role", "user",
                            "content", "Get a summary in"+ lines +" lines of the following text: " + text
                    ))
            ));

            HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, request, Map.class);

            // Extract response
            Map responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> firstChoice = choices.get(0);
                    Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                    return message.get("content").toString();
                }
            }
            return "Failed to generate summary.";
        } catch (Exception e) {
            return "Error calling Groq API: " + e.getMessage();
        }
    }
}
