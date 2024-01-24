package application.mapper;

import application.config.MapperConfig;
import application.dto.order.OrderRequestDto;
import application.dto.order.OrderResponseDto;
import application.model.Good;
import application.model.Job;
import application.model.Order;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    @Mapping(source = "car.id", target = "carId")
    @Mapping(source = "jobs", target = "jobsId", qualifiedByName = "getJobsId")
    @Mapping(source = "goods", target = "goodsId", qualifiedByName = "getGoodsId")
    @Mapping(source = "status", target = "status", qualifiedByName = "getStatusStringFromStatus")
    OrderResponseDto toDto(Order order);

    Order toEntity(OrderRequestDto orderRequestDto);

    @Named(value = "getJobsId")
    default Set<Long> getJobsId(Set<Job> jobs) {
        return jobs.stream()
                .map(Job::getId)
                .collect(Collectors.toSet());
    }

    @Named(value = "getGoodsId")
    default Set<Long> getGoodsId(Set<Good> goods) {
        return goods.stream()
                .map(Good::getId)
                .collect(Collectors.toSet());
    }

    @Named(value = "getStatusStringFromStatus")
    default String getStatusStringFromStatus(Order.Status status) {
        return status.toString();
    }
}
