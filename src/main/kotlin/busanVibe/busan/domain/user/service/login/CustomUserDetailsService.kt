package busanVibe.busan.domain.user.service.login

import busanVibe.busan.domain.user.repository.UserRepository
import lombok.RequiredArgsConstructor
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {


    override fun loadUserByUsername(email: String?): UserDetails? {
        return userRepository.findByEmail(email!!)
            .orElseThrow { RuntimeException("사용자를 찾을 수 없습니다: email = $email") } as UserDetails
    }

}