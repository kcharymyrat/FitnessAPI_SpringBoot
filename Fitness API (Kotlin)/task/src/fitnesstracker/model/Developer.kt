package fitnesstracker.model

import jakarta.persistence.*

@Entity
data class Developer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(unique = true, nullable = false)
    var email: String? = null,

    @Column(nullable = false)
    var password: String? = null,

    @OneToMany(mappedBy = "developer", fetch = FetchType.EAGER, cascade = [CascadeType.ALL])
    var apps: MutableSet<App> = emptySet<App>().toMutableSet()
) {
    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Developer) return false
        return id == other.id
    }
}
