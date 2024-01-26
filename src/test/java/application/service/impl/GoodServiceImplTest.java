package application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.dto.good.GoodRequestDto;
import application.dto.good.GoodResponseDto;
import application.exception.EntityNotFoundException;
import application.mapper.GoodMapper;
import application.model.Good;
import application.repository.GoodRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GoodServiceImplTest {
    private static final String EXCEPTION_FINDING = "Can't find good by id ";
    private static final Integer ONE_TIME = 1;
    private static final Long LUBRICANT_ID = 1L;
    private static final String LUBRICANT = "lubricant";
    private static final BigDecimal LUBRICANT_PRICE = BigDecimal.valueOf(100);
    private static final Long WHEEL_ID = 2L;
    private static final Long INCORRECT_ID = 200L;
    private static final String WHEEL = "wheel";
    private static final BigDecimal WHEEL_PRICE = BigDecimal.valueOf(600);
    private static final String VACUUM_CLEANER = "vacuum cleaner";
    private static final BigDecimal VACUUM_CLEANER_PRICE = BigDecimal.valueOf(1400);
    private static Good wheel;
    private static GoodRequestDto lubricantRequestDto;
    private static Good lubricant;
    private static GoodResponseDto lubricantResponseDto;
    private static GoodRequestDto vacuumCleanerRequestDto;
    @Mock
    private GoodMapper goodMapper;
    @Mock
    private GoodRepository goodRepository;
    @InjectMocks
    private GoodServiceImpl goodServiceImpl;

    @BeforeEach
    void beforeEach() {
        vacuumCleanerRequestDto = new GoodRequestDto()
                .setName(VACUUM_CLEANER)
                .setPrice(VACUUM_CLEANER_PRICE);
        lubricantResponseDto = new GoodResponseDto()
                .setId(LUBRICANT_ID)
                .setName(LUBRICANT)
                .setPrice(LUBRICANT_PRICE);
        lubricant = new Good().setId(LUBRICANT_ID)
                .setName(LUBRICANT)
                .setPrice(LUBRICANT_PRICE);
        lubricantRequestDto = new GoodRequestDto()
                .setName(LUBRICANT)
                .setPrice(LUBRICANT_PRICE);
        wheel = new Good().setId(WHEEL_ID)
                .setName(WHEEL)
                .setPrice(WHEEL_PRICE);
    }

    @Test
    @DisplayName("""
            Verify createGood() method
            """)
    void createGood_ValidRequestDto_ReturnResponseDto() {
        // when
        when(goodMapper.toEntity(lubricantRequestDto)).thenReturn(lubricant);
        when(goodRepository.save(lubricant)).thenReturn(lubricant);
        when(goodMapper.toDto(lubricant)).thenReturn(lubricantResponseDto);

        // then
        GoodResponseDto actual = goodServiceImpl.createGood(lubricantRequestDto);
        assertNotNull(actual);
        assertEquals(lubricantResponseDto, actual);
        verify(goodRepository, times(ONE_TIME)).save(lubricant);
    }

    @Test
    @DisplayName("""
            Verify updateGood() method
            """)
    void updateGood_ValidGoodId_ReturnResponseDto() {
        // when
        when(goodRepository.findById(WHEEL_ID)).thenReturn(Optional.ofNullable(wheel));
        when(goodMapper.toEntity(lubricantRequestDto)).thenReturn(lubricant);
        when(goodRepository.save(lubricant.setId(WHEEL_ID))).thenReturn(lubricant);
        when(goodMapper.toDto(lubricant)).thenReturn(lubricantResponseDto.setId(WHEEL_ID));

        // then
        GoodResponseDto actual = goodServiceImpl.updateGood(WHEEL_ID, lubricantRequestDto);
        assertNotNull(actual);
        assertEquals(lubricantResponseDto, actual);
        verify(goodRepository, times(ONE_TIME)).findById(WHEEL_ID);
        verify(goodRepository, times(ONE_TIME)).save(lubricant);
    }

    @Test
    @DisplayName("""
            Verify updateGood() method with incorrect goodId
            """)
    void updateGood_InvalidGoodId_ThrowException() {
        // when
        when(goodRepository.findById(INCORRECT_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> goodServiceImpl.updateGood(INCORRECT_ID, vacuumCleanerRequestDto));

        // then
        String expected = EXCEPTION_FINDING + INCORRECT_ID;
        String actual = exception.getMessage();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
