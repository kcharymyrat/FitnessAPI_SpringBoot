package fitnesstracker.service

import fitnesstracker.dao.AppRepository
import fitnesstracker.dao.FitnessTrackerRepository
import fitnesstracker.dto.FitnessTrackerRequestDTO
import fitnesstracker.dto.FitnessTrackerResponseDTO
import fitnesstracker.model.App
import fitnesstracker.model.FitnessTracker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class FitnessTrackerService @Autowired constructor(
    private val fitnessTrackerRepository: FitnessTrackerRepository,
    private val appRepository: AppRepository,
) {

    fun getFitnessTrackerById(id: Long): FitnessTracker? {
        return fitnessTrackerRepository.findById(id).orElse(null)
    }

    fun getAllFitnessTrackers(): List<FitnessTracker> {
        return fitnessTrackerRepository.findAllByOrderByIdDesc()
    }

    fun saveFitnessTracker(fitnessTracker: FitnessTracker): FitnessTracker {
        return fitnessTrackerRepository.save(fitnessTracker)
    }

    fun requestDTOToFitnessTracker(requestDTO: FitnessTrackerRequestDTO, appName: String): FitnessTracker {
        return FitnessTracker(
            username = requestDTO.username,
            activity = requestDTO.activity,
            duration = requestDTO.duration,
            calories = requestDTO.calories,
            application = appName,
        )
    }

    fun fitnessTrackerToResponseDTO(fitnessTracker: FitnessTracker): FitnessTrackerResponseDTO {
        return FitnessTrackerResponseDTO(
            id = fitnessTracker.id,
            username = fitnessTracker.username,
            activity = fitnessTracker.activity,
            duration = fitnessTracker.duration,
            calories = fitnessTracker.calories,
            application = fitnessTracker.application
        )
    }

    fun getAllFitnessTrackerResponseDTOs(): List<FitnessTrackerResponseDTO> {
        return getAllFitnessTrackers().map { fitnessTrackerToResponseDTO(it) }
    }

    fun validateApiKey(apiKey: String?): Boolean {
        if (apiKey == null || apiKey == "") return false
        return appRepository.findByApiKey(apiKey).isPresent
    }

    fun getAppByApiKey(apiKey: String?): App? {
        if (apiKey == null || apiKey == "") return null
        return appRepository.findByApiKey(apiKey).get()
    }
}