package busanVibe.busan.domain.festival.repository

import busanVibe.busan.domain.festival.domain.Festival
import busanVibe.busan.domain.festival.domain.FestivalLike
import busanVibe.busan.domain.user.data.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FestivalLikesRepository: JpaRepository<FestivalLike, String> {

    @Query(
        """
            SELECT fl FROM FestivalLike fl
            LEFT JOIN FETCH fl.user
            WHERE fl.festival IN :festivals
        """
    )
    fun findLikeByFestival(@Param("festivals") festivalList: List<Festival>): List<FestivalLike>

    fun findByFestivalAndUser(@Param("festival") festival: Festival, @Param("user") user: User): FestivalLike?

    fun deleteByFestivalAndUser(@Param("festival") festival: Festival, @Param("user") user: User)

}