package busanVibe.busan.domain.place.domain

import busanVibe.busan.domain.common.BaseEntity
import busanVibe.busan.domain.place.enums.Day
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalTime

@Entity
class OpenTime(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val day: Day,

    @Column(columnDefinition = "TIME")
    val openTime: LocalTime? = null,

    @Column(columnDefinition = "TIME")
    val closeTime: LocalTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    val place: Place? = null

) : BaseEntity()