package fitnesstracker.model

import jakarta.persistence.*
import org.jetbrains.annotations.NotNull
import java.time.Instant
import java.util.UUID

@Entity
data class App(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: String? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @Column(name = "api_key", nullable = false, unique = true)
    val apiKey: String,

    @Column(nullable = false)
    val category: String,

    @ManyToOne
    @JoinColumn(name = "developer_id")
    var developer: Developer,

    var timestamp: Instant = Instant.now()
) {
    init {
        require(name.isNotBlank()) { "App name cannot be blank" }
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is App) return false
        return id == other.id
    }
}
