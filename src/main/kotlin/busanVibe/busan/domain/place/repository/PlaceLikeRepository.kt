package busanVibe.busan.domain.place.repository

import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.domain.PlaceLike
import busanVibe.busan.domain.user.data.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PlaceLikeRepository: JpaRepository<PlaceLike, Long> {
    fun findAllByPlaceIn(placeList: List<Place>): List<PlaceLike>
    fun findByPlace(place: Place): List<PlaceLike>

    @Query("SELECT pl FROM PlaceLike pl WHERE pl.place IN :places")
    fun findLikeByPlace(@Param("places") placeList: List<Place>): List<PlaceLike>

    fun findByPlaceAndUser(@Param("place") place: Place, @Param("user") user: User): PlaceLike?

    fun deleteByPlaceAndUser(@Param("place") place: Place, @Param("user") user: User)


}