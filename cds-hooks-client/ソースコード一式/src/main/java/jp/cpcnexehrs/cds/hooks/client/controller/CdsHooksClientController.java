package jp.cpcnexehrs.cds.hooks.client.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jp.cpcnexehrs.cds.hooks.client.form.DiscoveryForm;
import jp.cpcnexehrs.cds.hooks.client.form.HooksForm;
import jp.cpcnexehrs.cds.hooks.client.service.CdsHooksClientService;

@Controller
public class CdsHooksClientController {

    private static final Logger logger = LoggerFactory.getLogger(CdsHooksClientController.class);

    @Value("${discovery.request.url}")
    private String discoveryRequestUrl;

    @Value("${hooks.request.url}")
    private String hooksRequestUrl;

    @Value("${case1.scenario.data.dir}")
    private String case1ScenarioDataDir;

    @Value("${case2.scenario.data.dir}")
    private String case2ScenarioDataDir;

    @Autowired
    private CdsHooksClientService cdsHooksClientService;

    @GetMapping("/")
    public String main(Model model) {
        logger.debug("Start");
        model.addAttribute("discovery", new DiscoveryForm());
        model.addAttribute("url", this.discoveryRequestUrl);
        logger.debug("End");
        return "discovery";
    }

    @GetMapping("/discovery")
    public String discoveryForm(Model model) {
        logger.debug("Start");
        model.addAttribute("discovery", new DiscoveryForm());
        model.addAttribute("url", this.discoveryRequestUrl);
        logger.debug("End");
        return "discovery";
    }

    @PostMapping("/discovery")
    public String discoverySubmit(@ModelAttribute DiscoveryForm discovery, Model model) {
        logger.debug("Start");
        String responseBody = cdsHooksClientService.sendDiscoveryRequest(this.discoveryRequestUrl);
        discovery.setResponseBody(responseBody);
        model.addAttribute("discovery", discovery);
        model.addAttribute("url", this.discoveryRequestUrl);
        logger.debug("End");
        return "discovery";
    }

    @GetMapping("/hooks")
    public String hooksForm(Model model) {
        logger.debug("Start");
        HooksForm hooks = new HooksForm();
        hooks.setSelectedUseCase("case1");
        hooks.setSelectedScenario("scenario1");
        String filePath = case1ScenarioDataDir + File.separator + hooks.getSelectedScenario() + ".json";
        String jsonString = readJsonFile(filePath);
        hooks.setRequestBody(jsonString);
        model.addAttribute("hooks", hooks);
        logger.debug("End");
        return "hooks";
    }

    @PostMapping("/hooks")
    public String hooksSubmit(@ModelAttribute HooksForm hooks, Model model) {
        logger.debug("Start");
        String responseBody = cdsHooksClientService.sendHooksRequest(this.hooksRequestUrl, hooks.getRequestBody());
        hooks.setResponseBody(responseBody);
        model.addAttribute("hooks", hooks);
        logger.debug("End");
        return "hooks";
    }

    @PostMapping("/loadJson")
    @ResponseBody
    public String loadJson(@ModelAttribute HooksForm hooks) {
        logger.debug("Start");
        String filePath = "";
        switch (hooks.getSelectedUseCase()) {
            case "case1":
                filePath = case1ScenarioDataDir;
                break;
            case "case2":
                filePath = case2ScenarioDataDir;
                break;
        }
        filePath += File.separator + hooks.getSelectedScenario() + ".json";
        String jsonString = readJsonFile(filePath);
        logger.debug("End");
        return jsonString;
    }

    private String readJsonFile(String filePath) {
        String jsonString = "";
        try {
            jsonString = new String(Files.readAllBytes(Paths.get(filePath)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            logger.error("", e);
        }
        return jsonString;
    }

}
