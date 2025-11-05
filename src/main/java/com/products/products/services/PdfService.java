package com.products.products.services;


import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.io.font.PdfEncodings;
import org.springframework.core.io.ClassPathResource;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.products.products.models.Company;
import com.products.products.models.CompanyBankDetails;
import com.products.products.models.CompanySettings;
import com.products.products.models.Customer;
import com.products.products.models.CustomerType;
import com.products.products.models.CustomerUtils;
import com.products.products.models.Invoice;
import com.products.products.models.Invoice.Item;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.products.products.models.InvoiceDTO;
import com.products.products.repositories.CompanyBankDetailsRepository;
import com.products.products.repositories.CompanySettingsRepository;
import com.products.products.repositories.InvoiceRepository;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

@Service
public class PdfService {
	
	
	@Autowired
    private InvoiceRepository invoiceRepository;
	
	@Autowired
	private CompanySettingsRepository companySettingsRepository;
	
	@Autowired
	private CompanyBankDetailsRepository companyBankDetailsRepository;
	
    @Autowired
    private CompanySettingsService companySettingsService;
	
	public byte[] generateInvoicePdf(Invoice invoice, CustomerType customerType) throws Exception {
	    if (customerType == CustomerType.BUSINESS) {
	    	 System.out.println("GST customerType: '" + customerType + "'");

	        return generateInvoicePdfB2B(invoice);
	    } else {
	    	System.out.println("GST customerType: '" + customerType + "'");
	        return generateInvoicePdf(invoice);
	    }
	}
	
/*	 private byte[] generateB2CInvoice(Invoice invoice) throws Exception {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfDocument pdf = new PdfDocument(new PdfWriter(baos));
	        Document document = new Document(pdf, PageSize.A4);
	        document.setMargins(20, 20, 20, 20);

	        document.add(buildHeader());
	        document.add(new Paragraph("").setMarginTop(20f));
	        document.add(new Paragraph("TAX INVOICE")
	                .setTextAlignment(TextAlignment.CENTER)
	                .setBold()
	                .setFontSize(14));
	        document.add(new Paragraph("").setMarginTop(10f));

	        Table invoiceInfo = buildB2CInvoiceInfoTable(invoice);
	        document.add(invoiceInfo);

	        Table mainTable = buildB2CInvoiceTable(invoice);
	        document.add(mainTable);

	        document.close();
	        return baos.toByteArray();
	    }
	 
	 private byte[] generateB2BInvoice(Invoice invoice) throws Exception {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
	        PdfDocument pdf = new PdfDocument(new PdfWriter(baos));
	        Document document = new Document(pdf, PageSize.A4);
	        document.setMargins(20, 20, 20, 20);

	        document.add(buildHeader());
	        document.add(new Paragraph("").setMarginTop(20f));
	        document.add(new Paragraph("TAX INVOICE")
	                .setTextAlignment(TextAlignment.CENTER)
	                .setBold()
	                .setFontSize(14));
	        document.add(new Paragraph("").setMarginTop(10f));

	        Table mainTable = buildB2BInvoiceTable(invoice);
	        document.add(mainTable);

	        document.close();
	        return baos.toByteArray();
	    }
	 
	  private Table buildB2CInvoiceInfoTable(Invoice invoice) {
	        Table invoiceInfoTable = new Table(UnitValue.createPercentArray(new float[]{2, 3}))
	                .setWidth(UnitValue.createPercentValue(40)) // left side only
	                .setMarginTop(10f);

	        invoiceInfoTable.addCell(new Cell().add(new Paragraph("Invoice No:").setBold())
	                .setBorder(Border.NO_BORDER)
	                .setTextAlignment(TextAlignment.LEFT));
	        invoiceInfoTable.addCell(new Cell().add(new Paragraph("xxxxxxxxxxxxxxxx"))
	                .setBorder(Border.NO_BORDER)
	                .setTextAlignment(TextAlignment.LEFT));

	        invoiceInfoTable.addCell(new Cell().add(new Paragraph("Invoice Date:").setBold())
	                .setBorder(Border.NO_BORDER)
	                .setTextAlignment(TextAlignment.LEFT));
	        invoiceInfoTable.addCell(new Cell().add(new Paragraph(invoice.getDate().toString()))
	                .setBorder(Border.NO_BORDER)
	                .setTextAlignment(TextAlignment.LEFT));

	        return invoiceInfoTable;
	    }

	
	private Table buildHeader() throws IOException {
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 4}))
                .useAllAvailableWidth();

        Image logo = new Image(ImageDataFactory.create("src/main/resources/static/logo.png"))
                .setWidth(100)
                .setAutoScale(true);

        Cell logoCell = new Cell().add(logo)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        Paragraph companyDetails = new Paragraph(
                "ETIQUETTE TECHNOLOGIES (OPC) PVT. LTD.\n" +
                "7, A. I. B. E. A Colony, Tiruchirappalli, Tamil Nadu - 620005\n" +
                "Mobile: XXXXXXXXXX\n" +
                "Email: contact@etiquette-tech.com\n" +
                "GSTIN: XXXXXXXXXXXXXXXX\nPAN: XXXXXXXXXX")
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(9);

        Cell detailsCell = new Cell().add(companyDetails)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        headerTable.addCell(logoCell);
        headerTable.addCell(detailsCell);
        return headerTable;
    }

	private Table buildB2CInvoiceTable(Invoice invoice) {
	    Table mainTable = new Table(new float[]{1, 3, 1, 2, 2})
	            .setWidth(UnitValue.createPercentValue(100))
	            .setFontSize(10)
	            .setMarginTop(15f)
	            .setMinHeight(200f);

	    // -------- Invoice Info rows --------
	    mainTable.addCell(new Cell(1, 2).add(new Paragraph("Invoice No:").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("XXXXX")));
	    mainTable.addCell(new Cell().add(new Paragraph("Invoice Date").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph(invoice.getDate().toString())));

	    // -------- Items header --------
	    mainTable.addCell(new Cell().add(new Paragraph("S.No").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Particulars").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Hours Spent").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Hourly Rate (USD)").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Amount (INR)").setBold()));

	    // -------- Dynamic Items --------
	    int count = 1;
	    double total = 0;
	    List<Invoice.Item> items = invoice.getItems();
	    for (Invoice.Item item : items) {
	        mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(count++))));
	        mainTable.addCell(new Cell().add(new Paragraph(item.getProductName())));
	        mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
	        mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getPrice()))));
	        mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getTotalPrice()))));
	        total += item.getTotalPrice();
	    }

	    double igstAmount = total * 0.18;
	    String igstFormatted = String.format("%.2f", igstAmount);

	    // -------- Totals --------
	    mainTable.addCell(new Cell(1, 3).add(new Paragraph("Total Amount in Words").setBold())
	            .setTextAlignment(TextAlignment.CENTER));
	    mainTable.addCell(new Cell().add(new Paragraph("IGST 18%").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph(igstFormatted)));

	    mainTable.addCell(new Cell(1, 3).add(new Paragraph(convertNumberToWords((long) (total + igstAmount)))));
	    mainTable.addCell(new Cell().add(new Paragraph("Grand Total").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph((total + igstAmount) + " INR").setBold()));

	    // -------- Footer --------
	    mainTable.addCell(new Cell(1, 5).add(buildFooter())); // footer spans all 5 columns

	    return mainTable;
	}

	private Table buildB2BInvoiceTable(Invoice invoice) {
	    Table mainTable = new Table(UnitValue.createPercentArray(new float[]{0.5f, 2.5f, 2.0f, 2.5f, 2.5f}))
	            .useAllAvailableWidth()
	            .setFontSize(10)
	            .setMarginTop(15f)
	            .setMinHeight(550f);

	    // -------- Invoice Info rows --------
	    mainTable.addCell(new Cell(1, 2).add(new Paragraph("Invoice No:").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("XXXXX")));
	    mainTable.addCell(new Cell().add(new Paragraph("Transport Mode").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("NA")));

	    mainTable.addCell(new Cell(1, 2).add(new Paragraph("Invoice Date:").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("08/09/2025")));
	    mainTable.addCell(new Cell().add(new Paragraph("Vehicle Number").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("NA")));

	    // -------- HSN row --------
	    mainTable.addCell(new Cell(1, 2).add(new Paragraph("HSN:").setBold()).setTextAlignment(TextAlignment.LEFT));
	    mainTable.addCell(new Cell(1, 3).add(new Paragraph("Information technology (IT) consulting and support services")));

	    // -------- Bill to / Ship to --------
	    mainTable.addCell(new Cell(1, 3).add(new Paragraph("Bill to Party").setBold()).setTextAlignment(TextAlignment.CENTER));
	    mainTable.addCell(new Cell(1, 2).add(new Paragraph("Ship to Party").setBold()).setTextAlignment(TextAlignment.CENTER));

	    mainTable.addCell(new Cell(1, 3).add(new Paragraph("Name:\nGSTIN / UIN: XXXXXXXX")));
	    mainTable.addCell(new Cell(1, 2).add(new Paragraph("Name:\nGSTIN / UIN: XXXXXXXX")));

	    // -------- Items header --------
	    mainTable.addCell(new Cell().add(new Paragraph("S.No").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Particulars").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Hours Spent").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Hourly Rate (USD)").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Amount (INR)").setBold()));

	    // -------- Example item row --------
	    mainTable.addCell(new Cell().add(new Paragraph("1")));
	    mainTable.addCell(new Cell().add(new Paragraph("Consulting Services")));
	    mainTable.addCell(new Cell().add(new Paragraph("10")));
	    mainTable.addCell(new Cell().add(new Paragraph("100")));
	    mainTable.addCell(new Cell().add(new Paragraph("1000")));

	    // -------- Totals --------
	    mainTable.addCell(new Cell(1, 3).add(new Paragraph("Total Amount in Words").setBold()).setTextAlignment(TextAlignment.CENTER));
	    mainTable.addCell(new Cell().add(new Paragraph("IGST 18%").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("1000")));

	    mainTable.addCell(new Cell(1, 3).add(new Paragraph("One Lakh Ninety-One Thousand Four Hundred and Eighty-One Rupees")));
	    mainTable.addCell(new Cell().add(new Paragraph("Grand Total").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("1000 INR").setBold()));

	    // -------- Footer --------
	    mainTable.addCell(new Cell(1, 5).add(buildFooter())); // footer spans all 5 columns

	    return mainTable;
	}

	private Div buildFooter() {
	    Div certDiv = new Div();

	    certDiv.add(new Paragraph("Certified that the particulars given above are true and correct")
	            .setFontSize(8)
	            .setTextAlignment(TextAlignment.CENTER)
	            .setItalic()
	            .setMargin(0));

	    certDiv.add(new Paragraph("For Etiquette Technologies (OPC) Private Limited.")
	            .setFontSize(8)
	            .setTextAlignment(TextAlignment.CENTER)
	            .setBold()
	            .setMargin(0));

	    certDiv.add(new Paragraph("\n\n\n")); // spacer

	    certDiv.add(new Paragraph("Authorized Signatory")
	            .setFontSize(8)
	            .setTextAlignment(TextAlignment.CENTER)
	            .setItalic());

	    return certDiv;
	}

	 private String convertNumberToWords(long number) {
	        if (number == 0) return "Zero";

	        String[] units = {
	                "", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
	                "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen",
	                "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
	        };

	        String[] tens = {
	                "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty", "Ninety"
	        };

	        StringBuilder words = new StringBuilder();
	        if (number / 100000 > 0) {
	            words.append(convertNumberToWords(number / 100000)).append(" Lakh ");
	            number %= 100000;
	        }
	        if (number / 1000 > 0) {
	            words.append(convertNumberToWords(number / 1000)).append(" Thousand ");
	            number %= 1000;
	        }
	        if (number / 100 > 0) {
	            words.append(convertNumberToWords(number / 100)).append(" Hundred ");
	            number %= 100;
	        }
	        if (number > 0) {
	            if (number < 20) {
	                words.append(units[(int) number]);
	            } else {
	                words.append(tens[(int) number / 10]);
	                if (number % 10 > 0) {
	                    words.append(" ").append(units[(int) number % 10]);
	                }
	            }
	        }
	        return words.toString().trim();
	    }
	}*/
	
