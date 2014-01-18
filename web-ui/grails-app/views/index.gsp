<html>
<head>
    <title>Judo Chop</title>

    <r:require modules='styles'/>
    <r:require modules='scripts'/>
    <r:layoutResources/>

</head>
<body>

<div class="container">
    <div class="header">
        <h1><img src="${createLinkTo(dir: 'img', file: 'judo-chop.jpeg')}"/> Judo Chop</h1>
    </div>

    <center>
        <form id="form" method="get">
            <g:select id="className" name="className" from="${classNames}" value="name" optionValue="value" optionKey="value" style="width: 400px;" onchange="reload()"/>

            <select id="metric" name="metric" onchange="reload()">
                <option value="avgTime">Avg Time</option>
                <option value="minTime">Min Time</option>
                <option value="maxTime">Max Time</option>
                <option value="actualTime">Actual Time</option>
            </select>
        </form>
    </center>

    <div class="row">
        <div class="span9">
            <div id="chart"></div>
        </div>
        <div class="span3">
            <span id="run-info" style="color: blue;">
                Click a point to see details
            </span>
        </div>
    </div>
</div>

<r:layoutResources/>

<script class="code" type="text/javascript">

    function reload() {
        $('#form').submit()
    }

    function showPointInfo(point) {
        $("#run-info").html(point.info);
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
                                showPointInfo(this)
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
</html>