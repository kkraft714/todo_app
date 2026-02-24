package server.note;

import server.element.EventInfo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// ToDo: Support recurring events?
public class ScheduleItem extends Note<ScheduleItem> {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private LocalDateTime date;
    private EventInfo.CompletionStatus status;

    public enum CompletionStatus { COMPLETED, UNFINISHED, CANCELED, MISSED }

    public ScheduleItem(String name, String description, LocalDateTime date) {
        super(name, description);
        this.date = date;
        this.status = EventInfo.CompletionStatus.UNFINISHED;
    }

    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime newDate) { this.date = newDate; }

    public EventInfo.CompletionStatus getStatus() { return status; }
    public void setStatus(EventInfo.CompletionStatus newStatus) { this.status = newStatus; }

    @Override
    public String toString() {
        return "Scheduled Item:\n" + super.toString() + "\n" + dateFormat.format(date) + "\n" + status;
    }
}
