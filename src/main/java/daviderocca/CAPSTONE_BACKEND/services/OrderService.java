package daviderocca.CAPSTONE_BACKEND.services;

import daviderocca.CAPSTONE_BACKEND.DTO.NewOrderDTO;
import daviderocca.CAPSTONE_BACKEND.DTO.OrderItemResponseDTO;
import daviderocca.CAPSTONE_BACKEND.DTO.OrderResponseDTO;
import daviderocca.CAPSTONE_BACKEND.entities.Order;
import daviderocca.CAPSTONE_BACKEND.entities.User;
import daviderocca.CAPSTONE_BACKEND.enums.OrderStatus;
import daviderocca.CAPSTONE_BACKEND.exceptions.BadRequestException;
import daviderocca.CAPSTONE_BACKEND.exceptions.ResourceNotFoundException;
import daviderocca.CAPSTONE_BACKEND.repositories.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    public Page<OrderResponseDTO> findAllOrders(int pageNumber, int pageSize, String sort) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(sort));
        Page<Order> page = this.orderRepository.findAll(pageable);

        return page.map(order -> {
            List<OrderItemResponseDTO> orderItemDTOs = order.getOrderItems().stream()
                    .map(item -> new OrderItemResponseDTO(
                            item.getOrderItemId(),
                            item.getQuantity(),
                            item.getPrice(),
                            item.getProduct().getProductId(),
                            item.getOrder().getOrderId()
                    ))
                    .toList();

            return new OrderResponseDTO(
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getCustomerEmail(),
                    order.getCustomerPhone(),
                    order.getOrderStatus(),
                    order.getCreatedAt(),
                    order.getUser() != null ? order.getUser().getUserId() : null,
                    orderItemDTOs
            );
        });
    }

    public Order findOrderById(UUID orderId) {
        return this.orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(orderId));
    }

    public OrderResponseDTO findOrderByIdAndConvert(UUID orderId) {
        Order found = this.orderRepository.findById(orderId).orElseThrow(()-> new ResourceNotFoundException(orderId));

        List<OrderItemResponseDTO> orderItemDTOs = found.getOrderItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getOrderItemId(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getProduct().getProductId(),
                        item.getOrder().getOrderId()
                ))
                .toList();

        return new OrderResponseDTO(found.getOrderId(), found.getCustomerName(),
                found.getCustomerEmail(), found.getCustomerPhone(), found.getOrderStatus(),
                found.getCreatedAt(), found.getUser() != null ? found.getUser().getUserId() : null,
                orderItemDTOs);

    }

    public OrderResponseDTO saveOrder(NewOrderDTO payload) {

        User relatedUser = null;
        if (payload.userId() != null) {
            relatedUser = userService.findUserById(payload.userId());
        }

        Order newOrder = new Order(payload.customerName(), payload.customerEmail(), payload.customerPhone(), relatedUser);
        Order savedOrder = orderRepository.save(newOrder);

        List<OrderItemResponseDTO> orderItemDTOs = savedOrder.getOrderItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getOrderItemId(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getProduct().getProductId(),
                        item.getOrder().getOrderId()
                ))
                .toList();

        log.info("Ordine {} creato (stato: {}).", savedOrder.getOrderId(), savedOrder.getOrderStatus());

        return new OrderResponseDTO(savedOrder.getOrderId(), savedOrder.getCustomerName(),
                savedOrder.getCustomerEmail(), savedOrder.getCustomerPhone(), savedOrder.getOrderStatus(),
                savedOrder.getCreatedAt(), relatedUser != null ? relatedUser.getUserId() : null, orderItemDTOs);
    }

    @Transactional
    public OrderResponseDTO findOrderByIdAndUpdate(UUID orderId, NewOrderDTO payload) {
        Order found = findOrderById(orderId);


        if (found.getOrderStatus().equals(OrderStatus.COMPLETED) || found.getOrderStatus().equals(OrderStatus.CANCELED)) {
            throw new BadRequestException("L'ordine non è modificabile in stato " + found.getOrderStatus());
        }

        User relatedUser = null;
        if (payload.userId() != null) {
            relatedUser = userService.findUserById(payload.userId());
        }

        found.setCustomerName(payload.customerName());
        found.setCustomerEmail(payload.customerEmail());
        found.setCustomerPhone(payload.customerPhone());

        if (payload.userId() != null) {
            found.setUser(userService.findUserById(payload.userId()));
        } else {
            found.setUser(null);
        }

        Order modifiedOrder = orderRepository.save(found);

        List<OrderItemResponseDTO> orderItemDTOs = modifiedOrder.getOrderItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getOrderItemId(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getProduct().getProductId(),
                        item.getOrder().getOrderId()
                ))
                .toList();

        log.info("Ordine {} aggiornato (stato: {}).", modifiedOrder.getOrderId(), modifiedOrder.getOrderStatus());

        return new OrderResponseDTO(modifiedOrder.getOrderId(), modifiedOrder.getCustomerName(),
                modifiedOrder.getCustomerEmail(), modifiedOrder.getCustomerPhone(), modifiedOrder.getOrderStatus(),
                modifiedOrder.getCreatedAt(), relatedUser != null ? relatedUser.getUserId() : null, orderItemDTOs);
    }

    @Transactional
    public OrderResponseDTO updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        Order found = findOrderById(orderId);

        if (found.getOrderStatus().equals(OrderStatus.CANCELED) || found.getOrderStatus().equals(OrderStatus.COMPLETED)) {
            throw new BadRequestException("Non puoi aggiornare lo stato di un ordine " + found.getOrderStatus());
        }

        found.setOrderStatus(newStatus);
        Order updatedOrder = orderRepository.save(found);

        List<OrderItemResponseDTO> orderItemDTOs = updatedOrder.getOrderItems().stream()
                .map(item -> new OrderItemResponseDTO(
                        item.getOrderItemId(),
                        item.getQuantity(),
                        item.getPrice(),
                        item.getProduct().getProductId(),
                        item.getOrder().getOrderId()
                ))
                .toList();

        log.info("Stato ordine {} aggiornato a {}", updatedOrder.getOrderId(), updatedOrder.getOrderStatus());
        return new OrderResponseDTO(updatedOrder.getOrderId(), updatedOrder.getCustomerName(),
                updatedOrder.getCustomerEmail(), updatedOrder.getCustomerPhone(), updatedOrder.getOrderStatus(),
                updatedOrder.getCreatedAt(), updatedOrder.getUser() != null ? updatedOrder.getUser().getUserId() : null,
                orderItemDTOs);
    }

    @Transactional
    public void findOrderByIdAndDelete(UUID orderId) {
        Order found = findOrderById(orderId);

        if (found.getOrderStatus().equals(OrderStatus.COMPLETED) || found.getOrderStatus().equals(OrderStatus.CANCELED)) {
            throw new BadRequestException("Non è possibile eliminare un ordine in stato " + found.getOrderStatus());
        }

        orderRepository.delete(found);
        log.info("Order {} è stato eliminato correttamente!", found.getOrderId());
    }


}
