package com.pdfservice.pdf.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RestController
public class PdfController {
    @Autowired
    private PdfServices pdfServices;
    @PostMapping("pdf/text/view")
    public ResponseEntity<String> viewTextPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file found.");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            return ResponseEntity.badRequest().body("Format not match,Only PDF file are allowed.");
        }
        try {
            return new ResponseEntity<>(pdfServices.viewTextPdf(file), HttpStatus.OK);

        } catch (IOException e) {
            return ResponseEntity.badRequest().body("can't able to process file");
        }
    }
    @PostMapping("pdf/view")
    public ResponseEntity<?> viewPdf(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file found.");
        }
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
            return ResponseEntity.badRequest().body( "Format not match. Only PDF files are allowed.");
        }
        try {
            byte[] pdfData = file.getBytes();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.inline().filename(originalFilename).build());
            return new ResponseEntity<>(pdfData, headers, HttpStatus.OK);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body( "Unable to process file");
        }
    }
    @PostMapping("summarize/pdfs/line/{line}")
    public ResponseEntity<Map<String, String>> summarizePdfs(@RequestParam("files") MultipartFile[] files, @PathVariable int line ) {
        Map<String, String> summaries = new HashMap<>();

        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".pdf")) {
                summaries.put(originalFilename, "Invalid format. Only PDF files are allowed.");
                continue;
            }
            String summary = pdfServices.processPdf(file, line);
            summaries.put(originalFilename, summary);
        }

        return new ResponseEntity<>(summaries, HttpStatus.OK);
    }
}
