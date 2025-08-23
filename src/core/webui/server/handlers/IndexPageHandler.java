package core.webui.server.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.protocol.HttpContext;

import core.languageHandler.Language;
import core.userDefinedTask.TaskGroup;
import core.userDefinedTask.UserDefinedAction;
import core.userDefinedTask.manualBuild.ManuallyBuildActionConstructorManager;
import core.webui.server.handlers.internals.tasks.manuallybuild.ManuallyBuildActionBuilderBody;
import core.webui.server.handlers.renderedobjects.ObjectRenderer;
import core.webui.server.handlers.renderedobjects.RenderedCompilingLanguage;
import core.webui.server.handlers.renderedobjects.RenderedConfig;
import core.webui.server.handlers.renderedobjects.RenderedReplayConfig;
import core.webui.server.handlers.renderedobjects.RenderedRunTaskConfig;
import core.webui.server.handlers.renderedobjects.RenderedTaskGroupButton;
import core.webui.server.handlers.renderedobjects.RenderedUserDefinedAction;
import core.webui.server.handlers.renderedobjects.TooltipsIndexPage;
import utilities.DateUtility;

public class IndexPageHandler extends AbstractUIHttpHandler {

	private final ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager;

	public IndexPageHandler(ObjectRenderer objectRenderer, ManuallyBuildActionConstructorManager manuallyBuildActionConstructorManager) {
		super(objectRenderer, AbstractSingleMethodHttpHandler.GET_METHOD);
		this.manuallyBuildActionConstructorManager = manuallyBuildActionConstructorManager;
	}

	@Override
	protected Void handleAllowedRequestWithBackend(HttpRequest request, HttpAsyncExchange exchange, HttpContext context)
			throws IOException {
		Map<String, Object> data = new HashMap<>();
		data.put("replayConfig", RenderedReplayConfig.fromReplayConfig(backEndHolder.getReplayConfig()));
		data.put("runTaskConfig", RenderedRunTaskConfig.fromRunTaskConfig(backEndHolder.getRunActionConfig()));

		TaskGroup group = backEndHolder.getCurrentTaskGroup();
		data.put("taskGroup", RenderedTaskGroupButton.fromTaskGroups(group, backEndHolder.getTaskGroups()));
		List<RenderedUserDefinedAction> taskList = group.getTasks().stream().map(RenderedUserDefinedAction::fromUserDefinedAction).collect(Collectors.toList());
		data.put("tasks", taskList);
		data.put("tooltips", new TooltipsIndexPage());

		data.put("executionTime", getExecutionTime());
		data.put("config", RenderedConfig.fromConfig(backEndHolder.getConfig(), backEndHolder.getRecorder()));

		Language selectedLanguage = backEndHolder.getSelectedLanguage();
		List<RenderedCompilingLanguage> languages = new ArrayList<>();
		for (Language language : Language.values()) {
			languages.add(RenderedCompilingLanguage.forLanguage(language, language == selectedLanguage));
		}
		data.put("compilingLanguages", languages);
		boolean displayManualBuild = backEndHolder.getSelectedLanguage() == Language.MANUAL_BUILD;
		data.put("displayManualBuild", displayManualBuild);
		if (displayManualBuild) {
			String id = manuallyBuildActionConstructorManager.addNew();
			Map<String, Object> manuallyBuildBodyData = ManuallyBuildActionBuilderBody.bodyData(manuallyBuildActionConstructorManager, id);
			data.putAll(manuallyBuildBodyData);
		}

		return renderedPage(exchange, "index", data);
	}

	private String getExecutionTime() {
		long time = 0;
		for (TaskGroup group : backEndHolder.getTaskGroups()) {
			for (UserDefinedAction action : group.getTasks()) {
				time += action.getStatistics().getTotalExecutionTime();
			}
		}

		return DateUtility.durationToString(time);
	}
}
