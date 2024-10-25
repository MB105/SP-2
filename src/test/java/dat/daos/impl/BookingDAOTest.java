package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.dtos.BookingDTO;
import dat.dtos.DestinationDTO;
import dat.entities.BookingStatus;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BookingDAOTest {

    private static BookingDAO bookingDAO;
    private static DestinationDAO destinationDAO;
    private static EntityManagerFactory emf;

    @BeforeAll
    static void setup() {
        // Initialiserer testkonfiguration og henter entity manager fabrik
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        bookingDAO = BookingDAO.getInstance(emf);
        destinationDAO = DestinationDAO.getInstance(emf);
    }

    @Test
    void create() {
        // Først oprettes en destination
        DestinationDTO destinationDTO = new DestinationDTO(null, "Paris", "Frankrig");
        DestinationDTO createdDestination = destinationDAO.create(destinationDTO);

        // Opretter en booking for den oprettede destination
        BookingDTO bookingDTO = new BookingDTO(null, createdDestination.getId(), "Paris",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5),
                LocalDate.now(),
                BookingStatus.CONFIRMED);
        BookingDTO createdBooking = bookingDAO.create(bookingDTO);

        // Bekræft at bookingen blev oprettet korrekt
        assertNotNull(createdBooking);
        assertNotNull(createdBooking.getId());
        assertEquals("Paris", createdBooking.getDestinationCity());
        assertEquals(createdDestination.getId(), createdBooking.getDestinationId());
    }

    @Test
    void read() {
        // Først oprettes en destination
        DestinationDTO destinationDTO = new DestinationDTO(null, "Paris", "Frankrig");
        DestinationDTO createdDestination = destinationDAO.create(destinationDTO);

        // Opretter en booking
        BookingDTO bookingDTO = new BookingDTO(null, createdDestination.getId(), "Paris",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5),
                LocalDate.now(),
                BookingStatus.CONFIRMED);
        BookingDTO createdBooking = bookingDAO.create(bookingDTO);

        // Læser bookingen fra databasen
        BookingDTO fetchedBooking = bookingDAO.read(createdBooking.getId());

        // Bekræfter at bookingen blev hentet korrekt
        assertNotNull(fetchedBooking);
        assertEquals(createdBooking.getId(), fetchedBooking.getId());
        assertEquals(createdDestination.getId(), fetchedBooking.getDestinationId());
    }

    @Test
    void readAll() {
        // Opretter flere destinationer og deres bookinger
        DestinationDTO destination1 = destinationDAO.create(new DestinationDTO(null, "Paris", "Frankrig"));
        DestinationDTO destination2 = destinationDAO.create(new DestinationDTO(null, "London", "UK"));
        DestinationDTO destination3 = destinationDAO.create(new DestinationDTO(null, "New York", "USA"));

        List<BookingDTO> bookings = new ArrayList<>();
        bookings.add(bookingDAO.create(new BookingDTO(null, destination1.getId(), "Paris",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5),
                LocalDate.now(),
                BookingStatus.CONFIRMED)));
        bookings.add(bookingDAO.create(new BookingDTO(null, destination2.getId(), "London",
                LocalDateTime.now().plusDays(3),
                LocalDateTime.now().plusDays(6),
                LocalDate.now(),
                BookingStatus.PENDING)));
        bookings.add(bookingDAO.create(new BookingDTO(null, destination3.getId(), "New York",
                LocalDateTime.now().plusDays(4),
                LocalDateTime.now().plusDays(7),
                LocalDate.now(),
                BookingStatus.CANCELLED)));

        // Henter alle bookinger
        List<BookingDTO> fetchedBookings = bookingDAO.readAll();

        // Bekræfter at bookingerne blev hentet
        assertEquals(3, fetchedBookings.size());
    }

    @Test
    void update() {
        // Først oprettes en destination
        DestinationDTO destinationDTO = new DestinationDTO(null, "Paris", "Frankrig");
        DestinationDTO createdDestination = destinationDAO.create(destinationDTO);

        // Opretter en booking for den oprettede destination
        BookingDTO bookingDTO = new BookingDTO(null, createdDestination.getId(), "Paris",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5),
                LocalDate.now(),
                BookingStatus.CONFIRMED);
        BookingDTO createdBooking = bookingDAO.create(bookingDTO);

        // Opdaterer bookingen med en ny destination og status
        createdBooking.setDestinationCity("München");
        createdBooking.setStatus(BookingStatus.PENDING);

        DestinationDTO destinationDTO2 = new DestinationDTO(null, "München", "Tyskland");
        destinationDAO.create(destinationDTO2);

        // Udfører opdateringen
        BookingDTO updatedBooking = bookingDAO.update(createdBooking.getId(), createdBooking);

        // Bekræfter at bookingen blev opdateret korrekt
        assertNotNull(updatedBooking);
        assertEquals("München", updatedBooking.getDestinationCity());
        assertEquals(BookingStatus.PENDING, updatedBooking.getStatus());
    }

    @Test
    void delete() {
        // Opretter en destination og booking
        DestinationDTO destinationDTO = destinationDAO.create(new DestinationDTO(null, "Paris", "Frankrig"));
        BookingDTO bookingDTO = new BookingDTO(null, destinationDTO.getId(), "Paris",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5),
                LocalDate.now(),
                BookingStatus.CONFIRMED);
        BookingDTO createdBooking = bookingDAO.create(bookingDTO);
        assertNotNull(createdBooking);

        // Sletter bookingen
        bookingDAO.delete(createdBooking.getId());

        // Bekræfter at bookingen er slettet
        BookingDTO fetchedBooking = bookingDAO.read(createdBooking.getId());
        assertNull(fetchedBooking);
    }

    @Test
    void validatePrimaryKey() {
        // Opretter en destination og booking
        DestinationDTO destinationDTO = destinationDAO.create(new DestinationDTO(null, "Paris", "Frankrig"));
        BookingDTO bookingDTO = new BookingDTO(null, destinationDTO.getId(), "Paris",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5),
                LocalDate.now(),
                BookingStatus.CONFIRMED);
        BookingDTO createdBooking = bookingDAO.create(bookingDTO);

        // Bekræfter at den oprettede booking har en gyldig primærnøgle
        assertTrue(bookingDAO.validatePrimaryKey(createdBooking.getId()));
        assertFalse(bookingDAO.validatePrimaryKey(-1)); // Tjekker for en ikke-eksisterende ID
    }

    @AfterEach
    void cleanUp() {
        // Rydder op i bookinger og destinationer efter hver test
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Booking").executeUpdate();
            em.createQuery("DELETE FROM Destination").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE booking_id_seq RESTART WITH 1").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE destination_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
