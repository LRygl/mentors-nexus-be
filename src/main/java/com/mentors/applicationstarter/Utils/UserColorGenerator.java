package com.mentors.applicationstarter.Utils;

import org.springframework.stereotype.Service;

import java.awt.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class UserColorGenerator {


    public enum Theme {
        LIGHT, DARK
    }

    public static String getUserColorHex(String userEmail, Theme theme) {
        float hue = getHueFromUserEmail(userEmail);
        float saturation = 0.6f;
        float lightness = theme == Theme.DARK ? 0.4f : 0.7f;

        Color color = hslToRgb(hue, saturation, lightness);
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());    }

    private static float getHueFromUserEmail(String username) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(username.getBytes());
            BigInteger number = new BigInteger(1, hash);
            return (number.mod(BigInteger.valueOf(360)).intValue()) / 360f; // hue between 0.0 and 1.0
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    private static Color hslToRgb(float h, float s, float l) {
        float r, g, b;

        if (s == 0f) {
            r = g = b = l; // achromatic
        } else {
            float q = l < 0.5f ? l * (1 + s) : (l + s - l * s);
            float p = 2 * l - q;
            r = hueToRgb(p, q, h + 1f / 3f);
            g = hueToRgb(p, q, h);
            b = hueToRgb(p, q, h - 1f / 3f);
        }

        return new Color(clamp(r), clamp(g), clamp(b));
    }

    private static float hueToRgb(float p, float q, float t) {
        if (t < 0f) t += 1f;
        if (t > 1f) t -= 1f;
        if (t < 1f / 6f) return p + (q - p) * 6f * t;
        if (t < 1f / 2f) return q;
        if (t < 2f / 3f) return p + (q - p) * (2f / 3f - t) * 6f;
        return p;
    }

    private static float clamp(float v) {
        return Math.min(1f, Math.max(0f, v));
    }
}
