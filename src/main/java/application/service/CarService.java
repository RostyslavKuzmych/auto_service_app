package application.service;

import application.dto.car.CarRequestDto;
import application.dto.car.CarResponseDto;

public interface CarService {
    CarResponseDto createCar(CarRequestDto carRequestDto);

    CarResponseDto updateCarById(Long id, CarRequestDto carRequestDto);
}
