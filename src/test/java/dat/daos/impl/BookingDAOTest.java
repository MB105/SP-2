package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.dtos.BookingDTO;
import dat.dtos.DestinationDTO;
import dat.entities.BookingStatus;
import jakarta.persistence.EntityManager;
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


        // Populate initial data
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Populate.main(new String[0]); // Use this method to populate
            em.getTransaction().commit();
        }

        destinationDAO = DestinationDAO.getInstance(emf);
    }

    @Test
    void create() {
      
        // Create the BookingDTO object
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setDestinationId(1); // Using existing destination ID 1
        bookingDTO.setDepartureDate(LocalDateTime.now().plusDays(1)); // Set departure date to 1 day from now
        bookingDTO.setArrivalDate(LocalDateTime.now().plusDays(4)); // Set arrival date to 4 days from now
        bookingDTO.setBookingDate(LocalDate.now()); // Set booking date to today
        bookingDTO.setStatus(BookingStatus.CONFIRMED); // Set booking status

        // Create the booking
        BookingDTO createdBooking = bookingDAO.create(bookingDTO);

        // Assertions to verify the booking creation
        assertNotNull(createdBooking, "Created booking should not be null.");
        assertNotNull(createdBooking.getId(), "Booking ID should be generated."); // Check that the ID is generated
        assertEquals(bookingDTO.getDestinationId(), createdBooking.getDestinationId(), "Destination ID should match.");
        assertEquals(bookingDTO.getStatus(), createdBooking.getStatus(), "Booking status should match.");

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

        BookingDTO booking = bookingDAO.read(1);  // Assuming ID 1 is for New York
        assertNotNull(booking, "Booking should not be null.");
        assertEquals("New York", booking.getDestinationCity(), "Expected destination city to be New York"); // Check if destination is New York

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

        List<BookingDTO> bookings = bookingDAO.readAll();
        assertNotNull(bookings, "Bookings list should not be null.");
        assertEquals(3, bookings.size(), "Expected three bookings from Populate"); // Ensure the expected number of bookings

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

        BookingDTO booking = bookingDAO.read(2);  // Read the existing booking for New York
        assertNotNull(booking, "Booking should exist before updating.");

        // Update details
        booking.setDestinationCity("Los Angeles"); // Change destination for testing
        booking.setDepartureDate(LocalDateTime.now().plusDays(2));
        booking.setArrivalDate(LocalDateTime.now().plusDays(5)); // Arrival after departure
        booking.setStatus(BookingStatus.CANCELLED);

        BookingDTO updatedBooking = bookingDAO.update(2, booking); // Update booking with ID 2
        assertNotNull(updatedBooking, "Updated booking should not be null.");
        assertEquals(BookingStatus.CANCELLED, updatedBooking.getStatus(), "Status should be updated to CANCELLED.");
        assertEquals("Los Angeles", updatedBooking.getDestinationCity(), "Verify updated city is Los Angeles"); // Verify updated city

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

        BookingDTO booking = bookingDAO.read(1);  // Read existing booking for New York
        assertNotNull(booking, "Booking should exist before deletion."); // Ensure it exists before deletion

        // Delete the booking
        bookingDAO.delete(booking.getId()); // Call the delete method with the booking ID

        // Verify deletion
        BookingDTO deletedBooking = bookingDAO.read(booking.getId());
        assertNull(deletedBooking, "Ensure the booking is null after deletion"); // Ensure the booking is null after deletion

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

        assertTrue(bookingDAO.validatePrimaryKey(1), "Primary key 1 should exist"); // Assuming ID 1 exists for New York
        assertFalse(bookingDAO.validatePrimaryKey(999), "Primary key 999 should not exist"); // Assuming ID 999 does not exist
    }

    @AfterAll
    public void cleanUp() {
        // Cleanup to ensure no test pollution
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Booking").executeUpdate();
            em.createQuery("DELETE FROM Destination").executeUpdate(); // Clear destinations too
            em.createNativeQuery("ALTER SEQUENCE booking_id_seq RESTART WITH 1").executeUpdate(); // Reset sequence for Booking ID
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace(); // Logs any exceptions that occur
        } finally {
            if (emf != null) {
                emf.close(); // Close EntityManagerFactory after all tests are done
            }

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