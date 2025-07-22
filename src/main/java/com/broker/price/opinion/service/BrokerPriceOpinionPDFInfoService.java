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
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BrokerPriceOpinionPDFInfoService {

    @Value("${dt.api.client.id}")
    private String DTAPIClientId;

    @Value("${dt.api.client.secret.key}")
    private String DTAPIClientSecretKey;

    private final JdbcTemplate trinoJdbcTemplate;
    private final JdbcTemplate prodJdbcTemplate;
    private final JdbcTemplate prodBackupJdbcTemplate;

    @Autowired
    public BrokerPriceOpinionPDFInfoService(
            @Qualifier("trinoJdbcTemplate") JdbcTemplate trinoJdbcTemplate,
            @Qualifier("prodJdbcTemplate") JdbcTemplate prodJdbcTemplate,
            @Qualifier("prodBackupJdbcTemplate") JdbcTemplate prodBackupJdbcTemplate) {
        this.trinoJdbcTemplate = trinoJdbcTemplate;
        this.prodJdbcTemplate = prodJdbcTemplate;
        this.prodBackupJdbcTemplate = prodBackupJdbcTemplate;
    }

    public Integer generateBPOInformationRequest(String propertyID) {

        String sql = "INSERT INTO firstamerican.broker_price_opinion_pdf_info " +
                "(property_id, bpo_info_generation_status) VALUES (?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        prodJdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, propertyID);
            ps.setString(2, "In progress");
            return ps;
        }, keyHolder);

        Map<String, Object> keys = keyHolder.getKeys();
        Integer id = (keys != null && keys.get("id") != null) ? ((Number) keys.get("id")).intValue() : null;

        CompletableFuture.runAsync(() -> this.generateBPOInformation(id, propertyID));

        return id;
    }

    public void generateBPOInformation(Integer id, String propertyID) {

        BrokerPriceOpinionPDFInfoDTO brokerPriceOpinionPDFInfoDTO = getBrokerPriceOpinionPDFInformation(propertyID);

        String sql = "UPDATE firstamerican.broker_price_opinion_pdf_info SET " +

                "full_address = ?, " +
                "property_id = ?, " +
                "status = ?, " +
                "longitude = ?, " +
                "latitude = ?, " +
                "placekey = ?, " +

                "o_i_loan_number = ?, " +
                "o_i_client = ?, " +
                "o_i_order_for = ?, " +
                "o_i_order_number = ?, " +
                "o_i_borrower_or_owner_name = ?, " +
                "o_i_address = ?, " +
                "o_i_city = ?, " +
                "o_i_state = ?, " +
                "o_i_zipcode = ?, " +
                "o_i_county = ?, " +
                "o_i_parcel_id = ?, " +
                "o_i_fee_simple_or_leasehold = ?, " +

                "p_i_number_of_units = ?, " +
                "p_i_property_type = ?, " +
                "p_i_property_style = ?, " +
                "p_i_sqft_gla = ?, " +
                "p_i_total_rooms = ?, " +
                "p_i_bedrooms = ?, " +
                "p_i_bathrooms = ?, " +
                "p_i_garage_spaces = ?, " +
                "p_i_garage = ?, " +
                "p_i_year_built = ?, " +
                "p_i_view = ?, " +
                "p_i_pool = ?, " +
                "p_i_spa = ?, " +
                "p_i_feature_porch = ?, " +
                "p_i_feature_patio = ?, " +
                "p_i_feature_deck = ?, " +
                "p_i_number_of_fireplaces = ?, " +
                "p_i_overall_condition = ?, " +
                "p_i_occupancy = ?, " +
                "p_i_current_rent = ?, " +
                "p_i_market_rent = ?, " +
                "p_i_is_listed = ?, " +
                "p_i_is_listed_in_past_12_months = ?, " +
                "p_i_list_price = ?, " +
                "p_i_name_of_listing_company = ?, " +
                "p_i_listing_agent_phone = ?, " +
                "p_i_is_transferred_in_past_12_months = ?, " +
                "p_i_prior_sale_date = ?, " +
                "p_i_prior_sale_price = ?, " +
                "p_i_current_tax = ?, " +
                "p_i_delinquent_tax = ?, " +
                "p_i_condo_or_pud = ?, " +
                "p_i_fee_hoa = ?, " +
                "p_i_zoning = ?, " +
                "p_i_lot_size = ?, " +
                "p_i_land_value = ?, " +
                "p_i_is_conforms_to_neighborhood = ?, " +

                "c_i_condition_overall = ?, " +
                "c_i_condition_comments = ?, " +

                "n_i_market_conditions = ?, " +
                "n_i_number_of_competitive_listings = ?, " +
                "n_i_price_range_of_current_listing_and_sales_from = ?, " +
                "n_i_price_range_of_current_listing_and_sales_to = ?, " +
                "n_i_supply_and_demand = ?, " +
                "n_i_positive_or_negative_influences = ?, " +
                "n_i_location = ?, " +
                "n_i_neighborhood_trend = ?, " +
                "n_i_homes_in_neighborhood_are = ?, " +
                "n_i_average_market_time = ?, " +
                "n_i_most_probable_buyer = ?, " +

                "bpo_info_generation_status = ? " +
                "WHERE id = ?";

        prodJdbcTemplate.update(sql,
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getFullAddress()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyID()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getStatus()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getLongitude()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getLatitude()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPlacekey()).orElse(null),

                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getLoanNumber()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getClient()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getOrderFor()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getOrderNumber()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getBorrowerOrOwnerName()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getAddress()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getCity()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getState()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getZipcode()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getCounty()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getParcelID()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getOrderInformation().getFeeSimpleORLeasehold()).orElse(null),

                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getNumberOfUnits()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPropertyType()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPropertyStyle()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getTotalRooms()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarage()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getView()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPool()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSpa()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getFeaturePorch()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getFeaturePatio()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getFeatureDeck()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getNumberOfFireplaces()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getOverallCondition()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getOccupancy()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getCurrentRent()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getMarketRent()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getIsListed()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getIsListedInPast12Months()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getListPrice()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getNameOfListingCompany()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getListingAgentPhone()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getIsTransferredInPast12Months()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPriorSaleDate()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getPriorSalePrice()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getCurrentTax()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getDelinquentTax()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getCondoOrPUD()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getFeeHOA()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getZoning()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLandValue()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getIsConformsToNeighborhood()).orElse(null),

                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getConditionInformation().getOverallCondition()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getConditionInformation().getComments()).orElse(null),

                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getNeighborhoodInformation().getMarketConditions()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getNeighborhoodInformation().getNumberOfCompetitiveListings()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getNeighborhoodInformation().getPriceRangeOfCurrentListingAndSalesFrom()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getNeighborhoodInformation().getPriceRangeOfCurrentListingAndSalesTo()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getNeighborhoodInformation().getSupplyAndDemand()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getNeighborhoodInformation().getPositiveOrNegativeInfluences()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getNeighborhoodInformation().getLocation()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getNeighborhoodInformation().getNeighborhoodTrend()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getNeighborhoodInformation().getHomesInNeighborhoodAre()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getNeighborhoodInformation().getAverageMarketTime()).orElse(null),
                Optional.ofNullable(brokerPriceOpinionPDFInfoDTO.getNeighborhoodInformation().getMostProbableBuyer()).orElse(null),

                "Completed",
                id
        );

        generateBPOInformationComps(id, "Closed", brokerPriceOpinionPDFInfoDTO.getClosedComparablePropertyInformationList());
        generateBPOInformationComps(id, "Active", brokerPriceOpinionPDFInfoDTO.getActiveComparablePropertyInformationList());
    }

    public void generateBPOInformationComps(Integer bpoId, String status, List<ComparablePropertyInformation> comps) {

        String sql = "INSERT INTO firstamerican.broker_price_opinion_pdf_info_comps (" +
                "bpo_id, comp_number, status, address, city, state, zipcode, county, proximity, sale_price, price_per_sqft, " +
                "original_listing_price, current_listing_price, sale_date, list_date, days_on_market, mls_id, financing, " +
                "sales_concession, bank_or_reo_sale, location, site_or_view, site_or_lot_size, year_built, construction, " +
                "condition, style, total_rooms, bedrooms, bathrooms, gross_living_area, basement_and_finish, heating, cooling, " +
                "garage, carport, additional_amenities, net_adjustments) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        for (int i = 0; i < comps.size(); i++) {

            ComparablePropertyInformation comp = comps.get(i);
            int compNumber = i + 1;

            prodJdbcTemplate.update(sql,
                    bpoId, compNumber, status,
                    Optional.ofNullable(comp.getAddress()).orElse(null),
                    Optional.ofNullable(comp.getCity()).orElse(null),
                    Optional.ofNullable(comp.getState()).orElse(null),
                    Optional.ofNullable(comp.getZipcode()).orElse(null),
                    Optional.ofNullable(comp.getCounty()).orElse(null),
                    comp.getProximity(),
                    comp.getSalePrice(),
                    comp.getPricePerSqFt(),
                    comp.getOriginalListingPrice(),
                    comp.getCurrentListingPrice(),
                    Optional.ofNullable(comp.getSaleDate()).orElse(null),
                    Optional.ofNullable(comp.getListDate()).orElse(null),
                    comp.getDaysOnMarket(),
                    Optional.ofNullable(comp.getMlsID()).orElse(null),
                    Optional.ofNullable(comp.getFinancing()).orElse(null),
                    Optional.ofNullable(comp.getSalesConcession()).orElse(null),
                    Optional.ofNullable(comp.getBankOrREOSale()).orElse(null),
                    Optional.ofNullable(comp.getLocation()).orElse(null),
                    Optional.ofNullable(comp.getSiteOrView()).orElse(null),
                    comp.getSiteOrLotSize(),
                    comp.getYearBuilt(),
                    Optional.ofNullable(comp.getConstruction()).orElse(null),
                    Optional.ofNullable(comp.getCondition()).orElse(null),
                    Optional.ofNullable(comp.getStyle()).orElse(null),
                    comp.getTotalRooms(),
                    comp.getBedrooms(),
                    comp.getBathrooms(),
                    comp.getGrossLivingArea(),
                    Optional.ofNullable(comp.getBasementAndFinish()).orElse(null),
                    Optional.ofNullable(comp.getHeating()).orElse(null),
                    Optional.ofNullable(comp.getCooling()).orElse(null),
                    Optional.ofNullable(comp.getGarage()).orElse(null),
                    Optional.ofNullable(comp.getCarport()).orElse(null),
                    Optional.ofNullable(comp.getAdditionalAmenities()).orElse(null),
                    Optional.ofNullable(comp.getNetAdjustments()).orElse(null)
            );
        }
    }

    public BrokerPriceOpinionPDFInfoDTO getBPOInformationById(int id) {

        String sql = "SELECT * FROM firstamerican.broker_price_opinion_pdf_info WHERE id = ?";

        BrokerPriceOpinionPDFInfoDTO dto = prodBackupJdbcTemplate.queryForObject(sql, new Object[]{id},
                (rs, rowNum) -> {
                    BrokerPriceOpinionPDFInfoDTO bpo = new BrokerPriceOpinionPDFInfoDTO();
                    bpo.setFullAddress(rs.getString("full_address"));
                    bpo.setPropertyID(rs.getString("property_id"));
                    bpo.setStatus(rs.getString("status"));
                    bpo.setLongitude(rs.getString("longitude"));
                    bpo.setLatitude(rs.getString("latitude"));
                    bpo.setPlacekey(rs.getString("placekey"));

                    bpo.setOrderInformation(mapOrderInformation(rs));
                    bpo.setPropertyInformation(mapPropertyInformation(rs));
                    bpo.setConditionInformation(mapConditionInformation(rs));
                    bpo.setNeighborhoodInformation(mapNeighborhoodInformation(rs));
                    return bpo;
                }
        );

        String compsSQL = "SELECT * FROM firstamerican.broker_price_opinion_pdf_info_comps WHERE bpo_id = ?";

        List<ComparablePropertyInformation> comps = prodBackupJdbcTemplate.query(compsSQL, new Object[]{id},
                (rs, rowNum) -> mapComparableProperty(rs)
        );

        dto.setActiveComparablePropertyInformationList(comps.stream()
                        .filter(c -> "Active".equalsIgnoreCase(c.getStatus()))
                        .collect(Collectors.toList()));

        dto.setClosedComparablePropertyInformationList(comps.stream()
                        .filter(c -> "Closed".equalsIgnoreCase(c.getStatus()))
                        .collect(Collectors.toList())
        );

        return dto;
    }

    private OrderInformation mapOrderInformation(ResultSet rs) throws SQLException {

        OrderInformation info = new OrderInformation();

        info.setLoanNumber(rs.getString("o_i_loan_number"));
        info.setClient(rs.getString("o_i_client"));
        info.setOrderFor(rs.getString("o_i_order_for"));
        info.setOrderNumber(rs.getString("o_i_order_number"));
        info.setBorrowerOrOwnerName(rs.getString("o_i_borrower_or_owner_name"));
        info.setAddress(rs.getString("o_i_address"));
        info.setCity(rs.getString("o_i_city"));
        info.setState(rs.getString("o_i_state"));
        info.setZipcode(rs.getString("o_i_zipcode"));
        info.setCounty(rs.getString("o_i_county"));
        info.setParcelID(rs.getString("o_i_parcel_id"));
        info.setFeeSimpleORLeasehold(rs.getString("o_i_fee_simple_or_leasehold"));

        return info;
    }

    private PropertyInformation mapPropertyInformation(ResultSet rs) throws SQLException {

        PropertyInformation info = new PropertyInformation();

        info.setNumberOfUnits(rs.getObject("p_i_number_of_units", Integer.class));
        info.setPropertyType(rs.getString("p_i_property_type"));
        info.setPropertyStyle(rs.getString("p_i_property_style"));
        info.setSqftGLA(rs.getObject("p_i_sqft_gla", Integer.class));
        info.setTotalRooms(rs.getObject("p_i_total_rooms", Integer.class));
        info.setBedrooms(rs.getObject("p_i_bedrooms", Integer.class));
        info.setBathrooms(rs.getObject("p_i_bathrooms", Double.class));
        info.setGarageSpaces(rs.getObject("p_i_garage_spaces", Integer.class));
        info.setGarage(rs.getString("p_i_garage"));
        info.setYearBuilt(rs.getObject("p_i_year_built", Integer.class));
        info.setView(rs.getString("p_i_view"));
        info.setPool(rs.getString("p_i_pool"));
        info.setSpa(rs.getString("p_i_spa"));
        info.setFeaturePorch(rs.getString("p_i_feature_porch"));
        info.setFeaturePatio(rs.getString("p_i_feature_patio"));
        info.setFeatureDeck(rs.getString("p_i_feature_deck"));
        info.setNumberOfFireplaces(rs.getObject("p_i_number_of_fireplaces", Integer.class));
        info.setOverallCondition(rs.getString("p_i_overall_condition"));
        info.setOccupancy(rs.getString("p_i_occupancy"));
        info.setCurrentRent(rs.getObject("p_i_current_rent", Integer.class));
        info.setMarketRent(rs.getObject("p_i_market_rent", Integer.class));
        info.setIsListed(rs.getString("p_i_is_listed"));
        info.setIsListedInPast12Months(rs.getObject("p_i_is_listed_in_past_12_months", Boolean.class));
        info.setListPrice(rs.getObject("p_i_list_price", Integer.class));
        info.setNameOfListingCompany(rs.getString("p_i_name_of_listing_company"));
        info.setListingAgentPhone(rs.getString("p_i_listing_agent_phone"));
        info.setIsTransferredInPast12Months(rs.getObject("p_i_is_transferred_in_past_12_months", Boolean.class));
        info.setPriorSaleDate(rs.getString("p_i_prior_sale_date"));
        info.setPriorSalePrice(rs.getObject("p_i_prior_sale_price", Double.class));
        info.setCurrentTax(rs.getObject("p_i_current_tax", Double.class));
        info.setDelinquentTax(rs.getObject("p_i_delinquent_tax", Double.class));
        info.setCondoOrPUD(rs.getString("p_i_condo_or_pud"));
        info.setFeeHOA(rs.getObject("p_i_fee_hoa", Double.class));
        info.setZoning(rs.getString("p_i_zoning"));
        info.setLotSize(rs.getObject("p_i_lot_size", Double.class));
        info.setLandValue(rs.getObject("p_i_land_value", Double.class));
        info.setIsConformsToNeighborhood(rs.getObject("p_i_is_conforms_to_neighborhood", Boolean.class));

        return info;
    }

    private ConditionInformation mapConditionInformation(ResultSet rs) throws SQLException {

        ConditionInformation info = new ConditionInformation();

        info.setOverallCondition(rs.getString("c_i_condition_overall"));
        info.setComments(rs.getString("c_i_condition_comments"));

        return info;
    }

    private NeighborhoodInformation mapNeighborhoodInformation(ResultSet rs) throws SQLException {

        NeighborhoodInformation info = new NeighborhoodInformation();

        info.setMarketConditions(rs.getString("n_i_market_conditions"));
        info.setNumberOfCompetitiveListings(rs.getInt("n_i_number_of_competitive_listings"));
        info.setPriceRangeOfCurrentListingAndSalesFrom(rs.getInt("n_i_price_range_of_current_listing_and_sales_from"));
        info.setPriceRangeOfCurrentListingAndSalesTo(rs.getInt("n_i_price_range_of_current_listing_and_sales_to"));
        info.setSupplyAndDemand(rs.getString("n_i_supply_and_demand"));
        info.setPositiveOrNegativeInfluences(rs.getString("n_i_positive_or_negative_influences"));
        info.setLocation(rs.getString("n_i_location"));
        info.setNeighborhoodTrend(rs.getString("n_i_neighborhood_trend"));
        info.setHomesInNeighborhoodAre(rs.getString("n_i_homes_in_neighborhood_are"));
        info.setAverageMarketTime(rs.getString("n_i_average_market_time"));
        info.setMostProbableBuyer(rs.getString("n_i_most_probable_buyer"));

        return info;
    }

    private ComparablePropertyInformation mapComparableProperty(ResultSet rs) throws SQLException {

        ComparablePropertyInformation info = new ComparablePropertyInformation();

        info.setCompNumber(rs.getInt("comp_number"));
        info.setStatus(rs.getString("status"));
        info.setAddress(rs.getString("address"));
        info.setCity(rs.getString("city"));
        info.setState(rs.getString("state"));
        info.setZipcode(rs.getString("zipcode"));
        info.setCounty(rs.getString("county"));
        info.setProximity(Optional.ofNullable(rs.getObject("proximity")).map(o -> ((Number) o).doubleValue()).orElse(null));
        info.setSalePrice(rs.getObject("sale_price", Integer.class));
        info.setPricePerSqFt(rs.getObject("price_per_sqft", Double.class));
        info.setOriginalListingPrice(rs.getObject("original_listing_price", Integer.class));
        info.setCurrentListingPrice(rs.getObject("current_listing_price", Integer.class));
        info.setSaleDate(rs.getString("sale_date"));
        info.setListDate(rs.getString("list_date"));
        info.setDaysOnMarket(rs.getObject("days_on_market", Integer.class));
        info.setMlsID(rs.getString("mls_id"));
        info.setFinancing(rs.getString("financing"));
        info.setSalesConcession(rs.getString("sales_concession"));
        info.setBankOrREOSale(rs.getString("bank_or_reo_sale"));
        info.setLocation(rs.getString("location"));
        info.setSiteOrView(rs.getString("site_or_view"));
        info.setSiteOrLotSize(rs.getObject("site_or_lot_size", Double.class));
        info.setYearBuilt(rs.getObject("year_built", Integer.class));
        info.setConstruction(rs.getString("construction"));
        info.setCondition(rs.getString("condition"));
        info.setStyle(rs.getString("style"));
        info.setTotalRooms(rs.getObject("total_rooms", Integer.class));
        info.setBedrooms(rs.getObject("bedrooms", Integer.class));
        info.setBathrooms(rs.getObject("bathrooms", Double.class));
        info.setGrossLivingArea(rs.getObject("gross_living_area", Integer.class));
        info.setBasementAndFinish(rs.getString("basement_and_finish"));
        info.setHeating(rs.getString("heating"));
        info.setCooling(rs.getString("cooling"));
        info.setGarage(rs.getString("garage"));
        info.setCarport(rs.getString("carport"));
        info.setAdditionalAmenities(rs.getString("additional_amenities"));
        info.setNetAdjustments(rs.getInt("net_adjustments"));

        return info;
    }

    public BrokerPriceOpinionPDFInfoDTO getBrokerPriceOpinionPDFInformation(String propertyID) {

        BrokerPriceOpinionPDFInfoDTO brokerPriceOpinionPDFInfoDTO = new BrokerPriceOpinionPDFInfoDTO();

        brokerPriceOpinionPDFInfoDTO.setPropertyID(propertyID);

        PropertyDetailReportResponse propertyDetailReportResponse = getPropertyDetailReportByPropertyIDDTAPI(propertyID);

        PropertyDetailReportData propertyDetailReportData = propertyDetailReportResponse.Reports.get(0).Data;

        brokerPriceOpinionPDFInfoDTO.setFullAddress(
                propertyDetailReportData.SubjectProperty.SitusAddress.StreetAddress
                        + ", " +
                propertyDetailReportData.SubjectProperty.SitusAddress.City
                        + ", " +
                propertyDetailReportData.SubjectProperty.SitusAddress.State
                        + " " +
                propertyDetailReportData.SubjectProperty.SitusAddress.Zip9);

        brokerPriceOpinionPDFInfoDTO.setLatitude(propertyDetailReportData.LocationInformation.Latitude);
        brokerPriceOpinionPDFInfoDTO.setLongitude(propertyDetailReportData.LocationInformation.Longitude);

        String queryTargetPropertyByPropertyId = "select " +
                "plfc.metro, plfc.mlsid " +
                "from platlab_listings_full_current plfc " +
                "where plfc.ref_id = '" + propertyID + "' " +
                "order by plfc.modificationtimestamp desc " +
                "limit 1";

        List<ListingDTO> result = prodBackupJdbcTemplate.query(queryTargetPropertyByPropertyId, (rs, rowNum) -> new ListingDTO(
                rs.getString("metro"),
                rs.getString("mlsid")
        ));

        List<Map<String, Object>> targetPropertyInfoPlatlabResult = null;

        if (!result.isEmpty()) {
            String queryTargetPropertyInfoPlatlab = "SELECT " +
                    "lc.address, " +
                    "lc.city, " +
                    "lc.state, " +
                    "lc.zip, " +
                    "lc.county, " +
                    "lc.parcel_number, " +
                    "REPLACE(lc.style, ',', ', ') AS style, " +
                    "CAST(lc.square_feet AS INTEGER) AS square_feet, " +
                    "CAST(lc.bedrooms AS INTEGER) AS bedrooms, " +
                    "CAST(lc.bathrooms_full AS INTEGER) AS bathrooms_full, " +
                    "CAST(lc.bathrooms_half AS INTEGER) AS bathrooms_half, " +
                    "CAST(lc.garage_spaces AS INTEGER) AS garage_spaces, " +
                    "CAST(lc.year_built AS INTEGER) AS year_built, " +
                    "lc.has_view, " +
                    "lc.has_pool, " +
                    "CAST(lc.fireplaces AS INTEGER) AS fireplaces, " +
                    "REPLACE(lc.occupant_type, ',', ', ') AS occupant_type, " +
                    "CASE " +
                    "   WHEN lc.status = 'expired' THEN 'Expired' " +
                    "   WHEN lc.status = 'pending' OR lc.status = 'Contingent' THEN 'Pending' " +
                    "   WHEN lc.status = 'active' THEN 'Active' " +
                    "   WHEN lc.status = 'withdrawn' OR lc.status = 'cancelled' THEN 'Withdrawn' " +
                    "   WHEN lc.status IN ('sold', 'rented', 'off_market') THEN 'Closed' " +
                    "   ELSE NULL " +
                    "END AS status, " +
                    "CAST(lc.price AS INTEGER) AS price, " +
                    "lc.listing_office_name, " +
                    "lc.listing_agent_phone, " +
                    "CAST(lc.annual_tax AS DOUBLE) AS annual_tax, " +
                    "lc.zoning, " +
                    "CASE " +
                    "   WHEN lc.lot_size_display IS NOT NULL AND CAST(lc.lot_size_display AS DOUBLE) != 0 AND lc.lot_size_units = 'Acres' " +
                    "       THEN CAST(lc.lot_size_area AS DOUBLE) "+
                    "   WHEN lc.lot_size_display IS NOT NULL AND CAST(lc.lot_size_display AS DOUBLE) != 0 AND lc.lot_size_units = 'Square Feet' " +
                    "       THEN CAST(lc.lot_size_area AS DOUBLE) / 43560 " +
                    "   WHEN lc.lot_size IS NOT NULL AND CAST(lc.lot_size AS DOUBLE) != 0 AND lc.lot_size_units = 'Acres' " +
                    "       THEN CAST(lc.lot_size_area AS DOUBLE) " +
                    "   WHEN lc.lot_size IS NOT NULL AND CAST(lc.lot_size AS DOUBLE) != 0 AND lc.lot_size_units = 'Square Feet' " +
                    "       THEN CAST(lc.lot_size_area AS DOUBLE) / 43560 " +
                    "   WHEN lc.lot_size_square_feet IS NOT NULL AND CAST(lc.lot_size_square_feet AS DOUBLE) != 0 " +
                    "       THEN CAST(lc.lot_size_square_feet AS DOUBLE) / 43560 " +
                    "   WHEN lc.lot_size_area IS NOT NULL AND CAST(lc.lot_size_area AS DOUBLE) != 0 AND lc.lot_size_units = 'Acres' " +
                    "       THEN CAST(lc.lot_size_area AS DOUBLE) " +
                    "   WHEN lc.lot_size_area IS NOT NULL AND CAST(lc.lot_size_area AS DOUBLE) != 0 AND lc.lot_size_units = 'Square Feet' " +
                    "       THEN CAST(lc.lot_size_area AS DOUBLE) / 43560 " +
                    "   ELSE NULL " +
                    "END AS lot_size " +
                    "FROM iceberg.platlab.listing_current lc " +
                    "WHERE " +
                    "lc.mls_id = '" + result.get(0).getMetro() + "' AND lc.display_mls_number = '"  + result.get(0).getMlsid() + "'";

            targetPropertyInfoPlatlabResult = trinoJdbcTemplate.query(queryTargetPropertyInfoPlatlab, rs -> {
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

        String statusPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (String) targetPropertyInfoPlatlabResult.get(0).get("status")
                : null;

        brokerPriceOpinionPDFInfoDTO.setStatus(statusPlatlabSource);

        OrderInformation orderInformation = new OrderInformation();

        String addressDTAPISource = propertyDetailReportData.SubjectProperty.SitusAddress.StreetAddress;
        String addressPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (String) targetPropertyInfoPlatlabResult.get(0).get("address")
                : null;

        if (addressDTAPISource != null && !addressDTAPISource.isEmpty()) {
            orderInformation.setAddress(addressDTAPISource);
        } else if (addressPlatlabSource != null && !addressPlatlabSource.isEmpty()) {
            orderInformation.setAddress(addressPlatlabSource);
        } else {
            orderInformation.setAddress(null);
        }

        String cityDTAPISource = propertyDetailReportData.SubjectProperty.SitusAddress.City;
        String cityPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (String) targetPropertyInfoPlatlabResult.get(0).get("city")
                : null;

        if (cityDTAPISource != null && !cityDTAPISource.isEmpty()) {
            orderInformation.setCity(cityDTAPISource);
        } else if (cityPlatlabSource != null && !cityPlatlabSource.isEmpty()) {
            orderInformation.setCity(cityPlatlabSource);
        } else {
            orderInformation.setCity(null);
        }

        String stateDTAPISource = propertyDetailReportData.SubjectProperty.SitusAddress.State;
        String statePlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (String) targetPropertyInfoPlatlabResult.get(0).get("state")
                : null;

        if (stateDTAPISource != null && !stateDTAPISource.isEmpty()) {
            orderInformation.setState(stateDTAPISource);
        } else if (statePlatlabSource != null && !statePlatlabSource.isEmpty()) {
            orderInformation.setState(statePlatlabSource);
        } else {
            orderInformation.setState(null);
        }

        String zipcodeDTAPISource = propertyDetailReportData.SubjectProperty.SitusAddress.Zip9;
        String zipcodePlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (String) targetPropertyInfoPlatlabResult.get(0).get("zip")
                : null;

        if (zipcodeDTAPISource != null && !zipcodeDTAPISource.isEmpty()) {
            orderInformation.setZipcode(zipcodeDTAPISource);
        } else if (zipcodePlatlabSource != null && !zipcodePlatlabSource.isEmpty()) {
            orderInformation.setZipcode(zipcodePlatlabSource);
        } else {
            orderInformation.setZipcode(null);
        }

        String countyDTAPISource = propertyDetailReportData.SubjectProperty.SitusAddress.County;
        String countyPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (String) targetPropertyInfoPlatlabResult.get(0).get("county")
                : null;

        if (countyDTAPISource != null && !countyDTAPISource.isEmpty()) {
            orderInformation.setCounty(countyDTAPISource);
        } else if (countyPlatlabSource != null && !countyPlatlabSource.isEmpty()) {
            orderInformation.setCounty(countyPlatlabSource);
        } else {
            orderInformation.setCounty(null);
        }

        String parcelIDPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (String) targetPropertyInfoPlatlabResult.get(0).get("parcel_number")
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
        String stylePlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (String) targetPropertyInfoPlatlabResult.get(0).get("style")
                : null;

        if (styleDTAPISource != null && !styleDTAPISource.isEmpty()) {
            propertyInformation.setPropertyStyle(styleDTAPISource);
        } else if (stylePlatlabSource != null && !stylePlatlabSource.isEmpty()) {
            propertyInformation.setPropertyStyle(stylePlatlabSource);
        } else {
            propertyInformation.setPropertyStyle(null);
        }

        Integer sqftDTAPISource = propertyDetailReportData.PropertyCharacteristics.LivingArea;
        Integer sqftPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (Integer) targetPropertyInfoPlatlabResult.get(0).get("square_feet")
                : null;

        if (sqftDTAPISource != null && sqftDTAPISource != 0) {
            propertyInformation.setSqftGLA(sqftDTAPISource);
        } else if (sqftPlatlabSource != null && sqftPlatlabSource != 0) {
            propertyInformation.setSqftGLA(sqftPlatlabSource);
        } else {
            propertyInformation.setSqftGLA(null);
        }

        Integer totalRoomsDTAPISource = propertyDetailReportData.PropertyCharacteristics.TotalRooms;

        if (totalRoomsDTAPISource != null && totalRoomsDTAPISource != 0) {
            propertyInformation.setTotalRooms(totalRoomsDTAPISource);
        } else {
            propertyInformation.setTotalRooms(null);
        }

        Integer bedroomsDTAPISource = propertyDetailReportData.PropertyCharacteristics.Bedrooms;
        Integer bedroomsPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (Integer) targetPropertyInfoPlatlabResult.get(0).get("bedrooms")
                : null;

        if (bedroomsDTAPISource != null && bedroomsDTAPISource != 0) {
            propertyInformation.setBedrooms(bedroomsDTAPISource);
        } else if (bedroomsPlatlabSource != null && bedroomsPlatlabSource != 0) {
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

        if (targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()) {
            bathroomsFullPlatlabSource = (targetPropertyInfoPlatlabResult.get(0).get("bathrooms_full") != null) ? (int) targetPropertyInfoPlatlabResult.get(0).get("bathrooms_full") : 0;
            bathroomsHalfPlatlabSource = (targetPropertyInfoPlatlabResult.get(0).get("bathrooms_half") != null) ? (int) targetPropertyInfoPlatlabResult.get(0).get("bathrooms_half") : 0;
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
        Integer garageSpacesPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (Integer) targetPropertyInfoPlatlabResult.get(0).get("garage_spaces")
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
                propertyInformation.setGarageSpaces(0);
                propertyInformation.setGarage("No Garage");
            }
        } else {
            propertyInformation.setGarageSpaces(null);
            propertyInformation.setGarage(null);
        }

        Integer yearBuiltDTAPISource = propertyDetailReportData.PropertyCharacteristics.YearBuilt;
        Integer yearBuiltPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (Integer) targetPropertyInfoPlatlabResult.get(0).get("year_built")
                : null;

        if (yearBuiltDTAPISource != null) {
            propertyInformation.setYearBuilt(yearBuiltDTAPISource);
        } else if (yearBuiltPlatlabSource != null) {
            propertyInformation.setYearBuilt(yearBuiltPlatlabSource);
        } else {
            propertyInformation.setYearBuilt(null);
        }

        if (targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()) {
            String hasView = (String) targetPropertyInfoPlatlabResult.get(0).get("has_view");
            propertyInformation.setView("true".equals(hasView) ? "Yes" : "No");
        } else {
            propertyInformation.setView(null);
        }

        String poolDTAPISource = propertyDetailReportData.PropertyCharacteristics.Pool;
        String poolPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? targetPropertyInfoPlatlabResult.get(0).get("has_pool") != null && targetPropertyInfoPlatlabResult.get(0).get("has_pool").equals("true") ? "Yes" : "No"
                : null;

        if (poolDTAPISource != null && !poolDTAPISource.isEmpty()) {
            propertyInformation.setPool(poolDTAPISource);
        } else if (poolPlatlabSource != null) {
            propertyInformation.setPool(poolPlatlabSource);
        } else {
            propertyInformation.setPool(null);
        }

        String porchTypeDTAPI = propertyDetailReportData.PropertyCharacteristics.PorchType;
        String patioTypeDTAPI = propertyDetailReportData.PropertyCharacteristics.PatioType;

        if (porchTypeDTAPI.isEmpty() && patioTypeDTAPI.isEmpty()) {
            propertyInformation.setFeaturePorch("Unk.");
            propertyInformation.setFeaturePatio("Unk.");
            propertyInformation.setFeatureDeck("Unk.");
        } else {
            if (porchTypeDTAPI.contains("porch") || patioTypeDTAPI.contains("porch")) {
                propertyInformation.setFeaturePorch("Yes");
            }
            if (porchTypeDTAPI.contains("patio") || patioTypeDTAPI.contains("patio")) {
                propertyInformation.setFeaturePatio("Yes");
            }
            if (porchTypeDTAPI.contains("deck") || patioTypeDTAPI.contains("deck")) {
                propertyInformation.setFeatureDeck("Yes");
            }
        }

        Integer numberOfFireplacesDTAPISource = propertyDetailReportData.PropertyCharacteristics.FirePlaceCount;
        Integer numberOfFireplacesPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (Integer) targetPropertyInfoPlatlabResult.get(0).get("fireplaces")
                : null;

        if (numberOfFireplacesDTAPISource != null) {
            propertyInformation.setNumberOfFireplaces(numberOfFireplacesDTAPISource);
        } else if (numberOfFireplacesPlatlabSource != null) {
            propertyInformation.setNumberOfFireplaces(numberOfFireplacesPlatlabSource);
        } else {
            propertyInformation.setNumberOfFireplaces(null);
        }

        String conditionDTAPISource = propertyDetailReportData.PropertyCharacteristics.Condition;

        if (conditionDTAPISource != null && !conditionDTAPISource.isEmpty()) {
            propertyInformation.setOverallCondition(conditionDTAPISource);
        } else {
            propertyInformation.setOverallCondition(null);
        }

        String occupancyDTAPISource = propertyDetailReportData.OwnerInformation.Occupancy;
        String occupancyPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (String) targetPropertyInfoPlatlabResult.get(0).get("occupant_type")
                : null;

        if (occupancyDTAPISource != null && !occupancyDTAPISource.isEmpty()) {
            propertyInformation.setOccupancy(occupancyDTAPISource);
        } else if (occupancyPlatlabSource != null && !occupancyPlatlabSource.isEmpty()) {
            propertyInformation.setOccupancy(occupancyPlatlabSource);
        } else {
            propertyInformation.setOccupancy(null);
        }

        String isListedPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? targetPropertyInfoPlatlabResult.get(0).get("status").equals("Active") ? "Yes" : "No"
                : null;

        propertyInformation.setIsListed(isListedPlatlabSource);

        Integer listPricePlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty() && targetPropertyInfoPlatlabResult.get(0).get("status").equals("Active")
                ? (Integer) targetPropertyInfoPlatlabResult.get(0).get("price")
                : null;

        if (listPricePlatlabSource != null && listPricePlatlabSource != 0) {
            propertyInformation.setListPrice(listPricePlatlabSource);
        } else {
            propertyInformation.setListPrice(null);
        }

        String nameOfListingCompanyPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (String) targetPropertyInfoPlatlabResult.get(0).get("listing_office_name")
                : null;

        if (nameOfListingCompanyPlatlabSource != null && !nameOfListingCompanyPlatlabSource.isEmpty()) {
            propertyInformation.setNameOfListingCompany(nameOfListingCompanyPlatlabSource);
        } else {
            propertyInformation.setNameOfListingCompany(null);
        }

        String listingAgentPhonePlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (String) targetPropertyInfoPlatlabResult.get(0).get("listing_agent_phone")
                : null;

        if (listingAgentPhonePlatlabSource != null && !listingAgentPhonePlatlabSource.isEmpty()) {
            propertyInformation.setListingAgentPhone(listingAgentPhonePlatlabSource);
        } else {
            propertyInformation.setListingAgentPhone(null);
        }

        String priorSaleDateDTAPISource = propertyDetailReportData.PriorSaleInformation.PriorSaleDate;

        if (priorSaleDateDTAPISource != null && priorSaleDateDTAPISource.length() >= 10) {
            propertyInformation.setPriorSaleDate(priorSaleDateDTAPISource.substring(0, 10));
        } else {
            propertyInformation.setPriorSaleDate(null);
        }

        Double priorSalePriceDTAPISource = propertyDetailReportData.PriorSaleInformation.PriorSalePrice;

        if (priorSalePriceDTAPISource != null && priorSalePriceDTAPISource != 0.0) {
            propertyInformation.setPriorSalePrice(priorSalePriceDTAPISource);
        } else {
            propertyInformation.setPriorSalePrice(null);
        }

        Double currentTaxDTAPISource = propertyDetailReportData.TaxInformation.PropertyTax;
        Double currentTaxPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (Double) targetPropertyInfoPlatlabResult.get(0).get("annual_tax")
                : null;

        if (currentTaxDTAPISource != null && currentTaxDTAPISource != 0.0) {
            propertyInformation.setCurrentTax(currentTaxDTAPISource);
        } else if (currentTaxPlatlabSource != null && currentTaxPlatlabSource != 0.0) {
            propertyInformation.setCurrentTax(currentTaxPlatlabSource);
        } else {
            propertyInformation.setCurrentTax(null);
        }

        String zoningDTAPISource = propertyDetailReportData.SiteInformation.Zoning;
        String zoningPlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (String) targetPropertyInfoPlatlabResult.get(0).get("zoning")
                : null;

        if (zoningDTAPISource != null && !zoningDTAPISource.isEmpty()) {
            propertyInformation.setZoning(zoningDTAPISource);
        } else if (zoningPlatlabSource != null && !zoningPlatlabSource.isEmpty()) {
            propertyInformation.setZoning(zoningPlatlabSource);
        } else {
            propertyInformation.setZoning(null);
        }

        Double lotSizeDTAPISource = propertyDetailReportData.SiteInformation.Acres;
        Double lotSizePlatlabSource = targetPropertyInfoPlatlabResult != null && !targetPropertyInfoPlatlabResult.isEmpty()
                ? (Double) targetPropertyInfoPlatlabResult.get(0).get("lot_size")
                : null;

        if (lotSizeDTAPISource != null && lotSizeDTAPISource != 0.0) {
            propertyInformation.setLotSize(lotSizeDTAPISource);
        } else if (lotSizePlatlabSource != null && lotSizePlatlabSource != 0.0) {
            propertyInformation.setLotSize(lotSizePlatlabSource);
        } else {
            propertyInformation.setLotSize(null);
        }

        Double landValueDTAPISource = propertyDetailReportData.TaxInformation.LandValue;

        if (landValueDTAPISource != null && landValueDTAPISource != 0.0) {
            propertyInformation.setLandValue(propertyDetailReportData.TaxInformation.LandValue);
        } else {
            propertyInformation.setLandValue(null);
        }

        brokerPriceOpinionPDFInfoDTO.setPropertyInformation(propertyInformation);

        ConditionInformation conditionInformation = new ConditionInformation();

        brokerPriceOpinionPDFInfoDTO.setConditionInformation(conditionInformation);

        NeighborhoodInformation neighborhoodInformation = new NeighborhoodInformation();

        neighborhoodInformation.setLocation(findLocationDensity(
                brokerPriceOpinionPDFInfoDTO.getLongitude(), brokerPriceOpinionPDFInfoDTO.getLatitude())
        );

        brokerPriceOpinionPDFInfoDTO.setNeighborhoodInformation(neighborhoodInformation);

        PropertyDetailReportResponse propertyDetailReportResponseComparableProperty;

        List<ListingDTO> resultCompsListingsSearchClosed;

        String queryCompsClosedPass1 = compsListingsSearchQueryBuilder(propertyID, brokerPriceOpinionPDFInfoDTO, "Closed", 1);

        resultCompsListingsSearchClosed = prodBackupJdbcTemplate.query(queryCompsClosedPass1, (rs, rowNum) -> new ListingDTO(
                rs.getString("metro"),
                rs.getString("mlsid")
        ));

        if (resultCompsListingsSearchClosed.size() < 3) {

            String queryCompsClosedPass2 = compsListingsSearchQueryBuilder(propertyID, brokerPriceOpinionPDFInfoDTO, "Closed", 2);

            resultCompsListingsSearchClosed = prodBackupJdbcTemplate.query(queryCompsClosedPass2, (rs, rowNum) -> new ListingDTO(
                    rs.getString("metro"),
                    rs.getString("mlsid")
            ));
        }

        if (resultCompsListingsSearchClosed.size() < 3) {

            String queryCompsClosedPass3 = compsListingsSearchQueryBuilder(propertyID, brokerPriceOpinionPDFInfoDTO, "Closed", 3);

            resultCompsListingsSearchClosed = prodBackupJdbcTemplate.query(queryCompsClosedPass3, (rs, rowNum) -> new ListingDTO(
                    rs.getString("metro"),
                    rs.getString("mlsid")
            ));
        }

        if (resultCompsListingsSearchClosed.size() < 3) {

            String queryCompsClosedPass4 = compsListingsSearchQueryBuilder(propertyID, brokerPriceOpinionPDFInfoDTO, "Closed", 4);

            resultCompsListingsSearchClosed = prodBackupJdbcTemplate.query(queryCompsClosedPass4, (rs, rowNum) -> new ListingDTO(
                    rs.getString("metro"),
                    rs.getString("mlsid")
            ));
        }

        String queryCompsClosedTrino = compsSearchQueryBuilder(resultCompsListingsSearchClosed, brokerPriceOpinionPDFInfoDTO);

        List<Map<String, Object>> resultCompsClosed = trinoJdbcTemplate.query(queryCompsClosedTrino, rs -> {
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

        List<ComparablePropertyInformation> closedComparablePropertyInformationList = new ArrayList<>();

        for (Map<String, Object> compClosed : resultCompsClosed) {

            propertyDetailReportResponseComparableProperty = getPropertyDetailReportByFullAddressDTAPI(compClosed.get("address") + ", " + compClosed.get("city") + ", " + compClosed.get("state") + " " + compClosed.get("zip") + ", United States");

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

            if (salePriceCPClosedPlatlabSource != null && salePriceCPClosedPlatlabSource != 0) {
                comp.setSalePrice(salePriceCPClosedPlatlabSource);
            } else {
                comp.setSalePrice(null);
            }

            comp.setPricePerSqFt(BigDecimal.valueOf(Double.valueOf((Integer) compClosed.get("price")) / Double.parseDouble(String.valueOf((Integer) compClosed.get("square_feet")))).setScale(2, RoundingMode.HALF_UP).doubleValue());

            Integer originalListingPriceCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Integer) compClosed.get("original_listing_price")
                    : null;

            if (originalListingPriceCPClosedPlatlabSource != null && originalListingPriceCPClosedPlatlabSource != 0) {
                comp.setOriginalListingPrice(originalListingPriceCPClosedPlatlabSource);
            } else {
                comp.setOriginalListingPrice(null);
            }

            Integer currentListingPriceCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Integer) compClosed.get("price")
                    : null;

            if (currentListingPriceCPClosedPlatlabSource != null && currentListingPriceCPClosedPlatlabSource != 0) {
                comp.setCurrentListingPrice(currentListingPriceCPClosedPlatlabSource);
            } else {
                comp.setCurrentListingPrice(null);
            }

            String saleDateCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (String) compClosed.get("sold_date")
                    : null;

            if (saleDateCPClosedPlatlabSource != null && !saleDateCPClosedPlatlabSource.isEmpty()) {
                comp.setSaleDate(saleDateCPClosedPlatlabSource);
            } else {
                comp.setSaleDate(null);
            }

            String listDateCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (String) compClosed.get("mls_list_date")
                    : null;

            if (listDateCPClosedPlatlabSource != null && !listDateCPClosedPlatlabSource.isEmpty()) {
                comp.setListDate(listDateCPClosedPlatlabSource);
            } else {
                comp.setListDate(null);
            }

            Integer daysOnMarketCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Integer) compClosed.get("days_on_market")
                    : null;

            if (daysOnMarketCPClosedPlatlabSource != null) {
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
            Double siteORLotSizeCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Double) compClosed.get("lot_size")
                    : null;

            if (siteORLotSizeCPClosedDTAPISource != null && siteORLotSizeCPClosedDTAPISource != 0.0) {
                comp.setSiteOrLotSize(siteORLotSizeCPClosedDTAPISource);
            } else if (siteORLotSizeCPClosedPlatlabSource != null && siteORLotSizeCPClosedPlatlabSource != 0.0) {
                comp.setSiteOrLotSize(siteORLotSizeCPClosedPlatlabSource);
            } else {
                comp.setSiteOrLotSize(null);
            }

            Integer yearBuiltCPClosedDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.YearBuilt;
            Integer yearBuiltCPClosedPlatlabSource = compClosed != null && !compClosed.isEmpty()
                    ? (Integer) compClosed.get("year_built")
                    : null;

            if (yearBuiltCPClosedDTAPISource != null && yearBuiltCPClosedDTAPISource != 0) {
                comp.setYearBuilt(yearBuiltCPClosedDTAPISource);
            } else if (yearBuiltCPClosedPlatlabSource != null && yearBuiltCPClosedPlatlabSource != 0) {
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

            Integer totalRoomsCPClosedDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.TotalRooms;

            if (totalRoomsCPClosedDTAPISource != null && totalRoomsCPClosedDTAPISource != 0) {
                propertyInformation.setTotalRooms(totalRoomsCPClosedDTAPISource);
            } else {
                propertyInformation.setTotalRooms(null);
            }

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
                comp.setBasementAndFinish(hasBasementCPClosedPlatlabSource ? "Yes" : "No");
            } else if (basementAreaCPClosedDTAPISource != null) {
                comp.setBasementAndFinish(basementAreaCPClosedDTAPISource > 0 ? "Yes" : "No");
            } else {
                comp.setBasementAndFinish("Unk.");
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

            if (sqftCPClosedDTAPISource != null && sqftCPClosedDTAPISource != 0) {
                comp.setGrossLivingArea(sqftCPClosedDTAPISource);
            } else if (sqftCPClosedPlatlabSource != null && sqftCPClosedPlatlabSource != 0) {
                comp.setGrossLivingArea(sqftCPClosedPlatlabSource);
            } else {
                comp.setGrossLivingArea(null);
            }

            closedComparablePropertyInformationList.add(comp);

            if (closedComparablePropertyInformationList.size() == 3) {
                break;
            }
        }

        brokerPriceOpinionPDFInfoDTO.setClosedComparablePropertyInformationList(closedComparablePropertyInformationList);

        List<ListingDTO> resultCompsListingsSearchActive;

        String queryCompsActivePass1 = compsListingsSearchQueryBuilder(propertyID, brokerPriceOpinionPDFInfoDTO, "Active", 1);

        resultCompsListingsSearchActive = prodBackupJdbcTemplate.query(queryCompsActivePass1, (rs, rowNum) -> new ListingDTO(
                rs.getString("metro"),
                rs.getString("mlsid")
        ));

        if (resultCompsListingsSearchActive.size() < 3) {

            String queryCompsActivePass2 = compsListingsSearchQueryBuilder(propertyID, brokerPriceOpinionPDFInfoDTO, "Active", 2);

            resultCompsListingsSearchActive = prodBackupJdbcTemplate.query(queryCompsActivePass2, (rs, rowNum) -> new ListingDTO(
                    rs.getString("metro"),
                    rs.getString("mlsid")
            ));
        }

        if (resultCompsListingsSearchActive.size() < 3) {

            String queryCompsActivePass3 = compsListingsSearchQueryBuilder(propertyID, brokerPriceOpinionPDFInfoDTO, "Active", 3);

            resultCompsListingsSearchActive = prodBackupJdbcTemplate.query(queryCompsActivePass3, (rs, rowNum) -> new ListingDTO(
                    rs.getString("metro"),
                    rs.getString("mlsid")
            ));
        }

        if (resultCompsListingsSearchActive.size() < 3) {

            String queryCompsActivePass4 = compsListingsSearchQueryBuilder(propertyID, brokerPriceOpinionPDFInfoDTO, "Active", 4);

            resultCompsListingsSearchActive = prodBackupJdbcTemplate.query(queryCompsActivePass4, (rs, rowNum) -> new ListingDTO(
                    rs.getString("metro"),
                    rs.getString("mlsid")
            ));
        }

        String queryCompsActiveTrino = compsSearchQueryBuilder(resultCompsListingsSearchActive, brokerPriceOpinionPDFInfoDTO);

        List<Map<String, Object>> resultCompsActive = trinoJdbcTemplate.query(queryCompsActiveTrino, rs -> {
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

        List<ComparablePropertyInformation> activeComparablePropertyInformationList = new ArrayList<>();

        for (Map<String, Object> compActive : resultCompsActive) {

            propertyDetailReportResponseComparableProperty = getPropertyDetailReportByFullAddressDTAPI(compActive.get("address") + ", " + compActive.get("city") + ", " + compActive.get("state") + " " + compActive.get("zip") + ", United States");

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

            if (salePriceCPActivePlatlabSource != null && salePriceCPActivePlatlabSource != 0) {
                comp.setSalePrice(salePriceCPActivePlatlabSource);
            } else {
                comp.setSalePrice(null);
            }

            comp.setPricePerSqFt(BigDecimal.valueOf(Double.valueOf((Integer) compActive.get("price")) / Double.parseDouble(String.valueOf((Integer) compActive.get("square_feet")))).setScale(2, RoundingMode.HALF_UP).doubleValue());

            Integer originalListingPriceCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Integer) compActive.get("original_listing_price")
                    : null;

            if (originalListingPriceCPClosedPlatlabSource != null && originalListingPriceCPClosedPlatlabSource != 0) {
                comp.setOriginalListingPrice(originalListingPriceCPClosedPlatlabSource);
            } else {
                comp.setOriginalListingPrice(null);
            }

            Integer currentListingPriceCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Integer) compActive.get("price")
                    : null;

            if (currentListingPriceCPClosedPlatlabSource != null && currentListingPriceCPClosedPlatlabSource != 0) {
                comp.setCurrentListingPrice(currentListingPriceCPClosedPlatlabSource);
            } else {
                comp.setCurrentListingPrice(null);
            }

            String saleDateCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (String) compActive.get("sold_date")
                    : null;

            if (saleDateCPClosedPlatlabSource != null && !saleDateCPClosedPlatlabSource.isEmpty()) {
                comp.setSaleDate(saleDateCPClosedPlatlabSource);
            } else {
                comp.setSaleDate(null);
            }

            String listDateCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (String) compActive.get("mls_list_date")
                    : null;

            if (listDateCPClosedPlatlabSource != null && !listDateCPClosedPlatlabSource.isEmpty()) {
                comp.setListDate(listDateCPClosedPlatlabSource);
            } else {
                comp.setListDate(null);
            }

            Integer daysOnMarketCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Integer) compActive.get("days_on_market")
                    : null;

            if (daysOnMarketCPClosedPlatlabSource != null) {
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
            Double siteORLotSizeCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Double) compActive.get("lot_size")
                    : null;

            if (siteORLotSizeCPClosedDTAPISource != null && siteORLotSizeCPClosedDTAPISource != 0.0) {
                comp.setSiteOrLotSize(siteORLotSizeCPClosedDTAPISource);
            } else if (siteORLotSizeCPClosedPlatlabSource != null && siteORLotSizeCPClosedPlatlabSource != 0.0) {
                comp.setSiteOrLotSize(siteORLotSizeCPClosedPlatlabSource);
            } else {
                comp.setSiteOrLotSize(null);
            }

            Integer yearBuiltCPClosedDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.YearBuilt;
            Integer yearBuiltCPClosedPlatlabSource = compActive != null && !compActive.isEmpty()
                    ? (Integer) compActive.get("year_built")
                    : null;

            if (yearBuiltCPClosedDTAPISource != null && yearBuiltCPClosedDTAPISource != 0) {
                comp.setYearBuilt(yearBuiltCPClosedDTAPISource);
            } else if (yearBuiltCPClosedPlatlabSource != null && yearBuiltCPClosedPlatlabSource != 0) {
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

            Integer totalRoomsCPActiveDTAPISource = propertyDetailReportDataCP.PropertyCharacteristics.TotalRooms;

            if (totalRoomsCPActiveDTAPISource != null && totalRoomsCPActiveDTAPISource != 0) {
                propertyInformation.setTotalRooms(totalRoomsCPActiveDTAPISource);
            } else {
                propertyInformation.setTotalRooms(null);
            }

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
                comp.setBasementAndFinish(hasBasementCPActivePlatlabSource ? "Yes" : "No");
            } else if (basementAreaCPActiveDTAPISource != null) {
                comp.setBasementAndFinish(basementAreaCPActiveDTAPISource > 0 ? "Yes" : "No");
            } else {
                comp.setBasementAndFinish("Unk.");
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

            if (sqftCPActiveDTAPISource != null && sqftCPActiveDTAPISource != 0) {
                comp.setGrossLivingArea(sqftCPActiveDTAPISource);
            } else if (sqftCPActivePlatlabSource != null && sqftCPActivePlatlabSource != 0) {
                comp.setGrossLivingArea(sqftCPActivePlatlabSource);
            } else {
                comp.setGrossLivingArea(null);
            }

            activeComparablePropertyInformationList.add(comp);

            if (activeComparablePropertyInformationList.size() == 3) {
                break;
            }
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

    public PropertyDetailReportResponse getPropertyDetailReportByFullAddressDTAPI(String fullAddress) {

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

    public PropertyDetailReportResponse getPropertyDetailReportByPropertyIDDTAPI(String propertyID) {

        Map<String, Object> body = new HashMap<>();
        body.put("ProductNames", Collections.singletonList("PropertyDetailReport"));
        body.put("SearchType", "PROPERTY");
        body.put("PropertyID", propertyID);

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

    public String compsListingsSearchQueryBuilder(String propertyID,
                                                  BrokerPriceOpinionPDFInfoDTO brokerPriceOpinionPDFInfoDTO,
                                                  String status, Integer level) {

        int bedroomsAdjustedValue = 0;
        int bathroomsAdjustedValue = 0;
        double squareFeetAdjustedValueLow = 0.0;
        double squareFeetAdjustedValueHigh = 0.0;
        double lotSizeAdjustedValueLow = 0.0;
        double lotSizeAdjustedValueHigh = 0.0;
        int garageSpacesAdjustedValue = 0;
        int yearBuiltAdjustedValue = 0;
        int dateAdjustedValue = 0;
        double distance = 0;

        if (level == 1) {
            bedroomsAdjustedValue = 0;
            bathroomsAdjustedValue = 0;
            squareFeetAdjustedValueLow = 0.95;
            squareFeetAdjustedValueHigh = 1.05;
            lotSizeAdjustedValueLow = 0.9;
            lotSizeAdjustedValueHigh = 1.1;
            garageSpacesAdjustedValue = 0;
            yearBuiltAdjustedValue = 10;
            dateAdjustedValue = 60;
            distance = 0.5;
        }

        if (level == 2) {
            bedroomsAdjustedValue = 1;
            bathroomsAdjustedValue = 1;
            squareFeetAdjustedValueLow = 0.9;
            squareFeetAdjustedValueHigh = 1.1;
            lotSizeAdjustedValueLow = 0.8;
            lotSizeAdjustedValueHigh = 1.2;
            garageSpacesAdjustedValue = 1;
            yearBuiltAdjustedValue = 20;
            dateAdjustedValue = 90;
            distance = 1.5;
        }

        if (level == 3) {
            bedroomsAdjustedValue = 2;
            bathroomsAdjustedValue = 2;
            squareFeetAdjustedValueLow = 0.8;
            squareFeetAdjustedValueHigh = 1.2;
            lotSizeAdjustedValueLow = 0.7;
            lotSizeAdjustedValueHigh = 1.3;
            garageSpacesAdjustedValue = 2;
            yearBuiltAdjustedValue = 20;
            dateAdjustedValue = 180;
            distance = 5;
        }

        if (level == 4) {
            bedroomsAdjustedValue = 3;
            bathroomsAdjustedValue = 3;
            squareFeetAdjustedValueLow = 0.7;
            squareFeetAdjustedValueHigh = 1.3;
            lotSizeAdjustedValueLow = 0.7;
            lotSizeAdjustedValueHigh = 1.3;
            garageSpacesAdjustedValue = 2;
            yearBuiltAdjustedValue = 30;
            dateAdjustedValue = 360;
            distance = 20;
        }

        StringBuilder query = new StringBuilder();

        query.append("select ").append("plfc.metro, ").append("plfc.mlsid ")
                .append("from platlab_listings_full_current plfc ")
                .append("where ST_Within(plfc.geometry, ST_Buffer(ST_MakePoint(").append(brokerPriceOpinionPDFInfoDTO.getLongitude()).append(", ").append(brokerPriceOpinionPDFInfoDTO.getLatitude()).append(")::geography, 1609.34 * ").append(distance).append(")::geometry) ")
                .append("and plfc.status = '").append(status).append("' ")
                .append("AND CURRENT_DATE - COALESCE(plfc.closedate::date, plfc.contractdate::date) <= ").append(dateAdjustedValue);

        if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms() != null) {
            int bedrooms = brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBedrooms();
            query.append("and plfc.bed between ")
                    .append(bedrooms - bedroomsAdjustedValue)
                    .append(" and ")
                    .append(bedrooms + bedroomsAdjustedValue)
                    .append(" ");
        }

        if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms() != null) {
            double bathrooms = brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getBathrooms();
            query.append("and plfc.bath between ")
                    .append(bathrooms - bathroomsAdjustedValue)
                    .append(" and ")
                    .append(bathrooms + bathroomsAdjustedValue)
                    .append(" ");
        }

        if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA() != null) {
            int sqft = brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getSqftGLA();
            query.append("and plfc.size between ")
                    .append(sqft * squareFeetAdjustedValueLow)
                    .append(" and ")
                    .append(sqft * squareFeetAdjustedValueHigh)
                    .append(" ");
        }

        if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize() != null) {
            double lotSize = brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getLotSize();
            query.append("and plfc.lot_size between ")
                    .append(lotSize * lotSizeAdjustedValueLow)
                    .append(" * 43560 and ")
                    .append(lotSize * lotSizeAdjustedValueHigh)
                    .append(" * 43560 ");
        }

        if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces() != null) {
            int garageSpaces = brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getGarageSpaces();
            query.append("and plfc.garage::integer between ")
                    .append(garageSpaces - garageSpacesAdjustedValue)
                    .append(" and ")
                    .append(garageSpaces + garageSpacesAdjustedValue)
                    .append(" ");
        }

        if (brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt() != null) {
            int yearBuilt = brokerPriceOpinionPDFInfoDTO.getPropertyInformation().getYearBuilt();
            query.append("and plfc.yearbuilt between ")
                    .append(yearBuilt - yearBuiltAdjustedValue)
                    .append(" and ")
                    .append(yearBuilt + yearBuiltAdjustedValue)
                    .append(" ");
        }

        query.append("and plfc.ref_id != '" + propertyID + "' and plfc.propertytype = 'Residential'");

        return query.toString();
    }

    public String compsSearchQueryBuilder(List<ListingDTO> listingDTOList, BrokerPriceOpinionPDFInfoDTO brokerPriceOpinionPDFInfoDTO) {

        StringBuilder query = new StringBuilder();

        query.append("SELECT ")
                .append("lc.address, ")
                .append("lc.city, ")
                .append("lc.state, ")
                .append("lc.zip, ")
                .append("lc.county, ")
                .append("ROUND(7917.511382 * ASIN(SQRT(POWER(SIN(RADIANS((CAST(lc.latitude AS DOUBLE) - ").append(brokerPriceOpinionPDFInfoDTO.getLatitude()).append(") / 2)), 2) + COS(RADIANS(").append(brokerPriceOpinionPDFInfoDTO.getLatitude()).append(")) * COS(RADIANS(CAST(lc.latitude AS DOUBLE))) * POWER(SIN(RADIANS((CAST(lc.longitude AS DOUBLE) - ").append(brokerPriceOpinionPDFInfoDTO.getLongitude()).append(") / 2)), 2))), 2) AS proximity, ")
                .append("CAST(lc.sold_price AS INTEGER) AS sold_price, ")
                .append("CAST(lc.original_listing_price AS INTEGER) AS original_listing_price, ")
                .append("CAST(lc.price AS INTEGER) AS price, ")
                .append("SUBSTRING(lc.sold_date, 1, 10) AS sold_date, ")
                .append("SUBSTRING(lc.mls_list_date, 1, 10) AS mls_list_date, ")
                .append("CAST(DATE_DIFF('day', TRY_CAST(SUBSTRING(lc.mls_list_date, 1, 10) AS DATE), COALESCE(TRY_CAST(SUBSTRING(lc.sold_date, 1, 10) AS DATE), CURRENT_DATE)) AS INTEGER) AS days_on_market, ")
                .append("lc.display_mls_number, ")
                .append("lc.longitude, ")
                .append("lc.latitude, ")
                .append("CASE")
                .append("    WHEN lc.lot_size_display IS NOT NULL AND TRY_CAST(lc.lot_size_display AS DOUBLE) != 0 AND lc.lot_size_units = 'Acres'")
                .append("        THEN ROUND(CAST(lc.lot_size_area AS DOUBLE), 2)")
                .append("    WHEN lc.lot_size_display IS NOT NULL AND TRY_CAST(lc.lot_size_display AS DOUBLE) != 0 AND lc.lot_size_units = 'Square Feet'")
                .append("        THEN ROUND(CAST(lc.lot_size_area AS DOUBLE) / 43560, 2)")
                .append("    WHEN lc.lot_size IS NOT NULL AND TRY_CAST(lc.lot_size AS DOUBLE) != 0 AND lc.lot_size_units = 'Acres'")
                .append("        THEN ROUND(CAST(lc.lot_size_area AS DOUBLE), 2)")
                .append("    WHEN lc.lot_size IS NOT NULL AND TRY_CAST(lc.lot_size AS DOUBLE) != 0 AND lc.lot_size_units = 'Square Feet'")
                .append("        THEN ROUND(CAST(lc.lot_size_area AS DOUBLE) / 43560, 2)")
                .append("    WHEN lc.lot_size_square_feet IS NOT NULL AND TRY_CAST(lc.lot_size_square_feet AS DOUBLE) != 0")
                .append("        THEN ROUND(CAST(lc.lot_size_square_feet AS DOUBLE) / 43560, 2)")
                .append("    WHEN lc.lot_size_area IS NOT NULL AND TRY_CAST(lc.lot_size_area AS DOUBLE) != 0 AND lc.lot_size_units = 'Acres'")
                .append("        THEN ROUND(CAST(lc.lot_size_area AS DOUBLE), 2)")
                .append("    WHEN lc.lot_size_area IS NOT NULL AND TRY_CAST(lc.lot_size_area AS DOUBLE) != 0 AND lc.lot_size_units = 'Square Feet'")
                .append("        THEN ROUND(CAST(lc.lot_size_area AS DOUBLE) / 43560, 2)")
                .append("    ELSE NULL ")
                .append("END AS lot_size, ")
                .append("CAST(lc.year_built AS INTEGER) AS year_built, ")
                .append("REPLACE(lc.style, ',', ', ') AS style, ")
                .append("CAST(lc.bedrooms AS INTEGER) AS bedrooms, ")
                .append("CAST(lc.bathrooms_full AS INTEGER) AS bathrooms_full, ")
                .append("CAST(lc.bathrooms_half AS INTEGER) AS bathrooms_half, ")
                .append("CAST(lc.square_feet AS INTEGER) AS square_feet, ")
                .append("CAST(lc.has_basement AS BOOLEAN) AS has_basement, ")
                .append("CAST(lc.garage_spaces AS INTEGER) AS garage_spaces ")
                .append("FROM iceberg.platlab.listing_current lc ");

        if (listingDTOList != null && !listingDTOList.isEmpty()) {
            query.append("WHERE ");
            for (int i = 0; i < listingDTOList.size(); i++) {
                ListingDTO dto = listingDTOList.get(i);
                query.append("(lc.mls_id = '").append(dto.getMetro()).append("' AND lc.display_mls_number = '").append(dto.getMlsid()).append("')");
                if (i < listingDTOList.size() - 1) {
                    query.append(" OR ");
                }
            }
        }

        return query.toString();
    }
}