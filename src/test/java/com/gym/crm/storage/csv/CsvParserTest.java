package com.gym.crm.storage.csv;

import com.gym.crm.storage.csv.dto.TraineeCsvDto;
import com.gym.crm.storage.csv.dto.TrainerCsvDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CsvParserTest {

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private CsvParser csvParser;

    @BeforeEach
    public void setUp() {
        csvParser.setResourceLoader(resourceLoader);
    }

    @Test
    public void shouldParseValidCsvFileWithSingleRow() throws IOException {
        String csvContent = """
                username,firstName,lastName,password,isActive,address,dateOfBirth
                john.doe,John,Doe,password123,true,123 Main St,1990-05-15""";
        Resource mockResource = createMockResource(csvContent);
        when(resourceLoader.getResource("classpath:trainee.csv")).thenReturn(mockResource);

        List<TraineeCsvDto> result = csvParser.parseCsv("classpath:trainee.csv", TraineeCsvDto.class);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john.doe", result.get(0).getUsername());
        assertEquals("John", result.get(0).getFirstName());
        assertEquals("Doe", result.get(0).getLastName());
        assertEquals("password123", result.get(0).getPassword());
        assertTrue(result.get(0).getIsActive());
        assertEquals("123 Main St", result.get(0).getAddress());
        assertEquals(LocalDate.of(1990, 5, 15), result.get(0).getDateOfBirth());
        verify(resourceLoader).getResource("classpath:trainee.csv");
    }

    @Test
    public void shouldParseValidCsvFileWithMultipleRows() throws IOException {
        String csvContent = """
                username,firstName,lastName,password,isActive,address,dateOfBirth
                john.doe,John,Doe,password123,true,123 Main St,1990-05-15
                jane.smith,Jane,Smith,password456,true,456 Oak Ave,1985-08-20
                bob.wilson,Bob,Wilson,password789,false,789 Pine Rd,1992-11-30""";
        Resource mockResource = createMockResource(csvContent);
        when(resourceLoader.getResource("classpath:trainee.csv")).thenReturn(mockResource);

        List<TraineeCsvDto> result = csvParser.parseCsv("classpath:trainee.csv", TraineeCsvDto.class);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("john.doe", result.get(0).getUsername());
        assertEquals("jane.smith", result.get(1).getUsername());
        assertEquals("bob.wilson", result.get(2).getUsername());
    }

    @Test
    public void shouldParseTrainerCsvFile() throws IOException {
        String csvContent = """
                username,firstName,lastName,password,isActive,specializationType
                mike.trainer,Mike,Trainer,trainerpass1,true,CARDIO""";
        Resource mockResource = createMockResource(csvContent);
        when(resourceLoader.getResource("classpath:trainer.csv")).thenReturn(mockResource);

        List<TrainerCsvDto> result = csvParser.parseCsv("classpath:trainer.csv", TrainerCsvDto.class);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("mike.trainer", result.get(0).getUsername());
        assertEquals("Mike", result.get(0).getFirstName());
        assertEquals("CARDIO", result.get(0).getSpecializationType());
    }

    @Test
    public void shouldParseEmptyCsvFileWithHeaderOnly() throws IOException {
        String csvContent = "username,firstName,lastName,password,isActive,address,dateOfBirth";
        Resource mockResource = createMockResource(csvContent);
        when(resourceLoader.getResource("classpath:empty.csv")).thenReturn(mockResource);

        List<TraineeCsvDto> result = csvParser.parseCsv("classpath:empty.csv", TraineeCsvDto.class);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenCsvFileIsEmpty() throws IOException {
        Resource mockResource = createMockResource("");
        when(resourceLoader.getResource("classpath:empty.csv")).thenReturn(mockResource);

        assertThrows(
                IllegalStateException.class,
                () -> csvParser.parseCsv("classpath:emty.csv", TraineeCsvDto.class)
        );
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenFileExtensionIsNotCsv() {
        assertThrows(
                IllegalStateException.class,
                () -> csvParser.parseCsv("classpath:data.txt", TraineeCsvDto.class)
        );
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenFileExtensionIsUpperCase() {
        assertThrows(
                IllegalStateException.class,
                () -> csvParser.parseCsv("classpath:data.CSV", TraineeCsvDto.class)
        );
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenFileHasNoExtension() {
        assertThrows(
                IllegalStateException.class,
                () -> csvParser.parseCsv("classpath:data", TraineeCsvDto.class)
        );
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenFileNotFound() {
        Resource mockResource = mock(Resource.class);
        when(mockResource.exists()).thenReturn(false);
        when(resourceLoader.getResource("classpath:nonexistent.csv")).thenReturn(mockResource);

        assertThrows(
                IllegalStateException.class,
                () -> csvParser.parseCsv("classpath:nonexistent.csv", TraineeCsvDto.class)
        );

        verify(resourceLoader).getResource("classpath:nonexistent.csv");
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenResourceLoaderThrowsIOException() throws IOException {
        Resource mockResource = mock(Resource.class);
        when(mockResource.exists()).thenReturn(true);
        when(mockResource.getInputStream()).thenThrow(new IOException("Failed to read file"));
        when(resourceLoader.getResource("classpath:error.csv")).thenReturn(mockResource);

        assertThrows(
                IllegalStateException.class,
                () -> csvParser.parseCsv("classpath:error.csv", TraineeCsvDto.class)
        );
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenCsvDataIsMalformed() throws IOException {
        String csvContent = """
                username,firstName,lastName,password,isActive,address,dateOfBirth
                john.doe,John,Doe,password123,INVALID_BOOLEAN,123 Main St,1990-05-15""";
        Resource mockResource = createMockResource(csvContent);
        when(resourceLoader.getResource("classpath:malformed.csv")).thenReturn(mockResource);

        assertThrows(
                IllegalStateException.class,
                () -> csvParser.parseCsv("classpath:malformed.csv", TraineeCsvDto.class)
        );
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenCsvHasInvalidDateFormat() throws IOException {
        String csvContent = """
                username,firstName,lastName,password,isActive,address,dateOfBirth
                john.doe,John,Doe,password123,true,123 Main St,INVALID_DATE""";
        Resource mockResource = createMockResource(csvContent);
        when(resourceLoader.getResource("classpath:invalid-date.csv")).thenReturn(mockResource);

        assertThrows(
                IllegalStateException.class,
                () -> csvParser.parseCsv("classpath:invalid-date.csv", TraineeCsvDto.class)
        );
    }

    @Test
    public void shouldHandleDifferentFilePathFormats() throws IOException {
        String csvContent = """
                username,firstName,lastName,password,isActive,address,dateOfBirth
                john.doe,John,Doe,password123,true,123 Main St,1990-05-15""";
        Resource mockResource = createMockResource(csvContent);
        when(resourceLoader.getResource("file:/data/trainee.csv")).thenReturn(mockResource);

        List<TraineeCsvDto> result = csvParser.parseCsv("file:/data/trainee.csv", TraineeCsvDto.class);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(resourceLoader).getResource("file:/data/trainee.csv");
    }

    private Resource createMockResource(String csvContent) throws IOException {
        Resource resource = mock(Resource.class);
        when(resource.exists()).thenReturn(true);
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        when(resource.getInputStream()).thenReturn(inputStream);
        return resource;
    }

}
