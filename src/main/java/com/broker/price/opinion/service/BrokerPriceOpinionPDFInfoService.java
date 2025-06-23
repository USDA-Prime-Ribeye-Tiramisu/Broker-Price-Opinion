package com.broker.price.opinion.service;

import com.broker.price.opinion.dto.*;
import com.broker.price.opinion.dto.DTAPI.PropertyDetailReportData;
import com.broker.price.opinion.dto.DTAPI.response.PropertyDetailReportResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.ResultSetMetaData;
import java.util.*;

@Slf4j
@Service
public class BrokerPriceOpinionPDFInfoService {

    @Value("${dt.api.client.id}")
    private String DTAPIClientId;

    @Value("${dt.api.client.secret.key}")
    private String DTAPIClientSecretKey;

    private final JdbcTemplate prodBackupJdbcTemplate;

    @Autowired
    public BrokerPriceOpinionPDFInfoService(
            @Qualifier("prodBackupJdbcTemplate") JdbcTemplate prodBackupJdbcTemplate) {
        this.prodBackupJdbcTemplate = prodBackupJdbcTemplate;
    }

    public BrokerPriceOpinionPDFInfoDTO getBrokerPriceOpinionPDFInformation(String fullAddress) {

        BrokerPriceOpinionPDFInfoDTO brokerPriceOpinionPDFInfoDTO = new BrokerPriceOpinionPDFInfoDTO();

        brokerPriceOpinionPDFInfoDTO.setFullAddress(fullAddress);

        PropertyDetailReportResponse propertyDetailReportResponse = getPropertyDetailReportDTAPI(fullAddress);

        String queryTPInfoPlatlab = "select " +
                "plfhf.address, " +
                "plfhf.city, " +
                "plfhf.state, " +
                "plfhf.zip, " +
                "plfhf.county, " +
                "plfhf.parcel_number, " +
                "replace(plfhf.style, ',', ', ') as style, " +
                "plfhf.square_feet::int, " +
                "plfhf.bedrooms::int, " +
                "plfhf.bathrooms_full::int, " +
                "plfhf.bathrooms_half::int, " +
                "plfhf.garage_spaces::int, " +
                "plfhf.year_built::int, " +
                "plfhf.has_view, " +
                "plfhf.has_pool, " +
                "plfhf.fireplaces::int, " +
                "replace(plfhf.occupant_type, ',', ', ') as occupant_type, " +
                "plfhf.status, " +
                "plfhf.price::int, " +
                "plfhf.listing_office_name, " +
                "plfhf.listing_agent_phone, " +
                "plfhf.annual_tax::float, " +
                "plfhf.zoning, " +
                "case " +
                "   when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Acres' then plfhf.lot_size_area::float8 " +
                "   when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Square Feet' then plfhf.lot_size_area::float8 / 43560 " +
                "   when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Acres' then plfhf.lot_size_area::float8 " +
                "   when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Square Feet' then plfhf.lot_size_area::float8 / 43560 " +
                "   when plfhf.lot_size_square_feet is not null and plfhf.lot_size_square_feet::numeric != 0 then plfhf.lot_size_square_feet::float8 / 43560 " +
                "   when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Acres' then plfhf.lot_size_area::float8 " +
                "   when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Square Feet' then plfhf.lot_size_area::float8 / 43560 " +
                "   else null " +
                "end as lot_size " +
                "from platlab_listings_full_history_filtered plfhf " +
                "where plfhf.display_mls_number = 'display_mls_number'";

        List<Map<String, Object>> queryResult = prodBackupJdbcTemplate.query(queryTPInfoPlatlab, rs -> {
            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                rows.add(row);
            }

            return rows;
        });

        PropertyDetailReportData propertyDetailReportData = propertyDetailReportResponse.Reports.get(0).Data;

        brokerPriceOpinionPDFInfoDTO.setLatitude(propertyDetailReportData.LocationInformation.Latitude);
        brokerPriceOpinionPDFInfoDTO.setLongitude(propertyDetailReportData.LocationInformation.Longitude);

        OrderInformation orderInformation = new OrderInformation();

        String addressDTAPISource = propertyDetailReportData.SubjectProperty.SitusAddress.StreetAddress;
        String addressPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (String) queryResult.get(0).get("address")
                : null;

        if (addressDTAPISource != null && !addressDTAPISource.isEmpty()) {
            orderInformation.setAddress(addressDTAPISource);
        } else if (addressPlatlabSource != null && !addressPlatlabSource.isEmpty()) {
            orderInformation.setAddress(addressPlatlabSource);
        } else {
            orderInformation.setAddress(null);
        }

        String cityDTAPISource = propertyDetailReportData.SubjectProperty.SitusAddress.City;
        String cityPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (String) queryResult.get(0).get("city")
                : null;

        if (cityDTAPISource != null && !cityDTAPISource.isEmpty()) {
            orderInformation.setCity(cityDTAPISource);
        } else if (cityPlatlabSource != null && !cityPlatlabSource.isEmpty()) {
            orderInformation.setCity(cityPlatlabSource);
        } else {
            orderInformation.setCity(null);
        }

        String stateDTAPISource = propertyDetailReportData.SubjectProperty.SitusAddress.State;
        String statePlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (String) queryResult.get(0).get("state")
                : null;

        if (stateDTAPISource != null && !stateDTAPISource.isEmpty()) {
            orderInformation.setState(stateDTAPISource);
        } else if (statePlatlabSource != null && !statePlatlabSource.isEmpty()) {
            orderInformation.setState(statePlatlabSource);
        } else {
            orderInformation.setState(null);
        }

        String zipcodeDTAPISource = propertyDetailReportData.SubjectProperty.SitusAddress.Zip9;
        String zipcodePlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (String) queryResult.get(0).get("zip")
                : null;

        if (zipcodeDTAPISource != null && !zipcodeDTAPISource.isEmpty()) {
            orderInformation.setZipcode(zipcodeDTAPISource);
        } else if (zipcodePlatlabSource != null && !zipcodePlatlabSource.isEmpty()) {
            orderInformation.setZipcode(zipcodePlatlabSource);
        } else {
            orderInformation.setZipcode(null);
        }

        String countyDTAPISource = propertyDetailReportData.SubjectProperty.SitusAddress.County;
        String countyPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (String) queryResult.get(0).get("county")
                : null;

        if (countyDTAPISource != null && !countyDTAPISource.isEmpty()) {
            orderInformation.setCounty(countyDTAPISource);
        } else if (countyPlatlabSource != null && !countyPlatlabSource.isEmpty()) {
            orderInformation.setCounty(countyPlatlabSource);
        } else {
            orderInformation.setCounty(null);
        }

        String parcelIDPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (String) queryResult.get(0).get("parcel_number")
                : null;

        if (parcelIDPlatlabSource != null && !parcelIDPlatlabSource.isEmpty()) {
            orderInformation.setParcelID(parcelIDPlatlabSource);
        } else {
            orderInformation.setParcelID(null);
        }

        brokerPriceOpinionPDFInfoDTO.setOrderInformation(orderInformation);

        PropertyInformation propertyInformation = new PropertyInformation();

        propertyInformation.setNumberOfUnits(propertyDetailReportData.SiteInformation.UnitsResidential);

        propertyInformation.setPropertyType(propertyDetailReportData.SiteInformation.LandUse);

        String styleDTAPISource = propertyDetailReportData.PropertyCharacteristics.Style;
        String stylePlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (String) queryResult.get(0).get("style")
                : null;

        if (styleDTAPISource != null && !styleDTAPISource.isEmpty()) {
            propertyInformation.setPropertyStyle(styleDTAPISource);
        } else if (stylePlatlabSource != null && !stylePlatlabSource.isEmpty()) {
            propertyInformation.setPropertyStyle(stylePlatlabSource);
        } else {
            propertyInformation.setPropertyStyle(null);
        }

        Integer sqftDTAPISource = propertyDetailReportData.PropertyCharacteristics.LivingArea;
        Integer sqftPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (Integer) queryResult.get(0).get("square_feet")
                : null;

        if (sqftDTAPISource != null) {
            propertyInformation.setSqftGLA(sqftDTAPISource);
        } else if (sqftPlatlabSource != null) {
            propertyInformation.setSqftGLA(sqftPlatlabSource);
        } else {
            propertyInformation.setSqftGLA(null);
        }

        propertyInformation.setTotalRooms(propertyDetailReportData.PropertyCharacteristics.TotalRooms);

        Integer bedroomsDTAPISource = propertyDetailReportData.PropertyCharacteristics.Bedrooms;
        Integer bedroomsPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (Integer) queryResult.get(0).get("bedrooms")
                : null;

        if (bedroomsDTAPISource != null) {
            propertyInformation.setBedrooms(bedroomsDTAPISource);
        } else if (bedroomsPlatlabSource != null) {
            propertyInformation.setBedrooms(bedroomsPlatlabSource);
        } else {
            propertyInformation.setBedrooms(null);
        }

        double bathroomsDTAPISource;

