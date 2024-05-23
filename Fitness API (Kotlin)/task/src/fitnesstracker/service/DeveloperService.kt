package fitnesstracker.service;

import fitnesstracker.dao.DeveloperRepository
import fitnesstracker.dto.DeveloperApplication
import fitnesstracker.dto.DeveloperResponseDTO
import fitnesstracker.dto.DeveloperSignupDTO
import fitnesstracker.model.Developer
import fitnesstracker.security.UserDetailsImpl;
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service;
import java.net.URI
import java.util.*

@Service
class DeveloperService @Autowired constructor(
        private val developerRepository: DeveloperRepository,
        private val encoder: PasswordEncoder
) {

    fun findById(id: Long): Optional<Developer> {
        return developerRepository.findById(id)
    }


    fun getAllDeveloperResponseDTOs(): List<DeveloperResponseDTO> {
        return developerRepository.findAll()
                .map { DeveloperResponseDTO(it.id, it.email) }
                .toList()
    }


    fun saveDeveloper(developerSignupDTO: DeveloperSignupDTO): Developer? {
        if (!isDeveloperSignUpDTOValid(developerSignupDTO)) {
            return null
        }

        val developer = Developer(
                email = developerSignupDTO.email,
                password = encoder.encode(developerSignupDTO.password)
        )

        // save developer
        return developerRepository.save(developer)
    }

    fun getDeveloperUrl(developer: Developer): URI {
        return URI("/api/developers/${developer.id}")
    }


    private fun isDeveloperSignUpDTOValid(developerSignupDTO: DeveloperSignupDTO): Boolean {
        // check if both field are not null
        if (developerSignupDTO.email == null || developerSignupDTO.password == null) {
            return false
        }

        // check if email is not empty
        if (developerSignupDTO.email?.trim()?.isEmpty() == true) {
            return false
        }

        // check if password is not empty
        if (developerSignupDTO.password?.trim()?.isEmpty() == true) {
            return false
        }

        // check if email is valid using regex
        if (!developerSignupDTO.email?.matches(Regex("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$"))!!) {
            return false
        }

        // check if email is unique
        if (developerRepository.findByEmail(developerSignupDTO.email!!).isPresent) {
            return false
        }

        return true
    }

    fun getDeveloperResponseDTO(id : Long): DeveloperResponseDTO? {
        val optionalDeveloper = developerRepository.findById(id)
        if (optionalDeveloper.isPresent) {
            val developer = optionalDeveloper.get()
            val devApps = developer.apps.sortedByDescending { it.timestamp }.map {
                DeveloperApplication(it.id ?: "", it.name, it.description, it.apiKey)
            }
            return DeveloperResponseDTO(developer.id, developer.email, devApps)
        }
        return null
    }

    @Transactional
    fun updateDeveloper(developer: Developer) {
        developerRepository.save(developer)
    }


}
