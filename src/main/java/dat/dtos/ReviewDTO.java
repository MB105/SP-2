package dat.dtos;

public class ReviewDTO {
    private int id;
    private int userId;
    private int destinationId;
    private int rating;
    private String comment;

    public ReviewDTO(int id, int userId, int destinationId, int rating, String comment) {
        this.id = id;
        this.userId = userId;
        this.destinationId = destinationId;
        this.rating = rating;
        this.comment = comment;
    }
}
