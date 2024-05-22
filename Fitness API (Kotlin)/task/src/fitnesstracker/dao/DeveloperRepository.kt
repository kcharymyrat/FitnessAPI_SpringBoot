package fitnesstracker.dao

import fitnesstracker.model.Developer
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface DeveloperRepository: JpaRepository<Developer, Long> {
    fun findByEmail(email: String): Optional<Developer>
}