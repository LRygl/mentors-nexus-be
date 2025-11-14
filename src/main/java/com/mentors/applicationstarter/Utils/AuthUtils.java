package com.mentors.applicationstarter.Utils;

import com.mentors.applicationstarter.Model.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class AuthUtils {
    public static UUID getAuthenticatedUserUuid() {
        return ((User) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal())
                .getUUID();
    }
}
