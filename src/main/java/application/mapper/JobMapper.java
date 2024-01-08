package application.mapper;

import application.config.MapperConfig;
import application.dto.job.JobRequestDto;
import application.dto.job.JobResponseDto;
import application.model.Job;
import application.model.Master;
import application.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface JobMapper {
    @Mapping(source = "masterId", target = "master", qualifiedByName = "getMasterById")
    @Mapping(source = "orderId", target = "order", qualifiedByName = "getOrderById")
    Job toEntity(JobRequestDto jobRequestDto);

    @Mapping(source = "master", target = "masterId", qualifiedByName = "getMasterIdByMaster")
    @Mapping(source = "order", target = "orderId", qualifiedByName = "getOrderIdByOrder")
    JobResponseDto toDto(Job job);

    @Named(value = "getMasterById")
    default Master getMasterById(Long id) {
        return new Master().setId(id);
    }

    @Named(value = "getOrderById")
    default Order getOrderById(Long id) {
        return new Order().setId(id);
    }

    @Named(value = "getMasterIdByMaster")
    default Long getMasterIdByMaster(Master master) {
        return master.getId();
    }

    @Named(value = "getOrderIdByOrder")
    default Long getOrderIdByOrder(Order order) {
        return order.getId();
    }
}
