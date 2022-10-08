package com.olatoye.daramola.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CronDto {

    private int seconds;
    private int minutes;
    private int hours;
    private int dayOfMonth;
    private int month;
    private int dayOfWeek;

    public CronDto(int seconds, int minutes, int hours, int dayOfMonth, int month, int dayOfWeek) {
        this.seconds = seconds;
        this.minutes = minutes;
        this.hours = hours;
        this.dayOfMonth = dayOfMonth;
        this.month = month;
        this.dayOfWeek = dayOfWeek;
    }

    @Override
    public String toString() {
        return seconds + " " +
                minutes + " " +
                hours + " " +
                dayOfMonth + " " +
                month + " " +
                dayOfWeek;
    }
}
