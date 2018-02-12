package com.company.office.core.eventlistener;

import com.company.office.OfficeConfig;
import com.haulmont.cuba.security.app.Authentication;
import com.haulmont.cuba.security.auth.events.UserLoggedInEvent;
import com.haulmont.cuba.security.global.UserSession;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class AfterUserLoginEventListener implements ApplicationListener<UserLoggedInEvent> {

    @Inject
    private Authentication auth;

    @Inject
    private OfficeConfig officeConfig;

    @Override
    public void onApplicationEvent(UserLoggedInEvent event) {
        if (event.getUserSession() != null && !event.getUserSession().isSystem()) {
            auth.withSystemUser(() -> {
                UserSession userSession = event.getUserSession();

                if (officeConfig.getManagersGroup() != null)
                    userSession.setAttribute("managersGroupId", officeConfig.getManagersGroup().getId());

                if (officeConfig.getRegistratorsGroup() != null)
                    userSession.setAttribute("registratorsGroupId", officeConfig.getRegistratorsGroup().getId());

                if (officeConfig.getApplicantsGroup() != null)
                    userSession.setAttribute("applicantsGroupId", officeConfig.getApplicantsGroup().getId());

                if (officeConfig.getWorkersGroup() != null)
                    userSession.setAttribute("workersGroupId", officeConfig.getWorkersGroup().getId());

                return null;
            });
        }

    }
}
