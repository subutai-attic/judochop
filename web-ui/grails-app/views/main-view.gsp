<html>
<head>
    <title>Judo Chop</title>
</head>
<body>

<center>
    <form id="form" method="get">
        <g:select id="className" name="className" from="${classNames}" value="name" optionValue="value" optionKey="value" style="width: 400px;" onchange="reload()"/>

        <select id="metric" name="metric" onchange="reload()">
            <option value="AVG">Avg Time</option>
            <option value="MIN">Min Time</option>
            <option value="MAX">Max Time</option>
            <option value="ACTUAL">Actual Time</option>
        </select>
    </form>
</center>

<div class="row">
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

    var COMMIT_LINK = "<a href='javascript:void(0);' onclick='openCommitDetails(\"commitId\");'>commitId</a>";

    function reload() {
        $('#form').submit()
    }

    function pointClicked(point) {
        showPointInfo(point.info);
    }

    function showPointInfo(info) {

        console.log(info);
        var text = "- chopType: " + info.chopType
            + "<br/>- commitId: " + COMMIT_LINK.replace(/commitId/g, info.commitId)
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

        $('#className').val('${params.className}');
        $('#metric').val('${params.metric}');

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
                categories: ${commitDirs}
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