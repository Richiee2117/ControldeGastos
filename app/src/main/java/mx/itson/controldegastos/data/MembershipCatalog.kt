package mx.itson.controldegastos.data

import mx.itson.controldegastos.model.Membership

object MembershipCatalog {
    
    data class MembershipPlan(
        val nombre: String,
        val tipo: String,
        val frecuencia: String,
        val monto: Double
    )
    
    val streamingServices = mapOf(
        "Netflix" to listOf(
            MembershipPlan("Netflix Básico con anuncios", "Netflix", "Mensual", 129.0),
            MembershipPlan("Netflix Estándar", "Netflix", "Mensual", 219.0),
            MembershipPlan("Netflix Premium", "Netflix", "Mensual", 299.0)
            // Netflix solo ofrece planes mensuales oficialmente
        ),
        "Disney+" to listOf(
            MembershipPlan("Disney+", "Disney+", "Mensual", 179.0),
            MembershipPlan("Disney+", "Disney+", "Anual", 1599.0)  // $133.25/mes
        ),
        "HBO Max" to listOf(
            MembershipPlan("HBO Max con anuncios", "HBO Max", "Mensual", 149.0),
            MembershipPlan("HBO Max Standard", "HBO Max", "Mensual", 249.0),
            MembershipPlan("HBO Max Ultimate", "HBO Max", "Mensual", 349.0)
        ),
        "Amazon Prime Video" to listOf(
            MembershipPlan("Prime Video", "Amazon Prime Video", "Mensual", 99.0),
            MembershipPlan("Prime Video", "Amazon Prime Video", "Anual", 899.0)  // $74.92/mes
        ),
        "Apple TV+" to listOf(
            MembershipPlan("Apple TV+", "Apple TV+", "Mensual", 129.0)
        ),
        "Spotify" to listOf(
            MembershipPlan("Spotify Individual", "Spotify", "Mensual", 115.0),
            MembershipPlan("Spotify Duo", "Spotify", "Mensual", 149.0),
            MembershipPlan("Spotify Familiar", "Spotify", "Mensual", 179.0),
            MembershipPlan("Spotify Estudiantil", "Spotify", "Mensual", 57.0)
        ),
        "YouTube Premium" to listOf(
            MembershipPlan("YouTube Premium Individual", "YouTube Premium", "Mensual", 119.0),
            MembershipPlan("YouTube Premium Familiar", "YouTube Premium", "Mensual", 179.0)
        ),
        "Apple Music" to listOf(
            MembershipPlan("Apple Music Individual", "Apple Music", "Mensual", 115.0),
            MembershipPlan("Apple Music Familiar", "Apple Music", "Mensual", 179.0),
            MembershipPlan("Apple Music Estudiantil", "Apple Music", "Mensual", 57.0)
        ),
        "Microsoft 365" to listOf(
            MembershipPlan("Microsoft 365 Personal", "Microsoft 365", "Mensual", 199.0),
            MembershipPlan("Microsoft 365 Personal", "Microsoft 365", "Anual", 1999.0),  // $166.58/mes
            MembershipPlan("Microsoft 365 Familiar", "Microsoft 365", "Mensual", 299.0),
            MembershipPlan("Microsoft 365 Familiar", "Microsoft 365", "Anual", 2999.0)   // $249.92/mes
        ),
        "Adobe Creative Cloud" to listOf(
            MembershipPlan("Adobe Todas las Apps", "Adobe Creative Cloud", "Mensual", 672.0),
            MembershipPlan("Adobe Fotografía", "Adobe Creative Cloud", "Mensual", 159.0),
            MembershipPlan("Adobe Estudiantes", "Adobe Creative Cloud", "Mensual", 239.0)
        ),
        "PlayStation Plus" to listOf(
            MembershipPlan("PlayStation Plus Essential", "PlayStation Plus", "Mensual", 149.0),
            MembershipPlan("PlayStation Plus Extra", "PlayStation Plus", "Mensual", 399.0),
            MembershipPlan("PlayStation Plus Deluxe", "PlayStation Plus", "Mensual", 499.0)
        ),
        "Xbox Game Pass" to listOf(
            MembershipPlan("PC Game Pass", "Xbox Game Pass", "Mensual", 199.0),
            MembershipPlan("Xbox Game Pass Ultimate", "Xbox Game Pass", "Mensual", 399.0)
        ),
        "Nintendo Switch Online" to listOf(
            MembershipPlan("Individual", "Nintendo Switch Online", "Mensual", 65.0),
            MembershipPlan("Individual", "Nintendo Switch Online", "Anual", 650.0),      // $54.17/mes
            MembershipPlan("Familiar", "Nintendo Switch Online", "Anual", 1200.0)        // $100/mes
        ),
        "Google One" to listOf(
            MembershipPlan("Google One 100GB", "Google One", "Mensual", 34.9),
            MembershipPlan("Google One 2TB", "Google One", "Mensual", 114.9),
            MembershipPlan("Google One 2TB", "Google One", "Anual", 1149.0)  // $95.75/mes
        ),
        "iCloud+" to listOf(
            MembershipPlan("iCloud+ 50GB", "iCloud+", "Mensual", 29.0),
            MembershipPlan("iCloud+ 200GB", "iCloud+", "Mensual", 89.0),
            MembershipPlan("iCloud+ 2TB", "iCloud+", "Mensual", 299.0)
        ),
        "Uber One" to listOf(
            MembershipPlan("Uber One", "Uber One", "Mensual", 119.0),
            MembershipPlan("Uber One", "Uber One", "Anual", 999.0)  // $83.25/mes
        ),
        "Rappi Prime" to listOf(
            MembershipPlan("Rappi Prime", "Rappi Prime", "Mensual", 129.0),
            MembershipPlan("Rappi Prime", "Rappi Prime", "Anual", 999.0)  // $83.25/mes
        ),
        "Didi Pass" to listOf(
            MembershipPlan("Didi Pass", "Didi Pass", "Mensual", 49.0),
            MembershipPlan("Didi Pass", "Didi Pass", "Trimestral", 129.0),  // $43/mes
            MembershipPlan("Didi Pass", "Didi Pass", "Anual", 399.0)        // $33.25/mes
        )
    )
    
    fun obtenerServicios(): List<String> {
        return streamingServices.keys.toList()
    }
    
    fun obtenerPlanes(servicio: String): List<MembershipPlan> {
        return streamingServices[servicio] ?: emptyList()
    }
    
    fun obtenerPlanesPorFrecuencia(servicio: String, frecuencia: String): List<MembershipPlan> {
        return obtenerPlanes(servicio).filter { it.frecuencia == frecuencia }
    }
}
