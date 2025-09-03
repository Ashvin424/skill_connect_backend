package com.skillconnect.backend.service;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import com.skillconnect.backend.dtos.BookingRequestDTO;
import com.skillconnect.backend.dtos.BookingResponseDTO;
import com.skillconnect.backend.dtos.FirestoreMessage;
import com.skillconnect.backend.models.Booking;
import com.skillconnect.backend.models.BookingStatus;
import com.skillconnect.backend.models.Service;
import com.skillconnect.backend.models.User;
import com.skillconnect.backend.repository.BookingRepository;
import com.skillconnect.backend.repository.ServiceRepository;
import com.skillconnect.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@org.springframework.stereotype.Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private FirebaseNotificationService firebaseNotificationService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServiceRepository serviceRepository;

    // Method to create a booking for a service
    // This method checks if the user is trying to book their own service
    /**
     * Creates a booking for a service requested by a user.
     *
     * @param userDetails    the ID of the user requesting the booking
     * @return the created Booking object
     * @throws RuntimeException if the user is not found, or if the service is not found,
     *                          or if the user tries to book their own service
     */
    public Booking createBooking(UserDetails userDetails, BookingRequestDTO dto) throws Exception {
        User requestedBy = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Service bookedService = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service Not Found"));

        User serviceProvider = bookedService.getPostedBy();

        if (bookingRepository.existsByRequestedByAndService(requestedBy, bookedService)) {
            throw new IllegalStateException("You have already booked this service");
        }

        if (serviceProvider.getId().equals(requestedBy.getId())) {
            throw new IllegalStateException("You cannot book your own service");
        }

        Booking booking = new Booking();
        booking.setStatus(BookingStatus.PENDING);
        booking.setRequestedBy(requestedBy);
        booking.setRequestedAt(LocalDateTime.now());
        booking.setService(bookedService);
        booking.setUpdatedAt(LocalDateTime.now());

        booking = bookingRepository.save(booking);

        // Notifications
        String userFcmToken = requestedBy.getFcmToken();
        String providerFcmToken = serviceProvider.getFcmToken();

        if (userFcmToken != null && userFcmToken.equals(providerFcmToken)) {
            firebaseNotificationService.sendNotification(userFcmToken, "SkillConnect",
                    "Booking successful: You booked " + bookedService.getTitle());
        } else {
            if (providerFcmToken != null) {
                firebaseNotificationService.sendNotification(providerFcmToken, "SkillConnect",
                        requestedBy.getName() + " booked your service: " + bookedService.getTitle());
            }

            if (userFcmToken != null) {
                firebaseNotificationService.sendNotification(userFcmToken, "SkillConnect",
                        "Booking successful: You booked " + bookedService.getTitle());
            }
        }

        // Create chat after booking & notifications
        try {
            String userId1 = String.valueOf(requestedBy.getId());
            String userId2 = String.valueOf(serviceProvider.getId());
            createChatAndSendFirstMessage(userId1, userId2);
        } catch (Exception e) {
            e.printStackTrace(); // Or log.error(...)
        }

        return booking;
    }




    // Method to get a booking by its ID
    public Booking getBookingById(Long bookingId){
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    // Method to cancel a booking
    @Transactional
    public Booking cancelBooking(Long bookingId, UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getRequestedBy().getId().equals(user.getId())) {
            throw new RuntimeException("You can only cancel your own bookings");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    // Method to update the status of a booking
    public Booking updateBookingStatus(Long bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        String userFcmToken = booking.getRequestedBy().getFcmToken();

        if (booking.getStatus() == status) {
            throw new RuntimeException("Booking is already " + status);
        }
        if (booking.getStatus() == BookingStatus.CONFIRMED && status != BookingStatus.CANCELLED) {
            throw new RuntimeException("Cannot change confirmed booking");
        }
        booking.setStatus(status);
        booking.setUpdatedAt(LocalDateTime.now());


        if (status == BookingStatus.CANCELLED) {
            booking.setCancelledAt(LocalDateTime.now());
            if(userFcmToken == null && userFcmToken.isEmpty()) {
                firebaseNotificationService.sendNotification(userFcmToken, "SkillConnect",
                        "Your booking for " + booking.getService().getTitle() + " has been cancelled 😔.");
            }
        } else if (status == BookingStatus.CONFIRMED) {
            booking.setConfirmedAt(LocalDateTime.now());
            if(userFcmToken == null && userFcmToken.isEmpty()) {
                firebaseNotificationService.sendNotification(userFcmToken, "SkillConnect",
                        "Your booking for " + booking.getService().getTitle() + " has been confirmed ✅");
            }
        }
        return bookingRepository.save(booking);
    }

    //List all bookings for a requested user
    public Page<BookingResponseDTO> getBookingsRequestedByUser(Long userId, Pageable pageable){
        Page<Booking> bookingListForRequestedUser = bookingRepository.findByRequestedBy_Id(userId, pageable);
        if (bookingListForRequestedUser.isEmpty()){
            throw new RuntimeException("No booking found for user with ID: "+userId);
        }
        return bookingListForRequestedUser.map(this::toDto);
    }

    //List all booking for service provider
    public Page<BookingResponseDTO> getBookingsForServiceProvider(Long serviceProviderId, Pageable pageable){
        Page<Booking> bookingListForServiceProvider = bookingRepository.findByService_PostedBy_Id(serviceProviderId,pageable);
        return bookingListForServiceProvider.map(this::toDto);
    }

    // Method to soft delete a booking (mark as cancelled)
    public Booking softDeleteBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.setUpdatedAt(LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public void createChatAndSendFirstMessage(String senderId, String receiverId) throws Exception {
        Firestore db = FirestoreClient.getFirestore();

        // Always sort to maintain same chatId for same pair
        List<String> participants = Arrays.asList(senderId, receiverId);
        participants.sort(String::compareTo);
        String chatId = participants.get(0) + "_" + participants.get(1);

        DocumentReference chatRef = db.collection("chats").document(chatId);
        DocumentSnapshot snapshot = chatRef.get().get();

        // If chat doesn't exist, create it
        if (!snapshot.exists()) {
            Map<String, Object> chatData = new HashMap<>();
            chatData.put("participants", Arrays.asList(senderId, receiverId));
            chatRef.set(chatData).get();
        }

        // Send the first message
        FirestoreMessage message = new FirestoreMessage(
                senderId,
                "Hi, I've booked your service. Let's chat here.",
                Timestamp.now()
        );

        chatRef.collection("messages").add(message).get();
    }
    public BookingResponseDTO toDto(Booking booking) {
        return new BookingResponseDTO(
                booking.getId(),
                booking.getRequestedBy().getId(),
                booking.getService().getId(),
                booking.getService().getPostedBy().getId(),
                booking.getService().getTitle(),
                booking.getRequestedBy().getName(),
                booking.getService().getPostedBy().getName(),
                booking.getStatus(),
                booking.getRequestedAt(),
                booking.getConfirmedAt(),
                booking.getCancelledAt(),
                booking.getUpdatedAt()
        );
    }
}
