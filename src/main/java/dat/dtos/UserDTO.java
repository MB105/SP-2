package dat.dtos;

import dat.entities.Gender;

public class UserDTO {
    private int id;
    private int age;
    private String username;
    private Gender gender; // Ændret til Gender enum
    private String email;

    public UserDTO(int id, int age, String username, Gender gender, String email) {
        this.id = id;
        this.age = age;
        this.username = username;
        this.gender = gender;
        this.email = email;
    }
}
