package fi.tuni.koodimankelit.antibiootit.models;

import java.util.List;

import fi.tuni.koodimankelit.antibiootit.database.data.DiagnosisInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents all the diagnoses saved in the database
 */
@Schema(description = "Represents all the diagnoses saved in the database")
public class Diagnoses {

    @Schema(description = "Information of each diagnosis")
    private final List<DiagnosisInfo> diagnosisInfos;
    
    /**
     * Default constructor
     * @param List<DiagnosisInfo> information of each diagnosis
     */
    public Diagnoses(List<DiagnosisInfo> diagnosisInfos) {
        this.diagnosisInfos = diagnosisInfos;
    }

    /**
     * Returns the list of all diagnoses
     * @return List<DiagnosisInfo> information of each diagnosis
     */
    @Schema(description = "Returns the list of all diagnosis infos")
    public List<DiagnosisInfo> getDiagnosisInfos() {
        return this.diagnosisInfos;
    }
}
