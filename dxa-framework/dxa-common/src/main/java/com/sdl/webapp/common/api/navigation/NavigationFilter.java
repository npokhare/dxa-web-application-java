package com.sdl.webapp.common.api.navigation;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Navigation filter that holds information about the requested navigation.
 */
@Data
@Accessors(chain = true)
public class NavigationFilter {

    public static final NavigationFilter DEFAULT = new NavigationFilter();

    private boolean withAncestors;

    private int descendantLevels = 1;
}