	 public byte[] generateInvoicePdf(Invoice invoice) throws Exception {
	        ByteArrayOutputStream baos = new ByteArrayOutputStream();
 
	        PdfWriter writer = new PdfWriter(baos);
	        PdfDocument pdf = new PdfDocument(writer);
	        Document document = new Document(pdf, PageSize.A4);
	        document.setMargins(20, 20, 20, 20);

	        // ---------------- Header ----------------
	        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 4}))
	                .useAllAvailableWidth();
	        CompanySettings settings = companySettingsRepository.findByCompany(invoice.getCompany());
	      /*  Image logo = new Image(ImageDataFactory.create("src/main/resources/static/logo.png"))
	                .setWidth(100)
	                .setAutoScale(true);*/
	        String logoPath = settings.getLogoPath(); // e.g. "/uploads/company-logo.png"
	        Image logo = null;
	        Cell logoCell = null; // ✅ Declare before if/else

	        if (logoPath != null && !logoPath.isEmpty()) {
	            try {
	                String fullLogoPath = new File("src/main/resources/static/" + logoPath).getAbsolutePath();
	                System.out.println("full path logo-----"+fullLogoPath );
	                logo = new Image(ImageDataFactory.create(fullLogoPath))
	                        .setWidth(100)
	                        .setAutoScale(true);
	                // document.add(logo); <-- Remove this line
	            } catch (Exception e) {
	                System.err.println("Error loading logo: " + e.getMessage());
	            }
	        }
	        else {
	            System.out.println("No logo found in company settings. Skipping logo display.");
	        }

	        // ✅ Now create logoCell after logo is known
	        if (logo != null) {
	            logoCell = new Cell().add(logo)
	                    .setBorder(Border.NO_BORDER)
	                    .setVerticalAlignment(VerticalAlignment.MIDDLE);
	        } else {
	            logoCell = new Cell().add(new Paragraph(" "))
	                    .setBorder(Border.NO_BORDER)
	                    .setVerticalAlignment(VerticalAlignment.MIDDLE);
	        }

	        // ✅ Company details paragraph
	        Paragraph companyDetails = new Paragraph(
	                settings.getCompanyName() + "\n" +
	                settings.getCompanyAddress() + "\n" +
	                "Mobile: " + settings.getCompanyPhone() + "\n" +
	                "Email: " + settings.getCompanyEmail() + "\n" +
	                "GSTIN: " + settings.getGstin() + "\n" +
	                "PAN: " + settings.getPan())
	                .setTextAlignment(TextAlignment.RIGHT)
	                .setFontSize(9);

	        Cell detailsCell = new Cell().add(companyDetails)
	                .setBorder(Border.NO_BORDER)
	                .setVerticalAlignment(VerticalAlignment.MIDDLE);

	        // ✅ Add both cells to the table
	        headerTable.addCell(logoCell);
	        headerTable.addCell(detailsCell);

	        document.add(headerTable);


	        document.add(new Paragraph("").setMarginTop(20f));

	        // ---------------- Title ----------------
	        Paragraph title = new Paragraph("TAX INVOICE")
	                .setTextAlignment(TextAlignment.CENTER)
	                .setBold()
	                .setFontSize(14);
	        document.add(title);

	        document.add(new Paragraph("").setMarginTop(10f));
	     // ---------------- Invoice Info (Clean Left Alignment) ----------------
	        Table invoiceInfoTable = new Table(UnitValue.createPercentArray(new float[]{2, 3}))
	                .setWidth(UnitValue.createPercentValue(40)) // left side only
	                .setMarginTop(10f);

	        invoiceInfoTable.addCell(new Cell().add(new Paragraph("Invoice No:").setBold())
	                .setBorder(Border.NO_BORDER)
	                .setTextAlignment(TextAlignment.LEFT));
	        invoiceInfoTable.addCell(new Cell().add(new Paragraph(invoice.getInvoiceNumber()))
	                .setBorder(Border.NO_BORDER)
	                .setTextAlignment(TextAlignment.LEFT));

	        invoiceInfoTable.addCell(new Cell().add(new Paragraph("Invoice Date:").setBold())
	                .setBorder(Border.NO_BORDER)
	                .setTextAlignment(TextAlignment.LEFT));
	        invoiceInfoTable.addCell(new Cell().add(new Paragraph(invoice.getDate().toString()))
	                .setBorder(Border.NO_BORDER)
	                .setTextAlignment(TextAlignment.LEFT));

	        document.add(invoiceInfoTable);



	        // ---------------- Main Table ----------------
	        Table mainTable = buildInvoiceTableb2c(invoice);
	        document.add(mainTable);

	        document.close();
	        return baos.toByteArray();
	    }

	    private Table buildInvoiceTableb2c(Invoice invoice) {
	        Table mainTable = new Table(new float[]{1, 3, 1, 2, 2})
	                .setWidth(UnitValue.createPercentValue(100))
	                .setFontSize(10)
	                .setMarginTop(15f)
	                .setMinHeight(200f);
	       
	        // -------- Invoice Info rows --------
	       // mainTable.addCell(new Cell(1, 2).add(new Paragraph("Invoice No:").setBold()));
	        //mainTable.addCell(new Cell().add(new Paragraph("XXXXX")));
	       // mainTable.addCell(new Cell().add(new Paragraph("Invoice Date").setBold()));
	       // mainTable.addCell(new Cell().add(new Paragraph("NA")));

	       
	       // -------- Items header --------
	    mainTable.addCell(new Cell().add(new Paragraph("S.No").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Particulars").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Hours Spent").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Hourly Rate (USD)").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Amount (INR)").setBold()));

	    // -------- Dynamic Items --------
	    int count = 1;
	    double total = 0;
	    List<Invoice.Item> items = invoice.getItems();
	    for (Invoice.Item item : items) {
	        mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(count++))));
	        mainTable.addCell(new Cell().add(new Paragraph(item.getProductName())));
	        mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
	        mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getPrice()))));
	        mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getTotalPrice()))));

	        total += item.getPrice();
	    }
	    double igstAmount = total * 0.18;
	    String igstFormatted = String.format("%.2f", igstAmount);
	    // -------- Totals --------
	    mainTable.addCell(new Cell(1, 3).add(new Paragraph("Total Amount in Words").setBold())
	            .setTextAlignment(TextAlignment.CENTER));
	    mainTable.addCell(new Cell().add(new Paragraph("IGST 18%").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph(igstFormatted)));

	    mainTable.addCell(new Cell(1, 3).add(new Paragraph(convertNumberToWords((long) (total + total * 0.18)))));
	    mainTable.addCell(new Cell().add(new Paragraph("Grand Total").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph((total + total * 0.18) + " INR").setBold()));

	        // Bank details header + GST reverse charge
	        mainTable.addCell(new Cell(1, 3)
	                .add(new Paragraph("BANK DETAILS").setBold())
	                .setTextAlignment(TextAlignment.CENTER)
	                .setBorderTop(new SolidBorder(1))
	                .setBorderLeft(new SolidBorder(1))
	                .setBorderRight(new SolidBorder(1))
	                .setBorderBottom(Border.NO_BORDER));

	        mainTable.addCell(new Cell().add(new Paragraph("GST on Reverse Charge").setBold()));
	        mainTable.addCell(new Cell().add(new Paragraph("NA").setBold()));

	     // 🔹 Fetch company + bank details
	        CompanySettings settings = companySettingsService.getCompanySettings();
	        CompanyBankDetails bankDetails = companyBankDetailsRepository.findByCompanySettingsId(settings.getId());

	        // 🔹 Build dynamic bank details text
	        String bankInfo = "Bank IFSC: " + (bankDetails != null ? bankDetails.getIfscCode() : "N/A") + "\n" +
	                          "Company Name: " + settings.getCompanyName() + "\n" +
	                          "Bank Name: " + (bankDetails != null ? bankDetails.getBankName() : "N/A") + "\n" +
	                          "Account Number: " + (bankDetails != null ? bankDetails.getAccountNumber() : "N/A") + "\n" +
	                          "Branch: " + (bankDetails != null ? bankDetails.getBankBranch() : "N/A");

	        // 🔹 Add to table
	        mainTable.addCell(new Cell(1, 3)
	                .add(new Paragraph(bankInfo))
	                .setTextAlignment(TextAlignment.LEFT)
	                .setBorderTop(Border.NO_BORDER)
	                .setBorderLeft(new SolidBorder(1))
	                .setBorderRight(new SolidBorder(1))
	                .setBorderBottom(new SolidBorder(1)));

	        Cell certCell = new Cell(1, 2);
	        Div certDiv = new Div();

	        certDiv.add(new Paragraph("Certified that the particulars given above are true and correct")
	                .setFontSize(8)
	                .setTextAlignment(TextAlignment.CENTER)
	                .setItalic()
	                .setMargin(0));

	        certDiv.add(new Paragraph("For Etiquette Technologies (OPC) Private Limited.")
	                .setFontSize(8)
	                .setTextAlignment(TextAlignment.CENTER)
	                .setBold()
	                .setMargin(0));

	        certDiv.add(new Paragraph("\n\n\n")); // spacer

	        certDiv.add(new Paragraph("Authorized Signatory")
	                .setFontSize(8)
	                .setTextAlignment(TextAlignment.CENTER)
	                .setItalic());

	        certCell.add(certDiv);
	        certCell.setVerticalAlignment(VerticalAlignment.BOTTOM);
	        mainTable.addCell(certCell);

	        return mainTable;
	    }

	
