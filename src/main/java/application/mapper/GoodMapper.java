package application.mapper;

import application.config.MapperConfig;
import application.dto.good.GoodRequestDto;
import application.dto.good.GoodResponseDto;
import application.model.Good;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface GoodMapper {
    Good toEntity(GoodRequestDto goodRequestDto);

    GoodResponseDto toDto(Good good);
}
