<html>
<head>

    <title><g:layoutTitle default="Judo Chop"/></title>

    <r:require modules='styles'/>
    <r:require modules='scripts'/>

    <g:layoutHead/>
    <r:layoutResources/>

</head>
<body>

    <div class="container">

        <div class="header">
            <h1><img src="${createLinkTo(dir: 'img', file: 'judo-chop.jpeg')}" /> Judo Chop</h1>
        </div>

        <g:layoutBody/>

        <div id="chart0" style="height:300px; width:500px;"></div>

        <div class="footer">
            <p>&copy; Judo Chop 2014</p>
        </div>
    </div>

    <r:layoutResources/>


<script class="code" type="text/javascript">

    $(document).ready(function(){
        var line1=[['23-May-08', 578.55], ['20-Jun-08', 566.5], ['25-Jul-08', 480.88], ['22-Aug-08', 509.84],
            ['26-Sep-08', 454.13], ['24-Oct-08', 379.75], ['21-Nov-08', 303], ['26-Dec-08', 308.56],
            ['23-Jan-09', 299.14], ['20-Feb-09', 346.51], ['20-Mar-09', 325.99], ['24-Apr-09', 386.15]];
        var plot1 = $.jqplot('chart0', [line1], {
            title:'Data Point Highlighting',
            axes:{
                xaxis:{
                    renderer:$.jqplot.DateAxisRenderer,
                    tickOptions:{
                        formatString:'%b&nbsp;%#d'
                    }
                },
                yaxis:{
                    tickOptions:{
                        formatString:'$%.2f'
                    }
                }
            },
            highlighter: {
                show: true,
                sizeAdjust: 7.5
            },
            cursor: {
                show: false
            }
        });
    });

</script>

</body>
</html>