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
        // Opretter en EntityManagerFactory til at interagere med databasen
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        // Opretter en EntityManager til at udføre databaseoperationer
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin(); // Begynder en transaktion

            // Rydder data i databasen
            clearExistingData(em);

            // Opretter og gemmer destinationer i databasen
            Set<Destination> destinations = getDestinations();
            for (Destination destination : destinations) {
                em.persist(destination); // Gemmer hver destination
            }

            // Opretter og gemmer bookinger i databasen
            Set<Booking> bookings = getBookings(destinations);
            for (Booking booking : bookings) {
                em.persist(booking); // Gemmer hver booking
            }

            // Opretter og gemmer anmeldelser i databasen
            Set<Review> reviews = getReviews(destinations);
            for (Review review : reviews) {
                em.persist(review); // Gemmer hver anmeldelse
            }

            em.getTransaction().commit(); // Afslutter transaktionen og gemmer ændringerne
        }
    }

    // Sletter data i databasen
    private static void clearExistingData(EntityManager em) {
        em.createQuery("DELETE FROM Review").executeUpdate();  // Sletter anmeldelser
        em.createQuery("DELETE FROM Booking").executeUpdate(); // Sletter bookinger
        em.createQuery("DELETE FROM Destination").executeUpdate(); // Sletter destinationer

        // Nulstiller sekvensværdier for ID'er
        em.createNativeQuery("ALTER SEQUENCE review_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE booking_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE destination_id_seq RESTART WITH 1").executeUpdate();
    }

    // Opretter destinationer og returnerer dem
    @NotNull
    private static Set<Destination> getDestinations() {
        Destination paris = new Destination("Paris", "Frankrig");
        Destination london = new Destination("London", "UK");
        Destination newYork = new Destination("New York", "USA");

        return Set.of(paris, london, newYork); // Returnerer destinationer
    }

    // Opretter bookinger og returnerer dem
    @NotNull
    private static Set<Booking> getBookings(Set<Destination> destinations) {
        Set<Booking> bookings = new HashSet<>();
        int index = 0; // Holder styr på unikke datoer og statusser

        // Opretter bookinger for hver destination
        for (Destination destination : destinations) {
            LocalDateTime departureDate = LocalDateTime.now().plusDays(index + 1); // Unik afrejsedato
            LocalDateTime arrivalDate = departureDate.plusDays(3); // Ankomstdato
            BookingStatus status;

            // Tildeler forskellige statusser afhængigt af indekset
            if (index == 0) {
                status = BookingStatus.CONFIRMED; // Bekræftet booking
            } else if (index == 1) {
                status = BookingStatus.PENDING; // Afventende booking
            } else {
                status = BookingStatus.CANCELLED; // Annulleret booking
            }

            // Opretter og tilføjer booking til sættet
            Booking booking = new Booking(destination, departureDate, arrivalDate, LocalDate.now(), status);
            bookings.add(booking);
            index++; // Opdaterer indekset
        }

        return bookings; // Returnerer bookinger
    }

    // Opretter anmeldelser og returnerer dem
    @NotNull
    private static Set<Review> getReviews(Set<Destination> destinations) {
        Set<Review> reviews = new HashSet<>();
        int index = 1; // Starter fra 1 for at tildele vurderinger

        // Opretter anmeldelser for hver destination
        for (Destination destination : destinations) {
            // Bruger modulus (%) for at sikre at vurderingen altid er mellem 1 og 5
            int rating = (index % 5) + 1;
            String comment = "Vurdering " + rating + " for mit besøg i " + destination.getCity() + "!";
            reviews.add(new Review(destination, rating, comment)); // Tilføjer anmeldelse

            index++; // Opdaterer indekset
        }


        return reviews; // Returnerer anmeldelser
    }
}
