package com.codebloom.cineman.service.impl;

import com.codebloom.cineman.exception.DataNotFoundException;
import com.codebloom.cineman.model.InvoiceEntity;
import com.codebloom.cineman.repository.InvoiceRepository;
import com.codebloom.cineman.service.QRCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "QR-CODE-SERVICE")
public class QRCodeServiceImpl implements QRCodeService {

    private final InvoiceRepository invoiceRepository;

    @Override
    public BufferedImage generateQRCode(String ticketCode) throws WriterException, IOException {
        log.info("Generating QR code for ticket code: {}", ticketCode);
        BitMatrix matrix = new MultiFormatWriter().encode(ticketCode, BarcodeFormat.QR_CODE, 500, 500);
        log.info("QR code generated");
        return MatrixToImageWriter.toBufferedImage(matrix);
    }
}
