package daviderocca.CAPSTONE_BACKEND.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import daviderocca.CAPSTONE_BACKEND.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.UUID;


@Entity
@Table(name = "user")
@NoArgsConstructor
@Getter
@Setter
@JsonIgnoreProperties({"password","authorities","enabled","accountNonExpired","credentialsNonExpired","accountNonLocked"})
public class User {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(name = "user_id")
    private UUID userId;

    private String name;

    private String surname;

    private String email;

    private String password;

    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;


    public User(UUID userId, String name, String surname, String email, String password, String phone) {
        this.userId = userId;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.role = Role.COSTUMER;
    }
}
