package nz.ac.auckland.lmz.widget.stencil

import nz.ac.auckland.lmz.common.LmzAppVersion
import nz.ac.auckland.lmz.widget.WidgetStencil
import nz.ac.auckland.lmz.widget.service.WidgetService
import nz.ac.auckland.lmzwidget.generator.annotations.Widget;
import nz.ac.auckland.stencil.Path;
import nz.ac.auckland.stencil.Stencil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author Marnix Cook
 *
 * This stencil outputs the version of the widget
 */
@Path("/widgets/{name}/version")
public class VersionStencil implements Stencil {

    private static final Logger LOG = LoggerFactory.getLogger(VersionStencil)

    @Inject WidgetService widgetService;

    @Inject LmzAppVersion appVersion;

    /**
     * Outputs the current version of the stencil
     *
     * @param request is the request object
     * @param response is the response object
     * @param params is the map of parameters
     */
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Map<String, String> params) {

        // make sure the widget with name `{name}` exists. set 404 otherwise.
        WidgetStencil stencil = widgetService.getWidgetByName(params['name'])
        if (!stencil) {
            widgetNotFound(response)
            return
        }

        try {
            response.setHeader("Content-Type", "text/plain")

            PrintWriter writer = new PrintWriter(new OutputStreamWriter(response.outputStream))
            writer.println(appVersion.version)
            writer.flush()
        }
        catch (IOException ioEx) {
            LOG.error("Unable to write to the outputstream", ioEx);
        }
    }

    protected void widgetNotFound(HttpServletResponse response) {
        response.status = HttpServletResponse.SC_NOT_FOUND
    }
}
