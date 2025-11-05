package com.products.products.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.io.source.OutputStream;
import org.springframework.http.MediaType;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import com.google.zxing.MultiFormatWriter;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class BarcodeGenerator {

	
	@GetMapping(value = "/barcode/{code}", produces = MediaType.IMAGE_PNG_VALUE)
    public void generateBarcode(@PathVariable String code, HttpServletResponse response) throws Exception {
        BitMatrix matrix = new MultiFormatWriter().encode(code, BarcodeFormat.CODE_128, 300, 100);
        ServletOutputStream out = response.getOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", out);

    }
	
    public static String generateBarcodeImage(String barcodeText, String outputDir) throws WriterException, IOException {
        // Ensure barcode image filename is unique
        String filePath = outputDir + "/" + barcodeText + ".png";

        int width = 300;
        int height = 100;

        BitMatrix bitMatrix = new MultiFormatWriter()
                .encode(barcodeText, BarcodeFormat.CODE_128, width, height);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

        return filePath; // returns the saved barcode image path
    }
}
