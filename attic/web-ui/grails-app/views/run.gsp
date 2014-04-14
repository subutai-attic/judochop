%{--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
--}%

<html>
<head>
    <meta name="layout" content="main">
    <title>Run #${params.runNumber}</title>
</head>
<body>

<h4>${params.commitId} / Run #${params.runNumber}</h4>
<b>Class</b>: <a href="/web-ui?className=${session.className}&metric=${session.metricType}">${session.className}</a><br/>
<b>Metric</b>: ${session.metricType}<br/>

<form id="form" method="get">
    <input type="hidden" name="commitId" value="${params.commitId}"/>
    <input type="hidden" name="runNumber" value="${params.runNumber}"/>

    Percentile:
    <select id="percentile" name="percentile" onchange="reload()">
        <option value="100">100</option>
        <option value="90">90</option>
        <option value="80">80</option>
        <option value="70">70</option>
        <option value="60">60</option>
        <option value="50">50</option>
        <option value="40">40</option>
        <option value="30">30</option>
        <option value="20">20</option>
        <option value="10">10</option>
    </select>

    Interation Points to Plot:
    <select id="pointFilter" name="pointFilter" onchange="reload()">
        <option value="ALL">All</option>
        <option value="FAILED">Only Failure</option>
        <option value="SUCCESS">Only Success</option>
    </select>
</form>

<br/>
<br/>

<div class="row">

    <div class="span9">
        <div id="chart"></div>
    </div>
    <div class="span3">
        <b>Details</b>:
        <div id="info"></div>
        <br/>

        <b>Note</b>:
        <textarea id="note" rows="10" cols="45" readonly>${note}</textarea>
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
        }

        $("#failures").html(s);
    }

    function reload() {
        $('#form').submit()
    }

    $(document).ready(function() {

        $('#percentile').val('${params.percentile}');
        $('#pointFilter').val('${params.pointFilter}');

        $('#chart').highcharts({
            chart: {
                type: 'spline'
            },
            title: {
                text: "Runners' actual iterations in run #${params.runNumber}"
            },
            xAxis: {
                title: {
                    text: 'Actual Iterations'
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