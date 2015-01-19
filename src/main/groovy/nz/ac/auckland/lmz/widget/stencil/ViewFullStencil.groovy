package nz.ac.auckland.lmz.widget.stencil

import nz.ac.auckland.stencil.Path
import nz.ac.auckland.stencil.Stencil

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * @author Marnix Cook
 *
 * The viewFull stencil renders a widget in the body of a template specified by a URL
 */
@Path("/widgets/{name}/viewFull")
class ViewFullStencil implements Stencil {

    @Override
    void render(HttpServletRequest request, HttpServletResponse response, Map<String, String> pathParameters) {

    }

}
