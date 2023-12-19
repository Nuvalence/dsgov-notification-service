package io.nuvalence.platform.notification.service.model;

import io.nuvalence.events.event.dto.ApplicationRole;
import lombok.Getter;

import java.util.List;

@Getter
public class ApplicationRoles {

    private String name;
    private List<ApplicationRole> roles;
}
