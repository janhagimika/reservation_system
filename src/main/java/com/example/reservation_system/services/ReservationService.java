package com.example.reservation_system.services;

import com.example.reservation_system.models.Reservation;
import com.example.reservation_system.models.Serv;
import com.example.reservation_system.models.User;
import com.example.reservation_system.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
public class ReservationService {

    private final UserRepository userRepository;
    private final ReservationTransactionService reservationTransactionService;

    private final ServRepository servRepository;
    private final RoomRepository roomRepository;
    private final BarRepository barRepository;
    private final LiftRepository liftRepository;
    private final ReservationRepository reservationRepository;
    private String openTimeBar = "12:00";
    private String closeTimeBar = "23:30";
    private String openTimeLift = "08:00";
    private String closeTimeLift = "18:00";

    public ReservationService(ReservationRepository reservationRepository, UserRepository userRepository, ReservationTransactionService reservationTransactionService, ServRepository servRepository, RoomRepository roomRepository, BarRepository barRepository, LiftRepository liftRepository) {
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
        this.reservationTransactionService = reservationTransactionService;
        this.servRepository = servRepository;
        this.roomRepository = roomRepository;
        this.barRepository = barRepository;
        this.liftRepository = liftRepository;
    }
    // Method to check if a service can be reserved
    public boolean canBeReserved(Reservation reservation, String serviceType) {

        LocalTime startTime = reservation.getStartTime().toLocalTime();
        LocalTime endTime = reservation.getEndTime().toLocalTime();

        // Check if the service is a bar or a lift and adjust the validation based on business hours
        if ("BAR".equals(serviceType)) {
            return isWithinBusinessHours(startTime, endTime, openTimeBar, closeTimeBar);
        } else if ("LIFT".equals(serviceType)) {
            return isWithinBusinessHours(startTime, endTime, openTimeLift, closeTimeLift);
        }
        return true; // For other types of services, no specific business hour validation
    }

    // Helper method to check if a reservation is within business hours
    private boolean isWithinBusinessHours(LocalTime startTime, LocalTime endTime, String openTime, String closeTime) {

        if (openTime == null || closeTime == null) {
            throw new IllegalArgumentException("Open or close time cannot be null");
        }
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;

        LocalTime open = LocalTime.parse(openTime, formatter);
        LocalTime close = LocalTime.parse(closeTime, formatter);

        // Validate that the start and end times fall within the opening hours
        return !startTime.isBefore(open) && !endTime.isAfter(close);
    }
    public synchronized void updateBusinessHours(Map<String, String> businessHours) {
        this.openTimeBar = businessHours.get("openBar");
        this.closeTimeBar = businessHours.get("closeBar");
        this.openTimeLift = businessHours.get("openLift");
        this.closeTimeLift = businessHours.get("closeLift");
    }

