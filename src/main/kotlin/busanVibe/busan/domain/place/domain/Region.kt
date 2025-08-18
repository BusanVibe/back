package busanVibe.busan.domain.place.domain

import busanVibe.busan.domain.common.BaseEntity
import busanVibe.busan.domain.place.enums.RegionType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne

//@Entity
class Region(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 10)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val type: RegionType,

    @Column(nullable = false)
    val depth: Int,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    val parentRegion: Region? = null

) : BaseEntity()
