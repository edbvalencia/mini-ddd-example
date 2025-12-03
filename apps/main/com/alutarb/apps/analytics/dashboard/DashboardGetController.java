package com.alutarb.apps.analytics.dashboard;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alutarb.analytics.interactionscounter.application.increment.InteractionsCounterSearcher;
import com.alutarb.analytics.publicationscounter.application.search.PublicationsCounterSearcher;
import com.alutarb.analytics.sentimentcounter.application.increment.SentimentCounterSearcher;
import com.alutarb.shared.domain.Constants;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class DashboardGetController {

    private final PublicationsCounterSearcher searcher;
    private final InteractionsCounterSearcher interactionsCounterSearcher;
    private final SentimentCounterSearcher sentimentCounterSearcher;

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {
        var publications = searcher.search(Constants.ID);
        var interactions = interactionsCounterSearcher.search(Constants.ID);
        var sentiments = sentimentCounterSearcher.search(Constants.ID);

        var interactionsChart = new InteractionsChartResponse(interactions.likesCount(), interactions.sharesCount(),
            interactions.commentsCount(), interactions.viewsCount());

        var sentimentChart = new SentimentChartResponse(sentiments.positiveCount(), sentiments.negativeCount(),
            sentiments.neutralCount());

        return new DashboardResponse(publications.count(), interactionsChart, sentimentChart);
    }

    public record SentimentChartResponse(
        int positive,
        int negative,
        int neutral
    ) {
    }

    public record DashboardResponse(
        long publications,
        InteractionsChartResponse interactionsChart,
        SentimentChartResponse sentimentChart
    ) {
    }

    public record InteractionsChartResponse(
        int likes,
        int shares,
        int comments,
        int views
    ) {
    }

}