private String convertNumberToWords(long number) {
    if (number == 0) return "Zero";

    String[] units = {
        "", "One", "Two", "Three", "Four", "Five", "Six", "Seven",
        "Eight", "Nine", "Ten", "Eleven", "Twelve", "Thirteen",
        "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen", "Nineteen"
    };

    String[] tens = {
        "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty",
        "Seventy", "Eighty", "Ninety"
    };

    StringBuilder words = new StringBuilder();
    if (number / 1000 > 0) {
        words.append(convertNumberToWords(number / 1000)).append(" Thousand ");
        number %= 1000;
    }
    if (number / 100 > 0) {
        words.append(convertNumberToWords(number / 100)).append(" Hundred ");
        number %= 100;
    }
    if (number > 0) {
        if (number < 20) {
            words.append(units[(int) number]);
        } else {
            words.append(tens[(int) number / 10]);
            if (number % 10 > 0) {
                words.append(" ").append(units[(int) number % 10]);
            }
        }
    }
    return words.toString().trim();
}



   public byte[] generateInvoicePdfB2B(Invoice invoice) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        String dir = "C:/invoices";
        File folder = new File(dir);
        if (!folder.exists()) folder.mkdirs();
        String outPath = dir + "/invoice-" + invoice.getId() + ".pdf";

        // Use PdfWriter to write to both byte array and file
        try (FileOutputStream fos = new FileOutputStream(outPath)) {
        	 PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(20, 20, 20, 20);

     // Load font bytes from classpath
        ClassPathResource fontResource = new ClassPathResource("fonts/Verdana.ttf");
        byte[] fontBytes = fontResource.getInputStream().readAllBytes();

        // Create FontProgram from byte array
        FontProgram fontProgram = FontProgramFactory.createFont(fontBytes);

        // Create PdfFont — modern, non-deprecated way
        PdfFont verdana = PdfFontFactory.createFont(fontProgram);
        
        ClassPathResource boldFontResource = new ClassPathResource("fonts/Verdana-Bold.ttf");
        byte[] boldBytes = boldFontResource.getInputStream().readAllBytes();
        FontProgram boldProgram = FontProgramFactory.createFont(boldBytes);
        PdfFont verdanaBold = PdfFontFactory.createFont(boldProgram);
        
        // ---------------- Header ----------------
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{1, 4}))
                .useAllAvailableWidth();

        
        
        Image logo = new Image(ImageDataFactory.create("src/main/resources/static/logo.png"))
                .setWidth(100)
                .setAutoScale(true);

        Cell logoCell = new Cell().add(logo)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);
        
        CompanySettings settings = companySettingsRepository.findByCompany(invoice.getCompany());
        
        Paragraph companyDetails = new Paragraph(
                settings.getCompanyName() + "\n" +
                settings.getCompanyAddress() + "\n" +
                "Mobile: " + settings.getCompanyPhone() + "\n" +
                "Email: " + settings.getCompanyEmail() + "\n" +
                "GSTIN: " + settings.getGstin() + "\n" +
                "PAN: " + settings.getPan())
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(9);

    

        Cell detailsCell = new Cell().add(companyDetails)
                .setBorder(Border.NO_BORDER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE);

        headerTable.addCell(logoCell);
        headerTable.addCell(detailsCell);
        document.add(headerTable);

        document.add(new Paragraph("").setMarginTop(20f));


        // ---------------- Title ----------------
        Paragraph title = new Paragraph("TAX INVOICE")
                .setTextAlignment(TextAlignment.CENTER)
                .setBold()
                .setFont(verdanaBold)
                .setFontSize(14);
        document.add(title);

        document.add(new Paragraph("").setMarginTop(10f));

        // ---------------- Main Table ----------------
        Table mainTable = buildInvoiceTable(invoice);
        mainTable.setFont(verdana); 
        document.add(mainTable);

        document.close();
        }
        
        invoice.setPdfFilePath(outPath);
        invoiceRepository.save(invoice);
        
        return Files.readAllBytes(Paths.get(outPath));
    }

   private Table buildInvoiceTable(Invoice invoice) throws IOException {
	    Table mainTable = new Table(UnitValue.createPercentArray(new float[]{0.5f, 2.5f, 2.0f, 2.5f, 2.5f}))
	            .setWidth(UnitValue.createPercentValue(100))
	            .setFontSize(10)
	            .setMarginTop(15f)
	            .setMinHeight(200f);

	    // -------- Invoice Info rows --------
	    mainTable.addCell(new Cell(1, 2).add(new Paragraph("Invoice No:").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(invoice.getInvoiceNumber()))));
	    mainTable.addCell(new Cell().add(new Paragraph("Transport Mode").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph(invoice.getTransportMode() != null ? invoice.getTransportMode() : "NA")));
	    mainTable.addCell(new Cell(1, 2).add(new Paragraph("Invoice Date:").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph(invoice.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))));
	    mainTable.addCell(new Cell().add(new Paragraph("Vehicle Number").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph(invoice.getVehicleNumber() != null ? invoice.getVehicleNumber() : "NA")));
	    mainTable.addCell(new Cell(1, 2).add(new Paragraph("Reverse Charge (Y/N):").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph(invoice.isReverseCharge() ? "Y" : "N")));
	    mainTable.addCell(new Cell().add(new Paragraph("Date of Supply").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph(invoice.getSupplyDate() != null ? invoice.getSupplyDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "NA")));
	    mainTable.addCell(new Cell(1, 2).add(new Paragraph("State:").setBold()));
	    String stateName = getStateNameFromGstin(invoice.getCustomerGstin());
	    mainTable.addCell(new Cell().add(new Paragraph(stateName != null ? stateName : "NA")));
	    mainTable.addCell(new Cell().add(new Paragraph("Due Date").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph(invoice.getDueDate() != null ? invoice.getDueDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "NA")));
	    mainTable.addCell(new Cell(1, 2).add(new Paragraph("HSN:").setBold()).setTextAlignment(TextAlignment.LEFT));
	    mainTable.addCell(new Cell(1, 3).add(new Paragraph("Information technology (IT) consulting and support services")));

	    // Bill to / Ship to
	    mainTable.addCell(new Cell(1, 3).add(new Paragraph("Bill to Party").setBold()).setTextAlignment(TextAlignment.CENTER));
	    mainTable.addCell(new Cell(1, 2).add(new Paragraph("Ship to Party").setBold()).setTextAlignment(TextAlignment.CENTER));
	    mainTable.addCell(new Cell(1, 3).add(new Paragraph("Name: " + invoice.getCustomerName() +
	            "\nGSTIN / UIN: " +
	            (invoice.getCustomerGstin() != null ? invoice.getCustomerGstin() : "N/A"))));
	    mainTable.addCell(new Cell(1, 2).add(new Paragraph("Name: " + invoice.getCustomerName() +
	            "\nGSTIN / UIN: " +
	            (invoice.getCustomerGstin() != null ? invoice.getCustomerGstin() : "N/A"))));

	    // Table headers
	    mainTable.addCell(new Cell().add(new Paragraph("S.No").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Particulars").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Hours Spent").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Hourly Rate (USD)").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph("Amount (INR)").setBold()));

	    // Dynamic Items
	    int count = 1;
	    double total = 0;
	    List<Invoice.Item> items = invoice.getItems();
	    for (Invoice.Item item : items) {
	        mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(count++))));
	        mainTable.addCell(new Cell().add(new Paragraph(item.getProductName())));
	        mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity()))));
	        mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getPrice()))));
	        mainTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getTotalPrice()))));

	        total += item.getPrice();
	    }

	    double igstAmount = total * 0.18;
	    String igstFormatted = String.format("%.2f", igstAmount);

	    // Totals
	    mainTable.addCell(new Cell(1, 3).add(new Paragraph("Total Amount in Words").setBold()).setTextAlignment(TextAlignment.CENTER));
	    mainTable.addCell(new Cell().add(new Paragraph("IGST 18%").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph(igstFormatted)));
	    mainTable.addCell(new Cell(1, 3).add(new Paragraph(convertNumberToWords((long) (total + igstAmount))).setTextAlignment(TextAlignment.CENTER)));
	    mainTable.addCell(new Cell().add(new Paragraph("Grand Total").setBold()));
	    mainTable.addCell(new Cell().add(new Paragraph((total + igstAmount) + " INR").setBold()));

	    // Bank details
	    CompanySettings settings = companySettingsService.getCompanySettings();
	    CompanyBankDetails bankDetails = companyBankDetailsRepository.findByCompanySettingsId(settings.getId());
	    String bankInfo = "Bank IFSC: " + (bankDetails != null ? bankDetails.getIfscCode() : "N/A") + "\n" +
	            "Company Name: " + settings.getCompanyName() + "\n" +
	            "Bank Name: " + (bankDetails != null ? bankDetails.getBankName() : "N/A") + "\n" +
	            "Account Number: " + (bankDetails != null ? bankDetails.getAccountNumber() : "N/A") + "\n" +
	            "Branch: " + (bankDetails != null ? bankDetails.getBankBranch() : "N/A");
	    mainTable.addCell(new Cell(1, 3).add(new Paragraph(bankInfo)).setTextAlignment(TextAlignment.LEFT)
	            .setBorderTop(Border.NO_BORDER).setBorderLeft(new SolidBorder(1)).setBorderRight(new SolidBorder(1)));

	    // Certification / Signature
	    Cell certCell = new Cell(1, 2);
	    Div certDiv = new Div();

	    certDiv.add(new Paragraph("Certified that the particulars given above are true and correct")
	            .setFontSize(8).setTextAlignment(TextAlignment.CENTER).setItalic().setMargin(0));
	    certDiv.add(new Paragraph("For " + settings.getCompanyName()).setFontSize(8).setTextAlignment(TextAlignment.CENTER).setBold().setMargin(0));

	    // Insert signature image if present
	    if (invoice.getSignatureImage() != null) { // byte[] of signature
	        ImageData imgData = ImageDataFactory.create(invoice.getSignatureImage());
	        Image signImg = new Image(imgData).setWidth(120).setHeight(50).setAutoScale(false);
	        certDiv.add(signImg); // dynamically add signature image
	    } else {
	        certDiv.add(new Paragraph("\n\n\n")); // empty space reserved for signature
	    }

	    certDiv.add(new Paragraph("Authorized Signatory").setFontSize(8).setTextAlignment(TextAlignment.CENTER).setItalic());
	    certCell.add(certDiv);
	    certCell.setVerticalAlignment(VerticalAlignment.BOTTOM);
	    mainTable.addCell(certCell);

	    // Apply cell padding
	    for (IElement element : mainTable.getChildren()) {
	        if (element instanceof Cell cell) {
	            cell.setPadding(5f);
	        }
	    }

	    return mainTable;
	}

    
    
    private String getStateNameFromGstin(String gstin) {
        if (gstin == null || gstin.length() < 2) return "NA";
        
        String code = gstin.substring(0, 2);
        return switch (code) {
            case "01" -> "Jammu & Kashmir";
            case "02" -> "Himachal Pradesh";
            case "03" -> "Punjab";
            case "04" -> "Chandigarh";
            case "05" -> "Uttarakhand";
            case "06" -> "Haryana";
            case "07" -> "Delhi";
            case "08" -> "Rajasthan";
            case "09" -> "Uttar Pradesh";
            case "10" -> "Bihar";
            case "11" -> "Sikkim";
            case "12" -> "Arunachal Pradesh";
            case "13" -> "Nagaland";
            case "14" -> "Manipur";
            case "15" -> "Mizoram";
            case "16" -> "Tripura";
            case "17" -> "Meghalaya";
            case "18" -> "Assam";
            case "19" -> "West Bengal";
            case "20" -> "Jharkhand";
            case "21" -> "Odisha";
            case "22" -> "Chhattisgarh";
            case "23" -> "Madhya Pradesh";
            case "24" -> "Gujarat";
            case "25" -> "Daman & Diu";
            case "26" -> "Dadra & Nagar Haveli";
            case "27" -> "Maharashtra";
            case "28" -> "Andhra Pradesh";
            case "29" -> "Karnataka";
            case "30" -> "Goa";
            case "31" -> "Lakshadweep";
            case "32" -> "Kerala";
            case "33" -> "Tamil Nadu";
            case "34" -> "Puducherry";
            case "35" -> "Andaman & Nicobar";
            case "36" -> "Telangana";
            case "37" -> "Andhra Pradesh (New)";
            default -> "NA";
        };
    }

    
    public void embedSignatureIntoPdf(String pdfPath, String signatureDataUrl) throws IOException {
        // Decode Base64 signature image
        String base64Image = signatureDataUrl.split(",")[1]; // remove "data:image/png;base64,"
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        System.out.println("123456789");
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfPath), new PdfWriter(pdfPath.replace(".pdf", "-signed.pdf")));
        Document document = new Document(pdfDoc);

        ImageData imgData = ImageDataFactory.create(imageBytes);
        Image signature = new Image(imgData).scaleToFit(200, 100);

        // Add signature at bottom-right corner of last page
        PdfPage lastPage = pdfDoc.getLastPage();
        float x = lastPage.getPageSize().getWidth() - 220; // margin from right
        float y = 50; // margin from bottom
        signature.setFixedPosition(pdfDoc.getNumberOfPages(), x, y);

        document.add(signature);
        document.close();
    }

}


   /* private static final double TAX_RATE = 0.18; // 18% tax rate

    public byte[] generateInvoicePdf(Invoice invoice) throws IOException {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.OVERWRITE, true)) {
            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            float yPosition = yStart;

            // Left Top Corner: Company Info (hardcoded)
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Company Name");
            yPosition -= 15;
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Company Address");
            yPosition -= 15;
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Company Phone Number");
            contentStream.endText();

            // Right Top Corner: Invoice Info
            float rightX = page.getMediaBox().getWidth() - margin - 150; // Adjust for right alignment
            float rightY = yStart;
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
            contentStream.newLineAtOffset(rightX, rightY);
            contentStream.showText("INVOICE");
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(0, -25); // Move down for date
            contentStream.showText("Date: " + invoice.getDate());
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Bill To: " + invoice.getCustomerName());
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Phone No: " + invoice.getCustomerPhoneNumber());
            contentStream.endText();

            // Space after header section
            yPosition -= 120;

            // Table Header
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.showText("Product Name        Qty     Unit Price      Total Price");
            yPosition -= 15;
            contentStream.endText();

            // Table Rows (Product Details)
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            List<Item> items = invoice.getItems();
            double subtotal = 0;
            for (Item item : items) {
                double totalPricePerItem = item.getQuantity() * item.getPrice();
                subtotal += totalPricePerItem;

                contentStream.beginText();
                contentStream.newLineAtOffset(margin, yPosition);
                contentStream.showText(String.format("%-20s %5d %10.2f %12.2f",
                        item.getProductName(), item.getQuantity(), item.getPrice(), totalPricePerItem));
                contentStream.endText();
                yPosition -= 15; // Move to next row
            }

            // Calculate tax and total
            double taxAmount = subtotal * TAX_RATE;
            double totalAmount = subtotal + taxAmount;

            // Space before summary
            yPosition -= 30;

            // Summary (Subtotal, Tax, Sales Tax, Total)
            contentStream.beginText();
            contentStream.newLineAtOffset(margin, yPosition);
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
            contentStream.showText(String.format("Subtotal: %35.2f", subtotal));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText(String.format("Tax Rate: 18%%"));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText(String.format("Sales Tax: %32.2f", taxAmount));
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText(String.format("Total: %38.2f", totalAmount));
            contentStream.endText();
        }

        // Save the document to a byte array output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        return outputStream.toByteArray();
    }*/


