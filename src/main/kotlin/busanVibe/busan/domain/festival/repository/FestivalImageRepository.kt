package busanVibe.busan.domain.festival.repository

import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.domain.FestivalImage
import org.springframework.data.jpa.repository.JpaRepository

interface FestivalImageRepository: JpaRepository<FestivalImage, String> {

    fun findAllByFestivalIn(festivals: List<Festival>): List<FestivalImage>

}