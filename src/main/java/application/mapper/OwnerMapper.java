package application.mapper;

import application.config.MapperConfig;
import application.dto.owner.OwnerResponseDto;
import application.dto.owner.OwnerResponseDtoWithCars;
import application.model.Car;
import application.model.Owner;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(config = MapperConfig.class)
public interface OwnerMapper {
    OwnerResponseDto toDto(Owner owner);

    @Mapping(target = "carsId", source = "cars", qualifiedByName = "getCarsIdByCars")
    OwnerResponseDtoWithCars toDtoWithCars(Owner owner);

    @Named(value = "getCarsIdByCars")
    default Set<Long> getCarsIdByCars(Set<Car> carSet) {
        return carSet.stream().map(Car::getId).collect(Collectors.toSet());
    }
}
