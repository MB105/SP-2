package dat.dtos;

import dat.entities.Destination;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DestinationDTO {
    private Integer id; // Assuming this is the primary key
    private String city;
    private String country;

    public DestinationDTO(Destination destination) {
        if (destination != null) { // Ensure destination is not null
            this.id = destination.getId();
            this.city = destination.getCity();
            this.country = destination.getCountry();
        }
    }

    public static List<DestinationDTO> toDestinationDTOList(List<Destination> destinations) {
        return destinations.stream().map(DestinationDTO::new).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DestinationDTO destinationDTO)) return false;

        return Objects.equals(id, destinationDTO.id); // Use Objects.equals to prevent NullPointerException
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Use Objects.hash for null safety
    }
}
