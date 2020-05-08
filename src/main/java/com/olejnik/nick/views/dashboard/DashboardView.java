package com.olejnik.nick.views.dashboard;

import com.olejnik.nick.backend.data.Day;
import com.olejnik.nick.backend.service.CovidService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import com.vaadin.flow.component.charts.model.Crosshair;
import com.vaadin.flow.component.charts.model.ListSeries;
import com.vaadin.flow.component.charts.model.XAxis;
import com.vaadin.flow.component.charts.model.YAxis;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
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
public class DashboardView extends Div implements AfterNavigationObserver {

    private final CovidService covidService;

    private final Chart casesConfirmationChart = new Chart();
    private final List<Day> ukraineData;

    public DashboardView(CovidService covidService) {
        this.covidService = covidService;
        ukraineData = covidService.getLatestUkraineData();
        setId("dashboard-view");

        Day lastDay = ukraineData.get(ukraineData.size() - 1);
        Day dayAfterLastDay = ukraineData.get(ukraineData.size() - 2);

        Board board = new Board();
        board.setSizeFull();

        String lastDayTemplate = "%s за минувшие сутки";

        H2 activeH2 = new H2();
        H2 recoveredH2 = new H2();
        H2 deathH2 = new H2();
        H2 casesH2 = new H2();
        board.addRow(
                createBadge("Подтверждённые", casesH2, "primary-text",
                        String.format(lastDayTemplate, lastDay.getConfirmed() - dayAfterLastDay.getConfirmed()), "badge"),
                createBadge("Активные", activeH2, "contrast-text",
                        String.format(lastDayTemplate, lastDay.getActive() - dayAfterLastDay.getActive()), "badge contrast"),
                createBadge("Выздоровело", recoveredH2, "success-text",
                        String.format(lastDayTemplate, lastDay.getRecovered() - dayAfterLastDay.getRecovered()), "badge success"),
                createBadge("Умерло", deathH2, "error-text",
                        String.format(lastDayTemplate, lastDay.getDeaths() - dayAfterLastDay.getDeaths()), "badge error")
        );

        casesH2.setText(String.valueOf(lastDay.getConfirmed()));
        activeH2.setText(String.valueOf(lastDay.getActive()));
        recoveredH2.setText(String.valueOf(lastDay.getRecovered()));
        deathH2.setText(String.valueOf(lastDay.getDeaths()));

        casesConfirmationChart.getConfiguration()
                .setTitle("Статистика с момента подтверждения первой сотни заболевших");
        casesConfirmationChart.getConfiguration().getChart().setType(ChartType.COLUMN);
        WrapperCard monthlyVisitorsWrapper = new WrapperCard("wrapper",
                new Component[] {casesConfirmationChart}, "card");
        board.add(monthlyVisitorsWrapper);


        add(board);
    }

    private WrapperCard createBadge(String title, H2 h2, String h2ClassName,
            String description, String badgeTheme) {
        Span titleSpan = new Span(title);
        titleSpan.getElement().setAttribute("theme", badgeTheme);

        h2.addClassName(h2ClassName);

        Span descriptionSpan = new Span(description);
        descriptionSpan.addClassName("secondary-text");

        return new WrapperCard("wrapper",
                new Component[] { titleSpan, h2, descriptionSpan }, "card",
                "space-m");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {

        // First chart
        Configuration configuration = casesConfirmationChart.getConfiguration();

        ListSeries confirmed = new ListSeries("Подтверждённые");
        ukraineData.forEach(day -> confirmed.addData(day.getConfirmed()));
        configuration.addSeries(confirmed);

        ListSeries death = new ListSeries("Умерло");
        ukraineData.forEach(day -> death.addData(day.getDeaths()));
        configuration.addSeries(death);

        ListSeries recovered = new ListSeries("Выздоровело");
        ukraineData.forEach(day -> recovered.addData(day.getRecovered()));
        configuration.addSeries(recovered);

        ListSeries active = new ListSeries("Активные");
        ukraineData.forEach(day -> active.addData(day.getActive()));
        configuration.addSeries(active);

        XAxis x = new XAxis();
        x.setCrosshair(new Crosshair());
        ukraineData.forEach(day -> x.addCategory(day.getDate().toString()));
        configuration.addxAxis(x);

        YAxis y = new YAxis();
        y.setMin(0);
        configuration.addyAxis(y);

    }
}
