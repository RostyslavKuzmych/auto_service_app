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
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CarServiceImpl implements CarService {
    private static final String EXCEPTION = "Can't find car by id ";
    private final CarRepository carRepository;
    private final CarMapper carMapper;

    @Override
    @Transactional
    public CarResponseDto createCar(CarRequestDto carRequestDto) {
        return carMapper.toDto(carRepository.save(carMapper.toEntity(carRequestDto)));
    }

    @Override
    @Transactional
    public CarResponseDto updateCarById(Long id, CarRequestDto carRequestDto) {
        if (carRepository.findById(id).isPresent()) {
            Car newCar = carMapper.toEntity(carRequestDto).setId(id);
            return carMapper.toDto(carRepository.save(newCar));
        }
        throw new EntityNotFoundException(EXCEPTION + id);
    }
}
