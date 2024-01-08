package application.controller;

import application.dto.car.CarRequestDto;
import application.dto.car.CarResponseDto;
import application.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Car management", description = "Endpoints for car management")
@RequiredArgsConstructor
@RequestMapping("/api/cars")
public class CarController {
    private final CarService carService;

    @PostMapping
    @Operation(summary = "Create a car", description = "Endpoint for adding new car to the db")
    @ResponseStatus(HttpStatus.CREATED)
    public CarResponseDto createCar(@Valid @RequestBody CarRequestDto carRequestDto) {
        return carService.createCar(carRequestDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a car", description = "Endpoint for updating a car by id")
    @ResponseStatus(HttpStatus.OK)
    public CarResponseDto updateCarByid(@PathVariable Long id,
                                        @RequestBody CarRequestDto requestDto) {
        return carService.updateCarById(id, requestDto);
    }
}
