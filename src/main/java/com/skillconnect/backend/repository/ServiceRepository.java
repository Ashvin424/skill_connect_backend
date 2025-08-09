package com.skillconnect.backend.repository;

import com.skillconnect.backend.models.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {
    // This interface will automatically inherit methods for CRUD operations
    // and can be extended with custom query methods if needed.
    Page<Service> findByCategoryIgnoreCase(String category, Pageable pageable);

    int countByPostedBy_Id(Long id);

    List<Service> findAllByPostedBy_Id(Long id);

    List<Service> findByTitleContainingIgnoreCase(String title);

    List<Service> findByCategoryContainingIgnoreCase(String category);

    List<Service> findByPostedByUsernameContainingIgnoreCase(String username);
}
