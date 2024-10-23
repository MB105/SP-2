package dat.daos.impl;

import dat.config.HibernateConfig;
import dat.config.Populate;
import dat.dtos.BookingDTO;
import dat.entities.BookingStatus;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BookingDAOTest {

    private static BookingDAO bookingDAO;
    private static EntityManagerFactory emf;

    @BeforeAll
    void setup() {
        // Enable testing mode and use TestContainers for the test environment
        HibernateConfig.setTest(true);

        // Initialize EntityManagerFactory for TestContainers setup
        emf = HibernateConfig.getEntityManagerFactoryForTest();

        // Get instance of BookingDAO using the static method
        bookingDAO = BookingDAO.getInstance(emf);

        // Populate initial data
        Populate.main(new String[0]);
    }

    @Test
    void create() {
        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setBookingId(4); // Ensure unique booking ID
        bookingDTO.setDestinationId(1); // Assuming destination ID 1 exists
        bookingDTO.setDepartureDate(LocalDateTime.now().plusDays(1));
        bookingDTO.setArrivalDate(LocalDateTime.now().plusDays(4));
        bookingDTO.setBookingDate(LocalDate.now());
        bookingDTO.setStatus(BookingStatus.CONFIRMED);

        BookingDTO createdBooking = bookingDAO.create(bookingDTO);
        assertNotNull(createdBooking);
        assertNotNull(createdBooking.getBookingId());
        assertEquals(bookingDTO.getDestinationId(), createdBooking.getDestinationId());
        assertEquals(bookingDTO.getStatus(), createdBooking.getStatus());
    }

    @Test
    void read() {
        BookingDTO booking = bookingDAO.read(1);  // Assuming ID 1 exists
        assertNotNull(booking);
        assertEquals(1, booking.getDestinationId());  // Adjust this according to your test data
    }

    @Test
    void readAll() {
        List<BookingDTO> bookings = bookingDAO.readAll();
        assertNotNull(bookings);
        assertEquals(3, bookings.size());  // Should match the number of bookings from Populate
    }

    @Test
    void update() {
        // Read the existing booking to ensure it exists before updating
        BookingDTO booking = bookingDAO.read(1);  // Assuming ID 1 exists
        assertNotNull(booking, "Booking should exist before updating.");  // Fail if booking is null

        // Update details
        booking.setDestinationId(2); // Ensure this destination ID exists
        booking.setDepartureDate(LocalDateTime.now().plusDays(2));
        booking.setArrivalDate(LocalDateTime.now().plusDays(5)); // Make sure the arrival date is after departure
        booking.setStatus(BookingStatus.CANCELLED);

        // Call update method
        BookingDTO updatedBooking = bookingDAO.update(1, booking); // Update booking with ID 1
        assertNotNull(updatedBooking, "Updated booking should not be null.");
        assertEquals(BookingStatus.CANCELLED, updatedBooking.getStatus());
        assertEquals(2, updatedBooking.getDestinationId()); // Verify the updated destination ID
    }


    @Test
    void delete() {
        // First, read the existing booking to verify it exists
        BookingDTO booking = bookingDAO.read(1);  // Assuming ID 1 exists
        assertNotNull(booking); // Ensure the booking exists before deletion

        // Delete the booking
        bookingDAO.delete(1); // Call the delete method with the booking ID

        // Verify the booking has been deleted
        BookingDTO deletedBooking = bookingDAO.read(1);
        assertNull(deletedBooking); // Ensure the booking is null after deletion
    }

    @Test
    void validatePrimaryKey() {
        assertTrue(bookingDAO.validatePrimaryKey(1)); // Assuming ID 1 exists
        assertFalse(bookingDAO.validatePrimaryKey(999)); // Assuming ID 999 does not exist
    }

    @AfterAll
    public void cleanUp() {
        // Cleanup to ensure no test pollution
        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM Booking").executeUpdate();
            em.createNativeQuery("ALTER SEQUENCE booking_id_seq RESTART WITH 1").executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace(); // Logs any exceptions that occur
        }
    }
}