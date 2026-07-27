package ru.ruslan.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.ruslan.model.CreateFileVKRequest;
import ru.ruslan.model.CreateFileVKResponse;
import ru.ruslan.service.VkEpcService;

@RestController
@RequestMapping("/api")
public class VkEpcController {

    private final VkEpcService vkEpcService;

    public VkEpcController(VkEpcService vkEpcService) {
        this.vkEpcService = vkEpcService;
    }

    @PostMapping(value = "/createFileVK",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CreateFileVKResponse> createFileVK(@RequestBody CreateFileVKRequest request) {
        CreateFileVKResponse response = vkEpcService.processRequest(request);
        return ResponseEntity.ok(response);
    }
}