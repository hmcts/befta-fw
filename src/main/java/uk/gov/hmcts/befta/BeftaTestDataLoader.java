package uk.gov.hmcts.befta;

public class BeftaTestDataLoader {
	private BeftaTestDataLoader() {};
    public static void main(String[] args) {
        BeftaMain.getAdapter().loadTestDataIfNecessary();
    }

}
