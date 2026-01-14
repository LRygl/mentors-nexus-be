package com.mentors.applicationstarter.Utils;

import com.mentors.applicationstarter.Enum.ErrorCodes;
import com.mentors.applicationstarter.Exception.InvalidRequestException;
import com.mentors.applicationstarter.Exception.ResourceNotFoundException;
import com.mentors.applicationstarter.Repository.IdentifiableRepository;

import java.util.UUID;

public class EntityLookupUtils {

    public static <T> T findByIdentifier(
            String identifier,
            IdentifiableRepository<T> repository,
            ErrorCodes notFoundErrorCode,
            ErrorCodes invalidRequestErrorCode
    ) {
        try {
            // Try to parse as numeric ID
            Long id = Long.parseLong(identifier);
            return repository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException(notFoundErrorCode));
        } catch (NumberFormatException e) {
            try {
                // Try to parse as UUID
                UUID uuid = UUID.fromString(identifier);
                return repository.findByUuid(uuid)
                        .orElseThrow(() -> new ResourceNotFoundException(notFoundErrorCode));
            } catch (IllegalArgumentException ex) {
                // Neither valid ID nor UUID
                throw new InvalidRequestException(invalidRequestErrorCode);
            }
        }
    }
}
