package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.BookingDTO;
import dat.entities.Booking;
import dat.entities.BookingStatus;
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
    public BookingDTO read(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Booking booking = em.find(Booking.class, id);
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
            Booking booking = new Booking(bookingDTO);
            em.persist(booking);
            em.getTransaction().commit();
            return new BookingDTO(booking);
        }
    }

    @Override
    public BookingDTO update(Integer id, BookingDTO bookingDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Booking booking = em.find(Booking.class, id);

            if (booking != null) {
                booking.setDestinationId(bookingDTO.getDestinationId());
                booking.setDepartureDate(bookingDTO.getDepartureDate());
                booking.setArrivalDate(bookingDTO.getArrivalDate());
                booking.setBookingDate(bookingDTO.getBookingDate());
                booking.setStatus(bookingDTO.getStatus() != null ? BookingStatus.valueOf(bookingDTO.getStatus().name()) : booking.getStatus());
                em.merge(booking);
            }

            em.getTransaction().commit();
            return booking != null ? new BookingDTO(booking) : null;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Booking booking = em.find(Booking.class, id);
            if (booking != null) {
                em.remove(booking);
                em.getTransaction().commit();
                return true;
            }
            em.getTransaction().rollback();
            return false;
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            Booking booking = em.find(Booking.class, id);
            return booking != null;
        }
    }
}
