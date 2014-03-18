function pointClicked() {
    var point = this;
    console.log(point.info);
    handleRunNumber(point.info.runNumber);
}

$('#runsChart').highcharts({
    chart: {
        type: 'spline'
    },
    title: {
        text: 'Runs in commit ${params.commitId}'
    },
    xAxis: {
        title: {
            text: 'Runs'
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