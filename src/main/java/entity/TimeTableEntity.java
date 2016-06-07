package entity;

import com.sun.org.glassfish.gmbal.Description;

import javax.persistence.*;
import java.sql.Time;

/**
 * Created by User on 20.04.2016.
 */
@Entity
@Table(name = "timetable", schema = "dbo", catalog = "scheduledb")
@Description(value = "Время пар")
public class TimeTableEntity implements Comparable<TimeTableEntity> {
    private Integer id;
    private Time timeBegin;
    private Time timeEnd;

    public TimeTableEntity() {
    }

    public TimeTableEntity(Time timeBegin, Time timeEnd) {
        this.timeBegin = timeBegin;
        this.timeEnd = timeEnd;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "timeBegin")
    public Time getTimeBegin() {
        return timeBegin;
    }

    public void setTimeBegin(Time time) {
        this.timeBegin = time;
    }

    @Basic
    @Column(name = "timeEnd")
    public Time getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Time time) {
        this.timeEnd = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TimeTableEntity that = (TimeTableEntity) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (timeBegin != null ? timeBegin.hashCode() : 0);
        result = 31 * result + (timeEnd != null ? timeEnd.hashCode() : 0);
        return result;
    }

    public String timeBeginString() {
        if (timeBegin == null) {
            return "";
        }
        return shortFormat(timeBegin.toString());
    }

    private String shortFormat(String time) {
        return time.substring(0, time.indexOf(':') + 1) + time.substring(time.indexOf(':') + 1, time.lastIndexOf(':'));
    }

    public String timeEndString() {
        if (timeEnd == null) {
            return "";
        }
        return shortFormat(timeEnd.toString());
    }

    public Time durationTime() {
        return timeBegin;
    }

    @Override
    public String toString() {
        return shortFormat(timeBegin.toString()) + " - " + shortFormat(timeEnd.toString());
    }

    @Override
    public int compareTo(TimeTableEntity o) {
        return id.compareTo(o.id);
    }
}
