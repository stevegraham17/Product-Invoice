package com.products.products.contoller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.docusign.esign.model.Notification;
import com.google.api.services.drive.Drive;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.*;
import com.products.products.config.GoogleDriveOAuthService;
import com.products.products.models.Invoice;
import com.products.products.models.SignedNotification;
import com.products.products.models.User;
import com.products.products.repositories.InvoiceRepository;
import com.products.products.repositories.NotificationRepository;
import com.products.products.repositories.UserRepository;
import com.products.products.services.GoogleDriveService;
import com.products.products.services.InvoiceService;
import com.products.products.services.PdfService;

import jakarta.ws.rs.core.HttpHeaders;

@Controller
public class InvoiceSignController {

	

	    public InvoiceSignController(GoogleDriveOAuthService googleDriveOAuthService) {
	        this.googleDriveOAuthService = googleDriveOAuthService;
	    }


	   @Autowired
	   private GoogleDriveOAuthService googleDriveOAuthService;

	   @Autowired
	   private GoogleDriveService googleDriveService;

	
	@Autowired
	private NotificationRepository notificationRepository;
	
	@Autowired
	private PdfService pdfService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private InvoiceRepository invoiceRepository;
	
	@Autowired
	private InvoiceService invoiceService;
	
  /*  @GetMapping("/sign-invoice")
    public String signInvoicePage(@RequestParam Long invoiceId, 
                                  @RequestParam String pdfPath,
                                  Model model) {
        model.addAttribute("invoiceId", invoiceId);
        model.addAttribute("pdfPath", pdfPath);
        return "sign-invoice"; // returns sign-invoice.html
    }*/
    
//	@GetMapping("/invoices/{invoiceId}/sign")
//	public String getSignPage(@PathVariable Long invoiceId, 
//	                          @AuthenticationPrincipal UserDetails userDetails, 
//	                          Model model) {
//	    String username = userDetails.getUsername(); // signed-in user email
//
//	    // Fetch invoice
//	    Invoice invoice = invoiceRepository.findById(invoiceId)
//	            .orElseThrow(() -> new RuntimeException("Invoice not found"));
//
//	    // Authorization: only the assigned signer can access
//	    if (!invoice.getSigningAuthorityEmail().equals(username)) {
//	        return "error/403"; // Not authorized
//	    }
//
//	    model.addAttribute("invoice", invoice);
//	    return "sign-invoice"; // Render signing page
//	}
	@GetMapping("/invoices/{invoiceId}/sign")
	public String getSignPage(@PathVariable String invoiceId, 
	                          @AuthenticationPrincipal UserDetails userDetails, 
	                          Model model) {
	    String username = userDetails.getUsername(); // signed-in user email

	    // Fetch invoice by invoice number (alphanumeric)
	    Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceId)
	            .orElseThrow(() -> new RuntimeException("Invoice not found"));

	    // Authorization: only the assigned signer can access
	    if (!invoice.getSigningAuthorityEmail().equals(username)) {
	        return "error/403"; // Not authorized
	    }

