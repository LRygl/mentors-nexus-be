package com.mentors.applicationstarter.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;

public interface IdentifiableRepository<T> {
    Optional<T> findById(Long id);
    Optional<T> findByUuid(UUID uuid);
}
