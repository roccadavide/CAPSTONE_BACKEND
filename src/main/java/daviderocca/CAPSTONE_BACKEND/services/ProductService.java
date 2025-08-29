package daviderocca.CAPSTONE_BACKEND.services;

import daviderocca.CAPSTONE_BACKEND.entities.Product;
import daviderocca.CAPSTONE_BACKEND.exceptions.ResourceNotFoundException;
import daviderocca.CAPSTONE_BACKEND.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product findProductById(UUID productId) {
        return this.productRepository.findById(productId).orElseThrow(()-> new ResourceNotFoundException(productId));
    }

}
