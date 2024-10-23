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
        if (instance == null) {
            emf = _emf;
            instance = new BookingDAO();
        }
        return instance;
    }

    @Override
    public BookingDTO read(Integer bookingId) {
        if (bookingId == null) {
            throw new IllegalArgumentException("Booking ID cannot be null.");
        }
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
        if (bookingDTO == null) {
            throw new IllegalArgumentException("BookingDTO cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Fetch the Destination based on the destination city name
            TypedQuery<Destination> query = em.createQuery("SELECT d FROM Destination d WHERE d.city = :city", Destination.class);
            query.setParameter("city", bookingDTO.getDestinationCity());
            Destination destination = query.getResultStream().findFirst().orElse(null); // Handle case where city is not found

            if (destination == null) {
                em.getTransaction().rollback();

                return null; // Handle the case where the destination city does not exist

                throw new BadRequestResponse("Destination not found.");

            }

            Booking booking = new Booking(bookingDTO, destination); // Pass the destination
            em.persist(booking);
            em.getTransaction().commit();
            return new BookingDTO(booking);
        } catch (Exception e) {

             
            e.printStackTrace();
            throw new BadRequestResponse("Error while creating booking: " + e.getMessage());

        }
    }

    @Override
    public BookingDTO update(Integer bookingId, BookingDTO bookingDTO) {
        if (bookingId == null || bookingDTO == null) {
            throw new IllegalArgumentException("Booking ID and BookingDTO cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Booking booking = em.find(Booking.class, bookingId);
            if (booking != null) {

                Destination destination = em.find(Destination.class, bookingDTO.getDestinationCity());

                // Fetch destination by city
                TypedQuery<Destination> destQuery = em.createQuery("SELECT d FROM Destination d WHERE d.city = :city", Destination.class);
                destQuery.setParameter("city", bookingDTO.getDestinationCity());
                Destination destination = destQuery.getResultStream().findFirst().orElse(null);


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
            throw new BadRequestResponse("Booking not found or invalid destination.");
        }
    }

    @Override
    public void delete(Integer bookingId) {
        if (bookingId == null) {
            throw new IllegalArgumentException("Booking ID cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Booking booking = em.find(Booking.class, bookingId);
            if (booking != null) {
                em.remove(booking);
            } else {
                throw new BadRequestResponse("Booking not found.");
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public boolean validatePrimaryKey(Integer bookingId) {
        if (bookingId == null) {
            throw new IllegalArgumentException("Booking ID cannot be null.");
        }

        try (EntityManager em = emf.createEntityManager()) {
            Booking booking = em.find(Booking.class, bookingId);
            return booking != null;
        }
    }
}
