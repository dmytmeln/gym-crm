package com.gym.crm.logging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JsonBodySanitizerTest {

    @Test
    void shouldSupportJsonContentTypes() {
        String contentType = "application/json";

        boolean result = JsonBodySanitizer.supports(contentType);

        assertThat(result).isTrue();
    }

    @Test
    void shouldSupportCustomJsonContentTypes() {
        String contentType = "application/merge-patch+json";

        boolean result = JsonBodySanitizer.supports(contentType);

        assertThat(result).isTrue();
    }

    @Test
    void shouldNotSupportNonJsonContentTypes() {
        String contentType = "text/html";

        boolean result = JsonBodySanitizer.supports(contentType);

        assertThat(result).isFalse();
    }

    @Test
    void shouldNotSupportNullContentType() {
        String contentType = null;

        boolean result = JsonBodySanitizer.supports(contentType);

        assertThat(result).isFalse();
    }

    @Test
    void shouldNotSupportWildcardContentType() {
        String contentType = "*/*";

        boolean result = JsonBodySanitizer.supports(contentType);

        assertThat(result).isFalse();
    }

    @Test
    void shouldHandleInvalidMediaTypeFormatGracefully() {
        String contentType = "invalid-type/";

        boolean result = JsonBodySanitizer.supports(contentType);

        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnEmptyStringWhenBodyIsNull() {
        String result = JsonBodySanitizer.sanitize(null);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyStringWhenBodyIsEmpty() {
        String body = "";

        String result = JsonBodySanitizer.sanitize(body);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnEmptyStringWhenBodyIsBlank() {
        String body = "   ";

        String result = JsonBodySanitizer.sanitize(body);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnOriginalStringWhenBodyIsNotJson() {
        String body = "Hello, World!";

        String result = JsonBodySanitizer.sanitize(body);

        assertThat(result).isEqualTo("Hello, World!");
    }

    @Test
    void shouldMaskSensitiveFieldsCaseInsensitively() {
        String body = "{\"password\": \"secret123\", \"oldpassword\": \"old123\", \"newpassword\": \"new123\"}";

        String result = JsonBodySanitizer.sanitize(body);

        assertThat(result).isEqualTo("{\"password\":\"***\",\"oldpassword\":\"***\",\"newpassword\":\"***\"}");
    }

    @Test
    void shouldMaskSensitiveFieldsWithDifferentCasing() {
        String body = "{\"Password\": \"secret123\", \"OLDpassword\": \"old123\", \"NewPassword\": \"new123\"}";

        String result = JsonBodySanitizer.sanitize(body);

        assertThat(result).isEqualTo("{\"Password\":\"***\",\"OLDpassword\":\"***\",\"NewPassword\":\"***\"}");
    }

    @Test
    void shouldNotMaskSensitiveFieldsWhenValueIsNull() {
        String body = "{\"password\": null}";

        String result = JsonBodySanitizer.sanitize(body);

        assertThat(result).isEqualTo("{\"password\":null}");
    }

    @Test
    void shouldFullyMaskShortAddress() {
        String body = "{\"address\": \"123\"}";

        String result = JsonBodySanitizer.sanitize(body);

        assertThat(result).isEqualTo("{\"address\":\"***\"}");
    }

    @Test
    void shouldPartiallyMaskLongAddress() {
        String body = "{\"address\": \"12345678\"}";

        String result = JsonBodySanitizer.sanitize(body);

        assertThat(result).isEqualTo("{\"address\":\"12***\"}");
    }

    @Test
    void shouldNotPartiallyMaskNonTextualAddress() {
        String body = "{\"address\": 12345}";

        String result = JsonBodySanitizer.sanitize(body);

        assertThat(result).isEqualTo("{\"address\":12345}");
    }

    @Test
    void shouldNotPartiallyMaskNullAddress() {
        String body = "{\"address\": null}";

        String result = JsonBodySanitizer.sanitize(body);

        assertThat(result).isEqualTo("{\"address\":null}");
    }

    @Test
    void shouldTruncateLongTextField() {
        String longText = "a".repeat(210);
        String body = "{\"name\": \"" + longText + "\"}";
        String expectedValue = "a".repeat(200) + "...[truncated]";

        String result = JsonBodySanitizer.sanitize(body);

        assertThat(result).isEqualTo("{\"name\":\"" + expectedValue + "\"}");
    }

    @Test
    void shouldTruncateBodyWhenSerializedBodyExceedsLimit() {
        String longText = "a".repeat(5010);
        String expected = longText.substring(0, 5000) + "...[body truncated]";

        String result = JsonBodySanitizer.sanitize(longText);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void shouldSanitizeRecursivelyInNestedObjectsAndArrays() {
        String body = "{\"user\": {\"password\": \"secret\"}, \"items\": [{\"address\": \"12345678\"}]}";

        String result = JsonBodySanitizer.sanitize(body);

        assertThat(result).isEqualTo("{\"user\":{\"password\":\"***\"},\"items\":[{\"address\":\"12***\"}]}");
    }

}
