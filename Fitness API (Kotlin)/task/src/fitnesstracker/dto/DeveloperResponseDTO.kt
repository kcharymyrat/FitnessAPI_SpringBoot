package fitnesstracker.dto

data class DeveloperResponseDTO(
    var id: Long = 0,
    var email: String? = null,
    var applications: List<DeveloperApplication> = emptyList()
)


data class DeveloperApplication(
    var id: String,
    var name: String,
    var description: String,
    val apikey: String,
    val category: String,
)