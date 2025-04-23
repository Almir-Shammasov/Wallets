package org.almir.wallets.mapper;

import org.almir.wallets.dto.TransactionResponseDTO;
import org.almir.wallets.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    TransactionMapper INSTANCE = Mappers.getMapper(TransactionMapper.class);

    @Mapping(source = "sourceCard.id", target = "sourceCardId")
    @Mapping(source = "targetCard.id", target = "targetCardId")
    @Mapping(source = "type", target = "transactionType")
    TransactionResponseDTO toResponseDto(Transaction transaction);
}
