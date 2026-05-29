package com.gym.crm.test.helper;

import org.jspecify.annotations.NonNull;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class JsonTestHelper {

    private JsonTestHelper() {
    }

    public static String readJson(String resourcePath) {
        try (InputStream is = openResourceInputStream(resourcePath)) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read JSON resource: " + resourcePath, e);
        }
    }

    private static @NonNull InputStream openResourceInputStream(String resourcePath) throws IOException {
        return new ClassPathResource(resourcePath).getInputStream();
    }

    public static void assertJsonEquals(String expectedJson, String actualJson) {
        try {
            JSONAssert.assertEquals(expectedJson, actualJson, true);
        } catch (Exception e) {
            throw new AssertionError("JSON comparison failed: " + e.getMessage(), e);
        }
    }

}
