package daviderocca.CAPSTONE_BACKEND.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.UUID;

@Entity
@Table(name = "product")
@NoArgsConstructor
@Getter
@Setter
public class Product {

    @Id
    @GeneratedValue
    @Setter(AccessLevel.NONE)
    @Column(name = "product_id")
    private UUID productId;

    private String name;

    private String price;

    private String description;

    private String[] images;

    private int stock;

    @ManyToOne
    @JoinColumn(name = "category")
    private Category category;

    public Product(String name, String price, String description, String[] images, int stock, Category category) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.images = images;
        this.stock = stock;
        this.category = category;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", price='" + price + '\'' +
                ", description='" + description + '\'' +
                ", images=" + Arrays.toString(images) +
                ", stock=" + stock +
                ", category=" + category +
                '}';
    }
}
