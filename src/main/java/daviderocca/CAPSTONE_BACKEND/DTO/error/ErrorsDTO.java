package daviderocca.CAPSTONE_BACKEND.DTO.error;

import java.time.LocalDateTime;

public record ErrorsDTO(
        String message,
        LocalDateTime timestamp)
{}
