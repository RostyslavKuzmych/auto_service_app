package application.service.impl;

import application.dto.order.OrderRequestDto;
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
import application.service.OrderService;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private static final String EXCEPTION_GOOD = "Can't find good by id ";
    private static final String EXCEPTION_ORDER = "Can't find order by id ";
    private static final String EXCEPTION_OWNER = "Can't find owner by car id ";
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OwnerRepository ownerRepository;
    private final GoodRepository goodRepository;
    private final MasterRepository masterRepository;
    private final JobRepository jobRepository;

    @Override
    public OrderResponseDto placeOrder(Long carId, OrderRequestDto orderRequestDto) {
        Order order = orderMapper.toEntity(orderRequestDto)
                .setDateOfAcceptance(LocalDateTime.now())
                .setStatus(Order.Status.RECEIVED)
                .setCar(new Car().setId(carId));
        Order savedOrder = orderRepository.save(order);
        Job diagnosis = new Job()
                .setOrder(savedOrder)
                .setMaster(new Master().setId(1L))
                .setStatus(Job.Status.UNPAID)
                .setPrice(BigDecimal.valueOf(500));
        jobRepository.save(diagnosis);
        addOrderToOwner(savedOrder, carId);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    public OrderResponseDto addGoodToOrder(Long orderId, Long goodId) {
        Order order = findByIdWithGoodsAndJobs(orderId);
        Good good = goodRepository.findById(goodId).orElseThrow(()
                -> new EntityNotFoundException(EXCEPTION_GOOD + goodId));
        order.getGoods().add(good);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponseDto updateOrder(OrderRequestUpdateDto requestUpdateDto, Long orderId) {
        Order order = findByIdWithGoodsAndJobs(orderId);
        setFieldsToOrder(order, requestUpdateDto);
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, Order.Status status) {
        Order order = findByIdWithGoodsAndJobs(orderId).setStatus(status);
        if (status == Order.Status.SUCCESSFUL || status == Order.Status.NOT_SUCCESSFUL) {
            order.setEndDate(LocalDateTime.now());
        }
        return orderMapper.toDto(orderRepository.save(order));
    }

    @Override
    public BigDecimal payForOrder(Long orderId, Long carId) {
        Order order = findByIdWithGoodsAndJobs(orderId).setStatus(Order.Status.PAID);
        Set<Master> masters =
                order.getJobs().stream().map(Job::getMaster).collect(Collectors.toSet());
        addOrderToMasters(masters, order);
        Owner owner = ownerRepository.findByCarId(carId).orElseThrow(()
                -> new EntityNotFoundException(EXCEPTION_OWNER + carId));
        if (getSumIfOrderHasOnlyDiagnosis(owner, order) != BigDecimal.ZERO) {
            return getSumIfOrderHasOnlyDiagnosis(owner, order);
        }
        BigDecimal finalAmount = getFinalSum(owner, order);
        order.setFinalAmount(finalAmount);
        orderRepository.save(order);
        return finalAmount;
    }

    private void addOrderToMasters(Set<Master> masters, Order order) {
        masters.forEach(master -> {
            Master masterFromDb
                    = masterRepository.findByIdWithAllOrders(master.getId()).get();
            Set<Order> orders = masterFromDb.getOrders();
            orders.add(order);
            masterFromDb.setOrders(orders);
            masterRepository.save(masterFromDb);
        });
    }

    private BigDecimal getFinalSum(Owner owner, Order order) {
        BigDecimal discount = BigDecimal
                .valueOf(owner.getOrders().size()).divide(BigDecimal.valueOf(100));
        BigDecimal goodsPrice = order.getGoods().stream()
                .map(Good::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal jobsPrice = order.getJobs().stream()
                .map(Job::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add).subtract(BigDecimal.valueOf(500));
        return goodsPrice.subtract(goodsPrice.multiply(discount))
                .add(jobsPrice.subtract(jobsPrice.multiply(BigDecimal.valueOf(2)
                        .multiply(discount))));
    }

    private BigDecimal getSumIfOrderHasOnlyDiagnosis(Owner owner, Order order) {
        if (order.getJobs().size() == 1) {
            BigDecimal resultSum = getFinalSum(owner, order).add(BigDecimal.valueOf(500));
            order.setFinalAmount(resultSum);
            orderRepository.save(order);
            return resultSum;
        } else {
            Job job = order.getJobs().get(0).setPrice(BigDecimal.valueOf(0));
            jobRepository.save(job);
            return BigDecimal.ZERO;
        }
    }

    private void addOrderToOwner(Order savedOrder, Long carId) {
        Owner owner = ownerRepository.findByCarId(carId)
                .orElseThrow(()
                        -> new EntityNotFoundException(EXCEPTION_OWNER + carId));
        owner.getOrders().add(savedOrder);
        ownerRepository.save(owner);
    }

    private void setFieldsToOrder(Order order, OrderRequestUpdateDto updateDto) {
        if (updateDto.getCarId() != null) {
            order.setCar(new Car().setId(updateDto.getCarId()));
        }
        if (updateDto.getProblemDescription() != null) {
            order.setProblemDescription(updateDto.getProblemDescription());
        }
    }

    private Order findByIdWithGoodsAndJobs(Long orderId) {
        return orderRepository.findByIdWithGoodsAndJobs(orderId)
                .orElseThrow(() -> new EntityNotFoundException(EXCEPTION_ORDER + orderId));
    }
}
