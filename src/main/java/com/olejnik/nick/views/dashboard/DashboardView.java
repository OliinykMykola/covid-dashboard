package com.olejnik.nick.views.dashboard;

import com.olejnik.nick.backend.data.Day;
import com.olejnik.nick.backend.data.RegionData;
import com.olejnik.nick.backend.data.RegionDataSummary;
import com.olejnik.nick.backend.service.CovidService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.olejnik.nick.views.main.MainView;

import java.util.List;

@Route(value = "dashboard", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Dashboard")
@CssImport(value = "styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class DashboardView extends Div {

    private final CovidService covidService;

    private final List<Day> ukraineTimeline;

    RegionDataSummary regionSummaryData;
    private final RegionData ukraineSummaryData;

    public DashboardView(CovidService covidService) {
        this.covidService = covidService;
        setId("dashboard-view");

        ukraineTimeline = covidService.getLatestUkraineData();

        regionSummaryData = covidService.getRegionDataSummary();
        ukraineSummaryData = regionSummaryData.getWorld().stream().filter(regionData -> "Ukraine".equals(regionData.getCountry())).findFirst().get();

        Board board = new Board();
        board.setSizeFull();

        board.addRow(getHeaderLabels(ukraineSummaryData));
        board.add(getCasesConfirmationWrapper());

        add(board);
    }

    private WrapperCard getCasesConfirmationWrapper() {
        Chart casesConfirmationChart = new Chart();

        casesConfirmationChart.getConfiguration()
                .setTitle("Статистика с момента подтверждения первой сотни заболевших");
        casesConfirmationChart.getConfiguration().getChart().setType(ChartType.COLUMN);

        Configuration configuration = casesConfirmationChart.getConfiguration();

        ListSeries confirmed = new ListSeries("Подтверждённые");
        ukraineTimeline.forEach(day -> confirmed.addData(day.getConfirmed()));
        configuration.addSeries(confirmed);

        ListSeries death = new ListSeries("Умерло");
        ukraineTimeline.forEach(day -> death.addData(day.getDeaths()));
        configuration.addSeries(death);

        ListSeries recovered = new ListSeries("Выздоровело");
        ukraineTimeline.forEach(day -> recovered.addData(day.getRecovered()));
        configuration.addSeries(recovered);

        ListSeries active = new ListSeries("Активные");
        ukraineTimeline.forEach(day -> active.addData(day.getActive()));
        configuration.addSeries(active);

        XAxis x = new XAxis();
        x.setCrosshair(new Crosshair());
        ukraineTimeline.forEach(day -> x.addCategory(day.getDate().toString()));
        configuration.addxAxis(x);

        YAxis y = new YAxis();
        y.setTitle("Всего");
        y.setMin(0);
        configuration.addyAxis(y);

        Tooltip tooltip = new Tooltip();
        tooltip.setShared(true);
        configuration.setTooltip(tooltip);

        return new WrapperCard("wrapper",
                new Component[]{casesConfirmationChart}, "card");
    }

    private Component[] getHeaderLabels(RegionData ukraineData) {

        String lastDayTemplate = "+%s за минувшие сутки";

        return new Component[]{createBadge("Подтверждённые", new H2(String.valueOf(ukraineData.getConfirmed())), "primary-text",
                String.format(lastDayTemplate, ukraineData.getDelta_confirmed()), "badge"),
                createBadge("Активные", new H2(String.valueOf(ukraineData.getExisting())), "contrast-text",
                        String.format(lastDayTemplate, ukraineData.getDelta_existing()), "badge contrast"),
                createBadge("Выздоровело", new H2(String.valueOf(ukraineData.getRecovered())), "success-text",
                        String.format(lastDayTemplate, ukraineData.getDelta_recovered()), "badge success"),
                createBadge("Умерло", new H2(String.valueOf(ukraineData.getDeaths())), "error-text",
                        String.format(lastDayTemplate, ukraineData.getDelta_deaths()), "badge error")};
    }

    private WrapperCard createBadge(String title, H2 h2, String h2ClassName,
                                    String description, String badgeTheme) {
        Span titleSpan = new Span(title);
        titleSpan.getElement().setAttribute("theme", badgeTheme);

        h2.addClassName(h2ClassName);

        Span descriptionSpan = new Span(description);
        descriptionSpan.addClassName("secondary-text");

        return new WrapperCard("wrapper",
                new Component[]{titleSpan, h2, descriptionSpan}, "card",
                "space-m");
    }

}
