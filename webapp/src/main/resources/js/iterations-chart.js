function pointClicked() {
    iterationsChartCallback(this.properties);
}

$('#iterationsChart').highcharts({
    chart: {
        type: 'spline'
    },
    title: {
        text: "Iterations"
    },
    xAxis: {
        title: {
            text: 'Iterations'
        }
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
        enabled: true
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