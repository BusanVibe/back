package busanVibe.busan.domain.user.repository

import busanVibe.busan.domain.user.data.User
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String?): Optional<User>

    fun findUsersByIdIn(ids: List<Long>): List<User>
}