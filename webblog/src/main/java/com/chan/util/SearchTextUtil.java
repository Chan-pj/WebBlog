package com.chan.util;

/**
 * SQL LIKE 검색용 이스케이프 및 요약 문구 생성.
 */
public final class SearchTextUtil {

    private SearchTextUtil() {
    }

    public static String escapeLikePattern(String keyword) {
        if (keyword == null) {
            return null;
        }
        return keyword.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    public static String toLikeKeyword(String keyword) {
        String escaped = escapeLikePattern(keyword);
        if (escaped == null || escaped.trim().isEmpty()) {
            return null;
        }
        return "%" + escaped.trim() + "%";
    }

    public static String buildSnippet(String text, String highlight, int maxLen) {
        if (text == null) {
            return "";
        }
        String plain = text.replaceAll("\\s+", " ").trim();
        if (plain.length() <= maxLen) {
            return plain;
        }
        if (highlight != null && !highlight.isEmpty()) {
            int idx = plain.toLowerCase().indexOf(highlight.toLowerCase());
            if (idx >= 0) {
                int start = Math.max(0, idx - 30);
                int end = Math.min(plain.length(), start + maxLen);
                String part = plain.substring(start, end);
                if (start > 0) {
                    part = "..." + part;
                }
                if (end < plain.length()) {
                    part = part + "...";
                }
                return part;
            }
        }
        return plain.substring(0, maxLen) + "...";
    }
}