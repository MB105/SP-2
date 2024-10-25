package dat.routes;

import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final BookingRoute bookingRoute = new BookingRoute();
    private final DestinationRoute destinationRoute = new DestinationRoute();
    private final ReviewRoute reviewRoute = new ReviewRoute();

    public EndpointGroup getRoutes() {
        return () -> {
                path("/bookings", bookingRoute.getRoutes());
                path("/destinations", destinationRoute.getRoutes());
                path("/reviews", reviewRoute.getRoutes());
        };
    }
}
