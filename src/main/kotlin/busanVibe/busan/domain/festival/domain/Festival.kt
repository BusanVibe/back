package busanVibe.busan.domain.festival.domain

import busanVibe.busan.domain.common.BaseEntity
import busanVibe.busan.domain.festival.enums.FestivalStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import java.util.Date

@Entity
class Festival (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 50)
    val name: String,

    @Column(nullable = false)
    val startDate: Date,

    @Column(nullable = false)
    val endDate: Date,

    @Column(nullable = false, length = 50)
    val place: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    val introduction: String,

    @Column(nullable = false)
    val fee: Int,

    @Column(nullable = false)
    val phone: String,

    @Column(nullable = false)
    val siteUrl: String,

    // 연관관계
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: FestivalStatus,

    @OneToMany(mappedBy = "festival", fetch = FetchType.LAZY)
    val festivalImages: Set<FestivalImage>,

    @OneToMany(mappedBy = "festival", fetch = FetchType.LAZY)
    val festivalLikes: Set<FestivalLike>


): BaseEntity(){



}