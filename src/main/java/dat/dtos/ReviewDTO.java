package dat.dtos;

import dat.entities.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Integer id;
    private Integer destinationId;
    private int rating;
    private String comment;

    public ReviewDTO(Review review) {
        if (review != null) {
            this.id = review.getId();
            this.destinationId = review.getDestination() != null ? review.getDestination().getId() : null;
            this.rating = review.getRating();
            this.comment = review.getComment();
        }
    }

    @Override
    public boolean equals(Object o) {
        // sammenligner dette objekt med et andet objekt for at tjekke om de er ens
        if (this == o) return true; // hvis de er det samme objekt, returner true
        if (!(o instanceof ReviewDTO reviewDto)) return false; // tjekker om det er af typen ReviewDTO

        // sammenligner id, destinationId, rating og kommentar for lighed
        return Objects.equals(id, reviewDto.id) &&
                Objects.equals(destinationId, reviewDto.destinationId) && // bruger Objects.equals for at undgå NullPointerException
                rating == reviewDto.rating &&
                Objects.equals(comment, reviewDto.comment); // bruger Objects.equals for at sammenligne comments
    }

    @Override
    public int hashCode() {
        // genererer hashkode baseret på id, destinationId, rating og kommentar for at sikre unik identifikation
        return Objects.hash(id, destinationId, rating, comment); // bruger Objects.hash for null safety
    }
}
