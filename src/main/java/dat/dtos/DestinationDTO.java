package dat.dtos;

import dat.entities.Destination;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class DestinationDTO {
    private Integer id; // Assuming this is the primary key
    private String city;
    private String country;

    public DestinationDTO(Destination destination) {
        this.id = destination.getId();
        this.city = destination.getCity();
        this.country = destination.getCountry();
    }

    public static List<DestinationDTO> toDestinationDTOList(List<Destination> destinations) {
        return destinations.stream().map(DestinationDTO::new).collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DestinationDTO destinationDTO)) return false;

        return getId().equals(destinationDTO.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
