package dat.dtos;

import dat.entities.Destination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DestinationDTO {
    private Integer id;
    private String city;
    private String country;

    public DestinationDTO(Destination destination) {
        if (destination != null) {
            this.id = destination.getId() != null ? destination.getId() : null;
            this.city = destination.getCity();
            this.country = destination.getCountry();

        }
    }

    @Override
    public boolean equals(Object o) {
        // sammenligner dette objekt med et andet objekt for at tjekke om de er ens
        if (this == o) return true; // hvis de er det samme objekt, returner true
        if (!(o instanceof DestinationDTO destinationDTO)) return false; // tjekker om det er af typen DestinationDTO

        // bruger Objects.equals for at sammenligne id for at forhindre NullPointerException
        return Objects.equals(id, destinationDTO.id);
    }

    @Override
    public int hashCode() {
        // genererer hashkode baseret på id for at sikre at objekter med samme id får samme hash
        return Objects.hash(id); // bruger Objects.hash for null safety
    }
}
