package keitacity.simulation;

public class Clock {
    private int day = 1;
    private int hour = 0;

    public void  tick() {
        hour++;

        if (hour >= 24) {
            hour = 0;
            day++;
        }
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public String getFormattedTime() {
        return String.format("Day %d, %02d:00", day, hour);
    }
    
}