    public List<Map<String, Object>> getAllReservationsWithCommodities() {
        List<Reservation> reservations = reservationRepository.findAll();
        List<Map<String, Object>> reservationDetails = new ArrayList<>();

        for (Reservation reservation : reservations) {
            Map<String, Object> reservationMap = new HashMap<>();
            reservationMap.put("reservation", reservation);

            // Get the service ID from the reservation
            Long serviceId = reservation.getService().getServiceId();

            // Try to fetch the commodity from each repository
            Object commodity = null;

            // Check rooms repository
            commodity = roomRepository.findByServiceId(serviceId).orElse(null);

            // If not found in rooms, check bars repository
            if (commodity == null) {
                commodity = barRepository.findByServiceId(serviceId).orElse(null);
            }

            // If not found in bars, check lifts repository
            if (commodity == null) {
                commodity = liftRepository.findByServiceId(serviceId).orElse(null);
            }

            // Add the commodity to the reservation map
            reservationMap.put("commodity", commodity);

            // Add the map to the details list
            reservationDetails.add(reservationMap);
        }
        return reservationDetails;
    }
    public List<Reservation> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserId(userId);
    }

    @Transactional
    public List<LocalDateTime> findAvailableTimes(Long serviceId, LocalDateTime startDate, LocalDateTime endDate, String serviceType, Duration duration) {
        // Fetch existing reservations for the service
        List<Reservation> reservations = reservationRepository.findReservationsByServiceAndDateRange(serviceId, startDate, endDate);

        String openTime;
        String closeTime;
        if (serviceType.equals("BAR")) {
            openTime = openTimeBar;  // Assuming openTimeBar is "12:00"
            closeTime = closeTimeBar;  // Assuming closeTimeBar is "23:00"
        } else if (serviceType.equals("LIFT")) {
            openTime = openTimeLift;  // Assuming openTimeLift is "08:00"
            closeTime = closeTimeLift;  // Assuming closeTimeLift is "18:00"
        } else if (serviceType.equals("ROOM")) {
            // For rooms, we set openTime and closeTime to span the entire day
            openTime = "00:00";
            closeTime = "23:59";
        } else {
            throw new IllegalArgumentException("Unsupported service type: " + serviceType);
        }

        // Initialize available time slots
        List<LocalDateTime> availableTimes = new ArrayList<>();

        LocalDateTime currentDay = startDate.toLocalDate().atTime(LocalTime.parse(openTime));
        while (!currentDay.toLocalDate().isAfter(endDate.toLocalDate())) {
            LocalDateTime closingTimeForDay = currentDay.toLocalDate().atTime(LocalTime.parse(closeTime));

            // Ensure currentDay is within the valid range
            LocalDateTime startOfDay = currentDay.isBefore(startDate) ? startDate : currentDay;
            LocalDateTime endOfDay = closingTimeForDay.isAfter(endDate) ? endDate : closingTimeForDay;

            // Generate time slots within opening hours for the current day
            LocalDateTime currentSlot = startOfDay;
            while (currentSlot.isBefore(endOfDay)) {
                boolean isAvailable = true;

                // Check if current time slot overlaps with any existing reservations
                for (Reservation reservation : reservations) {
                    LocalDateTime reservationStart = reservation.getStartTime();
                    LocalDateTime reservationEnd = reservation.getEndTime();

                    // Check if the current slot overlaps with any existing reservation
                    if (currentSlot.isBefore(reservation.getEndTime()) && currentSlot.plus(duration).isAfter(reservation.getStartTime())) {
                        isAvailable = false;
                        break;
                    }
                }

                // If the slot is available, add it to the list
                if (isAvailable) {
                    availableTimes.add(currentSlot);
                }

                // Move to the next slot
                currentSlot = currentSlot.plus(duration);
            }

            // Move to the next day and reset the start time to the opening hour
            currentDay = currentDay.plusDays(1).toLocalDate().atTime(LocalTime.parse(openTime));
        }

        return availableTimes;
    }

    public Reservation createReservation(Reservation reservation, String serviceType) {
        // Perform validations
        User user = userRepository.findById(reservation.getUser().getId())
                .orElseThrow(() -> new IllegalArgumentException("Uživatel nebyl nalezen."));
        Serv service = servRepository.findById(reservation.getService().getServiceId())
                .orElseThrow(() -> new IllegalArgumentException("Servis nebyl nalezen"));

        // Check if the reservation overlaps with an existing one
        if (hasOverlappingReservation(user.getId(), service.getServiceId(), reservation.getStartTime(), reservation.getEndTime())) {
            throw new IllegalArgumentException("The reservation overlaps with an existing one.");
        }

        // Check if the service is available during the requested time
        if (!isServiceAvailable(service, reservation.getStartTime(), reservation.getEndTime())) {
            throw new IllegalArgumentException("The service is not available during the requested time.");
        }

        // Check if the reservation is within the allowed business hours
        if (!canBeReserved(reservation, serviceType)) {
            throw new IllegalArgumentException("The reservation does not fall within the business hours.");
        }
        // Call the transactional method in another bean to not impact performance

        // Send the email
        //sendConfirmationEmail(savedReservation);

        return reservationTransactionService.createReservationInternal(
                reservation, user, service, reservationRepository
        );
    }

    private boolean isServiceAvailable(Serv service, LocalDateTime startTime,LocalDateTime endTime) {
        return reservationRepository.isServiceAvailable(service.getServiceId(), startTime, endTime);
    }

    private boolean hasOverlappingReservation(Long userId, Long serviceId, LocalDateTime startTime,LocalDateTime endTime) {
        return reservationRepository.hasOverlappingReservation(userId, serviceId, startTime, endTime);
    }

    public int updateReservation(Long id, Reservation reservation) {
        Optional<Reservation> existingReservation = reservationRepository.findById(id);
        if (existingReservation.isPresent()) {
            reservation.setId(id);
            reservationRepository.save(reservation); // Use save instead of update
            return 1;
        } else {
            return 0;
        }
    }


    public boolean deleteReservation(Long id) {
        if (reservationRepository.existsById(id)) {
            reservationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Map<String, Object>> getStatistics() {
        List<Map<String, Object>> statistics = new ArrayList<>();
        List<User> users = userRepository.findAll();

        for (User user : users) {
            Map<String, Object> userStats = new HashMap<>();

            List<Reservation> userReservations = reservationRepository.findByUserId(user.getId());

            // Calculate total, passed, active, and future reservations
            int totalReservations = userReservations.size();
            int passedReservations = 0;
            int activeReservations = 0;
            int futureReservations = 0;
            Map<String, Long> commodityCount = new HashMap<>();

            for (Reservation reservation : userReservations) {
                if (reservation.getEndTime().isBefore(LocalDateTime.now())) {
                    passedReservations++;
                } else if (reservation.getStartTime().isBefore(LocalDateTime.now()) && reservation.getEndTime().isAfter(LocalDateTime.now())) {
                    activeReservations++;
                } else {
                    futureReservations++;
                }

                // Track most reserved commodity (room, bar, lift)
                String commodityName = reservation.getService().getServiceName();
                commodityCount.put(commodityName, commodityCount.getOrDefault(commodityName, 0L) + 1);
            }

            // Determine the most reserved commodity
            String mostReservedCommodity = commodityCount.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("Žádné existující rezervace");

            // Store user statistics
            userStats.put("user", user);
            userStats.put("totalReservations", totalReservations);
            userStats.put("mostReservedCommodity", mostReservedCommodity);
            userStats.put("passedReservations", passedReservations);
            userStats.put("activeReservations", activeReservations);
            userStats.put("futureReservations", futureReservations);

            // Add to the final list
            statistics.add(userStats);
        }

        return statistics;
    }
    public Reservation convertToReservation(Map<String, Object> requestBody) {

        // Extract and set the service details
        Map<String, Object> serviceMap = (Map<String, Object>) requestBody.get("service");
        Serv serv = new Serv((long) (Integer) serviceMap.get("serviceId"), (String) serviceMap.get("serviceName"));


        // Set the user details
        Map<String, Object> userData = (Map<String, Object>) requestBody.get("user");
        User user = new User((long) (Integer) userData.get("id"));

        Instant startInstant = Instant.parse((String) requestBody.get("startTime"));
        Instant endInstant = Instant.parse((String) requestBody.get("endTime"));

        LocalDateTime startTime = LocalDateTime.ofInstant(startInstant, ZoneId.systemDefault());
        LocalDateTime endTime = LocalDateTime.ofInstant(endInstant, ZoneId.systemDefault());

        // Create and return the reservation
        Reservation reservation = new Reservation(user, serv, startTime, endTime);
        System.out.println(serv.getServiceName());//testing
        return reservation;
    }
    public Map<String, String> getBusinessHours() {
        Map<String, String> businessHours = new HashMap<>();
        businessHours.put("openBar", openTimeBar);
        businessHours.put("closeBar", closeTimeBar);
        businessHours.put("openLift", openTimeLift);
        businessHours.put("closeLift", closeTimeLift);
        return businessHours;
    }

    public void deleteAllReservations() {
        reservationRepository.deleteAllReservations();
    }
}
