package server.element;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScheduleItem {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime date;
    private EventInfo.CompletionStatus status;

    public enum CompletionStatus { COMPLETED, UNFINISHED, CANCELED, MISSED }

    public ScheduleItem(LocalDateTime date) {
        this.date = date;
        this.status = EventInfo.CompletionStatus.UNFINISHED;
    }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime newDate) { this.date = newDate; }

    public EventInfo.CompletionStatus getStatus() { return status; }
    public void setStatus(EventInfo.CompletionStatus newStatus) { this.status = newStatus; }

    @Override
    public String toString() {
        return "EventInfo:\n" + super.toString() + "\n" + dateFormat.format(date) + "\n" + status;
    }
}
