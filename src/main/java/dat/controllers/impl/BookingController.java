package dat.controllers.impl;

import dat.config.HibernateConfig;
import dat.controllers.IController;
import dat.daos.impl.BookingDAO;
import dat.dtos.BookingDTO;
import dat.entities.BookingStatus;
import dat.exceptions.Message;
import io.javalin.http.Context;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class BookingController implements IController<BookingDTO, Integer> {

    private BookingDAO dao;

    public BookingController() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        this.dao = BookingDAO.getInstance(emf);
    }

    @Override
    public void read(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        // entity
        BookingDTO bookingDTO = dao.read(id);

        // response
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
        // entity
        List<BookingDTO> bookings = dao.readAll();

        // response
        ctx.res().setStatus(200);
        ctx.json(bookings, BookingDTO.class);
    }

    @Override
    public void create(Context ctx) {
        // request
        BookingDTO bookingRequest = validateEntity(ctx);

        // entity
        BookingDTO newBooking = dao.create(bookingRequest);

        // response
        ctx.res().setStatus(201);
        ctx.json(newBooking, BookingDTO.class);
    }

    @Override
    public void update(Context ctx) {
        // request
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        // entity
        BookingDTO updatedBooking = dao.update(id, validateEntity(ctx));

        // response
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
        // request
        int id = ctx.pathParamAsClass("id", Integer.class)
                .check(this::validatePrimaryKey, "Not a valid id")
                .get();

        // entity
       dao.delete(id);

        // response
        ctx.res().setStatus(204);
    }

    @Override
    public boolean validatePrimaryKey(Integer integer) {
        return dao.validatePrimaryKey(integer);
    }

    @Override
    public BookingDTO validateEntity(Context ctx) {
        return ctx.bodyValidator(BookingDTO.class)
                .check(b -> b.getBookingId() > 0, "Not a valid booking ID")
                .check(b -> b.getDestinationId() > 0, "Not a valid destination ID")
                .check(b -> b.getDepartureDate() != null, "Not a valid departure date")
                .check(b -> b.getArrivalDate() != null, "Not a valid arrival date")
                .check(b -> b.getBookingDate() != null, "Not a valid booking date")
                .check(b -> b.getStatus() != null && b.getStatus() instanceof BookingStatus, "Not a valid booking status")
                .get();
    }
}
