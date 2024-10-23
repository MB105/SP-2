package dat.config;

import dat.entities.Booking;
import dat.entities.Destination;
import dat.entities.Review;
import dat.entities.BookingStatus;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityManager;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Populate {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Clear existing data
            clearExistingData(em);

            // Populate Destinations
            Set<Destination> destinations = getDestinations();
            for (Destination destination : destinations) {
                em.persist(destination);
            }

            // Populate Bookings
            Set<Booking> bookings = getBookings(destinations);
            for (Booking booking : bookings) {
                em.persist(booking);
            }

            // Populate Reviews
            Set<Review> reviews = getReviews(destinations);
            for (Review review : reviews) {
                em.persist(review);
            }

            em.getTransaction().commit();
        }
    }

    private static void clearExistingData(EntityManager em) {
        // Clear existing data using EntityManager
        em.createQuery("DELETE FROM Review").executeUpdate();  // Clear reviews
        em.createQuery("DELETE FROM Booking").executeUpdate(); // Clear bookings
        em.createQuery("DELETE FROM Destination").executeUpdate(); // Clear destinations
    }

    @NotNull
    private static Set<Destination> getDestinations() {
        Destination paris = new Destination("Paris", "France");
        Destination london = new Destination("London", "UK");
        Destination newYork = new Destination("New York", "USA");

        return Set.of(paris, london, newYork);
    }

    @NotNull
    private static Set<Booking> getBookings(Set<Destination> destinations) {
        Set<Booking> bookings = new HashSet<>();
        int bookingId = 1; // Starting booking ID

        // Create a unique booking for each destination with different statuses
        for (Destination destination : destinations) {
            LocalDateTime departureDate = LocalDateTime.now().plusDays(bookingId); // Different departure date for each booking
            LocalDateTime arrivalDate = departureDate.plusDays(3);
            BookingStatus status;

            // Assign different statuses to each booking
            switch (bookingId) {
                case 1:
                    status = BookingStatus.CONFIRMED;
                    break;
                case 2:
                    status = BookingStatus.PENDING;
                    break;
                case 3:
                    status = BookingStatus.CANCELLED;
                    break;
                default:
                    status = BookingStatus.PENDING; // Fallback (shouldn't happen in this case)
                    break;
            }

            Booking booking = new Booking(
                    destination,
                    departureDate,
                    arrivalDate,
                    LocalDate.now(),
                    status
            );
            bookings.add(booking);
        }
        return bookings;
    }

    @NotNull
    private static Set<Review> getReviews(Set<Destination> destinations) {
        Set<Review> reviews = new HashSet<>();
        reviews.add(new Review(destinations.stream().filter(d -> d.getCity().equals("Paris")).findFirst().orElse(null), 5, "Absolutely loved Paris! The sights were amazing."));
        reviews.add(new Review(destinations.stream().filter(d -> d.getCity().equals("London")).findFirst().orElse(null), 4, "London is beautiful, but it rained too much."));
        reviews.add(new Review(destinations.stream().filter(d -> d.getCity().equals("New York")).findFirst().orElse(null), 5, "New York is the city that never sleeps! Great experience."));
        return reviews;
    }
}
