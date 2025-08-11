package busanVibe.busan.domain.user.data

import busanVibe.busan.domain.common.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import lombok.AccessLevel
import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Getter
import lombok.NoArgsConstructor
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
class User(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     val id: Long? = null,

    @Column(nullable = false, unique = true)
     var email: String?,

    @Column(nullable = false, length = 50)
     var nickname: String,

    @Column(nullable = true, length = 255)
     var profileImageUrl: String?,


) : BaseEntity(), UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return arrayListOf(SimpleGrantedAuthority("ROLE_USER"))
    }

    override fun getPassword(): String? {
        return null;
    }

    override fun getUsername(): String? {
        return id.toString()
    }

}