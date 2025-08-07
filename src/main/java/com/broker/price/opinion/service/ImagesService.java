package com.broker.price.opinion.service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ImagesService {

    private final JdbcTemplate trinoJdbcTemplate;

    @Autowired
    public ImagesService(
            @Qualifier("trinoJdbcTemplate") JdbcTemplate trinoJdbcTemplate) {
        this.trinoJdbcTemplate = trinoJdbcTemplate;
    }

    static AmazonS3 amazonS3;

    @Value("${aws.access.key}")
    private String AWS_ACCESS_KEY;

    @Value("${aws.secret.key}")
    private String AWS_SECRET_KEY;

    @Value("${aws.region}")
    private String AWS_Region;

    @PostConstruct
    private void initS3Client() {
        BasicAWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
        amazonS3 = AmazonS3Client.builder().withRegion(Regions.fromName(AWS_Region))
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }

    public List<String> imageURLsPlatlabAWSS3(String mls_id, String display_mls_number) {

        List<String> output = new ArrayList<>();

        ObjectListing listing = amazonS3.listObjects("platlab", "images-all/" + mls_id + "/" + display_mls_number);

        if (listing != null) {
            List<S3ObjectSummary> s3ObjectSummariesList = listing.getObjectSummaries();
            if (!s3ObjectSummariesList.isEmpty()) {
                for (S3ObjectSummary objectSummary : s3ObjectSummariesList) {
                    output.add("https://platlab.s3.us-east-2.amazonaws.com/" + objectSummary.getKey());
                }
            }
        }

        return output;
    }

    public List<String> imageURLsPlatlabTrinoServer(String mls_id, String display_mls_number) {

        String sql = "SELECT source_url FROM iceberg.platlab.mls_image_updates " +
                "WHERE mls_id = ? AND mls_listing_id = ? " +
                "ORDER BY sequence_number";

        return trinoJdbcTemplate.queryForList(sql, String.class, mls_id, display_mls_number);
    }

    public String getFirstImagePlatlabTrinoServerANDPlatlabAWSS3(String mls_id, String display_mls_number) {

        String sql = "SELECT source_url FROM iceberg.platlab.mls_image_updates " +
                "WHERE mls_id = ? AND mls_listing_id = ? " +
                "ORDER BY sequence_number LIMIT 1";

        String sourceURL = null;

        try {
            sourceURL = trinoJdbcTemplate.queryForObject(sql, String.class, mls_id, display_mls_number);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }

        if (sourceURL == null) {
            return null;
        }

        String filenameWithoutExtension = sourceURL.substring(sourceURL.lastIndexOf('/') + 1, sourceURL.lastIndexOf('.'));

        String prefix = "images-all/" + mls_id + "/" + display_mls_number;
        ObjectListing listing = amazonS3.listObjects("platlab", prefix);

        if (listing == null || listing.getObjectSummaries().isEmpty()) {
            return null;
        }

        for (S3ObjectSummary summary : listing.getObjectSummaries()) {
            String key = summary.getKey();
            String s3Filename = key.substring(key.lastIndexOf('/') + 1);
            String s3NameWithoutExt = s3Filename.contains(".") ? s3Filename.substring(0, s3Filename.lastIndexOf('.')) : s3Filename;
            if (s3NameWithoutExt.equalsIgnoreCase(filenameWithoutExtension)) {
                return "https://platlab.s3.us-east-2.amazonaws.com/" + key;
            }
        }

        return null;
    }
}