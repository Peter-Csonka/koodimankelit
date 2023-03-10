package fi.tuni.koodimankelit.antibiootit.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.ArrayList;

import fi.tuni.koodimankelit.antibiootit.database.data.DiagnoseInfo;
import fi.tuni.koodimankelit.antibiootit.exceptions.InvalidParameterException;
import fi.tuni.koodimankelit.antibiootit.models.DiagnoseResponse;
import fi.tuni.koodimankelit.antibiootit.models.request.InfectionSelection;
import fi.tuni.koodimankelit.antibiootit.models.request.Parameters;



/**
 * Tests /dose-calculation endpoint HTTP requests and responses
 */
public class DoseCalculationTest extends AntibioticsControllerTest {

    private static final Parameters mockParameters = new Parameters("J03.0", 35.5, false, new ArrayList<InfectionSelection>());
    private static final String ADDRESS = "/api/antibiotics/dose-calculation";

    @Test
    public void validParametersShouldReturn200() throws Exception {

        // Mock needed methods
        when(service.calculateTreatments(any()))
        .thenReturn(
            new DiagnoseResponse("diagnosisResponseID", "etiology")
        );

        when(service.getDiagnoseInfoByID(any()))
        .thenReturn(new DiagnoseInfo("diagnosisID", "name", "etiology", new ArrayList<>()));

        // Actual test
        request(mockParameters)
        .andDo(print()).andExpect(status().isOk())
        .andExpect(jsonPath("$._id").value("diagnosisResponseID"))
        .andExpect(jsonPath("$.etiology").value("etiology"))
        .andReturn();
    }

    @Test
    public void emptyIDShouldReturn400() throws Exception {

        Parameters parameters = new Parameters("", 0.0, false, new ArrayList<>());
        
        request(parameters)
        .andExpect(status().isBadRequest());
    }

    @Test
    public void nullCheckBoxesShouldReturn400() throws Exception {

        Parameters parameters = new Parameters("test", 0.0, false, null);
        
        request(parameters)
        .andExpect(status().isBadRequest());
    }

    @Test
    public void missingWeightShouldReturn400() throws Exception {

        String payload = "{\"diagnosisID\":\"J03.0\",\"penicillinAllergic\":false,\"checkBoxes\":[]}";

        request(payload)
        .andExpect(status().isBadRequest());
    }

    @Test
    public void missingAllergicShouldReturn400() throws Exception {

        String payload = "{\"diagnosisID\":\"J03.0\",\"weight\":0.0,\"checkBoxes\":[]}";

        request(payload)
        .andExpect(status().isBadRequest());
    }

    @Test
    public void validatorExceptionShouldReturn400() throws Exception {

        when(service.calculateTreatments(any()))
        .thenReturn(null);

        when(service.getDiagnoseInfoByID(any()))
        .thenReturn(new DiagnoseInfo(null, null, null, null));

        Mockito.doThrow(new InvalidParameterException(null)).when(validator).validate(any(), any());

        request(mockParameters)
        .andDo(print())
        .andExpect(status().isBadRequest())
        .andReturn();
    }

    @Test
    public void runtimeExceptionShouldReturn500() throws Exception {

        when(service.getDiagnoseInfoByID(any())).thenThrow(RuntimeException.class);

        request(mockParameters)
        .andDo(print())
        .andExpect(status().isInternalServerError())
        .andReturn();
    }



    private ResultActions request(Parameters parameters) throws Exception {

        return request(jsonMapper.writeValueAsString(parameters));
    }

    private ResultActions request(String payload) throws Exception {
        return mockMvc.perform(
            post(ADDRESS)
            .headers(getHeaders())
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload)
        );
    }


}
