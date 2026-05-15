package core.webui.server.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;
import core.userDefinedTask.TaskGroupManager;
import frontEnd.Backend;



import core.languageHandler.Language;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructorManager;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionBuilderBody;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedCompilingLanguage;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedConfig;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedReplayConfig;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedRunTaskConfig;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedTaskGroupButton;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.RenderedUserDefinedAction;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpExchange;
import core.webui.server.handlers.renderedobjects.TooltipsIndexPage;
import utilities.DateUtility;

public final class IndexPageHandler extends AbstractUIHttpHandler {

	private final ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager;

	public IndexPageHandler(ObjectRenderer objectRenderer, ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
		this.manuallyBuildActionConstructorManager = manuallyBuildActionConstructorManager;
	}

	@Override
	public void handleAllowedRequestWithBackend(HttpExchange exchange)
			throws IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("replayConfig", RenderedReplayConfig.fromReplayConfig(Backend.replayConfig));
		data.put("runTaskConfig", RenderedRunTaskConfig.fromRunTaskConfig(Backend.getRunActionConfig()));

		TaskGroup group = TaskGroupManager.getCurrentTaskGroup();
		data.put("taskGroup", RenderedTaskGroupButton.fromTaskGroups(group, TaskGroupManager.getTaskGroups()));
		List<RenderedUserDefinedAction> taskList = group.getTasks().stream().map(RenderedUserDefinedAction::fromUserDefinedAction).collect(Collectors.toList());
		data.put("tasks", taskList);
		data.put("tooltips", new TooltipsIndexPage());

		data.put("executionTime", getExecutionTime());
		data.put("config", new RenderedConfig(Backend.config, Backend.recorder));

		Language selectedLanguage = Backend.getSelectedLanguage();
		List<RenderedCompilingLanguage> languages = new ArrayList<>();
		for (Language language : Language.values()) {
			languages.add(RenderedCompilingLanguage.forLanguage(language, language == selectedLanguage));
		}
		data.put("compilingLanguages", languages);
		boolean displayManualBuild = Backend.getSelectedLanguage() == Language.MANUAL_BUILD;
		data.put("displayManualBuild", displayManualBuild);
		if (displayManualBuild) {
			String id = manuallyBuildActionConstructorManager.addNew();
			Map<String, Object> manuallyBuildBodyData = ManuallyBuildActionBuilderBody.bodyData(manuallyBuildActionConstructorManager, id);
			data.putAll(manuallyBuildBodyData);
		}

		renderedPage(exchange, "index", data);
	}

	private String getExecutionTime() {
		long time = 0;
		for (TaskGroup group : TaskGroupManager.getTaskGroups()) {
			for (UserDefinedAction action : group.getTasks()) {
				time += action.getStatistics().getTotalExecutionTime();
			}
		}

		return DateUtility.durationToString(time);
	}
}
