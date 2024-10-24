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
        // Initialize test configuration and fetch entity manager factory
        HibernateConfig.setTest(true);
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        bookingDAO = BookingDAO.getInstance(emf);
        destinationDAO = DestinationDAO.getInstance(emf);
    }

    @Test
    void create() {
        // First, create a destination
        DestinationDTO destinationDTO = new DestinationDTO(null, "Paris", "France");
        DestinationDTO createdDestination = destinationDAO.create(destinationDTO);

        // Create a booking for the created destination
        BookingDTO bookingDTO = new BookingDTO(null, createdDestination.getId(), "Paris",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5),
                LocalDate.now(),
                BookingStatus.CONFIRMED);
        BookingDTO createdBooking = bookingDAO.create(bookingDTO);

        System.out.println("hi"+createdBooking.getId());
        System.out.println("hi"+createdDestination.getId());
        System.out.println("hi"+createdBooking.getDestinationId());

        // Assert that the booking was created successfully
        assertNotNull(createdBooking);
        assertNotNull(createdBooking.getId());
        assertEquals("Paris", createdBooking.getDestinationCity());
        assertEquals(createdDestination.getId(), createdBooking.getDestinationId());
    }

    @Test
    void read() {
        // First, create a destination
        DestinationDTO destinationDTO = new DestinationDTO(null, "Paris", "France");
        DestinationDTO createdDestination = destinationDAO.create(destinationDTO);

        // Create a booking
        BookingDTO bookingDTO = new BookingDTO(null, createdDestination.getId(), "Paris",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5),
                LocalDate.now(),
                BookingStatus.CONFIRMED);
        BookingDTO createdBooking = bookingDAO.create(bookingDTO);

        // Read the booking from the database
        BookingDTO fetchedBooking = bookingDAO.read(createdBooking.getId());

        // Assert that the booking was fetched correctly
        assertNotNull(fetchedBooking);
        assertEquals(createdBooking.getId(), fetchedBooking.getId());
        assertEquals(createdDestination.getId(), fetchedBooking.getDestinationId());
    }

    @Test
    void readAll() {
        // Create multiple destinations and their bookings
        DestinationDTO destination1 = destinationDAO.create(new DestinationDTO(null, "Paris", "France"));
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

        // Fetch all bookings
        List<BookingDTO> fetchedBookings = bookingDAO.readAll();

        // Assert that three bookings were fetched
        assertEquals(3, fetchedBookings.size());
    }

    @Test
    void update() {
        // First, create a destination
        DestinationDTO destinationDTO = new DestinationDTO(null, "Paris", "France");
        DestinationDTO createdDestination = destinationDAO.create(destinationDTO);

        // Create a booking for the created destination
        BookingDTO bookingDTO = new BookingDTO(null, createdDestination.getId(), "Paris",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5),
                LocalDate.now(),
                BookingStatus.CONFIRMED);
        BookingDTO createdBooking = bookingDAO.create(bookingDTO);

        // Log the details
        System.out.println("Initial booking ID: " + createdBooking.getId());
        System.out.println("Initial destination ID: " + createdDestination.getId());
        System.out.println("Initial booking's destination ID: " + createdBooking.getDestinationId());

        // Update the booking - change destination city and status
        createdBooking.setDestinationCity("München");
        createdBooking.setStatus(BookingStatus.PENDING);

        DestinationDTO destinationDTO2 = new DestinationDTO(null, "München", "Tyskland");
        destinationDAO.create(destinationDTO2);

        // Perform the update
        BookingDTO updatedBooking = bookingDAO.update(createdBooking.getId(), createdBooking);

        // Log the updated booking details
        System.out.println("Updated booking ID: " + updatedBooking.getId());
        System.out.println("Updated destination city: " + updatedBooking.getDestinationCity());
        System.out.println("Updated status: " + updatedBooking.getStatus());

        // Assert that the booking was updated correctly
        assertNotNull(updatedBooking);
        assertEquals("München", updatedBooking.getDestinationCity());
        assertEquals(BookingStatus.PENDING, updatedBooking.getStatus());
    }


    @Test
    void delete() {
        // Create a destination and booking
        DestinationDTO destinationDTO = destinationDAO.create(new DestinationDTO(null, "Paris", "France"));
        BookingDTO bookingDTO = new BookingDTO(null, destinationDTO.getId(), "Paris",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5),
                LocalDate.now(),
                BookingStatus.CONFIRMED);
        BookingDTO createdBooking = bookingDAO.create(bookingDTO);
        assertNotNull(createdBooking);

        // Delete the booking
        bookingDAO.delete(createdBooking.getId());

        // Verify that the booking is deleted
        BookingDTO fetchedBooking = bookingDAO.read(createdBooking.getId());
        assertNull(fetchedBooking);
    }

    @Test
    void validatePrimaryKey() {
        // Create a destination and booking
        DestinationDTO destinationDTO = destinationDAO.create(new DestinationDTO(null, "Paris", "France"));
        BookingDTO bookingDTO = new BookingDTO(null, destinationDTO.getId(), "Paris",
                LocalDateTime.now().plusDays(2),
                LocalDateTime.now().plusDays(5),
                LocalDate.now(),
                BookingStatus.CONFIRMED);
        BookingDTO createdBooking = bookingDAO.create(bookingDTO);

        // Validate that the created booking has a valid primary key
        assertTrue(bookingDAO.validatePrimaryKey(createdBooking.getId()));
        assertFalse(bookingDAO.validatePrimaryKey(-1)); // Check for a non-existent ID
    }

    @AfterEach
    void cleanUp() {
        // Clean up bookings and destinations after each test
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