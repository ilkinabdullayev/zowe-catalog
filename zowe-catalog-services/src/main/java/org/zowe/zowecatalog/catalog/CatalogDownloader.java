package org.zowe.zowecatalog.catalog;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import org.zowe.zowecatalog.github.GithubService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class CatalogDownloader {

    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String FORMAT_CATALOG_FILE = "%s/src/main/resources/static/catalogs/zowe-catalog-v%s.json";

    private String targetProjectDirectory = USER_DIR;

    private final CatalogFetcher catalogFetcher;
    private final ObjectMapper objectMapper;

    public CatalogDownloader() {
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        GithubService zoweGithubService = new GithubService(new RestTemplate(), objectMapper);
        this.catalogFetcher = new CatalogFetcher(zoweGithubService, null, objectMapper);

        if (!targetProjectDirectory.contains("zowe-catalog-services")) {
            targetProjectDirectory += File.separator + "zowe-catalog-services";
        }
    }

    public static void main(String[] args) {
        CatalogDownloader catalogDownloader= new CatalogDownloader();
        catalogDownloader.run();
    }

    public void run() {
        log.info("Catalog downloader started.");

        catalogFetcher.getAllCatalogsFromGithub().forEach(this::writeToFile);

        log.info("Catalog downloader stopped.");
    }

    private void writeToFile(Catalog catalog) {
        String filePath = String.format(FORMAT_CATALOG_FILE, targetProjectDirectory, catalog.getVersion());

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(objectMapper.writeValueAsString(catalog));
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
