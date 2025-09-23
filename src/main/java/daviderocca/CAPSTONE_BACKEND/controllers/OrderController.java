package daviderocca.CAPSTONE_BACKEND.controllers;

import daviderocca.CAPSTONE_BACKEND.DTO.NewOrderDTO;
import daviderocca.CAPSTONE_BACKEND.DTO.OrderResponseDTO;
import daviderocca.CAPSTONE_BACKEND.enums.OrderStatus;
import daviderocca.CAPSTONE_BACKEND.exceptions.BadRequestException;
import daviderocca.CAPSTONE_BACKEND.services.OrderService;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    // ---------------------------------- GET ----------------------------------

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<OrderResponseDTO> getAllOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "customerName") String sort
    ) {
        log.info("Richiesta elenco ordini - pagina: {}, size: {}, sort: {}", page, size, sort);
        return orderService.findAllOrders(page, size, sort);
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponseDTO getOrderById(@PathVariable UUID orderId) {
        log.info("Richiesta dettaglio ordine {}", orderId);
        return orderService.findOrderByIdAndConvert(orderId);
    }

    // ---------------------------------- POST ----------------------------------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDTO createOrder(@Validated @RequestBody NewOrderDTO payload, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining(", ")));
        }

        log.info("Richiesta creazione ordine {}", payload.customerEmail());
        return orderService.saveOrder(payload);
    }

    // ---------------------------------- PUT ----------------------------------

    @PutMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponseDTO updateOrder(
            @PathVariable UUID orderId,
            @Validated @RequestBody NewOrderDTO payload,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining(", ")));
        }

        log.info("Richiesta aggiornamento ordine {}", orderId);
        return orderService.findOrderByIdAndUpdate(orderId, payload);
    }

    // ---------------------------------- PATCH ----------------------------------

    @PatchMapping("/{orderId}/status")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponseDTO updateOrderStatus(
            @PathVariable UUID orderId,
            @RequestParam @NotBlank OrderStatus status
    ) {
        log.info("Richiesta aggiornamento status ordine {} -> {}", orderId, status);
        return orderService.updateOrderStatus(orderId, status);
    }

    // ---------------------------------- DELETE ----------------------------------

    @DeleteMapping("/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrder(@PathVariable UUID orderId) {
        log.info("Richiesta eliminazione ordine {}", orderId);
        orderService.findOrderByIdAndDelete(orderId);
    }
}