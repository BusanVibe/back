package busanVibe.busan.domain.festival.repository

import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.domain.FestivalLike
import org.springframework.data.jpa.repository.JpaRepository

interface FestivalLikesRepository: JpaRepository<FestivalLike, String> {

    fun findAllByFestivalIn(festivalList: List<Festival>): List<FestivalLike>
    fun findAllByFestivalIn(festivalList: Set<Festival>): List<FestivalLike>


}