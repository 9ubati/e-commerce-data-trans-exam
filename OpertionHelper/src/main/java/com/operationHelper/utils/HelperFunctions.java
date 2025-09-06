package com.operationHelper.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.operationHelper.errorHandler.InvalidParameters;

@Service
public class HelperFunctions {
	
	public void validateFields(Map<String, Object> requestMap, List<Map<String, Object>> fields) {
	    List<String> errors = new ArrayList<>();

	    // Build a quick lookup for definitions and the allowed field set
	    Map<String, Map<String, Object>> defs = new HashMap<>();
	    for (Map<String, Object> def : fields) {
	        String fieldName = Objects.toString(def.get("fieldName"), "").trim();
	        if (fieldName.isEmpty()) {
	            errors.add("A field definition is missing 'fieldName'.");
	            continue;
	        }
	        defs.put(fieldName, def);
	    }

	    // 1) Validate each expected field
	    for (Map.Entry<String, Map<String, Object>> e : defs.entrySet()) {
	        String fieldName = e.getKey();
	        Map<String, Object> def = e.getValue();

	        String fieldType = Objects.toString(def.get("fieldType"), "").trim().toLowerCase(Locale.ROOT);
	        if (fieldType.isEmpty()) {
	            errors.add("Field '" + fieldName + "' is missing 'fieldType'.");
	            continue;
	        }
	        boolean mandatory = Boolean.parseBoolean(Objects.toString(def.get("mandatory"), "false"));

	        Object value = requestMap.get(fieldName);

	        // Mandatory check (treat "" as missing for strings)
	        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
	            if (mandatory) {
	                errors.add("Field '" + fieldName + "' is mandatory but missing or empty.");
	            }
	            // If it's not mandatory and missing, nothing to validate
	            continue;
	        }

	        // Type check
	        String typeError = validateType(fieldName, value, fieldType);
	        if (typeError != null) {
	            errors.add(typeError);
	        }
	    }

	    // 2) (Optional) Detect unexpected fields present in request
	    Set<String> allowed = defs.keySet();
	    for (String key : requestMap.keySet()) {
	        if (!allowed.contains(key)) {
	            errors.add("Unexpected field '" + key + "' supplied.");
	        }
	    }

	    if (!errors.isEmpty()) {
	    	throw new InvalidParameters(errors);
	    }
	}

	private String validateType(String fieldName, Object value, String fieldType) {
	    try {
	        switch (fieldType) {
	            case "string":
	                if (!(value instanceof String)) return err(fieldName, "string", value);
	                return null;

	            case "integer":
	            case "int":
	                if (value instanceof Number) {
	                    Number n = (Number) value;
	                    if (Math.floor(n.doubleValue()) != n.doubleValue()) {
	                        return "Field '" + fieldName + "' must be an integer number.";
	                    }
	                    return null;
	                }
	                if (value instanceof String) {
	                    Integer.parseInt(((String) value).trim());
	                    return null;
	                }
	                return err(fieldName, "integer", value);

	            case "long":
	                if (value instanceof Number) return null;
	                if (value instanceof String) { Long.parseLong(((String) value).trim()); return null; }
	                return err(fieldName, "long", value);

	            case "double":
	            case "number":
	            case "decimal":
	                if (value instanceof Number) return null;
	                if (value instanceof String) { Double.parseDouble(((String) value).trim()); return null; }
	                return err(fieldName, "number", value);

	            case "boolean":
	            case "bool":
	                if (value instanceof Boolean) return null;
	                if (value instanceof String) {
	                    String s = ((String) value).trim().toLowerCase(Locale.ROOT);
	                    if (s.equals("true") || s.equals("false")) return null;
	                }
	                return err(fieldName, "boolean", value);

	            case "date": // expects ISO_LOCAL_DATE (e.g., 2025-09-06)
	                if (value instanceof java.util.Date) return null;
	                if (value instanceof String) {
	                    LocalDate.parse(((String) value).trim(), DateTimeFormatter.ISO_LOCAL_DATE);
	                    return null;
	                }
	                return err(fieldName, "ISO date (yyyy-MM-dd)", value);

	            case "datetime": // ISO_DATE_TIME (e.g., 2025-09-06T12:34:56 or with offset)
	            case "timestamp":
	                if (value instanceof java.util.Date) return null;
	                if (value instanceof String) {
	                    String s = ((String) value).trim();
	                    // try several common ISO formats
	                    try { OffsetDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME); return null; } catch (Exception ignored) {}
	                    try { LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME); return null; } catch (Exception ignored) {}
	                    try { Instant.parse(s); return null; } catch (Exception ignored) {}
	                }
	                return err(fieldName, "ISO date-time", value);

	            case "uuid":
	                if (value instanceof UUID) return null;
	                if (value instanceof String) { UUID.fromString(((String) value).trim()); return null; }
	                return err(fieldName, "UUID", value);

	            case "list":
	            case "array":
	                if (value instanceof Collection<?>) return null;
	                if (value != null && value.getClass().isArray()) return null;
	                return err(fieldName, "array/list", value);

	            case "object":
	            case "map":
	                if (value instanceof Map<?, ?>) return null;
	                return err(fieldName, "object/map", value);

	            default:
	                return "Field '" + fieldName + "' has unknown fieldType '" + fieldType + "'.";
	        }
	    } catch (Exception ex) {
	        return "Field '" + fieldName + "' failed " + fieldType + " validation: " + ex.getMessage();
	    }
	}

	private String err(String fieldName, String expected, Object actual) {
	    String actualType = (actual == null ? "null" : actual.getClass().getSimpleName());
	    return "Field '" + fieldName + "' must be " + expected + " (got " + actualType + ").";
	}
	
}
