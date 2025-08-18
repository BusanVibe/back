package busanVibe.busan.domain.place.repository

import busanVibe.busan.domain.place.domain.Place
import org.springframework.jdbc.core.BatchPreparedStatementSetter
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository

@Repository
class PlaceJdbcRepository(
    private val jdbcTemplate: JdbcTemplate
) {

    fun saveAll(places: List<Place>) {
        val sql = """
            INSERT INTO place (
                content_id, name, type, latitude, longitude, address, introduction, phone, 
                use_time, rest_date, created_at, modified_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """.trimIndent()

        jdbcTemplate.batchUpdate(sql, object : BatchPreparedStatementSetter {
            override fun getBatchSize(): Int = places.size

            override fun setValues(ps: java.sql.PreparedStatement, i: Int) {
                val place = places[i]
                ps.setLong(1, place.contentId)
                ps.setString(2, place.name)
                ps.setString(3, place.type.name)
                ps.setBigDecimal(4, place.latitude)
                ps.setBigDecimal(5, place.longitude)
                ps.setString(6, place.address)
                ps.setString(7, place.introduction)
                ps.setString(8, place.phone)
                ps.setString(9, place.useTime)
                ps.setString(10, place.restDate)
                ps.setTimestamp(11, java.sql.Timestamp.valueOf(place.createdAt))
                ps.setTimestamp(12, java.sql.Timestamp.valueOf(place.modifiedAt))
            }
        })
    }
}
