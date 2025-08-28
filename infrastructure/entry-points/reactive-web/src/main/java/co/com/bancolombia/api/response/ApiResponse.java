package co.com.bancolombia.api.response;

import lombok.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ApiResponse <T>{
    private T data;
    private int status;
    private String message;
}
