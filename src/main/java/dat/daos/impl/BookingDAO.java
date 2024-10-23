package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.BookingDTO;
import dat.entities.Booking;
import dat.entities.Destination;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.TypedQuery;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class BookingDAO implements IDAO<BookingDTO, Integer> {

    private static BookingDAO instance;
    private static EntityManagerFactory emf;

    public static BookingDAO getInstance(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new BookingDAO();
        }
        return instance;
    }

    @Override
    public BookingDTO read(Integer bookingId) {
        try (EntityManager em = emf.createEntityManager()) {
            Booking booking = em.find(Booking.class, bookingId);
            return booking != null ? new BookingDTO(booking) : null;
        }
    }

    @Override
    public List<BookingDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<BookingDTO> query = em.createQuery("SELECT new dat.dtos.BookingDTO(b) FROM Booking b", BookingDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public BookingDTO create(BookingDTO bookingDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Fetch the Destination based on the destinationId
            Destination destination = em.find(Destination.class, bookingDTO.getDestinationId());
            if (destination == null) {
                em.getTransaction().rollback();
                return null; // Handle the case where the destination does not exist
            }

            Booking booking = new Booking(bookingDTO, destination); // Pass the destination
            em.persist(booking);
            em.getTransaction().commit();
            return new BookingDTO(booking);
        }
    }

    @Override
    public BookingDTO update(Integer bookingId, BookingDTO bookingDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Booking booking = em.find(Booking.class, bookingId);
            if (booking != null) {
                Destination destination = em.find(Destination.class, bookingDTO.getDestinationId());
                if (destination != null) {
                    booking.setDestination(destination);
                    booking.setDepartureDate(bookingDTO.getDepartureDate());
                    booking.setArrivalDate(bookingDTO.getArrivalDate());
                    booking.setBookingDate(bookingDTO.getBookingDate());
                    booking.setStatus(bookingDTO.getStatus());
                    em.getTransaction().commit();
                    return new BookingDTO(booking);
                }
            }
            em.getTransaction().rollback();
            return null; // Handle the case where the booking doesn't exist
        }
    }

    @Override
    public void delete(Integer bookingId) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Booking booking = em.find(Booking.class, bookingId);
            if (booking != null) {
                em.remove(booking);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer bookingId) {
        try (EntityManager em = emf.createEntityManager()) {
            Booking booking = em.find(Booking.class, bookingId);
            return booking != null;
        }
    }
}
