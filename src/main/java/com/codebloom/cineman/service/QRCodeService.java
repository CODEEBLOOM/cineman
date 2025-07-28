package com.codebloom.cineman.service;

import com.google.zxing.WriterException;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface QRCodeService {

    BufferedImage generateQRCode(String ticketCode) throws WriterException, IOException;

}
