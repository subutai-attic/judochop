function pointClicked() {
    var info = this.info;
    console.log(info);
    overviewChartCallback(info);
}

$('#overviewChart').highcharts({
    chart: {
        type: 'spline'
    },
    title: {
        text: 'Commits / Runs'
    },
    xAxis: {
        title: {
            text: 'Commits'
        },
        categories: [$categories]
    },
    yAxis: {
        title: {
            text: 'Time'
        },
        labels: {
            formatter: function() {
                return this.value + ' ms'
            }
        }
    },
    legend: {
        enabled: false
    },
    tooltip: {
        enabled: false
    },
    plotOptions: {
        series: {
            cursor: 'pointer',
            marker: {
                enabled: true,
                symbol: 'circle',
                radius: 4,
                lineColor: '#666666',
                lineWidth: 1,
                fillColor: 'white'
            },
            point: {
                events: {
                    click: pointClicked
                }
            }
        }
    },
    series: $series
});

