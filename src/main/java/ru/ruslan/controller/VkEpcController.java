package ru.ruslan.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.ruslan.service.VkEpcService;

import java.time.Instant;

@RestController
//@RequestMapping("/api")
@RequiredArgsConstructor
public class VkEpcController {

    private final VkEpcService vkEpcService;

    @PostMapping(value = "${endpoints.embed-file}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VkEpcResponse> processVkFiles(@RequestBody VkEpcRequest request) {
        VkEpcResponse response = vkEpcService.processEpcData(request);
        return ResponseEntity.ok(response);
    }

    @Data
    public static class VkEpcRequest {
        @NotBlank(message = "Поле pathNewFileVK обязательно для заполнения")
        private String pathNewFileVK;
        @NotBlank(message = "Поле txtEpcParams обязательно для заполнения")
        private String textEpcParams;
        private String exerciseId;
        private String orderId;
        private String KNS;
        private String billingAccount;
        private Instant time;
    }

    @Data
    public static class VkEpcResponse {
        private String status;
        private String message;
    }
}
