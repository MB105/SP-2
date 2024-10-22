package dat.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "destination")
public class Destination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;
    private String city;
    private String country;

    public Destination(int id, String city, String country) {
        this.id = id;
        this.city = city;
        this.country = country;
    }
}
