package org.almir.wallets.mapper;

import org.almir.wallets.dto.LimitRequestDTO;
import org.almir.wallets.dto.LimitResponseDTO;
import org.almir.wallets.entity.Limit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LimitMapper {
    LimitMapper INSTANCE = Mappers.getMapper(LimitMapper.class);

    @Mapping(source = "card.id", target = "cardId")
    LimitResponseDTO toResponseDto(Limit limit);

    Limit toEntity(LimitRequestDTO requestDto);
}