	    model.addAttribute("invoice", invoice);
	    return "sign-invoice"; // Render signing page
	}


   /* @PostMapping("/invoices/{invoiceId}/embed-sign")
    public ResponseEntity<?> embedSignature(@PathVariable Long invoiceId,
                                            @RequestParam("signature") String base64Signature) throws Exception {

        Invoice invoice = invoiceRepository.findById(invoiceId).orElseThrow();
        byte[] decodedImg = Base64.getDecoder().decode(base64Signature.split(",")[1]);

        String signedPath = invoice.getPdfFilePath().replace(".pdf","-signed.pdf");
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(invoice.getPdfFilePath()),
                                             new PdfWriter(signedPath));
        Document doc = new Document(pdfDoc);

        ImageData imgData = ImageDataFactory.create(decodedImg);
        Image signImg = new Image(imgData).setWidth(120).setHeight(50).setAutoScale(false);

        // Fixed position = coordinates of reserved signature cell
        signImg.setFixedPosition(pdfDoc.getNumberOfPages(), 380, 120);
        doc.add(signImg);
        doc.close();

        invoice.setPdfFilePath(signedPath);
        invoiceRepository.save(invoice);

        return ResponseEntity.ok().build();
    }

    
  /*  @PostMapping("/invoices/{invoiceId}/sign")
    public ResponseEntity<String> signInvoice(
            @PathVariable Long invoiceId,
            @RequestParam("signature") String signatureDataUrl) {
     System.out.println("asdfghjkl");
        try {
            Invoice invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found"));

            // Path to the invoice PDF
            String pdfPath = invoice.getPdfFilePath();
            if (pdfPath == null || pdfPath.isEmpty()) {
                return ResponseEntity.badRequest().body("Invoice PDF not found");
            }

            // Embed signature into PDF
            pdfService.embedSignatureIntoPdf(pdfPath, signatureDataUrl);

            // Mark invoice as signed
            invoice.setSigned(true);
            invoice.setSignedAt(LocalDateTime.now());
            invoiceRepository.save(invoice);

            return ResponseEntity.ok("Invoice signed successfully-----contrl");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error signing invoice: " + e.getMessage());
        }
    }*/

	/*@PostMapping("/invoices/{invoiceId}/sign")
	public ResponseEntity<?> signInvoice(@PathVariable Long invoiceId,
	                                     @RequestParam("file") MultipartFile file,
	                                     @AuthenticationPrincipal UserDetails userDetails) { // inject logged-in user
	    try {
	        String username = userDetails.getUsername(); // email used as username

	        // 1. Find the invoice
	        Invoice invoice = invoiceRepository.findById(invoiceId)
	                .orElseThrow(() -> new RuntimeException("Invoice not found"));

	        // 2. Authorization check: only assigned signer can sign
	        if (!invoice.getSigningAuthorityEmail().equals(username)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                    .body(Map.of("error", "Not authorized to sign this invoice"));
	        }

	        // 3. Ensure directory exists
	        String uploadDir = "uploads/signed-invoices/";
	        Files.createDirectories(Paths.get(uploadDir));

	        // 4. Build file path
	        String fileName = "signed-invoice-" + invoiceId + ".pdf";
	        Path filePath = Paths.get(uploadDir, fileName);

	        // 5. Save signed PDF
	        Files.write(filePath, file.getBytes());

	        // 6. Update invoice record
	        invoice.setSignedFilePath(filePath.toString());
	        invoice.setSigned(true);             
	        invoice.setSignedAt(LocalDateTime.now());
	        invoiceRepository.save(invoice);

	        // 7. Notify cashiers
	        List<User> cashiers = userRepository.findByCompany(invoice.getCompany());
	        for (User cashier : cashiers) {
	            SignedNotification notification = new SignedNotification();
	            notification.setUsername(cashier.getUsername()); 
	            notification.setMessage("Invoice #" + invoice.getId() + " signed by manager at " + invoice.getSignedAt());
	            notification.setPdfPath(invoice.getSignedFilePath());
	            notificationRepository.save(notification);
	        }

	        return ResponseEntity.ok(Map.of(
	                "message", "Signed invoice saved successfully",
	                "signedPdfPath", filePath.toString()
	        ));

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", "Failed to save signed invoice", "details", e.getMessage()));
	    }
	}*/
