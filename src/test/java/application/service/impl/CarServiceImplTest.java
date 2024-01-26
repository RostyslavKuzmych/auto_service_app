package application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.dto.car.CarRequestDto;
import application.dto.car.CarResponseDto;
import application.exception.EntityNotFoundException;
import application.mapper.CarMapper;
import application.model.Car;
import application.model.Owner;
import application.repository.CarRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {
    private static final Long AUDI_ID = 1L;
    private static final Long BMW_ID = 2L;
    private static final Long CORRECT_ID = 1L;
    private static final Long INCORRECT_ID = 200L;
    private static final Integer ONE_TIME = 1;
    private static final String BMW_BRAND = "bmw";
    private static final String B6_MODEL = "B6";
    private static final Long STEPAN_ID = 1L;
    private static final String AUDI_BRAND = "audi";
    private static final String B5_MODEL = "B5";
    private static final String RANDOM_NUMBER = "123CER";
    private static final Integer RANDOM_YEAR = 2012;
    private static final String NEXT_RANDOM_NUMBER = "132CRD";
    private static final String EXCEPTION_FINDING = "Can't find car by id ";
    private static Car audi;
    private static CarRequestDto audiRequestDto;
    private static CarResponseDto audiResponseDto;
    private static CarRequestDto bmwRequestDto;
    private static Car bmv;
    @Mock
    private CarRepository carRepository;
    @Mock
    private CarMapper carMapper;
    @InjectMocks
    private CarServiceImpl carServiceImpl;

    @BeforeEach
    void beforeEach() {
        audiRequestDto = new CarRequestDto()
                .setOwnerId(STEPAN_ID)
                .setBrand(AUDI_BRAND)
                .setModel(B5_MODEL)
                .setNumber(RANDOM_NUMBER)
                .setManufactureYear(RANDOM_YEAR);
        audi = new Car().setId(AUDI_ID)
                .setOwner(new Owner().setId(STEPAN_ID))
                .setBrand(AUDI_BRAND)
                .setNumber(RANDOM_NUMBER)
                .setModel(B5_MODEL)
                .setManufactureYear(RANDOM_YEAR);
        audiResponseDto = new CarResponseDto()
                .setId(audi.getId())
                .setOwnerId(audi.getOwner().getId())
                .setManufactureYear(audi.getManufactureYear())
                .setNumber(audi.getNumber())
                .setModel(audi.getModel())
                .setBrand(audi.getBrand());
        bmwRequestDto = new CarRequestDto()
                .setOwnerId(STEPAN_ID)
                .setBrand(BMW_BRAND)
                .setModel(B6_MODEL)
                .setManufactureYear(RANDOM_YEAR)
                .setNumber(NEXT_RANDOM_NUMBER);
        bmv = new Car().setId(BMW_ID)
                .setOwner(new Owner().setId(STEPAN_ID))
                .setBrand(BMW_BRAND)
                .setNumber(NEXT_RANDOM_NUMBER)
                .setModel(B6_MODEL)
                .setManufactureYear(RANDOM_YEAR);
    }

    @Test
    @DisplayName("""
            Verify createCar() method
            """)
    void createCar_ValidRequestDto_ReturnResponseDto() {
        // when
        when(carMapper.toEntity(audiRequestDto)).thenReturn(audi);
        when(carRepository.save(audi)).thenReturn(audi);
        when(carMapper.toDto(audi)).thenReturn(audiResponseDto);

        // then
        CarResponseDto actual = carServiceImpl.createCar(audiRequestDto);
        assertNotNull(actual);
        assertEquals(audiResponseDto, actual);
        verify(carRepository, times(ONE_TIME)).save(audi);
    }

    @Test
    @DisplayName("""
            Verify updateCarById() method
            """)
    void updateCarById_ValidCarId_ReturnResponseDto() {
        // when
        when(carRepository.findById(CORRECT_ID)).thenReturn(Optional.ofNullable(bmv));
        when(carMapper.toEntity(audiRequestDto)).thenReturn(audi);
        when(carRepository.save(audi.setId(BMW_ID))).thenReturn(audi);
        when(carMapper.toDto(audi)).thenReturn(audiResponseDto.setId(BMW_ID));

        // then
        CarResponseDto actual = carServiceImpl.updateCarById(CORRECT_ID, audiRequestDto);
        assertNotNull(actual);
        assertEquals(audiResponseDto, actual);
        verify(carRepository, times(ONE_TIME)).findById(CORRECT_ID);
        verify(carRepository, times(ONE_TIME)).save(audi);
    }

    @Test
    @DisplayName("""
            Verify updateCarById() method with incorrect carId
            """)
    void updateCarById_InvalidCarId_ThrowException() {
        // when
        when(carRepository.findById(INCORRECT_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> carServiceImpl.updateCarById(INCORRECT_ID, bmwRequestDto)
        );

        // then
        String expected = EXCEPTION_FINDING + INCORRECT_ID;
        String actual = exception.getMessage();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
