package daviderocca.CAPSTONE_BACKEND.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "category")
@NoArgsConstructor
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(name = "category_id")
    private UUID categoryId;

    private String key;

    private String label;

    @OneToMany
    @JoinColumn(name = "id_product")
    private List<Product> products;

    @OneToMany
    @JoinColumn(name = "id_service")
    private List<Service> services;

    public Category(String key, String label) {
        this.key = key;
        this.label = label;
    }

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", key='" + key + '\'' +
                ", label='" + label + '\'' +
                '}';
    }
}
