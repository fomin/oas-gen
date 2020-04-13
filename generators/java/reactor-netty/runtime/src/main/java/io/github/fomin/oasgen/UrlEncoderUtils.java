package io.github.fomin.oasgen;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UrlEncoderUtils {
    private static final int COLLECT_KEY_STATE = 0;
    private static final int COLLECT_VALUE_STATE = 1;

    public static String encodeUrl(String baseUrl, String... keysAndValues) {
        StringBuilder sb = new StringBuilder(baseUrl);

        for (int i = 0; i < keysAndValues.length; i += 2) {
            if (i == 0) {
                sb.append('?');
            } else {
                sb.append('&');
            }
            String key = keysAndValues[i];
            String encodedKey = encode(key);
            sb.append(encodedKey);
            String value = keysAndValues[i + 1];
            if (value != null) {
                sb.append('=');
                String encodedValue = encode(value);
                sb.append(encodedValue);
            }
        }

        return sb.toString();
    }

    public static String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, String> parseQueryParams(String url) {
        int keyStartIndex = url.indexOf('?') + 1;
        if (keyStartIndex > 0) {
            int state = COLLECT_KEY_STATE;
            StringBuilder sb = new StringBuilder();
            String currentKey = null;
            Map<String, String> parameterMap = new HashMap<>();

            for (int i = keyStartIndex; i < url.length(); i++) {
                char c = url.charAt(i);
                if (state == COLLECT_KEY_STATE) {
                    if (c == '=') {
                        state = COLLECT_VALUE_STATE;
                        currentKey = decode(sb.toString());
                        sb.setLength(0);
                    } else if (c == '&') {
                        sb.setLength(0);
                    } else if (c == '#') {
                        break;
                    } else {
                        sb.append(c);
                    }
                } else {
                    if (c == '&') {
                        state = COLLECT_KEY_STATE;
                        String value = decode(sb.toString());
                        sb.setLength(0);
                        parameterMap.put(currentKey, value);
                    } else if (c == '#') {
                        String value = decode(sb.toString());
                        parameterMap.put(currentKey, value);
                        break;
                    } else if (i == url.length() - 1) {
                        sb.append(c);
                        String value = decode(sb.toString());
                        parameterMap.put(currentKey, value);
                        break;
                    } else {
                        sb.append(c);
                    }
                }
            }
            return parameterMap;
        } else {
            return Collections.emptyMap();
        }
    }

    private UrlEncoderUtils() {
    }
}
