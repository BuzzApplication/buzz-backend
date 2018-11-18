package com.buzz.view;

import com.buzz.model.Buzz;
import com.buzz.model.Poll;
import com.buzz.utils.TimeUtils;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by toshikijahja on 10/18/17.
 */
public class BuzzView extends BaseView {

    private final Buzz buzz;
    private final boolean liked;
    private final boolean favorited;
    private final int totalPolls;
    private final List<PollView> pollViews;

    public BuzzView(final Buzz buzz,
                    final boolean liked,
                    final boolean favorited,
                    final List<Integer> polledId) {
        this.buzz = buzz;
        this.liked = liked;
        this.favorited = favorited;
        this.totalPolls = buzz.getPolls().stream().map(Poll::getCount).mapToInt(Integer::intValue).sum();
        this.pollViews = buzz.getPolls().stream()
                .map(poll -> new PollView(poll, totalPolls, polledId.contains(poll.getId()))).collect(toList());
    }

    public int getId() {
        return buzz.getId();
    }

    public String getText() {
        return buzz.getText();
    }

    public int getCompanyId() {
        return buzz.getCompanyId();
    }

    public String getAlias() {
        return buzz.getAlias() == null ? "" : buzz.getAlias();
    }

    public CompanyView getUserCompany() {
        return new CompanyView(buzz.getUserEmail().getCompany());
    }

    public int getUserEmailId() {
        return buzz.getUserEmail().getId();
    }

    public int getLikesCount() {
        return buzz.getLikesCount();
    }

    public int getCommentsCount() {
        return buzz.getCommentsCount();
    }

    public boolean isLiked() {
        return liked;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public TimeView getTimePassed() {
        return TimeUtils.getTimePassed(buzz.getCreated());
    }

    public int getTotalPolls() {
        return totalPolls;
    }

    public List<PollView> getPolls() {
        return pollViews;
    }
}
