package application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.dto.master.MasterRequestDto;
import application.dto.master.MasterResponseDto;
import application.dto.order.OrderResponseDto;
import application.exception.EntityNotFoundException;
import application.mapper.MasterMapper;
import application.mapper.OrderMapper;
import application.model.Car;
import application.model.Job;
import application.model.Master;
import application.model.Order;
import application.repository.JobRepository;
import application.repository.MasterRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;

@ExtendWith(MockitoExtension.class)
class MasterServiceImplTest {
    private static final Integer ZERO = 0;
    private static final Integer TWO_ELEMENTS = 2;
    private static final Integer TWO_DAYS = 2;
    private static final Integer THREE_DAYS = 3;
    private static final Long AUDI_ID = 1L;
    private static final Long FIRST_ORDER_ID = 1L;
    private static final Long SECOND_ORDER_ID = 2L;
    private static final BigDecimal EXPECTED_SALARY = BigDecimal.valueOf(320);
    private static final BigDecimal WHEEL_CHANGE_PRICE = BigDecimal.valueOf(500);
    private static final BigDecimal LUBRICANT_CHANGE_PRICE = BigDecimal.valueOf(300);
    private static final Long LUBRICANT_CHANGE_ID = 1L;
    private static final Long WHEEL_CHANGE_ID = 2L;
    private static final String DESCRIPTION_FIRST_ORDER = "My car broke down";
    private static final String DESCRIPTION_SECOND_ORDER = "Something is wrong";
    private static final BigDecimal SMALL_AMOUNT = BigDecimal.valueOf(500);
    private static final BigDecimal BIG_AMOUNT = BigDecimal.valueOf(1200);
    private static final String EXCEPTION_FINDING = "Can't find master by id ";
    private static final Long INCORRECT_MASTER_ID = 200L;
    private static final Integer ONE_TIME = 1;
    private static final Long VOVA_ID = 2L;
    private static final String VOVA = "Vova";
    private static final String SHEVCHENKO = "Shevchenko";
    private static final Long STEPAN_ID = 1L;
    private static final String STEPAN = "Stepan";
    private static final String KOS = "Kos";
    private static MasterRequestDto stepanRequestDto;
    private static Master stepan;
    private static MasterResponseDto stepanResponseDto;
    private static Job lubricantChange;
    private static Job wheelChange;
    private static Master vova;
    private static Order firstOrder;
    private static Order secondOrder;
    private static OrderResponseDto firstOrderDto;
    private static OrderResponseDto secondOrderDto;
    @Mock
    private MasterRepository masterRepository;
    @Mock
    private MasterMapper masterMapper;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private JobRepository jobRepository;
    @InjectMocks
    private MasterServiceImpl masterServiceImpl;

    @BeforeEach
    void beforeEach() {
        lubricantChange = new Job().setId(LUBRICANT_CHANGE_ID)
                .setMaster(new Master().setId(STEPAN_ID))
                .setOrder(new Order().setId(FIRST_ORDER_ID))
                .setPrice(LUBRICANT_CHANGE_PRICE)
                .setStatus(Job.Status.UNPAID);
        wheelChange = new Job().setId(WHEEL_CHANGE_ID)
                .setPrice(WHEEL_CHANGE_PRICE)
                .setMaster(new Master().setId(STEPAN_ID))
                .setOrder(new Order().setId(FIRST_ORDER_ID))
                .setStatus(Job.Status.UNPAID);
        firstOrder = new Order().setId(FIRST_ORDER_ID)
                .setStatus(Order.Status.PAID)
                .setEndDate(LocalDateTime.now().minusDays(TWO_DAYS))
                .setCar(new Car().setId(AUDI_ID))
                .setDateOfAcceptance(LocalDateTime.now().minusDays(THREE_DAYS))
                .setProblemDescription(DESCRIPTION_FIRST_ORDER)
                .setFinalAmount(BIG_AMOUNT);
        secondOrder = new Order().setId(SECOND_ORDER_ID)
                .setStatus(Order.Status.PAID)
                .setDateOfAcceptance(LocalDateTime.now().minusDays(THREE_DAYS))
                .setFinalAmount(SMALL_AMOUNT)
                .setProblemDescription(DESCRIPTION_SECOND_ORDER)
                .setCar(new Car().setId(AUDI_ID))
                .setEndDate(LocalDateTime.now().minusDays(TWO_DAYS));
        firstOrderDto = new OrderResponseDto().setId(firstOrder.getId())
                .setEndDate(firstOrder.getEndDate())
                .setFinalAmount(firstOrder.getFinalAmount())
                .setCarId(firstOrder.getCar().getId())
                .setDateOfAcceptance(firstOrder.getDateOfAcceptance())
                .setStatus(firstOrder.getStatus().toString())
                .setProblemDescription(firstOrder.getProblemDescription());
        secondOrderDto = new OrderResponseDto().setId(secondOrder.getId())
                .setEndDate(secondOrder.getEndDate())
                .setCarId(secondOrder.getCar().getId())
                .setFinalAmount(secondOrder.getFinalAmount())
                .setStatus(secondOrder.getStatus().toString())
                .setDateOfAcceptance(secondOrder.getDateOfAcceptance())
                .setProblemDescription(secondOrder.getProblemDescription());
        vova = new Master().setId(VOVA_ID)
                .setFirstName(VOVA)
                .setLastName(SHEVCHENKO);
        stepanRequestDto = new MasterRequestDto()
                .setFirstName(STEPAN)
                .setLastName(KOS);
        stepan = new Master().setId(STEPAN_ID)
                .setFirstName(STEPAN)
                .setLastName(KOS);
        stepanResponseDto = new MasterResponseDto()
                .setId(STEPAN_ID)
                .setFirstName(STEPAN)
                .setLastName(KOS);
    }

