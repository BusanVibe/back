package busanVibe.busan.domain.festival.domain

import busanVibe.busan.domain.common.BaseEntity
import busanVibe.busan.domain.user.data.User
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.MapsId

@Entity
class FestivalLike(

    @EmbeddedId
    val id: FestivalLikeId,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @MapsId("userId")
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "festival_id")
    @MapsId("festivalId")
    val festival: Festival

): BaseEntity() {
}