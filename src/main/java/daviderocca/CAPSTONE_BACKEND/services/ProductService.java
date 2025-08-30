package daviderocca.CAPSTONE_BACKEND.services;

import daviderocca.CAPSTONE_BACKEND.DTO.NewProductDTO;
import daviderocca.CAPSTONE_BACKEND.DTO.ProductResponseDTO;
import daviderocca.CAPSTONE_BACKEND.entities.Category;
import daviderocca.CAPSTONE_BACKEND.entities.Product;
import daviderocca.CAPSTONE_BACKEND.exceptions.BadRequestException;
import daviderocca.CAPSTONE_BACKEND.exceptions.ResourceNotFoundException;
import daviderocca.CAPSTONE_BACKEND.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryService categoryService;

    public Page<Product> findAllProducts(int pageNumber, int pageSize, String sort) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sort));
        return this.productRepository.findAll(pageable);
    }

    public Product findProductById(UUID productId) {
        return this.productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException(productId));
    }

    public ProductResponseDTO saveProduct(NewProductDTO payload) {

        if (productRepository.existsByName(payload.name())) {
            throw new IllegalArgumentException("Esiste già un prodotto con questo nome!");
        }

        Category relatedCategory = categoryService.findCategoryById(payload.categoryId());

        Product newProduct = new Product(payload.name(), payload.price(), payload.description(), payload.images(), payload.stock(), relatedCategory);
        Product savedProduct = productRepository.save(newProduct);



        log.info("Prodotto {} ({} - categoria {}) creato", savedProduct.getProductId(), savedProduct.getName(), relatedCategory.getCategoryId());

        return new ProductResponseDTO(savedProduct.getProductId(), savedProduct.getName(),
                savedProduct.getPrice(), savedProduct.getDescription(), savedProduct.getImages(),
                savedProduct.getStock(), relatedCategory.getCategoryId());
    }

    @Transactional
    public ProductResponseDTO findProductByIdAndUpdate(UUID productId, NewProductDTO payload) {
        Product found = findProductById(productId);

        if (productRepository.existsByName(payload.name())) {
            throw new IllegalArgumentException("Esiste già un prodotto con questo nome!");
        }

        Category relatedCategory = categoryService.findCategoryById(payload.categoryId());

        found.setName(payload.name());
        found.setPrice(payload.price());
        found.setDescription(payload.description());
        found.setImages(payload.images());
        found.setStock(payload.stock());
        found.setCategory(relatedCategory);

        Product modifiedProduct = productRepository.save(found);

        log.info("Prodotto {} aggiornato (categoria: {})", modifiedProduct.getProductId(), relatedCategory.getCategoryKey());

        return new ProductResponseDTO(modifiedProduct.getProductId(), modifiedProduct.getName(),
                modifiedProduct.getPrice(), modifiedProduct.getDescription(), modifiedProduct.getImages(),
                modifiedProduct.getStock(), relatedCategory.getCategoryId());
    }

    @Transactional
    public void findProductByIdAndDelete(UUID productId) {
        Product found = findProductById(productId);

        if (!found.getOrderItems().isEmpty()) {
            throw new BadRequestException("Non è possibile eliminare un prodotto già ordinato.");
        }

        productRepository.delete(found);
        log.info("Prodotto {} è stato eliminato!", found.getProductId());
    }

}
