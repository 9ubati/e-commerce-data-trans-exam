package com.operationHelper.utils;

import java.util.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.operationHelper.errorHandler.AccessDenied;

public final class PermissionUtil {

private static final ObjectMapper MAPPER = new ObjectMapper();

private PermissionUtil() {}

/**
* Require a permission based ONLY on headers.
* Looks for permissions in:
*   1) X-Permissions: "p1,p2,p3"
*   2) Permissions:   "p1,p2,p3"        (fallback)
*   3) X-Claims / Claims: JSON with { "permissions": ["p1","p2"] }
* Comparison is case-sensitive by default; set ignoreCase=true to relax.
*/
public static void requirePermission(String requiredPermission,
                                    Map<String, ?> headers,
                                    boolean ignoreCase) {
 if (requiredPermission == null || requiredPermission.isBlank()) {
   throw new IllegalArgumentException("requiredPermission is blank");
 }
 if (headers == null || headers.isEmpty()) {
   throw new AccessDenied("Access denied: no headers");
 }

 Set<String> perms = extractPermissions(headers, ignoreCase);

 boolean allowed = perms.stream().anyMatch(p ->
     ignoreCase ? p.equalsIgnoreCase(requiredPermission) : p.equals(requiredPermission));

 if (!allowed) {
   throw new AccessDenied("Access denied: missing permission '" + requiredPermission + "'");
 }
}

public static void requirePermission(String requiredPermission, Map<String, ?> headers) {
 requirePermission(requiredPermission, headers, false);
}

/** Extract permissions from common header shapes. */
private static Set<String> extractPermissions(Map<String, ?> headers, boolean toLower) {
 // Build a case-insensitive view of headers
 Map<String, String> h = new HashMap<>();
 for (var e : headers.entrySet()) {
   if (e.getKey() == null || e.getValue() == null) continue;
   h.put(e.getKey().toLowerCase(Locale.ROOT), String.valueOf(e.getValue()));
 }

 // 1) X-Permissions or Permissions (comma separated)
 String csv = null;
 if (h.containsKey("x-permissions")) csv = h.get("x-permissions");
 else if (h.containsKey("permissions")) csv = h.get("permissions");

 Set<String> out = new LinkedHashSet<>();
 if (csv != null && !csv.isBlank()) {
   for (String s : csv.split(",")) {
     String v = s.trim();
     if (!v.isEmpty()) out.add(toLower ? v.toLowerCase(Locale.ROOT) : v);
   }
   if (!out.isEmpty()) return out;
 }

 // 2) X-Claims or Claims (JSON object containing "permissions": array)
 String claimsJson = null;
 if (h.containsKey("x-claims")) claimsJson = h.get("x-claims");
 else if (h.containsKey("claims")) claimsJson = h.get("claims");

 if (claimsJson != null && !claimsJson.isBlank()) {
   try {
     Map<?,?> claims = MAPPER.readValue(claimsJson, Map.class);
     Object perms = claims.get("permissions");
     if (perms instanceof Collection<?> col) {
       for (Object o : col) {
         if (o == null) continue;
         String v = String.valueOf(o).trim();
         if (!v.isEmpty()) out.add(toLower ? v.toLowerCase(Locale.ROOT) : v);
       }
     }
   } catch (Exception ignore) {
     // malformed claims: act as no permissions present
   }
 }

 return out;
}
}