        int bathroomsFullDTAPISource = (propertyDetailReportData.PropertyCharacteristics.FullBath != null) ? propertyDetailReportData.PropertyCharacteristics.FullBath : 0;
        int bathroomsHalfDTAPISource = (propertyDetailReportData.PropertyCharacteristics.HalfBath != null) ? propertyDetailReportData.PropertyCharacteristics.HalfBath : 0;

        bathroomsDTAPISource = bathroomsFullDTAPISource + bathroomsHalfDTAPISource / 2.0;

        double bathroomsPlatlabSource;

        int bathroomsFullPlatlabSource = 0;
        int bathroomsHalfPlatlabSource = 0;

        if (queryResult != null && !queryResult.isEmpty()) {
            bathroomsFullPlatlabSource = (queryResult.get(0).get("bathrooms_full") != null) ? (int) queryResult.get(0).get("bathrooms_full") : 0;
            bathroomsHalfPlatlabSource = (queryResult.get(0).get("bathrooms_half") != null) ? (int) queryResult.get(0).get("bathrooms_half") : 0;
        }

        bathroomsPlatlabSource = bathroomsFullPlatlabSource + bathroomsHalfPlatlabSource / 2.0;

        if (bathroomsDTAPISource != 0) {
            propertyInformation.setBathrooms(bathroomsDTAPISource);
        } else if (bathroomsPlatlabSource != 0) {
            propertyInformation.setBathrooms(bathroomsPlatlabSource);
        } else {
            propertyInformation.setBathrooms(null);
        }

        Integer garageSpacesDTAPISource = propertyDetailReportData.PropertyCharacteristics.GarageCapacity;
        Integer garageSpacesPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (Integer) queryResult.get(0).get("garage_spaces")
                : null;

        if (garageSpacesDTAPISource != null) {
            if (garageSpacesDTAPISource == 1) {
                propertyInformation.setGarageSpaces(garageSpacesDTAPISource);
                propertyInformation.setGarage("Garage - " + garageSpacesDTAPISource + " car");
            } else if (garageSpacesDTAPISource > 1) {
                propertyInformation.setGarageSpaces(garageSpacesDTAPISource);
                propertyInformation.setGarage("Garage - " + garageSpacesDTAPISource + " cars");
            } else {
                propertyInformation.setGarageSpaces(0);
                propertyInformation.setGarage("No Garage");
            }
        } else if (garageSpacesPlatlabSource != null) {
            if (garageSpacesPlatlabSource == 1) {
                propertyInformation.setGarageSpaces(garageSpacesPlatlabSource);
                propertyInformation.setGarage("Garage - " + garageSpacesPlatlabSource + " car");
            } else if (garageSpacesPlatlabSource > 1) {
                propertyInformation.setGarageSpaces(garageSpacesPlatlabSource);
                propertyInformation.setGarage("Garage - " + garageSpacesPlatlabSource + " cars");
            } else {
                propertyInformation.setGarage("No Garage");
            }
        } else {
            propertyInformation.setGarageSpaces(0);
            propertyInformation.setGarage(null);
        }

        Integer yearBuiltDTAPISource = propertyDetailReportData.PropertyCharacteristics.YearBuilt;
        Integer yearBuiltPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (Integer) queryResult.get(0).get("year_built")
                : null;

        if (yearBuiltDTAPISource != null) {
            propertyInformation.setYearBuilt(yearBuiltDTAPISource);
        } else if (yearBuiltPlatlabSource != null) {
            propertyInformation.setYearBuilt(yearBuiltPlatlabSource);
        } else {
            propertyInformation.setYearBuilt(null);
        }

        propertyInformation.setView(queryResult.get(0).get("has_pool").equals("true") ? "Yes" : "No");

        String poolDTAPISource = propertyDetailReportData.PropertyCharacteristics.Pool;
        String poolPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? queryResult.get(0).get("has_pool").equals("true") ? "Yes" : "No"
                : null;

        if (poolDTAPISource != null) {
            propertyInformation.setPool(poolDTAPISource);
        } else if (poolPlatlabSource != null) {
            propertyInformation.setPool(poolPlatlabSource);
        } else {
            propertyInformation.setPool(null);
        }

        propertyInformation.setFeaturePorch(
                (propertyDetailReportData.PropertyCharacteristics.PorchType.toLowerCase(Locale.ROOT).equals("porch")
                        || propertyDetailReportData.PropertyCharacteristics.PatioType.toLowerCase(Locale.ROOT).equals("porch")) ? "Yes" : "No");

        propertyInformation.setFeaturePatio(
                (propertyDetailReportData.PropertyCharacteristics.PorchType.toLowerCase(Locale.ROOT).equals("patio")
                        || propertyDetailReportData.PropertyCharacteristics.PatioType.toLowerCase(Locale.ROOT).equals("patio")) ? "Yes" : "No");

        propertyInformation.setFeatureDeck(
                (propertyDetailReportData.PropertyCharacteristics.PorchType.toLowerCase(Locale.ROOT).equals("deck")
                        || propertyDetailReportData.PropertyCharacteristics.PatioType.toLowerCase(Locale.ROOT).equals("deck")) ? "Yes" : "No");

        propertyInformation.setFeaturePorch("Unk.");
        propertyInformation.setFeaturePatio("Unk.");
        propertyInformation.setFeatureDeck("Unk.");

        String porchTypeDTAPI = propertyDetailReportData.PropertyCharacteristics.PorchType;
        String patioTypeDTAPI = propertyDetailReportData.PropertyCharacteristics.PatioType;

        if (porchTypeDTAPI.equalsIgnoreCase("porch") || patioTypeDTAPI.equalsIgnoreCase("porch")) {
            propertyInformation.setFeaturePorch("Yes");
        }

        if (porchTypeDTAPI.equalsIgnoreCase("patio") || patioTypeDTAPI.equalsIgnoreCase("patio")) {
            propertyInformation.setFeaturePatio("Yes");
        }

        if (porchTypeDTAPI.equalsIgnoreCase("deck") || patioTypeDTAPI.equalsIgnoreCase("deck")) {
            propertyInformation.setFeatureDeck("Yes");
        }

        Integer numberOfFireplacesDTAPISource = propertyDetailReportData.PropertyCharacteristics.FirePlaceCount;
        Integer numberOfFireplacesPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (Integer) queryResult.get(0).get("fireplaces")
                : null;

        if (numberOfFireplacesDTAPISource != null) {
            propertyInformation.setNumberOfFireplaces(numberOfFireplacesDTAPISource);
        } else if (numberOfFireplacesPlatlabSource != null) {
            propertyInformation.setNumberOfFireplaces(numberOfFireplacesPlatlabSource);
        } else {
            propertyInformation.setNumberOfFireplaces(null);
        }

        propertyInformation.setOverallCondition(propertyDetailReportData.PropertyCharacteristics.Condition);

        String occupancyDTAPISource = propertyDetailReportData.OwnerInformation.Occupancy;
        String occupancyPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (String) queryResult.get(0).get("occupant_type")
                : null;

        if (occupancyPlatlabSource != null && !occupancyPlatlabSource.isEmpty()) {
            propertyInformation.setOccupancy(occupancyPlatlabSource);
        } else if (occupancyDTAPISource != null && !occupancyDTAPISource.isEmpty()) {
            propertyInformation.setOccupancy(occupancyDTAPISource);
        } else {
            propertyInformation.setOccupancy(null);
        }

        String isListedPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? queryResult.get(0).get("status").equals("active") ? "Yes" : "No"
                : null;

        propertyInformation.setIsListed(isListedPlatlabSource);

        Integer listPricePlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (Integer) queryResult.get(0).get("price")
                : null;

        if (listPricePlatlabSource != null) {
            propertyInformation.setListPrice(listPricePlatlabSource);
        } else {
            propertyInformation.setLotSize(null);
        }

        String nameOfListingCompanyPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (String) queryResult.get(0).get("listing_office_name")
                : null;

        if (nameOfListingCompanyPlatlabSource != null && !nameOfListingCompanyPlatlabSource.isEmpty()) {
            propertyInformation.setNameOfListingCompany(nameOfListingCompanyPlatlabSource);
        } else {
            propertyInformation.setNameOfListingCompany(null);
        }

        String listingAgentPhonePlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (String) queryResult.get(0).get("listing_agent_phone")
                : null;

        if (listingAgentPhonePlatlabSource != null && !listingAgentPhonePlatlabSource.isEmpty()) {
            propertyInformation.setListingAgentPhone(listingAgentPhonePlatlabSource);
        } else {
            propertyInformation.setListingAgentPhone(null);
        }

        String priorSaleDateDTAPI = propertyDetailReportData.PriorSaleInformation.PriorSaleDate;

        if (priorSaleDateDTAPI != null && priorSaleDateDTAPI.length() >= 10) {
            propertyInformation.setPriorSaleDate(priorSaleDateDTAPI.substring(0, 10));
        } else {
            propertyInformation.setPriorSaleDate(null);
        }

        propertyInformation.setPriorSalePrice(propertyDetailReportData.PriorSaleInformation.PriorSalePrice);

