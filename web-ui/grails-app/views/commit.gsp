<html>
<head>
    <meta name="layout" content="main">
    <title>Commit Runs</title>
</head>
<body>

<div class="row">

    <b>Class</b>: ${session.className}<br/>
    <b>Metric</b>: ${session.metricType}
    <br/>
    <br/>

    <div class="span9">
        <div id="chart"></div>
    </div>
    <div class="span3">
        <span id="info" style="color: #000000;">
            Click a point to see details
        </span>
    </div>
</div>

<script class="code" type="text/javascript">

    function pointClicked(point) {
        showPointInfo(point.info);
    }

    function showPointInfo(info) {

        console.log(info);
        var text = "- chopType: " + info.chopType
                + "<br/>- runNumber: " + info.runNumber
                + "<br/>- totalTestsRun: " + info.totalTestsRun
                + "<br/>- iterations: " + info.iterations
                + "<br/>- failures: " + info.failures
                + "<br/>- ignores: " + info.ignores
                + "<br/>- value: " + info.value;

        $("#info").html(text);
    }

    function openCommitDetails(commitId) {
        document.location = "commit?commitId=" + commitId;
    }

    $(document).ready(function() {

        $('#chart').highcharts({
            chart: {
                type: 'spline'
            },
            title: {
                text: ' '
            },
            subtitle: {
                text: ' '
            },
            xAxis: {
                //categories: [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
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
                            click: function() {
                                pointClicked(this)
                            }
                        }
                    }
                }
            },
            series: [${series}]
        });

    });

</script>

</body>