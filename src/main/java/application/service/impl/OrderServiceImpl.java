package application.service.impl;

import application.dto.order.OrderRequestDto;
import application.dto.order.OrderRequestStatusDto;
import application.dto.order.OrderRequestUpdateDto;
import application.dto.order.OrderResponseDto;
import application.exception.EntityNotFoundException;
import application.exception.InvalidArgumentException;
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
import jakarta.transaction.Transactional;
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
    private static final String PAID_EXCEPTION = "This order is already paid!";
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OwnerRepository ownerRepository;
    private final GoodRepository goodRepository;
    private final MasterRepository masterRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional
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
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderResponseDto updateOrder(OrderRequestUpdateDto requestUpdateDto, Long orderId) {
        Order order = findByIdWithGoodsAndJobs(orderId);
        order.setProblemDescription(requestUpdateDto.getProblemDescription());
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long orderId, OrderRequestStatusDto status) {
        Order order = findByIdWithGoodsAndJobs(orderId).setStatus(status.getStatus());
        if (order.getStatus() == Order.Status.SUCCESSFUL
                || order.getStatus() == Order.Status.NOT_SUCCESSFUL) {
            order.setEndDate(LocalDateTime.now());
        }
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public BigDecimal payForOrder(Long orderId, Long carId) {
        Order order = findByIdWithGoodsAndJobs(orderId);
        checkIfOrderIsUnpaid(order);
        order.setStatus(Order.Status.PAID);
        Set<Master> masters =
                order.getJobs().stream().map(Job::getMaster).collect(Collectors.toSet());
        addOrderToMasters(masters, order);
        Owner owner = ownerRepository.findByCarId(carId).orElseThrow(()
                -> new EntityNotFoundException(EXCEPTION_OWNER + carId));
        BigDecimal finalAmount = getFinalSum(owner, order);
        order.setFinalAmount(finalAmount);
        orderRepository.save(order);
        return finalAmount;
    }

    private void checkIfOrderIsUnpaid(Order order) {
        if (order.getStatus() == Order.Status.PAID) {
            throw new InvalidArgumentException(PAID_EXCEPTION);
        }
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
                .valueOf(owner.getOrders().size() - 1).divide(BigDecimal.valueOf(100));
        BigDecimal goodsPrice = order.getGoods().stream()
                .map(Good::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (discount.compareTo(BigDecimal.valueOf(0.20)) > 0) {
            discount = BigDecimal.valueOf(0.20);
        }
        if (order.getJobs().size() != 1) {
            Job job = order.getJobs().stream().sorted().findFirst().get();
            order.getJobs().remove(job);
            job.setPrice(BigDecimal.ZERO);
            order.getJobs().add(job);
            jobRepository.save(job);
            BigDecimal jobsPrice = order.getJobs().stream()
                    .map(Job::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return goodsPrice.subtract(goodsPrice.multiply(discount))
                    .add(jobsPrice.subtract(jobsPrice.multiply(BigDecimal.valueOf(2)
                            .multiply(discount))));
        } else {
            return order.getJobs().stream().findFirst().get()
                    .getPrice().add(goodsPrice.subtract(goodsPrice.multiply(discount)));
        }
    }

    private void addOrderToOwner(Order savedOrder, Long carId) {
        Owner owner = ownerRepository.findByCarId(carId)
                .orElseThrow(()
                        -> new EntityNotFoundException(EXCEPTION_OWNER + carId));
        owner.getOrders().add(savedOrder);
        ownerRepository.save(owner);
    }

    private Order findByIdWithGoodsAndJobs(Long orderId) {
        return orderRepository.findByIdWithGoodsAndJobs(orderId)
                .orElseThrow(() -> new EntityNotFoundException(EXCEPTION_ORDER + orderId));
    }
}
