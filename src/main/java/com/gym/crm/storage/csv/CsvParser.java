package com.gym.crm.storage.csv;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

@Component
public class CsvParser {

    private static final String CSV_FILE_EXTENSION = ".csv";

    private ResourceLoader resourceLoader;

    @Autowired
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public <T> List<T> parseCsv(String filePath, Class<T> dtoClass) {
        if (!filePath.endsWith(CSV_FILE_EXTENSION)) {
            throw new IllegalStateException("File must be CSV");
        }

        try (Reader reader = openReader(filePath)) {
            return new CsvToBeanBuilder<T>(reader)
                    .withType(dtoClass)
                    .withThrowExceptions(true)
                    .build()
                    .parse();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse CSV file: " + filePath, e);
        }
    }

    private Reader openReader(String filePath) throws IOException {
        Resource resource = resourceLoader.getResource(filePath);
        if (!resource.exists()) {
            throw new IllegalStateException("CSV file not found: " + filePath);
        }

        return new BufferedReader(new InputStreamReader(resource.getInputStream()));
    }

}
