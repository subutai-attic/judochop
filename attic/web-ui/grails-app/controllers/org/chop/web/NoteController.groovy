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
