package com.mentors.applicationstarter.Utils;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class Base64Utils {

    public String encodeStringToUrlSafeBase64(String string) {
        return Base64.getUrlEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8));
    }

    public String decodeUrlSafeBase64ToString(String string) {
        byte[] stringBytes = Base64.getUrlDecoder().decode(string);
        return new String(stringBytes);
    }
}
