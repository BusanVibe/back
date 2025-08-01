package busanVibe.busan.domain.place.repository

import busanVibe.busan.domain.place.domain.VisitorDistribution
import org.springframework.data.jpa.repository.JpaRepository

interface VisitorDistributionRepository: JpaRepository<VisitorDistribution, Long> {

}