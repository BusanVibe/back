package busanVibe.busan.domain.place.repository

import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.enums.PlaceType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PlaceRepository: JpaRepository<Place, Long> {

    @Query(
        """
            SELECT p FROM Place p
            WHERE p.type = :type
        """
    )
    fun findByType(@Param("type")type: PlaceType): List<Place>


}