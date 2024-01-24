package application.mapper;

import application.config.MapperConfig;
import application.dto.car.CarRequestDto;
import application.dto.car.CarResponseDto;
import application.model.Car;
import application.model.Owner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface CarMapper {
    @Mapping(source = "ownerId", target = "owner", qualifiedByName = "getOwnerById")
    Car toEntity(CarRequestDto carRequestDto);

    @Mapping(source = "owner.id", target = "ownerId")
    CarResponseDto toDto(Car car);

    @Named(value = "getOwnerById")
    default Owner getOwnerById(Long id) {
        return new Owner().setId(id);
    }
}
