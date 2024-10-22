package dat.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int id;
    private int age;
    private String username;

    @Enumerated(EnumType.STRING)  // Brug enum som string
    private Gender gender;

    private String email;

    public User(int id, int age, String username, Gender gender, String email) {
        this.id = id;
        this.age = age;
        this.username = username;
        this.gender = gender;
        this.email = email;
    }
}
