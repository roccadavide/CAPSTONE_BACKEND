package daviderocca.CAPSTONE_BACKEND.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "order")
@NoArgsConstructor
@Getter
@Setter
public class Order {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(name = "order_id")
    private UUID orderId;



}
