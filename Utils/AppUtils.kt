import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


Object AppUtils{

 /**
     * Formats a millisecond timestamp into a readable date string.
     * * @param timestamp The timestamp in milliseconds (e.g., System.currentTimeMillis()).
     * @param pattern The desired date pattern (default is "yyyy-MM-dd HH:mm:ss").
     * @param zoneId The time zone to use (default is the system default zone).
     * @return A formatted date string.
     */
    fun formatTimestamp(
        timestamp: Long,
        pattern: String = "yyyy-MM-dd HH:mm:ss",
        zoneId: ZoneId = ZoneId.systemDefault()
    ): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val formatter = DateTimeFormatter.ofPattern(pattern, Locale.getDefault())
        return instant.atZone(zoneId).format(formatter)
    } 
  
}
