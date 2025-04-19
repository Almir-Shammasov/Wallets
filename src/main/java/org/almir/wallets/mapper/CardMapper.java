package org.almir.wallets.mapper;

import org.almir.wallets.dto.CardRequestDTO;
import org.almir.wallets.dto.CardResponseDTO;
import org.almir.wallets.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CardMapper {
    CardMapper INSTANCE = Mappers.getMapper(CardMapper.class);

    @Mapping(source = "user.name", target = "ownerName")
    CardResponseDTO toResponseDto(Card card);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "maskedNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "balance", source = "initialBalance")
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "transactions", ignore = true)
    @Mapping(target = "limits", ignore = true)
    Card toEntity(CardRequestDTO requestDto);
}
