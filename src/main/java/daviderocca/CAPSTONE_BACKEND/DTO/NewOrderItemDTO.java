package daviderocca.CAPSTONE_BACKEND.DTO;


import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

public record NewOrderItemDTO (
                               @NotNull(message = "La quantità dei prodotti non può essere vuota")
                               @Min(value = 1, message = "La quantità deve essere almeno 1")
                               int quantity,
                               @NotNull(message = "Il prezzo dei prodotti non può essere vuoto")
                               @DecimalMin(value = "0.01", message = "Il prezzo deve essere maggiore di 0")
                               BigDecimal price,
                               @NotNull(message = "ID del prodotto è obbligatorio")
                               UUID productId,
                               @NotNull(message = "ID del ordine è obbligatorio")
                               UUID orderId
)
{}
