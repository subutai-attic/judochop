/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.usergrid.chop.api.store.amazon;


import org.safehaus.guicyfig.Default;
import org.safehaus.guicyfig.FigSingleton;
import org.safehaus.guicyfig.GuicyFig;
import org.safehaus.guicyfig.Key;


/**
 * Amazon configuration settings.
 */
@FigSingleton
public interface AmazonFig extends GuicyFig {

    String AWS_ACCESS_KEY = "aws.access.key";

    @Key( AmazonFig.AWS_ACCESS_KEY )
    String getAwsAccessKey();



    String AWS_SECRET_KEY = "aws.secret.key";

    @Key( AmazonFig.AWS_SECRET_KEY )
    String getAwsSecretKey();


    String AWS_S3_KEY = "aws.s3.key";

    @Key( AmazonFig.AWS_S3_KEY )
    String getAwsKey();


    String AWS_S3_SECRET = "aws.s3.secret";

    @Key( AmazonFig.AWS_S3_SECRET )
    String getAwsSecret();


    String AWS_BUCKET_KEY = "aws.s3.bucket";
    String DEFAULT_BUCKET = "perftest-bucket";

    @Default( AmazonFig.DEFAULT_BUCKET )
    @Key( AmazonFig.AWS_BUCKET_KEY )
    String getAwsBucket();



    String SCAN_PERIOD_KEY = "scan.period.milliseconds";
    String DEFAULT_SCAN_PERIOD = "300000";

    @Default( AmazonFig.DEFAULT_SCAN_PERIOD )
    @Key( AmazonFig.SCAN_PERIOD_KEY )
    long getScanPeriod();
}