        Double currentTaxDTAPISource = propertyDetailReportData.TaxInformation.PropertyTax;
        Double currentTaxPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (Double) queryResult.get(0).get("annual_tax")
                : null;

        if (currentTaxPlatlabSource != null) {
            propertyInformation.setCurrentTax(currentTaxPlatlabSource);
        } else if (currentTaxDTAPISource != null) {
            propertyInformation.setCurrentTax(currentTaxDTAPISource);
        } else {
            propertyInformation.setCurrentTax(null);
        }

        String zoningDTAPISource = propertyDetailReportData.SiteInformation.Zoning;
        String zoningPlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (String) queryResult.get(0).get("zoning")
                : null;

        if (zoningDTAPISource != null && !zoningDTAPISource.isEmpty()) {
            propertyInformation.setZoning(zoningDTAPISource);
        } else if (zoningPlatlabSource != null && !zoningPlatlabSource.isEmpty()) {
            propertyInformation.setZoning(zoningPlatlabSource);
        } else {
            propertyInformation.setZoning(null);
        }

        Double lotSizeDTAPISource = propertyDetailReportData.SiteInformation.Acres;
        Double lotSizePlatlabSource = queryResult != null && !queryResult.isEmpty()
                ? (Double) queryResult.get(0).get("lot_size")
                : null;

        if (lotSizeDTAPISource != null) {
            propertyInformation.setLotSize(lotSizeDTAPISource);
        } else if (lotSizePlatlabSource != null) {
            propertyInformation.setLotSize(lotSizePlatlabSource);
        } else {
            propertyInformation.setLotSize(null);
        }

        propertyInformation.setLandValue(propertyDetailReportData.TaxInformation.LandValue);

        brokerPriceOpinionPDFInfoDTO.setPropertyInformation(propertyInformation);

        NeighborhoodInformation neighborhoodInformation = new NeighborhoodInformation();

        neighborhoodInformation.setLocation(
                findLocationDensity(
                        brokerPriceOpinionPDFInfoDTO.getLongitude(),
                        brokerPriceOpinionPDFInfoDTO.getLatitude()
                )
        );

        brokerPriceOpinionPDFInfoDTO.setNeighborhoodInformation(neighborhoodInformation);

        PropertyDetailReportResponse propertyDetailReportResponseComparableProperty;

        List<Map<String, Object>> resultCompsClosed;

        String queryCompsClosedPass1 = "select " +
                "plfhf.address, " +
                "plfhf.city, " +
                "plfhf.state, " +
                "plfhf.zip, " +
                "plfhf.county, " +
                "ROUND((ST_Distance(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geography, ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography) / 1609.34)::numeric, 2)::float as proximity, " +
                "plfhf.sold_price::int as sold_price, " +
                "plfhf.original_listing_price::int as original_listing_price, " +
                "plfhf.price::int as price, " +
                "left(plfhf.sold_date, 10)::varchar as sold_date, " +
                "left(plfhf.mls_list_date, 10)::varchar as mls_list_date, " +
                "(COALESCE(CAST(left(plfhf.sold_date, 10) AS date), CURRENT_DATE) - CAST(left(plfhf.mls_list_date, 10) AS date)) as days_on_market, " +
                "plfhf.display_mls_number, " +
                "plfhf.longitude, " +
                "plfhf.latitude, " +
                "case" +
                "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                "        then round(plfhf.lot_size_area::numeric, 2)" +
                "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                "        then round(plfhf.lot_size_area::numeric, 2)" +
                "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                "    when plfhf.lot_size_square_feet is not null and plfhf.lot_size_square_feet::numeric != 0" +
                "        then round(plfhf.lot_size_square_feet::numeric / 43560, 2)" +
                "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                "        then round(plfhf.lot_size_area::numeric, 2)" +
                "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                "    else null " +
                "end as lot_size, " +
                "plfhf.year_built::int, " +
                "replace(plfhf.style, ',', ', ') as style, " +
                "plfhf.bedrooms::int, " +
                "plfhf.bathrooms_full::int, " +
                "plfhf.bathrooms_half::int, " +
                "plfhf.square_feet::int, " +
                "plfhf.has_basement::bool, " +
                "plfhf.garage_spaces::int " +
                "from platlab_listings_full_history_filtered plfhf " +
                "where ST_Within(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geometry, ST_Buffer(ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography, 1609.34 * 0.5)::geometry) " +
                "and plfhf.status = 'sold' " +
                "and plfhf.bedrooms::integer = " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() + " " +
                "and plfhf.bathrooms::integer = " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() + " " +
                "and plfhf.square_feet::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 0.95 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 1.05 " +
                "and plfhf.lot_size::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 0.9 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 1.1 " +
                "and plfhf.garage_spaces::integer = " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() + " " +
                "and plfhf.year_built::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " - 10 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " + 10 " +
                "and plfhf.display_mls_number != 'display_mls_number' " +
                "and plfhf.mls_list_date >= 'date' " +
                "and plfhf.mls_property_type = 'Single Family Home' " +
                "order by proximity asc";

