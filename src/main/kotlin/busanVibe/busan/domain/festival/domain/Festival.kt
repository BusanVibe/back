package busanVibe.busan.domain.festival.domain

import busanVibe.busan.domain.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
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
    val introduction: String

    ): BaseEntity(){


}