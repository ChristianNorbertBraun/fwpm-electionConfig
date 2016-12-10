package de.fhws.fiw.fwpm.electionConfig.models;

public class ParticipatedFwpm {

	private long periodId;
	private String fwpmId;

	public ParticipatedFwpm() {
	}

	public ParticipatedFwpm(long periodId, String fwpmId) {
		this.periodId = periodId;
		this.fwpmId = fwpmId;
	}

	public long getPeriodId() {
		return periodId;
	}

	public void setPeriodId(long periodId) {
		this.periodId = periodId;
	}

	public String getFwpmId() {
		return fwpmId;
	}

	public void setFwpmId(String fwpmId) {
		this.fwpmId = fwpmId;
	}

	public interface Fields {
		String PERIOD_ID = "periodId";
		String FWPM_ID = "fwpmId";
	}
}
