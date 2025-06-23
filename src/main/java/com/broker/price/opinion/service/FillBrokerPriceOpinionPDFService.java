package com.broker.price.opinion.service;

import com.broker.price.opinion.dto.BrokerPriceOpinionPDFInfoDTO;
import com.broker.price.opinion.dto.ComparablePropertyInformation;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.File;
import java.io.IOException;

@Service
public class FillBrokerPriceOpinionPDFService {

    @Autowired
    private BrokerPriceOpinionPDFInfoService service;

    public void fillPlaltabPDF(String fullAddress) throws IOException {

        String inputPath = "PDF input";
        String outputPath = "PDF output";

        BrokerPriceOpinionPDFInfoDTO brokerPriceOpinionPDFInfoDTO = service.getBrokerPriceOpinionPDFInformation(fullAddress);

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
                contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getFeaturePorch());
                contentStream.showText(" / ");
                contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getFeaturePatio());
                contentStream.showText(" / ");
                contentStream.showText(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getFeatureDeck());

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

                for (int i = 0; i < 3; i++) {

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
                    if (comp.getSiteORLotSize() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getSiteORLotSize()));
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
                        // TODO: CHANGE
                        contentStream.showText("Detached");
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
                    if (comp.getBasement() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getBasement() + " / " + "Unk.");
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

                for (int i = 0; i < 3; i++) {

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
                    if (comp.getSiteORLotSize() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(String.valueOf(comp.getSiteORLotSize()));
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
                        // TODO: CHANGE
                        contentStream.showText("Detached");
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
                    if (comp.getBasement() == null) {
                        contentStream.showText("");
                    } else {
                        contentStream.showText(comp.getBasement() + " / " + "Unk.");
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
            }

            document.save(outputPath);
        }
    }
}