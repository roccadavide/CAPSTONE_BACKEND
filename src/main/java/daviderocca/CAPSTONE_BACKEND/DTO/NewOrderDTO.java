package daviderocca.CAPSTONE_BACKEND.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record NewOrderDTO (
                           @NotEmpty(message = "Il nome del cliente non può essere vuoto")
                           String customerName,
                           @Email(message = "Email non valida")
                           @NotEmpty(message = "L'email del cliente non può essere vuota")
                           String customerEmail,
                           @NotEmpty(message = "Il numero di telefono non può essere vuoto")
                           @Pattern(regexp = "\\+?[0-9]{7,15}", message = "Numero di telefono non valido")
                           String customerPhone,
                           UUID userId
)
{}
