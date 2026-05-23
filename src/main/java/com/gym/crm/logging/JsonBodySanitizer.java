package com.gym.crm.logging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class JsonBodySanitizer {

    private static final int MAX_BODY_CHARS = 5000;
    private static final int MAX_FIELD_CHARS = 200;
    private static final Set<String> MASKED_FIELDS = Set.of("password", "oldpassword", "newpassword");
    private static final Set<String> PARTIAL_FIELDS = Set.of("address");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonBodySanitizer() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String sanitize(String body) {
        if (body == null || body.isBlank()) {
            return "";
        }

        try {
            JsonNode node = MAPPER.readTree(body);
            JsonNode masked = maskNode(node);
            String maskedJson = MAPPER.writeValueAsString(masked);

            return truncateBody(maskedJson);
        } catch (JsonProcessingException e) {
            return truncateBody(body);
        }
    }

    public static boolean supports(String contentType) {
        if (contentType == null) {
            return false;
        }

        try {
            MediaType mediaType = MediaType.valueOf(contentType);

            return APPLICATION_JSON.equalsTypeAndSubtype(mediaType) || contentType.contains("json");
        } catch (InvalidMediaTypeException ignored) {
            return false;
        }
    }

    private static JsonNode maskNode(JsonNode node) {
        if (!node.isObject() && !node.isArray()) {
            return node;
        }

        if (node.isArray()) {
            ArrayNode arr = MAPPER.createArrayNode();
            node.forEach(element -> arr.add(maskNode(element)));
            return arr;
        }

        ObjectNode obj = (ObjectNode) node;
        maskObjectNodes(obj);

        return obj;
    }

    private static void maskObjectNodes(ObjectNode obj) {
        List<String> fieldNames = new ArrayList<>();
        obj.fieldNames().forEachRemaining(fieldNames::add);

        for (String key : fieldNames) {
            JsonNode originalValue = obj.get(key);
            JsonNode sanitizedValue = sanitizeValue(key, originalValue);
            obj.set(key, sanitizedValue);
        }
    }

    private static JsonNode sanitizeValue(String key, JsonNode value) {
        if (value.isContainerNode()) {
            return maskNode(value);
        }

        String lowerKey = key.toLowerCase();

        if (shouldBeMasked(lowerKey, value)) {
            return TextNode.valueOf("***");
        }

        if (shouldBeMaskedPartially(lowerKey, value)) {
            return TextNode.valueOf(maskValuePartially(value.asText()));
        }

        if (value.isTextual()) {
            return TextNode.valueOf(truncateValue(value.asText()));
        }

        return value;
    }

    private static boolean shouldBeMasked(String key, JsonNode value) {
        return MASKED_FIELDS.contains(key) && !value.isNull();
    }

    private static boolean shouldBeMaskedPartially(String key, JsonNode value) {
        return PARTIAL_FIELDS.contains(key) && value.isTextual();
    }

    private static String maskValuePartially(String value) {
        if (value.length() <= 4) {
            return "***";
        }

        int visible = Math.min(4, value.length() / 4);
        return value.substring(0, visible) + "***";
    }

    private static String truncateValue(String value) {
        if (value.length() <= MAX_FIELD_CHARS) {
            return value;
        }

        return value.substring(0, MAX_FIELD_CHARS) + "...[truncated]";
    }

    private static String truncateBody(String body) {
        if (body.length() <= MAX_BODY_CHARS) {
            return body;
        }

        return body.substring(0, MAX_BODY_CHARS) + "...[body truncated]";
    }

}