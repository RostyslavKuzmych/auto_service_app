package application.controller;

import application.dto.master.MasterRequestDto;
import application.dto.master.MasterResponseDto;
import application.dto.order.OrderResponseDto;
import application.service.MasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.math.BigDecimal;
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

@Tag(name = "Master management", description = "Endpoints for master management")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/masters")
public class MasterController {
    private final MasterService masterService;

    @PostMapping
    @Operation(summary = "Create a master",
            description = "Endpoint for adding new master to the db")
    @ResponseStatus(HttpStatus.CREATED)
    public MasterResponseDto createMaster(@Valid @RequestBody MasterRequestDto masterRequestDto) {
        return masterService.createMaster(masterRequestDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a master",
            description = "Endpoint for updating a master by id")
    @ResponseStatus(HttpStatus.OK)
    public MasterResponseDto updateMaster(@PathVariable Long id,
                                          @RequestBody
                                          MasterRequestDto masterRequestDto) {
        return masterService.updateById(id, masterRequestDto);
    }

    @GetMapping("/{id}/orders")
    @Operation(summary = "Get all successful orders by master id",
            description = "Endpoint for getting all successful orders by master id")
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponseDto> getAllSuccessfulOrdersByMasterId(@PathVariable Long id) {
        return masterService.getAllSuccessfulOrdersByMasterId(id);
    }

    @GetMapping("/{id}/salary")
    @Operation(summary = "Get salary of a master by id",
            description = "Endpoint for getting salary of a master by id")
    @ResponseStatus(HttpStatus.OK)
    public BigDecimal getSalaryByMasterId(@PathVariable Long id) {
        return masterService.getSalaryByMasterId(id);
    }
}
