package dat.routes;

import dat.controllers.impl.ReviewController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.delete;

public class ReviewRoute {
    private final ReviewController reviewController = new ReviewController();

    protected EndpointGroup getRoutes() {

        return () -> {
            post("/", reviewController::create, Role. USER, Role.ADMIN);
            get("/", reviewController::readAll, Role.ANYONE);
            get("/{id}", reviewController::read, Role.USER, Role.ADMIN);
            delete("/{id}", reviewController::delete, Role.ADMIN);
        };
    }
}