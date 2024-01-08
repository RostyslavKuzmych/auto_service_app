package application.service.impl;

import application.dto.car.CarRequestDto;
import application.dto.car.CarResponseDto;
import application.exception.EntityNotFoundException;
import application.mapper.CarMapper;
import application.model.Car;
import application.repository.CarRepository;
import application.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private static final String EXCEPTION = "Can't find car by id ";
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    public CarResponseDto createCar(CarRequestDto carRequestDto) {
        return carMapper.toDto(carRepository.save(carMapper.toEntity(carRequestDto)));
    }

    @Override
    public CarResponseDto updateCarById(Long id, CarRequestDto carRequestDto) {
        checkIfCarExists(id);
        Car newCar = carMapper.toEntity(carRequestDto).setId(id);
        return carMapper.toDto(carRepository.save(newCar));
    }

    private void checkIfCarExists(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(EXCEPTION + id));
    }
}
