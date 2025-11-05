package com.products.products.services;


import com.google.zxing.*;

import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

@Service
public class BarcodeDecoderService {

    public String decodeBarcode(InputStream barcodeImageStream) {
        try {
            BufferedImage bufferedImage = ImageIO.read(barcodeImageStream);
            LuminanceSource source = new BufferedImageLuminanceSource(bufferedImage);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            Result result = new MultiFormatReader().decode(bitmap);
            return result.getText(); // barcode value (e.g., PRD-1694875623000)

        } catch (Exception e) {
            throw new RuntimeException("Failed to decode barcode: " + e.getMessage(), e);
        }
    }
}

