package fitnesstracker.service

import fitnesstracker.dto.AppRegisterRequestDTO
import fitnesstracker.dto.AppRegisterResponseDTO
import fitnesstracker.model.App
import fitnesstracker.model.Developer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class AppService @Autowired constructor(
    private val developerService: DeveloperService
) {

    @Transactional
    fun registerApplications(
        appRegisterRequestDTO: AppRegisterRequestDTO, developer: Developer
    ): Optional<AppRegisterResponseDTO> {
        try {
            val isAppNameNonUnique = developer.apps.map { it.name }.any { it == appRegisterRequestDTO.name }
            if (isAppNameNonUnique) {
                return Optional.empty()
            }

            val app = App(
                name = appRegisterRequestDTO.name.trim(),
                description = appRegisterRequestDTO.description.trim(),
                apiKey =  UUID.randomUUID().toString(),
                developer = developer
            )

            developer.apps.add(app)
            developerService.updateDeveloper(developer)

            return Optional.of(AppRegisterResponseDTO(name = app.name, apikey = app.apiKey))
        } catch (e: Exception) {
            return Optional.empty()
        }
    }
}