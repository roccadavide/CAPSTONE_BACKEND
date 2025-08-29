package daviderocca.CAPSTONE_BACKEND.services;

import daviderocca.CAPSTONE_BACKEND.DTO.NewOrderItemDTO;
import daviderocca.CAPSTONE_BACKEND.DTO.OrderItemResponseDTO;
import daviderocca.CAPSTONE_BACKEND.entities.Order;
import daviderocca.CAPSTONE_BACKEND.entities.OrderItem;
import daviderocca.CAPSTONE_BACKEND.entities.Product;
import daviderocca.CAPSTONE_BACKEND.exceptions.ResourceNotFoundException;
import daviderocca.CAPSTONE_BACKEND.repositories.OrderItemRepository;
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
public class OrderItemService {

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;

    public Page<OrderItem> findAllOrderItems(int pageNumber, int pageSize, String sort) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sort));
        return this.orderItemRepository.findAll(pageable);
    }

    public OrderItem findOrderItemById(UUID orderItemId) {
        return this.orderItemRepository.findById(orderItemId).orElseThrow(()-> new ResourceNotFoundException(orderItemId));
    }


    public OrderItemResponseDTO saveOrderItem(NewOrderItemDTO payload) {

        Product relatedProduct = productService.findProductById(payload.productId());
        Order relatedOrder = orderService.findOrderById(payload.orderId());

        OrderItem newOrderItem = new OrderItem(payload.quantity(), payload.price(), relatedProduct, relatedOrder);
        OrderItem savedOrderItem = orderItemRepository.save(newOrderItem);

        log.info("OrderItem {} salvato per ordine {} e prodotto {}", savedOrderItem.getOrderItemId(), relatedOrder.getOrderId(), relatedProduct.getProductId());

        return new OrderItemResponseDTO(savedOrderItem.getOrderItemId(), savedOrderItem.getQuantity(),
                savedOrderItem.getPrice(), relatedProduct.getProductId(), relatedOrder.getOrderId());
    }

    @Transactional
    public OrderItemResponseDTO findOrderItemByIdAndUpdate(UUID orderItemId, NewOrderItemDTO payload) {
        OrderItem found = findOrderItemById(orderItemId);

        Product relatedProduct = productService.findProductById(payload.productId());
        Order relatedOrder = orderService.findOrderById(payload.orderId());

        found.setQuantity(payload.quantity());
        found.setPrice(payload.price());
        found.setProduct(relatedProduct);
        found.setOrder(relatedOrder);

        OrderItem modifiedOrderItem = orderItemRepository.save(found);

        log.info("OrderItem {} modificato per ordine {} e prodotto {}", modifiedOrderItem.getOrderItemId(), relatedOrder.getOrderId(), relatedProduct.getProductId());

        return new OrderItemResponseDTO(modifiedOrderItem.getOrderItemId(), modifiedOrderItem.getQuantity(),
                modifiedOrderItem.getPrice(), relatedProduct.getProductId(), relatedOrder.getOrderId());
    }

    @Transactional
    public void findOrderItemByIdAndDelete(UUID orderItemId) {
        OrderItem found = findOrderItemById(orderItemId);
        orderItemRepository.delete(found);
        log.info("OrderItem {} è stato eliminato!", found.getOrderItemId());
    }

}
