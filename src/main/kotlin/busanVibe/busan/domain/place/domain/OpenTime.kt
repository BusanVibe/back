package busanVibe.busan.domain.place.domain

import busanVibe.busan.domain.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import java.time.LocalTime

@Entity
class OpenTime(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = true)
    val monOpen: LocalTime? = null,

    @Column(nullable = true)
    val tueOpen: LocalTime? = null,

    @Column(nullable = true)
    val wedOpen: LocalTime? = null,

    @Column(nullable = true)
    val thuOpen: LocalTime? = null,

    @Column(nullable = true)
    val friOpen: LocalTime? = null,

    @Column(nullable = true)
    val satOpen: LocalTime? = null,

    @Column(nullable = true)
    val sunOpen: LocalTime? = null,

    @Column(nullable = true)
    val monClose: LocalTime? = null,

    @Column(nullable = true)
    val tueClose: LocalTime? = null,

    @Column(nullable = true)
    val wedClose: LocalTime? = null,

    @Column(nullable = true)
    val thuClose: LocalTime? = null,

    @Column(nullable = true)
    val friClose: LocalTime? = null,

    @Column(nullable = true)
    val satClose: LocalTime? = null,

    @Column(nullable = true)
    val sunClose: LocalTime? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    val place: Place? = null

) : BaseEntity()