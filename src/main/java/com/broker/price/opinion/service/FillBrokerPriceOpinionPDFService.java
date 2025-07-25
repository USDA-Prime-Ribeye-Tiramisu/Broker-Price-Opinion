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
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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

//                if (document.getNumberOfPages() >= 3) {
//
//                    PDPage page3 = document.getPage(2);
//
//                    File imageFile1 = new File("/Users/vladyslav/Desktop/Deployment/image.png");
//                    PDImageXObject pdImage1 = PDImageXObject.createFromFileByExtension(imageFile1, document);
//
//                    File imageFile2 = new File("/Users/vladyslav/Desktop/Deployment/image.png");
//                    PDImageXObject pdImage2 = PDImageXObject.createFromFileByExtension(imageFile2, document);
//
//                    try (PDPageContentStream imageContentStream = new PDPageContentStream(
//                            document, page3, PDPageContentStream.AppendMode.APPEND, true, true)) {
//
//                        float maxHeight = 220f;
//
//                        float pageWidth = page3.getMediaBox().getWidth();
//
//                        int image1Width = pdImage1.getWidth();
//                        int image1Height = pdImage1.getHeight();
//                        float scale1 = maxHeight / image1Height;
//                        float scaledWidth1 = image1Width * scale1;
//                        float x1 = (pageWidth - scaledWidth1) / 2;
//                        float y1 = 425f;
//
//                        imageContentStream.drawImage(pdImage1, x1, y1, scaledWidth1, maxHeight);
//
//                        int image2Width = pdImage2.getWidth();
//                        int image2Height = pdImage2.getHeight();
//                        float scale2 = maxHeight / image2Height;
//                        float scaledWidth2 = image2Width * scale2;
//                        float x2 = (pageWidth - scaledWidth2) / 2;
//                        float y2 = y1 - maxHeight - 30f;
//
//                        imageContentStream.drawImage(pdImage2, x2, y2, scaledWidth2, maxHeight);
//                    }
//                }
//
//                if (document.getNumberOfPages() >= 4) {
//
//                    PDPage page4 = document.getPage(3);
//
//                    File imageFile3 = new File("/Users/vladyslav/Desktop/Deployment/image.png");
//                    PDImageXObject pdImage3 = PDImageXObject.createFromFileByExtension(imageFile3, document);
//
//                    File imageFile4 = new File("/Users/vladyslav/Desktop/Deployment/image.png");
//                    PDImageXObject pdImage4 = PDImageXObject.createFromFileByExtension(imageFile4, document);
//
//                    try (PDPageContentStream imageContentStream = new PDPageContentStream(
//                            document, page4, PDPageContentStream.AppendMode.APPEND, true, true)) {
//
//                        float maxHeight = 220f;
//
//                        float pageWidth = page4.getMediaBox().getWidth();
//
//                        int image3Width = pdImage3.getWidth();
//                        int image3Height = pdImage3.getHeight();
//                        float scale3 = maxHeight / image3Height;
//                        float scaledWidth3 = image3Width * scale3;
//                        float x1 = (pageWidth - scaledWidth3) / 2;
//                        float y1 = 425f;
//
//                        imageContentStream.drawImage(pdImage3, x1, y1, scaledWidth3, maxHeight);
//
//                        int image4Width = pdImage4.getWidth();
//                        int image4Height = pdImage4.getHeight();
//                        float scale4 = maxHeight / image4Height;
//                        float scaledWidth4 = image4Width * scale4;
//                        float x2 = (pageWidth - scaledWidth4) / 2;
//                        float y2 = y1 - maxHeight - 30f;
//
//                        imageContentStream.drawImage(pdImage4, x2, y2, scaledWidth4, maxHeight);
//                    }
//                }
//
//                if (document.getNumberOfPages() >= 5) {
//
//                    PDPage page5 = document.getPage(4);
//
//                    File imageFile5 = new File("/Users/vladyslav/Desktop/Deployment/image.png");
//                    PDImageXObject pdImage5 = PDImageXObject.createFromFileByExtension(imageFile5, document);
//
//                    File imageFile6 = new File("/Users/vladyslav/Desktop/Deployment/image.png");
//                    PDImageXObject pdImage6 = PDImageXObject.createFromFileByExtension(imageFile6, document);
//
//                    try (PDPageContentStream imageContentStream = new PDPageContentStream(
//                            document, page5, PDPageContentStream.AppendMode.APPEND, true, true)) {
//
//                        float maxHeight = 220f;
//
//                        float pageWidth = page5.getMediaBox().getWidth();
//
//                        int image5Width = pdImage5.getWidth();
//                        int image5Height = pdImage5.getHeight();
//                        float scale5 = maxHeight / image5Height;
//                        float scaledWidth5 = image5Width * scale5;
//                        float x1 = (pageWidth - scaledWidth5) / 2;
//                        float y1 = 425f;
//
//                        imageContentStream.drawImage(pdImage5, x1, y1, scaledWidth5, maxHeight);
//
//                        int image6Width = pdImage6.getWidth();
//                        int image6Height = pdImage6.getHeight();
//                        float scale6 = maxHeight / image6Height;
//                        float scaledWidth6 = image6Width * scale6;
//                        float x2 = (pageWidth - scaledWidth6) / 2;
//                        float y2 = y1 - maxHeight - 30f;
//
//                        imageContentStream.drawImage(pdImage6, x2, y2, scaledWidth6, maxHeight);
//                    }
//                }
//
//                if (document.getNumberOfPages() >= 6) {
//
//                    PDPage page6 = document.getPage(5);
//
//                    File imageFile7 = new File("/Users/vladyslav/Desktop/Deployment/image.png");
//                    PDImageXObject pdImage7 = PDImageXObject.createFromFileByExtension(imageFile7, document);
//
//                    File imageFile8 = new File("/Users/vladyslav/Desktop/Deployment/image.png");
//                    PDImageXObject pdImage8 = PDImageXObject.createFromFileByExtension(imageFile8, document);
//
//                    try (PDPageContentStream imageContentStream = new PDPageContentStream(
//                            document, page6, PDPageContentStream.AppendMode.APPEND, true, true)) {
//
//                        float maxHeight = 220f;
//
//                        float pageWidth = page6.getMediaBox().getWidth();
//
//                        int image7Width = pdImage7.getWidth();
//                        int image7Height = pdImage7.getHeight();
//                        float scale7 = maxHeight / image7Height;
//                        float scaledWidth7 = image7Width * scale7;
//                        float x1 = (pageWidth - scaledWidth7) / 2;
//                        float y1 = 425f;
//
//                        imageContentStream.drawImage(pdImage7, x1, y1, scaledWidth7, maxHeight);
//
//                        int image8Width = pdImage8.getWidth();
//                        int image8Height = pdImage8.getHeight();
//                        float scale8 = maxHeight / image8Height;
//                        float scaledWidth8 = image8Width * scale8;
//                        float x2 = (pageWidth - scaledWidth8) / 2;
//                        float y2 = y1 - maxHeight - 30f;
//
//                        imageContentStream.drawImage(pdImage8, x2, y2, scaledWidth8, maxHeight);
//                    }
//                }
//
//                if (document.getNumberOfPages() >= 7) {
//
//                    PDPage page7 = document.getPage(6);
//
//                    File imageFile9 = new File("/Users/vladyslav/Desktop/Deployment/image.png");
//                    PDImageXObject pdImage9 = PDImageXObject.createFromFileByExtension(imageFile9, document);
//
//                    File imageFile10 = new File("/Users/vladyslav/Desktop/Deployment/image.png");
//                    PDImageXObject pdImage10 = PDImageXObject.createFromFileByExtension(imageFile10, document);
//
//                    try (PDPageContentStream imageContentStream = new PDPageContentStream(
//                            document, page7, PDPageContentStream.AppendMode.APPEND, true, true)) {
//
//                        float maxHeight = 220f;
//
//                        float pageWidth = page7.getMediaBox().getWidth();
//
//                        int image9Width = pdImage9.getWidth();
//                        int image9Height = pdImage9.getHeight();
//                        float scale9 = maxHeight / image9Height;
//                        float scaledWidth9 = image9Width * scale9;
//                        float x1 = (pageWidth - scaledWidth9) / 2;
//                        float y1 = 425f;
//
//                        imageContentStream.drawImage(pdImage9, x1, y1, scaledWidth9, maxHeight);
//
//                        int image10Width = pdImage10.getWidth();
//                        int image10Height = pdImage10.getHeight();
//                        float scale10 = maxHeight / image10Height;
//                        float scaledWidth10 = image10Width * scale10;
//                        float x2 = (pageWidth - scaledWidth10) / 2;
//                        float y2 = y1 - maxHeight - 30f;
//
//                        imageContentStream.drawImage(pdImage10, x2, y2, scaledWidth10, maxHeight);
//                    }
//                }
//
//                if (document.getNumberOfPages() >= 8) {
//
//                    PDPage page8 = document.getPage(7);
//
//                    File imageFile11 = new File("/Users/vladyslav/Desktop/Deployment/image.png");
//                    PDImageXObject pdImage11 = PDImageXObject.createFromFileByExtension(imageFile11, document);
//
//                    try (PDPageContentStream imageContentStream = new PDPageContentStream(
//                            document, page8, PDPageContentStream.AppendMode.APPEND, true, true)) {
//
//                        float maxHeight = 220f;
//
//                        float pageWidth = page8.getMediaBox().getWidth();
//
//                        int image11Width = pdImage11.getWidth();
//                        int image11Height = pdImage11.getHeight();
//                        float scale11 = maxHeight / image11Height;
//                        float scaledWidth11 = image11Width * scale11;
//                        float x1 = (pageWidth - scaledWidth11) / 2;
//                        float y1 = 425f;
//
//                        imageContentStream.drawImage(pdImage11, x1, y1, scaledWidth11, maxHeight);
//                    }
//                }
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
}