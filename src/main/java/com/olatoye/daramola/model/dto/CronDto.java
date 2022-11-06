package com.olatoye.daramola.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
public class CronDto {

    @NotNull
    private String seconds;
    @NotNull
    private String minutes;
    @NotNull
    private String hours;
    @NotNull
    private String dayOfMonth;
    @NotNull
    private String month;
    @NotNull
    private String dayOfWeek;

    public CronDto() {
        this.seconds = "0";
        this.minutes = "0/10";
        this.hours = "*";
        this.dayOfMonth = "*";
        this.month = "*";
        this.dayOfWeek = "*";
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
