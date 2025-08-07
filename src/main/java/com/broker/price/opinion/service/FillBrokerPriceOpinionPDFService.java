package com.broker.price.opinion.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.broker.price.opinion.dto.BrokerPriceOpinionPDFInfoDTO;
import com.broker.price.opinion.dto.ComparablePropertyInformation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@Service
public class FillBrokerPriceOpinionPDFService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    static AmazonS3 amazonS3;
    static TransferManager tx;
    private static String AWS_ACCESS_KEY = "AKIARQHW4MNAK4AS6JHX";
    private static String AWS_SECRET_KEY = "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";
    static final String bucketName = "broker-price-opinion-files";

    static {
        BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
        amazonS3 = AmazonS3Client.builder().withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
    }

    public String fillPlaltabPDF(BrokerPriceOpinionPDFInfoDTO brokerPriceOpinionPDFInfoDTO) throws IOException {

        String inputPath = "bpo_template.pdf";
        // String inputPath = "/Users/vladyslav/Desktop/Deployment/bpo_template.pdf";

        try (PDDocument document = PDDocument.load(new File(inputPath))) {

            PDPage page = document.getPage(0);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

                contentStream.setFont(PDType1Font.HELVETICA, 6);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(43, 687);
                if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getAddress() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getAddress());
                }

                contentStream.newLineAtOffset(147, 0);
                if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getCity() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getCity());
                }

                contentStream.newLineAtOffset(94, 0);
                if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getState() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getState());
                }

                contentStream.newLineAtOffset(18, 0);
                if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getZipcode() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getZipcode());
                }

                contentStream.newLineAtOffset(41, 0);
                if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getCounty() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getCounty());
                }

                contentStream.newLineAtOffset(54, 0);
                if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getParcelID() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getParcelID());
                }

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 6);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(43, 655);

                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getNumberOfUnits() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getNumberOfUnits()));
                }

                contentStream.newLineAtOffset(35, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPropertyType() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPropertyType());
                }

                contentStream.newLineAtOffset(67, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPropertyStyle() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPropertyStyle());
                }

                contentStream.newLineAtOffset(100, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA()));
                }

                contentStream.newLineAtOffset(40, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getTotalRooms() == null || brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getTotalRooms() == 0) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getTotalRooms()));
                }

                contentStream.newLineAtOffset(36, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() == null || brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() == 0) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms()));
                }

                contentStream.newLineAtOffset(36, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() == null || brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() == 0.0) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms()));
                }

                contentStream.newLineAtOffset(36, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarage() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarage());
                }

                contentStream.newLineAtOffset(63, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt()));
                }

                contentStream.newLineAtOffset(31, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getView() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getView());
                }

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 6);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(43, 637);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPool() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPool());
                }

                contentStream.showText(" / ");

                contentStream.showText("Unk.");

                contentStream.newLineAtOffset(35, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getFeaturePorch() == null) {
                    contentStream.showText("Unk.");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getFeaturePorch());
                }
                contentStream.showText(" / ");
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getFeaturePatio() == null) {
                    contentStream.showText("Unk.");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getFeaturePatio());
                }
                contentStream.showText(" / ");
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getFeatureDeck() == null) {
                    contentStream.showText("Unk.");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getFeatureDeck());
                }

                contentStream.newLineAtOffset(54, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getNumberOfFireplaces() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getNumberOfFireplaces()));
                }

                contentStream.newLineAtOffset(45, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getOverallCondition() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getOverallCondition());
                }

                contentStream.newLineAtOffset(117, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getOccupancy() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getOccupancy());
                }

                // Current Rent
                contentStream.newLineAtOffset(153, 0);
                contentStream.showText("");

                // Market Rent
                contentStream.newLineAtOffset(68, 0);
                contentStream.showText("");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 6);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(43, 619);
                contentStream.showText("");
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getIsListed() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getIsListed());
                }

                // Listed in Past 12 months
                contentStream.newLineAtOffset(35, 0);
                contentStream.showText("");

                contentStream.newLineAtOffset(59, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getListPrice() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getListPrice()));
                }

                contentStream.newLineAtOffset(40, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getNameOfListingCompany() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getNameOfListingCompany());
                }

                contentStream.newLineAtOffset(139, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getListingAgentPhone() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getListingAgentPhone());
                }

                // Transferred in Past 12 months
                contentStream.newLineAtOffset(58, 0);
                contentStream.showText("");

                contentStream.newLineAtOffset(90, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPriorSaleDate() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPriorSaleDate());
                }

                contentStream.newLineAtOffset(50, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPriorSalePrice() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPriorSalePrice()));
                }

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 6);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(43, 601);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getCurrentTax() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getCurrentTax()));
                }

                // Delinquent Tax
                contentStream.newLineAtOffset(84, 0);
                contentStream.showText("");

                // Condo or PUD
                contentStream.newLineAtOffset(81, 0);
                contentStream.showText("");

                // Fee HOA
                contentStream.newLineAtOffset(36, 0);
                contentStream.showText("");

                contentStream.newLineAtOffset(45, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getZoning() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getZoning());
                }

                contentStream.newLineAtOffset(130, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize()));
                }

                contentStream.newLineAtOffset(37, 0);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLandValue() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLandValue()));
                }

                // Conforms to Neighbourhood
                contentStream.newLineAtOffset(45, 0);
                contentStream.showText("");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 6);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(43, 515);
                contentStream.showText(brokerPriceOpinionPDFInfoDTO.getNeighborhoodInformation().getLocation());

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(43, 483);
                if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getAddress() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getAddress());
                }

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(43, 474);
                if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getCity() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getCity());
                }

                contentStream.showText(", ");

                if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getState() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getState());
                }

                contentStream.showText(", ");

                if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getZipcode() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getZipcode());
                }

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 457);
                contentStream.showText("N/A");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 448);
                contentStream.showText("N/A");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                // Price per SQFT target property
                contentStream.newLineAtOffset(104, 439);
                contentStream.showText("Price per SQFT");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                // Original List Price target property
                contentStream.newLineAtOffset(104, 430);
                contentStream.showText("Original List Price");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                // Current List Price target property
                contentStream.newLineAtOffset(104, 421);
                contentStream.showText("Current List Price");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                // Sale Date / List Date target property
                contentStream.newLineAtOffset(104, 412);
                contentStream.showText("");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                // Days on Market target property
                contentStream.newLineAtOffset(104, 403);
                contentStream.showText("Days on Market");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                // MLS ID target property
                contentStream.newLineAtOffset(104, 394);
                contentStream.showText("MLS ID");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 371);
                contentStream.showText("N/A");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 362);
                contentStream.showText("N/A");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 353);
                contentStream.showText("N/A");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 344);
                contentStream.showText(brokerPriceOpinionPDFInfoDTO.getNeighborhoodInformation().getLocation());

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 335);
                contentStream.showText("");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 326);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize()));
                }

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 317);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt()));
                }

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                // Construction
                contentStream.newLineAtOffset(104, 308);
                contentStream.showText("");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                // Condition
                contentStream.newLineAtOffset(104, 299);
                contentStream.showText("");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 290);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPropertyStyle() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPropertyStyle());
                }

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 272);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getTotalRooms() == null || brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getTotalRooms() == 0) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getTotalRooms()));
                }

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(122, 272);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() == null || brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() == 0) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms()));
                }

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(145, 272);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() == null || brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() == 0.0) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms()));
                }

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 263);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA()));
                }

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                // Basement & Finish
                contentStream.newLineAtOffset(104, 254);
                contentStream.showText("");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                // Heating & Cooling
                contentStream.newLineAtOffset(104, 245);
                contentStream.showText("");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 236);
                if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarage() == null) {
                    contentStream.showText("");
                } else {
                    contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarage());
                }

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 227);
                contentStream.showText("");

                contentStream.endText();

                contentStream.setFont(PDType1Font.HELVETICA, 5);
                contentStream.setNonStrokingColor(Color.BLACK);
                contentStream.beginText();

                contentStream.newLineAtOffset(104, 218);
                contentStream.showText("");

                contentStream.endText();

                for (int i = 0; i < brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().size(); i++) {

                    ComparablePropertyInformation comp = brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(i);

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 483);
                    if (comp.getAddress() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getAddress());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 474);
                    if (comp.getCity() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getCity());
                    }

                    contentStream.showText(", ");

                    if (comp.getState() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getState());
                    }

                    contentStream.showText(", ");

                    if (comp.getZipcode() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getZipcode());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 457);
                    if (comp.getProximity() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getProximity() + " miles");
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 448);
                    contentStream.showText("N/A");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 439);
                    if (comp.getPricePerSqFt() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText("$" + comp.getPricePerSqFt());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 430);
                    if (comp.getOriginalListingPrice() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText("$" + comp.getOriginalListingPrice() + ".00");
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 421);
                    if (comp.getCurrentListingPrice() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText("$" + comp.getCurrentListingPrice() + ".00");
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 412);
                    if (comp.getListDate() == null || comp.getListDate().isEmpty()) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText("N/A" + " / " + comp.getListDate());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 403);
                    if (comp.getDaysOnMarket() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getDaysOnMarket()));
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 394);
                    if (comp.getMlsID() == null || comp.getMlsID().isEmpty()) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getMlsID());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 371);
                    contentStream.showText("");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 362);
                    contentStream.showText("");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 353);
                    contentStream.showText("");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 344);
                    contentStream.showText(comp.getLocation());

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 335);
                    contentStream.showText("");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 326);
                    if (comp.getSiteOrLotSize() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getSiteOrLotSize()));
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 317);
                    if (comp.getYearBuilt() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getYearBuilt()));
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 308);
                    contentStream.showText("");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 299);
                    contentStream.showText("");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 290);
                    if (comp.getStyle() == null || comp.getStyle().isEmpty()) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getStyle());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 272);
                    if (comp.getTotalRooms() == null || comp.getTotalRooms() == 0) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getTotalRooms()));
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 22 + 67 * i, 272);
                    if (comp.getBedrooms() == null || comp.getBedrooms() == 0) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getBedrooms()));
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 22 + 23 + 67 * i, 272);
                    if (comp.getBathrooms() == null || comp.getBathrooms() == 0.0) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getBathrooms()));
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 263);
                    if (comp.getGrossLivingArea() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getGrossLivingArea()));
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    // Basement & Finish
                    contentStream.newLineAtOffset(168 + 67 * i, 254);
                    if (comp.getBasementAndFinish() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getBasementAndFinish() + " / " + "Unk.");
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    // Heating & Cooling
                    contentStream.newLineAtOffset(168 + 67 * i, 245);
                    if (comp.getHeating() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getHeating());
                    }

                    contentStream.showText(" / ");

                    if (comp.getCooling() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getCooling());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 236);
                    if (comp.getGarage() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getGarage());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 227);
                    contentStream.showText("");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(168 + 67 * i, 218);
                    contentStream.showText("");

                    contentStream.endText();
                }

                for (int i = 0; i < brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().size(); i++) {

                    ComparablePropertyInformation comp = brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(i);

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 483);
                    if (comp.getAddress() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getAddress());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 474);
                    if (comp.getCity() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getCity());
                    }

                    contentStream.showText(", ");

                    if (comp.getState() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getState());
                    }

                    contentStream.showText(", ");

                    if (comp.getZipcode() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getZipcode());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 457);
                    if (comp.getProximity() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getProximity() + " miles");
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 448);
                    if (comp.getSalePrice() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText("$" + comp.getSalePrice() + ".00");
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 439);
                    if (comp.getPricePerSqFt() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText("$" + comp.getPricePerSqFt());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 430);
                    if (comp.getOriginalListingPrice() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText("$" + comp.getOriginalListingPrice() + ".00");
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 421);
                    if (comp.getCurrentListingPrice() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText("$" + comp.getCurrentListingPrice() + ".00");
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 412);

                    if (comp.getSaleDate() == null || comp.getSaleDate().isEmpty()) {
                        contentStream.showText("N/A" + " / ");
                    } else {
                        contentStream.showText(comp.getSaleDate() + " / ");
                    }

                    if (comp.getListDate() == null || comp.getListDate().isEmpty()) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getListDate());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 403);
                    if (comp.getDaysOnMarket() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getDaysOnMarket()));
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 394);
                    if (comp.getMlsID() == null || comp.getMlsID().isEmpty()) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getMlsID());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 371);
                    contentStream.showText("");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 362);
                    contentStream.showText("");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 353);
                    contentStream.showText("");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 344);
                    contentStream.showText(comp.getLocation());

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 335);
                    contentStream.showText("");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 326);
                    if (comp.getSiteOrLotSize() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getSiteOrLotSize()));
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 317);
                    if (comp.getYearBuilt() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getYearBuilt()));
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 308);
                    contentStream.showText("");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 299);
                    contentStream.showText("");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 290);
                    if (comp.getStyle() == null || comp.getStyle().isEmpty()) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getStyle());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 272);
                    if (comp.getTotalRooms() == null || comp.getTotalRooms() == 0) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getTotalRooms()));
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 22 + 67 * i, 272);
                    if (comp.getBedrooms() == null || comp.getBedrooms() == 0) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getBedrooms()));
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 22 + 23 + 67 * i, 272);
                    if (comp.getBathrooms() == null || comp.getBathrooms() == 0.0) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getBathrooms()));
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 263);
                    if (comp.getGrossLivingArea() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getGrossLivingArea()));
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    // Basement & Finish
                    contentStream.newLineAtOffset(370 + 67 * i, 254);
                    if (comp.getBasementAndFinish() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getBasementAndFinish() + " / " + "Unk.");
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    // Heating & Cooling
                    contentStream.newLineAtOffset(370 + 67 * i, 245);
                    if (comp.getHeating() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getHeating());
                    }

                    contentStream.showText(" / ");

                    if (comp.getCooling() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getCooling());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 236);
                    if (comp.getGarage() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getGarage());
                    }

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 227);
                    contentStream.showText("");

                    contentStream.endText();

                    contentStream.setFont(PDType1Font.HELVETICA, 5);
                    contentStream.setNonStrokingColor(Color.BLACK);
                    contentStream.beginText();

                    contentStream.newLineAtOffset(370 + 67 * i, 218);
                    contentStream.showText("");

                    contentStream.endText();
                }

                float yTopImage = 425f;
                float yBottomImage = yTopImage - 220f - 30f;

                // Page 2

                PDPage page2 = document.getPage(1);
                fillBPOHeaderInformation(document, page2, brokerPriceOpinionPDFInfoDTO);

                // Page 3

                PDPage page3 = document.getPage(2);

                // drawImageIfExists(document, page3, "/Users/vladyslav/Desktop/Deployment/image.png", yTopImage);
                // drawImageIfExists(document, page3, "/Users/vladyslav/Desktop/Deployment/image.png", yBottomImage);

                fillBPOHeaderInformation(document, page3, brokerPriceOpinionPDFInfoDTO);

                // Page 4

                PDPage page4 = document.getPage(3);

                // drawImageIfExists(document, page4, "/Users/vladyslav/Desktop/Deployment/image.png", yTopImage);
                drawImageIfExists(document, page4, brokerPriceOpinionPDFInfoDTO.getImagesLinks().getActiveListing1(), yBottomImage);

                fillBPOHeaderInformation(document, page4, brokerPriceOpinionPDFInfoDTO);

                // Page 5

                PDPage page5 = document.getPage(4);

                drawImageIfExists(document, page5, brokerPriceOpinionPDFInfoDTO.getImagesLinks().getActiveListing2(), yTopImage);
                drawImageIfExists(document, page5, brokerPriceOpinionPDFInfoDTO.getImagesLinks().getActiveListing3(), yBottomImage);

                fillBPOHeaderInformation(document, page5, brokerPriceOpinionPDFInfoDTO);

                // Page 6

                PDPage page6 = document.getPage(5);

                drawImageIfExists(document, page6, brokerPriceOpinionPDFInfoDTO.getImagesLinks().getClosedListing1(), yTopImage);
                drawImageIfExists(document, page6, brokerPriceOpinionPDFInfoDTO.getImagesLinks().getClosedListing2(), yBottomImage);

                fillBPOHeaderInformation(document, page6, brokerPriceOpinionPDFInfoDTO);

                // Page 7

                PDPage page7 = document.getPage(6);

                drawImageIfExists(document, page7, brokerPriceOpinionPDFInfoDTO.getImagesLinks().getClosedListing3(), yTopImage);
                // drawImageIfExists(document, page7, "/Users/vladyslav/Desktop/Deployment/image.png", yBottomImage);

                fillBPOHeaderInformation(document, page7, brokerPriceOpinionPDFInfoDTO);

                // Page 8

                PDPage page8 = document.getPage(7);

                // drawImageIfExists(document, page8, "/Users/vladyslav/Desktop/Deployment/image.png", yTopImage);

                fillBPOHeaderInformation(document, page8, brokerPriceOpinionPDFInfoDTO);

                // Page 9

                PDPage page9 = document.getPage(8);

                fillBPOHeaderInformation(document, page9, brokerPriceOpinionPDFInfoDTO);

                fillProximitySection(document, page9, brokerPriceOpinionPDFInfoDTO);

                // Page 10

                PDPage page10 = document.getPage(9);

                fillBPOHeaderInformation(document, page10, brokerPriceOpinionPDFInfoDTO);

                fillProximitySection(document, page10, brokerPriceOpinionPDFInfoDTO);
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("application/pdf");
            metadata.setContentLength(byteArrayOutputStream.size());

            String outputTimestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date());
            String filename = "bpo_report_" + outputTimestamp + ".pdf";

            amazonS3.putObject(new PutObjectRequest(bucketName, filename, inputStream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));

            GeneratePresignedUrlRequest urlRequest = new GeneratePresignedUrlRequest(bucketName, filename);
            URL url = amazonS3.generatePresignedUrl(urlRequest);

            String convertedURL = url.toString();

            if (convertedURL != null) {
                convertedURL = StringUtils.substringBeforeLast(convertedURL, "?X-Amz-");
            }

            return convertedURL;
        }
    }

    private void drawImageIfExists(PDDocument document, PDPage page, String imagePath, float yPosition) throws IOException {

        if (imagePath == null || imagePath.isEmpty()) {
            return;
        }

        BufferedImage originalImage;

        try {
            if (imagePath.startsWith("http://") || imagePath.startsWith("https://")) {
                try (InputStream in = new URL(imagePath).openStream()) {
                    originalImage = ImageIO.read(in);
                    if (originalImage == null) {
                        System.err.println("Could not read image from URL: " + imagePath);
                        return;
                    }
                }
            } else {
                File imageFile = new File(imagePath);
                if (!imageFile.exists()) {
                    System.err.println("Image file not found: " + imagePath);
                    return;
                }
                originalImage = ImageIO.read(imageFile);
                if (originalImage == null) {
                    System.err.println("Could not read local image: " + imagePath);
                    return;
                }
            }

            // Convert to JPEG-compatible format (RGB, strips alpha)
            BufferedImage rgbImage = new BufferedImage(
                    originalImage.getWidth(),
                    originalImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );
            Graphics2D g = rgbImage.createGraphics();
            g.setBackground(Color.WHITE);
            g.clearRect(0, 0, rgbImage.getWidth(), rgbImage.getHeight());
            g.drawImage(originalImage, 0, 0, null);
            g.dispose();

            PDImageXObject image = LosslessFactory.createFromImage(document, rgbImage);

            float maxHeight = 220f;
            float scale = maxHeight / image.getHeight();
            float scaledWidth = image.getWidth() * scale;
            float pageWidth = page.getMediaBox().getWidth();
            float x = (pageWidth - scaledWidth) / 2;

            try (PDPageContentStream cs = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
                cs.drawImage(image, x, yPosition, scaledWidth, maxHeight);
            }

        } catch (IOException e) {
            System.err.println("Failed to process image: " + imagePath);
            e.printStackTrace();
        }
    }

    private void fillBPOHeaderInformation(
            PDDocument document, PDPage page, BrokerPriceOpinionPDFInfoDTO brokerPriceOpinionPDFInfoDTO) {

        try (PDPageContentStream contentStream3 = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

            contentStream3.setFont(PDType1Font.HELVETICA, 10);
            contentStream3.setNonStrokingColor(Color.BLACK);
            contentStream3.beginText();

            contentStream3.newLineAtOffset(60, 710);
            if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getLoanNumber() == null) {
                contentStream3.showText("");
            } else {
                contentStream3.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getLoanNumber());
            }

            contentStream3.endText();

            contentStream3.setFont(PDType1Font.HELVETICA, 10);
            contentStream3.setNonStrokingColor(Color.BLACK);
            contentStream3.beginText();

            contentStream3.newLineAtOffset(252, 710);
            if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getOrderNumber() == null) {
                contentStream3.showText("");
            } else {
                contentStream3.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getOrderNumber());
            }

            contentStream3.endText();

            contentStream3.setFont(PDType1Font.HELVETICA, 10);
            contentStream3.setNonStrokingColor(Color.BLACK);
            contentStream3.beginText();

            contentStream3.newLineAtOffset(355, 710);
            if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getClient() == null) {
                contentStream3.showText("");
            } else {
                contentStream3.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getClient());
            }

            contentStream3.endText();

            contentStream3.setFont(PDType1Font.HELVETICA, 10);
            contentStream3.setNonStrokingColor(Color.BLACK);
            contentStream3.beginText();

            contentStream3.newLineAtOffset(60, 683);
            if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getAddress() == null) {
                contentStream3.showText("");
            } else {
                contentStream3.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getAddress());
            }

            contentStream3.endText();

            contentStream3.setFont(PDType1Font.HELVETICA, 10);
            contentStream3.setNonStrokingColor(Color.BLACK);
            contentStream3.beginText();

            contentStream3.newLineAtOffset(230, 683);
            if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getCity() == null) {
                contentStream3.showText("");
            } else {
                contentStream3.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getCity());
            }

            contentStream3.endText();

            contentStream3.setFont(PDType1Font.HELVETICA, 10);
            contentStream3.setNonStrokingColor(Color.BLACK);
            contentStream3.beginText();

            contentStream3.newLineAtOffset(318, 683);
            if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getState() == null) {
                contentStream3.showText("");
            } else {
                contentStream3.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getState());
            }

            contentStream3.endText();

            contentStream3.setFont(PDType1Font.HELVETICA, 10);
            contentStream3.setNonStrokingColor(Color.BLACK);
            contentStream3.beginText();

            contentStream3.newLineAtOffset(359, 683);
            if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getZipcode() == null) {
                contentStream3.showText("");
            } else {
                contentStream3.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getZipcode().substring(0, 5));
            }

            contentStream3.endText();

            contentStream3.setFont(PDType1Font.HELVETICA, 10);
            contentStream3.setNonStrokingColor(Color.BLACK);
            contentStream3.beginText();

            contentStream3.newLineAtOffset(411, 683);
            if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getName() == null) {
                contentStream3.showText("");
            } else {
                contentStream3.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getName());
            }

            contentStream3.endText();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void fillProximitySection(PDDocument document, PDPage page, BrokerPriceOpinionPDFInfoDTO brokerPriceOpinionPDFInfoDTO) {

        try (PDPageContentStream contentStreamPage = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {

            contentStreamPage.setFont(PDType1Font.HELVETICA, 10);
            contentStreamPage.setNonStrokingColor(Color.BLACK);
            contentStreamPage.beginText();

            contentStreamPage.newLineAtOffset(110, 227);
            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getProximity() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getProximity()));
            }

            contentStreamPage.endText();

            contentStreamPage.setFont(PDType1Font.HELVETICA, 10);
            contentStreamPage.setNonStrokingColor(Color.BLACK);
            contentStreamPage.beginText();

            contentStreamPage.newLineAtOffset(110, 211);
            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getProximity() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(1).getProximity()));
            }

            contentStreamPage.endText();

            contentStreamPage.setFont(PDType1Font.HELVETICA, 10);
            contentStreamPage.setNonStrokingColor(Color.BLACK);
            contentStreamPage.beginText();

            contentStreamPage.newLineAtOffset(110, 195);
            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getProximity() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(2).getProximity()));
            }

            contentStreamPage.endText();

            contentStreamPage.setFont(PDType1Font.HELVETICA, 10);
            contentStreamPage.setNonStrokingColor(Color.BLACK);
            contentStreamPage.beginText();

            contentStreamPage.newLineAtOffset(110, 179);
            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getProximity() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(0).getProximity()));
            }

            contentStreamPage.endText();

            contentStreamPage.setFont(PDType1Font.HELVETICA, 10);
            contentStreamPage.setNonStrokingColor(Color.BLACK);
            contentStreamPage.beginText();

            contentStreamPage.newLineAtOffset(110, 164);
            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getProximity() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(1).getProximity()));
            }

            contentStreamPage.endText();

            contentStreamPage.setFont(PDType1Font.HELVETICA, 10);
            contentStreamPage.setNonStrokingColor(Color.BLACK);
            contentStreamPage.beginText();

            contentStreamPage.newLineAtOffset(110, 148);
            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getProximity() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(String.valueOf(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(2).getProximity()));
            }

            contentStreamPage.endText();

            contentStreamPage.setFont(PDType1Font.HELVETICA, 10);
            contentStreamPage.setNonStrokingColor(Color.BLACK);
            contentStreamPage.beginText();

            contentStreamPage.newLineAtOffset(235, 243);

            if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getAddress() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getAddress());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getCity() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getCity());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getState() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getState());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getOrderInformation().getZipcode() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getZipcode());
            }

            contentStreamPage.endText();

            contentStreamPage.setFont(PDType1Font.HELVETICA, 10);
            contentStreamPage.setNonStrokingColor(Color.BLACK);
            contentStreamPage.beginText();

            contentStreamPage.newLineAtOffset(235, 227);

            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getAddress() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getAddress());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getCity() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getCity());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getState() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getState());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getZipcode() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(0).getZipcode());
            }

            contentStreamPage.endText();

            contentStreamPage.setFont(PDType1Font.HELVETICA, 10);
            contentStreamPage.setNonStrokingColor(Color.BLACK);
            contentStreamPage.beginText();

            contentStreamPage.newLineAtOffset(235, 211);

            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(1).getAddress() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(1).getAddress());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(1).getCity() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(1).getCity());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(1).getState() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(1).getState());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(1).getZipcode() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(1).getZipcode());
            }

            contentStreamPage.endText();

            contentStreamPage.setFont(PDType1Font.HELVETICA, 10);
            contentStreamPage.setNonStrokingColor(Color.BLACK);
            contentStreamPage.beginText();

            contentStreamPage.newLineAtOffset(235, 195);

            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(2).getAddress() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(2).getAddress());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(2).getCity() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(2).getCity());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(2).getState() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(2).getState());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(2).getZipcode() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList().get(2).getZipcode());
            }

            contentStreamPage.endText();

            contentStreamPage.setFont(PDType1Font.HELVETICA, 10);
            contentStreamPage.setNonStrokingColor(Color.BLACK);
            contentStreamPage.beginText();

            contentStreamPage.newLineAtOffset(235, 179);

            if (brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(0).getAddress() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(0).getAddress());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(0).getCity() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(0).getCity());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(0).getState() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(0).getState());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(0).getZipcode() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(0).getZipcode());
            }

            contentStreamPage.endText();

            contentStreamPage.setFont(PDType1Font.HELVETICA, 10);
            contentStreamPage.setNonStrokingColor(Color.BLACK);
            contentStreamPage.beginText();

            contentStreamPage.newLineAtOffset(235, 164);

            if (brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(1).getAddress() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(1).getAddress());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(1).getCity() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(1).getCity());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(1).getState() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(1).getState());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(1).getZipcode() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(1).getZipcode());
            }

            contentStreamPage.endText();

            contentStreamPage.setFont(PDType1Font.HELVETICA, 10);
            contentStreamPage.setNonStrokingColor(Color.BLACK);
            contentStreamPage.beginText();

            contentStreamPage.newLineAtOffset(235, 148);

            if (brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(2).getAddress() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(2).getAddress());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(2).getCity() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(2).getCity());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(2).getState() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(2).getState());
            }

            contentStreamPage.showText(", ");

            if (brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(2).getZipcode() == null) {
                contentStreamPage.showText("");
            } else {
                contentStreamPage.showText(brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList().get(2).getZipcode());
            }

            contentStreamPage.endText();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}