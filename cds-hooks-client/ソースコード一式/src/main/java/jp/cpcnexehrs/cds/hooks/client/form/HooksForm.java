package jp.cpcnexehrs.cds.hooks.client.form;

public class HooksForm {

    private String selectedUseCase;
    private String selectedScenario;
    private String requestBody;
    private String responseBody;

    public String getSelectedUseCase() {
        return selectedUseCase;
    }

    public void setSelectedUseCase(String selectedUseCase) {
        this.selectedUseCase = selectedUseCase;
    }

    public String getSelectedScenario() {
        return selectedScenario;
    }

    public void setSelectedScenario(String selectedScenario) {
        this.selectedScenario = selectedScenario;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public void setRequestBody(String requestBody) {
        this.requestBody = requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }

}
