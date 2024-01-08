package application.mapper;

import application.config.MapperConfig;
import application.dto.owner.OwnerRequestDto;
import application.dto.owner.OwnerResponseDto;
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

    @Mapping(source = "carsId", target = "cars", qualifiedByName = "getCarsByIds")
    Owner toEntity(OwnerRequestDto ownerRequestDto);

    @Named("getCarsByIds")
    default Set<Car> getCarsByIds(Set<Long> carsId) {
        return carsId.stream().map(id -> new Car().setId(id)).collect(Collectors.toSet());
    }
}
