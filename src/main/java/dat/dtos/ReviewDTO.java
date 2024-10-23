package dat.dtos;

import dat.entities.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {
    private Integer id; // Unique identifier for the review
    private Integer destinationId; // ID of the associated destination
    private int rating; // Rating given in the review
    private String comment; // Comment provided in the review

    // Conversion constructor from Review entity
    public ReviewDTO(Review review) {
        if (review != null) { // Ensure review is not null
            this.id = review.getId();
            this.destinationId = review.getDestination() != null ? review.getDestination().getId() : null; // Safely handle destination
            this.rating = review.getRating();
            this.comment = review.getComment();
        }
    }

    // Constructor for creating a new review
    public ReviewDTO(Integer destinationId, int rating, String comment) {
        this.destinationId = destinationId;
        this.rating = rating;
        this.comment = comment;
    }

    // Method to convert a list of Review entities to ReviewDTOs
    public static List<ReviewDTO> toReviewDTOList(List<Review> reviews) {
        return reviews.stream()
                .map(ReviewDTO::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ReviewDTO reviewDto)) return false;

        return Objects.equals(id, reviewDto.id) &&
                Objects.equals(destinationId, reviewDto.destinationId) &&
                rating == reviewDto.rating &&
                Objects.equals(comment, reviewDto.comment);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, destinationId, rating, comment); // Use Objects.hash for null safety
    }
}
