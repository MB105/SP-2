package dat.daos.impl;

import dat.daos.IDAO;
import dat.dtos.BookingDTO;
import dat.entities.Booking;
import dat.entities.Destination;
import io.javalin.http.BadRequestResponse;
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
        // opretter en ny instans af bookingdao
        if (instance == null) {
            emf = _emf;
            instance = new BookingDAO();
        }
        return instance;
    }

    @Override
    public BookingDTO read(Integer bookingId) {
        // tjekker om booking id er null
        if (bookingId == null) {
            throw new IllegalArgumentException("booking id cannot be null.");
        }
        try (EntityManager em = emf.createEntityManager()) {
            // finder booking med det givne booking id
            Booking booking = em.find(Booking.class, bookingId);
            return booking != null ? new BookingDTO(booking) : null;
        }
    }

    @Override
    public List<BookingDTO> readAll() {
        try (EntityManager em = emf.createEntityManager()) {
            // henter alle bookinger som booking dtos
            TypedQuery<BookingDTO> query = em.createQuery("SELECT new dat.dtos.BookingDTO(b) FROM Booking b", BookingDTO.class);
            return query.getResultList();
        }
    }

    @Override
    public BookingDTO create(BookingDTO bookingDTO) {
        // tjekker om booking dto er null
        if (bookingDTO == null) {
            throw new IllegalArgumentException("booking dto cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // henter destination baseret på bynavn
            TypedQuery<Destination> query = em.createQuery("SELECT d FROM Destination d WHERE d.city = :city", Destination.class);
            query.setParameter("city", bookingDTO.getDestinationCity());
            Destination destination = query.getResultStream().findFirst().orElse(null);

            // tjekker om destination findes
            if (destination == null) {
                em.getTransaction().rollback();
                throw new BadRequestResponse("destination not found.");
            }

            // opretter booking med tilknyttede destination
            Booking booking = new Booking(bookingDTO, destination);
            em.persist(booking);
            em.getTransaction().commit();
            return new BookingDTO(booking);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestResponse("error while creating booking: " + e.getMessage());
        }
    }

    @Override
    public BookingDTO update(Integer bookingId, BookingDTO bookingDTO) {
        // tjekker om booking id eller booking dto er null
        if (bookingId == null || bookingDTO == null) {
            throw new IllegalArgumentException("booking id and booking dto cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // finder booking med det givne booking id
            Booking booking = em.find(Booking.class, bookingId);
            if (booking != null) {
                // henter destination baseret på bynavn
                TypedQuery<Destination> destQuery = em.createQuery("SELECT d FROM Destination d WHERE d.city = :city", Destination.class);
                destQuery.setParameter("city", bookingDTO.getDestinationCity());
                Destination destination = destQuery.getResultStream().findFirst().orElse(null);

                // tjekker om destination findes
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
            throw new BadRequestResponse("booking not found or invalid destination.");
        }
    }

    @Override
    public void delete(Integer bookingId) {
        // tjekker om booking id er null
        if (bookingId == null) {
            throw new IllegalArgumentException("booking id cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            // finder booking med det givne booking id
            Booking booking = em.find(Booking.class, bookingId);
            if (booking != null) {
                em.remove(booking);
            } else {
                throw new BadRequestResponse("booking not found.");
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer bookingId) {
        // tjekker om booking id er null
        if (bookingId == null) {
            throw new IllegalArgumentException("booking id cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            // finder booking med det givne booking id
            Booking booking = em.find(Booking.class, bookingId);
            return booking != null;
        }
    }
}
