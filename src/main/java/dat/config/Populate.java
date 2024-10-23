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
            em.getTransaction().begin(); // Begynd en ny transaktion

            // Ryd eksisterende data i databasen
            clearExistingData(em);

            // Udfyld destinationer
            Set<Destination> destinations = getDestinations();
            for (Destination destination : destinations) {
                em.persist(destination); // Gem hver destination i databasen
            }

            // Udfyld bookinger
            Set<Booking> bookings = getBookings(destinations);
            for (Booking booking : bookings) {
                em.persist(booking); // Gem hver booking i databasen
            }

            // Udfyld anmeldelser
            Set<Review> reviews = getReviews(destinations);
            for (Review review : reviews) {
                em.persist(review); // Gem hver anmeldelse i databasen
            }

            em.getTransaction().commit(); // Forpligt ændringerne til databasen
        }
    }

    // Metode til at rydde eksisterende data i databasen
    private static void clearExistingData(EntityManager em) {
        // Ryd eksisterende data ved hjælp af EntityManager
        em.createQuery("DELETE FROM Review").executeUpdate();  // Ryd anmeldelser
        em.createQuery("DELETE FROM Booking").executeUpdate(); // Ryd bookinger
        em.createQuery("DELETE FROM Destination").executeUpdate(); // Ryd destinationer

        // Nulstil sekvenserne for ID kolonnerne
        em.createNativeQuery("ALTER SEQUENCE review_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE booking_id_seq RESTART WITH 1").executeUpdate();
        em.createNativeQuery("ALTER SEQUENCE destination_id_seq RESTART WITH 1").executeUpdate();
    }

    // Metode til at oprette og returnere destinationer
    @NotNull
    private static Set<Destination> getDestinations() {
        // Opretter tre destinationer
        Destination paris = new Destination("Paris", "Frankrig");
        Destination london = new Destination("London", "UK");
        Destination newYork = new Destination("New York", "USA");

        // Returnerer destinationerne som et sæt
        return Set.of(paris, london, newYork);
    }

    // Metode til at oprette og returnere bookinger
    @NotNull
    private static Set<Booking> getBookings(Set<Destination> destinations) {
        Set<Booking> bookings = new HashSet<>();
        int index = 0; // Indeks til at holde styr på unikke egenskaber

        // Opretter bookinger for hver destination med unikke afrejsedatoer og statuser
        for (Destination destination : destinations) {
            // Beregner afrejsedato og ankomstdato
            LocalDateTime departureDate = LocalDateTime.now().plusDays(index + 1); // Unik afrejsedato
            LocalDateTime arrivalDate = departureDate.plusDays(3);
            BookingStatus status;

            // Tildeler forskellige statuser baseret på indekset
            if (index == 0) {
                status = BookingStatus.CONFIRMED; // Bekræftet status for første booking
            } else if (index == 1) {
                status = BookingStatus.PENDING; // Afventende status for anden booking
            } else {
                status = BookingStatus.CANCELLED; // Annulert status for tredje booking
            }

            // Opretter en ny booking
            Booking booking = new Booking(
                    destination,
                    departureDate,
                    arrivalDate,
                    LocalDate.now(),
                    status
            );
            bookings.add(booking); // Tilføjer booking til sættet
            index++; // Inkrementer indekset
        }

        return bookings; // Returnerer sættet med bookinger
    }

    // Metode til at oprette og returnere anmeldelser
    @NotNull
    private static Set<Review> getReviews(Set<Destination> destinations) {
        Set<Review> reviews = new HashSet<>();
        int index = 1; // Indeks til at holde styr på unikke egenskaber, starter fra 1

        // Opretter anmeldelser for hver destination med unikke vurderinger og kommentarer
        for (Destination destination : destinations) {
            // Tildeler en vurdering direkte baseret på indekset
            int rating = index <= 5 ? index : 5; // Vurdering vil være 1 til 5 baseret på indekset
            String comment = "Vurdering " + rating + " for mit besøg i " + destination.getCity() + "!";
            reviews.add(new Review(destination, rating, comment)); // Tilføjer anmeldelse til sættet

            index++; // Inkrementer indekset
        }

        return reviews; // Returnerer sættet med anmeldelser
    }
}
