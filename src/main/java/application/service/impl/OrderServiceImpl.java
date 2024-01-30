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
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private static final String EXCEPTION_JOB = "Can't find job by id ";
    private static final String EXCEPTION_MASTER = "Can't find master by id ";
    private static final String EXCEPTION_GOOD = "Can't find good by id ";
    private static final String EXCEPTION_ORDER = "Can't find order by id ";
    private static final String EXCEPTION_OWNER = "Can't find owner by car id ";
    private static final String PAID_EXCEPTION = "This order has already been paid";
    private static final Long MASTER_STEPAN_ID = 1L;
    private static final Integer DIAGNOSTICS_PRICE = 500;
    private static final Integer ONE_HUNDRED = 100;
    private static final Integer ZERO = 0;
    private static final Integer ONE = 1;
    private static final BigDecimal DOUBLE_DISCOUNT = BigDecimal.valueOf(2);
    private static final BigDecimal MAXIMUM_PERCENTAGE = BigDecimal.valueOf(0.20);
    private final OrderMapper orderMapper;
    private final OrderRepository orderRepository;
    private final OwnerRepository ownerRepository;
    private final GoodRepository goodRepository;
    private final MasterRepository masterRepository;
    private final JobRepository jobRepository;

    @Override
    @Transactional
    /*
     * When placing an order, car diagnostics is automatically included.
     */
    public OrderResponseDto placeOrder(Long carId, OrderRequestDto orderRequestDto) {
        Order order = orderMapper.toEntity(orderRequestDto)
                .setCar(new Car().setId(carId));
        Order savedOrder = orderRepository.save(order);
        Job diagnostics = new Job()
                .setOrder(savedOrder)
                .setMaster(new Master().setId(MASTER_STEPAN_ID))
                .setPrice(BigDecimal.valueOf(DIAGNOSTICS_PRICE));
        jobRepository.save(diagnostics);
        addOrderToOwner(savedOrder, carId);
        return orderMapper.toDto(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponseDto addGoodToOrder(Long orderId, Long goodId) {
        Order order = findByIdWithGoodsAndJobs(orderId);
        Good good = goodRepository.findById(goodId).orElseThrow(()
                -> new EntityNotFoundException(EXCEPTION_GOOD + goodId));
        order.getGoods().add(good);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
    public OrderResponseDto updateOrder(OrderRequestUpdateDto requestUpdateDto, Long orderId) {
        Order order = findByIdWithGoodsAndJobs(orderId);
        order.setProblemDescription(requestUpdateDto.getProblemDescription());
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    @Transactional
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
        Set<Master> masters =
                order.getJobs().stream().map(Job::getMaster).collect(Collectors.toSet());
        addOrderToMasters(masters, order);
        Owner owner = findOwnerByCarId(carId);
        BigDecimal finalAmount = getFinalSum(owner, order);
        prepareAndSaveOrder(order, finalAmount);
        return finalAmount;
    }

    private void prepareAndSaveOrder(Order order, BigDecimal finalAmount) {
        order.setStatus(Order.Status.PAID);
        order.setFinalAmount(finalAmount);
        orderRepository.save(order);
    }

    private Owner findOwnerByCarId(Long carId) {
        return ownerRepository.findByCarId(carId).orElseThrow(()
                -> new EntityNotFoundException(EXCEPTION_OWNER + carId));
    }

    private void checkIfOrderIsUnpaid(Order order) {
        if (order.getStatus() == Order.Status.PAID) {
            throw new InvalidArgumentException(PAID_EXCEPTION);
        }
    }

    private void addOrderToMasters(Set<Master> masters, Order order) {
        masters.forEach(master -> {
            Master masterFromDb
                    = masterRepository.findMasterById(master.getId())
                    .orElseThrow(()
                            -> new EntityNotFoundException(EXCEPTION_MASTER
                            + master.getId()));
            masterFromDb.getOrders().add(order);
            masterRepository.save(masterFromDb);
        });
    }

    /*
     * Owner's car discount:
     * for goods, 1% multiplied by the quantity of paid orders;
     * for services, 2% multiplied by the quantity of paid orders.
     */
    private BigDecimal getFinalSum(Owner owner, Order order) {
        BigDecimal discountPercentage = getDiscountOfOwner(owner);
        BigDecimal priceOfGoods = getGoodsPriceWithDiscount(order, discountPercentage);
        if (order.getJobs().size() != ONE) {
            makeDiagnosticsFree(order);
            BigDecimal priceOfJobs = getJobsPriceWithDiscount(order, discountPercentage);
            return priceOfGoods.add(priceOfJobs);
        } else {
            return getPriceOfDiagnosticsAndGoods(order, priceOfGoods);
        }
    }

    private BigDecimal getJobsPriceWithDiscount(Order order, BigDecimal discountPercentage) {
        BigDecimal jobsPrice = order.getJobs().stream()
                .map(Job::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return jobsPrice
                .subtract(jobsPrice.multiply(DOUBLE_DISCOUNT)
                        .multiply(discountPercentage));
    }

    private BigDecimal getGoodsPriceWithDiscount(Order order, BigDecimal discountPercentage) {
        BigDecimal goodsPrice = order.getGoods().stream()
                .map(Good::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return goodsPrice.subtract(goodsPrice
                .multiply(discountPercentage));
    }

    private BigDecimal getPriceOfDiagnosticsAndGoods(Order order,
                                                     BigDecimal priceOfGoodsWithDiscount
    ) {
        return order.getJobs().stream().findFirst()
                .orElseThrow(() -> new EntityNotFoundException(EXCEPTION_JOB))
                .getPrice().add(priceOfGoodsWithDiscount);
    }

    private void makeDiagnosticsFree(Order order) {
        Job job = order.getJobs().stream().sorted().findFirst()
                .orElseThrow(() -> new EntityNotFoundException(EXCEPTION_JOB));
        order.getJobs().remove(job);
        jobRepository.save(job.setPrice(BigDecimal.ZERO));
        order.getJobs().add(job);
    }

    private void addOrderToOwner(Order savedOrder, Long carId) {
        Owner owner = ownerRepository.findByCarId(carId)
                .orElseThrow(() -> new EntityNotFoundException(EXCEPTION_OWNER + carId));
        owner.getOrders().add(savedOrder);
        ownerRepository.save(owner);
    }

    private BigDecimal getDiscountOfOwner(Owner owner) {
        BigDecimal discount = BigDecimal
                .valueOf(owner.getOrders()
                        .stream()
                        .filter(o -> o.getStatus() == Order.Status.PAID).count())
                .divide(BigDecimal.valueOf(ONE_HUNDRED));
        if (discount.compareTo(MAXIMUM_PERCENTAGE) > ZERO) {
            discount = MAXIMUM_PERCENTAGE;
        }
        return discount;
    }

    private Order findByIdWithGoodsAndJobs(Long orderId) {
        return orderRepository.findOrderById(orderId)
                .orElseThrow(() -> new EntityNotFoundException(EXCEPTION_ORDER + orderId));
    }
}
