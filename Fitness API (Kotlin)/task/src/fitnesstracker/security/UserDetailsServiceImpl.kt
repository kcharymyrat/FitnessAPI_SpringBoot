package fitnesstracker.security

import fitnesstracker.dao.DeveloperRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl(private val repository: DeveloperRepository): UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        return repository.findByEmail(username)
            .map { UserDetailsImpl(it) }
            .orElseThrow { UsernameNotFoundException("User with email $username not found") }
    }
}