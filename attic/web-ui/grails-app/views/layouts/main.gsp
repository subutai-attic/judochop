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