        resultCompsClosed = prodBackupJdbcTemplate.query(queryCompsClosedPass1, rs -> {
            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                rows.add(row);
            }

            return rows;
        });

        if (resultCompsClosed.size() < 3) {

            String queryCompsClosedPass2 = "select " +
                    "plfhf.address, " +
                    "plfhf.city, " +
                    "plfhf.state, " +
                    "plfhf.zip, " +
                    "plfhf.county, " +
                    "ROUND((ST_Distance(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geography, ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography) / 1609.34)::numeric, 2)::float as proximity, " +
                    "plfhf.sold_price::int as sold_price, " +
                    "plfhf.original_listing_price::int as original_listing_price, " +
                    "plfhf.price::int as price, " +
                    "left(plfhf.sold_date, 10)::varchar as sold_date, " +
                    "left(plfhf.mls_list_date, 10)::varchar as mls_list_date, " +
                    "(COALESCE(CAST(left(plfhf.sold_date, 10) AS date), CURRENT_DATE) - CAST(left(plfhf.mls_list_date, 10) AS date)) as days_on_market, " +
                    "plfhf.display_mls_number, " +
                    "plfhf.longitude, " +
                    "plfhf.latitude, " +
                    "case" +
                    "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    when plfhf.lot_size_square_feet is not null and plfhf.lot_size_square_feet::numeric != 0" +
                    "        then round(plfhf.lot_size_square_feet::numeric / 43560, 2)" +
                    "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    else null " +
                    "end as lot_size, " +
                    "plfhf.year_built::int, " +
                    "replace(plfhf.style, ',', ', ') as style, " +
                    "plfhf.bedrooms::int, " +
                    "plfhf.bathrooms_full::int, " +
                    "plfhf.bathrooms_half::int, " +
                    "plfhf.square_feet::int, " +
                    "plfhf.has_basement::bool, " +
                    "plfhf.garage_spaces::int " +
                    "from platlab_listings_full_history_filtered plfhf " +
                    "where ST_Within(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geometry, ST_Buffer(ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography, 1609.34 * 1.5)::geometry) " +
                    "and plfhf.status = 'sold' " +
                    "and plfhf.bedrooms::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() + " - 1 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() + " + 1 " +
                    "and plfhf.bathrooms::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() + " - 1 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() + " + 1 " +
                    "and plfhf.square_feet::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 0.9 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 1.1 " +
                    "and plfhf.lot_size::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 0.8 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 1.2 " +
                    "and plfhf.garage_spaces::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() + " - 1 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() + " + 1 " +
                    "and plfhf.year_built::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " - 20 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " + 20 " +
                    "and plfhf.display_mls_number != 'display_mls_number' " +
                    "and plfhf.mls_list_date >= 'date' " +
                    "and plfhf.mls_property_type = 'Single Family Home' " +
                    "order by proximity asc";

            resultCompsClosed = prodBackupJdbcTemplate.query(queryCompsClosedPass2, rs -> {
                List<Map<String, Object>> rows = new ArrayList<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    rows.add(row);
                }

                return rows;
            });
        }

        if (resultCompsClosed.size() < 3) {

            String queryCompsClosedPass3 = "select " +
                    "plfhf.address, " +
                    "plfhf.city, " +
                    "plfhf.state, " +
                    "plfhf.zip, " +
                    "plfhf.county, " +
                    "ROUND((ST_Distance(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geography, ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography) / 1609.34)::numeric, 2)::float as proximity, " +
                    "plfhf.sold_price::int as sold_price, " +
                    "plfhf.original_listing_price::int as original_listing_price, " +
                    "plfhf.price::int as price, " +
                    "left(plfhf.sold_date, 10)::varchar as sold_date, " +
                    "left(plfhf.mls_list_date, 10)::varchar as mls_list_date, " +
                    "(COALESCE(CAST(left(plfhf.sold_date, 10) AS date), CURRENT_DATE) - CAST(left(plfhf.mls_list_date, 10) AS date)) as days_on_market, " +
                    "plfhf.display_mls_number, " +
                    "plfhf.longitude, " +
                    "plfhf.latitude, " +
                    "case" +
                    "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    when plfhf.lot_size_square_feet is not null and plfhf.lot_size_square_feet::numeric != 0" +
                    "        then round(plfhf.lot_size_square_feet::numeric / 43560, 2)" +
                    "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    else null " +
                    "end as lot_size, " +
                    "plfhf.year_built::int, " +
                    "replace(plfhf.style, ',', ', ') as style, " +
                    "plfhf.bedrooms::int, " +
                    "plfhf.bathrooms_full::int, " +
                    "plfhf.bathrooms_half::int, " +
                    "plfhf.square_feet::int, " +
                    "plfhf.has_basement::bool, " +
                    "plfhf.garage_spaces::int " +
                    "from platlab_listings_full_history_filtered plfhf " +
                    "where ST_Within(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geometry, ST_Buffer(ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography, 1609.34 * 5)::geometry) " +
                    "and plfhf.status = 'sold' " +
                    "and plfhf.bedrooms::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() + " - 2 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() + " + 2 " +
                    "and plfhf.bathrooms::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() + " - 2 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() + " + 2 " +
                    "and plfhf.square_feet::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 0.8 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 1.2 " +
                    "and plfhf.lot_size::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 0.7 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 1.3 " +
                    "and plfhf.garage_spaces::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() + " - 2 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() + " + 2 " +
                    "and plfhf.year_built::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " - 20 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " + 20 " +
                    "and plfhf.display_mls_number != 'display_mls_number' " +
                    "and plfhf.mls_list_date >= 'date' " +
                    "and plfhf.mls_property_type = 'Single Family Home' " +
                    "order by proximity asc";

            resultCompsClosed = prodBackupJdbcTemplate.query(queryCompsClosedPass3, rs -> {
                List<Map<String, Object>> rows = new ArrayList<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    rows.add(row);
                }

                return rows;
            });
        }

        if (resultCompsClosed.size() < 3) {

            String queryCompsClosedPass4 = "select " +
                    "plfhf.address, " +
                    "plfhf.city, " +
                    "plfhf.state, " +
                    "plfhf.zip, " +
                    "plfhf.county, " +
                    "ROUND((ST_Distance(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geography, ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography) / 1609.34)::numeric, 2)::float as proximity, " +
                    "plfhf.sold_price::int as sold_price, " +
                    "plfhf.original_listing_price::int as original_listing_price, " +
                    "plfhf.price::int as price, " +
                    "left(plfhf.sold_date, 10)::varchar as sold_date, " +
                    "left(plfhf.mls_list_date, 10)::varchar as mls_list_date, " +
                    "(COALESCE(CAST(left(plfhf.sold_date, 10) AS date), CURRENT_DATE) - CAST(left(plfhf.mls_list_date, 10) AS date)) as days_on_market, " +
                    "plfhf.display_mls_number, " +
                    "plfhf.longitude, " +
                    "plfhf.latitude, " +
                    "case" +
                    "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    when plfhf.lot_size_square_feet is not null and plfhf.lot_size_square_feet::numeric != 0" +
                    "        then round(plfhf.lot_size_square_feet::numeric / 43560, 2)" +
                    "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    else null " +
                    "end as lot_size, " +
                    "plfhf.year_built::int, " +
                    "replace(plfhf.style, ',', ', ') as style, " +
                    "plfhf.bedrooms::int, " +
                    "plfhf.bathrooms_full::int, " +
                    "plfhf.bathrooms_half::int, " +
                    "plfhf.square_feet::int, " +
                    "plfhf.has_basement::bool, " +
                    "plfhf.garage_spaces::int " +
                    "from platlab_listings_full_history_filtered plfhf " +
                    "where ST_Within(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geometry, ST_Buffer(ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography, 1609.34 * 20)::geometry) " +
                    "and plfhf.status = 'sold' " +
                    "and plfhf.bedrooms::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() + " - 3 AND " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() + " + 3 " +
                    "and plfhf.bathrooms::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() + " - 3 AND " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() + " + 3 " +
                    "and plfhf.square_feet::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 0.7 AND " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 1.3 " +
                    "and plfhf.lot_size::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 0.7 AND " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 1.3 " +
                    "and plfhf.garage_spaces::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() + " - 2 AND " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() + " + 2 " +
                    "and plfhf.year_built::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " - 30 AND " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " + 30 " +
                    "and plfhf.display_mls_number != 'display_mls_number' " +
                    "and plfhf.mls_list_date >= 'date' " +
                    "and plfhf.mls_property_type = 'Single Family Home' " +
                    "order by proximity asc";

            resultCompsClosed = prodBackupJdbcTemplate.query(queryCompsClosedPass4, rs -> {
                List<Map<String, Object>> rows = new ArrayList<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    rows.add(row);
                }

                return rows;
            });
        }

        List<ComparablePropertyInformation> closedComparablePropertyInformationList = new ArrayList<>();

        for (Map<String, Object> compClosed : resultCompsClosed) {

            if (closedComparablePropertyInformationList.size() >= 3) {
                break;
            }

            propertyDetailReportResponseComparableProperty = getPropertyDetailReportDTAPI(compClosed.get("address") + ", " + compClosed.get("city") + ", " + compClosed.get("state") + " " + compClosed.get("zip") + ", United States");

            PropertyDetailReportData propertyDetailReportDataCP = propertyDetailReportResponseComparableProperty.Reports.get(0).Data;

            ComparablePropertyInformation comp = new ComparablePropertyInformation();

            String addressCPClosedDTAPISource = propertyDetailReportDataCP.SubjectProperty.SitusAddress.StreetAddress;
            String addressCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (String) compClosed.get("address")
                    : null;

            if (addressCPClosedDTAPISource != null && !addressCPClosedDTAPISource.isEmpty()) {
                comp.setAddress(addressCPClosedDTAPISource);
            } else if (addressCPClosedPlatlabSource != null && !addressCPClosedPlatlabSource.isEmpty()) {
                comp.setAddress(addressCPClosedPlatlabSource);
            } else {
                comp.setAddress(null);
            }

            String cityCPClosedDTAPISource = propertyDetailReportDataCP.SubjectProperty.SitusAddress.City;
            String cityCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (String) compClosed.get("city")
                    : null;

            if (cityCPClosedDTAPISource != null && !cityCPClosedDTAPISource.isEmpty()) {
                comp.setCity(cityCPClosedDTAPISource);
            } else if (cityCPClosedPlatlabSource != null && !cityCPClosedPlatlabSource.isEmpty()) {
                comp.setCity(cityCPClosedPlatlabSource);
            } else {
                comp.setCity(null);
            }

            String stateCPClosedDTAPISource = propertyDetailReportDataCP.SubjectProperty.SitusAddress.State;
            String stateCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (String) compClosed.get("state")
                    : null;

            if (stateCPClosedDTAPISource != null && !stateCPClosedDTAPISource.isEmpty()) {
                comp.setState(stateCPClosedDTAPISource);
            } else if (stateCPClosedPlatlabSource != null && !stateCPClosedPlatlabSource.isEmpty()) {
                comp.setState(stateCPClosedPlatlabSource);
            } else {
                comp.setState(null);
            }

            String zipcodeCPClosedDTAPISource = propertyDetailReportDataCP.SubjectProperty.SitusAddress.Zip9;
            String zipcodeCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (String) compClosed.get("zip")
                    : null;

            if (zipcodeCPClosedDTAPISource != null && !zipcodeCPClosedDTAPISource.isEmpty()) {
                comp.setZipcode(zipcodeCPClosedDTAPISource);
            } else if (zipcodeCPClosedPlatlabSource != null && !zipcodeCPClosedPlatlabSource.isEmpty()) {
                comp.setZipcode(zipcodeCPClosedPlatlabSource);
            } else {
                comp.setZipcode(null);
            }

            String countyCPClosedDTAPISource = propertyDetailReportDataCP.SubjectProperty.SitusAddress.County;
            String countyCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (String) compClosed.get("county")
                    : null;

            if (countyCPClosedDTAPISource != null && !countyCPClosedDTAPISource.isEmpty()) {
                comp.setCounty(countyCPClosedDTAPISource);
            } else if (countyCPClosedPlatlabSource != null && !countyCPClosedPlatlabSource.isEmpty()) {
                comp.setCounty(countyCPClosedPlatlabSource);
            } else {
                comp.setCounty(null);
            }

            Double proximityCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Double) compClosed.get("proximity")
                    : null;

            if (proximityCPClosedPlatlabSource != null) {
                comp.setProximity(proximityCPClosedPlatlabSource);
            } else {
                comp.setProximity(null);
            }

            Integer salePriceCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Integer) compClosed.get("price")
                    : null;

            if (salePriceCPClosedPlatlabSource != null) {
                comp.setSalePrice(salePriceCPClosedPlatlabSource);
            } else {
                comp.setSalePrice(null);
            }

            comp.setPricePerSqFt(BigDecimal.valueOf(Double.valueOf((Integer) compClosed.get("price")) / Double.parseDouble(String.valueOf((Integer) compClosed.get("square_feet")))).setScale(2, RoundingMode.HALF_UP).doubleValue());

            Integer originalListingPriceCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Integer) compClosed.get("original_listing_price")
                    : null;

            if (originalListingPriceCPClosedPlatlabSource != null) {
                comp.setOriginalListingPrice(originalListingPriceCPClosedPlatlabSource);
            } else {
                comp.setOriginalListingPrice(null);
            }

            Integer currentListingPriceCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Integer) compClosed.get("price")
                    : null;

            if (currentListingPriceCPClosedPlatlabSource != null) {
                comp.setCurrentListingPrice(currentListingPriceCPClosedPlatlabSource);
            } else {
                comp.setCurrentListingPrice(null);
            }

            String saleDateCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (String) compClosed.get("sold_date")
                    : null;

            if (saleDateCPClosedPlatlabSource != null) {
                comp.setSaleDate(saleDateCPClosedPlatlabSource);
            } else {
                comp.setSaleDate(null);
            }

            String listDateCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (String) compClosed.get("mls_list_date")
                    : null;

            if (listDateCPClosedPlatlabSource != null) {
                comp.setListDate(listDateCPClosedPlatlabSource);
            } else {
                comp.setListDate(null);
            }

            Integer daysOnMarketCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Integer) compClosed.get("days_on_market")
                    : null;

            if (listDateCPClosedPlatlabSource != null) {
                comp.setDaysOnMarket(daysOnMarketCPClosedPlatlabSource);
            } else {
                comp.setDaysOnMarket(null);
            }

            String mlsIDCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (String) compClosed.get("display_mls_number")
                    : null;

            if (mlsIDCPClosedPlatlabSource != null) {
                comp.setMlsID(mlsIDCPClosedPlatlabSource);
            } else {
                comp.setMlsID(null);
            }

            comp.setLocation(findLocationDensity(String.valueOf(compClosed.get("longitude")), String.valueOf(compClosed.get("latitude"))));

            Double siteORLotSizeCPClosedDTAPISource = propertyDetailReportDataCP.SiteInformation.Acres;
            BigDecimal siteORLotSizeCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (BigDecimal) compClosed.get("lot_size")
                    : null;

            if (siteORLotSizeCPClosedDTAPISource != null) {
                comp.setSiteORLotSize(siteORLotSizeCPClosedDTAPISource);
            } else if (siteORLotSizeCPClosedPlatlabSource != null) {
                comp.setSiteORLotSize(siteORLotSizeCPClosedPlatlabSource.doubleValue());
            } else {
                comp.setSiteORLotSize(null);
            }

            Integer yearBuiltCPClosedDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.YearBuilt;
            Integer yearBuiltCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Integer) compClosed.get("year_built")
                    : null;

            if (yearBuiltCPClosedDTAPISource != null) {
                comp.setYearBuilt(yearBuiltCPClosedDTAPISource);
            } else if (yearBuiltCPClosedPlatlabSource != null) {
                comp.setYearBuilt(yearBuiltCPClosedPlatlabSource);
            } else {
                comp.setYearBuilt(null);
            }

            String styleCPClosedDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.Style;
            String styleCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (String) compClosed.get("style")
                    : null;

            if (styleCPClosedDTAPISource != null && !styleCPClosedDTAPISource.isEmpty()) {
                comp.setStyle(styleCPClosedDTAPISource);
            } else if (styleCPClosedPlatlabSource != null && !styleCPClosedPlatlabSource.isEmpty()) {
                comp.setStyle(styleCPClosedPlatlabSource);
            } else {
                comp.setStyle(null);
            }

            comp.setTotalRooms(propertyDetailReportDataCP.PropertyCharacteristics.TotalRooms);

            Integer bedroomsCPClosedDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.Bedrooms;
            Integer bedroomsCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Integer) compClosed.get("bedrooms")
                    : null;

            if (bedroomsCPClosedDTAPISource != null && bedroomsCPClosedDTAPISource != 0) {
                comp.setBedrooms(bedroomsCPClosedDTAPISource);
            } else if (bedroomsCPClosedPlatlabSource != null && bedroomsCPClosedPlatlabSource != 0) {
                comp.setBedrooms(bedroomsCPClosedPlatlabSource);
            } else {
                comp.setBedrooms(null);
            }

            double bathroomsCPClosedDTAPISource;

            int bathroomsFullCPClosedDTAPISource = (propertyDetailReportDataCP.PropertyCharacteristics.FullBath != null) ? propertyDetailReportDataCP.PropertyCharacteristics.FullBath : 0;
            int bathroomsHalfCPClosedDTAPISource = (propertyDetailReportDataCP.PropertyCharacteristics.HalfBath != null) ? propertyDetailReportDataCP.PropertyCharacteristics.HalfBath : 0;

            bathroomsCPClosedDTAPISource = bathroomsFullCPClosedDTAPISource + bathroomsHalfCPClosedDTAPISource / 2.0;

            double bathroomsCPClosedPlatlabSource;

            int bathroomsCPClosedFullPlatlabSource = 0;
            int bathroomsCPClosedHalfPlatlabSource = 0;

            if (compClosed != null && !compClosed.isEmpty()) {
                bathroomsCPClosedFullPlatlabSource = (compClosed.get("bathrooms_full") != null) ? (int) compClosed.get("bathrooms_full") : 0;
                bathroomsCPClosedHalfPlatlabSource = (compClosed.get("bathrooms_half") != null) ? (int) compClosed.get("bathrooms_half") : 0;
            }

            bathroomsCPClosedPlatlabSource = bathroomsCPClosedFullPlatlabSource + bathroomsCPClosedHalfPlatlabSource / 2.0;

            if (bathroomsCPClosedDTAPISource != 0) {
                comp.setBathrooms(bathroomsCPClosedDTAPISource);
            } else if (bathroomsCPClosedPlatlabSource != 0) {
                comp.setBathrooms(bathroomsCPClosedPlatlabSource);
            } else {
                comp.setBathrooms(null);
            }

            Integer basementAreaCPClosedDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.BasementArea;
            Boolean hasBasementCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Boolean) compClosed.get("has_basement")
                    : null;

            if (hasBasementCPClosedPlatlabSource != null) {
                comp.setBasement(hasBasementCPClosedPlatlabSource ? "Yes" : "No");
            } else if (basementAreaCPClosedDTAPISource != null) {
                comp.setBasement(basementAreaCPClosedDTAPISource > 0 ? "Yes" : "No");
            } else {
                comp.setBasement("Unk.");
            }

            String heatTypeCPClosedDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.HeatType;
            if (heatTypeCPClosedDTAPISource != null && !heatTypeCPClosedDTAPISource.isEmpty()) {
                comp.setHeating(heatTypeCPClosedDTAPISource);
            } else {
                comp.setHeating("Unk.");
            }

            String airConditioningCPClosedDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.AirConditioning;
            if (airConditioningCPClosedDTAPISource != null && !airConditioningCPClosedDTAPISource.isEmpty()) {
                comp.setCooling(airConditioningCPClosedDTAPISource);
            } else {
                comp.setCooling("Unk.");
            }

            Integer garageSpacesCPClosedDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.GarageCapacity;
            Integer garageSpacesCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Integer) compClosed.get("garage_spaces")
                    : null;

            if (garageSpacesCPClosedDTAPISource != null) {
                if (garageSpacesCPClosedDTAPISource == 1) {
                    comp.setGarage("Garage - " + garageSpacesCPClosedDTAPISource + " car");
                } else if (garageSpacesCPClosedDTAPISource > 1) {
                    comp.setGarage("Garage - " + garageSpacesCPClosedDTAPISource + " cars");
                } else {
                    comp.setGarage("No Garage");
                }
            } else if (garageSpacesCPClosedPlatlabSource != null) {
                if (garageSpacesCPClosedPlatlabSource == 1) {
                    comp.setGarage("Garage - " + garageSpacesCPClosedPlatlabSource + " car");
                } else if (garageSpacesCPClosedPlatlabSource > 1) {
                    comp.setGarage("Garage - " + garageSpacesCPClosedPlatlabSource + " cars");
                } else {
                    comp.setGarage("No Garage");
                }
            } else {
                comp.setGarage(null);
            }

            Integer sqftCPClosedDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.LivingArea;
            Integer sqftCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Integer) compClosed.get("square_feet")
                    : null;

            if (sqftCPClosedDTAPISource != null) {
                comp.setGrossLivingArea(sqftCPClosedDTAPISource);
            } else if (sqftCPClosedPlatlabSource != null) {
                comp.setGrossLivingArea(sqftCPClosedPlatlabSource);
            } else {
                comp.setGrossLivingArea(null);
            }

            closedComparablePropertyInformationList.add(comp);
        }

        brokerPriceOpinionPDFInfoDTO.setClosedComparablePropertyInformationList(closedComparablePropertyInformationList);

        List<Map<String, Object>> resultCompsActive;

        String queryCompsActivePass1 = "select " +
                "plfhf.address, " +
                "plfhf.city, " +
                "plfhf.state, " +
                "plfhf.zip, " +
                "plfhf.county, " +
                "ROUND((ST_Distance(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geography, ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography) / 1609.34)::numeric, 2)::float as proximity, " +
                "plfhf.sold_price::int as sold_price, " +
                "plfhf.original_listing_price::int as original_listing_price, " +
                "plfhf.price::int as price, " +
                "left(plfhf.sold_date, 10)::varchar as sold_date, " +
                "left(plfhf.mls_list_date, 10)::varchar as mls_list_date, " +
                "(COALESCE(CAST(left(plfhf.sold_date, 10) AS date), CURRENT_DATE) - CAST(left(plfhf.mls_list_date, 10) AS date)) as days_on_market, " +
                "plfhf.display_mls_number, " +
                "plfhf.longitude, " +
                "plfhf.latitude, " +
                "case" +
                "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                "        then round(plfhf.lot_size_area::numeric, 2)" +
                "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                "        then round(plfhf.lot_size_area::numeric, 2)" +
                "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                "    when plfhf.lot_size_square_feet is not null and plfhf.lot_size_square_feet::numeric != 0" +
                "        then round(plfhf.lot_size_square_feet::numeric / 43560, 2)" +
                "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                "        then round(plfhf.lot_size_area::numeric, 2)" +
                "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                "    else null " +
                "end as lot_size, " +
                "plfhf.year_built::int, " +
                "replace(plfhf.style, ',', ', ') as style, " +
                "plfhf.bedrooms::int, " +
                "plfhf.bathrooms_full::int, " +
                "plfhf.bathrooms_half::int, " +
                "plfhf.square_feet::int, " +
                "plfhf.has_basement::bool, " +
                "plfhf.garage_spaces::int " +
                "from platlab_listings_full_history_filtered plfhf " +
                "where ST_Within(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geometry, ST_Buffer(ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography, 1609.34 * 0.5)::geometry) " +
                "and plfhf.status = 'active' " +
                "and plfhf.bedrooms::integer = " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() + " " +
                "and plfhf.bathrooms::integer = " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() + " " +
                "and plfhf.square_feet::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 0.95 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 1.05 " +
                "and plfhf.lot_size::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 0.9 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 1.1 " +
                "and plfhf.garage_spaces::integer = " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() + " " +
                "and plfhf.year_built::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " - 10 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " + 10 " +
                "and plfhf.display_mls_number != 'display_mls_number' " +
                "and plfhf.mls_list_date >= 'date' " +
                "and plfhf.mls_property_type = 'Single Family Home' " +
                "order by proximity asc";

        resultCompsActive = prodBackupJdbcTemplate.query(queryCompsActivePass1, rs -> {
            List<Map<String, Object>> rows = new ArrayList<>();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnLabel(i);
                    Object value = rs.getObject(i);
                    row.put(columnName, value);
                }
                rows.add(row);
            }

            return rows;
        });

        if (resultCompsActive.size() < 3) {

            String queryCompsActivePass2 = "select " +
                    "plfhf.address, " +
                    "plfhf.city, " +
                    "plfhf.state, " +
                    "plfhf.zip, " +
                    "plfhf.county, " +
                    "ROUND((ST_Distance(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geography, ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography) / 1609.34)::numeric, 2)::float as proximity, " +
                    "plfhf.sold_price::int as sold_price, " +
                    "plfhf.original_listing_price::int as original_listing_price, " +
                    "plfhf.price::int as price, " +
                    "left(plfhf.sold_date, 10)::varchar as sold_date, " +
                    "left(plfhf.mls_list_date, 10)::varchar as mls_list_date, " +
                    "(COALESCE(CAST(left(plfhf.sold_date, 10) AS date), CURRENT_DATE) - CAST(left(plfhf.mls_list_date, 10) AS date)) as days_on_market, " +
                    "plfhf.display_mls_number, " +
                    "plfhf.longitude, " +
                    "plfhf.latitude, " +
                    "case" +
                    "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    when plfhf.lot_size_square_feet is not null and plfhf.lot_size_square_feet::numeric != 0" +
                    "        then round(plfhf.lot_size_square_feet::numeric / 43560, 2)" +
                    "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    else null " +
                    "end as lot_size, " +
                    "plfhf.year_built::int, " +
                    "replace(plfhf.style, ',', ', ') as style, " +
                    "plfhf.bedrooms::int, " +
                    "plfhf.bathrooms_full::int, " +
                    "plfhf.bathrooms_half::int, " +
                    "plfhf.square_feet::int, " +
                    "plfhf.has_basement::bool, " +
                    "plfhf.garage_spaces::int " +
                    "from platlab_listings_full_history_filtered plfhf " +
                    "where ST_Within(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geometry, ST_Buffer(ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography, 1609.34 * 1.5)::geometry) " +
                    "and plfhf.status = 'active' " +
                    "and plfhf.bedrooms::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() + " - 1 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() + " + 1 " +
                    "and plfhf.bathrooms::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() + " - 1 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() + " + 1 " +
                    "and plfhf.square_feet::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 0.9 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 1.1 " +
                    "and plfhf.lot_size::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 0.8 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 1.2 " +
                    "and plfhf.garage_spaces::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() + " - 1 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() + " + 1 " +
                    "and plfhf.year_built::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " - 20 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " + 20 " +
                    "and plfhf.display_mls_number != 'display_mls_number' " +
                    "and plfhf.mls_list_date >= 'date' " +
                    "and plfhf.mls_property_type = 'Single Family Home' " +
                    "order by proximity asc";

            resultCompsActive = prodBackupJdbcTemplate.query(queryCompsActivePass2, rs -> {
                List<Map<String, Object>> rows = new ArrayList<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    rows.add(row);
                }

                return rows;
            });
        }

        if (resultCompsActive.size() < 3) {

            String queryCompsActivePass3 = "select " +
                    "plfhf.address, " +
                    "plfhf.city, " +
                    "plfhf.state, " +
                    "plfhf.zip, " +
                    "plfhf.county, " +
                    "ROUND((ST_Distance(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geography, ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography) / 1609.34)::numeric, 2)::float as proximity, " +
                    "plfhf.sold_price::int as sold_price, " +
                    "plfhf.original_listing_price::int as original_listing_price, " +
                    "plfhf.price::int as price, " +
                    "left(plfhf.sold_date, 10)::varchar as sold_date, " +
                    "left(plfhf.mls_list_date, 10)::varchar as mls_list_date, " +
                    "(COALESCE(CAST(left(plfhf.sold_date, 10) AS date), CURRENT_DATE) - CAST(left(plfhf.mls_list_date, 10) AS date)) as days_on_market, " +
                    "plfhf.display_mls_number, " +
                    "plfhf.longitude, " +
                    "plfhf.latitude, " +
                    "case" +
                    "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    when plfhf.lot_size_square_feet is not null and plfhf.lot_size_square_feet::numeric != 0" +
                    "        then round(plfhf.lot_size_square_feet::numeric / 43560, 2)" +
                    "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    else null " +
                    "end as lot_size, " +
                    "plfhf.year_built::int, " +
                    "replace(plfhf.style, ',', ', ') as style, " +
                    "plfhf.bedrooms::int, " +
                    "plfhf.bathrooms_full::int, " +
                    "plfhf.bathrooms_half::int, " +
                    "plfhf.square_feet::int, " +
                    "plfhf.has_basement::bool, " +
                    "plfhf.garage_spaces::int " +
                    "from platlab_listings_full_history_filtered plfhf " +
                    "where ST_Within(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geometry, ST_Buffer(ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography, 1609.34 * 5)::geometry) " +
                    "and plfhf.status = 'active' " +
                    "and plfhf.bedrooms::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() + " - 2 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() + " + 2 " +
                    "and plfhf.bathrooms::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() + " - 2 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() + " + 2 " +
                    "and plfhf.square_feet::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 0.8 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 1.2 " +
                    "and plfhf.lot_size::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 0.7 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 1.3 " +
                    "and plfhf.garage_spaces::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() + " - 2 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() + " + 2 " +
                    "and plfhf.year_built::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " - 20 and " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " + 20 " +
                    "and plfhf.display_mls_number != 'display_mls_number' " +
                    "and plfhf.mls_list_date >= 'date' " +
                    "and plfhf.mls_property_type = 'Single Family Home' " +
                    "order by proximity asc";

            resultCompsActive = prodBackupJdbcTemplate.query(queryCompsActivePass3, rs -> {
                List<Map<String, Object>> rows = new ArrayList<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    rows.add(row);
                }

                return rows;
            });
        }

        if (resultCompsActive.size() < 3) {

            String queryCompsActivePass4 = "select " +
                    "plfhf.address, " +
                    "plfhf.city, " +
                    "plfhf.state, " +
                    "plfhf.zip, " +
                    "plfhf.county, " +
                    "ROUND((ST_Distance(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geography, ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography) / 1609.34)::numeric, 2)::float as proximity, " +
                    "plfhf.sold_price::int as sold_price, " +
                    "plfhf.original_listing_price::int as original_listing_price, " +
                    "plfhf.price::int as price, " +
                    "left(plfhf.sold_date, 10)::varchar as sold_date, " +
                    "left(plfhf.mls_list_date, 10)::varchar as mls_list_date, " +
                    "(COALESCE(CAST(left(plfhf.sold_date, 10) AS date), CURRENT_DATE) - CAST(left(plfhf.mls_list_date, 10) AS date)) as days_on_market, " +
                    "plfhf.display_mls_number, " +
                    "plfhf.longitude, " +
                    "plfhf.latitude, " +
                    "case" +
                    "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size_display is not null and plfhf.lot_size_display::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size is not null and plfhf.lot_size::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    when plfhf.lot_size_square_feet is not null and plfhf.lot_size_square_feet::numeric != 0" +
                    "        then round(plfhf.lot_size_square_feet::numeric / 43560, 2)" +
                    "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Acres'" +
                    "        then round(plfhf.lot_size_area::numeric, 2)" +
                    "    when plfhf.lot_size_area is not null and plfhf.lot_size_area::numeric != 0 and plfhf.lot_size_units = 'Square Feet'" +
                    "        then round(plfhf.lot_size_area::numeric / 43560, 2)" +
                    "    else null " +
                    "end as lot_size, " +
                    "plfhf.year_built::int, " +
                    "replace(plfhf.style, ',', ', ') as style, " +
                    "plfhf.bedrooms::int, " +
                    "plfhf.bathrooms_full::int, " +
                    "plfhf.bathrooms_half::int, " +
                    "plfhf.square_feet::int, " +
                    "plfhf.has_basement::bool, " +
                    "plfhf.garage_spaces::int " +
                    "from platlab_listings_full_history_filtered plfhf " +
                    "where ST_Within(ST_SetSRID(ST_MakePoint(plfhf.longitude::numeric, plfhf.latitude::numeric), 4326)::geometry, ST_Buffer(ST_MakePoint(" + brokerPriceOpinionPDFInfoDTO.getLongitude() + ", " + brokerPriceOpinionPDFInfoDTO.getLatitude() + ")::geography, 1609.34 * 20)::geometry) " +
                    "and plfhf.status = 'active' " +
                    "and plfhf.bedrooms::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() + " - 3 AND " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() + " + 3 " +
                    "and plfhf.bathrooms::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() + " - 3 AND " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() + " + 3 " +
                    "and plfhf.square_feet::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 0.7 AND " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() + " * 1.3 " +
                    "and plfhf.lot_size::numeric between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 0.7 AND " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() + " * 1.3 " +
                    "and plfhf.garage_spaces::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() + " - 2 AND " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() + " + 2 " +
                    "and plfhf.year_built::integer between " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " - 30 AND " + brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() + " + 30 " +
                    "and plfhf.display_mls_number != 'display_mls_number' " +
                    "and plfhf.mls_list_date >= 'date' " +
                    "and plfhf.mls_property_type = 'Single Family Home' " +
                    "order by proximity asc";

            resultCompsActive = prodBackupJdbcTemplate.query(queryCompsActivePass4, rs -> {
                List<Map<String, Object>> rows = new ArrayList<>();
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    rows.add(row);
                }

                return rows;
            });
        }

        List<ComparablePropertyInformation> activeComparablePropertyInformationList = new ArrayList<>();

        for (Map<String, Object> compActive : resultCompsActive) {

            if (activeComparablePropertyInformationList.size() >= 3) {
                break;
            }

            propertyDetailReportResponseComparableProperty = getPropertyDetailReportDTAPI(compActive.get("address") + ", " + compActive.get("city") + ", " + compActive.get("state") + " " + compActive.get("zip") + ", United States");

            PropertyDetailReportData propertyDetailReportDataCP = propertyDetailReportResponseComparableProperty.Reports.get(0).Data;

            ComparablePropertyInformation comp = new ComparablePropertyInformation();

            String addressCPActiveDTAPISource = propertyDetailReportDataCP.SubjectProperty.SitusAddress.StreetAddress;
            String addressCPActivePlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (String) compActive.get("address")
                    : null;

            if (addressCPActiveDTAPISource != null && !addressCPActiveDTAPISource.isEmpty()) {
                comp.setAddress(addressCPActiveDTAPISource);
            } else if (addressCPActivePlatlabSource != null && !addressCPActivePlatlabSource.isEmpty()) {
                comp.setAddress(addressCPActivePlatlabSource);
            } else {
                comp.setAddress(null);
            }

            String cityCPActiveDTAPISource = propertyDetailReportDataCP.SubjectProperty.SitusAddress.City;
            String cityCPActivePlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (String) compActive.get("city")
                    : null;

            if (cityCPActiveDTAPISource != null && !cityCPActiveDTAPISource.isEmpty()) {
                comp.setCity(cityCPActiveDTAPISource);
            } else if (cityCPActivePlatlabSource != null && !cityCPActivePlatlabSource.isEmpty()) {
                comp.setCity(cityCPActivePlatlabSource);
            } else {
                comp.setCity(null);
            }

            String stateCPActiveDTAPISource = propertyDetailReportDataCP.SubjectProperty.SitusAddress.State;
            String stateCPActivePlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (String) compActive.get("state")
                    : null;

            if (stateCPActiveDTAPISource != null && !stateCPActiveDTAPISource.isEmpty()) {
                comp.setState(stateCPActiveDTAPISource);
            } else if (stateCPActivePlatlabSource != null && !stateCPActivePlatlabSource.isEmpty()) {
                comp.setState(stateCPActivePlatlabSource);
            } else {
                comp.setState(null);
            }

            String zipcodeCPActiveDTAPISource = propertyDetailReportDataCP.SubjectProperty.SitusAddress.Zip9;
            String zipcodeCPActivePlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (String) compActive.get("zip")
                    : null;

            if (zipcodeCPActiveDTAPISource != null && !zipcodeCPActiveDTAPISource.isEmpty()) {
                comp.setZipcode(zipcodeCPActiveDTAPISource);
            } else if (zipcodeCPActivePlatlabSource != null && !zipcodeCPActivePlatlabSource.isEmpty()) {
                comp.setZipcode(zipcodeCPActivePlatlabSource);
            } else {
                comp.setZipcode(null);
            }

            String countyCPActiveDTAPISource = propertyDetailReportDataCP.SubjectProperty.SitusAddress.County;
            String countyCPActivePlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (String) compActive.get("county")
                    : null;

            if (countyCPActiveDTAPISource != null && !countyCPActiveDTAPISource.isEmpty()) {
                comp.setCounty(countyCPActiveDTAPISource);
            } else if (countyCPActivePlatlabSource != null && !countyCPActivePlatlabSource.isEmpty()) {
                comp.setCounty(countyCPActivePlatlabSource);
            } else {
                comp.setCounty(null);
            }

            Double proximityCPActivePlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Double) compActive.get("proximity")
                    : null;

            if (proximityCPActivePlatlabSource != null) {
                comp.setProximity(proximityCPActivePlatlabSource);
            } else {
                comp.setProximity(null);
            }

            Integer salePriceCPActivePlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Integer) compActive.get("price")
                    : null;

            if (salePriceCPActivePlatlabSource != null) {
                comp.setSalePrice(salePriceCPActivePlatlabSource);
            } else {
                comp.setSalePrice(null);
            }

            comp.setPricePerSqFt(BigDecimal.valueOf(Double.valueOf((Integer) compActive.get("price")) / Double.parseDouble(String.valueOf((Integer) compActive.get("square_feet")))).setScale(2, RoundingMode.HALF_UP).doubleValue());

            Integer originalListingPriceCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Integer) compActive.get("original_listing_price")
                    : null;

            if (originalListingPriceCPClosedPlatlabSource != null) {
                comp.setOriginalListingPrice(originalListingPriceCPClosedPlatlabSource);
            } else {
                comp.setOriginalListingPrice(null);
            }

            Integer currentListingPriceCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Integer) compActive.get("price")
                    : null;

            if (currentListingPriceCPClosedPlatlabSource != null) {
                comp.setCurrentListingPrice(currentListingPriceCPClosedPlatlabSource);
            } else {
                comp.setCurrentListingPrice(null);
            }

            String saleDateCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (String) compActive.get("sold_date")
                    : null;

            if (saleDateCPClosedPlatlabSource != null) {
                comp.setSaleDate(saleDateCPClosedPlatlabSource);
            } else {
                comp.setSaleDate(null);
            }

            String listDateCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (String) compActive.get("mls_list_date")
                    : null;

            if (listDateCPClosedPlatlabSource != null) {
                comp.setListDate(listDateCPClosedPlatlabSource);
            } else {
                comp.setListDate(null);
            }

            Integer daysOnMarketCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Integer) compActive.get("days_on_market")
                    : null;

            if (listDateCPClosedPlatlabSource != null) {
                comp.setDaysOnMarket(daysOnMarketCPClosedPlatlabSource);
            } else {
                comp.setDaysOnMarket(null);
            }

            String mlsIDCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (String) compActive.get("display_mls_number")
                    : null;

            if (mlsIDCPClosedPlatlabSource != null) {
                comp.setMlsID(mlsIDCPClosedPlatlabSource);
            } else {
                comp.setMlsID(null);
            }

            comp.setLocation(findLocationDensity(String.valueOf(compActive.get("longitude")), String.valueOf(compActive.get("latitude"))));

            Double siteORLotSizeCPClosedDTAPISource = propertyDetailReportDataCP.SiteInformation.Acres;
            BigDecimal siteORLotSizeCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (BigDecimal) compActive.get("lot_size")
                    : null;

            if (siteORLotSizeCPClosedDTAPISource != null) {
                comp.setSiteORLotSize(siteORLotSizeCPClosedDTAPISource);
            } else if (siteORLotSizeCPClosedPlatlabSource != null) {
                comp.setSiteORLotSize(siteORLotSizeCPClosedPlatlabSource.doubleValue());
            } else {
                comp.setSiteORLotSize(null);
            }

            Integer yearBuiltCPClosedDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.YearBuilt;
            Integer yearBuiltCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Integer) compActive.get("year_built")
                    : null;

            if (yearBuiltCPClosedDTAPISource != null) {
                comp.setYearBuilt(yearBuiltCPClosedDTAPISource);
            } else if (yearBuiltCPClosedPlatlabSource != null) {
                comp.setYearBuilt(yearBuiltCPClosedPlatlabSource);
            } else {
                comp.setYearBuilt(null);
            }

            String styleCPClosedDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.Style;
            String styleCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (String) compActive.get("style")
                    : null;

            if (styleCPClosedDTAPISource != null && !styleCPClosedDTAPISource.isEmpty()) {
                comp.setStyle(styleCPClosedDTAPISource);
            } else if (styleCPClosedPlatlabSource != null && !styleCPClosedPlatlabSource.isEmpty()) {
                comp.setStyle(styleCPClosedPlatlabSource);
            } else {
                comp.setStyle(null);
            }

            comp.setTotalRooms(propertyDetailReportDataCP.PropertyCharacteristics.TotalRooms);

            Integer bedroomsCPActiveDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.Bedrooms;
            Integer bedroomsCPActivePlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Integer) compActive.get("bedrooms")
                    : null;

            if (bedroomsCPActiveDTAPISource != null && bedroomsCPActiveDTAPISource != 0) {
                comp.setBedrooms(bedroomsCPActiveDTAPISource);
            } else if (bedroomsCPActivePlatlabSource != null && bedroomsCPActivePlatlabSource != 0) {
                comp.setBedrooms(bedroomsCPActivePlatlabSource);
            } else {
                comp.setBedrooms(null);
            }

            double bathroomsCPActiveDTAPISource;

            int bathroomsFullCPActiveDTAPISource = (propertyDetailReportDataCP.PropertyCharacteristics.FullBath != null) ? propertyDetailReportDataCP.PropertyCharacteristics.FullBath : 0;
            int bathroomsHalfCPActiveDTAPISource = (propertyDetailReportDataCP.PropertyCharacteristics.HalfBath != null) ? propertyDetailReportDataCP.PropertyCharacteristics.HalfBath : 0;

            bathroomsCPActiveDTAPISource = bathroomsFullCPActiveDTAPISource + bathroomsHalfCPActiveDTAPISource / 2.0;

            double bathroomsCPActivePlatlabSource;

            int bathroomsCPActiveFullPlatlabSource = 0;
            int bathroomsCPActiveHalfPlatlabSource = 0;

            if (compActive != null && !compActive.isEmpty()) {
                bathroomsCPActiveFullPlatlabSource = (compActive.get("bathrooms_full") != null) ? (int) compActive.get("bathrooms_full") : 0;
                bathroomsCPActiveHalfPlatlabSource = (compActive.get("bathrooms_half") != null) ? (int) compActive.get("bathrooms_half") : 0;
            }

            bathroomsCPActivePlatlabSource = bathroomsCPActiveFullPlatlabSource + bathroomsCPActiveHalfPlatlabSource / 2.0;

            if (bathroomsCPActiveDTAPISource != 0) {
                comp.setBathrooms(bathroomsCPActiveDTAPISource);
            } else if (bathroomsCPActivePlatlabSource != 0) {
                comp.setBathrooms(bathroomsCPActivePlatlabSource);
            } else {
                comp.setBathrooms(null);
            }

            Integer basementAreaCPActiveDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.BasementArea;
            Boolean hasBasementCPActivePlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Boolean) compActive.get("has_basement")
                    : null;

            if (hasBasementCPActivePlatlabSource != null) {
                comp.setBasement(hasBasementCPActivePlatlabSource ? "Yes" : "No");
            } else if (basementAreaCPActiveDTAPISource != null) {
                comp.setBasement(basementAreaCPActiveDTAPISource > 0 ? "Yes" : "No");
            } else {
                comp.setBasement("Unk.");
            }

            String heatTypeCPActiveDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.HeatType;
            if (heatTypeCPActiveDTAPISource != null && !heatTypeCPActiveDTAPISource.isEmpty()) {
                comp.setHeating(heatTypeCPActiveDTAPISource);
            } else {
                comp.setHeating("Unk.");
            }

            String airConditioningCPActiveDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.AirConditioning;
            if (airConditioningCPActiveDTAPISource != null && !airConditioningCPActiveDTAPISource.isEmpty()) {
                comp.setCooling(airConditioningCPActiveDTAPISource);
            } else {
                comp.setCooling("Unk.");
            }

            Integer garageSpacesCPActiveDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.GarageCapacity;
            Integer garageSpacesCPActivePlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Integer) compActive.get("garage_spaces")
                    : null;

            if (garageSpacesCPActiveDTAPISource != null) {
                if (garageSpacesCPActiveDTAPISource == 1) {
                    comp.setGarage("Garage - " + garageSpacesCPActiveDTAPISource + " car");
                } else if (garageSpacesCPActiveDTAPISource > 1) {
                    comp.setGarage("Garage - " + garageSpacesCPActiveDTAPISource + " cars");
                } else {
                    comp.setGarage("No Garage");
                }
            } else if (garageSpacesCPActivePlatlabSource != null) {
                if (garageSpacesCPActivePlatlabSource == 1) {
                    comp.setGarage("Garage - " + garageSpacesCPActivePlatlabSource + " car");
                } else if (garageSpacesCPActivePlatlabSource > 1) {
                    comp.setGarage("Garage - " + garageSpacesCPActivePlatlabSource + " cars");
                } else {
                    comp.setGarage("No Garage");
                }
            } else {
                comp.setGarage(null);
            }

            Integer sqftCPActiveDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.LivingArea;
            Integer sqftCPActivePlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Integer) compActive.get("square_feet")
                    : null;

            if (sqftCPActiveDTAPISource != null) {
                comp.setGrossLivingArea(sqftCPActiveDTAPISource);
            } else if (sqftCPActivePlatlabSource != null) {
                comp.setGrossLivingArea(sqftCPActivePlatlabSource);
            } else {
                comp.setGrossLivingArea(null);
            }

            activeComparablePropertyInformationList.add(comp);
        }

        brokerPriceOpinionPDFInfoDTO.setActiveComparablePropertyInformationList(activeComparablePropertyInformationList);

        return brokerPriceOpinionPDFInfoDTO;
    }

    public String getAccessTokenDTAPI() {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("ClientId", DTAPIClientId);
        requestBody.put("ClientSecretKey", DTAPIClientSecretKey);

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://dtapiuat.datatree.com/api/Login/AuthenticateClient?Ver=1.0",
                    HttpMethod.POST, requestEntity, String.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody().replaceAll("\"", "");
            } else {
                throw new RuntimeException("Authentication Failed: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Authentication Exception: " + e.getMessage(), e);
        }
    }

    public PropertyDetailReportResponse getPropertyDetailReportDTAPI(String fullAddress) {

        Map<String, Object> body = new HashMap<>();
        body.put("ProductNames", Collections.singletonList("PropertyDetailReport"));
        body.put("SearchType", "FullAddress");
        body.put("FullAddress", fullAddress);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(body);

            URL url = new URL("https://dtapiuat.datatree.com/api/Report/GetReport?Ver=1.0");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + getAccessTokenDTAPI());
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
            }

            int responseCode = connection.getResponseCode();
            InputStream responseStream = (responseCode == 200) ? connection.getInputStream() : connection.getErrorStream();

            return objectMapper.readValue(responseStream, PropertyDetailReportResponse.class);

        } catch (Exception e) {
            return null;
        }
    }

    public String findLocationDensity(String longitude, String latitude) {

        String query = "SELECT mktclassid, geoclassid FROM sti_block_group_2010_density " +
                "ORDER BY geom <-> 'SRID=4326;POINT(" + longitude + " " + latitude + ")'::geometry LIMIT 1";

        List<DensityResponseDTO> resultList = prodBackupJdbcTemplate.query(query, (rs, rowNum) -> {
            return new DensityResponseDTO(
                    rs.getInt("mktclassid"),
                    rs.getInt("geoclassid")
            );
        });

        switch (resultList.get(0).getGeoclassid()) {
            case 1:
            case 2:
                return "Urban";
            case 3:
                return "Low-Density Urban";
            case 4:
            case 5:
                return "Suburban";
            case 6:
                return "Exurban";
            case 7:
                return "Rural";
            default:
                return "N/A";
        }
    }
}