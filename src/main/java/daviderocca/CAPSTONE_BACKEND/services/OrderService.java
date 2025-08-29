package daviderocca.CAPSTONE_BACKEND.services;

import daviderocca.CAPSTONE_BACKEND.entities.Order;
import daviderocca.CAPSTONE_BACKEND.exceptions.ResourceNotFoundException;
import daviderocca.CAPSTONE_BACKEND.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order findOrderById(UUID orderId) {
        return this.orderRepository.findById(orderId).orElseThrow(()-> new ResourceNotFoundException(orderId));
    }

}
