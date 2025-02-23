package com.example.myapplication.data

import com.example.myapplication.domain.Quote
import java.util.UUID

// data/mapper/QuoteMapper.kt
class QuoteMapper {
    fun mapDtoToDomain(dto: QuoteDto): Quote {
        return Quote(
            id = UUID.randomUUID().toString(),  // Generate UUID for new quotes from API
            quote = dto.q,      // assuming 'q' is quote text in DTO
            author = dto.a      // assuming 'a' is author in DTO
        )
    }

    fun mapDomainToEntity(domain: Quote): QuoteEntity {
        return QuoteEntity(
            id = domain.id,
            quote = domain.quote,
            author = domain.author?:""
        )
    }

    fun mapEntityToDomain(entity: QuoteEntity): Quote {
        return Quote(
            id = entity.id,
            quote = entity.quote ?: "",  // handle nullable quote
            author = entity.author
        )
    }
}