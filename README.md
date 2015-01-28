# LMZ Widget

This artifact brings together the `configuration-model` and `generator` artifacts. It is included as a dependency in your project. When Jetty is started, it will expose a number of Stencils that dynamically generate their content based on the widgets that have been defined in your project. 

## What is an LMZ Widget

An LMZ widget is a piece of HTML content (called a partial) that is returned to the user based on the configuration it is passed. 

We have developed an LMZ Widget Bridge in AEM that is able to point at your widgets and transforms them into proper, configurable AEM components. Essentially allowing you to integrate pieces of dynamically generated content from bespoke LMZ applications into pages that have been authored in AEM.

These widgets are included on the page using Edge-Side Includes (ESI) that are parsed _and_ cached by Varnish. 

## When to use

AEM isn't great at serving dynamic data. It's great a serving static content, and so we should strive to have it keep doing that. Unfortunately, sometimes you will want to integrate something more dynamic in AEM, if Javascript APIs won't do the trick or would be too load-intensive, then consider using an LMZ widget.

## Usage

Assuming you have the following code:

*PrototypeWidgetConfiguration.groovy (contains the configuration POJO)*

	class PrototypeWidgetConfiguration {

		@ExposeAs(
			label = "Title",
			description = "This is title that is being displayed",
			required = true
		)
		String title;

		@ExposeAs(
			label = "Teaser description",
			description = "The teaser text shown in the widget",
			type = VariableConstants.Type.TEXTAREA
		)
		String teaser;

		@ExposeAs(
			label = "Links to",
			pattern = VariableConstants.Pattern.URL
		)
		String moreLink;

		@ExposeAs(
			label = "Count up to",
			required = true,
			group = "Counter"
		)
		Long countTo;

		@ExposeAs(
			label = "Text to count with",
			required = true,
			group = "Counter"
		)
		String countToText;

	}


*PrototypeWidget.groovy: (contains the widget rendering logic)*

	@Widget(
		name = "prototype-widget",
		description = "My prototype widget",
		configuration = PrototypeWidgetConfiguration
	)
	class PrototypeWidget implements WidgetStencil<PrototypeWidgetConfiguration> {

		@Inject WidgetStencilService stencilService

		/**
		 * Prototype widget implementation which renders a simple wrapped JSP.
		 *
		 * @param request is the request object
		 * @param response is the response object
		 * @param config is the configuration passed to the widget.
		 */
		@Override
		void render(HttpServletRequest request, HttpServletResponse response, PrototypeWidgetConfiguration config) {

			stencilService.renderWrappedJsp(
					request,
					response,
					new PrototypeAssetBundle(),
					"/WEB-INF/pages/PrototypeWidget.jsp", [
						config: config
					]
			)
		}

	}

*PrototypeAssetBundle.groovy: (contains references to which assets (JS/CSS) for this widget to work properly)*
		
	public class PrototypeAssetBundle extends AssetBundle {

		@Override
		public List<String> getStyles() {
			return [
				"//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css"
			]
		}

		@Override
		public List<String> getJavascripts() {
			return [
				"//maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js",
				"/js/prototypejs.js"
			]
		}

		@Override
		public String getBootstrap() {
			return "PW.bootstrap"
		}
	}


The following URLs will be available in your application:

* `http://../widgets/prototype-widget/configuration`: This stencil outputs the JSON version of the configuration model that has been generated based on the `@Widget` annotated `WidgetStencil` beans that have been found. 

* `http://../widgets/prototype-widget/version`: This stencil outputs the current version of the widget that is running. This is the exact version that is expected when the `view`-stencil URI is invoked. 

* `http://../widgets/prototype-widget/view`: This stencil always has at least two parameters. `version` and `configuration`. Version should always be exactly the version number that is returned in the previous endpoint. Configuration is a base64 encoded version of a JSON string that contains configuration values for the PrototypeWidgetConfiguration POJO. 

# View output

The `WidgetStencilService.renderWrappedJsp` method outputs the widget JSP, but wraps it in a `<div>` that has additional `data-` attributes. All of the global `ApplicationResource` instances are inserted there. AEM has a widget bootstrap javascript that is able to read them and exposes them properly as jQuery widgets. 
