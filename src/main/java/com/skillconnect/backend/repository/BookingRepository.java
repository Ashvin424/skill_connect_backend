package com.skillconnect.backend.repository;

import com.skillconnect.backend.models.Booking;
import com.skillconnect.backend.models.Service;
import com.skillconnect.backend.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRequestedBy_Id(Long userId);

    List<Booking> findByService_PostedBy_Id(Long serviceProviderId);

    boolean existsByRequestedByAndService(User requestedBy, Service bookedService);
    // This interface will automatically inherit methods for CRUD operations
    // and can be extended with custom query methods if needed.
    Page<Booking> findByRequestedBy_Id(Long userId, Pageable pageable);
    Page<Booking> findByService_PostedBy_Id(Long serviceProviderId, Pageable pageable);
}
