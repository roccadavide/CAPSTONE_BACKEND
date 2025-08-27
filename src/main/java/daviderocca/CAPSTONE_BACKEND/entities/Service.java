package daviderocca.CAPSTONE_BACKEND.entities;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "service")
@NoArgsConstructor
@Getter
@Setter
public class Service {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(name = "service_id")
    private UUID serviceId;

    private String title;

    @Column(name = "duration_min")
    private String durationMin;

    private String price;

    private String shortZ;

    private  String description;

    private String[] images;

    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;

    @OneToMany
    @JoinColumn(name = "booking_id")
    private List<Booking> bookings;

    public Service(String title, String durationMin, String price, String shortZ, String description, String[] images, Category category) {
        this.title = title;
        this.durationMin = durationMin;
        this.price = price;
        this.shortZ = shortZ;
        this.description = description;
        this.images = images;
        this.category = category;
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceId=" + serviceId +
                ", title='" + title + '\'' +
                ", durationMin='" + durationMin + '\'' +
                ", price='" + price + '\'' +
                ", shortZ='" + shortZ + '\'' +
                ", description='" + description + '\'' +
                ", images=" + Arrays.toString(images) +
                ", category=" + category +
                '}';
    }
}
