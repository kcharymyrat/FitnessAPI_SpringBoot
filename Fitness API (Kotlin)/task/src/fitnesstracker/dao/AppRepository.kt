package fitnesstracker.dao

import fitnesstracker.model.App
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface AppRepository: JpaRepository<App, String> {
    fun findByApiKey(apiKey: String): Optional<App>
}