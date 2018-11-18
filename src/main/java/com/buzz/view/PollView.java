package com.buzz.view;

import com.buzz.model.Poll;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_EVEN;

/**
 * Created by toshikijahja on 10/18/17.
 */
public class PollView {

    private final Poll poll;
    private final int totalPolls;
    private final boolean polled;

    public PollView(final Poll poll,
                    final int totalPolls,
                    final boolean polled) {
        this.poll = poll;
        this.totalPolls = totalPolls;
        this.polled = polled;
    }

    public int getId() {
        return poll.getId();
    }

    public String getText() {
        return poll.getText();
    }

    public int getCount() {
        return poll.getCount();
    }

    public BigDecimal getPercentage() {
        return totalPolls > 0 ?
                BigDecimal.valueOf(poll.getCount()).divide(BigDecimal.valueOf(totalPolls), 2, HALF_EVEN).multiply(valueOf(100)) :
                ZERO;
    }

    public boolean isPolled() {
        return polled;
    }
}
