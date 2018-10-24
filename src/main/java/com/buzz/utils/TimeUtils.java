package com.buzz.utils;

import com.buzz.view.TimeView;

import java.time.Duration;
import java.time.Instant;

import static com.buzz.view.TimeView.Unit.DAYS;
import static com.buzz.view.TimeView.Unit.HOURS;
import static com.buzz.view.TimeView.Unit.MINUTES;
import static com.buzz.view.TimeView.Unit.MONTHS;
import static com.buzz.view.TimeView.Unit.SECONDS;
import static com.buzz.view.TimeView.Unit.YEARS;

/**
 * Created by toshikijahja on 10/23/18.
 */
public class TimeUtils {

    public static TimeView getTimePassed(final Instant created) {
        final Duration duration = Duration.between(created, Instant.now());

        System.out.println("now: " + Instant.now().toEpochMilli());
        System.out.println("created: " + created.toEpochMilli());

        if (duration.getSeconds() < 60) {
            return new TimeView(duration.getSeconds(), SECONDS);
        } else if (duration.toMinutes() < 60) {
            return new TimeView(duration.toMinutes(), MINUTES);
        } else if (duration.toHours() < 24) {
            return new TimeView(duration.toHours(), HOURS);
        } else if (duration.toDays() < 31) {
            return new TimeView(duration.toDays(), DAYS);
        } else if (duration.toDays() < 365) {
            return new TimeView(duration.toDays() / 30, MONTHS);
        } else {
            return new TimeView(duration.toDays() / 365, YEARS);
        }
    }
}
