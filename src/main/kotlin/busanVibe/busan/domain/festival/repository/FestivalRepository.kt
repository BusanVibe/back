package busanVibe.busan.domain.festival.repository

import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.enums.FestivalStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FestivalRepository: JpaRepository<Festival, String> {

    @Query(
"""
        SELECT f FROM Festival f
        WHERE f.status = :status 
        """)
    fun getFestivalList(@Param("status") status: FestivalStatus): List<Festival>

}