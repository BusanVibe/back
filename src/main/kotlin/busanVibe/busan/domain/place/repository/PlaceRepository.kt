package busanVibe.busan.domain.place.repository

import busanVibe.busan.domain.place.domain.Place
import busanVibe.busan.domain.place.enums.PlaceType
import busanVibe.busan.domain.user.data.User
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal
import java.util.Optional

interface PlaceRepository: JpaRepository<Place, Long> {

    @Query(
        """
            SELECT p FROM Place p
            WHERE p.type = :type
        """
    )
    fun findByType(@Param("type")type: PlaceType): List<Place>

    @Query(
        """
            SELECT p FROM Place p
            LEFT JOIN FETCH p.placeLikes pl
            LEFT JOIN FETCH pl.user
            LEFT JOIN FETCH p.placeImages
            WHERE p.id = :placeId
        """
    )
    fun findByIdWithLIkeAndImage(@Param("placeId") placeId: Long): Place?

    // ALL 이면 검사 안 함
    @Query(
    """
            SELECT p FROM Place p
            WHERE p.latitude BETWEEN :minLat AND :maxLat
              AND p.longitude BETWEEN :minLng AND :maxLng
              AND (:#{#type.name() == 'ALL'} = true OR p.type = :type)
          """
    )
    fun findPlacesByLocationAndType(
        @Param("minLat") minLat: BigDecimal,
        @Param("maxLat") maxLat: BigDecimal,
        @Param("minLng") minLng: BigDecimal,
        @Param("maxLng") maxLng: BigDecimal,
        @Param("type") type: PlaceType?
    ): List<Place>

    @Query(
        """
            SELECT p FROM Place p
            LEFT JOIN FETCH p.placeImages
            LEFT JOIN FETCH p.placeLikes pl 
            WHERE p.id = :placeId
        """
    )
    fun findByIdWithReviewAndImage(@Param("placeId") placeId: Long): Place?

    @EntityGraph(attributePaths = ["visitorDistribution"])
    @Query("SELECT p FROM Place p WHERE p.id = :placeId")
    fun findWithDistribution(@Param("placeId") placeId: Long): Optional<Place>

    @Query("""
        SELECT p FROM Place p 
        LEFT JOIN FETCH p.placeLikes pl
         LEFT JOIN FETCH p.placeImages
         LEFT JOIN FETCH pl.user
    """)
    fun findAllWithLikesAndOpenTime(): List<Place>

    @Query("""
        SELECT p FROM Place p 
        LEFT JOIN FETCH p.placeLikes pl
         LEFT JOIN FETCH p.placeImages
         LEFT JOIN FETCH pl.user 
        WHERE p.type = :type
    """)
    fun findAllWithLikesAndOpenTimeByType(@Param("type") type: PlaceType): List<Place>

    @Query(
        """
            SELECT p FROM Place p
            LEFT JOIN FETCH p.placeImages
        """
    )
    fun findAllWithImages(): List<Place>

    @Query(
        """
            SELECT DISTINCT p FROM Place p
            LEFT JOIN FETCH p.placeImages
            LEFT JOIN FETCH p.placeLikes pl
            LEFT JOIN FETCH pl.user
        """
    )
    fun findAllWithFetch(): List<Place>

    @Query(
        """
            SELECT DISTINCT p FROM Place p
            LEFT JOIN FETCH p.placeLikes pl
            LEFT JOIN FETCH p.placeImages
            LEFT JOIN FETCH pl.user
            WHERE pl.user = :user
        """
    )
    fun findLikePlace(@Param("user") user: User): List<Place>

    @Query(
        """
            SELECT DISTINCT p FROM Place p
            LEFT JOIN FETCH p.placeLikes pl
            LEFT JOIN FETCH p.placeImages
            LEFT JOIN FETCH pl.user
            WHERE p.type = :type AND pl.user = :user
        """
    )
    fun findLikePlaceByType(@Param("type") type: PlaceType, @Param("user") user: User): List<Place>

    @Query(
        """
            SELECT p FROM Place p
            RIGHT JOIN FETCH p.placeImages pi
            WHERE pi.imgUrl LIKE 'http://tong.visitkorea.or.kr/cms/resource%'
                AND p.type = 'SIGHT'
        """
    )
    fun findPlaceImageNotNull(): List<Place>

}