package org.atmosphere.samples.chat.jersey;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;
import org.glassfish.jersey.server.mvc.freemarker.FreemarkerMvcFeature;
import org.glassfish.jersey.server.spring.scope.RequestContextFilter;
import static org.glassfish.jersey.servlet.ServletProperties.FILTER_STATIC_CONTENT_REGEX;
import static java.lang.String.format;

/**
 * @author Dmitry Baev charlie@yandex-team.ru
 *         Date: 03.07.14
 */
public class Application extends ResourceConfig {

    public Application() {
        register(RequestContextFilter.class);
        register(FreemarkerMvcFeature.class);
        register(JacksonFeature.class);
        registerFinder(packageScanner(".web"));
        property(FILTER_STATIC_CONTENT_REGEX, "/(static/.*|*.html)");
    }

    private PackageNamesScanner packageScanner(String path) {
        return new PackageNamesScanner(new String[]{format("%s%s", getClass().getPackage().getName(), path)}, true);
    }
}
