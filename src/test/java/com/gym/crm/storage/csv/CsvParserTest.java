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
class CsvParserTest {

    @Mock
    private ResourceLoader resourceLoader;

    @InjectMocks
    private CsvParser parser;

    @BeforeEach
    void setUp() {
        parser.setResourceLoader(resourceLoader);
    }

    @Test
    void shouldParseValidCsvFileWithSingleRow() throws IOException {
        String csvContent = """
                username,firstName,lastName,password,isActive,address,dateOfBirth
                liam.miller,Liam,Miller,password123,true,123 Main St,1990-05-15""";
        Resource mockResource = createMockResource(csvContent);

        when(resourceLoader.getResource("classpath:trainee.csv")).thenReturn(mockResource);

        List<TraineeCsvDto> result = parser.parseCsv("classpath:trainee.csv", TraineeCsvDto.class);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("liam.miller", result.get(0).getUsername());
        assertEquals("Liam", result.get(0).getFirstName());
        assertEquals("Miller", result.get(0).getLastName());
        assertEquals("password123", result.get(0).getPassword());
        assertTrue(result.get(0).getIsActive());
        assertEquals("123 Main St", result.get(0).getAddress());
        assertEquals(LocalDate.of(1990, 5, 15), result.get(0).getDateOfBirth());
        verify(resourceLoader).getResource("classpath:trainee.csv");
    }

    @Test
    void shouldParseValidCsvFileWithMultipleRows() throws IOException {
        String csvContent = """
                username,firstName,lastName,password,isActive,address,dateOfBirth
                liam.miller,Liam,Miller,password123,true,123 Main St,1990-05-15
                sophia.wilson,Sophia,Wilson,password456,true,456 Oak Ave,1985-08-20
                bob.wilson,Bob,Wilson,password789,false,789 Pine Rd,1992-11-30""";
        Resource mockResource = createMockResource(csvContent);

        when(resourceLoader.getResource("classpath:trainee.csv")).thenReturn(mockResource);

        List<TraineeCsvDto> result = parser.parseCsv("classpath:trainee.csv", TraineeCsvDto.class);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("liam.miller", result.get(0).getUsername());
        assertEquals("sophia.wilson", result.get(1).getUsername());
        assertEquals("bob.wilson", result.get(2).getUsername());
    }

    @Test
    void shouldParseTrainerCsvFile() throws IOException {
        String csvContent = """
                username,firstName,lastName,password,isActive,specializationType
                marcus.stone,Marcus,Stone,trainerpass1,true,CARDIO""";
        Resource mockResource = createMockResource(csvContent);

        when(resourceLoader.getResource("classpath:trainer.csv")).thenReturn(mockResource);

        List<TrainerCsvDto> result = parser.parseCsv("classpath:trainer.csv", TrainerCsvDto.class);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("marcus.stone", result.get(0).getUsername());
        assertEquals("Marcus", result.get(0).getFirstName());
        assertEquals("CARDIO", result.get(0).getSpecializationType());
    }

    @Test
    void shouldParseEmptyCsvFileWithHeaderOnly() throws IOException {
        String csvContent = "username,firstName,lastName,password,isActive,address,dateOfBirth";
        Resource mockResource = createMockResource(csvContent);

        when(resourceLoader.getResource("classpath:empty.csv")).thenReturn(mockResource);

        List<TraineeCsvDto> result = parser.parseCsv("classpath:empty.csv", TraineeCsvDto.class);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenCsvFileIsEmpty() throws IOException {
        Resource mockResource = createMockResource("");

        when(resourceLoader.getResource("classpath:empty.csv")).thenReturn(mockResource);

        assertThrows(
                IllegalStateException.class,
                () -> parser.parseCsv("classpath:empty.csv", TraineeCsvDto.class));
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenFileExtensionIsNotCsv() {
        assertThrows(
                IllegalStateException.class,
                () -> parser.parseCsv("classpath:data.txt", TraineeCsvDto.class));
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenFileExtensionIsUpperCase() {
        assertThrows(
                IllegalStateException.class,
                () -> parser.parseCsv("classpath:data.CSV", TraineeCsvDto.class));
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenFileHasNoExtension() {
        assertThrows(
                IllegalStateException.class,
                () -> parser.parseCsv("classpath:data", TraineeCsvDto.class));
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenFileNotFound() {
        Resource mockResource = mock(Resource.class);
        String filePath = "classpath:nonexistent.csv";

        when(mockResource.exists()).thenReturn(false);
        when(resourceLoader.getResource(filePath)).thenReturn(mockResource);

        assertThrows(
                IllegalStateException.class,
                () -> parser.parseCsv(filePath, TraineeCsvDto.class));

        verify(resourceLoader).getResource(filePath);
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenResourceLoaderThrowsIOException() throws IOException {
        Resource mockResource = mock(Resource.class);
        String filePath = "classpath:error.csv";

        when(mockResource.exists()).thenReturn(true);
        when(mockResource.getInputStream()).thenThrow(new IOException("Failed to read file"));
        when(resourceLoader.getResource(filePath)).thenReturn(mockResource);

        assertThrows(
                IllegalStateException.class,
                () -> parser.parseCsv(filePath, TraineeCsvDto.class));
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenCsvDataIsMalformed() throws IOException {
        String csvContent = """
                username,firstName,lastName,password,isActive,address,dateOfBirth
                liam.miller,Liam,Miller,password123,INVALID_BOOLEAN,123 Main St,1990-05-15""";
        Resource mockResource = createMockResource(csvContent);
        String filePath = "classpath:malformed.csv";

        when(resourceLoader.getResource(filePath)).thenReturn(mockResource);

        assertThrows(
                IllegalStateException.class,
                () -> parser.parseCsv(filePath, TraineeCsvDto.class));
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenCsvHasInvalidDateFormat() throws IOException {
        String csvContent = """
                username,firstName,lastName,password,isActive,address,dateOfBirth
                liam.miller,Liam,Miller,password123,true,123 Main St,INVALID_DATE""";
        Resource mockResource = createMockResource(csvContent);
        String filePath = "classpath:invalid-date.csv";

        when(resourceLoader.getResource(filePath)).thenReturn(mockResource);

        assertThrows(
                IllegalStateException.class,
                () -> parser.parseCsv(filePath, TraineeCsvDto.class));
    }

    @Test
    void shouldHandleDifferentFilePathFormats() throws IOException {
        String csvContent = """
                username,firstName,lastName,password,isActive,address,dateOfBirth
                liam.miller,Liam,Miller,password123,true,123 Main St,1990-05-15""";
        Resource mockResource = createMockResource(csvContent);

        when(resourceLoader.getResource("file:/data/trainee.csv")).thenReturn(mockResource);

        List<TraineeCsvDto> result = parser.parseCsv("file:/data/trainee.csv", TraineeCsvDto.class);

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
