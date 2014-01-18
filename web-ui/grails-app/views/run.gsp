<html>
<head>
    <meta name="layout" content="main">
    <title>Run #${params.runNumber}</title>
</head>
<body>

<h4>${params.commitId} / Run #${params.runNumber}</h4>
<b>Class</b>: <a href="/web-ui?className=${session.className}&metric=${session.metricType}">${session.className}</a><br/>
<b>Metric</b>: ${session.metricType}<br/>

<br/><br/>

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

<hr/>
<div id="failures"></div>

<script class="code" type="text/javascript">

    function pointClicked(point) {

        var info = point.info

        console.log(info);

        var text = "<br/>- value: " + info.runTime
                + "<br/>- failureCount: " + info.failureCount
                + "<br/>- ignoreCount: " + info.ignoreCount

        showFailures(info.failures);

        $("#info").html(text);
    }

    function showFailures(failures) {

        var s = "";

        for (var i = 0; i < failures.length; i++) {

            s += "message: <b>" + failures[i].message
                    + "</b><br/>testHeader: <b>" + failures[i].testHeader
                    + "</b><br/>trace: " + failures[i].trace
                    + "<hr/>"

            /*message: "Unable to perform migration"
            testHeader: "writeParamsEntity(org.apache.usergrid.persistence.collection.serialization.impl.MvccEntitySerializationStrategyImplTest)"
            trace: "org.a*/
        }

        $("#failures").html(s);
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