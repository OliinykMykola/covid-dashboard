package com.olejnik.nick.views.dashboard;

import com.olejnik.nick.backend.data.Day;
import com.olejnik.nick.backend.data.RegionData;
import com.olejnik.nick.backend.data.RegionDataSummary;
import com.olejnik.nick.backend.service.CovidService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.board.Row;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.olejnik.nick.views.main.MainView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Route(value = "dashboard", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Dashboard")
@CssImport(value = "styles/views/dashboard/dashboard-view.css", include = "lumo-badge")
@JsModule("@vaadin/vaadin-lumo-styles/badge.js")
public class DashboardView extends Div {

    private final String TOTAL_CASES_CHART = "Линейная статистика";
    private final String DAILY_CASES_CHART = "Кривая роста";


    private final CovidService covidService;

    private final List<Day> ukraineTimeline;

    RegionDataSummary regionSummaryData;
    private final RegionData ukraineSummaryData;

    public DashboardView(CovidService covidService) {
        this.covidService = covidService;
        setId("dashboard-view");

        ukraineTimeline = covidService.getLatestUkraineData();

        regionSummaryData = covidService.getRegionDataSummary();
        ukraineSummaryData = regionSummaryData.getWorld().stream()
                .filter(regionData -> "Ukraine".equals(regionData.getCountry())).findFirst().get();

        Board board = new Board();
        board.setSizeFull();

        board.addRow(getHeaderLabels(ukraineSummaryData));
        board.add(getMainChartWrapper());
        board.add(getUkraineRegionsGridDataWrapper());

        add(board);
    }

    private Board getUkraineRegionsGridDataWrapper() {
        Board board = new Board();

        Grid<RegionData> ukraineRegionsGrid = new Grid<>();

        Optional<RegionData> defaultRegion = regionSummaryData.getUkraine().stream()
                .filter(regionData -> "Харківська область".equals(regionData.getLabels().getUk())).findFirst();

        ukraineRegionsGrid.setItems(regionSummaryData.getUkraine());
        ukraineRegionsGrid.addColumn(regionData -> regionData.getLabels().getUk()).setHeader("Регион").setSortable(true);
        ukraineRegionsGrid.addColumn(RegionData::getConfirmed).setHeader("Подтверждено").setSortable(true);
        ukraineRegionsGrid.addColumn(RegionData::getExisting).setHeader("Активные").setSortable(true);
        ukraineRegionsGrid.addColumn(RegionData::getRecovered).setHeader("Выздоровевшие").setSortable(true);
        ukraineRegionsGrid.addColumn(RegionData::getDeaths).setHeader("Умершие").setSortable(true);

        Div regionDataHeader = new Div();
        H2 label = new H2();

        VerticalLayout wrapper = new VerticalLayout(regionDataHeader);
        wrapper.add(ukraineRegionsGrid);

        ukraineRegionsGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        Row row = new Row();

        ukraineRegionsGrid.addSelectionListener(event -> {


            Optional<RegionData> dataToShowStats = Optional.ofNullable(event.getFirstSelectedItem()).orElse(defaultRegion);
            dataToShowStats.ifPresent(regionData -> {
                label.setText(String.format("Регион: %s", regionData.getLabels().getUk()));
                row.removeAll();
                row.add(getHeaderLabels(regionData));
            });
        });

        defaultRegion.ifPresent(region -> {
            ukraineRegionsGrid.select(region);
            ukraineRegionsGrid.scrollToIndex(regionSummaryData.getUkraine().indexOf(region));
        });

        board.setSizeFull();
        board.add(label);
        board.addRow(row);
        board.add(ukraineRegionsGrid);

        return board;
    }

    private WrapperCard getMainChartWrapper() {
        Div chartDiv = new Div();
        chartDiv.setSizeFull();
        ComboBox<String> chartTypeCombobox = new ComboBox<>("График: ");
        chartTypeCombobox.setWidthFull();
        chartTypeCombobox.setPreventInvalidInput(true);
        chartTypeCombobox.setItems(TOTAL_CASES_CHART, DAILY_CASES_CHART);
        chartTypeCombobox.setAllowCustomValue(false);
        chartTypeCombobox.addValueChangeListener(event -> {
            chartDiv.removeAll();
            chartDiv.add(TOTAL_CASES_CHART.equals(event.getValue()) ? getTotalCasesChart() : getLineChart());
        });
        chartTypeCombobox.setValue(TOTAL_CASES_CHART);

        return new WrapperCard("wrapper", new Component[]{chartTypeCombobox, chartDiv}, "card");
    }

    private Chart getTotalCasesChart() {
        Chart totalCasesChart = new Chart();
        totalCasesChart.getConfiguration().setTitle("Общее количество заболевших и выздоровевших");
        totalCasesChart.getConfiguration().setSubTitle("(с момента подтверждения первой сотни)");
        totalCasesChart.getConfiguration().getChart().setType(ChartType.COLUMN);

        Configuration configuration = totalCasesChart.getConfiguration();

        ListSeries confirmed = new ListSeries("Подтвержденные");
        ukraineTimeline.forEach(day -> confirmed.addData(day.getConfirmed()));
        configuration.addSeries(confirmed);

        ListSeries death = new ListSeries("Умершие");
        ukraineTimeline.forEach(day -> death.addData(day.getDeaths()));
        configuration.addSeries(death);

        ListSeries recovered = new ListSeries("Выздоровевшие");
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

        return totalCasesChart;
    }

    private Chart getLineChart() {
        Chart casesConfirmationChart = new Chart();
        casesConfirmationChart.getConfiguration().setTitle("Ежедневный прирост");
        casesConfirmationChart.getConfiguration().setSubTitle("(с момента подтверждения первой сотни)");
        casesConfirmationChart.getConfiguration().getChart().setType(ChartType.LINE);

        Configuration configuration = casesConfirmationChart.getConfiguration();

        ListSeries confirmed = new ListSeries("Подтвержденные");
        for (int i = 0; i < ukraineTimeline.size(); i++) {
            Day day = ukraineTimeline.get(i);
            if (i > 0) {
                Day dayBefore = ukraineTimeline.get(i - 1);
                confirmed.addData(day.getConfirmed() - dayBefore.getConfirmed());

            } else {
                confirmed.addData(day.getConfirmed());
            }
        }
        configuration.addSeries(confirmed);

        ListSeries death = new ListSeries("Умершие");
        for (int i = 0; i < ukraineTimeline.size(); i++) {
            Day day = ukraineTimeline.get(i);
            if (i > 0) {
                Day dayBefore = ukraineTimeline.get(i - 1);
                death.addData(day.getDeaths() - dayBefore.getDeaths());

            } else {
                death.addData(day.getDeaths());
            }
        }
        configuration.addSeries(death);

        ListSeries recovered = new ListSeries("Выздоровевшие");
        for (int i = 0; i < ukraineTimeline.size(); i++) {
            Day day = ukraineTimeline.get(i);
            if (i > 0) {
                Day dayBefore = ukraineTimeline.get(i - 1);
                recovered.addData(day.getRecovered() - dayBefore.getRecovered());

            } else {
                recovered.addData(day.getRecovered());
            }
        }
        configuration.addSeries(recovered);

        ListSeries active = new ListSeries("Активные");
        for (int i = 0; i < ukraineTimeline.size(); i++) {
            Day day = ukraineTimeline.get(i);
            if (i > 0) {
                Day dayBefore = ukraineTimeline.get(i - 1);
                active.addData(day.getActive() - dayBefore.getActive());

            } else {
                active.addData(day.getActive());
            }
        }
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

        return casesConfirmationChart;
    }

    private Component[] getHeaderLabels(RegionData ukraineData) {

        String lastDayTemplate = "%s за минувшие сутки";


        return new Component[]{createBadge("Подтверждённые", new H2(String.valueOf(ukraineData.getConfirmed())), "primary-text",
                String.format(lastDayTemplate, formatInteger(ukraineData.getDelta_confirmed())), "badge"),
                createBadge("Активные", new H2(String.valueOf(ukraineData.getExisting())), "contrast-text",
                        String.format(lastDayTemplate, formatInteger(ukraineData.getDelta_existing())), "badge contrast"),
                createBadge("Выздоровело", new H2(String.valueOf(ukraineData.getRecovered())), "success-text",
                        String.format(lastDayTemplate, formatInteger(ukraineData.getDelta_recovered())), "badge success"),
                createBadge("Умерло", new H2(String.valueOf(ukraineData.getDeaths())), "error-text",
                        String.format(lastDayTemplate, formatInteger(ukraineData.getDelta_deaths())), "badge error")};
    }

    private String formatInteger(Integer dataInteger) {
        DecimalFormat decimalFormat = new DecimalFormat("+#,##0;-#");
        return decimalFormat.format(dataInteger);
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
