package application.controller;

import application.dto.order.OrderResponseDto;
import application.dto.owner.OwnerRequestDto;
import application.dto.owner.OwnerResponseDto;
import application.dto.owner.OwnerResponseDtoWithCars;
import application.service.OwnerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Owner management", description = "Endpoints for owner management")
@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {
    private final OwnerService ownerService;

    @PostMapping
    @Operation(summary = "Create an owner", description = "Endpoint for adding new owner to the db")
    @ResponseStatus(HttpStatus.CREATED)
    public OwnerResponseDto createOwner() {
        return ownerService.createOwner();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an owner", description = "Endpoint for updating owner by id")
    @ResponseStatus(HttpStatus.OK)
    public OwnerResponseDtoWithCars updateOwnerById(@PathVariable Long id,
                                                    @Valid
                                            @RequestBody OwnerRequestDto ownerRequestDto) {
        return ownerService.updateOwnerByid(id, ownerRequestDto);
    }

    @GetMapping("/{id}/orders")
    @Operation(summary = "Get orders by owner id",
            description = "Endpoint for getting all orders by owner id")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponseDto> getAllOrdersByOwnerId(@PathVariable Long id) {
        return ownerService.getAllOrdersByOwnerId(id);
    }
}
