package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.config.Populate;
import dat.dtos.BookingDTO;
import dat.entities.BookingStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingDAOTest {

    private BookingDAO bookingDAO;
    private EntityManagerFactory emf;

    @BeforeAll
    void setup() {
        // Enable testing mode and use TestContainers for the test environment
        HibernateConfig.setTest(true);

        // Initialize EntityManagerFactory for TestContainers setup
        emf = HibernateConfig.getEntityManagerFactoryForTest();

        // Get instance of BookingDAO using the static method
        bookingDAO = BookingDAO.getInstance(emf);

        // Populate initial data
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Populate.main(new String[0]); // Use this method to populate
            em.getTransaction().commit();
        }
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
    }

    @Test
    void read() {
        BookingDTO booking = bookingDAO.read(1);  // Assuming ID 1 is for New York
        assertNotNull(booking, "Booking should not be null.");
        assertEquals("New York", booking.getDestinationCity(), "Expected destination city to be New York"); // Check if destination is New York
    }

    @Test
    void readAll() {
        List<BookingDTO> bookings = bookingDAO.readAll();
        assertNotNull(bookings, "Bookings list should not be null.");
        assertEquals(3, bookings.size(), "Expected three bookings from Populate"); // Ensure the expected number of bookings
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
        }
    }
}