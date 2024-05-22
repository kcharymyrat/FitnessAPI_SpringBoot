package fitnesstracker.dto

data class FitnessTrackerResponseDTO(
    val id: Long? = null,
    var username: String? = null,
    var activity: String? = null,
    var duration: Int? = null,
    var calories: Int? = null
)
