package com.infobip.spring.data.jdbc.person.settings;

import com.infobip.spring.data.jdbc.QPersonSettings;
import com.infobip.spring.data.jdbc.QuerydslJdbcRepository;

public interface PersonSettingsRepository extends QuerydslJdbcRepository<PersonSettings, QPersonSettings, Long> {

}
