package com.buzz.view;

/**
 * Created by toshikijahja on 10/23/18.
 */
public class TimeView {

    public enum Unit {
        SECONDS("seconds"),
        MINUTES("minutes"),
        HOURS("hours"),
        DAYS("days"),
        MONTHS("months"),
        YEARS("years");

        private final String value;

        Unit(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public final long duration;
    public final Unit unit;

    public TimeView(final long duration, final Unit unit) {
        this.duration = duration;
        this.unit = unit;
    }

    public long getDuration() {
        return duration;
    }

    public String getUnit() {
        return unit.getValue();
    }
}
