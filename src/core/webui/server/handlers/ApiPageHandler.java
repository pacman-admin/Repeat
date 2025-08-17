package core.webui.server.handlers;

import core.languageHandler.Language;
import staticResources.BootStrapResources;

public class ApiPageHandler extends AbstractGETHandler {

    public ApiPageHandler() {
        super("Could not get API documentation for selected language!");
    }

    @Override
    protected String handle() {
        Language selected = backEndHolder.getSelectedLanguage();
        if (selected == Language.MANUAL_BUILD) return "The manual build compiler has no API.";
        //throw new IllegalArgumentException("The manual build compiler has no API.");
        return BootStrapResources.getAPI(selected);
    }
}