//	@PostMapping("/invoices/{invoiceId}/sign")
//	public ResponseEntity<?> signInvoice(@PathVariable Long invoiceId,
//	                                     @RequestParam("file") MultipartFile file,
//	                                     @AuthenticationPrincipal UserDetails userDetails) {
//	    try {
//	        String username = userDetails.getUsername(); // email used as username
//
//	        // 1. Find the invoice
//	        Invoice invoice = invoiceRepository.findById(invoiceId)
//	                .orElseThrow(() -> new RuntimeException("Invoice not found"));
//
//	        // 2. Authorization check: only assigned signer can sign
//	        if (!invoice.getSigningAuthorityEmail().equals(username)) {
//	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//	                    .body(Map.of("error", "Not authorized to sign this invoice"));
//	        }
//	        System.out.println("Uploaded file name: " + file.getOriginalFilename());
//	        System.out.println("Uploaded file size: " + file.getSize());
//	        System.out.println("Uploaded content type: " + file.getContentType());
//
//	        if (file.isEmpty() || !"application/pdf".equalsIgnoreCase(file.getContentType())) {
//	            return ResponseEntity.badRequest().body(Map.of("error", "Invalid PDF file"));
//	        }
//	        // -------------------------------
//	        // 3. Upload signed PDF to Google Drive
//	        // -------------------------------
//	        String fileName = "signed-invoice-" + invoiceId + ".pdf";
//	        
//	       
//	     // Hardcode tokens for testing
//	        String accessToken = "";
//	        String refreshToken = "";
//
//	        googleDriveOAuthService.setTokens(accessToken, refreshToken);
//	        Drive drive = googleDriveOAuthService.buildDriveService();
//
//	        // Get or create "Signed Invoices" folder
//	        String folderId = googleDriveService.getOrCreateFolder(drive, "Signed Invoices");
//
//	        // Upload the file
//	        String fileUrl = googleDriveService.uploadFile(drive, fileName, file.getBytes(), "application/pdf", folderId);
//
//	        // -------------------------------
//	        // 4. Update invoice record
//	        // -------------------------------
//	        invoice.setSignedFilePath(fileUrl);  // save Google Drive file URL instead of local path
//	        invoice.setSigned(true);
//	        invoice.setSignedAt(LocalDateTime.now());
//	        invoiceRepository.save(invoice);
//
//	        // -------------------------------
//	        // 5. Notify cashiers
//	        // -------------------------------
//	        List<User> cashiers = userRepository.findByCompany(invoice.getCompany());
//	        for (User cashier : cashiers) {
//	            SignedNotification notification = new SignedNotification();
//	            notification.setUsername(cashier.getUsername());
//	            notification.setMessage("Invoice #" + invoice.getInvoiceNumber() + " signed by manager at " + invoice.getSignedAt());
//	            notification.setPdfPath(invoice.getSignedFilePath()); // URL
//	            notificationRepository.save(notification);
//	        }
//
//	        return ResponseEntity.ok(Map.of(
//	                "message", "Signed invoice saved successfully",
//	                "signedPdfUrl", fileUrl // return Drive URL
//	        ));
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//	                .body(Map.of("error", "Failed to save signed invoice", "details", e.getMessage()));
//	    }
//	}

	@PostMapping("/invoices/{invoiceNumber}/sign")
	public ResponseEntity<?> signInvoice(@PathVariable String invoiceNumber,
	                                     @RequestParam("file") MultipartFile file,
	                                     @AuthenticationPrincipal UserDetails userDetails) {
	    try {
	        String username = userDetails.getUsername(); // email used as username

	        // 1. Find the invoice by invoice number instead of ID
	        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
	                .orElseThrow(() -> new RuntimeException("Invoice not found"));

	        // 2. Authorization check: only assigned signer can sign
	        if (!invoice.getSigningAuthorityEmail().equals(username)) {
	            return ResponseEntity.status(HttpStatus.FORBIDDEN)
	                    .body(Map.of("error", "Not authorized to sign this invoice"));
	        }

	        if (file.isEmpty() || !"application/pdf".equalsIgnoreCase(file.getContentType())) {
	            return ResponseEntity.badRequest().body(Map.of("error", "Invalid PDF file"));
	        }

	        // 3. Upload signed PDF to Google Drive
	        String fileName = "signed-invoice-" + invoiceNumber + ".pdf";

	        // Hardcode tokens for testing
	        String accessToken = "";
	        String refreshToken = "";

	        googleDriveOAuthService.setTokens(accessToken, refreshToken);
	        Drive drive = googleDriveOAuthService.buildDriveService();

	        String folderId = googleDriveService.getOrCreateFolder(drive, "Signed Invoices");

	        String fileUrl = googleDriveService.uploadFile(
	                drive, fileName, file.getBytes(), "application/pdf", folderId
	        );

	        // 4. Update invoice record
	        invoice.setSignedFilePath(fileUrl);
	        invoice.setSigned(true);
	        invoice.setSignedAt(LocalDateTime.now());
	        invoiceRepository.save(invoice);

	        // 5. Notify cashiers
	        List<User> cashiers = userRepository.findByCompany(invoice.getCompany());
	        for (User cashier : cashiers) {
	            SignedNotification notification = new SignedNotification();
	            notification.setUsername(cashier.getUsername());
	            notification.setMessage("Invoice #" + invoice.getInvoiceNumber()
	                    + " signed by manager at " + invoice.getSignedAt());
	            notification.setPdfPath(invoice.getSignedFilePath());
	            notificationRepository.save(notification);
	        }

	        return ResponseEntity.ok(Map.of(
	                "message", "Signed invoice saved successfully",
	                "signedPdfUrl", fileUrl
	        ));

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body(Map.of("error", "Failed to save signed invoice", "details", e.getMessage()));
	    }
	}



    
    @GetMapping("/invoices/{invoiceId}/download")
    public ResponseEntity<Resource> downloadInvoice(@PathVariable Long invoiceId) throws IOException {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        Path filePath = Paths.get(invoice.getPdfFilePath()); // Server local path
        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
    
//    @GetMapping("/invoices/{invoiceId}/pdf")
//    public ResponseEntity<Resource> getInvoicePdf(@PathVariable Long invoiceId) throws IOException {
//        Invoice invoice = invoiceService.getInvoiceById(invoiceId);
//        Path path = Paths.get(invoice.getPdfFilePath()); // e.g. C:/invoices/invoice-245.pdf
//        
//        if (!Files.exists(path)) {
//            return ResponseEntity.notFound().build();
//        }
//
//        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_PDF)
//                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=invoice_" + invoiceId + ".pdf")
//                .body(resource);
//    }
    @GetMapping("/invoices/{invoiceNumber}/pdf")
    public ResponseEntity<Resource> getInvoicePdf(@PathVariable String invoiceNumber) throws IOException {
        Invoice invoice = invoiceService.getInvoiceByInvoiceNumber(invoiceNumber);
        if (invoice == null) {
            return ResponseEntity.notFound().build();
        }

        Path path = Paths.get(invoice.getPdfFilePath()); // e.g. C:/invoices/invoice-245.pdf
        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build();
        }

        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=" + path.getFileName().toString())
                .body(resource);
    }

    
    @GetMapping("/cashier/notifications")
    @ResponseBody
    public List<SignedNotification> getCashierNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername(); // logged-in cashier
        System.out.println("✅ Test endpoint called");
        return notificationRepository.findByUsernameAndIsReadFalseOrderByCreatedAtDesc(username);
    }

   /* @GetMapping("/cashier/download-signed/{id}")
    public ResponseEntity<Resource> downloadSignedPdf(@PathVariable Long id) throws IOException {
        // 1. Fetch the notification (or PDF path)
        SignedNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        // 2. Load file from stored path
        Path filePath = Paths.get(notification.getPdfPath());
        if (!Files.exists(filePath)) {
            throw new RuntimeException("File not found: " + filePath);
        }

        Resource resource = new UrlResource(filePath.toUri());

      
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }*/
    
    @GetMapping("/cashier/download-signed/{id}")
    public ResponseEntity<Resource> downloadSignedPdf(@PathVariable Long id) throws IOException {
        // 1. Fetch the notification
        SignedNotification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        String pdfPath = notification.getPdfPath();
        
        // 2. If it's a URL, redirect to it directly
        if (pdfPath.startsWith("http")) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, pdfPath)
                    .build();
        }
        
        // 3. Otherwise, serve local file
        Path filePath = Paths.get(pdfPath);
        if (!Files.exists(filePath)) {
            throw new RuntimeException("File not found: " + filePath);
        }

        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filePath.getFileName().toString() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
    
//    @GetMapping("/invoices/{id}/details")
//    public ResponseEntity<?> getInvoiceDetails(@PathVariable Long id) {
//        try {
//            Invoice invoice = invoiceService.getInvoiceById(id);
//            if (invoice == null) {
//                return ResponseEntity.notFound().build();
//            }
//            return ResponseEntity.ok(Map.of(
//                "invoiceNumber", invoice.getInvoiceNumber(),
//                "id", invoice.getId()
//            ));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                                 .body("Error fetching invoice details");
//        }
//    }
    @GetMapping("/invoices/{invoiceNumber}/details")
    public ResponseEntity<?> getInvoiceDetails(@PathVariable String invoiceNumber) {
        try {
            Invoice invoice = invoiceService.getInvoiceByInvoiceNumber(invoiceNumber);
            if (invoice == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Map.of(
                "invoiceNumber", invoice.getInvoiceNumber(),
                "id", invoice.getId()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error fetching invoice details");
        }
    }



}
