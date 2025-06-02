package functional.tests

import grails.gorm.services.Service

@Service(LogEntry)
interface LogEntryDataService {
    
    LogEntry save(LogEntry logEntry)
}
