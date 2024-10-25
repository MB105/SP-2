package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.BookingDAO;
import dat.dtos.BookingDTO;
import dat.exceptions.Message;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class BookingController implements IController<BookingDTO, Integer> {

    private BookingDAO dao;

    public BookingController() {
        // Henter EntityManagerFactory og initialiserer BookingDAO
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = BookingDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        // Læser booking id fra URL'en
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        // Finder booking med det givne id
        BookingDTO bookingDTO = dao.read(id);

        // Returnerer booking hvis fundet ellers får man en 404 status
        if (bookingDTO != null) {
            ctx.res().setStatus(200);
            ctx.json(bookingDTO, BookingDTO.class);
        } else {
            ctx.res().setStatus(404);
            ctx.json(new Message(404, "Booking not found"));
        }
    }

    @Override
    public void readAll(Context ctx) {
        // Henter alle bookinger
        List<BookingDTO> bookings = dao.readAll();

        // Returnerer listen over bookinger
        ctx.res().setStatus(200);
        ctx.json(bookings, BookingDTO.class);
    }

    @Override
    public void create(Context ctx) {
        // Validerer og henter booking dataen fra request body
        BookingDTO bookingRequest = validateEntity(ctx);

        // Opretter ny booking i databasen
        BookingDTO newBooking = dao.create(bookingRequest);

        // Returnerer den nye booking med status 201
        ctx.res().setStatus(201);
        ctx.json(newBooking, BookingDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // Læser booking id fra URL'en
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        // Opdaterer eksisterende booking med nye data
        BookingDTO updatedBooking = dao.update(id, validateEntity(ctx));

        // Returnerer opdateret booking hvis fundet ellers får man en 404 status
        if (updatedBooking != null) {
            ctx.res().setStatus(200);
            ctx.json(updatedBooking, BookingDTO.class);
        } else {
            ctx.res().setStatus(404);
            ctx.json(new Message(404, "Booking not found"));
        }
    }

    @Override
    public void delete(Context ctx) {
        // Læser booking id fra URL'en
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        // Sletter booking med det givne id
        dao.delete(id);

        // Returnerer status 204
        ctx.res().setStatus(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        // Validerer, om booking id er gyldigt
        return dao.validatePrimaryKey(integer);
    }

    @Override
    public BookingDTO validateEntity(Context ctx) {
        // Validerer booking data i request body
        return ctx.bodyValidator(BookingDTO.class)
                .check(b -> b.getDestinationCity() != null, "Not a valid destination city")
                .check(b -> b.getDepartureDate() != null, "Not a valid departure date")
                .check(b -> b.getArrivalDate() != null, "Not a valid arrival date")
                .check(b -> b.getBookingDate() != null, "Not a valid booking date")
                .check(b -> b.getStatus() != null, "Not a valid booking status")
                .get();
    }
}
