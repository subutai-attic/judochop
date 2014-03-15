<html>
<head>
    <title><g:layoutTitle default="Judo Chop"/></title>

    <r:require modules='styles'/>
    <r:require modules='scripts'/>

    <g:layoutHead/>
    <r:layoutResources/>
</head>
<body>
    <r:layoutResources/>

    <div class="container">
        <div class="header">
            <h1><img src="${createLinkTo(dir: 'img', file: 'judo-chop.jpeg')}"/> Judo Chop</h1>
        </div>

        <g:layoutBody/>
    </div>
</body>
</html>