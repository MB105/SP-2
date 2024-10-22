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
            post("/", destinationController::create, Role.USER);
            get("/", destinationController::readAll);
            get("/{id}", destinationController::read);
            put("/{id}", destinationController::update);
            delete("/{id}", destinationController::delete);
        };
    }
}

