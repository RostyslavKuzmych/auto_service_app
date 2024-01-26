package application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.dto.order.OrderRequestDto;
import application.dto.order.OrderRequestStatusDto;
import application.dto.order.OrderRequestUpdateDto;
import application.dto.order.OrderResponseDto;
import application.exception.EntityNotFoundException;
import application.mapper.OrderMapper;
import application.model.Car;
import application.model.Good;
import application.model.Job;
import application.model.Master;
import application.model.Order;
import application.model.Owner;
import application.repository.GoodRepository;
import application.repository.JobRepository;
import application.repository.MasterRepository;
import application.repository.OrderRepository;
import application.repository.OwnerRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {
    private static final String VOVA_PHONE_NUMBER = "+380984567812";
    private static final String VOVA = "Vova";
    private static final String KOVAL = "Koval";
    private static final Integer ZERO = 0;
    private static final String PROCESSED = "PROCESSED";
    private static final BigDecimal ONE_THOUSAND = BigDecimal.valueOf(1000);
    private static final String EXCEPTION_FINDING = "Can't find order by id ";
    private static final Long INVALID_ORDER_ID = 1000L;
    private static final Long FIRST_ORDER_ID = 1L;
    private static final String ANOTHER_DESCRIPTION = "Something broke down";
    private static final Long VOVA_ID = 2L;
    private static final String STEPAN = "Stepan";
    private static final String KOS = "Kos";
    private static final Long LUBRICANT_ID = 1L;
    private static final String LUBRICANT = "lubricant";
    private static final BigDecimal LUBRICANT_PRICE = BigDecimal.valueOf(300);
    private static final Long DIAGNOSTICS_ID = 1L;
    private static final Long STEPAN_ID = 1L;
    private static final BigDecimal EXPECTED_PRICE = BigDecimal.valueOf(500);
    private static final BigDecimal DIAGNOSTICS_PRICE = BigDecimal.valueOf(500);
    private static final Long AUDI_ID = 1L;
    private static final Integer ONE_TIME = 1;
    private static final String DESCRIPTION = "My car broke down...";
    private static Job diagnostics;
    private static OrderRequestStatusDto statusDto;
    private static OrderRequestDto orderRequestDto;
    private static Order order;
    private static OrderResponseDto orderResponseDto;
    private static Master stepan;
    private static Good lubricant;
    private static OrderRequestUpdateDto updateDto;
    private static Owner vova;
    @Mock
    private MasterRepository masterRepository;
    @Mock
    private JobRepository jobRepository;
    @Mock
    private GoodRepository goodRepository;
    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderServiceImpl orderServiceImpl;

    @BeforeEach
    void beforeEach() {
        stepan = new Master().setId(STEPAN_ID)
                .setFirstName(STEPAN)
                .setLastName(KOS);
        statusDto = new OrderRequestStatusDto()
                .setStatus(Order.Status.PROCESSED);
        updateDto = new OrderRequestUpdateDto()
                .setProblemDescription(ANOTHER_DESCRIPTION);
        lubricant = new Good().setId(LUBRICANT_ID)
                .setName(LUBRICANT)
                .setPrice(LUBRICANT_PRICE);
        vova = new Owner().setId(VOVA_ID).setCars(Set.of(new Car().setId(AUDI_ID)))
                .setFirstName(VOVA).setLastName(KOVAL).setPhoneNumber(VOVA_PHONE_NUMBER);
        diagnostics = new Job()
                .setId(DIAGNOSTICS_ID)
                .setOrder(new Order().setId(FIRST_ORDER_ID))
                .setMaster(stepan)
                .setPrice(DIAGNOSTICS_PRICE)
                .setStatus(Job.Status.UNPAID);
        orderRequestDto = new OrderRequestDto()
                .setProblemDescription(DESCRIPTION);
        order = new Order().setId(FIRST_ORDER_ID)
                .setStatus(Order.Status.RECEIVED)
                .setCar(new Car().setId(AUDI_ID))
                .setDateOfAcceptance(LocalDateTime.now())
                .setProblemDescription(orderRequestDto.getProblemDescription())
                .setFinalAmount(ONE_THOUSAND)
                .setGoods(new HashSet<>());
        orderResponseDto = new OrderResponseDto()
                .setId(order.getId())
                .setStatus(order.getStatus().toString())
                .setCarId(order.getCar().getId())
                .setFinalAmount(order.getFinalAmount())
                .setDateOfAcceptance(order.getDateOfAcceptance())
                .setProblemDescription(order.getProblemDescription());
    }

    @Test
    @DisplayName("""
            Verify placeOrder() method
            """)
    void placeOrder_ValidRequestDto_ReturnResponseDto() {
        // when
        when(orderMapper.toEntity(orderRequestDto))
                .thenReturn(new Order().setProblemDescription(DESCRIPTION));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(jobRepository.save(any(Job.class))).thenReturn(diagnostics);
        when(ownerRepository.findByCarId(AUDI_ID)).thenReturn(Optional.ofNullable(vova));
        when(ownerRepository.save(vova)).thenReturn(vova);
        when(orderMapper.toDto(order)).thenReturn(orderResponseDto);

        // then
        OrderResponseDto actual = orderServiceImpl.placeOrder(AUDI_ID, orderRequestDto);
        assertNotNull(actual);
        assertEquals(orderResponseDto, actual);
    }

    @Test
    @DisplayName("""
            Verify addGoodToOrder() method
            """)
    void addGoodToOrder_ValidOrderId_ReturnResponseDto() {
        // when
        when(orderRepository.findByIdWithGoodsAndJobs(FIRST_ORDER_ID))
                .thenReturn(Optional.of(order));
        when(goodRepository.findById(LUBRICANT_ID)).thenReturn(Optional.of(lubricant));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponseDto);

        // then
        OrderResponseDto actual
                = orderServiceImpl.addGoodToOrder(FIRST_ORDER_ID, LUBRICANT_ID);
        assertNotNull(actual);
        assertEquals(orderResponseDto, actual);
    }

    @Test
    @DisplayName("""
            Verify updateOrder() method
            """)
    void updateOrder_ValidOrderId_ReturnResponseDto() {
        // when
        when(orderRepository.findByIdWithGoodsAndJobs(FIRST_ORDER_ID))
                .thenReturn(Optional.of(order));
        when(orderRepository.save(order.setProblemDescription(updateDto
                .getProblemDescription()))).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponseDto
                .setProblemDescription(updateDto.getProblemDescription()));

        // then
        OrderResponseDto actual = orderServiceImpl.updateOrder(updateDto, FIRST_ORDER_ID);
        assertNotNull(actual);
        assertEquals(orderResponseDto, actual);
        verify(orderRepository, times(ONE_TIME)).findByIdWithGoodsAndJobs(FIRST_ORDER_ID);
        verify(orderRepository, times(ONE_TIME)).save(order);
    }

    @Test
    @DisplayName("""
            Verify updateOrderStatus() method
            """)
    void updateOrderStatus_ValidRequestStatus_ReturnResponseDto() {
        // when
        when(orderRepository.findByIdWithGoodsAndJobs(FIRST_ORDER_ID))
                .thenReturn(Optional.of(order));
        when(orderRepository.save(order.setStatus(statusDto.getStatus())))
                .thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(orderResponseDto.setStatus(PROCESSED));

        // then
        OrderResponseDto actual =
                orderServiceImpl.updateOrderStatus(FIRST_ORDER_ID, statusDto);
        assertNotNull(actual);
        assertEquals(orderResponseDto, actual);
        verify(orderRepository, times(ONE_TIME)).findByIdWithGoodsAndJobs(FIRST_ORDER_ID);
        verify(orderRepository, times(ONE_TIME)).save(order);
    }

    @Test
    @DisplayName("""
            Verify payForOrder() method
            """)
    void payForOrder_ValidRequestDto_ReturnResponseDto() {
        // when
        when(orderRepository.findByIdWithGoodsAndJobs(FIRST_ORDER_ID))
                .thenReturn(Optional.of(order.setJobs(Set.of(diagnostics))));
        when(masterRepository.findByIdWithAllOrders(STEPAN_ID))
                .thenReturn(Optional.of(stepan));
        when(masterRepository.save(any(Master.class)))
                .thenReturn(stepan);
        when(ownerRepository.findByCarId(AUDI_ID)).thenReturn(Optional.of(vova));
        when(orderRepository.save(order)).thenReturn(order);

        // then
        BigDecimal actual = orderServiceImpl.payForOrder(FIRST_ORDER_ID, AUDI_ID);
        assertNotNull(actual);
        assertEquals(ZERO, EXPECTED_PRICE.compareTo(actual));
    }

    @Test
    @DisplayName("""
            Verify payForOrder() method with incorrect orderId
            """)
    void payForOrder_InvalidOrderId_ThrowException() {
        // when
        when(orderRepository.findByIdWithGoodsAndJobs(INVALID_ORDER_ID))
                .thenReturn(Optional.empty());
        Exception exception = assertThrows(
                EntityNotFoundException.class,
                () -> orderServiceImpl.payForOrder(INVALID_ORDER_ID, AUDI_ID)
        );

        // then
        String expected = EXCEPTION_FINDING + INVALID_ORDER_ID;
        String actual = exception.getMessage();
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
