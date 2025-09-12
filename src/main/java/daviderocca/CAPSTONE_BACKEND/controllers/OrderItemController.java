package daviderocca.CAPSTONE_BACKEND.controllers;

import daviderocca.CAPSTONE_BACKEND.DTO.NewOrderItemDTO;
import daviderocca.CAPSTONE_BACKEND.DTO.OrderItemResponseDTO;
import daviderocca.CAPSTONE_BACKEND.exceptions.BadRequestException;
import daviderocca.CAPSTONE_BACKEND.services.OrderItemService;
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
@RequestMapping("/api/orderItems")
@Slf4j
public class OrderItemController {

    @Autowired
    private OrderItemService orderItemService;

    // ---------------------------------- GET ----------------------------------

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<OrderItemResponseDTO> getAllOrderItems(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "orderItemId") String sort
    ) {
        log.info("Richiesta elenco items ordine - pagina: {}, size: {}, sort: {}", page, size, sort);
        return orderItemService.findAllOrderItems(page, size, sort);
    }

    @GetMapping("/{orderItemId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public OrderItemResponseDTO getOrderItemById(@PathVariable UUID orderItemId) {
        log.info("Richiesta dettaglio item ordine {}", orderItemId);
        return orderItemService.findOrderItemByIdAndConvert(orderItemId);
    }

    // ---------------------------------- POST ----------------------------------

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public OrderItemResponseDTO createOrderItem(@Validated @RequestBody NewOrderItemDTO payload, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining(", ")));
        }

        log.info("Richiesta creazione item ordine {} con prodotto {}", payload.orderId(), payload.productId());
        return orderItemService.saveOrderItem(payload);
    }

    // ---------------------------------- PUT ----------------------------------

    @PutMapping("/{orderItemId}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    public OrderItemResponseDTO updateOrderItem(
            @PathVariable UUID orderItemId,
            @Validated @RequestBody NewOrderItemDTO payload,
            BindingResult bindingResult
    ) {

        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining(", ")));
        }

        log.info("Richiesta aggiornamento item ordine {} con prodotto {}", orderItemId, payload.productId());
        return orderItemService.findOrderItemByIdAndUpdate(orderItemId, payload);
    }

    // ---------------------------------- DELETE ----------------------------------

    @DeleteMapping("/{orderItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteOrderItem(@PathVariable UUID orderItemId) {
        log.info("Richiesta eliminazione item ordine {}", orderItemId);
        orderItemService.findOrderItemByIdAndDelete(orderItemId);
    }

}
