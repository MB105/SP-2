package dat.routes;

import dat.controllers.impl.DestinationController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class DestinationRoute {
    private final DestinationController destinationController = new DestinationController();

    protected EndpointGroup getRoutes() {

        return () -> {
            post("/", destinationController::create, Role.ADMIN);
            get("/", destinationController::readAll, Role.USER, Role.ADMIN);
            get("/{id}", destinationController::read, Role.USER, Role.ADMIN);
            put("/{id}", destinationController::update, Role.ADMIN);
            delete("/{id}", destinationController::delete, Role.ADMIN);
        };
    }
}