    @Test
    @DisplayName("""
            Verify createMaster() method
            """)
    void createMaster_ValidRequestDto_ReturnResponseDto() {
        // when
        when(masterMapper.toEntity(stepanRequestDto)).thenReturn(stepan);
        when(masterRepository.save(stepan)).thenReturn(stepan);
        when(masterMapper.toDto(stepan)).thenReturn(stepanResponseDto);

        // then
        MasterResponseDto actual = masterServiceImpl.createMaster(stepanRequestDto);
        assertNotNull(actual);
        assertEquals(stepanResponseDto, actual);
        verify(masterRepository, times(ONE_TIME)).save(stepan);
    }

    @Test
    @DisplayName("""
            Verify updateById() method
            """)
    void updateById_ValidMasterId_ReturnResponseDto() {
        // when
        when(masterRepository.findById(VOVA_ID)).thenReturn(Optional.ofNullable(vova));
        when(masterMapper.toEntity(stepanRequestDto)).thenReturn(stepan);
        when(masterRepository.save(stepan.setId(VOVA_ID))).thenReturn(stepan);
        when(masterMapper.toDto(stepan)).thenReturn(stepanResponseDto.setId(VOVA_ID));

        // then
        MasterResponseDto actual = masterServiceImpl.updateById(VOVA_ID, stepanRequestDto);
        assertNotNull(actual);
        assertEquals(stepanResponseDto, actual);
        verify(masterRepository, times(ONE_TIME)).findById(VOVA_ID);
        verify(masterRepository, times(ONE_TIME)).save(stepan);
    }

    @Test
    @DisplayName("""
            Verify updateById() method with incorrect masterId
            """)
    void updateById_InvalidMasterId_ThrowException() {
        // when
        when(masterRepository.findById(INCORRECT_MASTER_ID)).thenReturn(Optional.empty());
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> masterServiceImpl.updateById(INCORRECT_MASTER_ID, stepanRequestDto)
        );

        // then
        String expected = EXCEPTION_FINDING + INCORRECT_MASTER_ID;
        String actual = exception.getMessage();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify getAllSuccessfulOrdersByMasterId() method
            """)
    void getAllSuccessfulOrdersByMasterId_ValidMasterId_ReturnList() {
        // when
        when(masterRepository.findMasterById(STEPAN_ID))
                .thenReturn(Optional.of(stepan.setOrders(Set.of(firstOrder, secondOrder))));
        when(orderMapper.toDto(firstOrder)).thenReturn(firstOrderDto);
        when(orderMapper.toDto(secondOrder)).thenReturn(secondOrderDto);

        // then
        List<OrderResponseDto> expected = List.of(firstOrderDto, secondOrderDto);
        List<OrderResponseDto> actual
                = masterServiceImpl.getAllSuccessfulOrdersByMasterId(STEPAN_ID);
        assertEquals(TWO_ELEMENTS, actual.size());
        EqualsBuilder.reflectionEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Verify getSalaryByMasterId() method
            """)
    void getSalaryByMasterId_ValidMasterId_ReturnExpectedSalary() {
        // given
        stepan.setOrders(Set.of(firstOrder.setJobs(Set.of(wheelChange, lubricantChange))));

        // when
        when(masterRepository.findMasterById(STEPAN_ID))
                .thenReturn(Optional.of(stepan));
        when(jobRepository.save(wheelChange))
                .thenReturn(wheelChange);
        when(jobRepository.save(lubricantChange))
                .thenReturn(lubricantChange);

        // then
        BigDecimal actual = masterServiceImpl.getSalaryByMasterId(STEPAN_ID);
        assertNotNull(actual);
        assertEquals(ZERO, EXPECTED_SALARY.compareTo(actual));
    }
}
