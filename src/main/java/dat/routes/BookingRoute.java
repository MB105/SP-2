package dat.routes;

import dat.controllers.impl.BookingController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class BookingRoute {
    private final BookingController bookingController = new BookingController();

    protected EndpointGroup getRoutes() {

        return () -> {
            post("/", bookingController::create, Role.USER);
            get("/", bookingController::readAll, Role.USER);
            get("/{id}", bookingController::read, Role.USER, Role.ADMIN);
            put("/{id}", bookingController::update, Role.ADMIN);
            delete("/{id}", bookingController::delete, Role.USER, Role.ADMIN);
        };
    }
}
