package com.mentors.applicationstarter.Repository;

import com.mentors.applicationstarter.Model.Label;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface LabelRepository extends JpaRepository<Label, Long> {
    List<Label> findByNameIn(Collection<String> names);
}
