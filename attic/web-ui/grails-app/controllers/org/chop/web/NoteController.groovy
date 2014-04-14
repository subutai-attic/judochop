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
package org.chop.web

import org.apache.commons.lang.StringUtils
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics
import org.chop.service.data.NoteService
import org.chop.service.data.PointType
import org.chop.service.store.ResultStore
import org.chop.web.util.FormatRunner

class NoteController {

    def save() {
        NoteService.save(params.commitId, params.runNumber, params.note)
        render 'ok'
    }

    def get() {
        render NoteService.get(params.commitId, params.runNumber)
    }
}
