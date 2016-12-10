package de.fhws.fiw.fwom.electionConfig.models;

import de.fhws.fiw.fwpm.electionConfig.models.ParticipatedFwpm;

public class ParticipatedFwpmFactory {

	public static ParticipatedFwpm getParticipatedFwpm(long periodId, String fwpmId) {
		ParticipatedFwpm participatedFwpm = new ParticipatedFwpm();
		participatedFwpm.setPeriodId(periodId);
		participatedFwpm.setFwpmId(fwpmId);

		return participatedFwpm;
	}

	public static ParticipatedFwpm getParticipatedFwpm(long periodId) {
		return getParticipatedFwpm(periodId, "9999999999999");
	}

	public static ParticipatedFwpm getParticipatedFwpm() {
		return getParticipatedFwpm(9999999999999l);
	}
}
