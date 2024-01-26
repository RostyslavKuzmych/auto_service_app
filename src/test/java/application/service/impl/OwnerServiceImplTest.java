package application.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import application.dto.order.OrderResponseDto;
import application.dto.owner.OwnerResponseDto;
import application.mapper.OrderMapper;
import application.mapper.OwnerMapper;
import application.model.Car;
import application.model.Order;
import application.model.Owner;
import application.repository.OwnerRepository;
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
class OwnerServiceImplTest {
    private static final Integer TWO_DAYS = 2;
    private static final Integer THREE_DAYS = 3;
    private static final Long AUDI_ID = 1L;
    private static final Long FIRST_ORDER_ID = 1L;
    private static final Long SECOND_ORDER_ID = 2L;
    private static final Integer EXPECTED_SIZE = 2;
    private static final Long VOVA_ID = 1L;
    private static final Integer ONE_TIME = 1;
    private static final String DESCRIPTION_FIRST_ORDER = "My car broke down";
    private static final String DESCRIPTION_SECOND_ORDER = "Something is wrong";
    private static final BigDecimal SMALL_AMOUNT = BigDecimal.valueOf(500);
    private static final BigDecimal BIG_AMOUNT = BigDecimal.valueOf(1200);
    private static Owner owner;
    private static Order firstOrder;
    private static OrderResponseDto firstOrderDto;
    private static Order secondOrder;
    private static OrderResponseDto secondOrderDto;
    private static OwnerResponseDto ownerResponseDto;
    @InjectMocks
    private OwnerServiceImpl ownerServiceImpl;
    @Mock
    private OwnerRepository ownerRepository;
    @Mock
    private OwnerMapper ownerMapper;
    @Mock
    private OrderMapper orderMapper;

    @BeforeEach
    void setUp() {
        owner = new Owner().setId(VOVA_ID);
        ownerResponseDto = new OwnerResponseDto().setId(VOVA_ID);
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
    }

    @Test
    @DisplayName("""
            Verify createOwner() method
            """)
    void createOwner_ValidRequest_ReturnResponseDto() {
        // when
        when(ownerRepository.save(any(Owner.class))).thenReturn(owner);
        when(ownerMapper.toDto(owner)).thenReturn(ownerResponseDto);

        // then
        OwnerResponseDto actual = ownerServiceImpl.createOwner();
        assertNotNull(actual);
        assertEquals(ownerResponseDto, actual);
        verify(ownerRepository, times(ONE_TIME)).save(any(Owner.class));
    }

    @Test
    @DisplayName("""
            Verify getAllOrdersByOwnerId() method
            """)
    void getAllOrdersByOwnerId_ValidOwnerId_ReturnList() {
        // when
        when(ownerRepository.findByIdWithOrders(VOVA_ID))
                .thenReturn(Optional.of(owner.setOrders(Set.of(firstOrder, secondOrder))));
        when(orderMapper.toDto(firstOrder)).thenReturn(firstOrderDto);
        when(orderMapper.toDto(secondOrder)).thenReturn(secondOrderDto);

        // then
        List<OrderResponseDto> expectedList = List.of(firstOrderDto, secondOrderDto);
        List<OrderResponseDto> actualList
                = ownerServiceImpl.getAllOrdersByOwnerId(VOVA_ID);
        assertEquals(EXPECTED_SIZE, actualList.size());
        EqualsBuilder.reflectionEquals(actualList, expectedList);
        verify(ownerRepository, times(ONE_TIME)).findByIdWithOrders(VOVA_ID);
    }
}
